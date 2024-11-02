package net.smok.koval.assembler;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public final class AssemblerBlockFactory implements ScreenHandlerType.Factory<AssemblerScreenHandler>, BlockEntityType.BlockEntityFactory<AssemblerTableBlockEntity> {
    private final Identifier id;
    private final int width;
    private final int height;

    private final BlockItem item;
    private final AssemblerTableBlock block;

    private final ScreenHandlerType<AssemblerScreenHandler> handlerType;
    private final BlockEntityType<AssemblerTableBlockEntity> entityType;

    public AssemblerBlockFactory(Identifier id, int width, int height, AbstractBlock.Settings blockSettings, Item.Settings itemSettings) {
        this.id = id;
        this.width = width;
        this.height = height;

        handlerType = new ScreenHandlerType<>(this);
        block = new AssemblerTableBlock(blockSettings, this, width, height);
        item = new BlockItem(block, itemSettings);

        entityType = BlockEntityType.Builder.create(this, block)
                .build(Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id.getPath()));

    }

    public AssemblerBlockFactory register() {
        Registry.register(Registry.SCREEN_HANDLER, id, handlerType);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, item);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, entityType);
        return this;
    }

    public Identifier id() {
        return id;
    }

    public BlockEntityType<AssemblerTableBlockEntity> getEntityType() {
        return entityType;
    }

    public ScreenHandlerType<AssemblerScreenHandler> getHandlerType() {
        return handlerType;
    }

    @Override
    public AssemblerTableBlockEntity create(BlockPos pos, BlockState state) {
        return new AssemblerTableBlockEntity(this, pos, state, width, height);
    }

    @Override
    public AssemblerScreenHandler create(int syncId, PlayerInventory playerInventory) {
        return new AssemblerScreenHandler(handlerType, syncId, playerInventory, width, height);
    }
}
