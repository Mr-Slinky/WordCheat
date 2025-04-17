package com.slinky.wordcheat.util;

import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.TileBonus;

/**
 * Utility class for operations on a GameBoard and related bonus matrices.
 *
 * <p>
 * Includes methods for finding word bounds on a board and setting symmetrical
 * bonus tiles.<
 * /p>
 */
public final class BoardUtils {

    // prevent instantiation
    private BoardUtils() { }

    /**
     * Finds the exclusive or inclusive column bound of a contiguous letter
     * sequence on a {@link GameBoard} row, starting from an anchor cell.
     *
     * <p>
     * If {@code toRight} is true, searches rightwards until an empty cell or
     * the board edge is reached, returning the first empty column index
     * (exclusive). Otherwise searches leftwards until the edge or empty cell,
     * returning the first occupied column index (inclusive) for the sequence
     * start.
     * </p>
     *
     * @param board    the game board to inspect; must not be null
     * @param row      the zero-based row index of the anchor cell
     * @param col      the zero-based column index of the anchor cell
     * @param toRight  {@code true} to search rightwards for the word end,
     *                 {@code false} to search leftwards for the word start
     * @return the column index where the contiguous letters end (if right)
     *         or begin (if left); will be within [0, board.getCols()]
     * @throws IllegalArgumentException if {@code row} or {@code col}
     *         are outside the board boundaries
     */
    public static int findColumnBound(GameBoard board, int row, int col, boolean toRight) {
        int maxCols = board.getCols();
        if (row < 0 || row >= board.getRows() || col < 0 || col >= maxCols) {
            throw new IllegalArgumentException(
                "Row or column index out of bounds: (" + row + ", " + col + ")");
        }

        int bound = col;
        if (toRight) {
            while (bound < maxCols && board.hasLetterAt(row, bound)) {
                bound++;
            }
        } else {
            while (bound > 0 && board.hasLetterAt(row, bound - 1)) {
                bound--;
            }
        }
        return bound;
    }

    /**
     * Sets a {@link TileBonus} at a specified position in a bonus matrix and
     * mirrors it across all four quadrants for symmetry.
     *
     * <p>
     * The bonus at (row, col) will be duplicated to (row, cols-1-col),
     * (rows-1-row, col), and (rows-1-row, cols-1-col).
     * </p>
     *
     * @param bonusMatrix the 2D bonus matrix; non-null, rectangular
     * @param row         the zero-based row index for the primary bonus
     * @param col         the zero-based column index for the primary bonus
     * @param type        the TileBonus to assign symmetrically
     * @throws IllegalArgumentException if indices are out of range
     */
    public static void setSymmetry(TileBonus[][] bonusMatrix,
                                   int row, int col,
                                   TileBonus type) {
        int rows = bonusMatrix.length;
        int cols = bonusMatrix[0].length;
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException(
                "Row or column index out of bounds: (" + row + ", " + col + ")");
        }

        bonusMatrix[row][col] = type;
        bonusMatrix[row][cols - 1 - col] = type;
        bonusMatrix[rows - 1 - row][col] = type;
        bonusMatrix[rows - 1 - row][cols - 1 - col] = type;
    }
    
}