package gg.hipposgrumm.armor_trims.api;

//? if >=1.18 {
import dev.hipposgrumm.armor_trims.api.*;
import dev.hipposgrumm.armor_trims.api.item.*;
import dev.hipposgrumm.armor_trims.api.trimming.*;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.TrimPattern;
import dev.hipposgrumm.armor_trims.api.item.UpgradeSmithingTemplate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

//? if forge
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class only exists to prevent issues with mods that may have used it.
 * @deprecated Use classes in {@link dev.hipposgrumm.armor_trims.api} instead.
 */
@Deprecated(since = "1.4")
public final class ArmortrimsApi {
    /**
     * @deprecated Just don't.
     */
    @Deprecated
    public ArmortrimsApi(String modid) {}

    /**
     * @deprecated Use {@link TrimRegistry#registerTrim(ResourceLocation, TrimPattern)} and {@link TrimPattern}
     */
    @Deprecated
    public ArmortrimsApi createTrim(String name, ResourceLocation overlay0, ResourceLocation overlay1) {
        return this;
    }

    /**
     * @deprecated Now handled directly by classes overriding {@link TrimPattern#test(ItemStack)}
     */
    @Deprecated
    public ArmortrimsApi addTrimmableItem(Class<? extends Item> itemClass, String translatableName) {
        return this;
    }

    /**
     * @deprecated Now handled directly by classes overriding {@link TrimPattern#test(ItemStack)}
     */
    @Deprecated
    public ArmortrimsApi addTrimmableItem(TagKey<Item> itemTag, String translatableName) {
        return this;
    }

    /** @deprecated It's a tag now. */
    @Deprecated
    public ArmortrimsApi addConfigDefault(String material) {
        return this;
    }

    /** @deprecated It's a tag now. */
    @Deprecated
    public ArmortrimsApi addConfigDefault(ResourceLocation material) {
        return this;
    }

    /** @deprecated It's a tag now. */
    @Deprecated
    public ArmortrimsApi addConfigDefault(TagKey<Item> material) {
        return this;
    }

    /**
     * @deprecated Use {@link SmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createTrimTemplate(ResourceLocation trim, String translatedName, String itemId) {
        return this;
    }

    /**
     * @deprecated Use {@link SmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createTrimTemplate(ResourceLocation trim, String translatedName, String itemId, Item.Properties properties) {
        return this;
    }

    /**
     * @deprecated Use {@link UpgradeSmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, String translatableName, String translatableInput, String itemId) {
        return this;
    }

    /**
     * @deprecated Use {@link UpgradeSmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, String translatableName, String translatableInput, String itemId, Item.Properties properties) {
        return this;
    }

    /**
     * @deprecated Use {@link UpgradeSmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, Supplier<Boolean> shouldEnableRecipe, String translatableName, String translatableInput, String itemId) {
        return this;
    }

    /**
     * @deprecated Use {@link UpgradeSmithingTemplate} and regular item registries for your modloader.
     */
    @Deprecated
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, Supplier<Boolean> shouldEnableRecipe, String translatableName, String translatableInput, String itemId, Item.Properties properties) {
        return this;
    }

    /**
     * @deprecated Use {@link OverlayRegistry#registerItemOverlay(ResourceLocation, ItemOverlay)} and {@link ItemOverlay}
     */
    @Deprecated
    public ArmortrimsApi createCustomTrimModel(Item item, ResourceLocation modelLocation) {
        return this;
    }

    /**
     * @deprecated Use {@link OverlayRegistry#registerItemOverlay(ResourceLocation, ItemOverlay)} and {@link ItemOverlay}
     */
    @Deprecated
    public void createTrimModel(Item item, ResourceLocation modelLocation) {}

    /**
     * What was this even about anyway?
     * @deprecated Use {@link OverlayRegistry#registerItemOverlay(ResourceLocation, ItemOverlay)} and {@link ItemOverlay}
     */
    @Deprecated
    public ArmortrimsApi addCustomModelLoader(Consumer<ArmortrimsApi> method) {
        method.accept(this);
        return this;
    }

    /**
     * @deprecated If you really need it just copy it. It's not anything special.
     */
    @Deprecated
    public static Item getItem(ResourceLocation itemId) {
        //? if forge {
        return ForgeRegistries.ITEMS.getValue(itemId);
        //?} else {
        /*return Registry.ITEM.get(itemId);
        *///?}
    }
}
//?}