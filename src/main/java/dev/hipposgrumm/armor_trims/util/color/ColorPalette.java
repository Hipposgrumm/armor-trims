package dev.hipposgrumm.armor_trims.util.color;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ColorPalette implements Tickable {
    public static final ColorPalette DEFAULT = new DefaultColorPalette(); // Default color palette, for when all else fails.
    public static final int[] PALETTE_COLORS = {224,192,160,128,96,64,32,0}; // Shades in palette, brightest to darkest.

    final List<ColorFrame> frames;
    final List<ColorFrame> uniqueFrames;
    protected final ResourceLocation name;
    protected AnimationMetadataSection meta;
    protected int tick = 0;
    private boolean discarded = false;

    /** Default */
    private ColorPalette() {
        this(new ResourceLocation(Armortrims.MODID,"default"));
        this.meta = AnimationMetadataSection.EMPTY;
    }

    /** Single Color */
    private ColorPalette(ResourceLocation name) {
        this.name = name;
        this.frames = Collections.emptyList();
        this.uniqueFrames = Collections.emptyList();
    }

    ColorPalette(ResourceLocation name, NativeImage texture, @Nullable AnimationMetadataSection meta) {
        this.name = name;
        this.frames = new ArrayList<>();
        this.uniqueFrames = new ArrayList<>();
        if (meta == null) meta = AnimationMetadataSection.EMPTY;
        this.meta = meta;

        // Read entire horizontal before increasing vertical.
        for (int frameY=0;frameY<texture.getHeight();frameY+=1) for (int frameX=0;frameX<texture.getWidth();frameX+=PALETTE_COLORS.length)  {
            NavigableMap<Integer,Integer> frame = new TreeMap<>(Integer::compareTo);
            for (int x=0;x<PALETTE_COLORS.length;x++) {
                int color = texture.getPixelRGBA(x+frameX, frameY);
                color = (color & 0xFF00FF00)      // Keep position of alpha and green.
                        | ((color >> 16) & 0xFF)  // Flip red.
                        | ((color & 0xFF) << 16); // Flip blue.
                frame.put(PALETTE_COLORS[x], color);
            }
            uniqueFrames.add(new ColorFrame(frame));
        }
        arrangeFrames(uniqueFrames);
    }

    protected void arrangeFrames(List<ColorFrame> uniqueFrames) {
        // If there is only one frame, shortcut.
        if (uniqueFrames.size() == 1) {
            frames.add(uniqueFrames.get(0));
            return;
        }

        // Take each frame from above and assign them to a new list, using the metadata.
        forEachFrame((index, time) -> {
            ColorFrame frame = uniqueFrames.get(index);
            if (meta.isInterpolatedFrames()) {
                if (time > 0) {
                    frames.add(frame);
                    if (time > 1) {
                        for (int i=1;i<time;i++) frames.add(null);
                    }
                }
            } else {
                for (int i=0;i<time;i++) frames.add(frame);
            }
        }, uniqueFrames);

        // An empty framelist would likely break the code below.
        if (frames.isEmpty()) return;

        // Resolve each of the interpolated frames
        if (this.meta.isInterpolatedFrames()) {
            Map<Triple<ColorFrame, ColorFrame, Double>, ColorFrame> cache = new HashMap<>();
            int interpolateStart = 0;
            int interpolateLength = 0;
            for (int i=0;i<frames.size();i++) {
                ColorFrame frame = frames.get(i);
                if (frame != null) {
                    // Replace previously null frames with interpolated frames.
                    setInterpolatedFrames(
                            frame,
                            interpolateStart, i,
                            interpolateLength,
                            cache
                    );
                    interpolateStart = i;
                    interpolateLength = 0;
                }
                interpolateLength++;
            }

            // Same as above but wrap to the beginning.
            setInterpolatedFrames(
                    frames.get(0),
                    interpolateStart, 0,
                    interpolateLength,
                    cache
            );

            // Add the interpolated frames as unique frames.
            uniqueFrames.addAll(cache.values());
        }
    }

    private void setInterpolatedFrames(ColorFrame nextFrame, int firstIndex, int secondIndex, int interpolateLength, Map<Triple<ColorFrame, ColorFrame, Double>, ColorFrame> cache) {
        ColorFrame lastFrame = frames.get(firstIndex);
        ColorFrame first = lastFrame;  // First is oldest.
        ColorFrame second = nextFrame; // Second is newest.
        boolean inverse = secondIndex < firstIndex; // Inverse the progress if necessary so that the cache works efficiently.
        if (inverse) { // If the second is older than the first, swap the first and the second.
            first = nextFrame;  // This is now actually the oldest.
            second = lastFrame; // This is now the actual newest.
        }
        for (int j=1;j<interpolateLength;j++) {
            final double progress = (double) ( // Progress used by the cache.
                    inverse ? (interpolateLength-j) : j // If inverse progress, then inverse j over interpolateLength; otherwise, j as normal.
            ) / (double)interpolateLength; // Divide j by interpolateLength
            if (first == second) {
                frames.set(firstIndex+j, first);
            } else {
                frames.set(firstIndex+j, cache.computeIfAbsent(new ImmutableTriple<>(
                        first,                              // First
                        second,                             // Next
                        progress // Progress
                ), data -> ColorFrame.interpolate(
                        lastFrame,                          // First
                        nextFrame,                          // Next
                        progress  // refMin, refMax, Progress
                )));
            }
        }
    }

    public ResourceLocation name() {
        return name;
    }

    public AnimationMetadataSection meta() {
        return meta;
    }

    public int get(int tintIndex) {
        if (tintIndex>PALETTE_COLORS[0]) tintIndex = PALETTE_COLORS[0];
        if (frames.isEmpty()) return DEFAULT.get(tintIndex);
        return frames.get(tick).getColor(tintIndex);
    }

    public TextColor textColor() {
        int color = get(ColorPalette.PALETTE_COLORS[1]);
        return TextColor.fromRgb(color);
    }

    /**
     * Apply a list of palettes to a texture.
     * @param colors - List of color palettes to apply to the texture.
     * @param base - The texture to apply the palettes to.
     * @param merged - Whether to merge all the textures into a single texture (for item textures).
     * @return A map of textures for each color palette.
     */
    // NOTE: I've decided that animated textures for trims will not be implemented.
    public static Map<ColorPalette, Pair<NativeImage[],AnimationMetadataSection>> apply(List<ColorPalette> colors, NativeImage base, boolean merged) {
        // Create map for textures.
        Map<ColorPalette, Triple<NativeImage[],Pair<Integer,Integer>,AnimationMetadataSection>> textures = new HashMap<>();

        // Size of texture
        int width = base.getWidth();
        int height = base.getHeight();

        // Initialize textures before setting them.
        for (ColorPalette color : colors) {
            NativeImage[] images;
            Pair<Integer,Integer> sizes = null;
            AnimationMetadataSection meta = AnimationMetadataSection.EMPTY;
            if (merged) { // If merged, create a larger texture for all the frames.
                images = new NativeImage[1];
                // Create a size that can accommodate all the textures.
                int size = color.uniqueFrames.size();
                int sizeX=1, sizeY=1;
                // Expand grid until satisfied.
                while (sizeX*sizeY < size) {
                    if (sizeY > sizeX) sizeX++; // Increase width if the height has already increased.
                    else sizeY++;       // Increase height before increasing width.
                }
                sizes = new Pair<>(sizeX, sizeY);
                images[0] = new NativeImage(width*sizeX,height*sizeY,true);
                List<AnimationFrame> frames = new ArrayList<>();
                color.forEachFrame((index, time) -> {
                    frames.add(new AnimationFrame(index,time));
                }, false);
                meta = new AnimationMetadataSection(frames, width, height, color.meta.getDefaultFrameTime(), color.meta.isInterpolatedFrames());
            } else {
                images = new NativeImage[color.uniqueFrames.size()];
                for (int i=0;i<images.length;i++) images[i] = new NativeImage(width, height, true);
            }

            textures.put(color,new ImmutableTriple<>(images,sizes,meta));
        }

        // Modify each texture based on each color palette.
        for (int x=0;x<width;x++) for (int y=0;y<height;y++) {
            // Get pixel from texture. Pixel is in RGBA.
            int pixel = base.getPixelRGBA(x,y);
            int alpha = pixel>>>24;
            // If alpha is 0 (completely transparent), skip.
            if (alpha == 0) continue;

            // Get tint from pixel color. Use red as tint.
            int tint = (pixel >> 16) & 0xff;
            for (ColorPalette color:colors) {
                Triple<NativeImage[],Pair<Integer,Integer>,?> framesData = textures.get(color);
                int sizeX = 1;
                int sizeY = 1;
                Pair<Integer, Integer> sizes = framesData.getMiddle();
                if (sizes != null) {
                    sizeX = sizes.getFirst();
                    sizeY = sizes.getSecond();
                }
                NativeImage[] frames = framesData.getLeft();
                int imageCount = frames.length;
                // If merged use the amount of frames from here instead.
                if (merged) imageCount = color.uniqueFrames.size();
                for (int i=0;i<imageCount;i++) {
                    int col = color.uniqueFrames.get(i).getColor(tint);
                    // Determine alpha of pixel.
                    float alphaMod = alpha/255f;
                    int finalAlpha = (int)((col>>>24)*alphaMod);
                    // If alpha is 0 (completely transparent), skip.
                    if (finalAlpha == 0) continue;

                    col = (col & 0x0000FF00)        // Keep position of green, and remove alpha  we're at it, it's calculated later.
                            | ((col >> 16) & 0xFF)  // Flip red.
                            | ((col & 0xFF) << 16); // Flip blue.
                    col = col | (finalAlpha<<24);   // Add RGB of color with final alpha of texture. Alpha was already removed above.

                    if (merged) frames[0].setPixelRGBA(x+((i%sizeX)*width), y+((i/sizeY)*height), col);
                    else frames[i].setPixelRGBA(x, y, col);
                }
            }
        }

        return textures.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new Pair<>(entry.getValue().getLeft(),entry.getValue().getRight()),
                        (x, y) -> y,
                        HashMap::new
                ));
    }

    public void forEachFrame(/*? if >=1.18 {*/AnimationMetadataSection.FrameOutput/*?} else {*//*BiConsumer<Integer, Integer>*//*?}*/ function, boolean countInterpolatedFrames) {
        ColorFrame last = null;
        int count = 1;
        for (ColorFrame frame:frames) {
            if (frame == last || (frame.interpolated() && !countInterpolatedFrames)) {
                count++;
            } else {
                if (last != null) function.accept(uniqueFrames.indexOf(last), count);
                last = frame;
                count = 1;
            }
        }
        if (last != null) function.accept(uniqueFrames.indexOf(last), count);
    }

    protected void forEachFrame(/*? if >=1.18 {*/AnimationMetadataSection.FrameOutput/*?} else {*//*BiConsumer<Integer, Integer>*//*?}*/ function, List<ColorFrame> frames) {
        AtomicBoolean ran = new AtomicBoolean(false);
        //? if >=1.18 {
        meta.forEachFrame((index, time) -> {
            function.accept(index, time);
            ran.set(true);
        });
        //?} else {
        /*if (meta.getFrameCount() > 0) {
            for (int i = 0; i < meta.getFrameCount(); i++) {
                function.accept(meta.getFrameIndex(i), meta.getFrameTime(i));
            }
            ran.set(true);
        }
        *///?}
        if (!ran.get()) {
            for (int i=0;i<frames.size();i++) {
                int time = meta.getDefaultFrameTime();
                if (time > 0) function.accept(i, time);
            }
        }
    }

    void discard() {
        this.discarded = true;
    }

    public boolean discarded() {
        return discarded;
    }

    @Override
    public void tick() {
        tick++;
        tick %= frames.size();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public static class SingleColorPalette extends ColorPalette {
        private final List<Integer> color = new ArrayList<>();
        private final List<Integer> textColor = new ArrayList<>();

        SingleColorPalette(ResourceLocation name, Item item) {
            super(name);
            this.meta = processTextureColor(name,item);
        }

        private AnimationMetadataSection processTextureColor(ResourceLocation itemId, Item item) {
            AnimationMetadataSection meta = null;
            try {
                BakedModel bakedModel = Minecraft.getInstance()
                    .getItemRenderer()
                    .getItemModelShaper()
                    .getItemModel(item.getDefaultInstance());
                TextureAtlasSprite particleIcon = null;
                if (bakedModel != null) particleIcon = bakedModel.getParticleIcon();
                ResourceLocation textureLoc = null;
                if (particleIcon != null) textureLoc = particleIcon.getName();
                if (textureLoc == null || textureLoc.equals(MissingTextureAtlasSprite.getLocation())) {
                    /*? if >=1.19 {*/Optional<Resource>/*?} else {*//*Resource*//*?}*/ resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(itemId.getNamespace(),"models/item/"+itemId.getPath()+".json"));
                    /*? if >=1.19 {*/if (resource.isPresent())/*?}*/ {
                        Reader reader = new InputStreamReader(resource./*? if >=1.19 {*/get().open/*?} else {*//*getInputStream*//*?}*/(), StandardCharsets.UTF_8);
                        BlockModel model = BlockModel.fromStream(reader);
                        textureLoc = model.getMaterial("layer0").texture();
                    }
                }
                if (textureLoc == null || textureLoc.equals(MissingTextureAtlasSprite.getLocation())) throw new NullPointerException("Could not find a texture in model of "+itemId+", it might be a non-vanilla model."); // This should compatible with most non-vanilla models anyway unless they're doing something really strange. This is just a failsafe.

                /*? if >=1.19 {*/Optional<Resource>/*?} else {*//*Resource*//*?}*/ texture = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(textureLoc.getNamespace(),"textures/"+textureLoc.getPath()+".png"));
                NativeImage main = NativeImage.read(texture./*? if >=1.19 {*/get().open/*?} else {*//*getInputStream*//*?}*/());
                meta = texture./*? if >=1.19 {*/get().metadata().getSection/*?} else {*//*getMetadata*//*?}*/(AnimationMetadataSection.SERIALIZER)/*? if >=1.19 {*/.orElse(null)/*?}*/;
                if (meta == null) meta = AnimationMetadataSection.EMPTY;

                int width = meta.getFrameWidth(main.getWidth());
                int height = meta.getFrameHeight(main.getHeight());
                AtomicInteger animSize = new AtomicInteger();
                AtomicInteger totalAnimSize = new AtomicInteger();
                //? if >=1.18 {
                AnimationMetadataSection lambdaSafeMeta = meta;
                meta.forEachFrame((index, time) -> {
                    animSize.addAndGet((lambdaSafeMeta.isInterpolatedFrames()?time:1));
                    totalAnimSize.incrementAndGet();
                });
                //?} else {
                /*for (int i=0;i<meta.getFrameCount();i++) {
                    animSize.addAndGet((meta.isInterpolatedFrames()?meta.getFrameTime(i):1));
                    totalAnimSize.incrementAndGet();
                }
                *///?}
                if (totalAnimSize.get() == 0) totalAnimSize.set((main.getWidth() / width) * (main.getHeight() / height));
                final int brightenAmount = 30;

                // Get colors from all frames and assign them to a list, ordered by index.
                for (int frameY=0;frameY<main.getHeight();frameY+=height) for (int frameX=0;frameX<main.getWidth();frameX+=width)  { // Read entire horizontal before increasing vertical.
                    int size = 0;
                    long[] colorVals = new long[]{0, 0, 0, 0};
                    for (int x=0;x<width;x++) for (int y=0;y<height;y++) {
                        int pixel = main.getPixelRGBA(frameX+x, frameY+y);
                        int alpha = FastColor.ARGB32.alpha(pixel);
                        if ((alpha >= 25)) {
                            colorVals[0] += alpha;
                            colorVals[1] += FastColor.ARGB32.blue(pixel);
                            colorVals[2] += FastColor.ARGB32.green(pixel);
                            colorVals[3] += FastColor.ARGB32.red(pixel);
                            size++;
                        }
                    }
                    colorVals[0] /= size;
                    colorVals[1] = (long) (((float) colorVals[1]/size)+brightenAmount);
                    colorVals[2] = (long) (((float) colorVals[2]/size)+brightenAmount);
                    colorVals[3] = (long) (((float) colorVals[3]/size)+brightenAmount);
                    color.add(FastColor.ARGB32.color(
                            (int) colorVals[0],
                            Math.min((int)colorVals[1],255),
                            Math.min((int)colorVals[2],255),
                            Math.min((int)colorVals[3],255)
                    ));
                    float tint = PALETTE_COLORS[1]/(float)PALETTE_COLORS[0];
                    textColor.add(FastColor.ARGB32.color(
                            (int) colorVals[0],
                            Math.min((int)(colorVals[1]*tint),255),
                            Math.min((int)(colorVals[2]*tint),255),
                            Math.min((int)(colorVals[3]*tint),255)
                    ));
                }

                Armortrims.LOGGER.debug("Generated palette from {}", itemId);
            } catch (IOException e) {
                if (item/*? if >=1.18.2 {*/.getDefaultInstance()/*?}*/.is(Armortrims.TRIM_MATERIALS_TAG)) Armortrims.LOGGER.error("Couldn't generate a palette for {}. Please define a palette for it instead.", itemId);
                else Armortrims.LOGGER.debug("Couldn't generate a palette for {}!", itemId);
            } catch (NullPointerException e) {
                Armortrims.LOGGER.error("Unable to generate palette for {}.", itemId);
            } catch (ClassCastException e) {
                Armortrims.LOGGER.error("Unable to load resultLocation for {}.", itemId);
            } catch (RuntimeException e) {
                Armortrims.LOGGER.error("Cannot find sprite of {} ({}).", itemId, item);
            }
            return meta;
        }

        @Override
        public int get(int tintIndex) {
            if (tintIndex>=PALETTE_COLORS[0]) {
                // If color is first or brighter.
                return color.get(tick);
            } else if (tintIndex==PALETTE_COLORS[1]) {
                // If color is same as textcolor.
                return textColor.get(tick);
            } else {
                // Anything else (probably slow, but shouldn't occur under normal circumstances).
                return (int)(get(PALETTE_COLORS[0])*((float)tintIndex/PALETTE_COLORS[0]));
            }
        }

        @Override
        public void tick() {
            tick++;
            tick%=color.size();
        }

        public boolean isValid() {
            return !color.isEmpty();
        }
    }

    private static final class DefaultColorPalette extends ColorPalette {
        @Override
        public int get(int tintIndex) {
            return 0xff000000 | (tintIndex << 16) | (tintIndex << 8) | tintIndex;
        }

        @Override
        public void tick() {}
    }
}
