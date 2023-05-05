package gg.hipposgrumm.armor_trims.compat.jei;

import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@JeiPlugin
public class JEI_Armortrims implements IModPlugin {
    RecipeType trimmingRecipeType = RecipeType.create(Armortrims.MODID, "armor_trimming", IArmortrimsRecipe.class);
    RecipeType upgradeRecipeType = RecipeType.create(Armortrims.MODID, "item_upgrading", IArmortrimsRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Armortrims.MODID, "armortrims_jei");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item armorItem : LargeItemLists.getAllArmors()) {
            registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, armorItem, (itemstack, context) -> {
                List<String> trimNames = new ArrayList<>();
                for (Item template : LargeItemLists.getTrimSmithingTemplates()) {
                    for (Item material : LargeItemLists.getAllMaterials()) {
                        trimNames.add(TrimmableItem.applyTrim(new ItemStack(armorItem), ((SmithingTemplate) template).getTrim(), new ItemStack(material)).getTagElement(TrimmableItem.TAG_TRIM).toString());
                    }
                }
                return trimNames.toString();
            });
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ArmortrimsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ItemUpgradeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        //registration.getCraftingCategory().addCategoryExtension(UntrimmingSpecialRecipe.class, SpecialUntrimmingHelper::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(trimmingRecipeType, ArmortrimRecipeMaker.getTrimmingRecipes(new ArmortrimRecipeMaker.ArmortrimsRecipeFactory(), registration.getIngredientManager()));
        registration.addRecipes(upgradeRecipeType, ItemUpgradeRecipeMaker.getUpgradingRecipes(new ItemUpgradeRecipeMaker.ItemUpgradeRecipeFactory(), registration.getIngredientManager()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.SMITHING_TABLE), trimmingRecipeType);
        registration.addRecipeCatalyst(new ItemStack(Items.SMITHING_TABLE), upgradeRecipeType);
    }
}
