package dev.hipposgrumm.armor_trims.api.trimming.trim_pattern;

import dev.hipposgrumm.armor_trims.api.TrimRegistry;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/// Class used for making trim types.
public abstract class TrimPattern {
    private ResourceLocation id;

    /**
     * Get the ID of this item trim.
     * @return ID of the item trim.<br>
     * Null if trim is not registered.
     */
    @Nullable
    public final ResourceLocation getId() {
        if (id == null) id = TrimRegistry.ITEM_TRIMS.getKey(this);
        return id;
    }

    /**
     * Determine whether this trim pattern can be applied to an item.
     * @see ArmorTrimPattern#test(ItemStack) ArmorTrimPattern#test()
     */
    public abstract boolean test(ItemStack item);

    @Override
    public String toString() {
        ResourceLocation id = getId();
        if (id==null) id = MissingTextureAtlasSprite.getLocation();
        return id.toString();
    }
}