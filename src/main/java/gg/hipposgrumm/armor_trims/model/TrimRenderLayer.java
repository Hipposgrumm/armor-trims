package gg.hipposgrumm.armor_trims.model;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

public class TrimRenderLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> TRIM_LOCATION_CACHE = Maps.newHashMap();
    private static final ModelLayerLocation TRIM_INNER = new ModelLayerLocation(new ResourceLocation(Armortrims.MODID, "trim"), "inner");
    private static final ModelLayerLocation TRIM_OUTER = new ModelLayerLocation(new ResourceLocation(Armortrims.MODID, "trim"), "outer");
    private static final Map<EquipmentSlot, HumanoidModel<LivingEntity>> TRIM_MODELS = new HashMap<>();

    public TrimRenderLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    public void render(PoseStack p_117096_, MultiBufferSource p_117097_, int p_117098_, T p_117099_, float p_117100_, float p_117101_, float p_117102_, float p_117103_, float p_117104_, float p_117105_) {
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.CHEST, p_117098_, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.LEGS, p_117098_, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.FEET, p_117098_, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.HEAD, p_117098_, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack pose, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int p_117123_, M armorModel) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem && TrimmableItem.isTrimmed(itemstack)) {
            this.getParentModel().copyPropertiesTo(armorModel);
            this.setPartVisibility(armorModel, slot);
            net.minecraft.client.model.Model model = getArmorModelHook(entity, itemstack, slot, armorModel);
            boolean isEnchanted = itemstack.hasFoil();
            int i = TrimmableItem.getMaterialColor(itemstack);
            float f = (float)(i >> 24 & 255) / 255.0F;
            float f1 = (float)(i >> 16 & 255) / 255.0F;
            float f2 = (float)(i >> 8 & 255) / 255.0F;
            float f3 = (float)(i & 255) / 255.0F;
            this.renderModel(pose, bufferSource, p_117123_, isEnchanted, model, f, f1, f2, f3, this.getTrimResource(entity, itemstack, slot));
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

    protected void setPartVisibility(M p_117126_, EquipmentSlot p_117127_) {
        p_117126_.setAllVisible(false);
        switch(p_117127_) {
            case HEAD:
                p_117126_.head.visible = true;
                p_117126_.hat.visible = true;
                break;
            case CHEST:
                p_117126_.body.visible = true;
                p_117126_.rightArm.visible = true;
                p_117126_.leftArm.visible = true;
                break;
            case LEGS:
                p_117126_.body.visible = true;
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
                break;
            case FEET:
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
        }

    }

    public static void modelsInit(EntityRendererProvider.Context context) {
        TRIM_MODELS.put(EquipmentSlot.HEAD, new HumanoidModel<>(context.bakeLayer(TRIM_OUTER)));
        TRIM_MODELS.put(EquipmentSlot.CHEST, new HumanoidModel<>(context.bakeLayer(TRIM_OUTER)));
        TRIM_MODELS.put(EquipmentSlot.LEGS, new HumanoidModel<>(context.bakeLayer(TRIM_INNER)));
        TRIM_MODELS.put(EquipmentSlot.FEET, new HumanoidModel<>(context.bakeLayer(TRIM_OUTER)));
    }

    @Mixin(PlayerRenderer.class)
    public static class InitModels {
        @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("RETURN"))
        private void armortrims_trimModelsInitWithContext(EntityRendererProvider.Context p_174557_, boolean p_174558_, CallbackInfo ci) {
            modelsInit(p_174557_);
        }
    }

    @SuppressWarnings("unchecked")
    private M getArmorModel(EquipmentSlot p_117079_) {
        return (M) TRIM_MODELS.get(p_117079_);
    }

    private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float alpha, float red, float green, float blue, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    /**
     * Hook to allow item-sensitive armor model. for HumanoidArmorLayer.
     * @author Forge
     */
    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, M model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
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
}
