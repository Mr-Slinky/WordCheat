package com.slinky.wordcheat.model;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents and manages the tile set used in a specific WWF game.
 *
 * <p>
 * This class functions both as a utility for scoring words based on tile values
 * and as a state-tracking object for the tiles available in the game. It
 * maintains counts for each letter tile as well as blank tiles, and provides
 * operations to add or remove tiles from the game set. Additionally, it offers
 * methods to query the current state, draw random tiles, validate words based
 * on available tiles, and reset the tile set to its initial state.
 *
 * <p>
 * The game uses a fixed total of 104 tiles, including 2 blank tiles. Each
 * letter from A to Z has a predefined point value and a limit to the number of
 * tiles available. The distribution is as follows:
 *
 * <table border="1">
 *   <caption>Tile distribution and point values</caption>
 *   <thead>
 *     <tr>
 *       <th>Letter</th>
 *       <th>Points</th>
 *       <th>Tile Count</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr><td>A</td><td>1</td><td>9</td></tr>
 *     <tr><td>B</td><td>4</td><td>2</td></tr>
 *     <tr><td>C</td><td>4</td><td>2</td></tr>
 *     <tr><td>D</td><td>2</td><td>5</td></tr>
 *     <tr><td>E</td><td>1</td><td>13</td></tr>
 *     <tr><td>F</td><td>4</td><td>2</td></tr>
 *     <tr><td>G</td><td>3</td><td>3</td></tr>
 *     <tr><td>H</td><td>3</td><td>4</td></tr>
 *     <tr><td>I</td><td>1</td><td>8</td></tr>
 *     <tr><td>J</td><td>10</td><td>1</td></tr>
 *     <tr><td>K</td><td>5</td><td>1</td></tr>
 *     <tr><td>L</td><td>2</td><td>4</td></tr>
 *     <tr><td>M</td><td>4</td><td>2</td></tr>
 *     <tr><td>N</td><td>2</td><td>5</td></tr>
 *     <tr><td>O</td><td>1</td><td>8</td></tr>
 *     <tr><td>P</td><td>4</td><td>2</td></tr>
 *     <tr><td>Q</td><td>10</td><td>1</td></tr>
 *     <tr><td>R</td><td>1</td><td>6</td></tr>
 *     <tr><td>S</td><td>1</td><td>5</td></tr>
 *     <tr><td>T</td><td>1</td><td>7</td></tr>
 *     <tr><td>U</td><td>2</td><td>4</td></tr>
 *     <tr><td>V</td><td>5</td><td>2</td></tr>
 *     <tr><td>W</td><td>4</td><td>2</td></tr>
 *     <tr><td>X</td><td>8</td><td>1</td></tr>
 *     <tr><td>Y</td><td>3</td><td>2</td></tr>
 *     <tr><td>Z</td><td>10</td><td>1</td></tr>
 *     <tr><td>Blank</td><td>0</td><td>2</td></tr>
 *   </tbody>
 * </table>
 *
 * @author Kheagen Haskins
 */
public final class DefaultTileSet implements TileSet {

    // ================================[ Static ]================================ \\

    /**
     * The total number of tiles in the game set.
     */
    public static final int TOTAL_TILE_COUNT = 104;

    /**
     * The total number of blank tiles available in the game set.
     */
    public static final int WILDCARD_COUNT = 2;

    /**
     * An array of tile limits for each letter from A to Z.
     * <p>
     * The limit at index 0 corresponds to 'A', index 1 to 'B', etc.
     * 
     */
    private static final int[] TILE_LIMITS = {
        9, 2, 2, 5, 13, 2, 3, 4, 8, 1,
        1, 4, 2, 5, 8, 2, 1, 6, 5, 7,
        4, 2, 2, 1, 2, 1
    };

    // ================================[ Fields ]================================ \\
    /**
     * The current total count of tiles remaining in the game set.
     */
    private int remainingTileCount = TOTAL_TILE_COUNT;

    /**
     * The current count of wildcards remaining.
     */
    private int wildCardCount = WILDCARD_COUNT;

    /**
     * An array tracking the remaining count for each letter tile.
     * <p>
     * The index 0 corresponds to 'A', index 1 to 'B', and so forth.
     * 
     */
    private int[] tileCounts;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code TileData} object with the default tile set.
     * <p>
     * This initialises the instance with 104 total tiles and 2 blank tiles.
     * The letter-specific counts are set based on the predefined tile limits.
     * 
     */
    public DefaultTileSet() {
        tileCounts = Arrays.copyOf(TILE_LIMITS, TILE_LIMITS.length);
    }

    /**
     * Constructs a new {@code TileData} object and removes the specified letters
     * from the initial tile set.
     * <p>
     * The removal of letters adjusts both the total tile count and the count for
     * each individual letter. If an invalid removal is attempted, an exception
     * is thrown.
     * 
     *
     * @param letters an array of characters representing the letters to be
     *                removed from the tile set.
     * @throws IllegalStateException if attempting to remove more tiles than are
     *                               available.
     */
    public DefaultTileSet(char[] letters) {
        this();
        for (char letter : letters) {
            removeLetter(letter);
        }
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the total number of tiles remaining in the game set.
     *
     * @return the total count of remaining tiles.
     */
    @Override
    public int getRemainingTileCount() {
        return remainingTileCount;
    }

    /**
     * Returns the number of blank tiles remaining in the game set.
     *
     * @return the count of remaining blank tiles.
     */
    @Override
    public int getRemainingWildcardCount() {
        return wildCardCount;
    }
    
    /**
     * Returns the maximum number of tiles
     * 
     * @return the maximum number of tiles
     */
    @Override
    public int getMaxTileCount() {
        return TOTAL_TILE_COUNT;
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Adds a tile back into the game set.
     * 
     * <p>
     * If the specified tile is a blank tile (represented by the blank tile
     * character, '?' or '*'), the blank tile count is incremented. Otherwise,
     * the method validates the letter, determines its index, and increments the
     * corresponding count.
     * 
     *
     * @param letter the character representing the tile to be added.
     * @throws IllegalStateException    if adding the tile would exceed its
     *                                  predefined limit.
     * @throws IllegalArgumentException if the provided letter is invalid.
     */
    @Override
    public void addLetter(char letter) {
        switch (letter) {
            case TileSet.WILDCARD:
                if (wildCardCount >= WILDCARD_COUNT) {
                    throw new IllegalStateException("Cannot add more blank tiles. Limit is " + WILDCARD_COUNT);
                }
                
                wildCardCount++;
                break;
            case ' ':
                return;
            default:
                letter        = validateLetter(letter);
                int index     = letter - 'A';
                int remaining = tileCounts[index];
                if (remaining >= TILE_LIMITS[index]) {
                    throw new IllegalStateException("Cannot add more '%s' tiles. Limit is %d".formatted(letter, TILE_LIMITS[index]));
                }
                
                tileCounts[index]++;
        }
        
        remainingTileCount++;
    }

    /**
     * Removes a tile from the game set.
     * 
     * <p>
     * If the specified tile is a blank tile (represented by the blank tile
     * character, '?' or '*'), the blank tile count is decremented. Otherwise,
     * the method validates the letter, determines its index, and decrements the
     * corresponding count.
     * 
     *
     * @param letter the character representing the tile to be removed.
     * @throws IllegalStateException    if there are no more tiles of the specified
     *                                  type to remove.
     * @throws IllegalArgumentException if the provided letter is invalid.
     */
    @Override
    public void removeLetter(char letter) {
        switch (letter) {
            case TileSet.WILDCARD:
                if (wildCardCount <= 0) {
                    throw new InvalidTileRemovalException("No more wildcards remaining");
                }
                
                wildCardCount--;
                break;
            case ' ':
                return;
            default:
                letter        = validateLetter(letter);
                int index     = letter - 'A';
                if (tileCounts[index] > 0) {
                    tileCounts[index]--;
                } else if (wildCardCount > 0) {
                    // Every tile of this letter is accounted for, so this one must be a blank.
                    wildCardCount--;
                } else {
                    throw new InvalidTileRemovalException("No more '%c' tiles remaining".formatted(letter));
                }
        }
        
        remainingTileCount--;
    }
    
    /**
     * Returns the remaining count for the specified tile.
     * 
     * <p>
     * If the specified tile represents a blank (i.e. {@code BLANK_TILE}, '?' or
     * '*'), this method returns the number of blank tiles remaining. Otherwise,
     * the letter is validated and its remaining count is returned.
     * 
     *
     * @param letter the tile to query.
     * @return the number of remaining tiles for the specified letter.
     * @throws IllegalArgumentException if the letter is invalid.
     */
    @Override
    public int getRemainingTileCount(char letter) {
        switch (letter) {
            case TileSet.WILDCARD, ' ', '*':
                return wildCardCount;
            default:
                letter = validateLetter(letter);
                return tileCounts[letter - 'A'];
        }
    }

    /**
     * Randomly draws a tile from the available tiles in the game set.
     * 
     * <p>
     * This method simulates drawing a random tile from the remaining set.
     * It updates the counts accordingly and returns the drawn tile.
     * 
     *
     * @return the character representing the drawn tile.
     * @throws IllegalStateException if no tiles remain to be drawn.
     */
    @Override
    public char drawRandomTile() {
        if (remainingTileCount == 0) {
            throw new InvalidTileRemovalException("No tiles remain to be drawn.");
        }
        
        int randomIndex = ThreadLocalRandom.current().nextInt(remainingTileCount);
        if (randomIndex < wildCardCount) {
            wildCardCount--;
            remainingTileCount--;
            return WILDCARD;
        }
        
        int letterIndex = randomIndex - wildCardCount;
        for (int i = 0; i < tileCounts.length; i++) {
            if (letterIndex < tileCounts[i]) {
                tileCounts[i]--;
                remainingTileCount--;
                return (char) ('A' + i);
            } else {
                letterIndex -= tileCounts[i];
            }
        }
        
        throw new IllegalStateException("Tile selection error.");
    }

    /**
     * Checks whether the given word can be constructed from the remaining
     * tiles.
     *
     * <p>
     * This method verifies that for each letter in the word the available count
     * (including blank tiles used as wildcards) is sufficient to form the word.
     * 
     *
     * @param word the word to validate.
     * @return {@code true} if the word can be constructed; {@code false}
     *         otherwise.
     * @throws IllegalArgumentException if the word is null or contains invalid
     *                                  characters.
     */
    @Override
    public boolean canConstructWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word cannot be null.");
        }
        // Create a copy of the current tile counts.
        int[] availableTiles = Arrays.copyOf(tileCounts, tileCounts.length);
        int availableBlanks  = wildCardCount;
        
        // Convert the word to uppercase for validation.
        for (char c : word.toUpperCase().toCharArray()) {
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException("Invalid character in word: " + c);
            }
            
            int index = c - 'A';
            if (availableTiles[index] > 0) {
                availableTiles[index]--;
            } else if (availableBlanks > 0) {
                availableBlanks--;
            } else {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Resets the tile data to its initial state.
     * 
     * <p>
     * This method restores the tile counts to the original limits,
     * including the total tile count and blank tile count.
     * 
     */
    @Override
    public void reset() {
        tileCounts         = Arrays.copyOf(TILE_LIMITS, TILE_LIMITS.length);
        wildCardCount      = WILDCARD_COUNT;
        remainingTileCount = TOTAL_TILE_COUNT;
    }    
    
    /**
     * Retrieves the maximum available count for a specific letter tile.
     * 
     * <p>
     * The method converts the letter to uppercase, validates that it falls
     * within the range A-Z, and then returns the corresponding limit from the
     * {@code TILE_LIMITS} array.
     * 
     *
     * @param letter the letter for which to retrieve the tile count.
     * @return the maximum number of tiles available for the specified letter.
     * @throws IllegalArgumentException if the letter is not in the range A-Z.
     */
    @Override
    public int getTileMaxCount(char letter) {
        letter    = validateLetter(letter);
        int index = letter - 'A';
        
        return TILE_LIMITS[index];
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Validates that the provided character is an uppercase letter between A
     * and Z.
     * 
     * <p>
     * The method converts the character to uppercase and checks that it lies
     * within the valid range. If the character is invalid, an
     * {@code IllegalArgumentException} is thrown.
     * 
     *
     * @param c the character to validate.
     * @return  the uppercase version of the character if it is valid.
     * @throws  IllegalArgumentException if the character is not a valid letter
     *                                   between A and Z.
     */
    private static char validateLetter(char c) {
        c = Character.toUpperCase(c);
        if (c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("Invalid letter: " + c);
        }
        
        return c;
    }

}