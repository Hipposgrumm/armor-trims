package gg.hipposgrumm.armor_trims.compat.jei.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Code <s>copied</s> stolen from MineColonies; Modified Slightly...
 */
public class RenderingHelper {
    /**
     * Render an entity on a GUI.
     * @param poseStack matrix
     * @param x horizontal center position
     * @param y vertical bottom position
     * @param yaw adjusts body rotation
     * @param pitch adjusts look rotation
     * @param entity the entity to render
     */
    public static void renderEntity(PoseStack poseStack, int x, int y, float yaw, float pitch, LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        float f = (float)Math.atan(yaw / 40.0F);
        float f1 = (float)Math.atan(pitch / 40.0F);
        poseStack.pushPose();
        poseStack.translate((float) x, (float) y, 1050.0F);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        poseStack.translate(0.0D, 0.0D, 1000.0D);
        final Quaternion pitchRotation = Vector3f.XP.rotationDegrees(pitch);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(pitchRotation);
        RenderSystem.applyModelViewMatrix();
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    /**
     * Enable scissor (clipping) to GUI region (prevent drawing outside).
     *
     * @param poseStack matrix
     * @param x left position
     * @param y top position
     * @param w width
     * @param h height
     */
    public static void scissor(final PoseStack poseStack, int x, int y, int w, int h) {
        final double scale = Minecraft.getInstance().getWindow().getGuiScale();
        final double[] xyzTranslation = getGLTranslation(poseStack, scale);
        x *= scale;
        y *= scale;
        w *= scale;
        h *= scale;
        final int scissorX = Math.round(Math.round(xyzTranslation[0] + x));
        final int scissorY = Math.round(Math.round(Minecraft.getInstance().getWindow().getScreenHeight() - y - h - xyzTranslation[1]));
        final int scissorW = Math.round(w);
        final int scissorH = Math.round(h);
        RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);
    }

    /**
     * Disable scissor.
     */
    public static void stopScissor() {
        RenderSystem.disableScissor();
    }

    private static double[] getGLTranslation(final PoseStack poseStack, final double scale) {
        final Matrix4f matrix = poseStack.last().pose();
        final FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        matrix.store(buf);
        // { x, y, z }
        return new double[] {
                buf.get(getIndexFloatBuffer(0,3)) * scale,
                buf.get(getIndexFloatBuffer(1, 3)) * scale,
                buf.get(getIndexFloatBuffer(2, 3)) * scale
        };
    }

    private static int getIndexFloatBuffer(final int x, final int y) {
        return y * 4 + x;
    }
}
