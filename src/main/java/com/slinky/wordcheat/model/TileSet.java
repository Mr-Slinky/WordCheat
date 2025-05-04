package com.slinky.wordcheat.model;

/**
 * Represents and manages the tile set used in a specific WWF game.
 *
 * <p>
 * This interface defines operations for tracking the counts of letter and blank
 * tiles, drawing and validating tiles, and resetting the set to its initial
 * state.
 *
 * @author Kheagen Haskins
 */
public interface TileSet {

    /**
     * Character representing a blank tile in the set.
     */
    public static final char BLANK_TILE = ' ';

    /**
     * Alternate wildcard character for blank tiles.
     */
    public static final char WILDCARD = '?';

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the total number of tiles remaining in the game set.
     *
     * @return the total count of remaining tiles
     */
    public int getRemainingTileCount();

    /**
     * Returns the number of blank (wildcard) tiles remaining in the game set.
     *
     * @return the count of remaining blank tiles
     */
    public int getRemainingWildcardCount();

    /**
     * Returns the maximum number of tiles defined for the game set.
     *
     * @return the maximum number of tiles
     */
    public int getMaxTileCount();

    /**
     * Returns the remaining count for the specified tile character.
     *
     * <p>
     * If the specified character is {@code BLANK_TILE} or {@code WILDCARD},
     * this method returns the number of blank tiles remaining. Otherwise, it
     * validates the letter and returns its remaining count.
     *
     * @param letter the tile character to query
     * @return the number of remaining tiles for the specified character
     * @throws IllegalArgumentException if the character is not a valid tile
     */
    public int getRemainingTileCount(char letter);

    // =============================[ API Methods ]============================== 
    /**
     * Adds the specified tile back into the game set.
     *
     * <p>
     * If the tile is a blank (represented by {@code BLANK_TILE} or
     * {@code WILDCARD}), increments the blank tile count; otherwise validates
     * the letter and increments the corresponding letter count.
     *
     * @param letter the tile character to be added
     * @throws IllegalStateException    if adding would exceed the tile's maximum
     * @throws IllegalArgumentException if the character is not a valid tile
     */
    public void addLetter(char letter);

    /**
     * Removes the specified tile from the game set.
     *
     * <p>
     * If the tile is a blank (represented by {@code BLANK_TILE} or
     * {@code WILDCARD}), decrements the blank tile count; otherwise validates
     * the letter and decrements the corresponding letter count.
     *
     * @param letter the tile character to be removed
     * @throws IllegalStateException    if no more tiles of that type remain
     * @throws IllegalArgumentException if the character is not a valid tile
     */
    public void removeLetter(char letter);

    /**
     * Randomly draws a tile from the remaining pool.
     *
     * <p>
     * Simulates drawing a tile at random, updates the internal counts, and
     * returns the drawn character.
     *
     * @return the character of the drawn tile
     * @throws IllegalStateException if no tiles remain to be drawn
     */
    public char drawRandomTile();

    /**
     * Determines if the provided word can be constructed from the remaining
     * tiles.
     *
     * <p>
     * Checks that for each character in the word, sufficient tiles (including
     * blanks as wildcards) are available to form the word.
     *
     * @param word the word to validate
     * @return {@code true} if the word can be constructed; {@code false}
     *         otherwise
     * @throws IllegalArgumentException if {@code word} is null or contains
     *                                  invalid characters
     */
    public boolean canConstructWord(String word);

    /**
     * Resets all tile counts to their initial maximum values.
     *
     * <p>
     * Restores both letter and blank tile counts to the original limits.
     */
    public void reset();

    /**
     * Retrieves the maximum allowed count for the given letter.
     *
     * <p>
     * Converts the letter to uppercase, validates that it is in A–Z, and
     * returns the configured limit for that letter.
     *
     * @param letter the letter to query
     * @return the maximum number of tiles available for that letter
     * @throws IllegalArgumentException if the letter is not in the range A–Z
     */
    public int getTileMaxCount(char letter);

    /**
     * Exception thrown when attempting to remove a tile that is not available.
     */
    public static class InvalidTileRemovalException extends IllegalStateException {

        /**
         * Creates a generic invalid removal exception.
         */
        public InvalidTileRemovalException() {
        }

        /**
         * Creates an exception indicating no more tiles of the specified
         * character remain.
         *
         * @param c the tile character that is unavailable
         */
        public InvalidTileRemovalException(char c) {
            super("No more " + c + " tiles remaining");
        }

        /**
         * Constructs the exception with a custom message.
         *
         * @param message the detail message
         */
        public InvalidTileRemovalException(String message) {
            super(message);
        }

        /**
         * Constructs the exception with a custom message and cause.
         *
         * @param message the detail message
         * @param cause the underlying cause
         */
        public InvalidTileRemovalException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs the exception with an underlying cause.
         *
         * @param cause the underlying cause
         */
        public InvalidTileRemovalException(Throwable cause) {
            super(cause);
        }
    }
}