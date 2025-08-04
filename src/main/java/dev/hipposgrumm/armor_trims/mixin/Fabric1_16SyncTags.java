package dev.hipposgrumm.armor_trims.mixin;

import dev.hipposgrumm.armor_trims.Armortrims;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if fabric && <1.18 {
/*@Mixin(ClientPacketListener.class)
public class Fabric1_16SyncTags {
    @Shadow private RegistryAccess registryAccess;

    @Inject(at = @At("TAIL"), method = "handleUpdateTags")
    private void onSynchronizeTagsHook(ClientboundUpdateTagsPacket packet, CallbackInfo info) {
        Armortrims.onReloadData(registryAccess, true);
    }
}
*/