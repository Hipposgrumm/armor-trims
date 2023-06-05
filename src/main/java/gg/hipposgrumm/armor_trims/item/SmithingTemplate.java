package gg.hipposgrumm.armor_trims.item;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.AssociateTagsWithItems;
import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SmithingTemplate extends Item {
    private final Trims trim;
    private final String translatableName;
    private final List<String> trimmableItemNames;

    public Trims getTrim() {
        return this.trim;
    }

    protected SmithingTemplate(Item.Properties properties) {
        super(properties.stacksTo(1));
        this.trim = null;
        this.translatableName = "";
        this.trimmableItemNames = List.of();
    }

    public SmithingTemplate(ResourceLocation trim, String translatableName, Item.Properties properties, List<String> trimmableItemNames) {
        super(properties.tab(CreativeModeTab.TAB_MATERIALS).stacksTo(1));
        this.translatableName = translatableName;
        this.trim = new Trims(trim);
        this.trimmableItemNames = trimmableItemNames;
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.translatable("item.armor_trims.smithing_template");
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack item) {
        return new ItemStack(this);
    }

    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        if (trim != null) {
            list.add(Component.translatable(translatableName).withStyle(ChatFormatting.DARK_GRAY));
            list.add(Component.literal(""));
            list.add(Component.translatable("tooltip.armor_trims.applyTo").withStyle(ChatFormatting.GRAY));
            MutableComponent appliables = Component.empty();
            for (int i=0;i<trimmableItemNames.size();i++) {
                appliables.append(Component.translatable(trimmableItemNames.get(i)).append(Component.literal((i>=trimmableItemNames.size()-2)?(i>=trimmableItemNames.size()-1)?"":(trimmableItemNames.size()>2?", & ":" & "):", ")));
            }
            list.add(Component.literal(" ").append(appliables).withStyle(ChatFormatting.BLUE));
            list.add(Component.translatable("tooltip.armor_trims.ingredients").withStyle(ChatFormatting.GRAY));

            List<String> configList = removeUnregisteredEntries(Config.compressItemNamesInTemplateTooltip() ? Config.trimmableMaterials() : removeUnregisteredAndDuplicateEntries(LargeItemLists.getAllMaterials()));
            MutableComponent coloredList = createColoredList(configList);
            if (coloredList == null) {
                list.add(Component.literal(" ").append(Component.translatable("tooltip.armor_trims.ingredients.empty").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
            } else {
                if (!(configList.size() > 4) || Screen.hasShiftDown()) {
                    list.add(Component.literal(" ").append(coloredList));
                } else {
                    list.add(Component.literal(" ").append(Component.translatable("tooltip.armor_trims.ingredients.show_more").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)));
                }
            }
        }
    }

    protected List<? extends String> removeUnregisteredAndDuplicateEntries(List<Item> list) {
        List<Item> items = new ArrayList<>();
        for (Item item:list) {
            if (ForgeRegistries.ITEMS.containsValue(item) && !items.contains(item)) {
                items.add(item);
            }
        }
        return items.stream().map(f -> ForgeRegistries.ITEMS.getKey(f).toString()).toList();
    }

    @SuppressWarnings("unchecked")
    protected List<String> removeUnregisteredEntries(List<? extends String> entryList) {
        List<String> list = (List<String>) entryList;
        List<String> itemList = new ArrayList<>();
        for (String item:list) {
            if (item.startsWith("#")) { // Remove empty tags.
                Item[] items = new AssociateTagsWithItems(item.replace("#","")).getItems();
                if (items.length==0) continue;
                if (items.length==1) {
                    itemList.add(ForgeRegistries.ITEMS.getKey(items[0]).toString());
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
            MutableComponent item = Component.literal("");
            for (int i = 0; i < list.size() - 2; i++) {
                item.append(colorAndNameIngredient(i, list)).append(Component.literal(", "));
            }
            return item.append(colorAndNameIngredient(list.size() - 2, list))
                    .append(Component.literal(", & ")).append(colorAndNameIngredient(list.size() - 1, list));
        } else if (list.size() == 2) {
            MutableComponent item = Component.literal("");
            return item.append(colorAndNameIngredient(0, list))
                    .append(" & ").append(colorAndNameIngredient(1, list));
        } else if (list.size() == 1) {
            return (MutableComponent) colorAndNameIngredient(0, list);
        } else {
            return null;
        }
    }

    protected static Component colorAndNameIngredient(int index, List<String> list) {
        boolean isFromTag = list.get(index).startsWith("#");
        Item item;
        item = isFromTag ? new AssociateTagsWithItems(list.get(index)).getItems()[0] : ForgeRegistries.ITEMS.getValue(Objects.equals(ResourceLocation.tryParse(list.get(index)), null) ?new ResourceLocation("minecraft:air"):new ResourceLocation(list.get(index)));
        MutableComponent output = Component.translatable(item.getDescriptionId());
        if (list.get(index).startsWith("#")) output.withStyle(ChatFormatting.ITALIC);
        return output.withStyle(output.getStyle().withColor(getIngredientColor(item)));
    }

    public static TextColor getIngredientColor(Item item) {
        return TextColor.fromRgb(new GetAvgColor(ForgeRegistries.ITEMS.getKey(item)).getColor());
    }
}
