package gg.hipposgrumm.armor_trims.util;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.compat.jei.ArmortrimsRecipe;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LargeItemLists {
    private static List<Item> allArmors = List.of();
    private static List<Item> smithingTemplates = List.of();
    private static List<Item> smithingTemplatesTrims = List.of();

    private static List<Item> getAllItems() {
        Item[] items = new Item[0];
        for (Item item : ImmutableList.copyOf(ForgeRegistries.ITEMS.iterator())) {
            ArrayUtils.add(items, item);
        }
        return List.of(items);
    }

    public static void setAllArmors() {
        allArmors = getAllItemsOfType(ArmorItem.class);
    }

    public static void setAllTemplates() {
        smithingTemplates = getAllItemsOfType(SmithingTemplate.class);
    }

    public static void setAllTrimTemplates() {
        Item[] templates = new Item[0];
        for (Item item:smithingTemplates) {
            templates = ArrayUtils.add(templates, item);
        }
        templates = ArrayUtils.removeAllOccurrences(templates, Armortrims.NETHERITE_UPGRADE.get());
        smithingTemplatesTrims = List.of(templates);
    }

    public static List<Item> getAllArmors() {
        return allArmors;
    }

    public static List<Item> getSmithingTemplates() {
        return smithingTemplates;
    }

    public static List<Item> getTrimSmithingTemplates() {
        return smithingTemplatesTrims;
    }

    public static List<Item> getAllMaterials() {
        Item[] itemlist = new Item[0];
        for (String item : Config.trimmableMaterials()) {
            if (item.startsWith("#")) {
                Item[] itemTagged = new AssociateTagsWithItems(item.replace("#","")).getItems();
                itemlist = ArrayUtils.addAll(itemlist, itemTagged);
            } else {
                itemlist = ArrayUtils.add(itemlist, ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
            }
        }
        itemlist = ArrayUtils.removeAllOccurrences(itemlist, Items.AIR);
        return List.of(itemlist);
    }

    public static List<Item> getAllItemsOfType(Class<? extends Item> itemType) {
        Item[] items = new Item[0];
        for (Item item:getAllItems()) {
            if (itemType.isInstance(item)) items = ArrayUtils.add(items, item);
        }
        return List.of(items);
    }
}
