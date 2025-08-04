package dev.hipposgrumm.armor_trims.api.base;

import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.OverlayRegistry;
import dev.hipposgrumm.armor_trims.api.trimming.ItemOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

/// Item Overlays
public class ItemOverlays {
    public static final ItemOverlay OTHER = OverlayRegistry.registerItemOverlay(new ResourceLocation(Armortrims.MODID, "other"), new ItemOverlay(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim")));
    public static final ItemOverlay HEAD  = OverlayRegistry.registerItemOverlay(new ResourceLocation(Armortrims.MODID, "head"),  new ItemOverlay(new ResourceLocation(Armortrims.MODID, "item/overlay/helmet_trim")));
    public static final ItemOverlay CHEST = OverlayRegistry.registerItemOverlay(new ResourceLocation(Armortrims.MODID, "chest"), new ItemOverlay(new ResourceLocation(Armortrims.MODID, "item/overlay/chestplate_trim")));
    public static final ItemOverlay LEGS  = OverlayRegistry.registerItemOverlay(new ResourceLocation(Armortrims.MODID, "legs"),  new ItemOverlay(new ResourceLocation(Armortrims.MODID, "item/overlay/leggings_trim")));
    public static final ItemOverlay FEET  = OverlayRegistry.registerItemOverlay(new ResourceLocation(Armortrims.MODID, "feet"),  new ItemOverlay(new ResourceLocation(Armortrims.MODID, "item/overlay/boots_trim")));

    @ApiStatus.Internal
    public static void register() {
        OverlayRegistry.addCondition((item, overlay) -> {
            Item it = item.getItem();
            if (it instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem) it;
                switch (armorItem.getSlot()) {
                    // This definitely won't confuse people.
                    case HEAD:  return HEAD;
                    case CHEST: return CHEST;
                    case LEGS:  return LEGS;
                    case FEET:  return FEET;
                }
            }
            return overlay;
        });
    }
}
