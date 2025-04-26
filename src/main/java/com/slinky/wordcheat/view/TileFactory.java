package com.slinky.wordcheat.view;

import static com.slinky.wordcheat.view.FontConstants.BONUS_FONT;
import static com.slinky.wordcheat.view.FontConstants.SMALL_FONT;
import static com.slinky.wordcheat.view.FontConstants.TILE_FONT;
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
        return createBoardTile(row, col, text, ColorConstants.DEFAULT_TILE_COLOR);
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
    
    public static TileNode createTileSetTile(char letter, int count) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("tile-" + letter);
        applyDefaultStyle(tile);
        
        tile.setLetter(letter);
        tile.setScore(0); // 0 to stop tile score displaying
        tile.setCount(count);
        
        tile.applyStyle();
        tile.refresh();
        
        return tile;
    }
    
    /**
     * Creates a TileNode for the RackView. The count will always be set to -1
     * (hidden).
     *
     * @param letter the letter to display
     * @param score  the score associated with the letter
     * @return a styled TileNode suitable for the rack
     */
    public static TileNode createRackTile(char letter, int score) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("rack-tile-" + letter);
        applyDefaultStyle(tile);

        tile.setLetter(letter);
        tile.setScore(score);
        tile.setCount(-1); // Always hide count for rack tiles

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
        tile.setBackgroundFill(ColorConstants.DEFAULT_TILE_COLOR);
        
        tile.setLetterFont(TILE_FONT);
        tile.setSmallFont(SMALL_FONT);
        tile.setBonusFont(BONUS_FONT);

        tile.setPadding(new Insets(3));
        // subtle inner shadow for depth
        InnerShadow inner = new InnerShadow();
        inner.setOffsetY(1);
        inner.setRadius(2);
        tile.getBackgroundNode().setEffect(inner);
    }
    
}