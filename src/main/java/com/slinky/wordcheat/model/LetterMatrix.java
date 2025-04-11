package com.slinky.wordcheat.model;

import com.slinky.wordcheat.util.MainUtil;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a grid of letters.
 * 
 * <p>
 * This base class encapsulates a two-dimensional array of characters, providing
 * methods for accessing and modifying individual letters. It also implements
 * the {@link Iterable} interface to allow iteration over the grid in row-major
 * order (i.e. left-to-right, top-to-bottom). This class forms the foundation
 * for specialised grid implementations.
 * </p>
 *
 * @author Kheagen Haskins
 */
public class LetterMatrix implements Iterable<Character> {

    // ================================[ Fields ]================================ \\
    /**
     * The two-dimensional array holding the grid of letters.
     */
    protected char[][] matrix;
    
    protected final int rows;
    protected final int cols;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a {@code LetterGrid} with the provided two-dimensional
     * character array.
     *
     * @param letterGrid a two-dimensional array of characters representing the
     *                   grid.
     */
    public LetterMatrix(char[][] letterGrid) {
        MainUtil.validateMatrix(letterGrid);
        this.rows = letterGrid.length;
        this.cols = letterGrid[0].length;
        
        matrix = new char[letterGrid.length][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = Arrays.copyOf(letterGrid[i], letterGrid[i].length);
        }
    }

    /**
     * Constructs a {@code LetterGrid} with the specified number of rows and
     * columns. Each cell is initialised with the default character value
     * ('\0').
     *
     * @param rows the number of rows.
     * @param cols the number of columns.
     */
    public LetterMatrix(int rows, int cols) {
        this(new char[rows][cols]);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns a deep copy of the letter matrix.
     *
     * @return a two-dimensional array of characters representing the grid.
     */
    public char[][] cloneMatrix() {
        char[][] deepCopy = new char[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            deepCopy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        
        return deepCopy;
    }


    /**
     * Retrieves the letter at the specified row and column.
     *
     * @param row the row index.
     * @param col the column index.
     * @return the character at the specified location.
     * @throws IndexOutOfBoundsException if the indices are out of bounds.
     */
    public char getLetterAt(int row, int col) {
        validateBounds(row, col);
        return matrix[row][col];
    }  

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
    
    // ===========================[ Mutator Methods ]============================ \\
    /**
     * Sets the letter at the specified row and column to the given character.
     * The character is converted to uppercase before being set.
     *
     * @param c   the character to set.
     * @param row the row index.
     * @param col the column index.
     * @throws IllegalArgumentException  if the character is not a valid letter
     *                                   (A-Z).
     * @throws IndexOutOfBoundsException if the indices are out of bounds.
     */
    public void placeLetterAt(char c, int row, int col) {
        c = Character.toUpperCase(c);
        if (c != DefaultTileSet.BLANK_TILE && c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("Invalid letter: " + c);
        }
        
        validateBounds(row, col);
        matrix[row][col] = c;
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Returns a string representation of the grid.
     * <p>
     * Each cell is formatted with a leading pipe character, and each row is
     * separated by a newline.
     * </p>
     *
     * @return a string representation of the grid.
     */
    @Override
    public String toString() {
        return MainUtil.matrixToString(matrix);
    }

    /**
     * Returns an iterator over the characters in the grid in row-major order.
     *
     * @return an {@code Iterator} of {@code Character}.
     */
    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private final int total = matrix.length * matrix[0].length;
            private int index       = 0;

            /**
             * Checks if there are more characters to iterate over.
             *
             * @return {@code true} if there are more characters, {@code false}
             *         otherwise.
             */
            @Override
            public boolean hasNext() {
                return index < total;
            }

            /**
             * Returns the next character in the iteration.
             *
             * @return the next character.
             * @throws java.util.NoSuchElementException if the iteration has no
             *         more characters.
             */
            @Override
            public Character next() {
                int cols = matrix[0].length;
                int row = index / cols;
                int col = index % cols;
                index++;
                return matrix[row][col];
            }
        };
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Validates that the provided row and column indices are within the bounds
     * of the grid.
     *
     * @param row the row index to validate.
     * @param col the column index to validate.
     * @throws IndexOutOfBoundsException if the indices are out of bounds.
     */
    protected void validateBounds(int row, int col) {
        if (row < 0 || col < 0 || row >= rows || col >= cols) {
            throw new IndexOutOfBoundsException(
                    "Row and/or column index [%d][%d] out of bounds for [%d][%d]".
                            formatted(row, col, rows, cols)
            );
        }
    }
    
}