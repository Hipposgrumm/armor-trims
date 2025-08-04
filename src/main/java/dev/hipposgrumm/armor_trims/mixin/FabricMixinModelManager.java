package dev.hipposgrumm.armor_trims.mixin;

//? if fabric {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.hipposgrumm.armor_trims.Armortrims;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelManager.class)
public class FabricMixinModelManager {
    @Shadow private Map<ResourceLocation, BakedModel> bakedRegistry;

    //? if >=1.19.3 {
    /^@WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;getModelGroups()Lit/unimi/dsi/fastutil/objects/Object2IntMap;"))
    private Object2IntMap<BlockState> armor_trims$bakeTrimModels1_19_3(ModelBakery bakery, Operation<Object2IntMap<BlockState>> original) {
        Object2IntMap<BlockState> out = original.call(bakery);
        Armortrims.onModelBake(this.bakedRegistry, bakery);
        return out;
    }
    ^///?} else {
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/resources/model/ModelBakery;getModelGroups()Lit/unimi/dsi/fastutil/objects/Object2IntMap;"), method = "apply(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    private void armor_trims$bakeTrimModels(ModelBakery bakery, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        Armortrims.onModelBake(this.bakedRegistry, bakery);
    }
    //?}
}
*///?}