package net.smok.koval.assembler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;
import net.smok.koval.KovalRegistry;
import net.smok.koval.items.KovalItem;
import net.smok.koval.items.PartItem;
import net.smok.utility.Vec2Int;

import java.util.function.Consumer;


public class AssemblerScreenHandler extends ScreenHandler {

    private final PlayerInventory playerInventory;
    private final Assembler table;
    private int playerInventoryStart, playerInventoryEnd;
    private final int rows, columns;


    public AssemblerScreenHandler(ScreenHandlerType<AssemblerScreenHandler> handlerType, int syncId,
                                  Assembler table, PlayerInventory playerInventory, int columns, int rows) {
        super(handlerType, syncId);

        this.table = table;
        this.playerInventory = playerInventory;
        this.rows = rows;
        this.columns = columns;
        init();
    }

    public AssemblerScreenHandler(ScreenHandlerType<AssemblerScreenHandler> handlerType, int syncId, PlayerInventory playerInventory, int columns, int rows) {
        this(handlerType, syncId, new Assembler(columns, rows, null), playerInventory, columns, rows);
    }

    public void init() {
        checkSize(table, rows * columns + 1);
        table.onOpen(playerInventory.player);

        int i = drawOwnSlots(rows, columns);

        drawPlayerSlots(i);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void forResultSlot(Consumer<ResultSlot> slotConsumer) {
        slotConsumer.accept((ResultSlot) slots.get(0));
    }

    public void foreachPartSlot(Consumer<PartSlot> slotConsumer) {
        for (int i = 1; i < playerInventoryStart; i++) {
            slotConsumer.accept((PartSlot) slots.get(i));
        }
    }

    protected void drawPlayerSlots(int yShift) {
        playerInventoryStart = slots.size();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + yShift));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + yShift));
        }
        playerInventoryEnd = slots.size();
    }

    protected int drawOwnSlots(int rows, int columns) {
        int rY = 18 + (rows - 1) * 9;
        int rX = 8 + 7 * 18;
        addSlot(new ResultSlot(table, 0, rX, rY));
        int yShift = (rows - 4) * 18;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Vec2Int pos = new Vec2Int(x, rows - y - 1);
                addSlot(new PartSlot(table, table.getIndex(pos), 8 + x * 18, 18 + y * 18, pos));
            }
        }
        return yShift;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot pickedSlot = slots.get(index);
        if (!pickedSlot.hasStack()) return ItemStack.EMPTY;

        ItemStack pickedStack = pickedSlot.getStack();
        ItemStack original = pickedStack.copy();

        // Move to player inventory
        if (index < playerInventoryStart) {
            if (!insertItem(pickedStack, playerInventoryStart, playerInventoryEnd, true)) {
                return ItemStack.EMPTY;
            }

            if (index == 0) pickedSlot.onQuickTransfer(pickedStack, original);

        } else {


            // Try to move from player inventory to table
            boolean b = false;
            if (pickedStack.getItem() instanceof KovalItem) {

                if (insertItem(pickedStack, 0, 1, false)) b = true;
                else return ItemStack.EMPTY;

            } else if (pickedStack.getItem() instanceof PartItem) {


                if (insertItem(pickedStack, 1, playerInventoryStart, false)) b = true;
                else return ItemStack.EMPTY;
            }

            // Otherwise move in player inventory
            if (!b) {
                if (index < playerInventoryEnd - 9) {

                    if (!this.insertItem(pickedStack, playerInventoryEnd - 9, playerInventoryEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= playerInventoryEnd - 9 && index < playerInventoryEnd &&
                        !this.insertItem(pickedStack, playerInventoryStart, playerInventoryEnd, false)) {

                    return ItemStack.EMPTY;
                }
            }
        }


        // Empty slot or save it.
        if (pickedStack.isEmpty()) pickedSlot.setStack(ItemStack.EMPTY);
        else pickedSlot.markDirty();

        // Stack is not transferred
        if (pickedStack.getCount() == original.getCount()) return ItemStack.EMPTY;

        // Handle transfer
        pickedSlot.onTakeItem(player, pickedStack);

        return original;
    }


    @Override
    protected boolean insertItem(ItemStack from, int startIndex, int endIndex, boolean fromLast) {
        boolean bl = false;
        int i = startIndex;
        if (fromLast) i = endIndex - 1;


        if (!from.isEmpty()) {
            while (!from.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack to = slot.getStack();
                if (to.isEmpty() || ItemStack.canCombine(from, to)) {
                    slot.insertStack(from);
                    bl = true;
                    if (slot instanceof AssemblerSlot) break;
                }

                if (fromLast) i--;
                else i++;
            }
        }

        return bl;
    }
    @Override
    public boolean canUse(PlayerEntity player) {
        return table.canPlayerUse(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == 0 && button == 1) {
            table.setResult(ItemStack.EMPTY);
            if (table.isAllPartSuitable()) table.assemble();
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private class AssemblerSlot extends Slot {



        public AssemblerSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }

    public class ResultSlot extends AssemblerSlot {

        public ResultSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return table.isAllPartSuitable();
        }

        public boolean canTake() {
            return getStack().isEmpty() || table.isAllPartSuitable();
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof KovalItem && getStack().isEmpty() || table.partsIsEmpty();
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            if (getStack().isEmpty() && table.partsIsEmpty()) {
                stack = super.insertStack(stack, count);
                if (!getStack().isEmpty()) table.disassemble();
            }
            return stack;
        }

        @Override
        public void setStack(ItemStack stack) {
            if (!getStack().isEmpty()) table.clear();
            super.setStack(stack);
            if (!stack.isEmpty()) table.disassemble();
        }
        @Override
        public ItemStack takeStack(int amount) {
            if (table.isAllPartSuitable()) {
                ItemStack result = super.takeStack(amount);
                if (!result.isEmpty()) table.clear();
                return result;
            }

            return ItemStack.EMPTY;
        }

    }

    public class PartSlot extends AssemblerSlot {

        private final Vec2Int pos;

        public PartSlot(Inventory inventory, int index, int x, int y, Vec2Int pos) {
            super(inventory, index, x, y);
            this.pos = pos;
        }


        @Override
        public void markDirty() {
            super.markDirty();
            table.assemble();
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return KovalRegistry.PARTS.get(Registry.ITEM.getId(stack.getItem())) != null;
        }

        public boolean isSuitable() {
            return table.isPartSuitable(pos);
        }
    }

}
