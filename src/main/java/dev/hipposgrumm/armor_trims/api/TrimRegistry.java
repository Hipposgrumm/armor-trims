package dev.hipposgrumm.armor_trims.api;

import com.mojang.serialization.Lifecycle;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.base.TrimPatterns;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.ArmorTrimPattern;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.TrimPattern;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * This class holds trim registries.<br>
 * Trims can be registered with this class.
 * @see TrimPatterns Example
 */
public class TrimRegistry {
    public static final ResourceKey<Registry<TrimPattern>> ITEM_TRIM_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Armortrims.MODID,"trims"));
    public static final MappedRegistry<TrimPattern> ITEM_TRIMS = new MappedRegistry<>(ITEM_TRIM_REGISTRY_KEY, Lifecycle.experimental()/*? if >=1.18 {*/, null/*?}*/);

    /// Default trim pattern.
    public static ArmorTrimPattern defaultTrim() {
        return TrimPatterns.COAST;
    }

    /**
     * Register an item trim.
     * @param id ID of the trim.
     * @param trim TrimPattern data for the trim.
     * @return Trim data given.
     */
    public static <T extends TrimPattern> T registerTrim(ResourceLocation id, T trim) {
        return Registry.register(ITEM_TRIMS, id, trim);
    }

    /**
     * Get an item trim from an ID.
     * @param id ID of the trim.
     * @return Trim from the ID.<br>
     * Null if there is no trim associated with that ID.
     */
    @Nullable
    public static TrimPattern getTrim(ResourceLocation id) {
        return ITEM_TRIMS.get(id);
    }
}
