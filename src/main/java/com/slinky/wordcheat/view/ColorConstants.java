package com.slinky.wordcheat.view;

import javafx.scene.paint.Color;

/**
 * Defines the standard colour constants used throughout the WordCheat UI.
 *
 * <p>Includes tile background colours for empty, new, default, and score multiplier tiles,
 * as well as button state colours.
 *
 * @author Kheagen
 */
public class ColorConstants {

    // ================================[ Static ]================================ \\

    /** The colour used for an empty tile (RGB 150,147,155). */
    public static final Color EMPTY_TILE_COLOR    = Color.rgb(150, 147, 155);

    /** The colour for newly placed tiles (RGB 145,205,60). */
    public static final Color NEW_TILE_COLOR      = Color.rgb(145, 205, 60);

    /** The default tile colour (RGB 244,159,10). */
    public static final Color DEFAULT_TILE_COLOR  = Color.rgb(244, 159, 10);

    /** The double-letter score tile colour (RGB 115,210,222). */
    public static final Color DOUBLE_LETTER_COLOR = Color.rgb(115, 210, 222);

    /** The double-word score tile colour (RGB 46,114,178). */
    public static final Color DOUBLE_WORD_COLOR   = Color.rgb(46, 114, 178);

    /** The triple-letter score tile colour (RGB 244,135,182). */
    public static final Color TRIPLE_LETTER_COLOR = Color.rgb(244, 135, 182);

    /** The triple-word score tile colour (RGB 204,89,210). */
    public static final Color TRIPLE_WORD_COLOR   = Color.rgb(204, 89, 210);
    
    /** The standard button colour, matching the new tile colour. */
    public static final Color BUTTON_COLOR         = NEW_TILE_COLOR;

    /** The button hover colour, matching the default tile colour. */
    public static final Color BUTTON_HOVER_COLOR   = DEFAULT_TILE_COLOR;

    /** The button active colour, matching the triple-word tile colour. */
    public static final Color BUTTON_ACTIVE_COLOR  = TRIPLE_WORD_COLOR;

}