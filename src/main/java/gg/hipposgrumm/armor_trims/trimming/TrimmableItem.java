package gg.hipposgrumm.armor_trims.trimming;

import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

    static String getTrim(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        return compoundtag != null && compoundtag.contains(TAG_TRIM_PATTERN) ? compoundtag.getString(TAG_TRIM_PATTERN) : "empty";
    }

    static void clearTrim(ItemStack targetItem) {
        CompoundTag compoundtag = targetItem.getTagElement(TAG_TRIM);
        if (compoundtag != null && compoundtag.contains(TAG_TRIM_PATTERN)) {
            compoundtag.remove(TAG_TRIM_PATTERN);
        }
    }

    static void setMaterial(ItemStack targetItem, ItemStack material) {
        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_MATERIAL, material.getItem().getRegistryName().toString());
    }
    static void setTrim(ItemStack targetItem, Trims trim) {
        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_PATTERN, trim.getId());
    }

    static ItemStack applyTrim(ItemStack targetItem, Trims trim, ItemStack material) {
        return applyTrim(targetItem, trim, material, false);
    }

    static ItemStack applyTrim(ItemStack targetItem, Trims trim, ItemStack material, boolean internal) {
        ItemStack armorItem;
        Item item = targetItem.getItem();
        if (internal || item instanceof ArmorItem) {
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