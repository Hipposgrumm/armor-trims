package dev.hipposgrumm.armor_trims.util.color;

import net.minecraft.util.FastColor;

import java.util.*;

public class ColorFrame {
    private final NavigableMap<Integer,Integer> color;
    private final boolean interpolated;

    /**
     * Create a frame for color
     * @param color Color of the frame.
     */
    public ColorFrame(NavigableMap<Integer,Integer> color) {
        this.color = color;
        this.interpolated = false;
    }

    private ColorFrame(NavigableMap<Integer,Integer> color, boolean interpolated) {
        this.color = color;
        this.interpolated = interpolated;
    }

    public int getColor(int tint) {
        if (!color.containsKey(tint)) {
            Integer floor = color.floorKey(tint);
            Integer ceiling = color.ceilingKey(tint);
            int col;
            if (floor == null) { // If tint is lower than the lowest (normally impossible but just in case).
                if (ceiling != null) { // If there is a ceiling.
                    col = color.get(ceiling);
                } else {
                    // Should not happen.
                    col = ColorPalette.DEFAULT.get(tint);
                }
            } else if (ceiling == null) { // If tint is higher than the highest.
                // This would make the highest the floor.
                col = color.get(floor);
            } else {
                col = interpolate(
                        color.get(floor),       // Existing Color Lower than Tint
                        color.get(ceiling),     // Existing Color Higher than Tint
                        color.floorKey(tint),   // Existing Tint Lower than Tint
                        color.ceilingKey(tint), // Existing Tint Higher than Tint
                        tint                    // Tint
                );
            }
            color.put(tint,col);
            return col;
        }
        return color.get(tint);
    }

    public boolean interpolated() {
        return interpolated;
    }

    public static ColorFrame interpolate(ColorFrame first, ColorFrame next, double progress) {
        NavigableMap<Integer,Integer> frame = new TreeMap<>();
        for (int tint:ColorPalette.PALETTE_COLORS) {
            frame.put(tint, interpolate(
                    first.color.get(tint),
                    next.color.get(tint),
                    0, 1, progress
            ));
        }
        return new ColorFrame(frame, true);
    }

    /**
     * @author <a href="http://www.java2s.com/example/java-utility-method/color-interpolate/interpolate-color-low-color-high-double-min-double-max-double-v-64b61.html">java2s.com</a><br>
     * With some modification.
     * @param low Color at the start of lerp.
     * @param high Color at the end of lerp.
     * @param distance Lerp between min and max from this value.
     */
    private static int interpolate(int low, int high, int min, int max, double distance) {
        if (low == high) return high;
        if (distance >= max) return high;
        if (distance <= min) return low;
        distance = 1 - (((double)max - distance) / ((double)max - (double)min));
        if (Double.isNaN(distance) || Double.isInfinite(distance)) return high;
        return FastColor.ARGB32.color(
                (int) lerp(FastColor.ARGB32.alpha(high), FastColor.ARGB32.alpha(low), distance),
                (int) lerp(FastColor.ARGB32.red(high), FastColor.ARGB32.red(low), distance),
                (int) lerp(FastColor.ARGB32.green(high), FastColor.ARGB32.green(low), distance),
                (int) lerp(FastColor.ARGB32.blue(high), FastColor.ARGB32.blue(low), distance)
        );
    }

    /**
     * @param min - Lowest
     * @param max - Highest
     * @param p - Percent (0.0 to 1.0)
     */
    private static double lerp(double min, double max, double p) {
        //  6.0, 13, 0.9
        min -= max; // -7.0, 13, 0.9
        min *= p;   // -6.3, 13, 0.9
        min += max; //  6.7, 13, 0.9
        return min;
    }
}
