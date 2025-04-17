package com.slinky.wordcheat.util;

/**
 * Utility class for validating two-dimensional arrays (matrices).
 * 
 * <p>
 * Provides methods to ensure that matrices are not null, have positive
 * dimensions, and optionally match expected row and column counts.
 * </p>
 */
public final class ValidationUtils {
    // Prevent instantiation
    private ValidationUtils() {}

    private static final String MATRIX_NULL
            = "Matrix must not be null and must have positive dimensions.";
    private static final String ROW_MISMATCH
            = "Row mismatch (%d), expected %d";
    private static final String COLUMN_MISMATCH
            = "Column mismatch (%d), expected %d";
    
    /**
     * Determines whether the given character is an English letter (A–Z or a–z).
     * <p>
     * The test is case‑insensitive: the character is converted to uppercase and
     * checked against the range 'A' to 'Z'.
     * </p>
     *
     * @param c the character to test
     * @return  {@code true} if {@code c} is between 'A' and 'Z'
     *          (case‑insensitive); {@code false} otherwise
     */
    public static boolean isLetter(char c) {
        c = Character.toUpperCase(c);
        return c >= 'A' && c <= 'Z';
    }
    
    /**
     * Validates that the given char matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(char[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given char matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(char[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given int matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(int[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given int matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(int[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given double matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(double[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given double matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(double[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given float matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(float[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given float matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(float[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given long matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(long[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given long matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(long[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given short matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(short[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given short matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(short[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given byte matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(byte[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given byte matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(byte[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given boolean matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given boolean matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(boolean[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

    /**
     * Validates that the given Object matrix is non-null and has positive
     * dimensions.
     *
     * @param matrix the matrix to validate
     * @throws IllegalArgumentException if matrix is null, has zero rows, or the
     *                                  first row has zero columns
     */
    public static void validate(Object[][] matrix) {
        if (matrix == null || matrix.length == 0
                || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(MATRIX_NULL);
        }
    }

    /**
     * Validates that the given Object matrix matches expected dimensions.
     *
     * @param matrix the matrix to validate
     * @param rows   expected number of rows
     * @param cols   expected number of columns per row
     * @throws IllegalArgumentException if validation fails or dimensions differ
     */
    public static void validate(Object[][] matrix, int rows, int cols) {
        validate(matrix);
        if (matrix.length != rows) {
            throw new IllegalArgumentException(
                    String.format(ROW_MISMATCH, matrix.length, rows)
            );
        }
        if (matrix[0].length != cols) {
            throw new IllegalArgumentException(
                    String.format(COLUMN_MISMATCH, matrix[0].length, cols)
            );
        }
    }

}
