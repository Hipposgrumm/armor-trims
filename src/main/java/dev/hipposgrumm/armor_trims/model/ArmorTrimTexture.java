package dev.hipposgrumm.armor_trims.model;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.util.PaletteMaps;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.Tickable;

public class ArmorTrimTexture extends DynamicTexture {
    public final PaletteMaps.Entry info;
    protected int tick = 0;

    protected ArmorTrimTexture(PaletteMaps.Entry info, NativeImage image) {
        super(image);
        this.info = info;
    }

    public static ArmorTrimTexture create(PaletteMaps.Entry info, NativeImage[] images, Integer[] order) {
        if (images.length > 1) {
            return new ArmorTrimTexture.Animated(info, images, order);
        } else {
            return new ArmorTrimTexture(info, images[0]);
        }
    }

    public static class Animated extends ArmorTrimTexture implements Tickable {
        protected final NativeImage[] frames;
        protected final Integer[] order;

        protected Animated(PaletteMaps.Entry info, NativeImage[] images, Integer[] order) {
            super(info, images[0]);
            this.frames = images;
            this.order = order;
        }

        // Based on TextureAtlas#tick()
        protected void nextFrame() {
            NativeImage pixels = getPixels();
            if (pixels == null) return;

            tick++;
            tick %= order.length;

            int frame = order[tick];
            if (frame < 0 || frame >= frames.length) {
                Armortrims.LOGGER.warn("Invalid frame {} in {} of {}", frame, info.color(), info.id());
            }
            pixels.copyFrom(frames[order[tick]]);
            upload();
        }

        @Override
        public void tick() {
            if (frames.length == 1) return;

            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(this::nextFrame);
            } else {
                this.nextFrame();
            }
        }
    }
}
