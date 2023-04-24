package gg.hipposgrumm.armor_trims.model;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Code copied from Botania; modified for my own purpose (selfish, I know).
 */
public class TrimDecorationBaker {
    public static final TrimDecorationBaker INSTANCE = new TrimDecorationBaker();
    private static final Logger LOGGER = LogUtils.getLogger();

    public boolean registeredModels = false;
    public BakedModel helmet;
    public BakedModel chestplate;
    public BakedModel leggings;
    public BakedModel boots;
    public BakedModel other;

    public void registerModels(Consumer<ResourceLocation> consumer) {
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/helmet_trim"));
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/chestplate_trim"));
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/leggings_trim"));
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/boots_trim"));
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim"));
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/empty"));

        registeredModels = true;
    }

    public void bakeModels(Map<ResourceLocation, BakedModel> map) {
        if (!registeredModels) {
            LOGGER.error("Additional models failed to register! Aborting baking models to avoid early crashing.");
            return;
        }
        helmet = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/helmet_trim"));
        chestplate = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/chestplate_trim"));
        leggings = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/leggings_trim"));
        boots = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/boots_trim"));
        other = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim"));
        //other = map.get(new ResourceLocation(Main.MODID, "item/overlay/empty"));
    }

    @Mod.EventBusSubscriber(modid = Armortrims.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModelBakeryInitializer {
        @SubscribeEvent
        public static void onModelBake(ModelBakeEvent evt) {
            INSTANCE.bakeModels(evt.getModelRegistry());
        }

        @SubscribeEvent
        public static void onModelRegister(ModelRegistryEvent evt) {
            INSTANCE.registerModels(ForgeModelBakery::addSpecialModel);
        }
    }
}
