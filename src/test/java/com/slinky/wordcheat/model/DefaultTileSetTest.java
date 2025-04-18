package com.slinky.wordcheat.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Comprehensive test class for {@code DefaultTileSet} – a class that manages
 * the WWF game tile set.
 *
 * <p>
 * This class tests:
 * <ul>
 *   <li>The initial state and accessor methods.</li>
 *   <li>The behavior of {@code addLetter(char)} and {@code removeLetter(char)} for A–Z and blank tiles.</li>
 *   <li>The {@code drawRandomTile()} method, including its error when empty.</li>
 *   <li>The {@code canConstructWord(String)} method using various words.</li>
 *   <li>The {@code reset()} method to restore the tile set to its initial state.</li>
 *   <li>The {@code getLetterTileCount(char)} method for maximum limits.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Parameterised tests are used for iterating over letters A–Z and for robust
 * coverage of word construction scenarios.
 * </p>
 *
 * @author Kheagen
 */
public class DefaultTileSetTest {

    /**
     * Test that a new DefaultTileSet initializes with the correct state.
     */
    @Test
    @DisplayName("Initial state: total count, blank count, and per-letter counts")
    void testInitialState() {
        DefaultTileSet tileSet = new DefaultTileSet();
        // Check total remaining tile count.
        assertEquals(DefaultTileSet.TOTAL_TILE_COUNT, tileSet.getRemainingTileCount(),
                "Total tile count should be " + DefaultTileSet.TOTAL_TILE_COUNT);
        // Check blank tile count.
        assertEquals(DefaultTileSet.WILDCARD_COUNT, tileSet.getRemainingWildcardCount(),
                "Blank tile count should be " + DefaultTileSet.WILDCARD_COUNT);
        
        // Expected tile limits for letters A to Z.
        int[] limits = {9, 2, 2, 5, 13, 2, 3, 4, 8, 1, 1, 4, 2, 5, 8, 2, 1, 6, 5, 7, 4, 2, 2, 1, 2, 1};
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            int index = letter - 'A';
            int expected = limits[index];
            assertEquals(expected, tileSet.getRemainingTileCount(letter),
                    "Tile count for " + letter + " should be " + expected);
        }
    }

    /* --------------------- Tests for addLetter(char) and removeLetter(char) --------------------- */

    /**
     * Provides all uppercase letters A–Z.
     */
    static Stream<Arguments> letterProvider() {
        return Stream.iterate('A', c -> (char)(c + 1)).limit(26)
                .map(c -> Arguments.of(c));
    }
    
    /**
     * Tests that removing a letter decreases its count and the total tile count,
     * and that adding it back restores the counts.
     */
    @ParameterizedTest(name = "Remove then add letter {0}")
    @MethodSource("letterProvider")
    @DisplayName("Remove and then add a tile for each letter A–Z")
    void testRemoveAndAddLetter(char letter) {
        DefaultTileSet tileSet = new DefaultTileSet();
        int initialCount = tileSet.getRemainingTileCount(letter);
        assumeTrue(initialCount > 0, "Tile for " + letter + " should be available initially.");
        
        // Remove one tile for the letter.
        tileSet.removeLetter(letter);
        int countAfterRemove = tileSet.getRemainingTileCount(letter);
        int totalAfterRemove = tileSet.getRemainingTileCount();
        
        assertAll(
            () -> assertEquals(initialCount - 1, countAfterRemove,
                    "Count for " + letter + " should decrease by 1 after removal."),
            () -> assertEquals(DefaultTileSet.TOTAL_TILE_COUNT - 1, totalAfterRemove,
                    "Total tile count should decrease by 1 after removal.")
        );
        
        // Add the letter back.
        tileSet.addLetter(letter);
        int countAfterAdd = tileSet.getRemainingTileCount(letter);
        int totalAfterAdd = tileSet.getRemainingTileCount();
        
        assertAll(
            () -> assertEquals(initialCount, countAfterAdd,
                    "Count for " + letter + " should be restored after adding."),
            () -> assertEquals(DefaultTileSet.TOTAL_TILE_COUNT, totalAfterAdd,
                    "Total tile count should be restored after adding.")
        );
    }

    /**
     * Tests that adding a letter when its count is already at its maximum limit throws an exception.
     * (Since the default state equals the maximum, any addLetter call for a letter should fail.)
     */
    @ParameterizedTest(name = "Adding letter {0} beyond limit throws exception")
    @MethodSource("letterProvider")
    @DisplayName("addLetter(char) throws exception when tile limit exceeded for A–Z")
    void testAddLetterOverLimit(char letter) {
        DefaultTileSet tileSet = new DefaultTileSet();
        // In the initial state the count for each letter equals its limit.
        assertThrows(IllegalStateException.class, () -> tileSet.addLetter(letter),
                "Should throw exception when adding extra tile for " + letter);
    }
    
    /**
     * Tests that attempting to remove a letter when none remain throws an exception.
     */
    @ParameterizedTest(name = "Removing letter {0} until exhaustion throws exception")
    @MethodSource("letterProvider")
    @DisplayName("removeLetter(char) throws exception when no tiles remain for A–Z")
    void testRemoveLetterNoTileRemaining(char letter) {
        DefaultTileSet tileSet = new DefaultTileSet();
        int count = tileSet.getRemainingTileCount(letter);
        // Remove all available tiles for the letter.
        for (int i = 0; i < count; i++) {
            tileSet.removeLetter(letter);
        }
        // Now, removal should fail.
        assertThrows(IllegalStateException.class, () -> tileSet.removeLetter(letter),
                "Should throw exception when removing letter " + letter + " with no tiles remaining.");
    }
    
    /**
     * Tests addLetter and removeLetter for blank tiles.
     */
    @Test
    @DisplayName("Test addLetter and removeLetter for blank tiles")
    void testBlankTileAddRemove() {
        DefaultTileSet tileSet = new DefaultTileSet();
        // Check initial blank count.
        assertEquals(2, tileSet.getRemainingTileCount('?'), "Initial blank tile count should be 2.");
        // Remove one blank tile.
        tileSet.removeLetter('?');
        assertEquals(1, tileSet.getRemainingTileCount('?'), "After removal, blank tile count should be 1.");
        // Add a blank tile back.
        tileSet.addLetter('?');
        assertEquals(2, tileSet.getRemainingTileCount('?'), "After adding, blank tile count should be 2.");
        // Adding a blank tile beyond the limit should throw an exception.
        assertThrows(IllegalStateException.class, () -> tileSet.addLetter('?'),
                "Should not allow adding a blank tile beyond its limit.");
        // Remove both blank tiles.
        tileSet.removeLetter('?');
        tileSet.removeLetter('?');
        assertEquals(0, tileSet.getRemainingTileCount('?'), "After removal, blank tile count should be 0.");
        // Removing a blank when none remain should throw.
        assertThrows(IllegalStateException.class, () -> tileSet.removeLetter('?'),
                "Should throw exception when removing a blank tile with none remaining.");
    }
    
    /* --------------------- Tests for drawRandomTile() --------------------- */
    
    /**
     * Tests that drawing a random tile reduces the tile count and returns a valid tile.
     */
    @Test
    @DisplayName("drawRandomTile() reduces count and returns a valid tile")
    void testDrawRandomTile() {
        DefaultTileSet tileSet = new DefaultTileSet();
        int before = tileSet.getRemainingTileCount();
        char drawn = tileSet.drawRandomTile();
        int after = tileSet.getRemainingTileCount();
        assertAll(
            () -> assertEquals(before - 1, after, "Remaining tile count should decrease by 1 after drawing a tile."),
            () -> {
                // The drawn tile should be a blank or a letter between A and Z.
                if (drawn != DefaultTileSet.BLANK_TILE) {
                    assertTrue(drawn >= 'A' && drawn <= 'Z', "Drawn tile should be between A and Z (or a blank).");
                }
            }
        );
    }
    
    /**
     * Tests that attempting to draw a tile when none remain throws an exception.
     */
    @Test
    @DisplayName("drawRandomTile() throws exception when tile set is empty")
    void testDrawRandomTileWhenEmpty() {
        DefaultTileSet tileSet = new DefaultTileSet();
        // Remove all tiles by drawing until empty.
        while (tileSet.getRemainingTileCount() > 0) {
            tileSet.drawRandomTile();
        }
        assertThrows(IllegalStateException.class, () -> tileSet.drawRandomTile(),
                "Should throw exception when no tiles remain to be drawn.");
    }
    
    /* --------------------- Tests for canConstructWord(String) --------------------- */
    
    /**
     * Provides test cases for canConstructWord(String). Each argument includes a word and the expected outcome.
     * Note: If a word contains invalid characters, an IllegalArgumentException is expected.
     */
    static Stream<Arguments> canConstructWordProvider() {
        return Stream.of(
                // Word is constructible from the full tile set.
                Arguments.of("CAT", true),
                // 1 Z and a Wildcard
                Arguments.of("ZZ", true),
                // 1 Z and 2 Wildcard
                Arguments.of("ZZZ", true),
                // 1 Z and 2 Wildcard, 1 extra
                Arguments.of("ZZZZ", false),
                // "QUIZ" should be constructible (Q, U, I, Z are available).
                Arguments.of("QUIZ", true),
                // Word with an invalid character (digit) – should throw exception.
                Arguments.of("HELLO1", false)
        );
    }

    /**
     * Tests the behavior of canConstructWord(String).
     * When the word contains an invalid character, an exception is expected.
     */
    @ParameterizedTest(name = "canConstructWord(\"{0}\") expected {1}")
    @MethodSource("canConstructWordProvider")
    @DisplayName("Test canConstructWord(String) with various words")
    void testCanConstructWord(String word, boolean expected) {
        DefaultTileSet tileSet = new DefaultTileSet();
        if (word.matches(".*\\d.*")) {
            assertThrows(IllegalArgumentException.class, () -> tileSet.canConstructWord(word),
                    "Word with invalid character(s) should throw IllegalArgumentException.");
        } else {
            boolean result = tileSet.canConstructWord(word);
            assertEquals(expected, result,
                    "canConstructWord(\"" + word + "\") should return " + expected);
        }
    }
    
    /* --------------------- Tests for reset() --------------------- */
    
    /**
     * Tests that reset() restores the tile set to its initial state.
     */
    @Test
    @DisplayName("reset() restores initial tile counts")
    void testReset() {
        DefaultTileSet tileSet = new DefaultTileSet();
        // Modify the state by removing some tiles.
        tileSet.removeLetter('A');
        tileSet.removeLetter('E');
        tileSet.removeLetter('?');
        // Call reset().
        tileSet.reset();
        // Check total counts and blank count.
        assertAll(
            () -> assertEquals(DefaultTileSet.TOTAL_TILE_COUNT, tileSet.getRemainingTileCount(),
                    "After reset, total tile count should be " + DefaultTileSet.TOTAL_TILE_COUNT),
            () -> assertEquals(DefaultTileSet.WILDCARD_COUNT, tileSet.getRemainingWildcardCount(),
                    "After reset, blank tile count should be " + DefaultTileSet.WILDCARD_COUNT)
        );
        // Check per-letter counts.
        int[] limits = {9, 2, 2, 5, 13, 2, 3, 4, 8, 1, 1, 4, 2, 5, 8, 2, 1, 6, 5, 7, 4, 2, 2, 1, 2, 1};
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            int expected = limits[letter - 'A'];
            assertEquals(expected, tileSet.getRemainingTileCount(letter),
                    "After reset, tile count for " + letter + " should be " + expected);
        }
    }
    
    /* --------------------- Test for getLetterTileCount(char) --------------------- */
    
    /**
     * Provides each letter A–Z along with its expected maximum count.
     */
    static Stream<Arguments> letterTileCountProvider() {
        int[] limits = {9, 2, 2, 5, 13, 2, 3, 4, 8, 1, 1, 4, 2, 5, 8, 2, 1, 6, 5, 7, 4, 2, 2, 1, 2, 1};
        return Stream.iterate(0, i -> i + 1).limit(26)
                .map(i -> Arguments.of((char) ('A' + i), limits[i]));
    }
    
    /**
     * Tests that getLetterTileCount(char) returns the correct limit for each letter.
     */
    @ParameterizedTest(name = "getLetterTileCount({0}) should return {1}")
    @MethodSource("letterTileCountProvider")
    @DisplayName("Test getLetterTileCount(char) for letters A–Z")
    void testGetLetterTileCount(char letter, int expectedLimit) {
        DefaultTileSet tileSet = new DefaultTileSet();
        assertEquals(expectedLimit, tileSet.getTileMaxCount(letter),
                "Maximum available tile count for " + letter + " should be " + expectedLimit);
    }
    
}