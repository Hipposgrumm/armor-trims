package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemUpgradeRecipeCategory implements IRecipeCategory<ArmortrimsRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Armortrims.MODID, "item_upgrading");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ItemUpgradeRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.NETHERITE_CHESTPLATE));
    }

    @Override
    @SuppressWarnings("removal")
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends ArmortrimsRecipe> getRecipeClass() {
        return ArmortrimsRecipe.class;
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<ArmortrimsRecipe> getRecipeType() {
        return new RecipeType<>(UID, ArmortrimsRecipe.class);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.item_upgrading");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ArmortrimsRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 36).addIngredients(Ingredient.of(recipe.getBaseInput()));
        builder.addSlot(RecipeIngredientRole.INPUT, 70, 36).addIngredients(Ingredient.of(recipe.getAdditionalInput()));
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 54).addIngredients(Ingredient.of(recipe.getMaterialInput()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 36).addItemStack(ItemUpgradeRecipeMaker.getUpgradedItem(recipe.getBaseInput(), recipe.getAdditionalInput(), recipe.getMaterialInput()));
    }
}