package dev.hipposgrumm.armor_trims.model;

import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.OverlayRegistry;
import dev.hipposgrumm.armor_trims.api.trimming.ItemOverlay;
import dev.hipposgrumm.armor_trims.mixinaccess.DuckModelBakery;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemTrimModels {
    private static final Map<ItemOverlay, Map<ColorPalette, BakedModel>> bakedModels = new HashMap<>();

    // Map<GeneratedResourceLocation, Pair<OverlayTextureLocation, ColorPalette>>
    public static final Map<ResourceLocation, Pair<ResourceLocation, ColorPalette>> generated = new HashMap<>();

    // Map<OverlayTextureLocation, List<Pair<GeneratedResourceLocation, ColorPalette>>>
    public static final Map<ResourceLocation, List<Pair<ResourceLocation, ColorPalette>>> generatedLocations = new HashMap<>();

    // Map<GeneratedResourceLocation, TextureAtlasSprite>
    public static final Map<ResourceLocation, TextureAtlasSprite> generatedSprites = new ConcurrentHashMap<>();

    public static void onReload() {
        bakedModels.clear();
        generated.clear();
        generatedLocations.clear();
        generatedSprites.clear();
    }

    public static void registerModels(Consumer<ResourceLocation> consumer) {
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/empty_slot_smithing_template_armor_trim"));

        for (ItemOverlay overlay:OverlayRegistry.ITEM_OVERLAYS) {
            consumer.accept(overlay.textureLocation());
        }
    }

    public static void bakeModels(Map<ResourceLocation, BakedModel> map, ModelBakery bakery) {
        long time = System.nanoTime();
        try {
            for (ItemOverlay overlay:OverlayRegistry.ITEM_OVERLAYS) {
                ResourceLocation overlayLocation = overlay.textureLocation();
                Map<ColorPalette, BakedModel> models = new HashMap<>();
                models.put(ColorPalette.DEFAULT, map.get(overlayLocation));

                List<Pair<ResourceLocation, ColorPalette>> locations = generatedLocations.get(overlay.textureLocation());
                if (locations != null) for (Pair<ResourceLocation, ColorPalette> location : locations) {
                    ((DuckModelBakery) bakery).armor_trims$setTrimAtlasTexture(location.getFirst());
                    models.put(
                            location.getSecond(),
                            bakery.bake(overlayLocation, BlockModelRotation.X0_Y0)
                    );
                }
                bakedModels.put(overlay, models);
            }
        } finally { // In case something happens, don't break the game.
            ((DuckModelBakery)bakery).armor_trims$setTrimAtlasTexture(null);
            Armortrims.LOGGER.debug("Model baking took {} seconds.", (System.nanoTime()-time)/1000000000f);
        }
    }

    public static Function<Material, TextureAtlasSprite> getSpriteFunction(Function<Material, TextureAtlasSprite> original, ResourceLocation location) {
        TextureAtlasSprite sprite = generatedSprites.get(location);
        if (sprite != null) return m -> sprite;
        return original;
    }

    // Returns the model and a boolean depicting if it has color.
    public static Pair<BakedModel, Boolean> getModel(ItemOverlay overlay, ColorPalette palette) {
        Map<ColorPalette, BakedModel> coloredModels = bakedModels.getOrDefault(overlay, bakedModels.get(OverlayRegistry.defaultItemOverlay()));
        if (coloredModels == null) return null;
        BakedModel model = coloredModels.get(palette);
        if (model != null) {
            return new Pair<>(model, true);
        } else {
            return new Pair<>(coloredModels.get(ColorPalette.DEFAULT), false);
        }
    }
}
