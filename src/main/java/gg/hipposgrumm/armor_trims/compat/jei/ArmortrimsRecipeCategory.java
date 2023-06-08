package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jline.utils.Log;
import repack.joml.Quaternionf;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class ArmortrimsRecipeCategory implements IRecipeCategory<ArmortrimsRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Armortrims.MODID, "armor_trimming");
    public final static ResourceLocation TEXTURE = new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ArmortrimsRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 168, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, TrimmableItem.applyTrim(new ItemStack(Items.IRON_CHESTPLATE), new Trims(new ResourceLocation(Armortrims.MODID, "coast")), new ItemStack(Items.EMERALD)));
    }

    @Override
    public RecipeType<ArmortrimsRecipe> getRecipeType() {
        return new RecipeType<>(UID, ArmortrimsRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.armor_trimming");
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
        builder.addSlot(RecipeIngredientRole.INPUT, 70, 36).addIngredients(Ingredient.of(recipe.getAdditionalInput().toArray(new ItemStack[0])));
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 54).addIngredients(Ingredient.of(recipe.getMaterialInput()));

        List<ItemStack> focus = focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
                .map(f -> f.getTypedValue().getIngredient()).toList();

        focus = focus.stream().filter(t -> t.getItem() instanceof SmithingTemplate).toList();
        focus = focus.isEmpty()?recipe.getAdditionalInput():focus;

        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 36).addItemStacks(focus.stream().map(i -> TrimmableItem.applyTrim(recipe.getBaseInput(), ((SmithingTemplate) i.getItem()).getTrim(), recipe.getMaterialInput())).toList());
    }

    private LivingEntity preview;

    /*
    @Override
    public void draw(ArmortrimsRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        //renderEntityInInventory(145, 75, 30, 50, -50, preview);
        spawnPreview();
        if (preview != null) renderEntity(poseStack, 145, 75, 30, 50, -50, preview);
    }
    */

    private void spawnPreview() {
        if (Minecraft.getInstance().level == null) return;
        preview = new ArmorStand(Minecraft.getInstance().level, 0, 0, 0);
        preview.setNoGravity(true);
        CompoundTag extraNBT = new CompoundTag();
        extraNBT.putBoolean("ShowArms", true);
        extraNBT.putBoolean("NoBasePlate", true);
        preview.load(extraNBT);
    }

    /**
     * @author <a href="https://github.com/Mrbysco/JustEnoughProfessions/blob/multi/1.19/Common/src/main/java/com/mrbysco/justenoughprofessions/RenderHelper.java">https://github.com/Mrbysco/JustEnoughProfessions/blob/multi/1.19.3/Common/src/main/java/com/mrbysco/justenoughprofessions/RenderHelper.java</a>
     */
    public static void renderEntity(PoseStack poseStack, int x, int y, double scale, double yaw, double pitch, LivingEntity livingEntity) {
        if (livingEntity.level == null) livingEntity.level = Minecraft.getInstance().level;
        poseStack.pushPose();
        poseStack.translate((float) x, (float) y, 50f);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        // Rotate entity
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(((float) Math.atan((-40 / 40.0F))) * 10.0F));

        livingEntity.yBodyRot = (float) -(yaw / 40.F) * 20.0F;
        livingEntity.setYRot((float) -(yaw / 40.F) * 20.0F);
        livingEntity.setYRot((float) -(pitch / 40.F) * 20.0F);
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();

        poseStack.translate(0.0F, livingEntity.getMyRidingOffset(), 0.0F);
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.overrideCameraOrientation(Quaternion.ONE);
        entityRenderDispatcher.setRenderShadow(false);
        final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, bufferSource, 15728880);
        });
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        poseStack.popPose();
    }
}