package gg.hipposgrumm.armor_trims.model;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TrimRenderLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;

    public TrimRenderLayer(RenderLayerParent<T, M> renderContainer, A modelInner, A modelOuter) {
        super(renderContainer, modelInner, modelOuter);
        this.innerModel = modelInner;
        this.outerModel = modelOuter;
    }

    @Override
    public void render(PoseStack p_117096_, MultiBufferSource p_117097_, int p_117098_, T p_117099_, float p_117100_, float p_117101_, float p_117102_, float p_117103_, float p_117104_, float p_117105_) {
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.CHEST, p_117098_, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.LEGS, p_117098_, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.FEET, p_117098_, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.HEAD, p_117098_, this.getArmorModel(EquipmentSlot.HEAD));
        throw new RuntimeException("Trim Layer has been Rendered");
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int p_117123_, A humanoidModel) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem) {
            if (((ArmorItem)itemstack.getItem()).getSlot() == slot) {
                this.getParentModel().copyPropertiesTo(humanoidModel);
                this.setPartVisibility(humanoidModel, slot);
                net.minecraft.client.model.Model model = getArmorModelHook(entity, createArmorModelFromSlot(slot), slot, humanoidModel);
                boolean flag = this.usesInnerModel(slot);
                boolean flag1 = itemstack.hasFoil();
                if (TrimmableItem.isTrimmed(itemstack)) {
                    int color = TrimmableItem.getMaterialColor(itemstack);
                    float a = (float)(color >> 24 & 255) / 255.0F;
                    float r = (float)(color >> 16 & 255) / 255.0F;
                    float g = (float)(color >> 8 & 255) / 255.0F;
                    float b = (float)(color & 255) / 255.0F;
                    this.renderModel(poseStack, bufferSource, p_117123_, flag1, model, r, g, b, a, this.getArmorResource(entity, itemstack, slot, "overlay"));
                }
            }
        }
    }

    private ItemStack createArmorModelFromSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> new ItemStack(Items.DIAMOND_HELMET);
            case CHEST -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case LEGS -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case FEET -> new ItemStack(Items.DIAMOND_BOOTS);
            default -> new ItemStack(Items.DIAMOND_CHESTPLATE);
        };
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int p_117109_, boolean hasFoil, net.minecraft.client.model.Model model, float red, float green, float blue, float alpha, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(armorResource), false, hasFoil);
        model.renderToBuffer(poseStack, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private A getArmorModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }

    public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        String trim = TrimmableItem.getTrim(stack);
        String namespace = Armortrims.MODID;
        String location = String.format(java.util.Locale.ROOT, "%s:textures/trims/models/armor/%s%s.png", namespace, trim, (usesInnerModel(slot) ? "_leggings" : ""));

        location = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, location, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(location);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(location);
            ARMOR_LOCATION_CACHE.put(location, resourcelocation);
        }

        return resourcelocation;
    }

}
