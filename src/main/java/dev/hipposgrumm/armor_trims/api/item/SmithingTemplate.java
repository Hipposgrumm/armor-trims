package dev.hipposgrumm.armor_trims.api.item;

import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Supplier;

//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
//?}

/**
 * Smithing template for use in the Smithing Table GUI
 * @see dev.hipposgrumm.armor_trims.api.base.SmithingTemplateItems Example
 */
public abstract class SmithingTemplate extends Item {
    protected final Supplier<Ingredient> materials;
    protected final ResourceLocation trimIdentifier;
    protected final Component applyTo;

    /**
     * Smithing Template
     * @param trimIdentifier - A registered Trim Pattern associated with this template. Doesn't have to be an armor trim pattern ({@see ItemUpgradePattern}) and is used for translation.
     * @param materials      - Materials used by the template - passed as a supplier and accessed during runtime.
     * @param applyTo        - Component following "Apply To" in tooltip
     * @param properties     - Item Properties
     */
    public SmithingTemplate(ResourceLocation trimIdentifier, Supplier<Ingredient> materials, Component applyTo, Item.Properties properties) {
        super(properties);
        this.materials = materials;
        this.trimIdentifier = trimIdentifier;
        this.applyTo = applyTo;
    }

    /**
     * Result of the recipe.
     * @param baseItem     - Base Item (eg Armor)
     * @param templateItem - Smithing Template (this)
     * @param materialItem - Material (trim material)
     * @param level        - World (used to access recipe data and such)
     * @return Resulting Item
     */
    public abstract ItemStack getResult(ItemStack baseItem, ItemStack templateItem, ItemStack materialItem, Level level);

    //? if forge {
    /// @apiNote Override {@link #getRecipeRemainder} instead.
    @ApiStatus.Internal
    public final ItemStack getContainerItem(ItemStack item) {
        return getRecipeRemainder(item);
    }

    ///  @apiNote Override {@link #hasCraftingRemainingItem()} instead.
    @ApiStatus.Internal
    public final boolean hasContainerItem(ItemStack stack) {
        return hasCraftingRemainingItem();
    }
    //?}

    public ItemStack getRecipeRemainder(ItemStack item) {
        return item;
    }

    public boolean hasCraftingRemainingItem() {
        return true;
    }

    public Ingredient materials() {
        return materials.get();
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("item.armor_trims.smithing_template");
    }

    /// This is where the meat of the code is. It's the trim information of the item. You can change it if you're willing to read it.
    public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);

        if (Minecraft.getInstance().level == null) return;

        // Name of Smithing Template
        if (trimIdentifier != null) list.add(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("trims." + trimIdentifier.toString().replace(':', '.')).withStyle(ChatFormatting.DARK_GRAY));

        // Application
        list.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/("").withStyle(ChatFormatting.GRAY));
        list.add(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.applyTo").withStyle(ChatFormatting.GRAY));
        list.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(applyTo));

        // Ingredients
        list.add(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.ingredients").withStyle(ChatFormatting.GRAY));
        ItemStack[] materialItems = materials().getItems();
        if (materialItems.length > 0) {
            if (materialItems.length <= 4 || Screen.hasShiftDown()) { // If fits or shifts.
                int firstIndex = list.size();
                list.addAll(createColoredList(materialItems, flag));
                list.set(firstIndex, /*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(list.get(firstIndex)));
            } else { // Require shift.
                list.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.ingredients.show_more").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)));
            }
        } else { // If none.
            list.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.ingredients.empty").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
        }
    }

    /// This creates all the items in a list by their color. You can change it if you're willing to read it.
    protected List<MutableComponent> createColoredList(ItemStack[] list, TooltipFlag flag) {
        if (list.length >= 3) {
            List<MutableComponent> itemlist = new ArrayList<>();
            MutableComponent item = /*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ");
            for (int i = 0; i < list.length; i++) {
                Component coloredIngredient = colorAndNameIngredient(list[i]);
                if (i == list.length - 1) {
                    item.append(coloredIngredient);
                } else if (i == list.length - 2) {
                    item.append(coloredIngredient).append(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(", & "));
                } else {
                    item.append(coloredIngredient).append(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(", "));
                }
                //? if fabric {
                /*// This is only needed on Fabric because the UI doesn't wraparound like on Forge.
                if (item.getString().length()>=(flag.isAdvanced()?Math.max(30,Registry.ITEM.getKey(this).toString().length()):30)) {
                    itemlist.add(item);
                    item = /^? if >=1.19 {^//^Component.literal^//^?} else {^/new TextComponent/^?}^/("");
                }
                *///?}
            }
            itemlist.add(item);
            itemlist.removeIf(c -> c.getString().equals(" "));
            return itemlist;
        } else if (list.length == 2) {
            MutableComponent item = /*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ");
            return Collections.singletonList(item.append(colorAndNameIngredient(list[0]))
                    .append(" & ").append(colorAndNameIngredient(list[1])));
        } else if (list.length == 1) {
            return Collections.singletonList(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/("").append(colorAndNameIngredient(list[0])));
        } else {
            return null;
        }
    }

    /// Create a component from an itemstack. You can change it if you're willing to read it.
    protected static Component colorAndNameIngredient(ItemStack item) {
        MutableComponent output = /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/(item.getDescriptionId());
        //? if forge {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(item.getItem());
        //?} else {
        /*ResourceLocation name = Registry.ITEM.getKey(item.getItem());
        *///?}
        ColorPalette color = ColorPaletteManager.get(name);
        if (color == null) color = ColorPalette.DEFAULT; // Yes this can happen for some reason.
        return output.withStyle(output.getStyle().withColor(color.textColor()));
    }
}
