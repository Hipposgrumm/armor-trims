package gg.hipposgrumm.armor_trims.compat.jei;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

public interface IArmortrimsRecipe {
    @Unmodifiable
    ItemStack getBaseInput();

    @Unmodifiable
    ItemStack getAdditionalInput();

    @Unmodifiable
    ItemStack getMaterialInput();

    @Unmodifiable
    ItemStack getOutput();
}
