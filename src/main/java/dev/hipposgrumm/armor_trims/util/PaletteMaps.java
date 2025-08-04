package dev.hipposgrumm.armor_trims.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.OverlayRegistry;
import dev.hipposgrumm.armor_trims.api.TrimRegistry;
import dev.hipposgrumm.armor_trims.api.trimming.ItemOverlay;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.TrimPattern;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.ArmorTrimPattern;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.ItemTags;
//? if >=1.18 {
import net.minecraft.tags.TagKey;
//?} elif fabric {
/*import net.fabricmc.fabric.api.tag.TagRegistry;
*///?}

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaletteMaps {
    private final ResourceManager resourceManager;
    // Map<OverlayResourceLocation, TextureData>
    private Map<ResourceLocation, Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>>> itemTexturesMap;

    // These are hashmaps so that the values can be overridden.
    private final Map<String, Entry> tags = new HashMap<>();
    private final Map<String, Entry> items = new HashMap<>();
    private List<Entry> entries;

    public PaletteMaps(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void addTag(String tag, String palette) {
        this.tags.put(tag, new Entry(new ResourceLocation(tag), palette, true));
    }

    public void addItem(String item, String palette) {
        this.items.put(item, new Entry(new ResourceLocation(item), palette, false));
    }

    public synchronized List<Entry> entries() {
        if (entries == null) entries = Stream.concat(tags.values().stream(), items.values().stream()).collect(Collectors.toList());
        return entries;
    }

    public synchronized Map<ResourceLocation, Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>>> getItemTexturesMap() {
        if (itemTexturesMap == null) processItems();
        return itemTexturesMap;
    }

    synchronized void prepareColors() {
        // Add colors from tags.
        for (Entry tag : tags.values()) {
            if (tag.color != null) continue;
            entries = null;

            try {
                ResourceLocation location = new ResourceLocation(tag.palette);
                location = new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png");
                //? if >=1.19 {
                try {
                    Optional<Resource> resource = resourceManager.getResource(location);
                    if (resource.isEmpty()) {
                        Armortrims.LOGGER.error("Palette at {} not found!", location);
                        return;
                    }
                //?} else {
                /*try (Resource resource = resourceManager.getResource(location)) {
                *///?}
                    tag.color = ColorPaletteManager.add(
                            //? if >=1.18.2 {
                            TagKey.create(Registry.ITEM_REGISTRY,
                            //?} elif forge {
                            /*ItemTags.createOptional(
                            *///?} else {
                            /*TagRegistry.item(
                            *///?}
                                    tag.id),
                            location,
                            NativeImage.read(resource./*? if >=1.19 {*/get().open/*?} else {*//*getInputStream*//*?}*/()),
                            resource./*? if >=1.19 {*/get().metadata().getSection/*?} else {*//*getMetadata*//*?}*/(AnimationMetadataSection.SERIALIZER)/*? if >=1.19 {*/.orElse(null)/*?}*/
                    );
                } catch (ResourceLocationException e) {
                    Armortrims.LOGGER.warn("#{} has invalid resourcelocation {}", tag.id, location);
                } catch (Exception e) {
                    Armortrims.LOGGER.error("#{} had a problem while loading {}", tag.id, location);
                }
            } catch (ResourceLocationException e) {
                Armortrims.LOGGER.warn("#{} has invalid palette resourcelocation {}", tag.id, tag.palette);
            }
        }

        // Add colors from items.
        for (Entry item : items.values()) {
            if (item.color != null) continue;
            entries = null;

            try {
                ResourceLocation location = new ResourceLocation(item.palette);
                location = new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png");
                //? if >=1.19 {
                try {
                    Optional<Resource> resource = resourceManager.getResource(location);
                    if (resource.isEmpty()) {
                        Armortrims.LOGGER.error("Palette at {} not found!", location);
                        return;
                    }
                //?} else {
                /*try (Resource resource = resourceManager.getResource(location)) {
                *///?}
                    item.color = ColorPaletteManager.add(
                            item.id,
                            location,
                            NativeImage.read(resource./*? if >=1.19 {*/get().open/*?} else {*//*getInputStream*//*?}*/()),
                            resource./*? if >=1.19 {*/get().metadata().getSection/*?} else {*//*getMetadata*//*?}*/(AnimationMetadataSection.SERIALIZER)/*? if >=1.19 {*/.orElse(null)/*?}*/
                    );
                } catch (ResourceLocationException e) {
                    Armortrims.LOGGER.warn("{} has invalid resourcelocation {}", item.id, location);
                } catch (Exception e) {
                    Armortrims.LOGGER.error("{} had a problem while loading {}", item.id, location);
                }
            } catch (ResourceLocationException e) {
                Armortrims.LOGGER.warn("{} has invalid palette resourcelocation {}", item.id, item.palette);
            }
        }
    }

    void processItems() {
        Map<ResourceLocation, Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>>> map = new HashMap<>();

        // Create item textures for all palettes.
        List<Entry> entries = entries();
        List<ColorPalette> colors = entries.stream().map(Entry::color).collect(Collectors.toList());
        for (ItemOverlay overlay:OverlayRegistry.ITEM_OVERLAYS) {
            ResourceLocation location = overlay.textureLocation();
            location = new ResourceLocation(location.getNamespace(), "textures/"+location.getPath()+".png");
            map.put(overlay.textureLocation(), TrimTextureManager.makeTextures(overlay.textureLocation(), location, entries, colors, true));
        }

        this.itemTexturesMap = map;
    }

    void processArmor() {
        // Create armor textures for all palettes.
        List<Entry> entries = entries();
        List<ColorPalette> colors = entries.stream().map(Entry::color).collect(Collectors.toList());
        ResourceLocation layer;
        for (TrimPattern pattern:TrimRegistry.ITEM_TRIMS) {
            if (pattern instanceof ArmorTrimPattern) {
                ArmorTrimPattern trimPattern = (ArmorTrimPattern) pattern;
                layer = trimPattern.getLayer0();
                TrimTextureManager.makeTextures(layer, layer, entries, colors, false);

                layer = trimPattern.getLayer1();
                TrimTextureManager.makeTextures(layer, layer, entries, colors, false);
            }
        }

        TrimTextureManager.onReloadDone();
    }

    public static class Entry {
        final ResourceLocation id;
        final String palette;
        final boolean isTag;

        ColorPalette color;

        public Entry(ResourceLocation id, String palette, boolean isTag) {
            this.id = id;
            this.palette = palette;
            this.isTag = isTag;
        }

        public ResourceLocation id() {
            return id;
        }

        public ColorPalette color() {
            return color;
        }

        public boolean isTag() {
            return isTag;
        }
    }
}
