package com.slinky.wordcheat.model;

/**
 * Enumeration representing the bonus tile modifiers used in the game.
 * <p>
 * Each bonus type carries a multiplier that affects the scoring of letters or words
 * when they are placed on a bonus tile. The defined bonus types are:
 * <ul>
 *   <li><b>DL</b>: Double Letter – applies a multiplier of 2 to a letter's score.</li>
 *   <li><b>TL</b>: Triple Letter – applies a multiplier of 3 to a letter's score.</li>
 *   <li><b>DW</b>: Double Word – applies a multiplier of 2 to the overall word score.</li>
 *   <li><b>TW</b>: Triple Word – applies a multiplier of 3 to the overall word score.</li>
 * </ul>
 * </p>
 *
 * @author Kheagen Haskins
 */
public enum TileBonus {

    DL(2),
    TL(3),
    DW(2),
    TW(3);
    
    /**
     * The multiplier value associated with the bonus tile.
     */
    public final int value;
    
    /**
     * Constructs a bonus tile with the specified multiplier value.
     *
     * @param value the multiplier for this bonus tile.
     */
    private TileBonus(int value) {
        this.value = value;
    }
    
}