package dev.hipposgrumm.armor_trims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.OverlayRegistry;
import dev.hipposgrumm.armor_trims.api.trimming.ItemOverlay;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.model.ItemTrimModels;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = ItemRenderer.class, priority = 1005) // sodium uses overwrite mixins so I have to inject after
public class MixinItemRenderer {
    @Unique private float[] armor_trims$trimColor = null;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void armor_trims$armorDecoration(ItemRenderer instance, BakedModel model, ItemStack stack, int light, int overlay, PoseStack poseStack, VertexConsumer shader, Operation<Void> original, @Local(argsOnly = true) MultiBufferSource shaderSource) {
        original.call(instance, model, stack, light, overlay, poseStack, shader);

        if (Armortrims.trimTextures().ready() && TrimGetter.isTrimmed(stack)) {
            ItemOverlay itemOverlay = OverlayRegistry.fromItem(stack);
            ResourceLocation material = TrimGetter.getMaterial(stack);
            ColorPalette palette = ColorPaletteManager.get(material);
            Pair<BakedModel, Boolean> trim = ItemTrimModels.getModel(itemOverlay, palette);
            if (trim == null) return;

            shader = ItemRenderer.getFoilBufferDirect(shaderSource, Sheets.translucentCullBlockSheet(), true, stack.hasFoil());
            if (trim.getSecond()) {
                armor_trims$trimColor = new float[3];
                armor_trims$trimColor[0] = 1;
                armor_trims$trimColor[1] = 1;
                armor_trims$trimColor[2] = 1;
            } else {
                int color = palette.get(ColorPalette.PALETTE_COLORS[0]);
                armor_trims$trimColor = new float[3];
                armor_trims$trimColor[0] = (color >> 16 & 0xFF)/255F;
                armor_trims$trimColor[1] = (color >>  8 & 0xFF)/255F;
                armor_trims$trimColor[2] = (color       & 0xFF)/255F;
            }
            original.call(instance, trim.getFirst(), stack, light, overlay, poseStack, shader);
            armor_trims$trimColor = null;
        }
    }

    @WrapOperation(
            method = "renderQuadList",
            at = @At(
                    //? if forge && >=1.17
                    remap = false,
                    value = "INVOKE",
                    //? if forge {
                    //? if >=1.19 {
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFIIZ)V"
                    //?} elif >=1.17 {
                    /*target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFIIZ)V"
                    *///?} else {
                    /*target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;addVertexData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFIIZ)V"
                    *///?}
                    //?} else {
                    /*target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFII)V"
                    *///?}
            )
    )
    private void armor_trims$armorDecorationTint(VertexConsumer instance, PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue, /*? if forge && >=1.19 {*/float alpha,/*?}*/ int light, int overlay, /*? if forge {*/boolean readExistingColor,/*?}*/ Operation<Void> original) {
        if (armor_trims$trimColor != null) {
            red   = armor_trims$trimColor[0];
            green = armor_trims$trimColor[1];
            blue  = armor_trims$trimColor[2];
        }
        original.call(instance, pose, quad, red, green, blue, /*? if forge && >=1.19 {*/alpha,/*?}*/ light, overlay/*? if forge {*/, readExistingColor/*?}*/);
    }
}
