package com.slinky.wordcheat.util;

import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.TileBonus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A utility class that provides various helper functions for matrix
 * manipulation and word extraction. This class includes methods to rotate a
 * matrix of characters, check if a character is an English letter, extract
 * words from a matrix, validate the integrity of a matrix and concatenate a
 * String with a character array.
 * <p>
 * Note: The word extraction assumes that words are sequences of at least two
 * consecutive letters.
 *
 * @author Kheagen Haskins
 */
public class MainUtil {
    
    // ================================[ Error Messages ]================================ \\
    private static final String ROW_MISMATCH    = "Row mismatch (%d), expected %d";
    private static final String COLUMN_MISMATCH = "Column mismatch (%d), expected %d";
    private static final String MATRIX_NULL     = "Matrix must not be null and must have positive dimensions.";
    
    // ================================[ Static Methods ]================================ \\
    /**
     * Determines whether the given character is an English letter.
     * <p>
     * The method converts the character to uppercase and checks whether its
     * value lies between 'A' and 'Z'. This approach is less comprehensive than
     * using {@code Character.isUpperCase()} or {@code Character.isLetter()},
     * but it offers a slight performance improvement by avoiding the
     * examination of a larger character set.
     * <p>
     * <strong>Note:</strong> The test is case-insensitive.
     *
     * @param c the character to be tested (this character is converted to
     * uppercase internally)
     * @return {@code true} if the character is an English letter between 'A'
     * and 'Z'; {@code false} otherwise.
     */
    public static boolean isLetter(char c) {
        c = Character.toUpperCase(c);
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Rotates the given two-dimensional character array (matrix) by 90 degrees
     * in the specified direction.
     * <p>
     * If {@code clockwise} is {@code true}, the matrix is rotated 90 degrees to
     * the right. Otherwise, the matrix is rotated 90 degrees to the left
     * (counter-clockwise).
     * <p>
     * For example, given the matrix:
     * <pre>
     * { {'a', 'b', 'c'},
     *   {'d', 'e', 'f'} }
     * </pre> If {@code clockwise} is {@code true}, the rotated matrix will be:
     * <pre>
     * { {'d', 'a'},
     *   {'e', 'b'},
     *   {'f', 'c'} }
     * </pre> and if {@code clockwise} is {@code false}, the rotated matrix will
     * be:
     * <pre>
     * { {'c', 'f'},
     *   {'b', 'e'},
     *   {'a', 'd'} }
     * </pre>
     *
     * @param matrix the original two-dimensional character array to be rotated;
     * must not be {@code null} or empty.
     * @param clockwise {@code true} to rotate the matrix 90 degrees to the
     * right (clockwise), {@code false} to rotate it 90 degrees to the left
     * (counter-clockwise)
     * @return a new two-dimensional character array representing the rotated
     * matrix.
     */
    public static char[][] rotate(char[][] matrix, boolean clockwise) {
        if (matrix == null || matrix.length == 0) {
            return matrix;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        char[][] rotated = new char[cols][rows];

        if (clockwise) {
            // Rotate 90° clockwise
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    rotated[r][c] = matrix[rows - 1 - c][r];
                }
            }
        } else {
            // Rotate 90° counter-clockwise (to the left)
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    rotated[r][c] = matrix[c][cols - 1 - r];
                }
            }
        }

        return rotated;
    }
    
    public static TileBonus[][] rotate(TileBonus[][] matrix, boolean clockwise) {
        if (matrix == null || matrix.length == 0) {
            return matrix;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        TileBonus[][] rotated = new TileBonus[cols][rows];

        if (clockwise) {
            // Rotate 90° clockwise
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    rotated[r][c] = matrix[rows - 1 - c][r];
                }
            }
        } else {
            // Rotate 90° counter-clockwise (to the left)
            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < rows; c++) {
                    rotated[r][c] = matrix[c][cols - 1 - r];
                }
            }
        }

        return rotated;
    }

    /**
     * Returns a string representation of the grid.
     * <p>
     * Each cell is formatted with a leading pipe character, and each row is
     * separated by a newline.
     * </p>
     *
     * @param letterMatrix the grid to stringify
     * @return a string representation of the grid.
     */
    public static String matrixToString(char[][] letterMatrix) {
        StringBuilder outp = new StringBuilder();
        final String cellTemplate = "|%c";
        final String newLine = "|\n";
        
        outp.append(cellTemplate.formatted(' '));
        for (char c = '0'; c < '0' + letterMatrix.length; c++) {
            outp.append(cellTemplate.formatted(c));
        }
        outp.append(newLine);
        
        char rowNum = '0';
        for (char[] row : letterMatrix) {
            outp.append(cellTemplate.formatted(rowNum++));
            for (char letter : row) {
                outp.append(cellTemplate.formatted(MainUtil.isLetter(letter) ? letter : ' '));
            }
            outp.append(newLine);
        }
        
        return outp.toString();
    }
    
    public static String matrixToString(boolean[][] letterMatrix) {
        StringBuilder outp = new StringBuilder();
        final String cellTemplate = "|%d";
        final String newLine = "|\n";
        for (boolean[] row : letterMatrix) {
            for (boolean bool : row) {
                outp.append(cellTemplate.formatted(bool ? 1 : 0));
            }
            outp.append(newLine);
        }
        
        return outp.toString();
    }
    
    public static void setSym(TileBonus[][] bonusMatrix, int row, int col, TileBonus type) {
        int rows = bonusMatrix.length;
        int cols = bonusMatrix[0].length;
        
        bonusMatrix[row][col] = type;
        bonusMatrix[row][cols - 1 - col] = type;
        bonusMatrix[rows - 1 - row][col] = type;
        bonusMatrix[rows - 1 - row][cols - 1 - col] = type;
    }
  
    /**
     * Given a GameBoard, along with a cell position, this function finds the
     * furthest left and right bounds
     *
     * @param matrix
     * @param anchorRow
     * @param anchorCol
     * @param right
     * @return
     */
    public static int findColumnBound(GameBoard matrix, int anchorRow, int anchorCol, boolean right) {
        int bound = anchorCol;
        if (right) {
            // Move rightwards until a blank cell or end of row is reached.
            while (matrix.hasLetterAt(anchorRow, bound)) {
                bound++;
                if (bound >= matrix.getCols()) {
                    return matrix.getCols();
                }
            } // exclusive bound
        } else {
            // Move leftwards until the start of the word is reached.
            while (matrix.hasLetterAt(anchorRow, bound - 1)) {
                bound--;
                if (bound == 0) {
                    return 0;
                }
            } // inclusive bound
        }

        return bound;
    }

    /**
     * 
     * @param chars
     * @param n
     * @param list 
     */
    public static void generateAllPermutations(char[] chars, int n, List<String> list) {
        if (chars.length == 0 || n < 1) {
            return;
        }

        // Base case: when n == 1, we've reached a complete permutation.
        if (n == 1) {
            list.add(String.valueOf(chars));
            return;
        }

        // Loop from 0 to n-2
        for (int i = 0; i < n - 1; i++) {
            // Recursively generate permutations for n-1 elements.
            generateAllPermutations(chars, n - 1, list);

            // Swap elements based on whether n is even or odd.
            if (isEven(n)) {
                // For even n, swap the element at index i with the last element.
                swap(chars, i, n - 1);
            } else {
                // For odd n, always swap the first element with the last element.
                swap(chars, 0, n - 1);
            }
        }
        // Final recursive call after the loop ensures the last permutation is produced.
        generateAllPermutations(chars, n - 1, list);
    }
    
    public static List<String> generateAllPermutations(char[] chars, boolean fixedSize) {
        List<String> perms = new ArrayList<>();
        generateAllPermutations(chars, chars.length, perms);
        return perms;
    }

    /**
     * Creates a deep copy of a two-dimensional char array.
     *
     * @param matrix the original char matrix to copy
     * @return a new char[][] that is a deep copy of the original matrix, or
     *         null if the input is null
     */
    public static char[][] deepCopyOf(char[][] matrix) {
        if (matrix == null) {
            return null;
        }
        
        char[][] copy = new char[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            // Create a copy of each row
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    /**
     * Creates a deep copy of a two-dimensional boolean array.
     *
     * @param matrix the original boolean matrix to copy
     * @return a new boolean[][] that is a deep copy of the original matrix, or
     *         null if the input is null
     */
    public static boolean[][] deepCopyOf(boolean[][] matrix) {
        if (matrix == null) {
            return null;
        }
        
        boolean[][] copy = new boolean[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            // Create a copy of each row
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }
    
    // |================================================================================| \\
    // |                                Validation Logic                                | \\
    // |================================================================================| \\
    /**
     * Validates that the provided two-dimensional {@code char} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code char} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows, 
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code char} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code char} array to validate
     * @param rows   the expected number of rows in the matrix
     * @param cols   the expected number of columns in the first row of the matrix
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  has no columns in the first row, or if the dimensions
     *                                  do not match the expected values
     */
    public static void validateMatrix(char[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code int} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code int} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows, 
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code int} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code int} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(int[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code double} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code double} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows, 
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(double[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code double} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code double} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(double[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code float} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code float} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(float[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code float} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code float} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(float[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code long} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code long} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(long[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code long} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code long} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(long[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code short} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code short} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(short[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code short} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code short} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(short[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code byte} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code byte} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(byte[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code byte} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code byte} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(byte[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    /**
     * Validates that the provided two-dimensional {@code boolean} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code boolean} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows,
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code boolean} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code boolean} array to validate
     * @param rows   the expected number of rows
     * @param cols   the expected number of columns in the first row
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(boolean[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    // ----- Object Matrices -----

    /**
     * Validates that the provided two-dimensional {@code Object} array is not
     * {@code null} and has positive dimensions.
     *
     * @param matrix the two-dimensional {@code Object} array to validate
     * @throws IllegalArgumentException if the matrix is {@code null}, has no rows, 
     *                                  or has no columns in the first row
     */
    public static void validateMatrix(Object[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the provided two-dimensional {@code Object} array is not
     * {@code null}, has positive dimensions, and exactly matches the expected
     * number of rows and columns.
     *
     * @param matrix the two-dimensional {@code Object} array to validate
     * @param rows   the expected number of rows in the matrix
     * @param cols   the expected number of columns in the first row of the matrix
     * @throws IllegalArgumentException if the matrix fails any of the validation checks
     */
    public static void validateMatrix(Object[][] matrix, int rows, int cols) {
        validateMatrix(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(ROW_MISMATCH.formatted(matrix.length, rows));
        } else if (matrix[0].length != cols) {
            throw new IllegalArgumentException(COLUMN_MISMATCH.formatted(matrix[0].length, cols));
        }
    }

    // ============================[ Private Helper Methods ]============================ \\
    private static void swap(char[] arr, int a, int b) {
        char temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    private static boolean isEven(int num) {
        return num % 2 == 0;
    }
}