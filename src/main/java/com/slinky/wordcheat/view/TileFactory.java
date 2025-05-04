package com.slinky.wordcheat.view;

import static com.slinky.wordcheat.view.FontConstants.BONUS_FONT;
import static com.slinky.wordcheat.view.FontConstants.SMALL_FONT;
import static com.slinky.wordcheat.view.FontConstants.TILE_FONT;

import javafx.geometry.Insets;

import javafx.scene.effect.InnerShadow;

/**
 * Static factory for producing TileNode instances for the board.
 * 
 * <p>
 * Keeps tile‑creation and default styling concerns in one place for future
 * extension (sizing, event hooks, etc.).
 * 
 */
public final class TileFactory {

    // =============================[ Static ]============================== \
    static final int TILE_SIZE = 40;
    
    // Prevent instantiation
    private TileFactory() {}

    // ==========================[ API Methods ]============================ \
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
    static TileNode createBoardTile(int row, int col, char letter, int score, String bonus) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("tile-" + row + "-" + col);
        tile.setSubstrate(Substrate.BOARD);
        tile.setLetter(letter);
        tile.setScore(score);
        tile.setBonus(bonus);
        
        applyDefaultStyle(tile);
        tile.syncView();
        
        return tile;
    }
    
    static TileNode createTileSetTile(char letter, int count) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("poolTile-" + letter);
        tile.setSubstrate(Substrate.POOL);
        applyDefaultStyle(tile);
        
        tile.setLetter(letter);
        tile.setCount(count);
        if (letter < 'A' || letter > 'Z') {
            tile.setWildcard(true);
        }
        
        tile.syncView();
        
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
    static TileNode createRackTile(char letter, int score) {
        TileNode tile = new TileNode(TILE_SIZE);
        tile.setId("rack-tile-" + letter);
        tile.setSubstrate(Substrate.RACK);
        applyDefaultStyle(tile);

        tile.setLetter(letter);
        tile.setScore(score);
        
        tile.syncView();

        return tile;
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Apply the default visual style to a TileNode:
     * 
     * - Rounded corners
     * - Default background fill
     * - Inner shadow for depth
     *
     * @param tile the TileNode to style
     */
    private static void applyDefaultStyle(TileNode tile) {
        // corner radius for smooth rounded tiles
        tile.setCornerRadius(20);
        
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