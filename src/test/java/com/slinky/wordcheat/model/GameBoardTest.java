package com.slinky.wordcheat.model;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Kheagen
 */
public class GameBoardTest {

    /**
     * Returns a test grid where rows 0-3 are completely empty. Rows 4 and
     * beyond are pre-populated with permanent letters.
     */
    private char[][] getTestGrid() {
        return new char[][]{
            // Rows 0-3: completely blank (all cells are BLANK_TILE)
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // Row 0
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // Row 1
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // Row 2
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // Row 3
            // Rows 4-9: pre-populated cells (permanent letters)
            {' ', ' ', ' ', ' ', ' ', ' ', 'F', 'O', 'R', 'B'}, // Row 4
            {' ', 'E', 'N', 'R', 'O', 'B', 'E', 'D', ' ', 'Y'}, // Row 5
            {' ', ' ', ' ', ' ', 'H', 'O', 'N', 'E', 'S', 'T'}, // Row 6
            {' ', ' ', ' ', ' ', ' ', 'W', ' ', ' ', ' ', 'E'}, // Row 7
            {' ', ' ', ' ', ' ', 'J', 'E', 'E', ' ', ' ', ' '}, // Row 8
            {' ', ' ', ' ', ' ', 'A', 'R', 'F', ' ', ' ', ' '} // Row 9
        };
    }

    // Use the getLargeTestGrid() from your test class.
    private char[][] getLargeTestGrid() {
        return new char[][]{
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', 'O', 'R', 'B', ' ', ' ', ' '},
            {' ', ' ', ' ', 'E', 'N', 'R', 'O', 'B', 'E', 'D', ' ', 'Y', ' ', ' ', ' '},
            {'P', 'H', 'E', 'W', ' ', ' ', 'H', 'O', 'N', 'E', 'S', 'T', 'Y', ' ', ' '},
            {'L', 'I', 'T', 'E', ' ', ' ', ' ', 'W', ' ', ' ', ' ', 'E', 'O', ' ', ' '},
            {'U', ' ', ' ', ' ', ' ', ' ', 'J', 'E', 'E', ' ', ' ', ' ', 'D', ' ', ' '},
            {'G', ' ', ' ', 'P', ' ', ' ', 'A', 'R', 'F', ' ', ' ', 'C', 'H', 'I', 'T'},
            {'S', ' ', ' ', 'A', ' ', ' ', 'R', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' '},
            {' ', 'R', 'I', 'T', 'U', 'A', 'L', ' ', ' ', 'T', 'E', 'A', 'M', ' ', ' '},
            {' ', ' ', ' ', 'E', ' ', ' ', 'S', 'I', 'Z', 'E', ' ', 'D', ' ', ' ', ' '}
        };
    }

    // Our constant expected blank tile (should match TileSet.BLANK_TILE)
    private final char BLANK_TILE = ' ';
    private GameBoard testBoard;

    @BeforeEach
    public void setUp() {
        testBoard = new GameBoard(getTestGrid());
    }
    
    // --- Test for getRows() and getCols() ---
    @Test
    public void testGetRowsAndCols() {
        GameBoard board = new GameBoard(getLargeTestGrid());
        // The large grid has 15 rows and 15 columns.
        assertAll("Dimensions",
                () -> assertEquals(15, board.getRows(), "Row count should be 15"),
                () -> assertEquals(15, board.getCols(), "Column count should be 15")
        );
    }

    // --- Data provider for valid indices for getLetterAt ---
    private static Stream<Arguments> provideValidLetterAtIndices() {
        // Use specific indices from getLargeTestGrid()
        // Note: rows 0-5 are empty so expected letter is BLANK_TILE.
        // From row 7, col 3 = 'E'; row 8, col 0 = 'P'; row 11, col 0 = 'G';
        // row 13, col 3 = 'T'; row 14, col 3 = 'E'.
        return Stream.of(
                Arguments.of(0, 0, ' '),
                Arguments.of(7, 3, 'E'),
                Arguments.of(8, 0, 'P'),
                Arguments.of(11, 0, 'G'),
                Arguments.of(13, 3, 'T'),
                Arguments.of(14, 3, 'E')
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidLetterAtIndices")
    public void testGetLetterAt_ValidIndices(int row, int col, char expected) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        // If the cell is supposed to have a letter (i.e. a non-blank) then hasLetterAt should be true.
        boolean expectedHasLetter = expected != BLANK_TILE;
        assertAll("getLetterAt at [" + row + "][" + col + "]",
                () -> assertEquals(expected, board.getLetterAt(row, col), "Returned letter should match expected"),
                () -> assertEquals(expectedHasLetter, board.hasLetterAt(row, col), "hasLetter flag should be " + expectedHasLetter)
        );
    }

    // --- Data provider for invalid indices for getLetterAt and hasLetterAt ---
    private static Stream<Arguments> provideInvalidIndices() {
        return Stream.of(
                Arguments.of(-1, 0),
                Arguments.of(15, 0),
                Arguments.of(0, -1),
                Arguments.of(0, 15)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidIndices")
    public void testGetLetterAt_OutOfBounds(int row, int col) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertThrows(IndexOutOfBoundsException.class, () -> board.getLetterAt(row, col),
                "getLetterAt should throw IndexOutOfBoundsException for indices [" + row + "][" + col + "]");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidIndices")
    public void testHasLetter_OutOfBounds(int row, int col) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertThrows(IndexOutOfBoundsException.class, () -> board.hasLetterAt(row, col),
                "hasLetter should throw IndexOutOfBoundsException for indices [" + row + "][" + col + "]");
    }

    // --- Data provider for rowHasLetters ---
    private static Stream<Arguments> provideRowHasLettersIndices() {
        // Rows 0-5 are empty; rows 6-14 have at least one letter.
        return IntStream.range(0, 15)
                .mapToObj(row -> Arguments.of(row, row >= 6));
    }

    @ParameterizedTest
    @MethodSource("provideRowHasLettersIndices")
    public void testRowHasLetters_Valid(int row, boolean expected) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertEquals(expected, board.rowHasLetters(row),
                "rowHasLetters for row " + row + " should be " + expected);
    }

    // --- Out-of-bound tests for rowHasLetters ---
    private static Stream<Arguments> provideInvalidRowIndices() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(15)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRowIndices")
    public void testRowHasLetters_OutOfBounds(int row) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertThrows(IndexOutOfBoundsException.class, () -> board.rowHasLetters(row),
                "rowHasLetters should throw IndexOutOfBoundsException for row " + row);
    }

    // --- Data provider for colHasLetters ---
    private static Stream<Arguments> provideColHasLettersIndices() {
        // Every column in getLargeTestGrid() has at least one letter.
        return IntStream.range(0, 15)
                .mapToObj(col -> Arguments.of(col, true));
    }

    @ParameterizedTest
    @MethodSource("provideColHasLettersIndices")
    public void testColHasLetters_Valid(int col, boolean expected) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertEquals(expected, board.colHasLetters(col),
                "colHasLetters for column " + col + " should be " + expected);
    }

    // --- Out-of-bound tests for colHasLetters ---
    private static Stream<Arguments> provideInvalidColIndices() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(15)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidColIndices")
    public void testColHasLetters_OutOfBounds(int col) {
        GameBoard board = new GameBoard(getLargeTestGrid());
        assertThrows(IndexOutOfBoundsException.class, () -> board.colHasLetters(col),
                "colHasLetters should throw IndexOutOfBoundsException for column " + col);
    }

    // PL1: Valid placement on an empty cell.
    private static Stream<Arguments> provideValidEmptyCellIndices() {
        // Choose indices in rows 0-3 which are completely empty in the test grid.
        return Stream.of(
                Arguments.of(0, 0, 'X'),
                Arguments.of(1, 5, 'Y'),
                Arguments.of(2, 9, 'Z'),
                Arguments.of(3, 3, 'A')
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidEmptyCellIndices")
    public void testPlaceLetterAt_ValidPlacement(int row, int col, char letter) {
        GameBoard board = new GameBoard(getTestGrid());
        boolean result = board.placeLetterAt(letter, row, col);
        // After placement, verify:
        // - the method returns true,
        // - the matrix cell holds the letter,
        // - hasLetterAt is true,
        // - newLetter flag is true,
        // - the row and column are marked as populated.
        assertAll("Valid placement at (" + row + "," + col + ")",
                () -> assertTrue(result, "Placement should return true"),
                () -> assertEquals(letter, board.getLetterAt(row, col), "Cell should contain the placed letter"),
                () -> assertTrue(board.hasLetterAt(row, col), "hasLetter flag should be true"),
                () -> assertTrue(board.isNewLetter(row, col), "newLetter flag should be true"),
                () -> assertTrue(board.rowHasLetters(row), "Row " + row + " should be marked as populated"),
                () -> assertTrue(board.colHasLetters(col), "Column " + col + " should be marked as populated")
        );
    }

    // PL2: Attempt placement on a cell with an existing non-new letter.
    // Use a cell that is already populated from the initial grid.
    // In the test grid, row 4, col 6 initially contains 'F'.
    @Test
    public void testPlaceLetterAt_OnNonNewLetterCell() {
        GameBoard board = new GameBoard(getTestGrid());
        // Verify the cell already contains a letter (and is not new)
        char initialLetter = board.getLetterAt(4, 6);
        assertEquals('F', initialLetter, "Initial letter at (4,6) should be 'F'");
        // Attempt to place a new letter on this cell.
        boolean result = board.placeLetterAt('X', 4, 6);
        assertAll("Placement on non-new letter cell at (4,6)",
                () -> assertFalse(result, "Placement should return false"),
                () -> assertEquals('F', board.getLetterAt(4, 6), "Cell should still contain the original letter 'F'")
        );
    }

    // PL3: Attempt placement on a cell with an existing new letter.
    // The implementation currently allows replacing a new letter.
    @Test
    public void testPlaceLetterAt_OnExistingNewLetterCell() {
        GameBoard board = new GameBoard(getTestGrid());
        // Place a letter on an empty cell.
        boolean firstResult = board.placeLetterAt('A', 0, 0);
        // Immediately attempt a replacement on the same cell.
        boolean secondResult = board.placeLetterAt('B', 0, 0);
        assertAll("Placement on existing new letter cell at (0,0)",
                () -> assertTrue(firstResult, "First placement should return true"),
                () -> assertTrue(secondResult, "Second placement should return true"),
                () -> assertEquals('B', board.getLetterAt(0, 0), "Cell should contain 'B' after replacement")
        );
    }

    // PL4: Exceeding maximum new tiles.
    @Test
    public void testPlaceLetterAt_ExceedMaxNewTiles() {
        GameBoard board = new GameBoard(getTestGrid());
        // Place letters in 7 distinct empty cells (rows 0-3 are fully empty).
        int[][] placements = {
            {0, 0}, {0, 1}, {1, 0}, {1, 1}, {2, 0}, {2, 1}, {3, 0}
        };
        for (int[] pos : placements) {
            boolean result = board.placeLetterAt('X', pos[0], pos[1]);
            assertTrue(result, "Placement at (" + pos[0] + "," + pos[1] + ") should succeed");
        }
        // The next placement should exceed the MAX_NEW_TILES limit (7).
        boolean extraResult = board.placeLetterAt('Y', 3, 3);
        assertFalse(extraResult, "Extra placement should return false due to exceeding maximum new tiles");
    }

    // PL5: Out-of-bound indices.
    private static Stream<Arguments> provideOutOfBoundIndices() {
        // Grid is 10x10 so valid indices are 0 to 9.
        return Stream.of(
                Arguments.of(-1, 0, 'W'),
                Arguments.of(0, -1, 'W'),
                Arguments.of(10, 0, 'W'),
                Arguments.of(0, 10, 'W')
        );
    }

    @ParameterizedTest
    @MethodSource("provideOutOfBoundIndices")
    public void testPlaceLetterAt_OutOfBounds(int row, int col, char letter) {
        GameBoard board = new GameBoard(getTestGrid());
        assertThrows(IndexOutOfBoundsException.class, () -> board.placeLetterAt(letter, row, col),
                "Placing letter at (" + row + "," + col + ") should throw IndexOutOfBoundsException");
    }

    // RS1: Test that calling reset() on a board with no new-letter placements leaves the board unchanged.
    @Test
    public void testReset_NoNewLetters() {
        GameBoard board = new GameBoard(getTestGrid());

        // Capture some initial permanent values.
        char permanentR4C6 = board.getLetterAt(4, 6); // should be 'F'
        char permanentR5C2 = board.getLetterAt(5, 2); // should be 'N'
        boolean row4Before = board.rowHasLetters(4);
        boolean col6Before = board.colHasLetters(6);

        board.reset();

        assertAll("Reset with no new letters",
                () -> assertEquals(permanentR4C6, board.getLetterAt(4, 6), "Permanent letter at (4,6) should remain unchanged."),
                () -> assertEquals(permanentR5C2, board.getLetterAt(5, 2), "Permanent letter at (5,2) should remain unchanged."),
                () -> assertEquals(row4Before, board.rowHasLetters(4), "rowPopulated flag for row 4 should remain unchanged."),
                () -> assertEquals(col6Before, board.colHasLetters(6), "colPopulated flag for column 6 should remain unchanged.")
        );
    }

    // RS2: Test that reset() clears new letters in a row that already contains permanent letters.
    @Test
    public void testReset_RemovesNewLettersButKeepsPermanent() {
        GameBoard board = new GameBoard(getTestGrid());

        // In row 4, columns 0-5 are originally blank.
        // Place a new letter in row 4, column 2.
        boolean placementResult = board.placeLetterAt('X', 4, 2);
        assertTrue(placementResult, "New letter placement at (4,2) should succeed.");

        // Pre-reset assertions.
        assertAll("Pre-reset state for row 4",
                () -> assertEquals('X', board.getLetterAt(4, 2), "Cell (4,2) should contain the new letter 'X'."),
                () -> assertTrue(board.isNewLetter(4, 2), "Cell (4,2) should be flagged as new letter."),
                () -> assertTrue(board.rowHasLetters(4), "Row 4 should be populated due to permanent letters.")
        );

        // Call reset() which should remove the new letter.
        board.reset();

        // After reset, the new letter cell should revert to the blank tile.
        // Permanent letters in row 4 (columns 6-9) should remain.
        assertAll("Post-reset state for row 4",
                () -> assertEquals(TileSet.BLANK_TILE, board.getLetterAt(4, 2), "Cell (4,2) should be reset to blank tile."),
                () -> assertFalse(board.isNewLetter(4, 2), "Cell (4,2) should no longer be flagged as new letter."),
                () -> assertEquals('F', board.getLetterAt(4, 6), "Permanent letter at (4,6) should remain unchanged."),
                () -> assertTrue(board.rowHasLetters(4), "Row 4 should remain populated because of permanent letters."),
                () -> assertTrue(board.colHasLetters(2), "Column 2 should remain populated if other rows have letters.")
        );
    }

    // RS3: Test that reset() clears new letters in a row that becomes empty after reset.
    @Test
    public void testReset_ClearsNewOnlyRow() {
        // Row 0 is originally empty.
        // Place a new letter in row 0, col 0.
        boolean placementResult = testBoard.placeLetterAt('Y', 0, 0);
        assertTrue(placementResult, "New letter placement at (0,0) should succeed.");

        // Before reset, row 0 and column 0 are marked as populated due to the new letter.
        assertAll("Pre-reset state for row 0 and col 0",
                () -> assertEquals('Y', testBoard.getLetterAt(0, 0), "Cell (0,0) should contain the new letter 'Y'."),
                () -> assertTrue(testBoard.rowHasLetters(0), "Row 0 should be marked as populated after placement."),
                () -> assertTrue(testBoard.colHasLetters(0), "Column 0 should be marked as populated after placement.")
        );

        // Call reset() to clear new letters.
        testBoard.reset();

        // After reset, row 0 should be empty and column 0 should be unpopulated if no other row contributes.
        assertAll("Post-reset state for row 0 and col 0",
                () -> assertEquals(TileSet.BLANK_TILE, testBoard.getLetterAt(0, 0), "Cell (0,0) should be reset to blank tile."),
                () -> assertFalse(testBoard.isNewLetter(0, 0), "Cell (0,0) should no longer be flagged as new letter."),
                () -> assertFalse(testBoard.rowHasLetters(0), "Row 0 should not be populated after reset."),
                () -> assertFalse(testBoard.colHasLetters(0), "Column 0 should not be populated after reset.")
        );
    }

    // Helper method to convert placements to a string representation.
    private static String placementsToString(int[][] placements) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < placements.length; i++) {
            sb.append("(").append(placements[i][0]).append(",").append(placements[i][1]).append(")");
            if (i < placements.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Provides test cases for new letter bounds. Each test case consists of: -
     * an array of placements (each as {row, col}) - expected lower new row,
     * expected upper new row, - expected lower new col, expected upper new col.
     */
    private static Stream<Arguments> provideNewLetterBoundScenarios() {
        return Stream.of(
                // NL1: No new placements; expect all bounds to be -1.
                Arguments.of(new int[][]{}, -1, -1, -1, -1),
                // NL2: Single placement at (0,0)
                Arguments.of(new int[][]{{0, 0}}, 0, 0, 0, 0),
                // NL3: Multiple placements in same row: (2,1) and (2,8)
                Arguments.of(new int[][]{{2, 1}, {2, 8}}, 2, 2, 1, 8),
                // NL4: Placements across different rows/cols: (1,5), (2,7), (3,2)
                Arguments.of(new int[][]{{1, 5}, {2, 7}, {3, 2}}, 1, 3, 2, 7)
        );
    }

    @ParameterizedTest(name = "Placements: {0} -> NewRowLower: {1}, NewRowUpper: {2}, NewColLower: {3}, NewColUpper: {4}")
    @MethodSource("provideNewLetterBoundScenarios")
    public void testNewLetterBounds(int[][] placements, int expRowLower, int expRowUpper, int expColLower, int expColUpper) {
        GameBoard board = new GameBoard(getTestGrid());

        // Place new letters at the specified positions.
        // We use 'X' for simplicity.
        for (int[] pos : placements) {
            int row = pos[0];
            int col = pos[1];
            // Since these rows (0-3) are empty, placement should succeed.
            assertTrue(board.placeLetterAt('X', row, col),
                    "Placement at (" + row + "," + col + ") should succeed.");
        }

        // Now check the bounds as returned by the board.
        assertAll("New letter bounds",
                () -> assertEquals(expRowLower, board.getNewRowLowerBound(), "getNewRowLowerBound()"),
                () -> assertEquals(expRowUpper, board.getNewRowUpperBound(), "getNewRowUpperBound()"),
                () -> assertEquals(expColLower, board.getNewColLowerBound(), "getNewColLowerBound()"),
                () -> assertEquals(expColUpper, board.getNewColUpperBound(), "getNewColUpperBound()")
        );
    }

    // Additional tests: Check that if no new letters are placed, all bound methods return -1.
    @Test
    public void testNewLetterBounds_NoPlacements() {
        GameBoard board = new GameBoard(getTestGrid());
        assertAll("No new letters placed",
                () -> assertEquals(-1, board.getNewRowLowerBound(), "getNewRowLowerBound() should be -1"),
                () -> assertEquals(-1, board.getNewRowUpperBound(), "getNewRowUpperBound() should be -1"),
                () -> assertEquals(-1, board.getNewColLowerBound(), "getNewColLowerBound() should be -1"),
                () -> assertEquals(-1, board.getNewColUpperBound(), "getNewColUpperBound() should be -1")
        );
    }
    
       // ---------------------------
    // Tests for findLeftMostLetter
    // ---------------------------
    
    private static Stream<Arguments> provideLeftMostLetterScenarios() {
        return Stream.of(
            // TC L1: Row 4, starting at col 8 ("R" in "FORB") should return col 6 (letter 'F').
            Arguments.of(4, 8, 6),
            // TC L2: Row 5, starting at col 4 ("O" in "ENROBED") should return col 1 (letter 'E').
            Arguments.of(5, 4, 1),
            // TC L3: Row 5, starting at col 9 ("Y" isolated) should return 9.
            Arguments.of(5, 9, 9),
            // TC L4: Row 8, starting at col 6 ("E" in "JEE") should return col 4 (letter 'J').
            Arguments.of(8, 6, 4)
        );
    }
    
    @ParameterizedTest(name = "findLeftMostLetter: row={0}, startCol={1} -> expected={2}")
    @MethodSource("provideLeftMostLetterScenarios")
    public void testFindLeftMostLetter(int row, int startCol, int expectedLeftMost) {
        int result = testBoard.findLeftMostLetter(row, startCol);
        assertEquals(expectedLeftMost, result,
                     "Expected leftmost letter in row " + row + " starting at col " + startCol + " to be at col " + expectedLeftMost);
    }
    
    // ---------------------------
    // Tests for findRightMostLetter
    // ---------------------------
    
    private static Stream<Arguments> provideRightMostLetterScenarios() {
        return Stream.of(
            // TC R1: Row 5, starting at col 3 ("R" in "ENROBED") should traverse right to col 7 (letter 'D').
            Arguments.of(5, 3, 7),
            // TC R2: Row 8, starting at col 4 ("J" in "JEE") should traverse right to col 6 (letter 'E').
            Arguments.of(8, 4, 6)
        );
    }
    
    @ParameterizedTest(name = "findRightMostLetter: row={0}, startCol={1} -> expected={2}")
    @MethodSource("provideRightMostLetterScenarios")
    public void testFindRightMostLetter(int row, int startCol, int expectedRightMost) {
        int result = testBoard.findRightMostLetter(row, startCol);
        assertEquals(expectedRightMost, result,
                     "Expected rightmost letter in row " + row + " starting at col " + startCol + " to be at col " + expectedRightMost);
    }
    
}