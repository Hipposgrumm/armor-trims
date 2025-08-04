package dev.hipposgrumm.armor_trims.api.base;

import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.base.item.NetheriteUpgradeSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.item.ArmorTrimSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.item.SmithingTemplate;
import dev.hipposgrumm.armor_trims.api.item.UpgradeSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.ArmorTrimPattern;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?}

public class SmithingTemplateItems {
    //? if forge
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Armortrims.MODID);

    public static final Supplier<UpgradeSmithingTemplate> NETHERITE_UPGRADE_SMITHING_TEMPLATE = createAndRegisterTemplate(UpgradeSmithingTemplate.class, "netherite_upgrade_smithing_template", null, true);

    public static final Supplier<ArmorTrimSmithingTemplate> BOLT_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "bolt_armor_trim_smithing_template",      TrimPatterns.BOLT,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> COAST_SMITHING_TEMPLATE     = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "coast_armor_trim_smithing_template",     TrimPatterns.COAST,     false);
    public static final Supplier<ArmorTrimSmithingTemplate> DUNE_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "dune_armor_trim_smithing_template",      TrimPatterns.DUNE,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> EYE_SMITHING_TEMPLATE       = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "eye_armor_trim_smithing_template",       TrimPatterns.EYE,       false);
    public static final Supplier<ArmorTrimSmithingTemplate> FLOW_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "flow_armor_trim_smithing_template",      TrimPatterns.FLOW,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> HOST_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "host_armor_trim_smithing_template",      TrimPatterns.HOST,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> RAISER_SMITHING_TEMPLATE    = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "raiser_armor_trim_smithing_template",    TrimPatterns.RAISER,    false);
    public static final Supplier<ArmorTrimSmithingTemplate> RIB_SMITHING_TEMPLATE       = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "rib_armor_trim_smithing_template",       TrimPatterns.RIB,       false);
    public static final Supplier<ArmorTrimSmithingTemplate> SENTRY_SMITHING_TEMPLATE    = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "sentry_armor_trim_smithing_template",    TrimPatterns.SENTRY,    false);
    public static final Supplier<ArmorTrimSmithingTemplate> SHAPER_SMITHING_TEMPLATE    = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "shaper_armor_trim_smithing_template",    TrimPatterns.SHAPER,    false);
    public static final Supplier<ArmorTrimSmithingTemplate> SILENCE_SMITHING_TEMPLATE   = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "silence_armor_trim_smithing_template",   TrimPatterns.SILENCE,   false);
    public static final Supplier<ArmorTrimSmithingTemplate> SNOUT_SMITHING_TEMPLATE     = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "snout_armor_trim_smithing_template",     TrimPatterns.SNOUT,     false);
    public static final Supplier<ArmorTrimSmithingTemplate> SPIRE_SMITHING_TEMPLATE     = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "spire_armor_trim_smithing_template",     TrimPatterns.SPIRE,     false);
    public static final Supplier<ArmorTrimSmithingTemplate> TIDE_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "tide_armor_trim_smithing_template",      TrimPatterns.TIDE,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> VEX_SMITHING_TEMPLATE       = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "vex_armor_trim_smithing_template",       TrimPatterns.VEX,       false);
    public static final Supplier<ArmorTrimSmithingTemplate> WARD_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "ward_armor_trim_smithing_template",      TrimPatterns.WARD,      false);
    public static final Supplier<ArmorTrimSmithingTemplate> WAYFINDER_SMITHING_TEMPLATE = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "wayfinder_armor_trim_smithing_template", TrimPatterns.WAYFINDER, false);
    public static final Supplier<ArmorTrimSmithingTemplate> WILD_SMITHING_TEMPLATE      = createAndRegisterTemplate(ArmorTrimSmithingTemplate.class, "wild_armor_trim_smithing_template",      TrimPatterns.WILD,      false);

    @SuppressWarnings("unchecked")
    private static <T extends SmithingTemplate> Supplier<T> createAndRegisterTemplate(Class<T> _genericBinding, String id, ArmorTrimPattern pattern, boolean diamondUpgrade) {
        Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS).stacksTo(1);
        Supplier<SmithingTemplate> item = () -> {
            if (diamondUpgrade) return new NetheriteUpgradeSmithingTemplate(new ResourceLocation(Armortrims.MODID, "netherite_upgrade"), props);
            return new ArmorTrimSmithingTemplate(pattern, () -> Ingredient.of(Armortrims.TRIM_MATERIALS_TAG), props);
        };
        //? if forge {
        return (Supplier<T>) ITEMS.register(id,item);
        //?} else {
        /*T i = (T) Registry.register(Registry.ITEM, new ResourceLocation(Armortrims.MODID, id), item.get());
        return () -> i;
        *///?}
    }

    @ApiStatus.Internal
    public static void register(/*? if forge {*/IEventBus bus/*?}*/) {
        //? if forge {
        ITEMS.register(bus);
        //?} else {
        /*NETHERITE_UPGRADE_SMITHING_TEMPLATE.get();
        BOLT_SMITHING_TEMPLATE.get();
        COAST_SMITHING_TEMPLATE.get();
        DUNE_SMITHING_TEMPLATE.get();
        EYE_SMITHING_TEMPLATE.get();
        FLOW_SMITHING_TEMPLATE.get();
        HOST_SMITHING_TEMPLATE.get();
        RAISER_SMITHING_TEMPLATE.get();
        RIB_SMITHING_TEMPLATE.get();
        SENTRY_SMITHING_TEMPLATE.get();
        SHAPER_SMITHING_TEMPLATE.get();
        SILENCE_SMITHING_TEMPLATE.get();
        SNOUT_SMITHING_TEMPLATE.get();
        SPIRE_SMITHING_TEMPLATE.get();
        TIDE_SMITHING_TEMPLATE.get();
        VEX_SMITHING_TEMPLATE.get();
        WARD_SMITHING_TEMPLATE.get();
        WAYFINDER_SMITHING_TEMPLATE.get();
        WILD_SMITHING_TEMPLATE.get();
        *///?}
    }
}
