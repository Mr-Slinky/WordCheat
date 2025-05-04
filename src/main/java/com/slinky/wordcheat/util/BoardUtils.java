package com.slinky.wordcheat.util;

/**
 * Utility class for operations on a GameBoard and related bonus matrices.
 *
 * <p>
 * Includes methods for finding word bounds on a board and setting symmetrical
 * bonus tiles.
 */
public final class BoardUtils {

    // prevent instantiation
    private BoardUtils() { }

    /**
     * Sets a value at the specified (row, col) in a 2D matrix
     * and mirrors it across all four quadrants for symmetry.
     *
     * <p>
     * The element at (row, col) will also be placed at:
     * <ul>
     *   <li>(row,      cols - 1 - col)</li>
     *   <li>(rows - 1 - row, col)</li>
     *   <li>(rows - 1 - row, cols - 1 - col)</li>
     * </ul>
     * 
     * @param <T>    the element type of the matrix
     * @param matrix a non‑null, rectangular 2D array
     * @param row    zero‑based row index for the primary placement
     * @param col    zero‑based column index for the primary placement
     * @param value  the value to assign symmetrically
     * @throws IllegalArgumentException if matrix is null, non‑rectangular,
     *                                  or row/col are out of range
     */
    public static <T> void setSymmetry(T[][] matrix, int row, int col, T value) {
        if (matrix == null || matrix.length == 0
         || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException("Matrix must be non‑null and non‑empty");
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        // verify rectangular
        for (int r = 1; r < rows; r++) {
            if (matrix[r].length != cols) {
                throw new IllegalArgumentException("Non‑rectangular matrix");
            }
        }

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException(
                "Row or column index out of bounds: (" + row + ", " + col + ")");
        }

        // primary
        matrix[row][col] = value;
        // mirror horizontally
        matrix[row][cols - 1 - col] = value;
        // mirror vertically
        matrix[rows - 1 - row][col] = value;
        // mirror both
        matrix[rows - 1 - row][cols - 1 - col] = value;
    }
    
}