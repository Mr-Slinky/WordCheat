package com.slinky.wordcheat.util;

/**
 * Provides utility methods for determining optimal text contrast on
 * coloured backgrounds.
 *
 * <p>Implements WCAG 2.0 luminance calculations to decide whether black
 * or white text offers better readability against a given RGB colour.
 *
 * @author Kheagen Haskins
 * @since 0.1.0
 */
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
     * <p>The calculation uses luminance based on WCAG 2.0 standards.
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