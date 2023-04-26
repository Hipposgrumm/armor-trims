package gg.hipposgrumm.armor_trims.compat.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmortrimsRecipe implements IArmortrimsRecipe {
    private final ItemStack baseInput;
    private final ItemStack additionalInput;
    private final ItemStack materialInput;

    public ArmortrimsRecipe(ItemStack baseInput, ItemStack additionalInput, ItemStack materialInput) {
        this.baseInput = baseInput;
        this.additionalInput = additionalInput;
        this.materialInput = materialInput;
    }

    @Override
    public ItemStack getBaseInput() {
        return baseInput;
    }

    @Override
    public ItemStack getAdditionalInput() {
        return additionalInput;
    }

    @Override
    public ItemStack getMaterialInput() {
        return materialInput;
    }
}
