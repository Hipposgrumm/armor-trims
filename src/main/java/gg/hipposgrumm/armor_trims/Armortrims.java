package gg.hipposgrumm.armor_trims;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.gui.SmithingMenuNew;
import gg.hipposgrumm.armor_trims.gui.SmithingScreenNew;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.loot.ChestLootModifier;
import gg.hipposgrumm.armor_trims.loot.EntityLootModifier;
import gg.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.GetAvgColor;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
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

import java.util.List;

@Mod(Armortrims.MODID)
public class Armortrims {
    public static final String MODID = "armor_trims";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> SMITHING_TEMPLATES = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> SMITHING_TEMPLATES_SPECIAL_NETHERITEUPGRADE = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<MenuType<?>> NEW_SMITHING_MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> TRIMMING_RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> TEMPLATES_LOOT_SPAWNER = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Item> NETHERITE_UPGRADE = SMITHING_TEMPLATES_SPECIAL_NETHERITEUPGRADE.register("netherite_upgrade_smithing_template", () -> new SmithingTemplate(Trims.NETHERITE_UPGRADE, new Item.Properties()));
    public static final RegistryObject<Item> COAST_ARMOR_TRIM = SMITHING_TEMPLATES.register("coast_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.COAST, new Item.Properties()));
    public static final RegistryObject<Item> DUNE_ARMOR_TRIM = SMITHING_TEMPLATES.register("dune_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.DUNE, new Item.Properties()));
    public static final RegistryObject<Item> EYE_ARMOR_TRIM = SMITHING_TEMPLATES.register("eye_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.EYE, new Item.Properties()));
    public static final RegistryObject<Item> HOST_ARMOR_TRIM = SMITHING_TEMPLATES.register("host_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.HOST, new Item.Properties()));
    public static final RegistryObject<Item> RAISER_ARMOR_TRIM = SMITHING_TEMPLATES.register("raiser_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.RAISER, new Item.Properties()));
    public static final RegistryObject<Item> RIB_ARMOR_TRIM = SMITHING_TEMPLATES.register("rib_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.RIB, new Item.Properties()));
    public static final RegistryObject<Item> SENTRY_ARMOR_TRIM = SMITHING_TEMPLATES.register("sentry_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.SENTRY, new Item.Properties()));
    public static final RegistryObject<Item> SHAPER_ARMOR_TRIM = SMITHING_TEMPLATES.register("shaper_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.SHAPER, new Item.Properties()));
    public static final RegistryObject<Item> SILENCE_ARMOR_TRIM = SMITHING_TEMPLATES.register("silence_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.SILENCE, new Item.Properties()));
    public static final RegistryObject<Item> SNOUT_ARMOR_TRIM = SMITHING_TEMPLATES.register("snout_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.SNOUT, new Item.Properties()));
    public static final RegistryObject<Item> SPIRE_ARMOR_TRIM = SMITHING_TEMPLATES.register("spire_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.SPIRE, new Item.Properties()));
    public static final RegistryObject<Item> TIDE_ARMOR_TRIM = SMITHING_TEMPLATES.register("tide_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.TIDE, new Item.Properties()));
    public static final RegistryObject<Item> VEX_ARMOR_TRIM = SMITHING_TEMPLATES.register("vex_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.VEX, new Item.Properties()));
    public static final RegistryObject<Item> WARD_ARMOR_TRIM = SMITHING_TEMPLATES.register("ward_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.WARD, new Item.Properties()));
    public static final RegistryObject<Item> WAYFINDER_ARMOR_TRIM = SMITHING_TEMPLATES.register("wayfinder_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.WAYFINDER, new Item.Properties()));
    public static final RegistryObject<Item> WILD_ARMOR_TRIM = SMITHING_TEMPLATES.register("wild_armor_trim_smithing_template", () -> new SmithingTemplate(Trims.WILD, new Item.Properties()));

    public static final RegistryObject<MenuType<SmithingMenuNew>> SMITHING_MENU_NEW = NEW_SMITHING_MENUS.register("smithing_menu_new", () -> IForgeMenuType.create(SmithingMenuNew::new));

    public static final RegistryObject<RecipeSerializer<UntrimmingSpecialRecipe>> UNTRIMMING_RECIPE = TRIMMING_RECIPES.register("crafting_special_untrimming", () -> UntrimmingSpecialRecipe.Serializer.INSTANCE); // TODO: Serialize UntrimmingSpecialRecipe with a proper serializer like SmithingRecipeNew.

    public static final RegistryObject<ChestLootModifier.Serializer> TEMPLATE_CHEST_LOOT = TEMPLATES_LOOT_SPAWNER.register("chest_loot_modifier", ChestLootModifier.Serializer::new);
    public static final RegistryObject<EntityLootModifier.Serializer> TEMPLATE_ENTITY_LOOT = TEMPLATES_LOOT_SPAWNER.register("entity_loot_modifier", EntityLootModifier.Serializer::new);

    public static final TagKey<Item> smithing_templates = ItemTags.create(new ResourceLocation(Armortrims.MODID,"armor_trims"));

    public Armortrims() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (FMLEnvironment.dist.isDedicatedServer()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.COMMON_SERVER_SPEC, "armor_trims.toml");
        } else {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC, "armor_trims.toml");
        }

        SMITHING_TEMPLATES.register(modEventBus);
        NEW_SMITHING_MENUS.register(modEventBus);
        TRIMMING_RECIPES.register(modEventBus);
        TEMPLATES_LOOT_SPAWNER.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus2 = FMLJavaModLoadingContext.get().getModEventBus();
        if (!Config.disableNetheriteUpgrade()) {
            SMITHING_TEMPLATES_SPECIAL_NETHERITEUPGRADE.register(modEventBus2);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Armortrims.MODID)
    public static class VisualModEvent {
        @SubscribeEvent
        public static void appendTrimInfo(ItemTooltipEvent event) {
            ItemStack itemstack = event.getItemStack();
            if (TrimmableItem.isTrimmed(itemstack)) {
                List<Component> list = event.getToolTip();
                list.add(1,new TranslatableComponent("tooltip.armor_trims.trim").withStyle(ChatFormatting.GRAY));
                TextColor color = TextColor.fromRgb(TrimmableItem.getMaterialColor(itemstack));
                TranslatableComponent trimName = new TranslatableComponent("trims.armor_trims." + TrimmableItem.getTrim(itemstack));
                trimName.withStyle(trimName.getStyle().withColor(color));
                list.add(2,new net.minecraft.network.chat.TextComponent(" ").append(trimName));
                TranslatableComponent materialName = new TranslatableComponent(ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(itemstack)).getDescriptionId());
                materialName.withStyle(materialName.getStyle().withColor(color));
                list.add(3,new TextComponent(" ").append(materialName));
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
                    GetAvgColor.buildDefaults();
                }
            });
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(Armortrims.SMITHING_MENU_NEW.get(), SmithingScreenNew::new);
        }

        @SubscribeEvent
        public static void loadCompleted(FMLLoadCompleteEvent event) {
            GetAvgColor.isGameLoaded = true;
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            LargeItemLists.setAllArmors();
            LargeItemLists.setAllTemplates();
            LargeItemLists.setAllTrimTemplates();
        }
    }
}
