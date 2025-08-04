package dev.hipposgrumm.armor_trims.api;

import com.mojang.serialization.Lifecycle;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.base.ItemOverlays;
import dev.hipposgrumm.armor_trims.api.trimming.ItemOverlay;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This class holds item overlays.<br>
 * Item Overlays can be registered with this class.
 * @see ItemOverlays Example
 */
public class OverlayRegistry {
    public static final ResourceKey<Registry<ItemOverlay>> ITEM_OVERLAY_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Armortrims.MODID,"overlays"));
    public static final MappedRegistry<ItemOverlay> ITEM_OVERLAYS = new MappedRegistry<>(ITEM_OVERLAY_REGISTRY_KEY, Lifecycle.experimental()/*? if >=1.18 {*/, null/*?}*/);
    private static final List<BiFunction<ItemStack, ItemOverlay, ItemOverlay>> conditions = new ArrayList<>();

    /// Default item overlay.
    public static ItemOverlay defaultItemOverlay() {
        return ItemOverlays.OTHER;
    }

    /**
     * Register an item overlay.
     * @param id ID of the overlay.
     * @param overlay ItemOverlay data for the overlay.
     * @return Overlay data given.
     */
    public static <T extends ItemOverlay> T registerItemOverlay(ResourceLocation id, T overlay) {
        return Registry.register(ITEM_OVERLAYS, id, overlay);
    }

    /**
     * Run a function to return a specific overlay for the item.
     * @param function - BiFunction returning desired ItemOverlay, params are the Item and the Item Overlay currently inputted by previous functions before it.
     */
    public static void addCondition(BiFunction<ItemStack, ItemOverlay, ItemOverlay> function) {
        conditions.add(function);
    }

    /**
     * Get an item overlay from an ID.
     * @param id ID of the overlay.
     * @return Overlay from the ID.<br>
     * Null if there is no overlay associated with that ID.
     */
    @Nullable
    public static ItemOverlay getItemOverlay(ResourceLocation id) {
        return ITEM_OVERLAYS.get(id);
    }

    /**
     * Returns an item overlay based on a provided itemstack.
     * @param item - Itemstack to give the overlay to.
     * @return Item Overlay associated with itemstack. By default, this is OTHER_TRIM.
     */
    public static ItemOverlay fromItem(ItemStack item) {
        ItemOverlay overlay = ItemOverlays.OTHER;
        for (BiFunction<ItemStack, ItemOverlay, ItemOverlay> condition:conditions) {
            overlay = condition.apply(item, overlay);
        }
        return overlay;
    }
}
