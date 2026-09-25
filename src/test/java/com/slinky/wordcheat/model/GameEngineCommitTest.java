package com.slinky.wordcheat.model;

import static com.slinky.wordcheat.model.TestGrids.buildGrid;
import static com.slinky.wordcheat.model.TestGrids.write;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests how {@link GameEngine} takes in a board and rack copied from a real game, and what it
 * reports back to the screen.
 *
 * <p>
 * TDD context: written before the fixes for the audit of September 2026. The findings pinned here:
 * a commit that never rejected anything and left the engine half updated when it failed, a rack
 * that could not be empty, board letters taken from the pool twice by one constructor, rack letters
 * shown as still in the pool, and a best move that threw when there was none.
 *
 * @author Claude Code
 */
class GameEngineCommitTest {

    // ========================================================================================== \\
    //                                           Static                                           \\
    // ========================================================================================== \\

    private static final Dictionary DICTIONARY = new OxfordDictionary();

    // ========================================================================================== \\
    //                                         API Methods                                        \\
    // ========================================================================================== \\

    @Test
    void testConstructor_withPopulatedBoardAndNoRack_RemovesEachBoardLetterOnce() {
        var grid = buildGrid();
        grid[7][7] = 'A';
        var finder = new MoveFinder(new GameBoard(grid), DICTIONARY, new DefaultScoringModule());

        var engine = new GameEngine(new DefaultTileSet(), finder);

        assertEquals(8, engine.getRemainingTileCount('A'));
    }

    @Test
    void testCommit_withEmptyRack_NoThrow() {
        var engine = buildEngine(buildGrid(), "CAT");
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);

        assertDoesNotThrow(() -> engine.commit(grid, new int[0][], new char[0]));
    }

    @Test
    void testCommit_withMoreTilesThanTheGameHas_ThrowsIllegalStateException() {
        // The game has one Z and two blanks, so a fourth Z cannot exist.
        var engine = buildEngine(buildGrid(), "");
        var grid = buildGrid();
        write(grid, 7, 5, "ZZZZ", true);

        assertThrows(IllegalStateException.class, () -> engine.commit(grid, new int[0][], new char[0]));
    }

    @Test
    void testCommit_withRejectedBoard_LeavesEngineUnchanged() {
        var engine = buildEngine(buildGrid(), "CAT");
        var grid = buildGrid();
        write(grid, 7, 5, "ZZZZ", true);

        try {
            engine.commit(grid, new int[0][], "DOG".toCharArray());
        } catch (IllegalStateException expected) {
            // The rejection is covered by the test above.
        }

        assertAll(
            () -> assertArrayEquals(buildGrid(), engine.getMatrix()),
            () -> assertArrayEquals("ACT".toCharArray(), sorted(engine.getRackLetters())),
            () -> assertEquals(DefaultTileSet.TOTAL_TILE_COUNT, engine.getRemainingTileCount())
        );
    }

    @Test
    void testCommit_withNullRack_ThrowsNullPointerException() {
        var engine = buildEngine(buildGrid(), "");

        assertThrows(NullPointerException.class, () -> engine.commit(buildGrid(), new int[0][], null));
    }

    @Test
    void testCommit_withBlankOnBoard_RemovesBlankFromPool() {
        var engine = buildEngine(buildGrid(), "");
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);

        engine.commit(grid, new int[][] {{7, 7}}, new char[0]);

        assertAll(
            () -> assertTrue(engine.isWildCard(7, 7)),
            () -> assertEquals(1, engine.getRemainingWildcardCount()),
            () -> assertEquals(9, engine.getRemainingTileCount('A'))
        );
    }

    @Test
    void testGetUnseenTileCounts_withRackLetters_ExcludesRack() {
        var engine = buildEngine(buildGrid(), "AAB?");

        var counts = engine.getUnseenTileCounts();

        assertAll(
            () -> assertEquals(27, counts.length),
            () -> assertEquals(1, counts[0]),
            () -> assertEquals(7, counts[1]),
            () -> assertEquals(1, counts[2])
        );
    }

    @Test
    void testFindUnknownWords_withNewUnknownWord_ReturnsIt() {
        var start = buildGrid();
        write(start, 7, 6, "CAT", true);
        var engine = buildEngine(start, "");
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        write(grid, 1, 0, "QXZ", true);

        var unknown = engine.findUnknownWords(grid);

        assertEquals(List.of("QXZ"), unknown);
    }

    @Test
    void testFindUnknownWords_withUnknownWordAlreadyCommitted_ReturnsEmptyList() {
        var start = buildGrid();
        write(start, 1, 0, "QXZ", true);
        var engine = buildEngine(start, "");
        var grid = buildGrid();
        write(grid, 1, 0, "QXZ", true);
        write(grid, 7, 6, "CAT", true);

        var unknown = engine.findUnknownWords(grid);

        assertEquals(List.of(), unknown);
    }

    @Test
    void testGetBestMove_withEmptyBoard_ReturnsMoveCrossingCentre() {
        var engine = buildEngine(buildGrid(), "CAT");

        var move = engine.getBestMove();

        assertAll(
            () -> assertNotNull(move),
            () -> assertTrue(isCrossingCentre(move), "Move should cover (7, 7): " + move)
        );
    }

    @Test
    void testGetBestMove_withEmptyRack_ReturnsNull() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var engine = buildEngine(grid, "");

        var move = engine.getBestMove();

        assertNull(move);
    }

    @Test
    void testPrepareMoveSearch_withBoardAndRack_ReturnsSameMovesAndLeavesBoardUnchanged() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var engine = buildEngine(grid, "SOE");
        var search = engine.prepareMoveSearch();

        var moves = search.get();

        assertAll(
            () -> assertEquals(engine.getAllMoves(), moves),
            () -> assertArrayEquals(grid, engine.getMatrix())
        );
    }

    // ========================================================================================== \\
    //                                       Helper Methods                                       \\
    // ========================================================================================== \\

    private static GameEngine buildEngine(char[][] grid, String rack) {
        var finder = new MoveFinder(new GameBoard(grid), DICTIONARY, new DefaultScoringModule());
        return new GameEngine(new DefaultTileSet(), finder, new LetterRack(rack.toCharArray()));
    }

    private static boolean isCrossingCentre(Move move) {
        int last = move.col() + move.word().length() - 1;
        if (move.verticallyPlaced()) {
            last = move.row() + move.word().length() - 1;
            return move.col() == 7 && move.row() <= 7 && last >= 7;
        }
        return move.row() == 7 && move.col() <= 7 && last >= 7;
    }

    private static char[] sorted(char[] letters) {
        var copy = letters.clone();
        Arrays.sort(copy);
        return copy;
    }

}
