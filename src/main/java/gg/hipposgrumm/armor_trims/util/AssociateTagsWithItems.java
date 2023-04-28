package gg.hipposgrumm.armor_trims.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.*;

public class AssociateTagsWithItems {
    private Item[] item;

    public AssociateTagsWithItems(String tag) {
        tag = tag.replace("#", "");
        TagKey<Item> itemTag = ItemTags.create(new ResourceLocation(tag));
        Item[] items = ezIteratorToListForItems(ForgeRegistries.ITEMS.tags().getTag(itemTag).iterator());
        items = alphabetize(items);
        item = items;
    }

    private static Item[] alphabetize(Item[] items) {
        String[] itemNames = new String[0];
        for (Item item:items) {
            itemNames = ArrayUtils.add(itemNames, item.getRegistryName().toString());
        }
        Collections.sort(Lists.newArrayList(itemNames));
        Item[] sortedItems = new Item[0];
        for (String itemName : itemNames) {
            sortedItems = ArrayUtils.add(sortedItems, ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)));
        }
        return sortedItems;
    }

    public Item[] getItems() {
        return item;
    }

    public static Item[] ezIteratorToListForItems(Iterator<Item> iterator) {
        Item[] objects = Iterators.toArray(iterator, Item.class);
        if (objects.length > 0) {
            return objects;
        }
        return new Item[]{};
    }
}
