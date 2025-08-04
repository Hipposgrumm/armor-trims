package dev.hipposgrumm.armor_trims.util.color;

import com.mojang.blaze3d.platform.NativeImage;
import dev.hipposgrumm.armor_trims.Armortrims;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
//? if >=1.18.2 {
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraft.tags.Tag;
*///?}
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
//?}

// This class extends AbstractTexture so that it can be ticked as a texture.
public class ColorPaletteManager extends AbstractTexture implements Tickable {
    private static final Map</*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item>, ColorPalette> colorListTags = new HashMap<>();
    // Map<ItemID, ColorPalette>
    private static final Map<ResourceLocation, ColorPalette> colorList = new HashMap<>();

    // This is a shortcut to associate an item with a tag without needing to go through all the tags.
    private static final Map<ResourceLocation, /*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item>> itemTagColorMap = new HashMap<>();
    private static final Set<ResourceLocation> noTaggedColors = new HashSet<>();

    private static final List<ColorPalette> colorPalettes = new ArrayList<>();

    public static void onReload() {
        // Mark all colors as discarded. This only benefits the NetheriteUpgradeSmithingTemplate.DiamondColored component class. It was added pretty late which is why this value isn't used much.
        for (ColorPalette color:colorPalettes) color.discard();
        colorPalettes.clear();

        colorListTags.clear();
        colorList.clear();
        noTaggedColors.clear();
    }

    public static void onReloadData() {
        itemTagColorMap.clear();
        noTaggedColors.clear();
    }

    public static ColorPalette add(ResourceLocation id, Item item) {
        ColorPalette.SingleColorPalette color = new ColorPalette.SingleColorPalette(id,item);
        if (!color.isValid()) {
            colorList.put(id,ColorPalette.DEFAULT);
            Armortrims.LOGGER.debug("No color created for {}, using default instead.", id);
            return ColorPalette.DEFAULT;
        }
        colorList.put(id,color);
        colorPalettes.add(color);
        return color;
    }

    public static ColorPalette add(/*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item> tag, ResourceLocation id, NativeImage texture, AnimationMetadataSection meta) {
        ColorPalette color = new ColorPalette(id,texture,meta);
        colorListTags.put(tag,color);
        colorPalettes.add(color);
        return color;
    }

    public static ColorPalette add(ResourceLocation item, ResourceLocation id, NativeImage texture, AnimationMetadataSection meta) {
        ColorPalette color = new ColorPalette(id,texture,meta);
        colorList.put(item,color);
        colorPalettes.add(color);
        return color;
    }

    //? if forge
    private static final ResourceLocation BARRIER_LOCATION = ForgeRegistries.ITEMS.getKey(Items.BARRIER);
    public static ColorPalette get(ResourceLocation resourceLocation) {
        // Missingno = DEFAULT
        if (resourceLocation.equals(MissingTextureAtlasSprite.getLocation())) return ColorPalette.DEFAULT;
        // Barrier also = DEFAULT
        //? if forge
        if (resourceLocation.equals(BARRIER_LOCATION)) return ColorPalette.DEFAULT;

        if (noTaggedColors.contains(resourceLocation)) {
            // Get color because no tag.
            return colorList.getOrDefault(resourceLocation, ColorPalette.DEFAULT);
        }

        // Check if the item is in a defined tag.
        /*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item> tag = itemTagColorMap.get(resourceLocation);
        if (tag != null) {
            ColorPalette color = colorListTags.get(tag);
            if (color != null) return color;
            // after this means that the color is no longer associated with a tag
            itemTagColorMap.remove(resourceLocation);
        }

        // Assign tag if not set up, or assign color for non-defined color.
        //? if forge {
        if (ForgeRegistries.ITEMS.containsKey(resourceLocation)) {
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        //?} else {
        /*if (Registry.ITEM.containsKey(resourceLocation)) {
            Item item = Registry.ITEM.get(resourceLocation);
        *///?}
            if (item != null && !(item instanceof AirItem)) {
                //? if >=1.18 {
                ItemStack itemInstance = item.getDefaultInstance();
                //?} else {
                /*Item itemInstance = item;
                *///?}
                // Check all tags for item. Select last.
                for (/*? if >=1.18.2 {*/TagKey/*?} else {*//*Tag*//*?}*/<Item> t:colorListTags.keySet()) if (itemInstance.is(t)) tag = t;
                if (tag != null) {
                    // Tag found, add shortcut.
                    itemTagColorMap.put(resourceLocation,tag);
                    return colorListTags.get(tag);
                }
                // No tag, create color palette.
                noTaggedColors.add(resourceLocation);
                ColorPalette color = colorList.get(resourceLocation);
                if (color == null) color = add(resourceLocation, item);
                return color;
            }
        }

        // When all else fails, DEFAULT.
        colorList.put(resourceLocation,ColorPalette.DEFAULT);
        noTaggedColors.add(resourceLocation);
        return ColorPalette.DEFAULT;
    }

    @Override
    public void tick() {
        for (int i=0;i<colorPalettes.size();i++) {
            colorPalettes.get(i).tick();
        }
    }


    
    // Abstraction of AbstractTexture
    public void setFilter(boolean blur, boolean mipmap) {}

    public int getId() {
        return 0;
    }

    public void releaseId() {}

    public void load(ResourceManager resourceManager) {}

    public void bind() {}
}
