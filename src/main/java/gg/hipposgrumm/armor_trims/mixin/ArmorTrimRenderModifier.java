package gg.hipposgrumm.armor_trims.mixin;

import gg.hipposgrumm.armor_trims.model.TrimRenderLayer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public class ArmorTrimRenderModifier extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ArmorTrimRenderModifier(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("RETURN"))
    private void armortrims_playerTrimInjector(EntityRendererProvider.Context p_174557_, boolean p_174558_, CallbackInfo ci) {
        this.addLayer(new TrimRenderLayer<>(this, new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
        return player.getSkinTextureLocation();
    }

    @Mixin(AbstractZombieRenderer.class)
    public static abstract class ZombieTrimRenderInjector<T extends Zombie, M extends ZombieModel<T>> extends HumanoidMobRenderer<T, M> {
        public ZombieTrimRenderInjector(EntityRendererProvider.Context p_174169_, M p_174170_, float p_174171_) {
            super(p_174169_, p_174170_, p_174171_);
        }

        @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/ZombieModel;Lnet/minecraft/client/model/ZombieModel;Lnet/minecraft/client/model/ZombieModel;)V", at = @At("RETURN"))
        private void armortrims_zombieTrimInjector(EntityRendererProvider.Context p_173910_, ZombieModel p_173911_, ZombieModel p_173912_, ZombieModel p_173913_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, p_173912_, p_173913_));
        }
    }

    @Mixin(ArmorStandRenderer.class)
    public static abstract class ArmorstandTrimRenderInjector extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
        public ArmorstandTrimRenderInjector(EntityRendererProvider.Context p_174289_, ArmorStandArmorModel p_174290_, float p_174291_) {
            super(p_174289_, p_174290_, p_174291_);
        }

        @Inject(method = "Lnet/minecraft/client/renderer/entity/ArmorStandRenderer;<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("RETURN"))
        private void armortrims_armorstandTrimInjector(EntityRendererProvider.Context p_173915_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, new ArmorStandArmorModel(p_173915_.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorModel(p_173915_.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR))));
        }
    }

    @Mixin(GiantMobRenderer.class)
    public static abstract class GiantZombieTrimRenderInjector extends MobRenderer<Giant, HumanoidModel<Giant>> {
        public GiantZombieTrimRenderInjector(EntityRendererProvider.Context p_174304_, HumanoidModel<Giant> p_174305_, float p_174306_) {
            super(p_174304_, p_174305_, p_174306_);
        }

        @Inject(method = "Lnet/minecraft/client/renderer/entity/GiantMobRenderer;<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;F)V", at = @At("RETURN"))
        private void armortrims_giantzombieTrimInjector(EntityRendererProvider.Context p_174131_, float p_174132_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, new GiantZombieModel(p_174131_.bakeLayer(ModelLayers.GIANT_INNER_ARMOR)), new GiantZombieModel(p_174131_.bakeLayer(ModelLayers.GIANT_OUTER_ARMOR))));
        }
    }

    @Mixin(PiglinRenderer.class)
    public static abstract class PiglinTrimRenderInjector extends HumanoidMobRenderer<Mob, PiglinModel<Mob>> {
        public PiglinTrimRenderInjector(EntityRendererProvider.Context p_174169_, PiglinModel<Mob> p_174170_, float p_174171_) {
            super(p_174169_, p_174170_, p_174171_);
        }

        @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/geom/ModelLayerLocation;Lnet/minecraft/client/model/geom/ModelLayerLocation;Lnet/minecraft/client/model/geom/ModelLayerLocation;Z)V", at = @At("RETURN"))
        private void armortrims_piglinTrimInjector(EntityRendererProvider.Context p_174344_, ModelLayerLocation p_174345_, ModelLayerLocation p_174346_, ModelLayerLocation p_174347_, boolean p_174348_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, new HumanoidModel(p_174344_.bakeLayer(p_174346_)), new HumanoidModel(p_174344_.bakeLayer(p_174347_))));
        }
    }

    @Mixin(SkeletonRenderer.class)
    public static abstract class SkeletonTrimRenderInjector extends HumanoidMobRenderer<AbstractSkeleton, SkeletonModel<AbstractSkeleton>> {
        public SkeletonTrimRenderInjector(EntityRendererProvider.Context p_174169_, SkeletonModel<AbstractSkeleton> p_174170_, float p_174171_) {
            super(p_174169_, p_174170_, p_174171_);
        }

        @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/geom/ModelLayerLocation;Lnet/minecraft/client/model/geom/ModelLayerLocation;Lnet/minecraft/client/model/geom/ModelLayerLocation;)V", at = @At("RETURN"))
        private void armortrims_skeletonTrimInjector(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, new SkeletonModel(p_174382_.bakeLayer(p_174384_)), new SkeletonModel(p_174382_.bakeLayer(p_174385_))));
        }
    }

    @Mixin(ZombieVillagerRenderer.class)
    public static abstract class VillagerZombieTrimRenderInjector extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
        public VillagerZombieTrimRenderInjector(EntityRendererProvider.Context p_174169_, ZombieVillagerModel<ZombieVillager> p_174170_, float p_174171_) {
            super(p_174169_, p_174170_, p_174171_);
        }

        @Inject(method = "Lnet/minecraft/client/renderer/entity/ZombieVillagerRenderer;<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("RETURN"))
        private void armortrims_villagerzombieTrimInjector(EntityRendererProvider.Context p_174463_, CallbackInfo ci) {
            this.addLayer(new TrimRenderLayer<>(this, new ZombieVillagerModel(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))));
        }
    }
}

/**
 * Unused old code.
 */
/*
@OnlyIn(Dist.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorTrimRenderModifier<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static Map<String, ResourceLocation> TRIM_LOCATION_CACHE;

    public ArmorTrimRenderModifier(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("TAIL"))
    public void armortrims_modifyArmorRender(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci) {
        ItemStack itemstack_m = p_117121_.getItemBySlot(p_117122_);
        if (itemstack_m.getItem() instanceof ArmorItem && TrimmableItem.isTrimmed(itemstack_m)) {
            net.minecraft.client.model.Model model_m = this.getArmorModelHook_armortrims_forMixin(p_117121_, itemstack_m, p_117122_, p_117124_);
            boolean flag1_m = itemstack_m.hasFoil();
            int material = TrimmableItem.getMaterialColor(itemstack_m);
            float trimR = (float)(material >> 24 & 255) / 255.0F;
            float trimG = (float)(material >> 16 & 255) / 255.0F;
            float trimB = (float)(material >> 8 & 255) / 255.0F;
            float trimA = (float)(material >> 0 & 255) / 255.0F;
            this.renderModel_armortrims_forMixin(p_117119_, p_117120_, p_117123_, flag1_m, model_m, trimR, trimG, trimB, this.getTrimResource(p_117121_, itemstack_m, p_117122_, "overlay"));
        }
    }

    private void renderModel_armortrims_forMixin(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, p_117114_, p_117115_, p_117116_, 1.0F);
    }

    public ResourceLocation getTrimResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        String trim = TrimmableItem.getTrim(stack);
        String s1 = Main.MODID+":textures/trims/models/armor/"+trim+".png";

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = TRIM_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            TRIM_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    /**
     * Hook to allow item-sensitive armor model. for HumanoidArmorLayer.
     *\/
    protected net.minecraft.client.model.Model getArmorModelHook_armortrims_forMixin(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }

}
*/
