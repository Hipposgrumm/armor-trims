package gg.hipposgrumm.armor_trims.mixin;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
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

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorTrimArmorLayerModifier<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> TRIM_LOCATION_CACHE = Maps.newHashMap();
    private static final Map<Class, Boolean> BLOCKBENCH_MODELS = Maps.newHashMap();
    private boolean isCustomModel = false;

    public ArmorTrimArmorLayerModifier(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Shadow protected abstract void setPartVisibility(A p_117126_, EquipmentSlot p_117127_);

    @Shadow protected abstract Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("TAIL"))
    private void armortrims_humanoidRenderLayerModifier(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci) {
        ItemStack itemstack_m = p_117121_.getItemBySlot(p_117122_);
        if (itemstack_m.getItem() instanceof ArmorItem && TrimmableItem.isTrimmed(itemstack_m)) {
            isCustomModel = this.getArmorModelHook(p_117121_, itemstack_m, p_117122_, p_117124_) != p_117124_;
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

    private boolean isBlockbenchModel(Class clazz) {
        if (!BLOCKBENCH_MODELS.containsKey(clazz)) {
            try {
                // In order to be declared a BlockBench model, one (class) must pass... THE GAUNTLET!
                Method test;
                test = clazz.getMethod("setupAnim", Entity.class, Float.class, Float.class, Float.class, Float.class, Float.class);
                if (!test.isAnnotationPresent(Override.class)) throw new NoSuchMethodException();
                test = clazz.getMethod("renderToBuffer", PoseStack.class, VertexConsumer.class, Integer.class, Integer.class, Float.class, Float.class, Float.class, Float.class);
                if (!test.isAnnotationPresent(Override.class)) throw new NoSuchMethodException();
                test = clazz.getMethod("createBodyLayer");
                BLOCKBENCH_MODELS.put(clazz, true);
            } catch (NoSuchMethodException e) {
                BLOCKBENCH_MODELS.put(clazz, false);
            }
        }
        return BLOCKBENCH_MODELS.get(clazz);
    }

    /* // Whatever, I give up.
    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZLnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V", shift = At.Shift.BEFORE))
    private void armortrims_humanoidRenderLayerCustomArmorModelWorkaround(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci) {
        if (isCustomModel) {
            ItemStack itemstack_w = p_117121_.getItemBySlot(p_117122_);
            int i = TrimmableItem.getMaterialColor(itemstack_w);
            float f = (float) (i >> 24 & 255) / 255.0F;
            float f1 = (float) (i >> 16 & 255) / 255.0F;
            float f2 = (float) (i >> 8 & 255) / 255.0F;
            float f3 = (float) (i & 255) / 255.0F;
            renderModelCustomArmorModelWorkaround(p_117119_, p_117120_, p_117123_, itemstack_w.hasFoil(), p_117124_, f, f1, f2, f3, this.getArmorResource(p_117121_, itemstack_w, p_117122_, null), this.getTrimResource(p_117121_, itemstack_w, p_117122_));
        }
    }

    @Redirect(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZLnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V"))
    private void armortrims_humanoidRenderLayerCustomArmorModelWorkaroundHelper(HumanoidArmorLayer instance, PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, Model p_117112_, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
        if (!isCustomModel) {
            renderModel(p_117107_,p_117108_,p_117109_,p_117111_,p_117112_,p_117114_,p_117115_,p_117116_, armorResource);
        }
    }

    private void renderModelCustomArmorModelWorkaround(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float alpha, float red, float green, float blue, ResourceLocation armorResource, ResourceLocation trimResource) {
        VertexConsumer vertexconsumer = getArmorFoilBufferCustomArmorModelWorkaround(p_117108_, RenderType.armorCutoutNoCull(armorResource), RenderType.armorCutoutNoCull(trimResource), p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    private static VertexConsumer getArmorFoilBufferCustomArmorModelWorkaround(MultiBufferSource buffer, RenderType armorRendery, RenderType trimRendery, boolean isTinFoil) {
        return isTinFoil ? VertexMultiConsumer.create(buffer.getBuffer(RenderType.armorEntityGlint()), VertexMultiConsumer.create(buffer.getBuffer(armorRendery), buffer.getBuffer(trimRendery))) : buffer.getBuffer(armorRendery);
    }
    */
    private boolean usesInnerModel_armortrimsMixin(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    public ResourceLocation getTrimResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot) {
        String trim = TrimmableItem.getTrim(stack);
        String namespace = Armortrims.MODID;
        String location = String.format(java.util.Locale.ROOT, "%s:textures/trims/models/armor/%s%s.png", namespace, trim, (usesInnerModel_armortrimsMixin(slot) ? "_leggings" : ""));

        location = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, location, slot, "overlay");
        ResourceLocation resourcelocation = TRIM_LOCATION_CACHE.get(location);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(location);
            TRIM_LOCATION_CACHE.put(location, resourcelocation);
        }

        return resourcelocation;
    }
}
