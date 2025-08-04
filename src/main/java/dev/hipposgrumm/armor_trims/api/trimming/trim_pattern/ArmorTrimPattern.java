package dev.hipposgrumm.armor_trims.api.trimming.trim_pattern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/// Use this for armor trim templates. The layers work just like the would in regular armor.
public class ArmorTrimPattern extends TrimPattern {
    private final ResourceLocation layer0;
    private final ResourceLocation layer1;

    /**
     * @param layer0 Base layer for armor (helmet, chestplate, boots).
     * @param layer1 Secondary layer for armor (leggings).
     */
    public ArmorTrimPattern(ResourceLocation layer0, ResourceLocation layer1) {
        this.layer0 = layer0;
        this.layer1 = layer1;
    }

    public ResourceLocation getLayer0() {
        return layer0;
    }

    public ResourceLocation getLayer1() {
        return layer1;
    }

    @Override
    public boolean test(ItemStack item) {
        return item.getItem() instanceof ArmorItem;
    }
}
