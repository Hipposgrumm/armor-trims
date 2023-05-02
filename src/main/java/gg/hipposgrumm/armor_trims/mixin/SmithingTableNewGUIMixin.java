package gg.hipposgrumm.armor_trims.mixin;

import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTableBlock.class)
public abstract class SmithingTableNewGUIMixin {
    @Shadow @Final private static Component CONTAINER_TITLE;

    @Shadow public abstract MenuProvider getMenuProvider(BlockState p_56435_, Level p_56436_, BlockPos p_56437_);

    @Redirect(method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;"))
    public MenuProvider armortrims_openNewGUI(BlockState p_56435_, Level p_56436_, BlockPos p_56437_) {
        if (Config.enableNewSmithingGUI()) {
            return new SimpleMenuProvider((p_56424_, p_56425_, p_56426_) -> {
                return new SmithingMenuNew(p_56424_, p_56425_, ContainerLevelAccess.create(p_56436_, p_56437_), null);
            }, CONTAINER_TITLE);
        } else {
            return getMenuProvider(p_56435_, p_56436_, p_56437_);
        }
    }
}
