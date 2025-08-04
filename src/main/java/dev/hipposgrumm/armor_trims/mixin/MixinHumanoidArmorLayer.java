package dev.hipposgrumm.armor_trims.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hipposgrumm.armor_trims.api.TrimRegistry;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.TrimPattern;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.ArmorTrimPattern;
import dev.hipposgrumm.armor_trims.util.TrimTextureManager;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public MixinHumanoidArmorLayer(RenderLayerParent<T, M> layer) {
        super(layer);
    }

    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    private void armor_trims$applyTrimOverlay(PoseStack poseStack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int i, A model, CallbackInfo ci) {
        ItemStack stack = entity.getItemBySlot(slot);
        // Check for trim.
        if (!stack.isEmpty() && TrimGetter.isTrimmed(stack)) {
            // Make sure it should show.
            if (slot == EquipmentSlot.HEAD || stack.getItem() instanceof ArmorItem) {
                TrimPattern pattern = TrimRegistry.getTrim(TrimGetter.getPattern(stack));
                // Make sure the trim is an armor trim.
                if (pattern instanceof ArmorTrimPattern) {
                    ArmorTrimPattern armorPattern = (ArmorTrimPattern) pattern;
                    
                    // Define variables.
                    ResourceLocation material = TrimGetter.getMaterial(stack);
                    ResourceLocation location = slot == EquipmentSlot.LEGS ? armorPattern.getLayer1() : armorPattern.getLayer0();
                    ResourceLocation coloredLocation = TrimTextureManager.get(location, material);
                    if (coloredLocation == null) return;
                    VertexConsumer shader = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(coloredLocation), false, stack.hasFoil());

                    // TrimTextureManager returns the initial location if there is no palette variation for that pattern and material.
                    if (coloredLocation == location) {
                        // Tint in real time.
                        ColorPalette palette = ColorPaletteManager.get(material);
                        if (palette != null) {
                            int color = palette.get(ColorPalette.PALETTE_COLORS[0]);
                            model.renderToBuffer(poseStack, shader, i, OverlayTexture.NO_OVERLAY, (color >> 16 & 0xFF)/255f, (color >> 8 & 0xFF)/255f, (color & 0xFF)/255f, (color >> 24 & 0xFF)/255f); // Extract R, G, B, A from color.
                            return;
                        }
                    }
                    // Render pre-colored.
                    model.renderToBuffer(poseStack, shader, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }
}
