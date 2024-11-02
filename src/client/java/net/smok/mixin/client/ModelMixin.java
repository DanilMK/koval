package net.smok.mixin.client;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.smok.BakedAssembledModel;
import net.smok.Debug;
import net.smok.koval.items.KovalItem;
import net.smok.koval.items.KovalStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ModelMixin {

    @Shadow public abstract ItemModels getModels();

    @Inject(method = "getModel", at = @At("TAIL"), cancellable = true)
    private void getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack == null || !(stack.getItem() instanceof KovalItem)) return;

        BakedModel baseModel = cir.getReturnValue();

        BakedModelManager modelManager = getModels().getModelManager();
        BakedModel missingModel = modelManager.getMissingModel();

        List<BakedModel> bakedModels = kovalStack.getModelIds().stream().map(identifier -> {
            ModelIdentifier modelId = new ModelIdentifier(identifier.getNamespace(), "part/" + identifier.getPath(), "inventory");
            BakedModel partModel = modelManager.getModel(modelId);

            if (partModel != missingModel) return partModel;
            return modelManager.getModel(new ModelIdentifier(identifier, "inventory"));
        }).toList();

        if (bakedModels.isEmpty()) return;

        cir.setReturnValue(new BakedAssembledModel(bakedModels,
                baseModel == null ? ModelTransformation.NONE : baseModel.getTransformation(),
                baseModel != null && baseModel.hasDepth()));

    }

}
