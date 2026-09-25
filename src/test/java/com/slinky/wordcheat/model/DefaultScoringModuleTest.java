package com.slinky.wordcheat.model;

import static com.slinky.wordcheat.model.TestGrids.buildGrid;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the scores {@link DefaultScoringModule} gives to moves on the classic board.
 *
 * <p>
 * TDD context: the first two tests were written before the fixes for the audit of September 2026.
 * Row 3 of the classic board has a triple-word square at column 0, a triple-letter square at column
 * 3 and a double-word square at column 7. A word covering both word squares scores six times its
 * letters, and a blank placed during a move scores nothing.
 *
 * @author Kheagen Haskins
 */
public class DefaultScoringModuleTest {

    private final DefaultScoringModule scoring = new DefaultScoringModule();

    @Test
    void testCalculateScore_withWordOnDoubleAndTripleWordSquares_MultipliesBonuses() {
        // Eight A tiles across row 3, columns 0 to 7. The A at column 4 is already on the board.
        // Letters: 7 plain A (7) + 1 A on the triple letter (3) = 10. Word bonus: 3 x 2 = 6.
        // Seven new tiles also earn the 35 point bingo: 10 x 6 + 35 = 95.
        var grid = buildGrid();
        grid[3][4] = 'A';
        var board = new GameBoard(grid);
        board.placeWord("AAAAAAAA", 3, 0, true);

        var score = scoring.calculateScore(board);

        assertEquals(95, score);
    }

    @Test
    void testCalculateScore_withNewBlankTile_ScoresBlankAsZero() {
        // A blank standing for Z at (7, 6), next to the A already at (7, 7). Neither square has a bonus.
        var grid = buildGrid();
        grid[7][7] = 'A';
        var board = new GameBoard(grid);
        board.placeWord("zA", 7, 6, true);

        var score = scoring.calculateScore(board);

        assertEquals(1, score);
    }

    @ParameterizedTest
    @CsvSource({"A, 1", "a, 1", "Z, 10", "z, 10", "?, 0", "' ', 0"})
    void testGetPointsOf_withCharacter_ReturnsExpectedValue(char letter, int expected) {
        var points = scoring.getPointsOf(letter);

        assertEquals(expected, points);
    }

}
