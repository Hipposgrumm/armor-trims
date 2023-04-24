package gg.hipposgrumm.armor_trims.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public final class Config {
    private static final List<String> trimmableMaterialsList = Arrays.asList("#forge:ingots/gold","#forge:ingots/iron","#forge:ingots/copper","#forge:ingots/netherite","#forge:gems/emerald","#forge:gems/amethyst","#forge:dusts/redstone","#forge:gems/lapis","#forge:gems/quartz","#forge:gems/diamond","#forge:ingots/manasteel","#forge:ingots/elementium","#forge:ingots/terrasteel","#forge:gems/mana_diamond","#forge:gems/dragonstone", "#forge:ingots/zinc","create:polished_rose_quartz");
    public static class As_Client {
        final ForgeConfigSpec.BooleanValue enableNewSmithingGUI;
        final ForgeConfigSpec.BooleanValue dontConsumeSmithingTemplate;
        final ForgeConfigSpec.BooleanValue disableNetheriteUpgrade;
        final ForgeConfigSpec.BooleanValue disableVanillaNetheriteUpgrade;
        final ForgeConfigSpec.BooleanValue compressItemsInTemplateTooltip;
        final ForgeConfigSpec.ConfigValue<String> customArmorModelHandling;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> trimmableMaterials;
        final ForgeConfigSpec.BooleanValue enableUntrimming;

        private As_Client(ForgeConfigSpec.Builder builder) {
            builder.push("Client");
            compressItemsInTemplateTooltip = builder
                    .comment(" Condense item names based on tags in the tooltip of Smithing Templates.")
                    .define("Condense Item Names", true);
            customArmorModelHandling = builder
                    .comment(" For some reason, trim layers don't play nice with custom armor models (and I have given up trying to fix it now). You can choose what to do.\n NORMAL - Will attempt to render anyway. Usually trims will be invisible but sometimes yields wierd results (such as \"mystery belts\" for some boots).\n TINTED - Will tint armors the associated trim color.\n HIDDEN - Will hide trims on custom armor models entirely. Default option; anything not specified will default to this.")
                    .define("Custom Armor Model View", "HIDDEN");
            builder.pop();

            builder.push("Base Features");
            enableNewSmithingGUI = builder
                    .comment(" Basically half the premise of the mod, turn this off if you don't want people trimming armor themselves (E.G. if you have another system on place).")
                    .define("New Smithing Table GUI", true);
            dontConsumeSmithingTemplate = builder
                    .comment(" Don't consume smithing templates when trimming armor.")
                    .define("Don't Consume Smithing Templates", false);
            disableVanillaNetheriteUpgrade = builder
                    .comment(" Disable vanilla netherite upgrading in favor of the new one. NOT recommended to enable this alongside disableNetheriteUpgrade.")
                    .define("Disable Vanilla Netherite Upgrading", false);
            disableNetheriteUpgrade = builder
                    .comment(" Disable netherite upgrading via smithing template. Great if you feel like it takes away from the old vanilla experience. NOT recommended to enable this alongside disableVanillaNetheriteUpgrade")
                    .define("Disable Netherite Upgrade Smithing Template", false);
            builder.push("Untrimming");
                enableUntrimming = builder
                        .comment(" Decides whether or not the player is allowed to un-trim armor.")
                        .define("Allow Untrimming", true);
            builder.pop();

            builder.push("Armor Trim Materials");
            trimmableMaterials = builder
                    .comment(" This is a list of items that can be used to trim armor.\n You can add any item/tag here. For example, you could use the item id \"minecraft:sponge\" for sponge or you could use the tag \"#forge:gems/amethyst\" for amethyst shards.")
                    .defineList("Armor Trim Materials", trimmableMaterialsList, entry -> true); // TODO: Complete this list with common metals and ores.
            builder.pop();
        }
    }

    public static class As_Server {
        final ForgeConfigSpec.BooleanValue enableNewSmithingGUI;
        final ForgeConfigSpec.BooleanValue dontConsumeSmithingTemplate;
        final ForgeConfigSpec.BooleanValue disableNetheriteUpgrade;
        final ForgeConfigSpec.BooleanValue disableVanillaNetheriteUpgrade;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> trimmableMaterials;
        final ForgeConfigSpec.BooleanValue enableUntrimming;

        private As_Server(ForgeConfigSpec.Builder builder) {
            builder.push("Base Features");
            enableNewSmithingGUI = builder
                    .comment(" Basically half the premise of the mod, turn this off if you don't want people trimming armor themselves (if you want to charge money for trims or have another system on place).")
                    .define("New Smithing Table GUI", true);
            dontConsumeSmithingTemplate = builder
                    .comment(" Don't consume smithing templates when crafting.")
                    .define("Don't Consume Smithing Templates", false);
            disableVanillaNetheriteUpgrade = builder
                    .comment(" Disable vanilla netherite upgrading in favor of the new one. NOT recommended to enable this alongside disableNetheriteUpgrade.")
                    .define("Disable Vanilla Netherite Upgrading", false);
            disableNetheriteUpgrade = builder
                    .comment(" Disable netherite upgrading via smithing template. Great if you feel like it takes away from the old vanilla experience. NOT recommended to enable this alongside disableVanillaNetheriteUpgrade")
                    .define("Disable Netherite Upgrade Smithing Template", false);
            builder.push("Untrimming");
            enableUntrimming = builder
                    .comment(" Decides whether or not the player is allowed to un-trim armor.")
                    .define("Allow Untrimming", true);
            builder.pop();

            builder.push("Armor Trim Materials");
            trimmableMaterials = builder
                    .comment(" This is a list of items that can be used to trim armor.\n You can add any item/tag here by adding \"minecraft:quartz\" for items (in this case a sponge) or \"#forge:gems/amethyst\" for tags (in this case, amethyst shards)")
                    .defineList("Armor Trim Materials", trimmableMaterialsList, entry -> true); // TODO: Complete this list with common metals and ores.
            builder.pop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static String customArmorModelHandling() {
        return Config.COMMON.customArmorModelHandling.get();
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean compressItemNamesInTemplateTooltip() {
        return Config.COMMON.compressItemsInTemplateTooltip.get();
    }
    public static boolean enableNewSmithingGUI() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.enableNewSmithingGUI.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.enableNewSmithingGUI.get();
        } else {
            return true;
        }
    }
    public static boolean dontConsumeSmithingTemplates() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.dontConsumeSmithingTemplate.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.dontConsumeSmithingTemplate.get();
        } else {
            return false;
        }
    }
    public static boolean disableVanillaNetheriteUpgrade() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.disableVanillaNetheriteUpgrade.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.disableVanillaNetheriteUpgrade.get();
        } else {
            return false;
        }
    }
    public static boolean disableNetheriteUpgrade() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.disableNetheriteUpgrade.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.disableNetheriteUpgrade.get();
        } else {
            return false;
        }
    }
    public static boolean enableUntrimming() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.enableUntrimming.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.enableUntrimming.get();
        } else {
            return true;
        }
    }
    public static List<? extends String> trimmableMaterials() {
        if (FMLEnvironment.dist.isClient()) {
            return Config.COMMON.trimmableMaterials.get();
        } else if (FMLEnvironment.dist.isDedicatedServer()) {
            return Config.COMMON_SERVER.trimmableMaterials.get();
        } else {
            return List.of(new String[]{});
        }
    }

    public static final As_Client COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final As_Server COMMON_SERVER;
    public static final ForgeConfigSpec COMMON_SERVER_SPEC;

    static {
        Pair<As_Client, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(As_Client::new);
        Pair<As_Server, ForgeConfigSpec> commonServerSpecPair = new ForgeConfigSpec.Builder().configure(As_Server::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON_SERVER = commonServerSpecPair.getLeft();
        COMMON_SERVER_SPEC = commonServerSpecPair.getRight();
    }
}