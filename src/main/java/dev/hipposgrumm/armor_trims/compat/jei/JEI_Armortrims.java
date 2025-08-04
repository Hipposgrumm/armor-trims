package dev.hipposgrumm.armor_trims.compat.jei;

//? if forge {
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.TrimRegistry;
import dev.hipposgrumm.armor_trims.api.item.ArmorTrimSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipe;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipeCategory;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.config.Config;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
//? >=1.17 {
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
//?}
//? if <1.19 {
/*import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.TranslatableComponent;
*///?}
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JeiPlugin
public class JEI_Armortrims implements IModPlugin {
    //? if >=1.17 {
    RecipeType<ArmortrimsRecipe> trimmingRecipeType = RecipeType.create(Armortrims.MODID, "armor_trimming", ArmortrimsRecipe.class);
    RecipeType<ArmortrimsRecipe> upgradeRecipeType = RecipeType.create(Armortrims.MODID, "item_upgrading", ArmortrimsRecipe.class);
    //?} else {
    /*ResourceLocation trimmingRecipeType = new ResourceLocation(Armortrims.MODID, "armor_trimming");
    ResourceLocation upgradeRecipeType = new ResourceLocation(Armortrims.MODID, "item_upgrading");
    *///?}

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Armortrims.MODID, "armortrims");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (Config.enableJei) {
            IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
            registration.addRecipeCategories(new ArmortrimsRecipeCategory(helper, new ResourceLocation(Armortrims.MODID, "armor_trimming"), /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("jei.armor_trimming"), TrimGetter.applyTrim(new ItemStack(Items.IRON_CHESTPLATE), TrimRegistry.defaultTrim(), new ItemStack(Items.EMERALD))) {
                //? if >=1.17 {
                private IRecipeLayoutBuilder builder;
                private ArmortrimsRecipe recipe;
                //?}

                @Override
                public void setRecipe(/*? if >=1.17 {*/IRecipeLayoutBuilder/*?} else {*//*IRecipeLayout*//*?}*/ builder, ArmortrimsRecipe recipe, /*? if >=1.17 {*/IFocusGroup/*?} else {*//*IIngredients*//*?}*/ focuses) {
                    //? if >=1.17 {
                    this.builder = builder;
                    this.recipe = recipe;
                    //?}
                    super.setRecipe(builder, recipe, focuses);

                    //? if <1.17 {
                    /*IFocus<ItemStack> item = builder.getFocus(VanillaTypes.ITEM);
                    if (item != null && item.getValue().getItem().is(Armortrims.TRIM_MATERIALS_TAG)) {
                        builder.getItemStacks().set(SmithingSlot.MATERIAL.index, item.getValue());
                        builder.getItemStacks().set(SmithingSlot.OUTPUT.index, TrimGetter.applyTrim(
                                recipe.baseInput().getItems()[0],
                                ((ArmorTrimSmithingTemplate) recipe.additionalInput().getItems()[0].getItem()).trim(),
                                item.getValue()
                        ));
                        return;
                    }
                    *///?}
                    ItemStack[] materials = recipe.materialInput().getItems();
                    List<ItemStack> items = new ArrayList<>(materials.length);
                    for (ItemStack material:materials) {
                        items.add(TrimGetter.applyTrim(
                                recipe.baseInput().getItems()[0],
                                ((ArmorTrimSmithingTemplate) recipe.additionalInput().getItems()[0].getItem()).trim(),
                                material
                        ));
                    }
                    //? if <1.17 {
                    /*builder.getItemStacks().setOverrideDisplayFocus(null);
                    builder.getItemStacks().set(SmithingSlot.OUTPUT.index, items);
                    *///?}
                }

                //? if >=1.17 {
                @Override
                protected void onSlotsCreated(IRecipeSlotBuilder base, IRecipeSlotBuilder additional, IRecipeSlotBuilder material, IRecipeSlotBuilder out) {
                    ItemStack[] materials = recipe.materialInput().getItems();
                    List<ItemStack> items = new ArrayList<>(materials.length);
                    for (ItemStack mat:materials) {
                        items.add(TrimGetter.applyTrim(
                                recipe.baseInput().getItems()[0],
                                ((ArmorTrimSmithingTemplate) recipe.additionalInput().getItems()[0].getItem()).trim(),
                                mat
                        ));
                    }
                    out.addItemStacks(items);
                    builder.createFocusLink(material,out);
                }
                //?}
            });
            registration.addRecipeCategories(new ArmortrimsRecipeCategory(helper, new ResourceLocation(Armortrims.MODID, "item_upgrading"), /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("jei.item_upgrading"), Items.NETHERITE_CHESTPLATE.getDefaultInstance()));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Config.enableJei) {
            registration.addRecipes(/*? if >=1.17 {*/trimmingRecipeType,/*?}*/ ItemTrimRecipeMaker.getTrimmingRecipes(registration.getIngredientManager()) /*? if <1.17 {*//*,trimmingRecipeType*//*?}*/);
            registration.addRecipes(/*? if >=1.17 {*/upgradeRecipeType,/*?}*/ ItemUpgradeRecipeMaker.getUpgradingRecipes(registration.getIngredientManager()) /*? if <1.17 {*//*,upgradeRecipeType*//*?}*/);
            if (Config.enableUntrimming)
                registration.addIngredientInfo(
                        Arrays.asList(Ingredient.of(ArmortrimsInternalUtils.SHEARS_TAG).getItems()),
                        VanillaTypes./*? if >=1.17 {*/ITEM_STACK/*?} else {*//*ITEM*//*?}*/,
                        /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("jei.armor_untrimming_notice")
                );
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (Config.enableJei) {
            registration.addRecipeCatalyst(Items.SMITHING_TABLE.getDefaultInstance(), trimmingRecipeType);
            registration.addRecipeCatalyst(Items.SMITHING_TABLE.getDefaultInstance(), upgradeRecipeType);
        }
    }
}
//?}