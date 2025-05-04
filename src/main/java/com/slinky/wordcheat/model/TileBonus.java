package com.slinky.wordcheat.model;

/**
 * Enumeration representing the bonus tile modifiers used in the game.
 *
 * <p>Each bonus type carries a multiplier that affects the scoring of letters or words
 * when placed on a bonus tile. The defined bonus types are:
 * <ul>
 *   <li>{@link #DL} (Double Letter) – applies a multiplier of 2 to a letter's score.</li>
 *   <li>{@link #TL} (Triple Letter) – applies a multiplier of 3 to a letter's score.</li>
 *   <li>{@link #DW} (Double Word) – applies a multiplier of 2 to the overall word score.</li>
 *   <li>{@link #TW} (Triple Word) – applies a multiplier of 3 to the overall word score.</li>
 * </ul>
 *
 * @author Kheagen Haskins
 * @since 0.1.0
 */
public enum TileBonus {

    /** Double Letter – applies a multiplier of 2 to a letter's score. */
    DL(2),

    /** Triple Letter – applies a multiplier of 3 to a letter's score. */
    TL(3),

    /** Double Word – applies a multiplier of 2 to the overall word score. */
    DW(2),

    /** Triple Word – applies a multiplier of 3 to the overall word score. */
    TW(3);
    
    /** The multiplier value associated with this bonus tile. */
    public final int value;
    
    /**
     * Constructs a bonus tile with the specified multiplier value.
     *
     * @param value the multiplier for this bonus tile
     */
    TileBonus(int value) {
        this.value = value;
    }
}