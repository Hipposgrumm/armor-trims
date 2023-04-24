package gg.hipposgrumm.armor_trims.util;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LargeItemLists {
    private static List<Item> allArmors = List.of();
    private static List<Item> smithingTemplates = List.of();
    private static List<Item> smithingTemplatesTrims = List.of();

    public static void setAllArmors() {
        Item[] armors = new Item[0];
        for (Item item : ImmutableList.copyOf(ForgeRegistries.ITEMS.iterator())) {
            if (item instanceof ArmorItem) ArrayUtils.add(armors, item);
        }
        allArmors = List.of(armors);
    }

    public static void setAllTemplates() {
        Item[] templates = new Item[0];
        for (Item item : ImmutableList.copyOf(ForgeRegistries.ITEMS.iterator())) {
            if (item instanceof SmithingTemplate) ArrayUtils.add(templates, item);
        }
        smithingTemplates = List.of(templates);
    }

    public static void setAllTrimTemplates() {
        Item[] templates = new Item[0];
        for (Item item : ImmutableList.copyOf(ForgeRegistries.ITEMS.iterator())) {
            if (item instanceof SmithingTemplate && ((SmithingTemplate) item).getTrim()!= Trims.NETHERITE_UPGRADE) ArrayUtils.add(templates, item);
        }
        smithingTemplatesTrims = List.of(templates);
    }

    public static boolean isEntryRegistered(String item) {
        if (item.startsWith("#")) { // Is it a tag?
            return new AssociateTagsWithItems(item).getItems().length > 0;
        } else {
            return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(item));
        }
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
        return List.of(itemlist);
    }
}
