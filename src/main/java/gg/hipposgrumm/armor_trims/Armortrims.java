package gg.hipposgrumm.armor_trims;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import gg.hipposgrumm.armor_trims.api.ArmortrimsApi;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import gg.hipposgrumm.armor_trims.gui.SmithingScreenNew;
import gg.hipposgrumm.armor_trims.loot.ChestLootModifier;
import gg.hipposgrumm.armor_trims.loot.EntityLootModifier;
import gg.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.*;

@Mod(Armortrims.MODID)
public class Armortrims {
    public static final String MODID = "armor_trims";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<MenuType<?>> NEW_SMITHING_MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> TRIMMING_RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> TEMPLATES_LOOT_SPAWNER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<MenuType<SmithingMenuNew>> SMITHING_MENU_NEW = NEW_SMITHING_MENUS.register("smithing_menu_new", () -> IForgeMenuType.create(SmithingMenuNew::new));
    public static final RegistryObject<RecipeSerializer<UntrimmingSpecialRecipe>> UNTRIMMING_RECIPE = TRIMMING_RECIPES.register("crafting_special_untrimming", () -> UntrimmingSpecialRecipe.Serializer.INSTANCE);

    public static final RegistryObject<Codec<ChestLootModifier>> CHEST_LOOT_MODIFIER = TEMPLATES_LOOT_SPAWNER.register("chest_loot_modifier", ChestLootModifier.CODEC);
    public static final RegistryObject<Codec<EntityLootModifier>> ENTITY_LOOT_MODIFIER = TEMPLATES_LOOT_SPAWNER.register("entity_loot_modifier", EntityLootModifier.CODEC);

    public static final TagKey<Item> smithing_templates = ItemTags.create(new ResourceLocation(Armortrims.MODID,"armor_trims"));

    @SuppressWarnings("deprecation")
    public Armortrims() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.COMMON_SERVER_SPEC, "armor_trims.toml");
        } else {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC, "armor_trims.toml");
        }

        new ArmortrimsApi(MODID)
                .createUpgradeTemplate(Tags.Items.INGOTS_NETHERITE, Items.DIAMOND, Config::disableVanillaNetheriteUpgrade, "trims.armor_trims.netherite_upgrade", "tooltip.armor_trims.applyTo.diamond_equipment", "netherite_upgrade_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "coast"), "trims.armor_trims.coast", "coast_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "dune"), "trims.armor_trims.dune", "dune_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "eye"), "trims.armor_trims.eye", "eye_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "host"), "trims.armor_trims.host", "host_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "raiser"), "trims.armor_trims.raiser", "raiser_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "rib"), "trims.armor_trims.rib", "rib_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "sentry"), "trims.armor_trims.sentry", "sentry_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "shaper"), "trims.armor_trims.shaper", "shaper_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "silence"), "trims.armor_trims.silence", "silence_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "snout"), "trims.armor_trims.snout", "snout_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "spire"), "trims.armor_trims.spire", "spire_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "tide"), "trims.armor_trims.tide", "tide_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "vex"), "trims.armor_trims.vex", "vex_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "ward"), "trims.armor_trims.ward", "ward_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "wayfinder"), "trims.armor_trims.wayfinder", "wayfinder_armor_trim_smithing_template")
                .createTrimTemplate(new ResourceLocation(MODID, "wild"), "trims.armor_trims.wild", "wild_armor_trim_smithing_template")

                .createTrim("coast", new ResourceLocation(MODID, "textures/trims/models/armor/coast.png"), new ResourceLocation(MODID, "textures/trims/models/armor/coast_leggings.png"))
                .createTrim("dune", new ResourceLocation(MODID, "textures/trims/models/armor/dune.png"), new ResourceLocation(MODID, "textures/trims/models/armor/dune_leggings.png"))
                .createTrim("eye", new ResourceLocation(MODID, "textures/trims/models/armor/eye.png"), new ResourceLocation(MODID, "textures/trims/models/armor/eye_leggings.png"))
                .createTrim("host", new ResourceLocation(MODID, "textures/trims/models/armor/host.png"), new ResourceLocation(MODID, "textures/trims/models/armor/host_leggings.png"))
                .createTrim("raiser", new ResourceLocation(MODID, "textures/trims/models/armor/raiser.png"), new ResourceLocation(MODID, "textures/trims/models/armor/raiser_leggings.png"))
                .createTrim("rib", new ResourceLocation(MODID, "textures/trims/models/armor/rib.png"), new ResourceLocation(MODID, "textures/trims/models/armor/rib_leggings.png"))
                .createTrim("sentry", new ResourceLocation(MODID, "textures/trims/models/armor/sentry.png"), new ResourceLocation(MODID, "textures/trims/models/armor/sentry_leggings.png"))
                .createTrim("shaper", new ResourceLocation(MODID, "textures/trims/models/armor/shaper.png"), new ResourceLocation(MODID, "textures/trims/models/armor/shaper_leggings.png"))
                .createTrim("silence", new ResourceLocation(MODID, "textures/trims/models/armor/silence.png"), new ResourceLocation(MODID, "textures/trims/models/armor/silence_leggings.png"))
                .createTrim("snout", new ResourceLocation(MODID, "textures/trims/models/armor/snout.png"), new ResourceLocation(MODID, "textures/trims/models/armor/snout_leggings.png"))
                .createTrim("spire", new ResourceLocation(MODID, "textures/trims/models/armor/spire.png"), new ResourceLocation(MODID, "textures/trims/models/armor/spire_leggings.png"))
                .createTrim("tide", new ResourceLocation(MODID, "textures/trims/models/armor/tide.png"), new ResourceLocation(MODID, "textures/trims/models/armor/tide_leggings.png"))
                .createTrim("vex", new ResourceLocation(MODID, "textures/trims/models/armor/vex.png"), new ResourceLocation(MODID, "textures/trims/models/armor/vex_leggings.png"))
                .createTrim("ward", new ResourceLocation(MODID, "textures/trims/models/armor/ward.png"), new ResourceLocation(MODID, "textures/trims/models/armor/ward_leggings.png"))
                .createTrim("wayfinder", new ResourceLocation(MODID, "textures/trims/models/armor/wayfinder.png"), new ResourceLocation(MODID, "textures/trims/models/armor/wayfinder_leggings.png"))
                .createTrim("wild", new ResourceLocation(MODID, "textures/trims/models/armor/wild.png"), new ResourceLocation(MODID, "textures/trims/models/armor/wild_leggings.png"))

                .addConfigDefault(Tags.Items.INGOTS_GOLD)
                .addConfigDefault(Tags.Items.INGOTS_IRON)
                .addConfigDefault(Tags.Items.INGOTS_COPPER)
                .addConfigDefault(Tags.Items.INGOTS_NETHERITE)
                .addConfigDefault(Tags.Items.GEMS_EMERALD)
                .addConfigDefault(Tags.Items.GEMS_AMETHYST)
                .addConfigDefault(Tags.Items.DUSTS_REDSTONE)
                .addConfigDefault(Tags.Items.GEMS_LAPIS)
                .addConfigDefault(Tags.Items.GEMS_QUARTZ)
                .addConfigDefault(Tags.Items.GEMS_DIAMOND)
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "ingots/manasteel")))
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "ingots/elementium")))
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "ingots/terrasteel")))
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "gems/mana_diamond")))
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "gems/dragonstone")))
                .addConfigDefault(ItemTags.create(new ResourceLocation("forge", "ingots/zinc")))
                .addConfigDefault("create:polished_rose_quartz");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ArmortrimsApi.loadAll(modEventBus);
        NEW_SMITHING_MENUS.register(modEventBus);
        TRIMMING_RECIPES.register(modEventBus);
        TEMPLATES_LOOT_SPAWNER.register(modEventBus);


        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Armortrims.MODID)
    public static class VisualModEvent {
        @SubscribeEvent
        public static void appendTrimInfo(ItemTooltipEvent event) {
            ItemStack itemstack = event.getItemStack();
            if (TrimmableItem.isTrimmed(itemstack)) {
                List<Component> list = event.getToolTip();
                list.add(1, Component.translatable("tooltip.armor_trims.trim").withStyle(ChatFormatting.GRAY));
                TextColor color = TextColor.fromRgb(TrimmableItem.getMaterialColor(itemstack));
                MutableComponent trimName = Component.translatable("trims."+TrimmableItem.getTrim(itemstack).getNamespace()+"." + TrimmableItem.getTrim(itemstack).getPath());
                trimName.withStyle(trimName.getStyle().withColor(color));
                list.add(2,Component.literal(" ").append(trimName));
                MutableComponent materialName = Component.translatable(ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(itemstack)).getDescriptionId());
                materialName.withStyle(materialName.getStyle().withColor(color));
                list.add(3,Component.literal(" ").append(materialName));
            }
        }
    }


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void reloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new SimplePreparableReloadListener<Unit>() {

                @Override
                protected Unit prepare(ResourceManager p_10796_, ProfilerFiller p_10797_) {
                    return Unit.INSTANCE;
                }

                @Override
                protected void apply(Unit p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
                    if (FMLEnvironment.dist.isClient()) GetAvgColor.buildDefaults();
                }
            });
        }

        /*@SuppressWarnings({"rawtypes", "unchecked"})
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event) {
            for (EntityType entityType : ForgeRegistries.ENTITIES) {
                try {
                    addLayerToHumanoid(event, entityType, TrimRenderLayer::new);
                } catch(ClassCastException ignored) {}
            }
        }

        /**
         * Borrowed from <a href="https://www.curseforge.com/minecraft/mc-mods/tool-belt">Tool Belt</a>.
         * @author Gigaherz
         *
        @SuppressWarnings({"rawtypes", "unchecked"})
        private static <E extends Player, M extends HumanoidModel<E>> void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName, Function<LivingEntityRenderer<E, M>, ? extends RenderLayer<E, M>> factory) {
            LivingEntityRenderer renderer = event.getSkin(skinName);
            if (renderer != null) renderer.addLayer(factory.apply(renderer));
        }

        /**
         * Also borrowed from <a href="https://www.curseforge.com/minecraft/mc-mods/tool-belt">Tool Belt</a>.
         * @author Gigaherz
         *
        private static <E extends LivingEntity, M extends HumanoidModel<E>> void addLayerToHumanoid(EntityRenderersEvent.AddLayers event, EntityType<E> entityType, Function<LivingEntityRenderer<E, M>, ? extends RenderLayer<E, M>> factory) {
            LivingEntityRenderer<E, M> renderer = event.getRenderer(entityType);
            if (renderer != null) renderer.addLayer(factory.apply(renderer));
        }*/

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(Armortrims.SMITHING_MENU_NEW.get(), SmithingScreenNew::new);
        }

        @SubscribeEvent
        public static void loadCompleted(FMLLoadCompleteEvent event) {
            if (FMLEnvironment.dist.isClient()) GetAvgColor.isGameLoaded = true;
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            LargeItemLists.setAllArmors();
            LargeItemLists.setAllTemplates();
            LargeItemLists.setAllUpgradeTemplates();
            LargeItemLists.setAllTrimTemplates();
        }
    }
}
