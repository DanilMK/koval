package net.smok.mixin.client;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class LoaderMixin {


    @Shadow
    protected abstract void putModel(Identifier id, UnbakedModel unbakedModel);

    @Shadow protected abstract JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException;

    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;


    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;

    /**
     * Load also part model. See {@link ModelMixin}
     */
    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;putModel(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V"))
    private void loadModel(Identifier id, CallbackInfo ci) throws IOException {

        Identifier jsonId = new Identifier(id.getNamespace(), "item/part/" + id.getPath());
        ModelIdentifier modelId = new ModelIdentifier(id.getNamespace(), "part/" + id.getPath(), "inventory");


        JsonUnbakedModel jsonUnbakedModel;
        try {
            jsonUnbakedModel = loadModelFromJson(jsonId);
        } catch (FileNotFoundException ignored) {
            return;
        }
        putModel(modelId, jsonUnbakedModel);
        unbakedModels.put(jsonId, jsonUnbakedModel);
        modelsToBake.put(modelId, jsonUnbakedModel);
    }

}