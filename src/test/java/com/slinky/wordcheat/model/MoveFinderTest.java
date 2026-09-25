package com.slinky.wordcheat.model;

import static com.slinky.wordcheat.model.TestGrids.buildGrid;
import static com.slinky.wordcheat.model.TestGrids.write;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the moves {@link MoveFinder} finds for a board and a rack.
 *
 * <p>
 * TDD context: written before the fixes for the audit of September 2026. The findings pinned here:
 * moves that join two separate groups of letters went unfound, a board letter appearing twice in a
 * word was only tried at its first position, one placement could be listed twice, a single unknown
 * word anywhere on the board blocked every move, and a blank was scored at the full value of the
 * letter it stood for.
 *
 * @author Claude Code
 */
class MoveFinderTest {

    // ========================================================================================== \\
    //                                           Static                                           \\
    // ========================================================================================== \\

    private static final Dictionary DICTIONARY = new OxfordDictionary();

    // ========================================================================================== \\
    //                                         API Methods                                        \\
    // ========================================================================================== \\

    @Test
    void testGetMoves_withGapBetweenBoardLetters_FindsMoveFillingGap() {
        // CA at (7, 5) and (7, 6), S at (7, 8). A T at (7, 7) spells CATS.
        var grid = buildGrid();
        write(grid, 7, 5, "CA", true);
        grid[7][8] = 'S';

        var moves = buildFinder(grid).getMoves("T".toCharArray());

        assertTrue(contains(moves, "CATS", 7, 5, false), "Moves: " + moves);
    }

    @Test
    void testGetMoves_withBoardLetterRepeatedInWord_FindsPlacementOnSecondOccurrence() {
        // The O on the board at (7, 7) is the second O of OBOE, which then starts at column 5.
        var grid = buildGrid();
        grid[7][7] = 'O';

        var moves = buildFinder(grid).getMoves("OBE".toCharArray());

        assertTrue(contains(moves, "OBOE", 7, 5, false), "Moves: " + moves);
    }

    @Test
    void testGetMoves_withTileFormingWordsBothWays_ListsPlacementOnce() {
        // An X at (7, 8) spells AX across and OX down. Both are the same single-tile move.
        var grid = buildGrid();
        grid[7][7] = 'A';
        grid[6][8] = 'O';

        var moves = buildFinder(grid).getMoves("X".toCharArray());

        var count = moves.stream()
                         .filter(m -> isMove(m, "AX", 7, 7, false) || isMove(m, "OX", 6, 8, true))
                         .count();
        assertEquals(1, count, "Moves: " + moves);
    }

    @Test
    void testGetMoves_withUnknownWordElsewhereOnBoard_StillFindsMoves() {
        var grid = buildGrid();
        write(grid, 1, 0, "QXZ", true);
        write(grid, 7, 6, "CAT", true);

        var moves = buildFinder(grid).getMoves("S".toCharArray());

        assertTrue(contains(moves, "CATS", 7, 6, false), "Moves: " + moves);
    }

    @Test
    void testGetMoves_withOnlyBlankInRack_ScoresBlankAsZero() {
        // One tile next to a lone A forms a two-letter word. With the blank worth nothing, the A
        // (1 point) on the best square available (triple word) scores at most 3.
        var grid = buildGrid();
        grid[7][7] = 'A';

        var moves = buildFinder(grid).getMoves("?".toCharArray());

        assertAll(
            () -> assertFalse(moves.isEmpty()),
            () -> assertTrue(moves.stream().allMatch(m -> m.score() <= 3), "Moves: " + moves)
        );
    }

    @Test
    void testGetMoves_withEmptyBoard_ReturnsOnlyMovesCrossingCentre() {
        var moves = buildFinder(buildGrid()).getMoves("CAT".toCharArray());

        assertAll(
            () -> assertFalse(moves.isEmpty()),
            () -> assertTrue(moves.stream().allMatch(MoveFinderTest::isCrossingCentre), "Moves: " + moves)
        );
    }

    @Test
    void testGetMoves_withEmptyRack_ReturnsEmptyList() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);

        var moves = buildFinder(grid).getMoves(new char[0]);

        assertTrue(moves.isEmpty());
    }

    @Test
    void testGetMoves_withBoardAndRack_ReturnsMovesInDescendingScoreOrder() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);

        var moves = buildFinder(grid).getMoves("SOEDR".toCharArray());

        var ordered = true;
        for (int i = 1; i < moves.size(); i++) {
            ordered &= moves.get(i - 1).score() >= moves.get(i).score();
        }
        assertTrue(ordered);
    }

    // ========================================================================================== \\
    //                                       Helper Methods                                       \\
    // ========================================================================================== \\

    private static MoveFinder buildFinder(char[][] grid) {
        return new MoveFinder(new GameBoard(grid), DICTIONARY, new DefaultScoringModule());
    }

    private static boolean contains(List<Move> moves, String word, int row, int col, boolean vertical) {
        return moves.stream().anyMatch(m -> isMove(m, word, row, col, vertical));
    }

    private static boolean isMove(Move move, String word, int row, int col, boolean vertical) {
        return move.word().equalsIgnoreCase(word)
            && move.row() == row
            && move.col() == col
            && move.verticallyPlaced() == vertical;
    }

    private static boolean isCrossingCentre(Move move) {
        int length = move.word().length();
        if (move.verticallyPlaced()) {
            return move.col() == 7 && move.row() <= 7 && move.row() + length - 1 >= 7;
        }
        return move.row() == 7 && move.col() <= 7 && move.col() + length - 1 >= 7;
    }

}
