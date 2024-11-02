package net.smok.koval.assembler;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AssemblerTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Nameable {


    private Text customName;
    private final Assembler table;
    private final AssemblerBlockFactory factory;
    private final int rows, columns;




    public AssemblerTableBlockEntity(AssemblerBlockFactory factory, BlockPos pos, BlockState state, int columns, int rows) {
        super(factory.getEntityType(), pos, state);
        this.factory = factory;
        table = new Assembler(columns, rows, this);
        this.rows = rows;
        this.columns = columns;
    }



    @Override
    public Text getName() {
        return customName != null ? customName : Text.translatable("container.assemble_table");
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }


    // Save/Load

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        table.readNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        table.writeNbt(nbt);
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AssemblerScreenHandler(factory.getHandlerType(), syncId, table, player.getInventory(), columns, rows);
    }

    public void summonDrop() {
        if (world != null) ItemScatterer.spawn(world, pos, table.getParts());
    }


}
