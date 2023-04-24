package gg.hipposgrumm.armor_trims.mixin;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.model.TrimRenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class ArmorTrimRenderingPiggyback {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;<init>(Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;)V", at = @At("RETURN"))
    private void armortrims_trimRenderLayerPiggybackArmorRenderer(RenderLayerParent p_117075_, HumanoidModel p_117076_, HumanoidModel p_117077_, CallbackInfo ci) {
        new TrimRenderLayer(p_117075_, p_117076_, p_117077_);
    }
}
