package dev.hipposgrumm.armor_trims;

import com.mojang.serialization.Codec;
import dev.hipposgrumm.armor_trims.api.base.ItemOverlays;
import dev.hipposgrumm.armor_trims.api.base.SmithingTemplateItems;
import dev.hipposgrumm.armor_trims.api.base.TrimPatterns;
import dev.hipposgrumm.armor_trims.config.Config;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.config.ConfigScreen;
import dev.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import dev.hipposgrumm.armor_trims.gui.SmithingScreenNew;
import dev.hipposgrumm.armor_trims.model.ItemTrimModels;
import dev.hipposgrumm.armor_trims.util.TrimTextureManager;
import dev.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
//? if >=1.18.2 {
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
*///?}
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;

//? if forge {
import dev.hipposgrumm.armor_trims.loot.ChestLootModifier;
import dev.hipposgrumm.armor_trims.loot.EntityLootModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//? if >=1.19 {
    import net.minecraftforge.client.ConfigScreenHandler;
    import net.minecraftforge.client.event.ModelEvent;
//?} else {
    /*import net.minecraftforge.client.event.ModelBakeEvent;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
    //? if >=1.18 {
        /^import net.minecraftforge.client.ConfigGuiHandler;
        import net.minecraftforge.client.model.ForgeModelBakery;
    ^///?}
*///?}
//? if >=1.17 {
    import net.minecraftforge.common.extensions.IForgeMenuType;
    import net.minecraftforge.event.ModMismatchEvent;
    import net.minecraftforge.registries.RegistryObject;
//?} else {
    /*import net.minecraftforge.client.model.ModelLoader;
    import net.minecraftforge.fml.ExtensionPoint;
    import net.minecraftforge.common.extensions.IForgeContainerType;
    import net.minecraftforge.event.RegistryEvent;
*///?}
//?} else {
    /*import dev.hipposgrumm.armor_trims.loot.LootModifiers;
    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.api.ModInitializer;
    import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
    import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
    //? if >1.18 {
        import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
        import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
    //?} else {
        /^import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
        import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
        import net.fabricmc.fabric.api.tag.TagRegistry;
        import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
    ^///?}
*///?}

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

//? if forge
@Mod(Armortrims.MODID)
public class Armortrims/*? if fabric {*//*implements ModInitializer, ClientModInitializer*//*?}*/{
    public static final String MODID = "armor_trims";
    public static final Logger LOGGER = LogManager.getLogger("Armor Trims Backport");

    //? if forge {
    public static final DeferredRegister<MenuType<?>> GUIS = DeferredRegister.create(ForgeRegistries./*? if >=1.19 {*/MENU_TYPES/*?} else {*//*CONTAINERS*//*?}*/, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    //? if >=1.17 {
    public static final DeferredRegister</*? if >=1.19 {*/Codec<? extends IGlobalLootModifier>/*?} else {*//*GlobalLootModifierSerializer<?>*//*?}*/> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys./*? if >=1.19 {*/GLOBAL_LOOT_MODIFIER_SERIALIZERS/*?} else {*//*LOOT_MODIFIER_SERIALIZERS*//*?}*/, MODID);
    public static final RegistryObject</*? if >=1.19 {*/Codec<ChestLootModifier>/*?} else {*//*ChestLootModifier.Serializer*//*?}*/> TEMPLATE_CHEST_LOOT = LOOT_MODIFIERS.register("chest_loot_modifier", ChestLootModifier./*? if >=1.19 {*/CODEC/*?} else {*//*Serializer::new*//*?}*/);
    public static final RegistryObject</*? if >=1.19 {*/Codec<EntityLootModifier>/*?} else {*//*EntityLootModifier.Serializer*//*?}*/> TEMPLATE_ENTITY_LOOT = LOOT_MODIFIERS.register("entity_loot_modifier", EntityLootModifier./*? if >=1.19 {*/CODEC/*?} else {*//*Serializer::new*//*?}*/);
    //?}

    //?} else {
    /*private static MenuType<SmithingMenuNew> _SMITHING_MENU_NEW;
    *///?}

    public static final Supplier<MenuType<SmithingMenuNew>> SMITHING_MENU_NEW =
            //? if forge {
            GUIS.register("smithing_menu_new", () -> /*? if >=1.17 {*/IForgeMenuType/*?} else {*//*IForgeContainerType*//*?}*/.create(SmithingMenuNew::new))
            //?} else {
            /*() -> _SMITHING_MENU_NEW
            *///?}
    ;

    public static final Supplier<RecipeSerializer<UntrimmingSpecialRecipe>> UNTRIMMING_RECIPE =
            //? if forge {
            RECIPE_TYPES.register(UntrimmingSpecialRecipe.ID.getPath(), () -> UntrimmingSpecialRecipe.Serializer.INSTANCE)
            //?} else {
            /*() -> UntrimmingSpecialRecipe.Serializer.INSTANCE
            *///?}
    ;

    //? if >=1.18.2 {
    public static final TagKey<Item> TRIM_MATERIALS_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID,"trim_materials"));
    public static final TagKey<Item> NON_TRIMMABLE_ITEMS_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID,"non_trimmables"));
    //?} else {
    /*public static final Tag<Item> TRIM_MATERIALS_TAG = /^? if forge {^/ItemTags.createOptional/^?} else {^//^TagRegistry.item^//^?}^/(new ResourceLocation(MODID,"trim_materials"));
    public static final Tag<Item> NON_TRIMMABLE_ITEMS_TAG = /^? if forge {^/ItemTags.createOptional/^?} else {^//^TagRegistry.item^//^?}^/(new ResourceLocation(MODID,"non_trimmables"));
    *///?}

    private static final TrimTextureManager trimTextures = new TrimTextureManager();

    //? if fabric {
    /*@Override
    public void onInitialize() {
    *///?} else {
    public Armortrims() {
    //?}
        Config.registerConfig();

        //? if forge
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Tell the game to register base trims and overlays.
        TrimPatterns.register();
        SmithingTemplateItems.register(/*? if forge {*/bus/*?}*/);
        ItemOverlays.register();

        //? if fabric {
        /*//? if >=1.17 {
        _SMITHING_MENU_NEW = new MenuType<>(SmithingMenuNew::new);
        Registry.register(Registry.MENU, new ResourceLocation(Armortrims.MODID,"smithing_menu_new"), _SMITHING_MENU_NEW);
        //?} else {
        /^_SMITHING_MENU_NEW = ScreenHandlerRegistry.registerSimple(new ResourceLocation(Armortrims.MODID,"smithing_menu_new"), SmithingMenuNew::new);
        ^///?}

        Registry.register(Registry.RECIPE_SERIALIZER, UntrimmingSpecialRecipe.ID, UntrimmingSpecialRecipe.Serializer.INSTANCE);
        *///?}

        //? if forge {
        GUIS.register(bus);
        RECIPE_TYPES.register(bus);
        //? if >=1.17
        LOOT_MODIFIERS.register(bus);

        MinecraftForge.EVENT_BUS.addListener(Armortrims::appendTrimInfo);
        //? if <1.17 {
        /*bus.addGenericListener(GlobalLootModifierSerializer.class, Armortrims::registerLootModifiers);
        *///?}
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Armortrims::onClientSetup);
            bus.addListener(Armortrims::onModelRegister);
            bus.addListener(Armortrims::onModelBake);
            MinecraftForge.EVENT_BUS.addListener(Armortrims::onReloadData);
        }
        //?} else {
        /*ItemTooltipCallback.EVENT.register(Armortrims::appendTrimInfo);

        //? if >=1.18 {
        LootTableEvents.MODIFY
        //?} else {
        /^LootTableLoadingCallback.EVENT
        ^///?}
                .register((resourceManager, lootManager, id, tableBuilder, source) -> {
            //? if >=1.18
            if (source.isBuiltin())
                LootModifiers.register(id,tableBuilder);
        });
        *///?}
    }

    //? if fabric {
    /*@Override
    public void onInitializeClient() {
        //? if >=1.18 {
        MenuScreens
        //?} else {
        /^ScreenRegistry
        ^///?}
                .register(_SMITHING_MENU_NEW, SmithingScreenNew::new);

        //? if >=1.18
        CommonLifecycleEvents.TAGS_LOADED.register(Armortrims::onReloadData);

        ModelLoadingRegistry.INSTANCE.registerModelProvider(Armortrims::onModelRegister);
    }
    *///?}

    public static TrimTextureManager trimTextures() {
        return trimTextures;
    }

    // Add Trim stuff to tooltip.
    //? if forge {
    public static void appendTrimInfo(ItemTooltipEvent event) {
        ItemStack itemstack = event.getItemStack();
        TooltipFlag tooltipFlag = event.getFlags();
        List<Component> list = event.getToolTip();
    //?} else {
    /*private static void appendTrimInfo(ItemStack itemstack, TooltipFlag tooltipFlag, List<Component> list) {
    *///?}
        if (TrimGetter.isTrimmed(itemstack)) {
            String trim = TrimGetter.getPattern(itemstack).toString();
            ResourceLocation material = TrimGetter.getMaterial(itemstack);
            ColorPalette palette = ColorPaletteManager.get(material);
            TextColor color = palette.textColor();

            List<Component> extra = new ArrayList<>(3);
            extra.add(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("tooltip.armor_trims.trim").withStyle(ChatFormatting.GRAY));

            MutableComponent trimName = /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("trims."+trim.replace(':','.'));
            if (tooltipFlag.isAdvanced()) trimName.append(" ("+trim+")");
            trimName.withStyle(trimName.getStyle().withColor(color));
            extra.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(trimName));

            MutableComponent materialName;
            //? if forge {
            if (ForgeRegistries.ITEMS.containsKey(material)) {
                materialName = /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/(ForgeRegistries.ITEMS.getValue(material).getDescriptionId());
            //?} else {
            /*if (Registry.ITEM.containsKey(material)) {
                materialName = /^? if >=1.19 {^/Component.translatable/^?} else {^//^new TranslatableComponent^//^?}^/(Registry.ITEM.get(material).getDescriptionId());
            *///?}
            } else {
                //? if >=1.19 {
                materialName = Component.empty();
                //?} else {
                /*materialName = (TextComponent) TextComponent.EMPTY;
                *///?}
            }
            if (tooltipFlag.isAdvanced()) materialName.append(" ("+material+")");
            materialName.withStyle(materialName.getStyle().withColor(color));
            extra.add(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(" ").append(materialName));

            list.addAll(1,extra);
        }
    }

    //? if forge {
    // Init Forge Menus
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(SMITHING_MENU_NEW.get(), SmithingScreenNew::new);
        if (ModList.get().isLoaded("cloth_config")) event.enqueueWork(() -> {
            ModLoadingContext.get().registerExtensionPoint(/*? if >=1.19 {*/ConfigScreenHandler.ConfigScreenFactory.class/*?} elif >=1.18 {*//*ConfigGuiHandler.ConfigGuiFactory.class*//*?} else {*//*ExtensionPoint.CONFIGGUIFACTORY*//*?}*/, () -> /*? if >=1.19 {*/new ConfigScreenHandler.ConfigScreenFactory/*?} elif >=1.18 {*//*new ConfigGuiHandler.ConfigGuiFactory*//*?}*/((mc, parent) -> ConfigScreen.create(parent)));
        });
    }

    //? if <1.17 {
    /*public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new ChestLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID, "chest_loot_modifier")));
        event.getRegistry().register(new EntityLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID, "entity_loot_modifier")));
    }
    *///?}
    //?}

    // Reload Datapacks
    //? if forge {
    public static void onReloadData(TagsUpdatedEvent event) {
    //?} else {
    /*public static void onReloadData(RegistryAccess registries, boolean client) {
    *///?}
        TrimTextureManager.onReloadData();
        ColorPaletteManager.onReloadData();
    }

    //? if forge {
    // Register Models
    public static void onModelRegister(/*? if >=1.19 {*/ModelEvent.RegisterAdditional/*?} else {*//*ModelRegistryEvent*//*?}*/ event) {
        ItemTrimModels.registerModels(/*? if >=1.19 {*/event::register/*?} elif >=1.18 {*//*ForgeModelBakery::addSpecialModel*//*?} else {*//*ModelLoader::addSpecialModel*//*?}*/);
    }

    // Bake Models
    public static void onModelBake(/*? if >=1.19 {*/ModelEvent.BakingCompleted/*?} else {*//*ModelBakeEvent*//*?}*/ event) {
        //? if >=1.19 {
        ItemTrimModels.bakeModels(event.getModels(), event.getModelBakery());
        //?} else {
        /*ItemTrimModels.bakeModels(event.getModelRegistry(), event.getModelLoader());
        *///?}
    }
    //?} else {
    /*// Register Models
    public static void onModelRegister(ResourceManager manager, Consumer<ResourceLocation> out) {
        ItemTrimModels.registerModels(out);
    }

    // Bake Models
    public static void onModelBake(Map<ResourceLocation, BakedModel> map, ModelBakery bakery) {
        ItemTrimModels.bakeModels(map, bakery);
    }
    *///?}
}
