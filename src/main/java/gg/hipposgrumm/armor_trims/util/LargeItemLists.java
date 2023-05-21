package gg.hipposgrumm.armor_trims.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate$Upgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class LargeItemLists {
    public static List<Class<? extends Item>> allTrimmableClasses = new ArrayList<>(){{add(ArmorItem.class);}};
    public static List<TagKey<Item>> allTrimmableTags = new ArrayList<>();
    private static List<Item> smithingTemplates = List.of();
    private static List<Item> smithingTemplatesUpgrades = List.of();
    private static List<Item> smithingTemplatesTrims = List.of();

    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        for (Item item : ImmutableList.copyOf(ForgeRegistries.ITEMS.iterator())) {
            items.add(item);
        }
        return items;
    }

    public static void setAllTemplates() {
        smithingTemplates = getAllItemsOfType(SmithingTemplate.class);
    }

    public static void setAllUpgradeTemplates() {
        smithingTemplatesUpgrades = getAllItemsOfType(SmithingTemplate$Upgrade.class);
    }

    public static void setAllTrimTemplates() {
        smithingTemplatesTrims = smithingTemplates.stream().filter(f -> !smithingTemplatesUpgrades.contains(f)).toList();
    }

    /**
     * @deprecated Use {@link #getAllTrimmable()}
     */
    @Deprecated
    public static List<Item> getAllArmors() {
        return getAllItemsOfType(ArmorItem.class);
    }

    public static List<Item> getAllTrimmable() {
        List<Item> items = new ArrayList<>();
        for (Class<? extends Item> item:allTrimmableClasses) {
            items.addAll(getAllItemsOfType(item));
        }
        for (TagKey<Item> item:allTrimmableTags) {
            items.addAll(List.of(new AssociateTagsWithItems(item.location().toString()).getItems()));
        }
        return items;
    }

    public static List<Item> getAllSmithingTemplates() {
        return smithingTemplates;
    }

    public static List<Item> getUpgradeSmithingTemplates() {
        return smithingTemplatesUpgrades;
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
