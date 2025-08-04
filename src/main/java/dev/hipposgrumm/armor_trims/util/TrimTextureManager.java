package dev.hipposgrumm.armor_trims.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.model.ArmorTrimTexture;
import dev.hipposgrumm.armor_trims.model.ItemTrimModels;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
//? if >=1.18 {
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraft.tags.Tag;
//? if fabric
/^import net.fabricmc.fabric.api.tag.TagRegistry;^/
*///?}
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
//?}

// Class for managing all the generated trim textures.
public class TrimTextureManager {
    public static final String TRIM_DEFINITION_LOCATION = "textures/trims/color_palettes.json";

    private PaletteMaps paletteMaps;

    // Map<MaterialTag,Map<TrimTexture,GeneratedResourceLocation>>
    // Textures for tag-defined trims.
    private static final Map<ResourceLocation, Map<ResourceLocation,ResourceLocation>> tagTrimTextures = new HashMap<>();

    // Map<Material,Map<TrimTexture,GeneratedResourceLocation>>
    // Textures for item-defined trims.
    private static final Map<ResourceLocation, Map<ResourceLocation,ResourceLocation>> trimTextures = new HashMap<>();

    // Map<Material,MaterialTag>
    // Map items from their item tags.
    private static final Map<ResourceLocation, ResourceLocation> tagItemTrimTextures = new HashMap<>();

    // Map<TagLocation,Tag>
    // This is because it broke sub-1.18
    private static final Map<ResourceLocation, /*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item>> tagRefs = new HashMap<>();

    private static boolean loading = true;
    private static long count = 0;

    private boolean prepared = false;

    public TrimTextureManager() {}

    public boolean ready() {
        return paletteMaps != null;
    }

    public synchronized PaletteMaps getPaletteMaps(ResourceManager resourceManager) {
        if (paletteMaps == null) loadPalettes(resourceManager);
        return paletteMaps;
    }

    private void loadPalettes(ResourceManager resourceManager) {
        PaletteMaps paletteMaps = new PaletteMaps(resourceManager);
        for (String namespace:resourceManager.getNamespaces()) {
            //? if <1.19
            /*try {*/
                List<Resource> resourceList = resourceManager./*? if >=1.19 {*/getResourceStack/*?} else {*//*getResources*//*?}*/(new ResourceLocation(namespace, TRIM_DEFINITION_LOCATION));
            //? if >=1.19
            if (!resourceList.isEmpty()) {
                boolean successful = false;
                for (Resource resource : resourceList) {
                    try {
                        try {
                            InputStream inputstream = resource./*? if >=1.19 {*/open/*?} else {*//*getInputStream*//*?}*/();
                            try {
                                Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8); // Not AutoClosable
                                try {
                                    JsonObject parsableJsonObject = GsonHelper.parse(reader);
                                    if (parsableJsonObject.has("tag") && parsableJsonObject.get("tag").isJsonArray())
                                        for (JsonElement tag:parsableJsonObject.get("tag").getAsJsonArray()) {
                                            JsonObject object = tag.getAsJsonObject();
                                            paletteMaps.addTag(object.get("id").getAsString(), object.get("path").getAsString());

                                        }
                                    if (parsableJsonObject.has("item") && parsableJsonObject.get("item").isJsonArray())
                                        for (JsonElement item:parsableJsonObject.get("item").getAsJsonArray()) {
                                            JsonObject object = item.getAsJsonObject();
                                            paletteMaps.addItem(object.get("id").getAsString(), object.get("path").getAsString());
                                        }
                                    successful = true;
                                } catch (Throwable parseError) {
                                    try {
                                        reader.close();
                                    } catch (Throwable closeError) {
                                        parseError.addSuppressed(closeError);
                                    }
                                    throw parseError;
                                }
                                reader.close();
                            } catch (Throwable readError) { // Probably not what this actually may catch, but that is what I made of it.
                                if (inputstream != null) {
                                    try {
                                        inputstream.close();
                                    } catch (Throwable closeError) {
                                        readError.addSuppressed(closeError);
                                    }
                                }
                                throw readError;
                            }
                            if (inputstream != null) inputstream.close();
                        } catch (RuntimeException e) {
                            Armortrims.LOGGER.warn("Invalid {} in namespace: '{}'", TRIM_DEFINITION_LOCATION, namespace);
                        }
                    } catch (Throwable ignored) {}
                    //? if <1.19
                    /*if (resource != null) resource.close();*/
                }
                if (successful) Armortrims.LOGGER.debug("Loaded trim palettes definition from {}.", namespace);
                else Armortrims.LOGGER.error("Found trim palettes definition in {}, but it didn't load properly.", namespace);
            //? if >=1.19 {
            } else {
            //?} else {
            /*} catch (IOException ignored) {
            *///?}
                if (Armortrims.MODID.equals(namespace)) Armortrims.LOGGER.error("Palettes definition not found in {}!", namespace);
                else Armortrims.LOGGER.debug("No trim palettes definition in {}.", namespace);
            }
        }
        paletteMaps.prepareColors();
        this.paletteMaps = paletteMaps;
    }

    //@Override
    public Stream<ResourceLocation> getResourcesToLoad() {
        List<ResourceLocation> ids = new ArrayList<>();

        int count = 0;
        PaletteMaps paletteMaps = getPaletteMaps(Minecraft.getInstance().getResourceManager());
        Map<ResourceLocation, Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>>> itemTexturesMap = paletteMaps.getItemTexturesMap();
        for (ResourceLocation overlayLocation:itemTexturesMap.keySet()) {
            List<Pair<ResourceLocation, ColorPalette>> generated = new ArrayList<>();
            for (PaletteMaps.Entry entry:paletteMaps.entries()) {
                ResourceLocation id = new ResourceLocation(Armortrims.MODID, "generateditem_" + Long.toString(count, Character.MAX_RADIX));
                count++;

                ColorPalette color = entry.color();
                ItemTrimModels.generated.put(id, new Pair<>(overlayLocation, color));
                generated.add(new Pair<>(id, color));
                ids.add(id);
            }
            ItemTrimModels.generatedLocations.put(overlayLocation, generated);
        }

        return ids.stream();
    }

    public TextureAtlas.Preparations prepare(Stream<ResourceLocation> res, Function<Stream<ResourceLocation>, TextureAtlas.Preparations> original) {
        // Unready the data.
        this.paletteMaps = null;
        ColorPaletteManager.onReload();
        ItemTrimModels.onReload();

        // Container is redefined by a call here.
        TextureAtlas.Preparations preparations = original.apply(Stream.concat(res, getResourcesToLoad()));

        loading = true;

        tagTrimTextures.clear();
        trimTextures.clear();

        prepared = true;
        return preparations;
    }

    public void apply() {
        if (!prepared) return;
        paletteMaps.processArmor();
        Minecraft.getInstance().getTextureManager().register(new ResourceLocation(Armortrims.MODID, "palette_ticker"), new ColorPaletteManager());
        prepared = false;
    }

    public static Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>> makeTextures(ResourceLocation texture, ResourceLocation location, List<PaletteMaps.Entry> entries, List<ColorPalette> colors, boolean forItem) {
        if (!forItem) {
            RenderSystem.recordRenderCall(() -> {
                Minecraft.getInstance().getTextureManager().register(texture, new SimpleTexture(location));
                Armortrims.LOGGER.debug("Registered default for texture {}.", texture);
            });
        }

        //? if >=1.19 {
        try {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
            if (resource.isEmpty()) {
                Armortrims.LOGGER.error(String.format("Resource %s is missing!", location));
                return null;
            }
        //?} else {
        /*try (Resource resource = Minecraft.getInstance().getResourceManager().getResource(location)) {
        *///?}
            NativeImage baseImage = NativeImage.read(resource./*? if >=1.19 {*/get().open/*?} else {*//*getInputStream*//*?}*/());

            Map<ColorPalette, Pair<NativeImage[], AnimationMetadataSection>> textures = ColorPalette.apply(colors, baseImage, forItem);

            if (forItem) return textures;

            for (PaletteMaps.Entry entry:entries) {
                // Register the texture.
                ResourceLocation id = new ResourceLocation(Armortrims.MODID,"generated_"+Long.toString(count,Character.MAX_RADIX));
                count++;

                boolean isTag = entry.isTag();
                Pair<NativeImage[], AnimationMetadataSection> images = textures.get(entry.color());
                RenderSystem.recordRenderCall(() -> {
                    if (images.getFirst().length == 0) return;

                    List<Integer> frames = new ArrayList<>();
                    entry.color().forEachFrame((index,time) -> {
                        for (int i=0;i<time;i++) frames.add(index);
                    }, true);
                    Minecraft.getInstance().getTextureManager().register(id, ArmorTrimTexture.create(entry, images.getFirst(), frames.toArray(new Integer[0])));

                    if (isTag) {
                        tagRefs.computeIfAbsent(entry.id(),
                                //? if >=1.18.2 {
                                (k) -> TagKey.create(Registry.ITEM_REGISTRY, k)
                                //?} elif forge {
                                /*ItemTags::createOptional
                                *///?} else {
                                /*TagRegistry::item
                                *///?}
                        );
                        tagTrimTextures.compute(entry.id(), (k, map) -> {
                            if (map == null) map = new HashMap<>();
                            map.put(texture, id);
                            return map;
                        });
                    } else {
                        trimTextures.compute(entry.id(), (k, map) -> {
                            if (map == null) map = new HashMap<>();
                            map.put(texture, id);
                            return map;
                        });
                    }

                    Armortrims.LOGGER.debug("Applied palette of {} to texture {}.", (isTag?"#":"")+entry.id(), texture);
                });
            }
        } catch (Exception e) {
            Armortrims.LOGGER.error("Failed to apply any or all color palettes to texture {}.", texture);
        }
        return null;
    }

    // Get the texture to render, with the animation frame already set.
    public static ResourceLocation get(ResourceLocation trimTexture, ResourceLocation material) {
        if (loading) return null;

        if (trimTextures.containsKey(material)) {
            // Explicitly-Defined Trim Texture
            return trimTextures.get(material).getOrDefault(trimTexture, trimTexture);
        } else if (tagItemTrimTextures.containsKey(material)) {
            // Tag-Defined Trim Texture
            ResourceLocation tag = tagItemTrimTextures.get(material);
            if (tag == null) return trimTexture; // For if not existing.
            Map<ResourceLocation, ResourceLocation> textures = tagTrimTextures.get(tag);
            if (textures == null) return trimTexture;
            return textures.getOrDefault(trimTexture, trimTexture);
        } else {
            // Search all tags for a texture.
            //? if forge {
            Item item = ForgeRegistries.ITEMS.getValue(material);
            //?} else {
            /*Item item = Registry.ITEM.get(material);
            *///?}
            if (!(item instanceof AirItem)) {
                ResourceLocation tag = null;
                for (Map.Entry<ResourceLocation, /*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item>> t : tagRefs.entrySet()) if (item/*? if >=1.18.2 {*/.getDefaultInstance()/*?}*/.is(t.getValue())) tag = t.getKey();
                // Add item to this list for quicker lookup later.
                tagItemTrimTextures.put(material,tag); // If no tag is matching this will be null.
            }
        }

        return trimTexture;
    }

    public static void onReloadDone() {
        loading = false;
    }

    public static void onReloadData() {
        // On Datapack Reload
        tagItemTrimTextures.clear();
    }
}
