package com.slinky.wordcheat.model;

import java.util.Arrays;

/**
 * Builds the 15 by 15 letter grids that the tests pass to {@link GameBoard}.
 *
 * <p>
 * A test starts from an empty grid and writes words onto it:
 *
 * <pre>
 *     var grid = TestGrids.buildGrid();
 *     TestGrids.write(grid, 7, 6, "CAT", true); // C at (7, 6), A at (7, 7), T at (7, 8)
 * </pre>
 *
 * @author Claude Code
 */
public final class TestGrids {

    // ========================================================================================== \\
    //                                           Static                                           \\
    // ========================================================================================== \\

    /** The row and column count of a standard board. */
    public static final int SIZE = 15;

    // ========================================================================================== \\
    //                                       Constructor(s)                                       \\
    // ========================================================================================== \\

    private TestGrids() {
    }

    // ========================================================================================== \\
    //                                         API Methods                                        \\
    // ========================================================================================== \\

    /**
     * Builds a 15 by 15 grid with a space in every cell.
     *
     * @return a new empty grid
     */
    public static char[][] buildGrid() {
        var grid = new char[SIZE][SIZE];
        for (var row : grid) {
            Arrays.fill(row, ' ');
        }
        return grid;
    }

    /**
     * Writes a word onto the grid, one letter per cell.
     *
     * @param grid       the grid to write on
     * @param row        the row index of the first letter
     * @param col        the column index of the first letter
     * @param word       the letters to write
     * @param horizontal {@code true} to write left to right; {@code false} to write top to bottom
     */
    public static void write(char[][] grid, int row, int col, String word, boolean horizontal) {
        for (int i = 0; i < word.length(); i++) {
            grid[horizontal ? row : row + i][horizontal ? col + i : col] = word.charAt(i);
        }
    }

}
