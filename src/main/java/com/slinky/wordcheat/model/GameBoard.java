package com.slinky.wordcheat.model;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.util.MatrixUtils;
import com.slinky.wordcheat.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a letter matrix designed using a Data-Oriented Design (DOD)
 * approach for fast state querying.
 * 
 * <p>
 * This class models a game board for where rapid access to individual cell
 * states is required. It maintains a character matrix along with several
 * parallel boolean arrays that track:
 * <ul>
 *   <li>Whether a cell contains a permanent letter (<code>hasLetterAt</code>).</li>
 *   <li>Whether a letter was newly placed in the current move
 *       (<code>newLetter</code>).</li>
 *   <li>The overall population status of each row and column
 *       (<code>rowPopulated</code> and <code>colPopulated</code>).</li>
 * </ul>
 * </p>
 * <p>
 * <b>Key Features:</b>
 * <ul>
 *   <li>Efficient state querying using primitive arrays rather than encapsulated
 *       cell objects.</li>
 *   <li>Supports individual letter placement via <code>placeLetterAt</code> with
 *       a limit on the number of new placements per move (controlled by
 *       <code>MAX_NEW_TILES</code>).</li>
 *   <li>Provides batch word placement through the <code>placeWord</code> method,
 *       which performs fail-fast boundary checks and validates pre-existing
 *       letters.</li>
 *   <li>Includes methods to extract horizontal and vertical words
 *       (<code>getWords</code>) for move evaluation and scoring.</li>
 *   <li>Offers methods to determine the bounds of new-letter placements (e.g.
 *       <code>getNewRowLowerBound</code>, <code>getNewRowUpperBound</code>,
 *       <code>getNewColLowerBound</code>, and <code>getNewColUpperBound</code>).</li>
 *   <li>Supports board state management with operations to reset new placements
 *       (<code>reset</code>) and to preserve the board (clearing new-letter flags)
 *       when the state is valid (<code>preserve</code>).</li>
 *   <li>Provides utility methods for neighbour queries
 *       (<code>hasVerticalNeighbours</code> and
 *       <code>hasHorizontalNeighbours</code>), as well as conversion to JSON
 *       (<code>toJson</code>) and a textual format (<code>toString</code>).</li>
 *   <li>Includes a method (<code>areAllWordsValid</code>) to verify that all
 *       horizontal and vertical words are valid according to a supplied
 *       dictionary.</li>
 * </ul>
 * </p>
 * <p>
 * This class implements {@link Cloneable} and supports deep cloning, ensuring
 * that all internal arrays are duplicated correctly to prevent unintended
 * side-effects when modifying cloned instances.
 * </p>
 * <p>
 * <b>Example Usage:</b>
 * <pre>
 *   char[][] initialGrid = {
 *       {'A', ' '},
 *       {' ', 'B'}
 *   };
 *   GameBoard board  = new GameBoard(initialGrid);
 *   board.placeLetterAt('C', 0, 1);
 *   String json      = board.toJson();
 *   boolean allValid = board.areAllWordsValid(dictionary);
 * </pre>
 * </p>
 * 
 * @author  Kheagen Haskins
 * @version 1.0
 */
public class GameBoard implements Cloneable {
   
    // ================================[ Static ]================================ \\
    /**
     * The maximum number of new tiles that can be placed in a single move.
     */
    public final static int MAX_NEW_TILES = 7;
    
    // ================================[ Fields ]================================ \\
    /**
     * An array indicating for each row whether it contains at least one letter.
     */
    private boolean[] rowPopulated;

    /**
     * An array indicating for each column whether it contains at least one
     * letter.
     */
    private boolean[] colPopulated;

    /**
     * A 2D boolean array marking the cells where letters have been newly
     * placed.
     * 
     * <p>
     * A value of {@code true} indicates that the letter in the corresponding
     * cell is new.
     * </p>
     */
    private boolean[][] newLetter;

    /**
     * A 2D boolean array indicating the presence of a letter in each cell.
     * 
     * <p>
     * A value of {@code true} means the corresponding cell contains a valid
     * letter.
     * </p>
     */
    private boolean[][] hasLetter;

    /**
     * The character matrix representing the game board.
     * 
     * <p>
     * Each cell contains either a letter or the designated blank tile (see
     * {@link DefaultTileSet#BLANK_TILE}).
     * </p>
     */
    private char[][] matrix;

    /**
     * The number of rows in the board.
     */
    private int rows, cols;

    /**
     * The current count of new tiles placed on the board.
     */
    private int newTileCount = 0;
    
    /**
     * The maximum allowed number of wildcards
     */
    private int wildCardLimit = 2;
    
    /**
     * A counter for the number of Wildcards
     */
    private int wildCardCount = 0;
    
    /**
     * The row and column index locations of wildcards
     */
    private int[][] wildCardPositions = new int[wildCardLimit][2];

    /**
     * The smallest row index that has received a new letter.
     * 
     * <p>
     * Initialized to {@code Integer.MAX_VALUE} and updated upon placement of
     * new letters.
     * </p>
     */
    private int firstNewLetterRow = Integer.MAX_VALUE;

    /**
     * The smallest column index that has received a new letter.
     * 
     * <p>
     * Initialized to {@code Integer.MAX_VALUE} and updated upon placement of
     * new letters.
     * </p>
     */
    private int firstNewLetterCol = Integer.MAX_VALUE;

    /**
     * The largest row index that has received a new letter.
     * 
     * <p>
     * Initialized to {@code -1} and updated upon placement of new letters.
     * </p>
     */
    private int lastNewLetterRow = -1;

    /**
     * The largest column index that has received a new letter.
     * 
     * <p>
     * Initialized to {@code -1} and updated upon placement of new letters.
     * </p>
     */
    private int lastNewLetterCol = -1;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code GameBoard} instance based on the provided 2D
     * character grid.
     * 
     * <p>
     * The provided {@code letterGrid} is first validated using
     * {@link MainUtil#validateMatrix(char[][])} to ensure it is rectangular. A
     * deep copy of the grid is made, and each cell is examined:
     * 
     * <ul>
     *   <li>If the character in a cell is determined to be a letter (via
     *       {@link MainUtil#isLetter(char)}), the corresponding cell in
     *       {@code hasLetterAt} is set to {@code true}, and the row and column are
     *       marked as populated.</li>
     *   <li>If the cell does not contain a valid letter, it is set to the
     *       designated blank tile ({@link DefaultTileSet#BLANK_TILE}).</li>
     * </ul>
     * </p>
     *
     * @param letterGrid the initial grid of characters to be used for
     *                   constructing the game board.
     * @throws IllegalArgumentException if the provided grid is not rectangular.
     */
    public GameBoard(char[][] letterGrid) {
        ValidationUtils.validate(letterGrid);
        this.rows = letterGrid.length;
        this.cols = letterGrid[0].length;

        matrix = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            if (letterGrid[r].length != cols) {
                throw new IllegalArgumentException("Non rectangular grid provided");
            }
            
            matrix[r] = Arrays.copyOf(letterGrid[r], cols);
        }

        newLetter    = new boolean[rows][cols];
        hasLetter    = new boolean[rows][cols];
        rowPopulated = new boolean[rows];
        colPopulated = new boolean[cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char letter = letterGrid[r][c];
                if (ValidationUtils.isLetter(letter)) {
                    hasLetter[r][c] = true;
                    rowPopulated[r] = true;
                    colPopulated[c] = true;
                } else {
                    matrix[r][c] = DefaultTileSet.BLANK_TILE;
                }
            }
        }
        
        // Intialise wildcards as unset
        for (int r = 0; r < wildCardPositions.length; r++) {
            for (int c = 0; c < wildCardPositions.length; c++) {
                wildCardPositions[r][c] = -1;
            }
        }
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the number of rows in the game board.
     *
     * @return the row count.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in the game board.
     *
     * @return the column count.
     */
    public int getCols() {
        return cols;
    }
 
    /**
     * Returns the current count of new tile placements on the board.
     * 
     * <p>
     * This value represents the number of cells that have been marked with new
     * letter placements in the current move. It is used to enforce the limit on
     * new placements defined by {@link #MAX_NEW_TILES}.
     * </p>
     *
     * @return the number of new tiles placed on the board.
     */
    public int getNewTileCount() {
        return newTileCount;
    }
    
    /**
     * The number of wildcard currently present on the board.
     * 
     * @return 
     */
    public int getWildcardCount() {
        return wildCardCount;
    }
    
    /**
     * Creates a deep copy of the letter matrix.
     * 
     * @return a deep copy of the letter matrix
     */
    public char[][] getMatrix() {
        return MatrixUtils.deepCopy(matrix);
    }
    
    /**
     * Retrieves the letter at the specified cell.
     * 
     * <p>
     * If the cell does not contain a letter, the method returns the designated
     * blank tile (see {@link DefaultTileSet#BLANK_TILE}).
     * </p>
     *
     * @param row the row index (zero-based).
     * @param col the column index (zero-based).
     * @return the letter at the specified cell, or {@link DefaultTileSet#BLANK_TILE}
     *         if no letter is present.
     * @throws IndexOutOfBoundsException if the provided indices are out of
     *                                   bounds.
     */
    public char getLetterAt(int row, int col) {
        validateBounds(row, col);
        return hasLetter[row][col] ? matrix[row][col] : DefaultTileSet.BLANK_TILE;
    }

    /**
     * Determines whether the specified cell contains a letter.
     *
     * @param row the row index (zero-based).
     * @param col the column index (zero-based).
     * @return {@code true} if the cell contains a letter; {@code false}
     *         otherwise.
     * @throws IndexOutOfBoundsException if the provided indices are out of
     *                                   bounds.
     */
    public boolean hasLetterAt(int row, int col) {
        validateBounds(row, col);
        return hasLetter[row][col];
    }

    /**
     * Checks whether the specified row has at least one letter.
     *
     * @param row the row index (zero-based).
     * @return {@code true} if the row is populated with at least one letter;
     *         {@code false} otherwise.
     * @throws IndexOutOfBoundsException if the row index is out of bounds.
     */
    public boolean rowHasLetters(int row) {
        validateBounds(row, 0);
        return rowPopulated[row];
    }

    /**
     * Checks whether the specified column has at least one letter.
     *
     * @param col the column index (zero-based).
     * @return {@code true} if the column is populated with at least one letter;
     *         {@code false} otherwise.
     * @throws IndexOutOfBoundsException if the column index is out of bounds.
     */
    public boolean colHasLetters(int col) {
        validateBounds(0, col);
        return colPopulated[col];
    }

    /**
     * Determines if the letter at the given cell was newly placed.
     *
     * @param row the row index (zero-based).
     * @param col the column index (zero-based).
     * @return {@code true} if the cell contains a new letter; {@code false}
     *         otherwise.
     * @throws IndexOutOfBoundsException if the provided indices are out of
     *                                   bounds.
     */
    public boolean isNewLetter(int row, int col) {
        validateBounds(row, col);
        return newLetter[row][col];
    }
    
    /**
     * Determines if the given row and column position contain a wildcard
     *
     * @param row
     * @param col
     * @return true if the tile at the given location is a wildcard
     */
    public boolean isWildCard(int row, int col) {
        for (int r = 0; r < wildCardPositions.length; r++) {
            var position = wildCardPositions[r];
            if (position[0] == row && position[1] == col) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns the lowest row index that contains a new letter.
     *
     * @return the minimum row index with a new letter, or {@code -1} if no new
     *         letters exist.
     */
    public int getNewRowLowerBound() {
        for (int r = 0; r < rows; r++) {
            if (!rowPopulated[r]) {
                continue;
            }
            
            for (int c = 0; c < cols; c++) {
                if (newLetter[r][c]) {
                    return r;
                }
            }
        }
        
        return -1;
    }

    /**
     * Returns the highest row index that contains a new letter.
     *
     * @return the maximum row index with a new letter, or {@code -1} if no new
     *         letters exist.
     */
    public int getNewRowUpperBound() {
        for (int r = rows - 1; r >= 0; r--) {
            if (!rowPopulated[r]) {
                continue;
            }
            for (int c = 0; c < cols; c++) {
                if (newLetter[r][c]) {
                    return r;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the lowest column index that contains a new letter.
     *
     * @return the minimum column index with a new letter, or {@code -1} if no
     *         new letters exist.
     */
    public int getNewColLowerBound() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (newLetter[r][c]) {
                    return c;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the highest column index that contains a new letter.
     *
     * @return the maximum column index with a new letter, or {@code -1} if no
     *         new letters exist.
     */
    public int getNewColUpperBound() {
        for (int c = cols - 1; c >= 0; c--) {
            for (int r = 0; r < rows; r++) {
                if (newLetter[r][c]) {
                    return c;
                }
            }
        }
        return -1;
    }


    // ===========================[ Mutator Methods ]============================ \\
    /**
     * Resets the game board with a new letter grid.
     * 
     * <p>
     * This method replaces the current board state with the supplied
     * {@code letterGrid} and reinitializes the internal state arrays
     * accordingly. The provided grid is first validated using
     * {@link MainUtil#validateMatrix(char[][], int, int)} to ensure that it has
     * the same dimensions as the existing board.
     * 
     * <p>
     * For each cell in the grid:
     * <ul>
     *   <li>If the character is a valid letter (as determined by
     *       {@link MainUtil#isLetter(char)}), the letter is placed in the
     *       {@code matrix} and the corresponding cell in {@code hasLetterAt} is set to
     *       {@code true}. The row and column are also marked as populated by setting
     *       the appropriate entries in {@code rowPopulated} and
     *       {@code colPopulated}.</li>
     *   <li>If the character is not valid, the cell is set to the blank tile
     *       defined by {@link DefaultTileSet#BLANK_TILE}.</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Note:</b> If any row in the provided grid does not match the current
     * column count, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param letterGrid the new 2D character array to be used as the board.
     * @throws IllegalArgumentException if the grid is non-rectangular or its
     *                                  dimensions do not match the current board.
     */
    public void setBoard(char[][] letterGrid) {
        ValidationUtils.validate(letterGrid, rows, cols);
        // Reset flags
        for (int r = 0; r < rows; r++) {
            rowPopulated[r] = false;
        }
        for (int c = 0; c < cols; c++) {
            colPopulated[c] = false;
        }
        for (int r = 0; r < rows; r++) {
            if (letterGrid[r].length != cols) {
                throw new IllegalArgumentException("Non rectangular grid provided");
            }
            for (int c = 0; c < cols; c++) {
                char letter = letterGrid[r][c];
                boolean valid = ValidationUtils.isLetter(letter);
                hasLetter[r][c] = valid;
                matrix[r][c] = valid ? letter : DefaultTileSet.BLANK_TILE;
                if (valid) {
                    rowPopulated[r] = true;
                    colPopulated[c] = true;
                }
            }
        }
    }
    
    /**
     * 
     * @param row
     * @param col 
     */
    public void setWildCardPosition(int row, int col) {
        if (wildCardCount >= wildCardLimit) {
            throw new WildcardLimitReachedException(
                    "Cannot place wildcard at %d, %d; GameBoard already has %d wildcards"
                    .formatted(row, col, wildCardCount)
            );
        }
        
        wildCardPositions[wildCardCount][0] = row;
        wildCardPositions[wildCardCount][1] = col;
    }

    /**
     * 
     * @param wildCardLimit 
     */
    public void setWildCardLimit(int wildCardLimit) {
        this.wildCardLimit = Math.max(0, wildCardLimit);
        
        wildCardPositions = new int[wildCardLimit][2];
        for (int[] wildCardPosition : wildCardPositions) {
            wildCardPosition[0] = -1;
            wildCardPosition[1] = -1;
        }
    }

    /**
     * Attempts to place a letter at the specified cell on the board.
     * 
     * <p>
     * This method first verifies that the given indices are within the bounds
     * of the board by calling {@link #validateBounds(int, int)}. It then
     * checks:
     * </p>
     * <ul>
     *   <li>If the cell already contains a letter that is not marked as new, the
     *       placement is rejected.</li>
     *   <li>If the maximum number of new tile placements (as defined by
     *       {@code MAX_NEW_TILES}) has been reached, the placement is rejected.</li>
     * </ul>
     * If placement is permitted, the letter is placed into the {@code matrix},
     * and the corresponding flags in {@code hasLetterAt} and {@code newLetter}
     * are set to {@code true}. The row and column are marked as populated in
     * {@code rowPopulated} and {@code colPopulated}, respectively.
     * <p>
     * Additionally, the bounds of new-letter placements are updated:
     * <ul>
     *   <li>{@code firstNewLetterRow} and {@code firstNewLetterCol} are updated
     *       to the smallest row and column indices that have received a new
     *       letter.</li>
     *   <li>{@code lastNewLetterRow} and {@code lastNewLetterCol} are updated to
     *       the largest row and column indices that have received a new letter.</li>
     * </ul>
     * Finally, the internal counter {@code newTileCount} is incremented.
     * </p>
     *
     * @param letter the letter to be placed.
     * @param row the zero-based row index where the letter should be placed.
     * @param col the zero-based column index where the letter should be placed.
     * @return {@code true} if the letter was successfully placed; {@code false}
     *         if placement is disallowed.
     * @throws IndexOutOfBoundsException if the specified row or column is
     *         outside the board.
     */
    public boolean placeLetterAt(char letter, int row, int col) {
        validateBounds(row, col);
        if (hasLetter[row][col] && !newLetter[row][col] || (newTileCount >= MAX_NEW_TILES)) {
            return false;
        }

        matrix[row][col]    = letter;
        hasLetter[row][col] = true;
        newLetter[row][col] = true;
        rowPopulated[row]   = true;
        colPopulated[col]   = true;

        firstNewLetterRow = Math.min(row, firstNewLetterRow);
        firstNewLetterCol = Math.min(col, firstNewLetterCol);
        lastNewLetterRow  = Math.max(row, lastNewLetterRow);
        lastNewLetterCol  = Math.max(col, lastNewLetterCol);

        newTileCount++;
        
        return true;
    }
    
    // =============================[ API Methods ]============================== \\
    /**
     * Finds the leftmost column index in the specified row that contains a
     * letter.
     *
     * <p>
     * Starting from the given column, the method searches to the left until no
     * letter is found.
     * </p>
     *
     * @param board the {@code GameBoard} to search.
     * @param row the row index where the search is performed.
     * @param col the starting column index for the search.
     * @return the column index of the leftmost letter (inclusive).
     */
    public int findLeftMostLetter(int row, int col) {
        int leftBound = col;
        while (leftBound > 0 && hasLetter[row][leftBound - 1]) {
            leftBound--;
        }

        return leftBound; // Inclusive index.
    }

    /**
     * Finds the rightmost column index in the specified row that contains a
     * letter.
     *
     * <p>
     * Starting from the given column, the method searches to the right until no
     * letter is found.
     * </p>
     *
     * @param board the {@code GameBoard} to search.
     * @param row the row index where the search is performed.
     * @param col the starting column index for the search.
     * @return the column index of the rightmost letter (inclusive).
     */
    public int findRightMostLetter(int row, int col) {
        int rightBound = col;
        while (rightBound < cols - 1 && hasLetter[row][rightBound + 1]) {
            rightBound++;
        }

        return rightBound; // Inclusive index.
    }
    
    /**
     * Determines if the cell at the specified location has vertical neighbours.
     * 
     * <p>
     * A vertical neighbour is defined as any cell immediately above or below
     * the target cell that contains a letter. This method first validates the
     * provided indices using {@link #validateBounds(int, int)} and then checks
     * the cell immediately above (if available) and the cell immediately below
     * (if available).
     * </p>
     *
     * @param row the zero-based row index of the target cell.
     * @param col the zero-based column index of the target cell.
     * @return {@code true} if at least one vertical neighbour contains a
     *         letter; {@code false} otherwise.
     * @throws IndexOutOfBoundsException if the specified row or column is out
     *                                   of bounds.
     */
    public boolean hasVerticalNeighbours(int row, int col) {
        validateBounds(row, col);
        boolean hasBelow = row < rows - 1 && hasLetter[row + 1][col] && !newLetter[row + 1][col];
        boolean hasAbove = row > 0 && hasLetter[row - 1][col] && !newLetter[row - 1][col];
        return hasAbove || hasBelow;
    }

    /**
     * Determines if the cell at the specified location has horizontal
     * neighbours.
     * 
     * <p>
     * A horizontal neighbour is defined as any cell immediately to the left or
     * right of the target cell that contains a letter. This method validates
     * the cell's indices and then checks the cell immediately to the left (if
     * available) and the cell immediately to the right (if available).
     * </p>
     *
     * @param row the zero-based row index of the target cell.
     * @param col the zero-based column index of the target cell.
     * @return {@code true} if at least one horizontal neighbour contains a
     *         letter; {@code false} otherwise.
     * @throws IndexOutOfBoundsException if the specified row or column is out
     *                                   of bounds.
     */
    public boolean hasHorizontalNeighbours(int row, int col) {
        validateBounds(row, col);
        return col > 0 && hasLetter[row][col - 1] && !newLetter[row][col - 1] 
                || col < cols - 1 && hasLetter[row][col + 1] && !newLetter[row][col + 1];
    }

    /**
     * Checks whether the current configuration of new letter placements on the
     * board is valid.
     *
     * <p>
     * A valid state is defined as satisfying two conditions:
     * <ol>
     *   <li>New letters are confined to at most one row or at most one column.
     *       This is determined by iterating over all board cells and tracking
     *       distinct rows and columns that contain new letters. If new letters span
     *       more than one row <em>and</em> more than one column, the state is
     *       invalid.</li>
     *   <li>If one or more new letters are present, at least one of those new
     *       letters must have at least one adjacent (vertical or horizontal)
     *       neighbour that contains a letter. Diagonal neighbours are not
     *       considered.</li>
     * </ol>
     * If no new letters are present, the state is considered valid.
     * </p>
     *
     * @return {@code true} if the new letter placements satisfy the above
     * conditions; {@code false} otherwise.
     */
    public boolean isValidState() {
        if (isEmpty()) {
            return true;
        }
        
        boolean[] rowTracker = new boolean[rows];
        boolean[] colTracker = new boolean[cols];
        int rowCount = 0;
        int colCount = 0;
        // Check that new letters are confined to at most one row or one column.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (newLetter[r][c]) {
                    if (!rowTracker[r]) {
                        rowCount++;
                        rowTracker[r] = true;
                    }
                    if (!colTracker[c]) {
                        colCount++;
                        colTracker[c] = true;
                    }
                    if (rowCount > 1 && colCount > 1) {
                        return false;
                    }
                }
            }
        }
        
        // If no new letters are present, the state is valid.
        if (rowCount == 0) {
            return true;
        }
        // Ensure that at least one new letter has a vertical or horizontal neighbour.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (newLetter[r][c]) {
                    if (hasNeighbour(r, c)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Resets the board by clearing all new letter placements and updating the
     * row and column population flags.
     * <p>
     * This method iterates over each row that is marked as populated and, for
     * each column that is also marked as populated, checks if a cell contains a
     * new letter. If so, the cell is cleared by:
     * <ul>
     *   <li>Setting the cell in the {@code matrix} to the blank tile
     *       ({@link DefaultTileSet#BLANK_TILE}).</li>
     *   <li>Marking the cell in {@code hasLetterAt} as {@code false}.</li>
     *   <li>Clearing the new letter flag in {@code newLetter}.</li>
     * </ul>
     * 
     * After processing a row, the method recalculates the row's population flag
     * ({@code rowPopulated}) based on whether any cell in that row still
     * contains a permanent letter.
     * 
     * <p>
     * Subsequently, it recalculates the column population flags
     * ({@code colPopulated}) by checking, for each column, if any cell in that
     * column contains a letter.
     * </p>
     * 
     * <p>
     * This method does not affect any permanent letters that were present
     * before new letters were added.
     * </p>
     */
    public void reset() {
        // Reset new letter placements for each row and update row flags
        for (int r = 0; r < rows; r++) {
            if (!rowPopulated[r]) continue;

            boolean stillPopulated = false;
            for (int c = 0; c < cols; c++) {
                if (!colPopulated[c]) continue;

                if (newLetter[r][c]) {
                    matrix[r][c]    = DefaultTileSet.BLANK_TILE;
                    hasLetter[r][c] = false;
                    newLetter[r][c] = false;
                }

                if (hasLetter[r][c]) {
                    stillPopulated = true;
                }
            }

            rowPopulated[r] = stillPopulated;
        }

        // Recalculate colPopulated flags after resetting new letters
        for (int c = 0; c < cols; c++) {
            boolean stillPopulated = false;
            for (int r = 0; r < rows; r++) {
                if (hasLetter[r][c]) {
                    stillPopulated = true;
                    break;
                }
            }
            
            colPopulated[c] = stillPopulated;
        }
        
        newTileCount = 0;
    }

    /**
     * Preserves the current board state by clearing all new letter flags,
     * provided that the board is in a valid state.
     * 
     * <p>
     * The board is considered valid if the new letter placements are confined
     * to at most one row and one column, as determined by
     * {@link #isValidState()}. If the board is in an invalid state, no changes
     * are made and the method returns {@code false}.
     * </p>
     * 
     * <p>
     * When the board is valid, this method clears all entries in the
     * {@code newLetter} array (i.e. marks all cells as not containing a new
     * letter) and resets the boundary tracking fields:
     * <ul>
     *   <li>{@code firstNewLetterRow} is reset to {@code Integer.MAX_VALUE}.</li>
     *   <li>{@code firstNewLetterCol} is reset to {@code Integer.MAX_VALUE}.</li>
     *   <li>{@code lastNewLetterRow} is reset to {@code -1}.</li>
     *   <li>{@code lastNewLetterCol} is reset to {@code -1}.</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the board was in a valid state and the new letter
     *         flags were successfully cleared; {@code false} if the board was in an
     *         invalid state and no changes were made.
     */
    public boolean preserve() {
//        if (!isEmpty() && !isValidState()) {
        if (!isValidState()) {
            return false;
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                newLetter[r][c] = false;
            }
        }

        firstNewLetterRow = Integer.MAX_VALUE;
        firstNewLetterCol = Integer.MAX_VALUE;
        lastNewLetterRow = -1;
        lastNewLetterCol = -1;

        return true;
    }

    /**
     * Attempts to place an entire word on the board starting at the specified
     * position and extending in the given direction.
     * 
     * <p>
     * This method performs a fail-fast check to ensure that the word will fit
     * within the board's boundaries. For each letter in the word, it checks
     * whether:
     * <ul>
     *   <li>The target cell is within bounds.</li>
     *   <li>If a letter already exists in that cell and is permanent (i.e. not a
     *       new letter), it must match the corresponding character in the word.</li>
     *   <li>If the cell is empty, it counts towards the number of new placements
     *       required.</li>
     * </ul>
     * 
     * After the pre-check, the method verifies that adding the required new
     * letters would not exceed the maximum allowed new placements (defined by
     * {@code MAX_NEW_TILES}). If all preconditions are met, each letter is
     * placed using {@link #placeLetterAt(char, int, int)}, updating the board
     * state accordingly.
     * </p>
     *
     * @param word the word to be placed on the board.
     * @param row  the starting row index (zero-based) for the first letter of
     *             the word.
     * @param col  the starting column index (zero-based) for the first letter of
     *             the word.
     * @param horizontal if {@code true}, the word is placed left-to-right; if
     *                   {@code false}, it is placed top-to-bottom.
     * @return {@code true} if the word was successfully placed; {@code false}
     *         if any precondition fails or if placement is disallowed.
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal) {
        return placeWord(word, row, col, horizontal, -1);
    }
    
    /**
     * Attempts to place an entire word on the board starting at the specified
     * position and extending in the given direction.
     * 
     * <p>
     * This method performs a fail-fast check to ensure that the word will fit
     * within the board's boundaries. For each letter in the word, it checks
     * whether:
     * <ul>
     *   <li>The target cell is within bounds.</li>
     *   <li>If a letter already exists in that cell and is permanent (i.e. not a
     *       new letter), it must match the corresponding character in the word.</li>
     *   <li>If the cell is empty, it counts towards the number of new placements
     *       required.</li>
     * </ul>
     * 
     * After the pre-check, the method verifies that adding the required new
     * letters would not exceed the maximum allowed new placements (defined by
     * {@code MAX_NEW_TILES}). If all preconditions are met, each letter is
     * placed using {@link #placeLetterAt(char, int, int)}, updating the board
     * state accordingly.
     * </p>
     *
     * @param word the word to be placed on the board.
     * @param row  the starting row index (zero-based) for the first letter of
     *             the word.
     * @param col  the starting column index (zero-based) for the first letter of
     *             the word.
     * @param horizontal    if {@code true}, the word is placed left-to-right; if
     *                      {@code false}, it is placed top-to-bottom.
     * @param wildCardIndex The index of the wildcard within the word. Use -1 if none.
     * 
     * @return {@code true} if the word was successfully placed; {@code false}
     *         if any precondition fails or if placement is disallowed.
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal, int wildCardIndex) {
        int len = word.length();
        // Check if the word goes out of bounds.
        if (horizontal) {
            if (col + len > cols) {
                return false;
            }
        } else {
            if (row + len > rows) {
                return false;
            }
        }

        // Pre-check each cell in the target area.
        // Also count how many new placements are required.
        int additionalNew = 0;
        for (int i = 0; i < len; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            // If cell already has a letter:
            if (hasLetterAt(r, c)) {
                // If the letter is permanent, it must match the corresponding character.
                if (!isNewLetter(r, c) && matrix[r][c] != word.charAt(i)) {
                    return false;
                }
            } else {
                // The cell is empty so we need to place a new letter.
                additionalNew++;
            }
        }
        // Ensure that adding the new placements does not exceed the maximum allowed.
        if (newTileCount + additionalNew > MAX_NEW_TILES) {
            return false;
        }

        boolean isEmpty = isEmpty();
        // All pre-checks passed; now place each letter.
        // If a cell already contains the correct permanent letter, we skip placement.
        for (int i = 0; i < len; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (hasLetterAt(r, c) && !isNewLetter(r, c)) {
                // The cell is already permanently set with the correct letter.
                if (matrix[r][c] == word.charAt(i)) {
                    continue;
                } else {
                    // This branch should not be reached because of the pre-check.
                    reset();
                    return false;
                }
            }
            
            if (i == wildCardIndex) {
                setWildCardPosition(row, col);
            }
            
            // Use placeLetterAt to handle the placement and state updates.
            boolean placed = placeLetterAt(word.charAt(i), r, c);
            if (!placed) {
                reset();
                return false;
            }
        }
        
        if (isEmpty) {
            return true;
        } else if (!isValidState()) {
            reset();
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Determines if the cell at the specified row and column has at least one
     * immediate neighbour containing a letter.
     * <p>
     * This method checks for adjacent cells in both horizontal and vertical
     * directions. It returns {@code true} if at least one of the following
     * conditions is met:
     * 
     * <ul>
     *   <li>The cell to the left or right contains a letter (as determined by
     *       {@link #hasHorizontalNeighbours(int, int)}).</li>
     *   <li>The cell above or below contains a letter (as determined by
     *       {@link #hasVerticalNeighbours(int, int)}).</li>
     * </ul>
     * 
     * Diagonal neighbours are not considered.
     * </p>
     *
     * @param row the zero-based row index of the target cell.
     * @param col the zero-based column index of the target cell.
     * @return {@code true} if the cell has at least one horizontal or vertical
     *         neighbour with a letter; {@code false} otherwise.
     * @throws IndexOutOfBoundsException if the provided row or column indices
     *                                   are out of bounds.
     */
    public boolean hasNeighbour(int row, int col) {
        return hasHorizontalNeighbours(row, col) || hasVerticalNeighbours(row, col);
    }

    /**
     * Determines if the board has had any letters placed on it yet, whether new
     * or not.
     * 
     * @return {@code true} if the board is empty, {@code false} if not.
     */
    public boolean isEmpty() {
        for (int r = 0; r < rows; r++) {
            if (!rowPopulated[r]) continue;
            
            for (int c = 0; c < cols; c++) {
                if (hasLetter[r][c] && !newLetter[r][c]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Extracts all horizontal and vertical words from the board.
     * 
     * <p>
     * Words are defined as sequences of contiguous letters (cells with valid
     * letters) with a minimum length of 2. The method scans each row to extract
     * horizontal words, and each column to extract vertical words. When a blank
     * cell is encountered, if the accumulated word has a length of at least 2,
     * it is added to the list.
     * </p>
     *
     * @return a {@code List<String>} containing all extracted words from the
     *         board.
     */
    public List<String> getWords() {
        List<String> words = new ArrayList<>();
        // Extract horizontal words
        for (int r = 0; r < rows; r++) {
            if (!rowPopulated[r]) continue;            
            
            StringBuilder word = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                if (hasLetter[r][c]) {
                    word.append(matrix[r][c]);
                } else {
                    if (word.length() >= 2) {
                        words.add(word.toString());
                    }
                    word.setLength(0);
                }
            }
            if (word.length() >= 2) {
                words.add(word.toString());
            }
        }

        // Extract vertical words
        for (int c = 0; c < cols; c++) {
            if (!colPopulated[c]) continue;
            
            StringBuilder word = new StringBuilder();
            for (int r = 0; r < rows; r++) {
                if (hasLetter[r][c]) {
                    word.append(matrix[r][c]);
                } else {
                    if (word.length() >= 2) {
                        words.add(word.toString());
                    }
                    
                    word.setLength(0);
                }
            }
            
            if (word.length() >= 2) {
                words.add(word.toString());
            }
        }
        
        return words;
    }

    /**
     * Checks whether all horizontal and vertical words on the board are valid
     * according to the provided dictionary.
     * 
     * <p>
     * The method extracts all words from the board using {@link #getWords()}
     * and then verifies each word against the dictionary. The dictionary is
     * expected to have a {@code search(String word)} method which returns a
     * value &lt; 0 if the word is not found.
     * </p>
     *
     * @param dictionary a {@code OxfordDictionary} instance used to validate words;
     *                   must not be null.
     * @return {@code true} if all extracted words are valid; {@code false} if
     *         at least one word is invalid.
     * @throws IllegalArgumentException if the provided dictionary is
     *                                  {@code null}.
     */
    public boolean areAllWordsValid(Dictionary dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionary cannot be null");
        }

        List<String> words = getWords();
        for (String word : words) {
            if (dictionary.search(word) < 0) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Creates and returns a deep copy of the current {@code GameBoard}
     * instance.
     * 
     * <p>
     * The cloning process duplicates the internal character matrix and all
     * state tracking arrays to ensure that modifications to the clone do not
     * affect the original board.
     * </p>
     *
     * @return a deep clone of the current {@code GameBoard} instance.
     * @throws CloneNotSupportedException if the board cannot be cloned.
     */
    @Override
    public GameBoard clone() throws CloneNotSupportedException {
        GameBoard cloned    = (GameBoard) super.clone();
        cloned.matrix       = MatrixUtils.deepCopy(this.matrix);
        cloned.newLetter    = MatrixUtils.deepCopy(this.newLetter);
        cloned.hasLetter    = MatrixUtils.deepCopy(this.hasLetter);
        
        cloned.rowPopulated = Arrays.copyOf(this.rowPopulated, this.rowPopulated.length);
        cloned.colPopulated = Arrays.copyOf(this.colPopulated, this.colPopulated.length);

        // Copy other primitive fields directly
        cloned.newTileCount      = this.newTileCount;
        cloned.firstNewLetterRow = this.firstNewLetterRow;
        cloned.firstNewLetterCol = this.firstNewLetterCol;
        cloned.lastNewLetterRow  = this.lastNewLetterRow;
        cloned.lastNewLetterCol  = this.lastNewLetterCol;

        return cloned;
    }

    /**
     * Serializes the permanent state of the board into a JSON-formatted string.
     * 
     * <p>
     * This method constructs a JSON object containing the board's character
     * matrix. Cells that contain new letters are represented by the blank tile
     * ({@link DefaultTileSet#BLANK_TILE}), thereby preserving only the permanent
     * board state. The resulting JSON string is structured with a single key
     * "matrix" mapping to a two-dimensional array of string values.
     * </p>
     *
     * @return a JSON representation of the board's permanent state.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"matrix\": [");
        for (int r = 0; r < rows; r++) {
            sb.append("[");
            for (int c = 0; c < cols; c++) {
                char ch = (newLetter[r][c]) ? DefaultTileSet.BLANK_TILE : matrix[r][c];
                sb.append("\"").append(ch).append("\"");
                if (c < cols - 1) {
                    sb.append(",");
                }
            }
            
            sb.append("]");
            if (r < rows - 1) {
                sb.append(",");
            }
        }
        
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Returns a string representation of the board.
     * 
     * <p>
     * The representation includes two parts per row:
     * <ul>
     *   <li>A formatted line showing the characters in the board (each cell
     *       prefixed by a vertical bar).</li>
     *   <li>A subsequent line showing the new-letter flags as binary values (1
     *       for new letters, 0 otherwise), also separated by vertical bars.</li>
     * </ul>
     * This textual output is primarily intended for debugging purposes.
     * </p>
     *
     * @return a string representation of the board.
     */
    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            StringBuilder row1 = new StringBuilder();
            StringBuilder row2 = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                row1.append("|%c".formatted(matrix[r][c]));
                row2.append("|%s".formatted((newLetter[r][c] ? 1 : " ")));
            }
            row1.append("|     ");
            row2.append("|\n");

            outp.append(row1).append(row2);
        }

        return outp.toString();
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Validates that the provided row and column indices are within the valid
     * bounds of the board.
     * 
     * <p>
     * If either the row or column index is negative or exceeds the board's
     * dimensions, an {@link IndexOutOfBoundsException} is thrown with a
     * formatted error message.
     * </p>
     *
     * @param row the row index to validate.
     * @param col the column index to validate.
     * @throws IndexOutOfBoundsException if {@code row} or {@code col} is out of
     *                                   the valid range.
     */
    private void validateBounds(int row, int col) {
        if (row < 0 || col < 0 || row >= rows || col >= cols) {
            throw new IndexOutOfBoundsException(
                    "Row and/or column index [%d][%d] out of bounds for [%d][%d]".formatted(row, col, rows, cols)
            );
        }
    }

    // ============================[ Helper Classes ]============================ \\
    private static final class WildcardLimitReachedException extends IllegalStateException {

        public WildcardLimitReachedException() {
        }

        public WildcardLimitReachedException(String s) {
            super(s);
        }

        public WildcardLimitReachedException(String message, Throwable cause) {
            super(message, cause);
        }

        public WildcardLimitReachedException(Throwable cause) {
            super(cause);
        }
        
    }
    
}