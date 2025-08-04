package dev.hipposgrumm.armor_trims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.mixinaccess.DuckModelBakery;
import dev.hipposgrumm.armor_trims.model.ItemTrimModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ModelBakery.class)
public class MixinModelBakery implements DuckModelBakery {
    @Shadow @Final /*? if forge {*/protected/*?} else {*//*private*//*?}*/ static Set<Material> UNREFERENCED_TEXTURES;

    //? if >=1.19.3
    /*@Shadow @Final private Map<ResourceLocation, BlockModel> modelResources;*/
    @Unique private ResourceLocation armor_trims$trimAtlasTexture = null;

    @Override
    public void armor_trims$setTrimAtlasTexture(ResourceLocation location) {
        armor_trims$trimAtlasTexture = location;
    }

    @WrapOperation(/*? if forge && <1.19 {*//*remap = false, method = "processLoading"*//*?} else {*/method = "<init>"/*?}*/, at = @At(remap = true, value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;prepareToStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/util/profiling/ProfilerFiller;I)Lnet/minecraft/client/renderer/texture/TextureAtlas$Preparations;"))
    private TextureAtlas.Preparations armor_trims$addTrimModels_prepare(TextureAtlas instance, ResourceManager resourceManager, Stream<ResourceLocation> textures, ProfilerFiller profiler, int i, Operation<TextureAtlas.Preparations> original) {
        if (instance.location() != TextureAtlas.LOCATION_BLOCKS) return original.call(instance, resourceManager, textures, profiler, i);
        return Armortrims.trimTextures().prepare(textures, res -> original.call(instance, resourceManager, res, profiler, i));
    }

    @Inject(method = "uploadTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;reload(Lnet/minecraft/client/renderer/texture/TextureAtlas$Preparations;)V", shift = At.Shift.AFTER))
    private void armor_trims$addTrimModels_apply(TextureManager textureManager, ProfilerFiller profiler, CallbackInfoReturnable<AtlasSet> cir) {
        Armortrims.trimTextures().apply();
    }

    @WrapOperation(/*? if forge && <1.19 {*//*remap = false, method = "processLoading"*//*?} else {*/method = "<init>"/*?}*/, at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
    private boolean armor_trims$createTrimMaterials(Set<?> set, Collection<?> additions, Operation<Boolean> original) {
        boolean success = original.call(set, additions);
        if (additions == UNREFERENCED_TEXTURES) original.call(set, ItemTrimModels.generated.keySet().stream().map(generated -> new Material(TextureAtlas.LOCATION_BLOCKS, generated)).collect(Collectors.toSet()));
        return success;
    }

    @WrapOperation(
            method = "loadBlockModel",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.19.3 {
                    /*target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
                    *///?} elif >=1.19 {
                    target = "Lnet/minecraft/server/packs/resources/ResourceManager;openAsReader(Lnet/minecraft/resources/ResourceLocation;)Ljava/io/BufferedReader;"
                    //?} else {
                    /*target = "Lnet/minecraft/server/packs/resources/ResourceManager;getResource(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/Resource;"
                    *///?}
            )
    )
    private /*? if >=1.19.3 {*//*<V> V*//*?} elif >=1.19 {*/BufferedReader/*?} else {*//*Resource*//*?}*/ armor_trims$swapTrimModel(/*? if >=1.19.3 {*//*Map*//*?} else {*/ResourceManager/*?}*/ instance, /*? if >=1.19.3 {*//*Object*//*?} else {*/ResourceLocation/*?}*/ location, Operation</*? if >=1.19.3 {*//*V*//*?} elif >=1.19 {*/BufferedReader/*?} else {*//*Resource*//*?}*/> original, @Local(argsOnly = true) ResourceLocation generated) {
        Pair<ResourceLocation, ?> gen = ItemTrimModels.generated.get(generated);
        if (gen != null) {
            ResourceLocation originalLocation = gen.getFirst();
            location = new ResourceLocation(originalLocation.getNamespace(), "models/" + originalLocation.getPath() + ".json");
        }
        return original.call(instance, location);
    }

    //? if >=1.19.3 {
    /*@Mixin(ModelBakery.)
    public static class MixinBakeryImpl {
    *///?}

    // This is the only one that needs to change on both Fabric and Forge.
    @WrapOperation(
            // Forge has split the implementation of bake into a separate method that takes the sprite getter as a parameter.
            //? if forge {
            remap = false,
            //? if >=1.17 {
            method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
            //?} else {
            /*method = "getBakedModel",
            *///?}
            //?} else {
            /*method = "bake",
            *///?}
            at = @At(remap = true, value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;")
    )
    private BakedModel armor_trims$swapTrimSpriteGetter_bakeblockmodel(BlockModel instance, ModelBakery modelBakery, BlockModel model, Function<Material, TextureAtlasSprite> sprites, ModelState state, ResourceLocation location, boolean guiLight3d, Operation<BakedModel> original) {
        if (armor_trims$trimAtlasTexture != null) sprites = ItemTrimModels.getSpriteFunction(sprites, armor_trims$trimAtlasTexture);
        return original.call(instance, modelBakery, model, sprites, state, location, guiLight3d);
    }


    //? if forge {
    @Inject(
            remap = false,
            //? if >=1.17 {
            method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BakedModel;",
            //?} else {
            /*method = "getBakedModel",
            *///?}
            at = @At("HEAD")
    )
    private void armor_trims$swapTrimSpriteGetter_forge(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> originalSprites, CallbackInfoReturnable<BakedModel> cir, @Local(argsOnly = true) LocalRef<Function<Material, TextureAtlasSprite>> sprites) {
        if (armor_trims$trimAtlasTexture != null) sprites.set(ItemTrimModels.getSpriteFunction(originalSprites, armor_trims$trimAtlasTexture));
    }
    //?} else {
    /*@WrapOperation(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemModelGenerator;generateBlockModel(Ljava/util/function/Function;Lnet/minecraft/client/renderer/block/model/BlockModel;)Lnet/minecraft/client/renderer/block/model/BlockModel;"))
    private BlockModel armor_trims$swapTrimSpriteGetter_generateblockmodel(ItemModelGenerator instance, Function<Material, TextureAtlasSprite> sprites, BlockModel model, Operation<BlockModel> original, @Local(argsOnly = true) ResourceLocation location) {
        if (armor_trims$trimAtlasTexture != null) sprites = ItemTrimModels.getSpriteFunction(sprites, armor_trims$trimAtlasTexture);
        return original.call(instance, sprites, model);
    }

    @WrapOperation(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/UnbakedModel;bake(Lnet/minecraft/client/resources/model/ModelBakery;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel armor_trims$swapTrimSpriteGetter_bakeunbakedmodel(UnbakedModel instance, ModelBakery modelBakery, Function<Material, TextureAtlasSprite> sprites, ModelState state, ResourceLocation location, Operation<BakedModel> original) {
        if (armor_trims$trimAtlasTexture != null) sprites = ItemTrimModels.getSpriteFunction(sprites, armor_trims$trimAtlasTexture);
        return original.call(instance, modelBakery, sprites, state, location);
    }
    *///?}
    //? if >=1.19.3
    /*}*/
}