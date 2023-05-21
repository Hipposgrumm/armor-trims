package gg.hipposgrumm.armor_trims.trimming;

import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public interface TrimmableItem {
    String TAG_TRIM_MATERIAL = "material";
    String TAG_TRIM_PATTERN = "pattern";
    String TAG_TRIM = "trim";

    static boolean isTrimmed(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        return compoundtag != null && compoundtag.contains(TAG_TRIM_PATTERN);
    }

    static int getMaterialColor(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        return compoundtag != null && compoundtag.contains(TAG_TRIM_MATERIAL) ? new GetAvgColor(new ResourceLocation(compoundtag.getString(TAG_TRIM_MATERIAL))).getColor() : 0;
    }

    static ResourceLocation getMaterial(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        return compoundtag != null && compoundtag.contains(TAG_TRIM_MATERIAL) ? new ResourceLocation(compoundtag.getString(TAG_TRIM_MATERIAL)) : null;
    }

    static ResourceLocation getTrim(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        return compoundtag != null && compoundtag.contains(TAG_TRIM_PATTERN) ?  compoundtag.getString(TAG_TRIM_PATTERN).split(":").length>1?new ResourceLocation(compoundtag.getString(TAG_TRIM_PATTERN)):new ResourceLocation("armor_trims", compoundtag.getString(TAG_TRIM_PATTERN)) : new ResourceLocation("armor_trim:empty");
    }

    static void clearTrim(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        if (compoundtag != null && compoundtag.contains(TAG_TRIM_PATTERN)) {
            compoundtag.remove(TAG_TRIM_PATTERN);
        }
    }

    static void setMaterial(ItemStack targetItem, ItemStack material) {
        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_MATERIAL, ForgeRegistries.ITEMS.getKey(material.getItem()).toString());
    }
    static void setTrim(ItemStack targetItem, Trims trim) {
        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_PATTERN, trim.name.toString());
    }

    static ItemStack applyTrim(ItemStack targetItem, Trims trim, ItemStack material) {
        return applyTrim(targetItem, trim, material, false);
    }

    static ItemStack applyTrim(ItemStack targetItem, Trims trim, ItemStack material, boolean internal) {
        ItemStack armorItem;
        Item item = targetItem.getItem();
        if ((internal || LargeItemLists.getAllTrimmable().contains(item)) && trim != null) {
            armorItem = targetItem.copy();
            armorItem.setCount(1);

            if (isTrimmed(armorItem) && !internal) {
                return ItemStack.EMPTY;
            } else {
                setTrim(armorItem, trim);
                setMaterial(armorItem, material);
                return armorItem;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }
}