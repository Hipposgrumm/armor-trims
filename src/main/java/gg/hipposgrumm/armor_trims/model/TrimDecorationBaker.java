package gg.hipposgrumm.armor_trims.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Code copied from Botania; modified for my own purpose (selfish, I know).
 */
public class TrimDecorationBaker {
    public static final TrimDecorationBaker INSTANCE = new TrimDecorationBaker();

    public boolean registeredModels = false;
    public Map<Item, BakedModel> customModels = new HashMap<>();
    private List<Pair<Item, ResourceLocation>> customModelsToLoad = new ArrayList<>();
    public BakedModel other;

    public static void addModel(Item item, ResourceLocation modelResource) {
        INSTANCE.customModelsToLoad.add(new Pair<>(item, modelResource));
    }

    public void registerModels(Consumer<ResourceLocation> consumer) {
        consumer.accept(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim"));

        List<ResourceLocation> addedModels = new ArrayList<>() {{
            add(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim"));
        }};
        for (Pair<Item, ResourceLocation> itemModel:customModelsToLoad) {
            if (addedModels.contains(itemModel.getSecond())) continue;
            consumer.accept(itemModel.getSecond());
            addedModels.add(itemModel.getSecond());
        }

        registeredModels = true;
    }

    public void bakeModels(Map<ResourceLocation, BakedModel> map) {
        if (!registeredModels) {
            LogUtils.getLogger().error("Additional models failed to register! Aborting baking models to avoid early crashing.");
            return;
        }

        other = map.get(new ResourceLocation(Armortrims.MODID, "item/overlay/other_trim"));

        for (Pair<Item, ResourceLocation> itemModel:customModelsToLoad) {
            customModels.put(itemModel.getFirst(), map.get(itemModel.getSecond()));
        }
    }

    public BakedModel getModel(Item item) {
        return customModels.getOrDefault(item, other);
    }

    @Mod.EventBusSubscriber(modid = Armortrims.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModelBakeryInitializer {
        @SubscribeEvent
        public static void onModelBake(ModelEvent.BakingCompleted evt) {
            INSTANCE.bakeModels(evt.getModels());
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterAdditional evt) {
            INSTANCE.registerModels(evt::register);
        }
    }
}
