package dev.hipposgrumm.armor_trims.api.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.hipposgrumm.armor_trims.gui.SmithingScreenNew;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

//? if fabric {
/*//? if >=1.17 {
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.entry.EntryStack;
//?} else {
/^import static dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipeCategory.Slot;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.api.EntryStack;
^///?}
*///?} elif >=1.17 {
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
//?} else {
/*import mezz.jei.api.gui.ingredient.IGuiIngredient;
*///?}


public class ArmorstandWidget
        //? if fabric
        /*extends Widget*/
{
    protected final int x;
    protected final int y;
    protected final float scale;
    //? if fabric {
    /*protected Slot slot;
    *///?} elif >=1.17 {
    protected IRecipeSlotView slot;
    //?} else {
    /*protected IGuiIngredient<ItemStack> slot;
    *///?}

    protected final LivingEntity preview;

    //? if fabric {
    /*protected EntryStack/^? >=1.17 {^/<?>/^?}^/ last;
    *///?} else {
    protected ItemStack last;
    //?}

    public ArmorstandWidget(int x, int y, float scale, /*? if fabric {*//*Slot*//*?} elif >=1.17 {*/IRecipeSlotView/*?} else {*//*IGuiIngredient<ItemStack>*//*?}*/ slot) {
        //? if fabric
        /*super();*/
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.slot = slot;
        this.preview = SmithingScreenNew.createPreview();
    }

    public void update(
            /*? if fabric {*//*Slot
            *//*?} elif >=1.17 {*/IRecipeSlotView
            /*?} else {*//*IGuiIngredient<ItemStack>
            *//*?}*/ slot
    ) {
        this.slot = slot;
    }

    //? if fabric {
    /*@Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
    *///?}

    protected static float armorStandRotationX = (float) Math.atan(1.25F);
    protected static float armorStandRotationY = -armorStandRotationX;
    /**
     * Code derived from {@link net.minecraft.client.gui.screens.inventory.InventoryScreen#renderEntityInInventory(int, int, int, float, float, LivingEntity) InventoryScreen#renderEntityInInventory()}<br>
     * This version allows us to input our own <code>PoseStack</code>.
     */
    //? if fabric
    /*@Override*/
    @SuppressWarnings("deprecation")
    public void render(PoseStack jeiPoseStack/*? if fabric {*//*, int mouseX, int mouseY, float deltaTime*//*?}*/) {
        //? if fabric {
        /*if (slot != null) {
            EntryStack current = slot.getCurrentEntry();
            if (current != this.last) {
                this.last = current;
                // Only put the stack if it's an item.
                // Though it should always be an item anyway.
                if (last./^? >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/() instanceof ItemStack) {
                    SmithingScreenNew.updatePreview(this.preview, (ItemStack) last./^? >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/());
                } else {
                    SmithingScreenNew.updatePreview(this.preview, ItemStack.EMPTY);
                }
            }
        }
        *///?} else {
        if (slot != null) {
            //? if >=1.17 {
            ItemStack current = slot.getDisplayedItemStack().orElse(this.last);
            //?} else {
            /*ItemStack current = slot.getDisplayedIngredient();
            *///?}
            if (current != this.last) {
                this.last = current;
                SmithingScreenNew.updatePreview(this.preview, this.last);
            }
        }
        //?}

        // Positioning
        //? >=1.17 {
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.mulPoseMatrix(jeiPoseStack.last().pose());
        poseStack.translate(x, y, 1050.0);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        //?} else {
        /*RenderSystem.pushMatrix();
        RenderSystem.multMatrix(jeiPoseStack.last().pose());
        RenderSystem.translatef(x, y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        *///?}

        // Rotationing
        //? >=1.17
        RenderSystem.applyModelViewMatrix();
        PoseStack renderPose = new PoseStack();
        renderPose.translate(0.0, 0.0, 1000.0);
        renderPose.scale(scale, scale, scale);
        Quaternion rot180 = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion = Vector3f.XP.rotationDegrees(armorStandRotationY * 20.0F);
        rot180.mul(quaternion);
        renderPose.mulPose(rot180);

        // Setup Entity Rotation
        float yBodyRot = preview.yBodyRot;
        //? if >=1.17 {
        float yRot = preview.getYRot();
        float xRot = preview.getXRot();
        preview.setXRot(-armorStandRotationY * 20.0F);
        preview.setYRot(180.0F + armorStandRotationX * 40.0F);
        //?} else {
        /*float yRot = preview.yRot;
        float xRot = preview.xRot;
        preview.xRot = -armorStandRotationY * 20.0F;
        preview.yRot = 180.0F + armorStandRotationX * 40.0F;
        *///?}
        preview.yBodyRot = 180.0F + armorStandRotationX * 20.0F;

        // Setup Rendering and Render Entity
        //? if >=1.17
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion.conj();
        dispatcher.overrideCameraOrientation(quaternion);
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource shader = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> dispatcher.render(preview, 0.0, 0.0, 0.0, 0.0F, 1.0F, renderPose, shader, 0x00F000F0));
        shader.endBatch();
        dispatcher.setRenderShadow(true);

        // Reset entity.
        preview.yBodyRot = yBodyRot;
        //? if >=1.17 {
        preview.setYRot(yRot);
        preview.setXRot(xRot);
        //?} else {
        /*preview.yRot = yRot;
        preview.xRot = xRot;
        *///?}

        // Reset Other Rendering
        //? >=1.17 {
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        //?} else {
        /*RenderSystem.popMatrix();
        *///?}
    }
}