package net.smok.koval.assembler;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.smok.koval.KovalRegistry;
import net.smok.Values;
import net.smok.koval.MovablePlace;
import net.smok.koval.Assembly;
import net.smok.koval.Part;
import net.smok.koval.forging.AssemblerContext;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Assembler implements Inventory {


    // x - columns - first, y - rows - second.
    private final int columns, rows;

    private final DefaultedList<ItemStack> table;
    private final @Nullable BlockEntity block;


    public Assembler(int columns, int rows, @Nullable BlockEntity block) {
        this.columns = columns;
        this.rows = rows;
        this.block = block;
        table = DefaultedList.ofSize(rows * columns + 1, ItemStack.EMPTY);

    }

    public int getIndex(Vec2Int pos) {
        return pos.x() * columns + pos.y() + 1;
    }

    public void setResult(@NotNull ItemStack result) {
        table.set(0, result);
        markDirty();
    }

    public void setPart(Vec2Int pos, ItemStack part) {
        table.set(getIndex(pos), part);
    }

    public @NotNull ItemStack getResult() {
        return table.get(0);
    }

    public Part getPart(Vec2Int pos) {
        return KovalRegistry.PARTS.get(Registry.ITEM.getId(getPartItem(pos).getItem()));
    }

    public ItemStack getPartItem(Vec2Int pos) {
        return table.get(getIndex(pos));
    }





    public boolean isOccupied(int x, int y) {
        return x >= 0 && y >= 0 && x < columns && y < rows && !getPartItem(new Vec2Int(x, y)).isEmpty();
    }

    public boolean isPartSuitable(@NotNull Vec2Int pos) {
        return isPartSuitable(pos, getPart(pos));
    }

    public boolean isPartSuitable(@NotNull Vec2Int pos, Part part) {
        if (pos.x() < 0 || pos.y() < 0 || pos.x() >= columns || pos.y() >= rows)
            throw new IndexOutOfBoundsException(MessageFormat.format("Position {0} out of bounds: min = {1}, max = {2}", pos, new Vec2Int(0, 0), new Vec2Int(columns, rows)));

        if (part == null) return true;

        boolean canAssemble;
        try {
            canAssemble = part.parameters().parameters().entrySet().stream()
                    .allMatch(entry -> entry.getValue().canAssemble(
                            new MovablePlace(new AssemblerContext(table), this::getPart, part, pos, entry.getKey())));
        } catch (StackOverflowError e) {
            return false;
        }


        return canAssemble;
    }

    public boolean isAllPartSuitable() {
        boolean b = false;
        for (int x = 0; x < columns; x++)
            for (int y = 0; y < rows; y++)
                if (isOccupied(x, y)) {
                    b = true;
                    if (!isPartSuitable(new Vec2Int(x, y))) return false;
                }
        return b;
    }

    public boolean partsIsEmpty() {
        for (int i = 1; i < table.size(); i++) {
            ItemStack itemStack = table.get(i);
            if (!itemStack.isEmpty()) return false;
        }
        return true;
    }

    private Item constructNewItem() {
        for (int x = 0; x < columns; x++)
            for (int y = 0; y < rows; y++) {
                Vec2Int pos = new Vec2Int(x, y);
                Part part = getPart(pos);
                ItemStack partItem = getPartItem(pos);

                if (part == null || partItem == null) continue;
                AssemblerContext context = new AssemblerContext(table);
                Item constructItem = part.testRecipe(new MovablePlace(context, this::getPart, part, pos, Registry.ITEM.getId(partItem.getItem())));

                if (constructItem != null) return constructItem;
            }

        return null;
    }

    public void disassemble() {
        if (getResult().isEmpty() || !getResult().hasNbt()) return;

        Map<Vec2Int, Identifier> map = Assembly.nbtToItemsMap(getResult().getSubNbt(Values.Json.PARTS));
        for (Vec2Int vec2Int : map.keySet())
            if (vec2Int.x() < 0 || vec2Int.y() < 0 || vec2Int.x() >= columns || vec2Int.y() >= rows) return;


        for (Map.Entry<Vec2Int, Identifier> entry : map.entrySet()) {
            Vec2Int pos = entry.getKey();
            ItemStack part = new ItemStack(Registry.ITEM.get(entry.getValue()));
            setPart(pos, part);
        }
    }

    public void assemble() {
        if (!isAllPartSuitable()) return;

        Item result = constructNewItem();
        if (result == null) return;


        HashMap<Vec2Int, Identifier> map = new HashMap<>();
        for (int x = 0; x < columns; x++) for (int y = 0; y < rows; y++)
                if (isOccupied(x, y)) map.put(new Vec2Int(x, y), Registry.ITEM.getId(getPartItem(new Vec2Int(x, y)).getItem()));

        NbtCompound nbtCompound = Assembly.itemsMapToNbt(map);
        if (getResult().isEmpty() || !getResult().isOf(result)) setResult(new ItemStack(result));
        getResult().setSubNbt(Values.Json.PARTS, nbtCompound);
    }

    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, table);
    }

    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, table);
    }

    @Override
    public int size() {
        return table.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return table.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return table.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(table, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(table, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        table.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public void markDirty() {
        if (block != null) block.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return block == null || block.getWorld().getBlockEntity(block.getPos()) == block
                && player.squaredDistanceTo((double) block.getPos().getX() + 0.5, (double) block.getPos().getY() + 0.5, (double) block.getPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        table.clear();
    }

    public DefaultedList<ItemStack> getParts() {
        DefaultedList<ItemStack> parts = DefaultedList.of();
        for (int i = 1; i < table.size(); i++) {
            parts.add(table.get(i));
        }
        return parts;
    }

    public boolean hasResult() {
        return constructNewItem() != null;
    }
}
