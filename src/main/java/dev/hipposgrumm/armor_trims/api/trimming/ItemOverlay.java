package dev.hipposgrumm.armor_trims.api.trimming;

import dev.hipposgrumm.armor_trims.api.OverlayRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/// This is the trim overlay on the trimmed item.
public class ItemOverlay {
    protected final ResourceLocation location;
    private ResourceLocation id;

    /**
     * @param location Location of the overlay texture.
     */
    public ItemOverlay(ResourceLocation location) {
        this.location = location;
    }

    /**
     * Get the ID of this item overlay.
     * @return ID of the item overlay.<br>
     * Null if overlay is not registered.
     */
    @Nullable
    public ResourceLocation getId() {
        if (id == null) id = OverlayRegistry.ITEM_OVERLAYS.getKey(this);
        return id;
    }

    /**
     * @return Location of overlay texture.
     */
    public ResourceLocation textureLocation() {
        return location;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
