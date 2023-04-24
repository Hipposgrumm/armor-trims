package gg.hipposgrumm.armor_trims.compat.jei;

import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public class JEI_Armortrims implements IModPlugin {
    RecipeType trimmingRecipeType = RecipeType.create(Armortrims.MODID, "armor_trimming", IArmortrimsRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Armortrims.MODID, "armortrims_jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ArmortrimsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(UntrimmingSpecialRecipe.class, SpecialUntrimmingHelper::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(trimmingRecipeType, ArmortrimRecipeMaker.getTrimmingRecipes(new ArmortrimRecipeMaker.ArmortrimsRecipeFactory(), registration.getIngredientManager()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.SMITHING_TABLE), trimmingRecipeType);
    }
}
