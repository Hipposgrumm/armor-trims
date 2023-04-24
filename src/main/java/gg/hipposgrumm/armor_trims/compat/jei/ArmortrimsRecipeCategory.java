package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.compat.jei.util.RenderingHelper;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.List;

public class ArmortrimsRecipeCategory implements IRecipeCategory<ArmortrimsRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Armortrims.MODID, "armor_trimming");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new.png");

    private final IDrawable background;
    private final IDrawable icon;

    private LivingEntity preview;

    public ArmortrimsRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, TrimmableItem.applyTrim(new ItemStack(Items.IRON_CHESTPLATE), Trims.COAST, new ItemStack(Items.EMERALD)));
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
    public RecipeType<ArmortrimsRecipe> getRecipeType() {
        return new RecipeType<>(UID, ArmortrimsRecipe.class);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.armor_trimming");
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
    public void draw(ArmortrimsRecipe recipe, IRecipeSlotsView slots, PoseStack poseStack, double mouseX, double mouseY) {
        spawnPreview();
        RenderingHelper.renderEntity(poseStack, 145, 75, 30, 50, preview); // TODO: Polish this.
    }

    private void spawnPreview() {
        if (Minecraft.getInstance().level == null) return;
        preview = new ArmorStand(Minecraft.getInstance().level, 0, 0, 0);
        preview.setNoGravity(true);
        CompoundTag extraNBT = new CompoundTag();
        extraNBT.putBoolean("ShowArms", true);
        extraNBT.putBoolean("NoBasePlate", true);
        preview.load(extraNBT);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ArmortrimsRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 40).addIngredients(Ingredient.of(recipe.getBaseInput()));
        builder.addSlot(RecipeIngredientRole.INPUT, 57, 18).addIngredients(Ingredient.of(recipe.getAdditionalInput()));
        builder.addSlot(RecipeIngredientRole.INPUT, 103, 18).addIngredients(Ingredient.of(recipe.getMaterialInput()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 60).addItemStack(recipe.getOutput());
    }
}