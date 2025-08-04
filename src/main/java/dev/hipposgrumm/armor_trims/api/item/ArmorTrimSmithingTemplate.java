package dev.hipposgrumm.armor_trims.api.item;

import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.ArmorTrimPattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TranslatableComponent;*/
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * Smithing Template designed for Armor Trims
 * Use this when making an Item Trim
 * @see dev.hipposgrumm.armor_trims.api.base.SmithingTemplateItems Example
 */
public class ArmorTrimSmithingTemplate extends SmithingTemplate {
    private final ArmorTrimPattern trim;

    /**
     * Armor Trim Smithing Template
     * @param trim       - A registered Trim Pattern associated with this template. Doesn't have to be an armor trim pattern ({@see ItemUpgradePattern})
     * @param materials  - Materials used by the template - passed as a supplier and accessed during runtime.
     * @param properties - Item Properties
     */
    public ArmorTrimSmithingTemplate(ArmorTrimPattern trim, Supplier<Ingredient> materials, Properties properties) {
        this(trim, materials, /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.applyTo.armor").withStyle(ChatFormatting.BLUE), properties);
    }

    /**
     * Can be overridden to allow changing of the component.
     * @param applyTo - Component following "Apply To" in tooltip
     */
    protected ArmorTrimSmithingTemplate(ArmorTrimPattern trim, Supplier<Ingredient> materials, Component applyTo, Properties properties) {
        super(trim.getId(), materials, applyTo, properties);
        this.trim = trim;
    }

    /// Public overridable method for getting the trim
    public ArmorTrimPattern trim() {
        return trim;
    }

    /**
     * Result of the recipe.
     * @param baseItem     - Base Item (eg Armor)
     * @param templateItem - Smithing Template (this)
     * @param materialItem - Material (trim material)
     * @param level        - World (used to access recipe data and such)
     * @return Resulting Item
     */
    public ItemStack getResult(ItemStack baseItem, ItemStack templateItem, ItemStack materialItem, Level level) {
        if (baseItem/*? if <1.18 {*//*.getItem()*//*?}*/.is(Armortrims.NON_TRIMMABLE_ITEMS_TAG)) return ItemStack.EMPTY;

        if (materials().test(materialItem)) {
            return TrimGetter.applyTrim(baseItem, trim(), materialItem);
        } else {
            return ItemStack.EMPTY;
        }
    }
}
