package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ArmortrimRecipeMaker {
    public static List<IArmortrimsRecipe> getTrimmingRecipes(IArmortrimsRecipeFactory recipeFactory, IIngredientManager ingredientManager) {
        return getArmortrimRecipes(recipeFactory, ingredientManager).toList();
    }

    public static class ArmortrimsRecipeFactory implements IArmortrimsRecipeFactory {
        public ArmortrimsRecipeFactory() {}

        @Override
        public ArmortrimsRecipe createTrimmingRecipe(ItemStack baseInputs, ItemStack additionalInputs, ItemStack materialInputs) {
            return new ArmortrimsRecipe(baseInputs, additionalInputs, materialInputs);
        }
    }


    public interface IArmortrimsRecipeFactory {
        IArmortrimsRecipe createTrimmingRecipe(ItemStack baseInput, ItemStack additionalInputs, ItemStack materialInputs);
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IArmortrimsRecipeFactory recipeFactory, IIngredientManager ingredientManager) {
        return ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .flatMap(ingredient -> getArmortrimRecipes(recipeFactory, ingredient));
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IArmortrimsRecipeFactory recipeFactory, ItemStack armorItem) {
        List<IArmortrimsRecipe> recipes = new ArrayList<>();
        LogUtils.getLogger().info(armorItem+", "+LargeItemLists.getTrimSmithingTemplates().size()+", "+LargeItemLists.getAllMaterials());
        for (Item templateItem:LargeItemLists.getTrimSmithingTemplates()) {
            for (Item materialItem:LargeItemLists.getAllMaterials()) {
                if (armorItem.getItem() instanceof ArmorItem) recipes.add(recipeFactory.createTrimmingRecipe(armorItem, templateItem.getDefaultInstance(), materialItem.getDefaultInstance()));
            }
        }
        return recipes.stream();
    }

    public static ItemStack getTrimmedItem(ItemStack armorItem, ItemStack templateItem, ItemStack material) {
        ItemStack trimmableItem = armorItem.copy();
        if (templateItem.getItem() instanceof SmithingTemplate template) {
            TrimmableItem.applyTrim(trimmableItem, template.getTrim(), material);
            return trimmableItem;
        }
        return trimmableItem;
    }
}
