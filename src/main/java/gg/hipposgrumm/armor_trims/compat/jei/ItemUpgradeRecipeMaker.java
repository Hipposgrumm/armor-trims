package gg.hipposgrumm.armor_trims.compat.jei;

import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.AssociateTagsWithItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ItemUpgradeRecipeMaker {
    public static List<IArmortrimsRecipe> getUpgradingRecipes(IItemUpgradeRecipeFactory recipeFactory, IIngredientManager ingredientManager) {
        return getArmortrimRecipes(recipeFactory, ingredientManager).toList();
    }

    public static class ItemUpgradeRecipeFactory implements IItemUpgradeRecipeFactory {
        public ItemUpgradeRecipeFactory() {}

        @Override
        public ArmortrimsRecipe createUpgradingRecipe(ItemStack baseInputs, ItemStack additionalInputs, ItemStack materialInputs) {
            return new ArmortrimsRecipe(baseInputs, additionalInputs, materialInputs);
        }
    }


    public interface IItemUpgradeRecipeFactory {
        IArmortrimsRecipe createUpgradingRecipe(ItemStack baseInput, ItemStack additionalInputs, ItemStack materialInputs);
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IItemUpgradeRecipeFactory recipeFactory, IIngredientManager ingredientManager) {
        return ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .flatMap(ingredient -> getArmortrimRecipes(recipeFactory, ingredient));
    }

    private static Stream<IArmortrimsRecipe> getArmortrimRecipes(IItemUpgradeRecipeFactory recipeFactory, ItemStack upgradableItem) {
        List<IArmortrimsRecipe> recipes = new ArrayList<>();
        for (Item materialItem:new AssociateTagsWithItems(Tags.Items.INGOTS_NETHERITE.location().toString()).getItems()) {
            if ((upgradableItem.getItem() instanceof TieredItem || upgradableItem.getItem() instanceof ArmorItem)) {
                ItemStack item = getUpgradedItem(upgradableItem.copy(), new ItemStack(Armortrims.NETHERITE_UPGRADE.get()), materialItem.getDefaultInstance());
                if (!item.getItem().equals(upgradableItem.getItem())) recipes.add(recipeFactory.createUpgradingRecipe(upgradableItem, new ItemStack(Armortrims.NETHERITE_UPGRADE.get()), materialItem.getDefaultInstance()));
            }
        }
        return recipes.stream();
    }

    public static ItemStack getUpgradedItem(ItemStack tieredItem, ItemStack templateItem, ItemStack material) {
        ItemStack upgradableItem = tieredItem.copy();
        Container vanillaRecipeContainer = new SimpleContainer(2);
        vanillaRecipeContainer.setItem(0, tieredItem);
        vanillaRecipeContainer.setItem(1, material);
        List<UpgradeRecipe> list = Minecraft.getInstance().level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, vanillaRecipeContainer, Minecraft.getInstance().level);
        if (!list.isEmpty()) return list.get(0).assemble(vanillaRecipeContainer);
        return upgradableItem;
    }
}
