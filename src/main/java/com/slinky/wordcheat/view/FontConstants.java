package com.slinky.wordcheat.view;

import javafx.scene.text.Font;

/**
 * Provides application-wide constants for font usage, ensuring all custom fonts
 * are registered with the JVM before being used. If a specified font fails to
 * load, the system gracefully falls back to using Arial.
 * <p>
 * This class is designed to be used statically and should not be instantiated.
 * </p>
 *
 * <p>
 * <strong>Fonts defined:</strong></p>
 * <ul>
 *   <li>{@code TILE_FONT} – Used for game tiles.</li>
 *   <li>{@code SMALL_FONT} – Used for small-sized text such as scores.</li>
 *   <li>{@code BONUS_FONT} – Used for bonus indicators, slightly smaller than
 *       tile font.</li>
 *   <li>{@code LABEL_FONT_DEFAULT} – Default font for UI labels.</li>
 *   <li>{@code LABEL_FONT_HEADING} – Font for label headings.</li>
 * </ul>
 *
 * <p>
 * All fonts attempt to load their respective custom typefaces, and default to
 * {@code Arial} if the desired font is not found.</p>
 *
 * @author Kheagen
 */
public class FontConstants {

    // ==============================[ Static ]============================== \\
    /**
     * The name of the font used for tile text.
     */
    public static final String TILE_FONT_NAME   = "Courier New";

    /**
     * The name of the font used for labels.
     */
    public static final String LABEL_FONT_NAME  = "Univers";

    /**
     * Fallback font family used when a custom font cannot be loaded.
     */
    private static final String FALLBACK_FAMILY = "Arial";

    /**
     * Font size used for default labels.
     */
    public static final int LABEL_FONT_SIZE     = 14;

    /**
     * Font size used for tile text.
     */
    public static final int TILE_FONT_SIZE      = 18;

    /**
     * Font size used for smaller text, such as scores.
     */
    public static final int SCORE_FONT_SIZE     = 10;

    /**
     * Font instance used for tile display.
     */
    public static final Font TILE_FONT;

    /**
     * Font instance used for small-sized elements such as scores.
     */
    public static final Font SMALL_FONT;

    /**
     * Font instance used for bonus-related display elements.
     */
    public static final Font BONUS_FONT;

    /**
     * Font instance used as the default for UI labels.
     */
    public static final Font LABEL_FONT_DEFAULT;

    /**
     * Font instance used for label headings and larger titles.
     */
    public static final Font LABEL_FONT_HEADING;

    static {
        Font loaded;

        loaded    = Font.font(TILE_FONT_NAME, TILE_FONT_SIZE);
        TILE_FONT = (loaded == Font.getDefault())
                    ? Font.font(FALLBACK_FAMILY, TILE_FONT_SIZE)
                    : loaded;

        loaded     = Font.font(TILE_FONT_NAME, SCORE_FONT_SIZE);
        SMALL_FONT = (loaded == Font.getDefault())
                     ? Font.font(FALLBACK_FAMILY, SCORE_FONT_SIZE)
                     : loaded;

        loaded     = Font.font(TILE_FONT_NAME, TILE_FONT_SIZE - 2);
        BONUS_FONT = (loaded == Font.getDefault())
                     ? Font.font(FALLBACK_FAMILY, TILE_FONT_SIZE - 2)
                     : loaded;

        loaded             = Font.font(LABEL_FONT_NAME, LABEL_FONT_SIZE);
        LABEL_FONT_DEFAULT = (loaded == Font.getDefault())
                             ? Font.font(FALLBACK_FAMILY, LABEL_FONT_SIZE)
                             : loaded;

        loaded             = Font.font(LABEL_FONT_NAME, 20);
        LABEL_FONT_HEADING = (loaded == Font.getDefault())
                             ? Font.font(FALLBACK_FAMILY, 20)
                             : loaded;
    }
    
}