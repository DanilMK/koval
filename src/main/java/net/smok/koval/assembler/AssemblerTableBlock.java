package net.smok.koval.assembler;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AssemblerTableBlock extends BlockWithEntity {

    private final int tableWidth, tableHeight;
    private final AssemblerBlockFactory factory;

    public AssemblerTableBlock(Settings settings, AssemblerBlockFactory factory, int tableWidth, int tableHeight) {
        super(settings);
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;
        this.factory = factory;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AssemblerTableBlockEntity(factory, pos, state, tableWidth, tableHeight);
    }

    public int getTableHeight() {
        return tableHeight;
    }

    public int getTableWidth() {
        return tableWidth;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof AssemblerTableBlockEntity assemblerTableBlockEntity)
            player.openHandledScreen(assemblerTableBlockEntity);

        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;

        if (world.getBlockEntity(pos) instanceof AssemblerTableBlockEntity tableBlock) {
            tableBlock.summonDrop();
            world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AssemblerTableBlockEntity assemblerTableBlockEntity) {
                assemblerTableBlockEntity.setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }
}
