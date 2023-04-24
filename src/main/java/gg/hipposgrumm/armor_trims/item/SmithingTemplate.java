package gg.hipposgrumm.armor_trims.item;

import com.google.common.collect.Iterators;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.AssociateTagsWithItems;
import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.ArrayUtils;
import oshi.util.tuples.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SmithingTemplate extends Item {
    private Trims trim;

    public Trims getTrim() {
        return this.trim;
    }

    public SmithingTemplate(Trims trim, Item.Properties properties) {
        super(properties.tab(CreativeModeTab.TAB_MATERIALS).stacksTo(1));
        this.trim = trim;
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return new TranslatableComponent("item.armor_trims.smithing_template");
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack item) {
        return new ItemStack(this);
    }

    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(new TranslatableComponent("trims.armor_trims."+trim.getId()).withStyle(ChatFormatting.DARK_GRAY));
        list.add(new TextComponent(""));
        list.add(new TranslatableComponent("tooltip.armor_trims.applyTo").withStyle(ChatFormatting.GRAY));
        if (trim.getId().equals("netherite_upgrade")) {
            TranslatableComponent output = new TranslatableComponent("tooltip.armor_trims.applyTo.diamond_equipment");
            output.withStyle(output.getStyle().withColor(getIngredientColor(Items.DIAMOND.getRegistryName())));
            list.add(new TextComponent(" ").append(output));
        } else {
            list.add(new TextComponent(" ").append(new TranslatableComponent("tooltip.armor_trims.applyTo.armor")).withStyle(ChatFormatting.BLUE));
        }
        list.add(new TranslatableComponent("tooltip.armor_trims.ingredients").withStyle(ChatFormatting.GRAY));
        if (trim.getId().equals("netherite_upgrade")) {
            TranslatableComponent output = new TranslatableComponent("item.minecraft.netherite_ingot");
            output.withStyle(output.getStyle().withColor(getIngredientColor(Items.NETHERITE_INGOT.getRegistryName())));
            list.add(new TextComponent(" ").append(output.withStyle(ChatFormatting.ITALIC)));
        } else {
            List<String> configList = removeUnregisteredEntries(Config.compressItemNamesInTemplateTooltip()?Config.trimmableMaterials():removeUnregisteredAndDuplicateEntries(LargeItemLists.getAllMaterials()));
            MutableComponent coloredList = createColoredList(configList);
            if (coloredList==null) {
                    list.add(new TextComponent(" ").append(new TranslatableComponent("tooltip.armor_trims.ingredients.empty").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
            } else {
                if (configList.size() > 4) {
                    if (Screen.hasShiftDown()) {
                        list.add(new TextComponent(" ").append(coloredList));
                    } else {
                        list.add(new TextComponent(" ").append(new TranslatableComponent("tooltip.armor_trims.ingredients.show_more").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)));
                    }
                }
            }
            /*
            int configListSize = configList.size();
            MutableComponent ingredientList = new TextComponent(" ");
            if (configListSize > 4) {
                if (Screen.hasShiftDown()) {
                    for (int i = 0; i < configList.size() - 2; i++) {
                        ingredientList = ingredientList.append(getIngredientNameFromListOf(i,configList)).append(new TextComponent(", "));
                    }
                    ingredientList = ingredientList.append(getIngredientNameFromListOf(configList.size() - 2,configList)).append(new TextComponent(", & "));
                    ingredientList = ingredientList.append(getIngredientNameFromListOf(configList.size() - 1,configList));
                    list.add(ingredientList);
                } else {
                    list.add(new TextComponent(" ").append(new TranslatableComponent("tooltip.armor_trims.ingredients.show_more").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)));
                }
            } else if (configListSize == 2) {
                ingredientList = ingredientList.append(getIngredientNameFromListOf(0,configList)).append(new TextComponent(" & "));
                ingredientList = ingredientList.append(getIngredientNameFromListOf(1,configList));
                list.add(ingredientList);
            } else if (configListSize == 1) {
                ingredientList = ingredientList.append(getIngredientNameFromListOf(0, configList));
                list.add(ingredientList);
            } else if (configListSize == 0) {
                list.add(new TranslatableComponent("tooltip.armor_trims.ingredients.empty").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
            } else {
                for (int i = 0; i < configList.size() - 2; i++) {
                    ingredientList = ingredientList.append(getIngredientNameFromListOf(i,configList)).append(new TextComponent(", "));
                }
                ingredientList = ingredientList.append(getIngredientNameFromListOf(configList.size() - 2,configList)).append(new TextComponent(", & "));
                ingredientList = ingredientList.append(getIngredientNameFromListOf(configList.size() - 1,configList));
                list.add(ingredientList);
            }*/
        }
    }

    private List<? extends String> removeUnregisteredAndDuplicateEntries(List<Item> list) {
        List<String> items = new ArrayList<String>();
        for (Item item:list) {
            if (ForgeRegistries.ITEMS.containsKey(item.getRegistryName()) && !items.contains(item.getRegistryName().toString())) {
                items.add(item.getRegistryName().toString());
            }
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    private List<String> removeUnregisteredEntries(List<? extends String> entryList) {
        List<String> list = (List<String>) entryList;
        List<String> itemList = new ArrayList<String>();
        for (String item:list) {
            if (item.startsWith("#")) { // Remove empty tags.
                Item[] items = new AssociateTagsWithItems(item.replace("#","")).getItems();
                if (items.length==0) continue;
                if (items.length==1) {
                    itemList.add(items[0].getRegistryName().toString());
                } else {
                    itemList.add(item);
                }
            } else { // Remove items not registered in ForgeRegistries.
                if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(item))) continue;
                itemList.add(item);
            }
        }
        return itemList;
    }

    public static MutableComponent createColoredList(List<String> list) {
        if (list.size() >= 3) {
            MutableComponent item = new TextComponent("");
            for (int i = 0; i < list.size() - 2; i++) {
                item.append(colorAndNameIngredient(i, list)).append(new TextComponent(", "));
            }
            return item.append(colorAndNameIngredient(list.size() - 2, list))
                    .append(new TextComponent(", & ")).append(colorAndNameIngredient(list.size() - 1, list));
        } else if (list.size() == 2) {
            MutableComponent item = new TextComponent("");
            return item.append(colorAndNameIngredient(0, list))
                    .append(" & ").append(colorAndNameIngredient(1, list));
        } else if (list.size() == 1) {
            return (MutableComponent) colorAndNameIngredient(0, list);
        } else {
            return null;
        }
    }

    private static Component colorAndNameIngredient(int index, List<String> list) {
        boolean isFromTag = list.get(index).startsWith("#");
        Item item;
        item = isFromTag ? new AssociateTagsWithItems(list.get(index)).getItems()[0] : ForgeRegistries.ITEMS.getValue(Objects.equals(ResourceLocation.tryParse(list.get(index)), null) ?new ResourceLocation("minecraft:air"):new ResourceLocation(list.get(index)));
        MutableComponent output = new TranslatableComponent(item.getDescriptionId());
        if (list.get(index).startsWith("#")) output.withStyle(ChatFormatting.ITALIC);
        return output.withStyle(output.getStyle().withColor(getIngredientColor(item.getRegistryName())));
    }

    public static TextColor getIngredientColor(ResourceLocation item) {
        return TextColor.fromRgb(new GetAvgColor(item).getColor());
    }
}
