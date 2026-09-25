package com.slinky.wordcheat.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.slinky.wordcheat.model.TestGrids.buildGrid;
import static com.slinky.wordcheat.model.TestGrids.write;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests how {@link GameBoard} records blank tiles, copies itself, and reports the words a move
 * forms.
 *
 * <p>
 * TDD context: written before the fixes for the audit of September 2026. Each test pins one
 * finding: unset blank slots saved as {@code [-1, -1]}, the blank positions shared between a
 * board and its clone, a blank accepted on an empty cell, a lowercase letter standing for a blank
 * during a move search, a move with a gap passing validation, and move validation reading every
 * word on the board rather than the words the move forms.
 *
 * @author Claude Code
 */
class GameBoardBlankTileTest {

    @Test
    void testGetWildCardPositions_withNoBlanksPlaced_ReturnsEmptyArray() {
        var board = new GameBoard(buildGrid());

        var positions = board.getWildCardPositions();

        assertEquals(0, positions.length);
    }

    @Test
    void testGetWildCardPositions_withOneBlankPlaced_ReturnsOnlyThatPosition() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        board.setWildCardPosition(7, 7);

        var positions = board.getWildCardPositions();

        assertArrayEquals(new int[][] {{7, 7}}, positions);
    }

    @Test
    void testClone_withBlankAddedToClone_LeavesOriginalUnchanged() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        var clone = board.clone();

        clone.setWildCardPosition(7, 7);

        assertAll(
            () -> assertFalse(board.isWildCard(7, 7)),
            () -> assertTrue(clone.isWildCard(7, 7))
        );
    }

    @Test
    void testSetWildCardPosition_withEmptyCell_ThrowsIllegalArgumentException() {
        var board = new GameBoard(buildGrid());

        assertThrows(IllegalArgumentException.class, () -> board.setWildCardPosition(7, 7));
    }

    @Test
    void testSetWildCardPosition_withOutOfBoundsArgs_ThrowsIndexOutOfBoundsException() {
        var board = new GameBoard(buildGrid());

        assertThrows(IndexOutOfBoundsException.class, () -> board.setWildCardPosition(-1, -1));
    }

    @Test
    void testSetWildCardPosition_withThirdBlank_ThrowsIllegalStateException() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        board.setWildCardPosition(7, 6);
        board.setWildCardPosition(7, 7);

        assertThrows(IllegalStateException.class, () -> board.setWildCardPosition(7, 8));
    }

    @Test
    void testSetWildCards_withNewPositions_ReplacesExistingBlanks() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        board.setWildCardPosition(7, 6);

        board.setWildCards(new int[][] {{7, 8}});

        assertAll(
            () -> assertFalse(board.isWildCard(7, 6)),
            () -> assertTrue(board.isWildCard(7, 8)),
            () -> assertEquals(1, board.getWildcardCount())
        );
    }

    @Test
    void testSetBoard_withBlankPreviouslyPlaced_ClearsBlanks() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        var board = new GameBoard(grid);
        board.setWildCardPosition(7, 7);

        board.setBoard(grid);

        assertEquals(0, board.getWildcardCount());
    }

    @Test
    void testPlaceWord_withLowercaseLetter_PlacesUppercaseBlank() {
        var grid = buildGrid();
        grid[7][7] = 'A';
        var board = new GameBoard(grid);

        var placed = board.placeWord("zA", 7, 6, true);

        assertAll(
            () -> assertTrue(placed),
            () -> assertEquals('Z', board.getLetterAt(7, 6)),
            () -> assertTrue(board.isWildCard(7, 6)),
            () -> assertTrue(board.isNewLetter(7, 6))
        );
    }

    @Test
    void testReset_withNewBlankPlaced_ClearsBlank() {
        var grid = buildGrid();
        grid[7][7] = 'A';
        var board = new GameBoard(grid);
        board.placeWord("zA", 7, 6, true);

        board.reset();

        assertAll(
            () -> assertFalse(board.isWildCard(7, 6)),
            () -> assertEquals(0, board.getWildcardCount())
        );
    }

    @Test
    void testPreserve_withNewBlankPlaced_KeepsBlank() {
        var grid = buildGrid();
        grid[7][7] = 'A';
        var board = new GameBoard(grid);
        board.placeWord("zA", 7, 6, true);

        board.preserve();

        assertAll(
            () -> assertTrue(board.isWildCard(7, 6)),
            () -> assertEquals(1, board.getWildcardCount())
        );
    }

    @Test
    void testGetNewWords_withMoveFormingCrossWord_ReturnsMainAndCrossWords() {
        var grid = buildGrid();
        write(grid, 7, 6, "CAT", true);
        write(grid, 1, 0, "QXZ", true);
        grid[6][9] = 'O';
        var board = new GameBoard(grid);
        board.placeWord("S", 7, 9, true);

        var words = new HashSet<>(board.getNewWords());

        assertEquals(Set.of("CATS", "OS"), words);
    }

    @Test
    void testIsValidState_withGapBetweenNewLetters_ReturnsFalse() {
        var grid = buildGrid();
        grid[7][7] = 'A';
        var board = new GameBoard(grid);
        board.placeLetterAt('B', 7, 8);
        board.placeLetterAt('C', 7, 10);

        var valid = board.isValidState();

        assertFalse(valid);
    }

}
