package gg.hipposgrumm.armor_trims.util;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GetAvgColor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int color = 16777215;
    private static Map<String, Integer> colorList = new HashMap<>();
    public static boolean isGameLoaded = false;

    private GetAvgColor() {}

    public GetAvgColor(ResourceLocation location) {
        this(location.toString());
    }

    public GetAvgColor(String location) {
        if (isGameLoaded) {
            if (colorList.containsKey(location)) {
                color = colorList.get(location);
            } else {
                addColorEntry(location);
            }
        }
    }

    public void addColorEntry(String resourceLocation) {
        // TODO: https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.19/src/main/java/com/buuz135/industrial/utils/ItemStackUtils.java
        ResourceLocation trueResourceLocation = resourceLocation.startsWith("#")?new AssociateTagsWithItems(resourceLocation).getItems()[0].getRegistryName():new ResourceLocation(resourceLocation);
        try {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            ItemModelShaper itemModelMesher = itemRenderer.getItemModelShaper();
            BakedModel itemModel = itemModelMesher.getItemModel(new ItemStack(ForgeRegistries.ITEMS.getValue(trueResourceLocation)));
            TextureAtlasSprite particleTexture = itemModel.getParticleIcon();
            if (particleTexture instanceof MissingTextureAtlasSprite) {

            }
            TextureAtlasSprite texture = particleTexture;
            /*
            AbstractTexture atlas = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            TextureAtlasSprite texture;
            LOGGER.debug("True Resource Location: "+trueResourceLocation);
            texture = ((TextureAtlas) atlas).getSprite(trueResourceLocation);
            //if (texture instanceof MissingTextureAtlasSprite) throw new RuntimeException();
            */
            try { // TODO: https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.19/src/main/java/com/buuz135/industrial/utils/ColorUtils.java
                if (texture == null || texture.getFrameCount() <= 0) throw new IOException();
                long[] colorVals = new long[]{0, 0, 0, 0};
                int size = 0;
                for (int x = 0; x < texture.getWidth(); x++) {
                    for (int y = 0; y < texture.getHeight(); y++) {
                        int pixel = texture.getPixelRGBA(0, x, y);
                        int borrowedAlpha =  pixel >> 24 & 0xFF;
                        if (borrowedAlpha >= 5) {
                            colorVals[0] += borrowedAlpha;
                            colorVals[1] += (pixel >> 0 & 0xFF);
                            colorVals[2] += (pixel >> 8 & 0xFF);
                            colorVals[3] += (pixel >> 16 & 0xFF);
                            size++;
                        }
                    }
                }
                for (int i=0;i<colorVals.length;i++) {
                    colorVals[i] = Math.round(colorVals[i]/size);
                    colorVals[i] = i!=0?colorVals[i]>=235?255:colorVals[i]+20:colorVals[i];
                }
                color = FastColor.ARGB32.color(255, (int) colorVals[1], (int) colorVals[2], (int) colorVals[3]);
                colorList.put(resourceLocation, color);
                LOGGER.debug("Extracted color " + color + " from " + texture + " (" + resourceLocation + ")");
            } catch (IOException e) {
                LOGGER.error("Unable to find color for " + resourceLocation + ".");
            }
        } catch (ClassCastException e) {
            LOGGER.error("Unable to load location for "+resourceLocation);
        } catch (RuntimeException e) {
            LOGGER.error("Cannot find sprite for "+resourceLocation+" ("+trueResourceLocation+")");
        }
    }

    public int getColor() {
        return color;
    }

    public static void buildDefaults() {
        colorList.clear();
        GetAvgColor avgColorMethod = new GetAvgColor();
        for (int i=0;i<Config.trimmableMaterials().size();i++) {
            try {
                if (!(colorList.containsKey(Config.trimmableMaterials().get(i)))) {
                    avgColorMethod.addColorEntry(Config.trimmableMaterials().get(i));
                }
            } catch (Exception e) {
                LOGGER.warn("ArmorTrims: Could not create color for entry "+Config.trimmableMaterials().get(i)+" ("+i+"). You can ignore this error.");
            }
        }
        LOGGER.info("ArmorTrims: Loaded trim colors for materials in config.");
    }
}