package gg.hipposgrumm.armor_trims.mixin;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorTrimArmorLayerModifier<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> TRIM_LOCATION_CACHE = Maps.newHashMap();
    private static final Map<HumanoidModel<LivingEntity>, Boolean> CUSTOM_MODELS_CHECK_CACHE = Maps.newHashMap();
    private boolean isCustomModel = false;

    public ArmorTrimArmorLayerModifier(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Shadow protected abstract void setPartVisibility(A p_117126_, EquipmentSlot p_117127_);

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("TAIL"))
    private void armortrims_humanoidRenderLayerModifier(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci) {
        ItemStack itemstack_m = p_117121_.getItemBySlot(p_117122_);
        if (LargeItemLists.getAllTrimmable().contains(itemstack_m.getItem()) && TrimmableItem.isTrimmed(itemstack_m)) {
            isCustomModel = checkCustomModel(p_117121_, itemstack_m, p_117122_, p_117124_);
            this.getParentModel().copyPropertiesTo(p_117124_);
            this.setPartVisibility(p_117124_, p_117122_);
            net.minecraft.client.model.Model model = ForgeHooksClient.getArmorModel(p_117121_, Config.customArmorModelHandling().equalsIgnoreCase("NORMAL")?createVanillaArmorModel(itemstack_m.getEquipmentSlot()):itemstack_m, p_117122_, p_117124_);
            boolean isPants = usesInnerModel_armortrimsMixin(p_117122_);
            boolean isEnchanted = itemstack_m.hasFoil();
            int i = TrimmableItem.getMaterialColor(itemstack_m);
            float f = (float)(i >> 24 & 255) / 255.0F;
            float f1 = (float)(i >> 16 & 255) / 255.0F;
            float f2 = (float)(i >> 8 & 255) / 255.0F;
            float f3 = (float)(i & 255) / 255.0F;
            if (!isCustomModel || (Config.customArmorModelHandling().equalsIgnoreCase("NORMAL") || Config.customArmorModelHandling().equalsIgnoreCase("TINTED"))) {
                this.renderModel_armortrimsMixin(p_117119_, p_117120_, p_117123_, isEnchanted, model, f, f1, f2, f3, this.getTrimResource(p_117121_, itemstack_m, p_117122_));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean checkCustomModel(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        if (!CUSTOM_MODELS_CHECK_CACHE.containsKey(model)) CUSTOM_MODELS_CHECK_CACHE.put((HumanoidModel<LivingEntity>) model, this.getArmorModelHook(entity, itemStack, slot, model) != model);
        return CUSTOM_MODELS_CHECK_CACHE.get(model);
    }

    private static ItemStack createVanillaArmorModel(EquipmentSlot slot) {
        if (slot==null) return Items.AIR.getDefaultInstance();
        return new ItemStack(switch (slot) {
            case HEAD -> Items.DIAMOND_HELMET;
            case CHEST -> Items.DIAMOND_CHESTPLATE;
            case LEGS -> Items.DIAMOND_LEGGINGS;
            case FEET -> Items.DIAMOND_BOOTS;
            default -> Items.AIR;
        });
    }

    private void renderModel_armortrimsMixin(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float alpha, float red, float green, float blue, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    private boolean usesInnerModel_armortrimsMixin(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    public ResourceLocation getTrimResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot) {
        Trims trim = new Trims(TrimmableItem.getTrim(stack));
        String location = trim.getLocation(slot.equals(EquipmentSlot.LEGS)).toString();

        location = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, location, slot, "overlay");
        ResourceLocation resourcelocation = TRIM_LOCATION_CACHE.get(location);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(location);
            TRIM_LOCATION_CACHE.put(location, resourcelocation);
        }

        return resourcelocation;
    }

    /**
     * Hook to allow item-sensitive armor model. for HumanoidArmorLayer.
     */
    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}
