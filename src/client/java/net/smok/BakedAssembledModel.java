package net.smok;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BakedAssembledModel implements BakedModel, FabricBakedModel {

    private final List<BakedModel> parts;
    private final ModelTransformation transformation;
    private final boolean hasDepth;



    public BakedAssembledModel(List<BakedModel> parts, ModelTransformation transformation, boolean hasDepth) {
        this.parts = parts;
        this.transformation = transformation;
        this.hasDepth = hasDepth;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        parts.forEach(bakedModel -> ((FabricBakedModel)bakedModel).emitBlockQuads(blockView, state, pos, randomSupplier, context));
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        parts.forEach(bakedModel -> ((FabricBakedModel)bakedModel).emitItemQuads(stack, randomSupplier, context));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return parts.stream().flatMap(bakedModel -> bakedModel.getQuads(state, face, random).stream()).collect(Collectors.toList());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return hasDepth;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        if (parts.isEmpty()) return null;
        return parts.get(0).getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return null;
    }
}
