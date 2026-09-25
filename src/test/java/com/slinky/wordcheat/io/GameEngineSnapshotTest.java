package com.slinky.wordcheat.io;

import static com.slinky.wordcheat.model.TestGrids.buildGrid;
import static com.slinky.wordcheat.model.TestGrids.write;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;
import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.LetterRack;
import com.slinky.wordcheat.model.MoveFinder;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests saving a {@link GameEngine} as JSON and reading it back.
 *
 * <p>
 * TDD context: written before the fixes for the audit of September 2026. A save made before both
 * blanks were on the board stored the unused blank slots as {@code [-1, -1]}, and reading it back
 * threw, so the app could not start. A blank on the board also came back as an ordinary letter.
 *
 * @author Claude Code
 */
class GameEngineSnapshotTest {

    // ========================================================================================== \\
    //                                           Static                                           \\
    // ========================================================================================== \\

    private static final Dictionary DICTIONARY = new OxfordDictionary();

    /** A save written by version 0.1.0 before any blank was played. */
    private static final String OLDER_SAVE = """
        {
          "boardMatrix" : [ "               ", "               ", "               ", "               ",
                            "               ", "               ", "               ", "      CAT      ",
                            "               ", "               ", "               ", "               ",
                            "               ", "               ", "               " ],
          "wildcardPositions" : [ [ -1, -1 ], [ -1, -1 ] ],
          "rackLetters" : "DOG",
          "tileCounts" : { "A" : 8 },
          "timestamp" : 1750000000000
        }
        """;

    // ========================================================================================== \\
    //                                         API Methods                                        \\
    // ========================================================================================== \\

    @Test
    void testParseGame_withSaveFromOlderVersion_NoThrow() {
        assertDoesNotThrow(() -> Persistence.parseGame(OLDER_SAVE));
    }

    @Test
    void testParseGame_withSaveFromOlderVersion_RestoresBoardAndRack() throws IOException {
        var expected = buildGrid();
        write(expected, 7, 6, "CAT", true);

        var engine = Persistence.parseGame(OLDER_SAVE);

        assertAll(
            () -> assertArrayEquals(expected, engine.getMatrix()),
            () -> assertArrayEquals("DGO".toCharArray(), sorted(engine.getRackLetters())),
            () -> assertEquals(0, engine.getWildcardCount())
        );
    }

    @Test
    void testParseGame_withMalformedJson_ThrowsIOException() {
        assertThrows(IOException.class, () -> Persistence.parseGame("{ \"boardMatrix\" : 12 }"));
    }

    @Test
    void testFormatGame_withBlankOnBoard_RestoresBlank() throws IOException {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        board.setWildCardPosition(7, 7);
        var finder = new MoveFinder(board, DICTIONARY, new DefaultScoringModule());
        var engine = new GameEngine(new DefaultTileSet(), finder, new LetterRack("DOG".toCharArray()));

        var restored = Persistence.parseGame(Persistence.formatGame(engine));

        assertAll(
            () -> assertTrue(restored.isWildCard(7, 7)),
            () -> assertEquals(1, restored.getRemainingWildcardCount()),
            () -> assertEquals(9, restored.getRemainingTileCount('A'))
        );
    }

    // ========================================================================================== \\
    //                                       Helper Methods                                       \\
    // ========================================================================================== \\

    private static char[] sorted(char[] letters) {
        var copy = letters.clone();
        Arrays.sort(copy);
        return copy;
    }

}
