package gg.hipposgrumm.armor_trims.api;

import com.mojang.datafixers.util.Pair;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate$Upgrade;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This serves as an API class for integration. You can call this class's various methods to create trims, templates, and armor model overrides.
 * <i>Note: If something can't be integrated you expect to, make sure to check the GitHub page, and create a pull request if it isn't implemented for some reason.</i>
 */
public class ArmortrimsApi {
    private static Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> trims = new HashMap<>();
    private static List<Pair<Pair<ResourceLocation, String>, Pair<ResourceLocation, Item.Properties>>> trimitems = new ArrayList<>();
    private static List<Pair<Pair<ResourceLocation, Pair<String, String>>, Pair<Pair<Pair<TagKey<Item>, Item>, Supplier<Boolean>>, Item.Properties>>> upgradeitems = new ArrayList<>();
    //private static Map<ResourceLocation, Item> templateItems = new HashMap<>();
    public static Map<Pair<TagKey<Item>, Item>, Supplier<Boolean>> upgradeBaseBlockedConditions = new HashMap<>();
    private static List<ResourceLocation> itemsList = new ArrayList<>();
    private final String modid;

    /**
     * Must instantiate this class to use anything.
     * I would suggest creating this as a separate (static) variable, but it can be used repeatedly if you so desire.
     * @param modid You must declare your modid here.
     */
    public ArmortrimsApi(String modid) {
        this.modid = modid;
    }

    /**
     * Create a trim that can be attached to an item.
     * @param name Identification name for Trim (will automatically be merged with modid to create ResourceLocation)
     * @param overlayLocation Location for Trim's Armor Texture
     * @param overlaySecondLocation Location for Trim's Secondary Armor Texture (Vanilla Pants)
     */
    public ArmortrimsApi createTrim(String name, ResourceLocation overlayLocation, ResourceLocation overlaySecondLocation) {
        trims.put(prefix(name, modid), new Pair<>(suffix(prefix(overlayLocation, modid).toString(), ".png"), suffix(prefix(overlaySecondLocation, modid).toString(), ".png")));
        return this;
    }

    /**
     * Add materials to the config's default options (if you add support and mod users are too lazy to add them themselves).
     * @param material ResourceLocation of the material to add.
     */
    public ArmortrimsApi addConfigDefault(ResourceLocation material) {
        Config.addCustomMaterial(material.toString());
        return this;
    }

    /**
     * Version of {@link #addConfigDefault(ResourceLocation)} allowing for raw strings.
     * @param material String ResourceLocation of the material to add.
     */
    public ArmortrimsApi addConfigDefault(String material) {
        Config.addCustomMaterial(material);
        return this;
    }

    /**
     * Version of {@link #addConfigDefault(ResourceLocation)} allowing for tag usage.
     * @param material TagKey of the material to add.
     */
    public ArmortrimsApi addConfigDefault(TagKey<Item> material) {
        Config.addCustomMaterial("#"+material.location());
        return this;
    }

    /**
     * Create a smithing template that can apply a trim.
     * @param trim ResourceLocation of the trim associated with the item.
     * @param translatedName Translation key for template subtitle name.
     * @param itemId ID of the item to create.
     */
    public ArmortrimsApi createTrimTemplate(ResourceLocation trim, String translatedName, String itemId) {
        createTrimTemplate(trim, translatedName, itemId, new Item.Properties());
        return this;
    }

    /**
     * Version of {@link #createTrimTemplate(ResourceLocation, String, String)} where you can define item properties.
     */
    public ArmortrimsApi createTrimTemplate(ResourceLocation trim, String translatedName, String itemId, Item.Properties properties) {
        trimitems.add(new Pair<>(new Pair<>(new ResourceLocation(modid, itemId), translatedName), new Pair<>(trim, properties)));
        return this;
    }

    /**
     * Note: Upgrade recipes must be defined as smithing recipes in the datapack!
     * @param tag Item tag that can be used to upgrade.
     * @param blockVanillaOutput Blocks outputs from being created the "vanilla way".
     * @param translatableName Translation key for template subtitle name.
     * @param translatableInput Translation key for template input items (Apply To:)
     * @param itemId ID of the item to create.
     */
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, String translatableName, String translatableInput, String itemId) {
        createUpgradeTemplate(tag, itemRepresentative, blockVanillaOutput, translatableName, translatableInput, itemId, new Item.Properties());
        return this;
    }

    /**
     * Version of {@link #createUpgradeTemplate(TagKey, Item, Supplier, String, String, String)} where you can define item properties.
     */
    public ArmortrimsApi createUpgradeTemplate(TagKey<Item> tag, Item itemRepresentative, Supplier<Boolean> blockVanillaOutput, String translatableName, String translatableInput, String itemId, Item.Properties properties) {
        upgradeitems.add(new Pair<>(new Pair<>(new ResourceLocation(modid, itemId), new Pair<>(translatableName, translatableInput)), new Pair<>(new Pair<>(new Pair<>(tag, itemRepresentative), blockVanillaOutput), properties)));
        return this;
    }

    /**
     * Load all the data for registration.<br>
     * @deprecated You do not need to call this. (In fact, you probably shouldn't.)
     */
    @Deprecated
    public static void loadAll(IEventBus modEventBus) {
        for (ResourceLocation trim:trims.keySet()) {
            Trims.createTrim(trim, trims.get(trim).getFirst(), trims.get(trim).getSecond());
        }
        Map<String, DeferredRegister<Item>> registerMap = new HashMap<>();
        for (Pair<Pair<ResourceLocation, Pair<String, String>>, Pair<Pair<Pair<TagKey<Item>, Item>, Supplier<Boolean>>, Item.Properties>> upgradeItem:upgradeitems) {
            if (!registerMap.containsKey(upgradeItem.getFirst().getFirst().getNamespace())) registerMap.put(upgradeItem.getFirst().getFirst().getNamespace(), DeferredRegister.create(ForgeRegistries.ITEMS, upgradeItem.getFirst().getFirst().getNamespace()));
            registerMap.get(upgradeItem.getFirst().getFirst().getNamespace()).register(upgradeItem.getFirst().getFirst().getPath(), () -> new SmithingTemplate$Upgrade(upgradeItem.getSecond().getFirst().getFirst().getFirst(), upgradeItem.getSecond().getFirst().getFirst().getSecond(), upgradeItem.getFirst().getSecond().getFirst(), upgradeItem.getFirst().getSecond().getSecond(), upgradeItem.getSecond().getSecond()));
            upgradeBaseBlockedConditions.put(upgradeItem.getSecond().getFirst().getFirst(), upgradeItem.getSecond().getFirst().getSecond());
            itemsList.add(upgradeItem.getFirst().getFirst());
        }
        for (Pair<Pair<ResourceLocation, String>, Pair<ResourceLocation, Item.Properties>> trimitem:trimitems) {
            if (!registerMap.containsKey(trimitem.getFirst().getFirst().getNamespace())) registerMap.put(trimitem.getFirst().getFirst().getNamespace(), DeferredRegister.create(ForgeRegistries.ITEMS, trimitem.getFirst().getFirst().getNamespace()));
            registerMap.get(trimitem.getFirst().getFirst().getNamespace()).register(trimitem.getFirst().getFirst().getPath(), () -> new SmithingTemplate(trimitem.getSecond().getFirst(), trimitem.getFirst().getSecond(), trimitem.getSecond().getSecond()));
            itemsList.add(trimitem.getFirst().getFirst());
        }
        for (DeferredRegister<Item> registry:registerMap.values()) {
            registry.register(modEventBus);
        }
    }

    @Mod.EventBusSubscriber(modid = Armortrims.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CreativeTabRegistry {
        @SubscribeEvent
        public static void doCreativeTabs(CreativeModeTabEvent.BuildContents event) {
            for (ResourceLocation item:itemsList) {
                if (event.getTab().equals(CreativeModeTabs.INGREDIENTS)) event.accept(getItem(item));
            }
        }
    }

    /**
     * Hook to get armor trim items.
     * @param itemId Item to get.
     * @return Item specified if it is present, otherwise air.
     */
    public static Item getItem(ResourceLocation itemId) {
        Item target = Items.AIR;
        for (Item item:LargeItemLists.getAllSmithingTemplates()) {
            if (ForgeRegistries.ITEMS.getKey(item).equals(itemId)) target = item;
        }
        return target;
    }

    /** API Util */
    private ResourceLocation suffix(String input, String suffix) {
        return input.endsWith(suffix) ? new ResourceLocation(input) : new ResourceLocation(input + suffix);
    }

    /** API Util */
    private ResourceLocation prefix(String input, String prefix) {
        return input.startsWith(prefix)?new ResourceLocation(input):new ResourceLocation(prefix, input);
    }

    /** API Util */
    private ResourceLocation prefix(ResourceLocation input, String prefix) {
        return prefix(input.getPath(), prefix);
    }
}
