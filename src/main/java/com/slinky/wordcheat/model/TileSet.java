package com.slinky.wordcheat.model;

/**
 * Represents and manages the tile set used in a specific WWF game.
 * 
 * <p>
 * This class functions as a state-tracking object for the tiles available in
 * the game. It maintains counts for each letter tile as well as blank tiles,
 * and provides operations to add or remove tiles from the game set.
 * Additionally, it offers methods to query the current state, draw random
 * tiles, validate words based on available tiles, and reset the tile set to its
 * initial state.
 * </p>
 *
 * @author Kheagen Haskins
 */
public interface TileSet {

    /**
     * Represents a blank tile.
     */
    public static final char BLANK_TILE = ' ';
    
    public static final char WILDCARD   = '?';
    
    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the total number of tiles remaining in the game set.
     *
     * @return the total count of remaining tiles.
     */
    public int getRemainingTileCount();

    /**
     * Returns the number of blank tiles remaining in the game set.
     *
     * @return the count of remaining blank tiles.
     */
    public int getRemainingWildcardCount();
    
    /**
     * Returns the maximum number of tiles
     *
     * @return the maximum number of tiles
     */
    public int getMaxTileCount();
    
    /**
     * Returns the remaining count for the specified tile.
     *
     * <p>
     * If the specified tile represents a blank (i.e. {@code BLANK_TILE}, '?' or
     * '*'), this method returns the number of blank tiles remaining. Otherwise,
     * the letter is validated and its remaining count is returned.
     * </p>
     *
     * @param letter the tile to query.
     * @return the number of remaining tiles for the specified letter.
     * @throws IllegalArgumentException if the letter is invalid.
     */
    public int getRemainingTileCount(char letter);

    // =============================[ API Methods ]==============================
    /**
     * Adds a tile back into the game set.
     * 
     * <p>
     * If the specified tile is a blank tile (represented by the blank tile
     * character, such as '?' or '*'), the blank tile count is incremented.
     * Otherwise, the method validates the letter, determines its index, and
     * increments the corresponding count.
     * </p>
     *
     * @param letter the character representing the tile to be added.
     * @throws IllegalStateException    if adding the tile would exceed its
     *                                  predefined limit.
     * @throws IllegalArgumentException if the provided letter is invalid.
     */
    public void addLetter(char letter);

    /**
     * Removes a tile from the game set.
     * 
     * <p>
     * If the specified tile is a blank tile (represented by the blank tile
     * character, '?' or '*'), the blank tile count is decremented. Otherwise,
     * the method validates the letter, determines its index, and decrements the
     * corresponding count.
     * </p>
     *
     * @param letter the character representing the tile to be removed.
     * @throws IllegalStateException    if there are no more tiles of the specified
     *                                  type to remove.
     * @throws IllegalArgumentException if the provided letter is invalid.
     */
    public void removeLetter(char letter);
    
    /**
     * Randomly draws a tile from the available tiles in the game set.
     * 
     * <p>
     * This method simulates drawing a random tile from the remaining set.
     * It updates the counts accordingly and returns the drawn tile.
     * </p>
     *
     * @return the character representing the drawn tile.
     * @throws IllegalStateException if no tiles remain to be drawn.
     */
    public char drawRandomTile();

    /**
     * Checks whether the given word can be constructed from the remaining
     * tiles.
     *
     * <p>
     * This method verifies that for each letter in the word the available count
     * (including blank tiles used as wildcards) is sufficient to form the word.
     * </p>
     *
     * @param word the word to validate.
     * @return {@code true} if the word can be constructed; {@code false}
     *         otherwise.
     * @throws IllegalArgumentException if the word is null or contains invalid
     *                                  characters.
     */
    public boolean canConstructWord(String word);

    /**
     * Resets the tile data to its initial state.
     * 
     * <p>
     * This method restores the tile counts to the original limits,
     * including the total tile count and blank tile count.
     * </p>
     */
    public void reset();    
    
    /**
     * Retrieves the maximum available count for a specific letter tile.
     * 
     * <p>
     * The method converts the letter to uppercase, validates that it falls
     * within the range A-Z, and then returns the corresponding limit from the
     * {@code TILE_LIMITS} array.
     * </p>
     *
     * @param letter the letter for which to retrieve the tile count.
     * @return the maximum number of tiles available for the specified letter.
     * @throws IllegalArgumentException if the letter is not in the range A-Z.
     */
    public int getLetterTileCount(char letter);
    
    public static class InvalidTileRemovalException extends IllegalStateException {
        
        public InvalidTileRemovalException() {
        }

        public InvalidTileRemovalException(char c) {
            super("No more %c tiles remaining".formatted(c));
        }
        public InvalidTileRemovalException(String s) {
            super(s);
        }

        public InvalidTileRemovalException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidTileRemovalException(Throwable cause) {
            super(cause);
        }
        
    }
    
}