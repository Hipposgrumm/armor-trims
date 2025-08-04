package dev.hipposgrumm.armor_trims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.hipposgrumm.armor_trims.config.Config;
import dev.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingTableBlock.class)
public class MixinSmithingTable {
    @Shadow @Final private static Component CONTAINER_TITLE;

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;"))
    public MenuProvider armor_trims$openNewGUI(BlockState state, Level level, BlockPos pos, Operation<MenuProvider> original) {
        if (Config.enableNewSmithingGUI) {
            return new SimpleMenuProvider((id, inventory, player) -> new SmithingMenuNew(id, inventory, ContainerLevelAccess.create(level, pos), player), CONTAINER_TITLE);
        }
        return original.call(state, level, pos);
    }
}