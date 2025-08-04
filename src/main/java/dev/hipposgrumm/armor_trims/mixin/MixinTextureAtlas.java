package dev.hipposgrumm.armor_trims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.util.PaletteMaps;
import dev.hipposgrumm.armor_trims.model.ItemTrimModels;
import dev.hipposgrumm.armor_trims.util.TrimTextureManager;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
//? if >=1.19
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.*;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas {
    @Shadow @Final private ResourceLocation location;

    @Unique private PaletteMaps armor_trims$paletteMaps;

    @Inject(method = "getBasicSpriteInfos", at = @At("HEAD"))
    private void armor_trims$prepareOverlaySpriteNames(ResourceManager resourceManager, Set<ResourceLocation> locations, CallbackInfoReturnable<Collection<TextureAtlasSprite.Info>> cir) {
        if (this.location == TextureAtlas.LOCATION_BLOCKS) {
            armor_trims$paletteMaps = Armortrims.trimTextures().getPaletteMaps(resourceManager);
        }
    }

    @WrapOperation(// targeting lambda in getBasicSpriteInfos
            remap = false,
            //? if fabric {
            /*method = "method_18160",
            *///?} elif >=1.18 {
            method = {"m_174717_", "method_18160"},
            //?} else {
            /*method = {"func_224738_a", "method_18160"},
            *///?}
            at = @At(remap = true, value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;getResourceLocation(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;")
    )
    private ResourceLocation armor_trims$redirectTrimInfoResourceLocation(TextureAtlas instance, ResourceLocation location, Operation<ResourceLocation> original, @Share("data") LocalRef<Pair<ResourceLocation, ColorPalette>> data, @Share("originalLocation") LocalRef<ResourceLocation> originalLocation) {
        if (this.location == TextureAtlas.LOCATION_BLOCKS) {
            Pair<ResourceLocation, ColorPalette> _data = ItemTrimModels.generated.get(location);
            if (_data != null) {
                originalLocation.set(location);
                location = _data.getFirst();
                data.set(_data);
            }
        }
        return original.call(instance, location);
    }

    @WrapOperation(// targeting lambda in getBasicSpriteInfos
            remap = false,
            //? if fabric {
            /*method = "method_18160",
            *///?} elif >=1.18 {
            method = {"method_18160", "m_174717_"},
            //?} else {
            /*method = {"method_18160", "func_224738_a"},
            *///?}
            at = @At(
                    remap = true,
                    value = "INVOKE",
                    //? if >=1.19 {
                    target = "Lnet/minecraft/server/packs/resources/ResourceMetadata;getSection(Lnet/minecraft/server/packs/metadata/MetadataSectionSerializer;)Ljava/util/Optional;"
                    //?} else {
                    /*target = "Lnet/minecraft/server/packs/resources/Resource;getMetadata(Lnet/minecraft/server/packs/metadata/MetadataSectionSerializer;)Ljava/lang/Object;"
                    *///?}
            )
    )
    private /*? if >=1.19 {*/Optional<?>/*?} else {*//*Object*//*?}*/ armor_trims$addTrimSpriteInfos(/*? if >=1.19 {*/ResourceMetadata/*?} else {*//*Resource*//*?}*/ instance, MetadataSectionSerializer/*? if >=1.19 {*/<?/*?} else {*//*<AnimationMetadataSection*//*?}*/> serializer, Operation</*? if >=1.19 {*/Optional<?>/*?} else {*//*Object*//*?}*/> original, @Share("data") LocalRef<Pair<ResourceLocation, ColorPalette>> data, @Share("originalLocation") LocalRef<ResourceLocation> originalLocation) {
        if (this.location == TextureAtlas.LOCATION_BLOCKS) {
            Map<ResourceLocation, Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>>> itemTexturesMap = armor_trims$paletteMaps.getItemTexturesMap();

            Pair<ResourceLocation, ColorPalette> _data = data.get();
            if (_data != null && itemTexturesMap.containsKey(_data.getFirst())) {
                Pair<NativeImage[], AnimationMetadataSection> image = itemTexturesMap
                        .get(_data.getFirst())
                        .get(_data.getSecond());
                if (image == null) {
                    String err = String.format("Unable to get metadata for %s", originalLocation.get());
                    Armortrims.LOGGER.error(err);
                    //? if >=1.19 {
                    return Optional.empty();
                    //?} else {
                    /*return AnimationMetadataSection.EMPTY;
                    *///?}
                }
                //? if >=1.19 {
                return Optional.of(image.getSecond());
                //?} else {
                /*return image.getSecond();
                *///?}

            }
        }
        return original.call(instance, serializer);
    }

    // Replace resourcelocation with the valid one.
    @WrapOperation(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;getResourceLocation(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation armor_trims$setActualGeneratedTrimSpriteName(TextureAtlas instance, ResourceLocation generated, Operation<ResourceLocation> original, @Share("generated") LocalRef<ResourceLocation> outGenerated, @Share("location") LocalRef<ResourceLocation> outLocation) {
        if (this.location == TextureAtlas.LOCATION_BLOCKS && !ItemTrimModels.generated.isEmpty()) {
            Pair<ResourceLocation, ColorPalette> location = ItemTrimModels.generated.get(generated);
            if (location != null) {
                outGenerated.set(generated);
                generated = location.getFirst();
            }
            outLocation.set(generated);
        }
        return original.call(instance, generated);
    }

    @WrapOperation(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;read(Ljava/io/InputStream;)Lcom/mojang/blaze3d/platform/NativeImage;"))
    private NativeImage armor_trims$setSpriteImage(InputStream stream, Operation<NativeImage> original, @Share("generated") LocalRef<ResourceLocation> generated, @Share("location") LocalRef<ResourceLocation> location) {
        ResourceLocation generatedLocation = generated.get();
        if (generatedLocation != null) {
            return armor_trims$paletteMaps
                    .getItemTexturesMap().get(location.get())
                    .get(ItemTrimModels.generated.get(generatedLocation).getSecond())
                    .getFirst()[0];
        } else {
            return original.call(stream);
        }
    }

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("RETURN"))
    private void armor_trims$rememberGeneratedSprite(ResourceManager resourceManager, TextureAtlasSprite.Info info, int storageX, int storageY, int mipLevel, int x, int y, CallbackInfoReturnable<TextureAtlasSprite> cir, @Share("generated") LocalRef<ResourceLocation> generated) {
        ResourceLocation generatedLocation = generated.get();
        if (generatedLocation != null) {
            ItemTrimModels.generatedSprites.put(generatedLocation, cir.getReturnValue());
        }
    }
}