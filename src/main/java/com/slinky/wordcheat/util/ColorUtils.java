package com.slinky.wordcheat.util;

public class ColorUtils {

    // ==============================[ Constants ]============================== \\
    private static final double RED_WEIGHT   = 0.2126;
    private static final double GREEN_WEIGHT = 0.7152;
    private static final double BLUE_WEIGHT  = 0.0722;
    private static final double THRESHOLD    = 0.5;

    // ==============================[ API Methods ]============================= \\

    /**
     * Returns {@code true} if black text should be used on a background
     * colour with the given RGB components; {@code false} if white text
     * provides better contrast.
     *
     * <p>The calculation uses luminance based on WCAG 2.0 standards.</p>
     *
     * @param red   the red channel   (0–1)
     * @param green the green channel (0–1)
     * @param blue  the blue channel  (0–1)
     * @return {@code true} for black text, {@code false} for white text
     */
    public static boolean useBlackText(double red, double green, double blue) {
        double luminance = (RED_WEIGHT * red) + (GREEN_WEIGHT * green) + (BLUE_WEIGHT * blue);
        return luminance > THRESHOLD;
    }

}