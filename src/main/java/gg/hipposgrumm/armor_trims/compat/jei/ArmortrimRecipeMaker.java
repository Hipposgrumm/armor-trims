package gg.hipposgrumm.armor_trims.compat.jei;

import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ArmortrimRecipeMaker {
    public static List<IArmortrimsRecipe> getTrimmingRecipes(IArmortrimsRecipeFactory recipeFactory, IIngredientManager ingredientManager) {
        return getArmortrimRecipes(recipeFactory, ingredientManager).toList();
    }

    public static class ArmortrimsRecipeFactory implements IArmortrimsRecipeFactory {
        public ArmortrimsRecipeFactory() {}

        @Override
        public ArmortrimsRecipe createTrimmingRecipe(ItemStack baseInputs, ItemStack additionalInputs, ItemStack materialInputs, ItemStack outputs) {
            return new ArmortrimsRecipe(baseInputs, additionalInputs, materialInputs, outputs);
        }
    }


    public interface IArmortrimsRecipeFactory {
        IArmortrimsRecipe createTrimmingRecipe(ItemStack baseInput, ItemStack additionalInputs, ItemStack materialInputs, ItemStack outputs);
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IArmortrimsRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        return ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .flatMap(ingredient -> getArmortrimRecipes(vanillaRecipeFactory, ingredient));
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IArmortrimsRecipeFactory recipeFactory, ItemStack armorItem) {
        List<IArmortrimsRecipe> recipes = new ArrayList<>();
        for (Item templateItem:LargeItemLists.getTrimSmithingTemplates()) {
            for (Item materialItem:LargeItemLists.getAllMaterials()) {
                recipes.add(recipeFactory.createTrimmingRecipe(armorItem, templateItem.getDefaultInstance(), materialItem.getDefaultInstance(), getTrimmedItem(armorItem, templateItem.getDefaultInstance(), materialItem.getDefaultInstance())));
            }
        }
        return recipes.stream();
    }

    private static ItemStack getTrimmedItem(ItemStack armorItem, ItemStack templateItem, ItemStack material) {
        ItemStack enchantedIngredient = armorItem.copy();
        if (templateItem.getItem() instanceof SmithingTemplate template) {
            TrimmableItem.applyTrim(enchantedIngredient, template.getTrim(), material);
            return enchantedIngredient;
        }
        return enchantedIngredient;
    }
}
