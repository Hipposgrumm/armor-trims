package dev.hipposgrumm.armor_trims.mixinaccess;

import net.minecraft.resources.ResourceLocation;

/**
 * A Duck Interface is an interface implemented by a Mixin class that can be
 * used to call functions directly within that Mixin class. This includes
 * setters and getters.<br><br>
 * For more information, use the <code>!!duck</code> command in The Fabric Project discord server.
 */
public interface DuckModelBakery {
    void armor_trims$setTrimAtlasTexture(ResourceLocation location);
}
