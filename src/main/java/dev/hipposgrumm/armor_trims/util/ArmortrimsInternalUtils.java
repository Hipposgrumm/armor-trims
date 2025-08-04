package dev.hipposgrumm.armor_trims.util;

//? if >=1.18 {
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
//? if fabric
/^import net.fabricmc.fabric.api.tag.TagRegistry;^/
*///?}
import net.minecraft.world.item.Item;

//? if fabric {
/*import net.fabricmc.loader.api.FabricLoader;
//? if >=1.18.2
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
*///?} else {
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.loading.FMLLoader;
//?}

// Stuff I don't where else to put.
public class ArmortrimsInternalUtils {
    //? if >=1.18.2 {
    public static final TagKey<Item> SHEARS_TAG = /*? if fabric {*//*ConventionalItemTags.SHEARS;*//*?} else {*/Tags.Items.SHEARS/*?}*/;
    public static final TagKey<Item> NETHERITE_TAG = /*? if fabric {*//*ConventionalItemTags.NETHERITE_INGOTS*//*?} else {*/Tags.Items.INGOTS_NETHERITE/*?}*/;
    //?} else {
    /*public static final Tag<Item> SHEARS_TAG = /^? if fabric {^//^TagRegistry.item(new ResourceLocation("c:tools/shears"))^//^?} else {^/Tags.Items.SHEARS/^?}^/;
    public static final Tag<Item> NETHERITE_TAG = /^? if fabric {^//^TagRegistry.item(new ResourceLocation("c:ingots/netherite"))^//^?} else {^/Tags.Items.INGOTS_NETHERITE/^?}^/;
    *///?}

    public static boolean dev() {
        //? if forge {
        return !FMLLoader.isProduction();
        //?} else {
        /*return FabricLoader.getInstance().isDevelopmentEnvironment();
        *///?}
    }
}
