package com.slinky.wordcheat.util;

import com.slinky.wordcheat.model.TileBonus;
import java.util.Arrays;

/**
 * Provides common operations on two‑dimensional arrays (matrices) and
 * human‑readable rendering of those matrices.
 */
public final class MatrixUtils {

    private MatrixUtils() {}
    
    /**
     * Shifts the elements of the given array to the left by the specified
     * offset, wrapping around, in place.
     *
     * @param arr    the array to rotate
     * @param offset number of positions to shift left; may be larger than
     *               arr.length or negative
     * @return the same array instance, now rotated
     */
    public static int[] shiftLeft(int[] arr, int offset) {
        if (arr == null || arr.length < 2) {
            return arr;  // nothing to do
        }
        if (offset == 0) {
            return arr;  // no shift needed
        }
        
        int n = arr.length;
        // normalize offset to [0, n)
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        
        // reverse first segment [0 .. offset - 1]
        reverse(arr, 0, offset - 1);
        // reverse second segment [offset .. n - 1]
        reverse(arr, offset, n - 1);
        // reverse entire array
        reverse(arr, 0, n - 1);
        
        return arr;
    }

    /**
     * Rotates a 2D array of characters by 90 degrees.
     *
     * <p>
     * If {@code clockwise} is {@code true}, the result is rotated 90° to the
     * right; otherwise 90° to the left.
     * 
     *
     * @param matrix    the input grid; must not be {@code null} or empty
     * @param clockwise direction flag: {@code true} = right, {@code false} =
     *                  left
     * @return a new grid containing the rotated data; never {@code null}
     */
    public static char[][] rotate(char[][] matrix, boolean clockwise) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Input matrix must not be null or empty");
        }
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        char[][] result = new char[cols][rows];

        if (clockwise) {
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    result[r][c] = matrix[rows - 1 - c][r];
                }
            }
        } else {
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    result[r][c] = matrix[c][cols - 1 - r];
                }
            }
        }
        return result;
    }

    /**
     * Rotates a 2D array of {@link TileBonus} by 90 degrees.
     *
     * <p>
     * Behaviour is analogous to {@link #rotate(char[][], boolean)}.
     * 
     *
     * @param matrix    the input grid; must not be {@code null} or empty
     * @param clockwise direction flag: {@code true} = right, {@code false} =
     *                  left
     * @return a new grid containing the rotated bonuses; never {@code null}
     */
    public static TileBonus[][] rotate(TileBonus[][] matrix, boolean clockwise) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Input matrix must not be null or empty");
        }
        int rows = matrix.length;
        int cols = matrix[0].length;
        TileBonus[][] result = new TileBonus[cols][rows];

        if (clockwise) {
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    result[r][c] = matrix[rows - 1 - c][r];
                }
            }
        } else {
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    result[r][c] = matrix[c][cols - 1 - r];
                }
            }
        }
        return result;
    }

    /**
     * Produces a deep copy of a 2D char array.
     *
     * @param matrix the original grid, may not be {@code null}
     * @return a new grid with identical content; never {@code null}
     */
    public static char[][] deepCopy(char[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Input matrix must not be null");
        }
        char[][] copy = new char[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    /**
     * Produces a deep copy of a 2D boolean array.
     *
     * @param matrix the original grid, may not be {@code null}
     * @return a new grid with identical content; never {@code null}
     */
    public static boolean[][] deepCopy(boolean[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Input matrix must not be null");
        }
        
        boolean[][] copy = new boolean[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        
        return copy;
    }

    /**
     * Renders a 2D char array as a human‑readable string, with row and column
     * headers.
     *
     * <p>
     * Non‑letter cells are shown as a space. Each row ends with a newline.
     * 
     *
     * @param matrix the grid to render; must not be {@code null}
     * @return a multi‑line string representing the grid; never {@code null}
     */
    public static String toString(char[][] matrix) {
        StringBuilder sb = new StringBuilder();
        // column headers
        sb.append(" |");
        for (int c = 0; c < matrix[0].length; c++) {
            sb.append(c).append('|');
        }
        
        sb.append('\n');
        // rows
        for (int r = 0; r < matrix.length; r++) {
            sb.append(r).append('|');
            for (char ch : matrix[r]) {
                sb.append(Character.isLetter(ch) ? ch : ' ').append('|');
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }

    /**
     * Renders a 2D boolean array as a human‑readable string.
     *
     * <p>
     * True = 1, False = 0. Each row ends with a newline.
     * 
     *
     * @param matrix the grid to render; must not be {@code null}
     * @return a multi‑line string representing the grid; never {@code null}
     */
    public static String toString(boolean[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean b : row) {
                sb.append(b ? '1' : '0').append('|');
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }

    /**
     * Reverses a portion of the given array in place, from the specified start
     * index to the end index.
     * <p>
     * This method swaps elements from the start and end of the specified range,
     * moving inwards until the indices meet or cross.
     *
     * @param arr the array whose elements are to be reversed
     * @param start the starting index of the section to reverse (inclusive)
     * @param end the ending index of the section to reverse (inclusive)
     */
    public static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int tmp = arr[start];
            arr[start++] = arr[end];
            arr[end--] = tmp;
        }
    }
    
    /**
     * We flatten a two-dimensional character matrix into a one-dimensional
     * array in row-major order. Each row is appended in sequence.
     *
     * @param matrix the two-dimensional array of characters to flatten
     * @return a one-dimensional array containing all characters from {@code matrix},
     *         in row-major order
     * @throws IllegalArgumentException if {@code matrix} is {@code null},
     *                                  or if any row within {@code matrix} is {@code null}
     */
    public static char[] flatten(char[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Input matrix cannot be null");
        }
        // Compute total length
        int total = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Row " + i + " cannot be null");
            }
            total += matrix[i].length;
        }

        // Copy rows in sequence
        char[] result = new char[total];
        int pos = 0;
        for (char[] row : matrix) {
            System.arraycopy(row, 0, result, pos, row.length);
            pos += row.length;
        }
        return result;
    }

    /**
     * We flatten a two-dimensional integer matrix into a one-dimensional array
     * in row-major order. Each row is appended in sequence.
     *
     * @param matrix the two-dimensional array of integers to flatten
     * @return a one-dimensional array containing all integers from {@code matrix},
     *         in row-major order
     * @throws IllegalArgumentException if {@code matrix} is {@code null},
     *         or if any row within {@code matrix} is {@code null}
     */
    public static int[] flatten(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Input matrix cannot be null");
        }
        // Compute total length
        int total = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Row " + i + " cannot be null");
            }
            total += matrix[i].length;
        }

        // Copy rows in sequence
        int[] result = new int[total];
        int pos = 0;
        for (int[] row : matrix) {
            System.arraycopy(row, 0, result, pos, row.length);
            pos += row.length;
        }
        return result;
    }
}