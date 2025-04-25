package com.slinky.wordcheat.view;

import javafx.scene.text.Font;

/**
 * Holds application‑wide font constants and ensures each custom font is
 * registered with the JVM before use.
 *
 * @author Kheagen
 */
public class FontConstants {

    // ==============================[ Static ]============================== \
    public static final String TILE_FONT_NAME   = "Arial.ttf";
    public static final String LABEL_FONT_NAME  = "Arial.ttf";
    
    public static final int    LABEL_FONT_SIZE  = 14;
    public static final int    TILE_FONT_SIZE   = 18;
    public static final int    SCORE_FONT_SIZE  = 10;

    public static final Font   TILE_FONT;         
    /**
     * For tile score and count.
     */
    public static final Font   SMALL_FONT;         
    public static final Font   BONUS_FONT;        
    
    public static final Font   LABEL_FONT_DEFAULT;
    public static final Font   LABEL_FONT_HEADING;
    
    static {
        TILE_FONT = Font.font(TILE_FONT_NAME, TILE_FONT_SIZE);

        SMALL_FONT = Font.font(TILE_FONT_NAME, SCORE_FONT_SIZE);
        BONUS_FONT = Font.font(TILE_FONT_NAME, TILE_FONT_SIZE - 2);

        LABEL_FONT_DEFAULT = Font.font(LABEL_FONT_NAME, LABEL_FONT_SIZE);
        LABEL_FONT_HEADING = Font.font(LABEL_FONT_NAME, 20);
    }
    
}
