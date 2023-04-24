package gg.hipposgrumm.armor_trims.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.model.TrimDecorationBaker;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class TrimmedItemDecorator {
    @Shadow public abstract void renderModelLists(BakedModel p_115190_, ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_);

    @Shadow @Final private ItemColors itemColors;

    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    private void armortrims_armorDecoration(ItemStack p_115144_, ItemTransforms.TransformType p_115145_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo ci) {
        if (!p_115151_.isCustomRenderer() && (!p_115144_.is(Items.TRIDENT) || (p_115145_ == ItemTransforms.TransformType.GUI || p_115145_ == ItemTransforms.TransformType.GROUND || p_115145_ == ItemTransforms.TransformType.FIXED)) && TrimmableItem.isTrimmed(p_115144_)) {
            boolean fabulousFlag_armortrimsMixin;
            if (p_115145_ != ItemTransforms.TransformType.GUI && !p_115145_.firstPerson() && p_115144_.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) p_115144_.getItem()).getBlock();
                fabulousFlag_armortrimsMixin = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
                fabulousFlag_armortrimsMixin = true;
            }
            net.minecraftforge.client.ForgeHooksClient.drawItemLayered((ItemRenderer) (Object) this, p_115151_, p_115144_, p_115147_, p_115148_, p_115149_, p_115150_, p_115146_);

            /** *Casually steals forge code...* */
            BakedModel layer = getModelForSlot(p_115144_);

            RenderType rendertype = ItemBlockRenderTypes.getRenderType(p_115144_, fabulousFlag_armortrimsMixin);
            net.minecraftforge.client.ForgeHooksClient.setRenderType(rendertype); // needed for compatibility with MultiLayerModels
            VertexConsumer ivertexbuilder;
            if (fabulousFlag_armortrimsMixin) {
                ivertexbuilder = ItemRenderer.getFoilBufferDirect(p_115148_, rendertype, true, p_115144_.hasFoil());
            } else {
                ivertexbuilder = ItemRenderer.getFoilBuffer(p_115148_, rendertype, true, p_115144_.hasFoil());
            }

            ItemStack overlayStack = new ItemStack(Items.AIR);
            overlayStack = TrimmableItem.applyTrim(overlayStack, Trims.getValueOf(TrimmableItem.getTrim(p_115144_).toUpperCase()), new ItemStack(ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(p_115144_))), true);
            this.renderModelLists(layer, overlayStack, p_115149_, p_115150_, p_115147_, ivertexbuilder);
        }
    }

    @Redirect(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I"))
    private int armortrims_armorDecorationTint(ItemColors instance, ItemStack p_92677_, int p_92678_) {
        if (TrimmableItem.isTrimmed(p_92677_) && p_92677_.is(Items.AIR)) {
            return TrimmableItem.getMaterialColor(p_92677_);
        }
        return this.itemColors.getColor(p_92677_,p_92678_);
    }

    @Redirect(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean armortrims_armorDecorationEmptyCheckOverride(ItemStack instance) {
        if (instance.is(Items.AIR) && TrimmableItem.isTrimmed(instance)) {
            return false;
        }
        return instance.isEmpty();
    }

    @Redirect(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BakedQuad;isTinted()Z"))
    private boolean armortrims_armorDecorationTintCheckOverride(BakedQuad instance) {
        return true;
    }

    private static BakedModel getModelForSlot(ItemStack item) {
        if (!(item.getItem() instanceof ArmorItem)) return TrimDecorationBaker.INSTANCE.other;
        EquipmentSlot slot = ((ArmorItem)item.getItem()).getSlot();
        return switch (slot) {
            case HEAD -> TrimDecorationBaker.INSTANCE.helmet;
            case CHEST -> TrimDecorationBaker.INSTANCE.chestplate;
            case LEGS -> TrimDecorationBaker.INSTANCE.leggings;
            case FEET -> TrimDecorationBaker.INSTANCE.boots;
            default -> TrimDecorationBaker.INSTANCE.other;
        };
    }
}
