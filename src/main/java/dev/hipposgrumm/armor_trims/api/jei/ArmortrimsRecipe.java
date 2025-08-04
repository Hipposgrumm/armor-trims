package dev.hipposgrumm.armor_trims.api.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/// Data Type for JEI Recipe
public class ArmortrimsRecipe {
    private final Ingredient baseInput;
    private final Ingredient additionalInput;
    private final Ingredient materialInput;
    private final ItemStack output;

    /**
     * @param baseInput       - Ingredient used as the base (such as armor)
     * @param additionalInput - Usually a smithing template
     * @param materialInput   - Material used to modify the item (E.G. trim materials)
     * @param output          - Outputs of the recipe
     */
    public ArmortrimsRecipe(Ingredient baseInput, Ingredient additionalInput, Ingredient materialInput, ItemStack output) {
        this.baseInput = baseInput;
        this.additionalInput = additionalInput;
        this.materialInput = materialInput;
        this.output = output;
    }

    public Ingredient baseInput() {
        return baseInput;
    }

    public Ingredient additionalInput() {
        return additionalInput;
    }

    public Ingredient materialInput() {
        return materialInput;
    }

    public ItemStack output() {
        return output;
    }
}
