package com.slinky.wordcheat.view;

import javafx.geometry.Insets;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

/**
 * Static factory for producing TileNode instances for the board.
 * <p>
 * Keeps tile‑creation and default styling concerns in one place for future
 * extension (sizing, event hooks, etc.).
 * </p>
 */
public final class TileFactory {

    // =============================[ Static ]============================== \
    /** Default tile color. */
    public static final Color DEFAULT_COLOR   = Color.rgb(243, 193, 120);
    
    public static final int TILE_SIZE = 40;
    
    // Prevent instantiation
    private TileFactory() {}

    // ==========================[ API Methods ]============================ \
    /**
     * Create a TileNode for placement on the game board grid.
     * Uses DEFAULT_COLOR as background.
     *
     * @param row  zero‑based row index
     * @param col  zero‑based column index
     * @param text text to display on the tile (letter or bonus)
     * @return styled TileNode with default color
     */
    public static TileNode createBoardTile(int row, int col, String text) {
        return createBoardTile(row, col, text, DEFAULT_COLOR);
    }

    /**
     * Create a TileNode for placement on the game board grid.
     * Applies default visual styling before any additional adjustments, then
     * sets the display text and custom background color.
     *
     * @param row       zero‑based row index (for id or metadata)
     * @param col       zero‑based column index
     * @param text      text to display on the tile (letter or bonus)
     * @param fillColor background fill color for this tile
     * @return a styled TileNode with specified text and color
     */
    public static TileNode createBoardTile(int row, int col, String text, Color fillColor) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("tile-" + row + "-" + col);
        applyDefaultStyle(tile);
        if (text != null && !text.isEmpty()) {
            if (text.length() == 1) {
                tile.setLetter(text.charAt(0));
            } else {
                tile.setBonus(text);
            }
        }
        
        // override background color
        tile.setBackgroundFill(fillColor);
        // commit style and text
        tile.applyStyle();
        tile.refresh();
        return tile;
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Apply the default visual style to a TileNode:
     * - Rounded corners
     * - Default background fill
     * - Inner shadow for depth
     *
     * @param tile the TileNode to style
     */
    private static void applyDefaultStyle(TileNode tile) {
        // corner radius for smooth rounded tiles
        tile.setCornerRadius(20);
        // default background
        tile.setBackgroundFill(DEFAULT_COLOR);
        // commit style properties
        tile.applyStyle();

        tile.setPadding(new Insets(3));
        // subtle inner shadow for depth
        InnerShadow inner = new InnerShadow();
        inner.setOffsetY(1);
        inner.setRadius(2);
        tile.getBackgroundNode().setEffect(inner);
    }
    
}