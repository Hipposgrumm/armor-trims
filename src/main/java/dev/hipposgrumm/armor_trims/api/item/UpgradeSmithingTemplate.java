package dev.hipposgrumm.armor_trims.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

/**
 * Smithing Template designed for Item Upgrading, recognized by JEI.
 * @see dev.hipposgrumm.armor_trims.api.base.SmithingTemplateItems Example
 */
public abstract class UpgradeSmithingTemplate extends SmithingTemplate {
    /**
     * Smithing Template
     * @param trimIdentifier - A registered Trim Pattern associated with this template. Doesn't have to be an armor trim pattern ({@see ItemUpgradePattern}) and is used for translation.
     * @param materials      - Materials used by the template - passed as a supplier and accessed during runtime.
     * @param applyTo        - Component following "Apply To" in tooltip
     * @param properties     - Item Properties
     */
    public UpgradeSmithingTemplate(ResourceLocation trimIdentifier, Supplier<Ingredient> materials, Component applyTo, Properties properties) {
        super(trimIdentifier, materials, applyTo, properties);
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
}
