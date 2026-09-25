package com.slinky.wordcheat.model;

import com.slinky.wordcheat.util.MatrixUtils;
import com.slinky.wordcheat.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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
 * 
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
 *   <li>Includes methods to extract horizontal and vertical words: every word
 *       on the board (<code>getWords</code>), or only the words the new
 *       letters form (<code>getNewWords</code>).</li>
 *   <li>Records which letters are blank tiles (<code>isWildCard</code>). A
 *       lowercase letter passed to <code>placeLetterAt</code> or
 *       <code>placeWord</code> places a blank.</li>
 *   <li>Offers methods to determine the bounds of new-letter placements (e.g.
 *       <code>getNewRowLowerBound</code>, <code>getNewRowUpperBound</code>,
 *       <code>getNewColLowerBound</code>, and <code>getNewColUpperBound</code>).</li>
 *   <li>Supports board state management with operations to reset new placements
 *       (<code>reset</code>) and to preserve the board (clearing new-letter flags)
 *       when the state is valid (<code>preserve</code>).</li>
 *   <li>Provides utility methods for neighbour queries
 *       (<code>hasVerticalNeighbours</code> and
 *       <code>hasHorizontalNeighbours</code>), as well as a textual format
 *       (<code>toString</code>).</li>
 * </ul>
 * 
 * <p>
 * This class implements {@link Cloneable} and supports deep cloning, ensuring
 * that all internal arrays are duplicated correctly to prevent unintended
 * side-effects when modifying cloned instances.
 * 
 * <p>
 * <b>Example Usage:</b>
 * <pre>
 *   char[][] initialGrid = {
 *       {'A', ' '},
 *       {' ', 'B'}
 *   };
 *   GameBoard board = new GameBoard(initialGrid);
 *   board.placeLetterAt('C', 0, 1);
 *   List&lt;String&gt; words = board.getNewWords(); // ["AC", "CB"]
 * </pre>
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

    /**
     * The number of blank tiles in a game, and so the most the board can show.
     */
    public final static int MAX_BLANKS = 2;

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
     * 
     */
    private boolean[][] newLetter;

    /**
     * A 2D boolean array indicating the presence of a letter in each cell.
     * 
     * <p>
     * A value of {@code true} means the corresponding cell contains a valid
     * letter.
     * 
     */
    private boolean[][] hasLetter;

    /**
     * The character matrix representing the game board.
     * 
     * <p>
     * Each cell contains either a letter or the designated blank tile (see
     * {@link DefaultTileSet#BLANK_TILE}).
     * 
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
     * A 2D boolean array marking the cells whose letter is a blank tile.
     *
     * <p>
     * A value of {@code true} means the letter in that cell scores nothing. A
     * blank placed during the current move is also flagged in
     * {@code newLetter}.
     */
    private boolean[][] blank;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code GameBoard} instance based on the provided 2D
     * character grid.
     *
     * <p>
     * The provided {@code letterGrid} is first validated via
     * {@link ValidationUtils#validate(char[][])} to ensure it is rectangular.
     * A deep copy of the grid is then made, and each cell is examined:
     * <ul>
     *   <li>If the character is a letter (via
     *       {@link ValidationUtils#isLetter(char)}), the cell is marked
     *       populated.</li>
     *   <li>Otherwise the cell is set to the blank tile
     *       ({@link DefaultTileSet#BLANK_TILE}).</li>
     * </ul>
     *
     * @param letterGrid the initial grid of characters
     * @throws IllegalArgumentException if the provided grid is not rectangular
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
        blank        = new boolean[rows][cols];
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
     * 
     *
     * @return the number of new tiles placed on the board.
     */
    public int getNewTileCount() {
        return newTileCount;
    }
    
    /**
     * Returns the number of blank tiles on the board, including any placed
     * during the current move.
     *
     * @return the count of blanks, from 0 to {@link #MAX_BLANKS}
     */
    public int getWildcardCount() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (blank[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the position of each blank tile on the board, in row order.
     *
     * <p>
     * For a board with blanks at (3, 11) and (9, 10), the method returns
     * {@code {{3, 11}, {9, 10}}}. A board without blanks returns an empty
     * array.
     *
     * @return a new array of {@code [row, column]} pairs, one per blank
     */
    public int[][] getWildCardPositions() {
        var positions = new int[getWildcardCount()][];
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (blank[r][c]) {
                    positions[index++] = new int[] {r, c};
                }
            }
        }
        return positions;
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
     * Checks whether the letter at the specified cell is a blank tile.
     *
     * @param row the zero-based row index of the cell to check
     * @param col the zero-based column index of the cell to check
     * @return {@code true} if a blank occupies the specified cell;
     *         {@code false} otherwise
     * @throws IndexOutOfBoundsException if the provided indices are out of
     *                                   bounds.
     */
    public boolean isWildCard(int row, int col) {
        validateBounds(row, col);
        return blank[row][col];
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
     * The provided {@code letterGrid} is first validated via
     * {@link ValidationUtils#validate(char[][], int, int)} to ensure it matches
     * the current dimensions. Then for each cell:
     * <ul>
     *   <li>If valid (via {@link ValidationUtils#isLetter(char)}), the letter
     *       is placed;</li>
     *   <li>Otherwise the cell is set to {@link DefaultTileSet#BLANK_TILE}.</li>
     * </ul>
     *
     * <p>
     * Every letter in the new grid counts as already played, so the board
     * afterwards holds no new letters and no blanks. A caller marks the blanks
     * with {@link #setWildCards(int[][])}.
     *
     * @param letterGrid the new 2D character array to use
     * @throws IllegalArgumentException if the grid is non-rectangular or wrong
     *                                  size
     */
    public void setBoard(char[][] letterGrid) {
        ValidationUtils.validate(letterGrid, rows, cols);
        for (int r = 0; r < rows; r++) {
            if (letterGrid[r].length != cols) {
                throw new IllegalArgumentException("Non rectangular grid provided");
            }
        }

        // Reset flags
        for (int r = 0; r < rows; r++) {
            rowPopulated[r] = false;
        }
        for (int c = 0; c < cols; c++) {
            colPopulated[c] = false;
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char letter = Character.toUpperCase(letterGrid[r][c]);
                boolean valid = ValidationUtils.isLetter(letter);
                hasLetter[r][c] = valid;
                newLetter[r][c] = false;
                blank[r][c]     = false;
                matrix[r][c] = valid ? letter : DefaultTileSet.BLANK_TILE;
                if (valid) {
                    rowPopulated[r] = true;
                    colPopulated[c] = true;
                }
            }
        }

        newTileCount = 0;
    }

    /**
     * Marks the letter at the given cell as a blank tile, so it scores
     * nothing.
     *
     * <p>
     * Marking a cell that is already a blank leaves the board unchanged.
     *
     * @param row the zero-based row index of the blank
     * @param col the zero-based column index of the blank
     * @throws IndexOutOfBoundsException if (row, col) lies outside the board
     * @throws IllegalArgumentException  if the cell holds no letter
     * @throws IllegalStateException     if the board already shows
     *                                   {@link #MAX_BLANKS} blanks
     */
    public void setWildCardPosition(int row, int col) {
        validateBounds(row, col);
        if (!hasLetter[row][col]) {
            throw new IllegalArgumentException(
                    "Cannot mark (%d, %d) as a blank because the cell has no letter".formatted(row, col)
            );
        }

        if (blank[row][col]) {
            return;
        }

        if (getWildcardCount() >= MAX_BLANKS) {
            throw new WildcardLimitReachedException(
                    "Cannot mark (%d, %d) as a blank because the board already has %d".formatted(row, col, MAX_BLANKS)
            );
        }

        blank[row][col] = true;
    }

    /**
     * Replaces every blank on the board with the given positions.
     *
     * <p>
     * The method checks every position before it changes anything, so a
     * rejected call leaves the board as it was.
     *
     * @param positions the {@code [row, column]} pair of each blank; may be
     *                  empty
     * @throws NullPointerException      if {@code positions} is {@code null}
     * @throws IndexOutOfBoundsException if a position lies outside the board
     * @throws IllegalArgumentException  if a position holds no letter
     * @throws IllegalStateException     if more than {@link #MAX_BLANKS}
     *                                   positions are given
     */
    public void setWildCards(int[][] positions) {
        Objects.requireNonNull(positions, "Blank positions cannot be null");
        if (positions.length > MAX_BLANKS) {
            throw new WildcardLimitReachedException(
                    "A board can show at most %d blanks, but %d were given".formatted(MAX_BLANKS, positions.length)
            );
        }

        for (int[] position : positions) {
            validateBounds(position[0], position[1]);
            if (!hasLetter[position[0]][position[1]]) {
                throw new IllegalArgumentException(
                        "Cannot mark (%d, %d) as a blank because the cell has no letter".formatted(position[0], position[1])
                );
            }
        }

        for (boolean[] row : blank) {
            Arrays.fill(row, false);
        }

        for (int[] position : positions) {
            blank[position[0]][position[1]] = true;
        }
    }

    /**
     * Attempts to place a letter at the specified cell on the board.
     * 
     * <p>
     * This method first verifies that the given indices are within the bounds
     * of the board by calling {@link #validateBounds(int, int)}. It then
     * checks:
     * 
     * <ul>
     *   <li>If the cell already contains a letter that is not marked as new, the
     *       placement is rejected.</li>
     *   <li>If the maximum number of new tile placements (as defined by
     *       {@code MAX_NEW_TILES}) has been reached, the placement is rejected.</li>
     * </ul>
     * If placement is permitted, the letter is placed into the {@code matrix},
     * and the corresponding flags in {@code hasLetterAt} and {@code newLetter}
     * are set to {@code true}. The row and column are marked as populated in
     * {@code rowPopulated} and {@code colPopulated}, respectively. Finally, the
     * internal counter {@code newTileCount} is incremented.
     *
     * <p>
     * A lowercase letter stands for a blank tile. The board stores it in
     * uppercase and marks the cell as a blank, so {@code placeLetterAt('z', 7, 6)}
     * places a Z that scores nothing.
     *
     * @param letter the letter to be placed; lowercase for a blank.
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

        matrix[row][col]    = Character.toUpperCase(letter);
        blank[row][col]     = Character.isLowerCase(letter);
        hasLetter[row][col] = true;
        newLetter[row][col] = true;
        rowPopulated[row]   = true;
        colPopulated[col]   = true;

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
     * 
     *
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
     * Starting at {@code (row, col)}, scans right until no letter is found.
     *
     * @param row the zero-based row index
     * @param col the starting column index
     * @return the index of the rightmost contiguous letter (inclusive)
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
     * A valid state is defined as satisfying three conditions:
     * <ol>
     *   <li>New letters are confined to at most one row or at most one column.
     *       This is determined by iterating over all board cells and tracking
     *       distinct rows and columns that contain new letters. If new letters span
     *       more than one row <em>and</em> more than one column, the state is
     *       invalid.</li>
     *   <li>Every cell between the first and the last new letter holds a
     *       letter, either new or already played.</li>
     *   <li>If one or more new letters are present, at least one of those new
     *       letters must have at least one adjacent (vertical or horizontal)
     *       neighbour that contains a letter. Diagonal neighbours are not
     *       considered.</li>
     * </ol>
     * If no new letters are present, the state is considered valid.
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

        // Every cell between the first and last new letter must hold a letter.
        if (!isNewLineUnbroken()) {
            return false;
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
     * 
     * <p>
     * This method does not affect any permanent letters that were present
     * before new letters were added.
     * 
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
                    blank[r][c]     = false;
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
     * 
     * <p>
     * When the board is valid, this method clears all entries in the
     * {@code newLetter} array (i.e. marks all cells as not containing a new
     * letter) and resets the count of new tiles. A blank placed during the move
     * stays a blank.
     *
     * @return {@code true} if the board was in a valid state and the new letter
     *         flags were successfully cleared; {@code false} if the board was in an
     *         invalid state and no changes were made.
     */
    public boolean preserve() {
        if (!isValidState()) {
            return false;
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                newLetter[r][c] = false;
            }
        }

        newTileCount = 0;

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
     * 
     * @param word the word to be placed on the board.
     * @param row  the starting row index (zero-based) for the first letter of
     *             the word.
     * @param col  the starting column index (zero-based) for the first letter of
     *             the word.
     * @param horizontal    if {@code true}, the word is placed left-to-right; if
     *                      {@code false}, it is placed top-to-bottom.
     * @return {@code true} if the word was successfully placed; {@code false}
     *         if any precondition fails or if placement is disallowed.
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal) {
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
     * Extracts the words that the new letters form, leaving out every other
     * word on the board.
     *
     * <p>
     * A word counts when it is at least two letters long and contains at least
     * one new letter. For a board holding CAT across row 7, with an O above
     * the cell after the T and a new S placed in that cell, the method returns
     * {@code CATS} and {@code OS}. Each word appears once, however many new
     * letters it contains.
     *
     * @return a {@code List<String>} of the words formed by the new letters;
     *         empty when the board holds no new letters.
     */
    public List<String> getNewWords() {
        var words = new LinkedHashMap<String, String>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!newLetter[r][c]) continue;

                int left  = findLeftMostLetter(r, c);
                int right = findRightMostLetter(r, c);
                if (right > left) {
                    words.putIfAbsent("H" + r + "," + left, readLine(r, left, right, true));
                }

                int top    = findTopMostLetter(r, c);
                int bottom = findBottomMostLetter(r, c);
                if (bottom > top) {
                    words.putIfAbsent("V" + c + "," + top, readLine(c, top, bottom, false));
                }
            }
        }

        return new ArrayList<>(words.values());
    }

    /**
     * Creates and returns a deep copy of the current {@code GameBoard}
     * instance.
     *
     * <p>
     * The cloning process duplicates the internal character matrix and all
     * state tracking arrays to ensure that modifications to the clone do not
     * affect the original board.
     *
     * @return a deep clone of the current {@code GameBoard} instance.
     */
    @Override
    public GameBoard clone() {
        GameBoard cloned;
        try {
            cloned = (GameBoard) super.clone();
        } catch (CloneNotSupportedException ex) {
            // GameBoard implements Cloneable, so Object.clone() always succeeds.
            throw new AssertionError(ex);
        }

        cloned.matrix       = MatrixUtils.deepCopy(this.matrix);
        cloned.newLetter    = MatrixUtils.deepCopy(this.newLetter);
        cloned.hasLetter    = MatrixUtils.deepCopy(this.hasLetter);
        cloned.blank        = MatrixUtils.deepCopy(this.blank);

        cloned.rowPopulated = Arrays.copyOf(this.rowPopulated, this.rowPopulated.length);
        cloned.colPopulated = Arrays.copyOf(this.colPopulated, this.colPopulated.length);

        return cloned;
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

    /**
     * Checks that no empty cell lies between the first and the last new
     * letter. Assumes the new letters share one row or one column.
     *
     * @return {@code true} if the new letters and the letters between them
     *         form one unbroken line
     */
    private boolean isNewLineUnbroken() {
        int top    = getNewRowLowerBound();
        int bottom = getNewRowUpperBound();
        int left   = getNewColLowerBound();
        int right  = getNewColUpperBound();

        for (int r = top; r <= bottom; r++) {
            for (int c = left; c <= right; c++) {
                if (!hasLetter[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds the topmost row index of the unbroken run of letters in the given
     * column that passes through {@code (row, col)}.
     */
    private int findTopMostLetter(int row, int col) {
        int top = row;
        while (top > 0 && hasLetter[top - 1][col]) {
            top--;
        }
        return top;
    }

    /**
     * Finds the bottommost row index of the unbroken run of letters in the
     * given column that passes through {@code (row, col)}.
     */
    private int findBottomMostLetter(int row, int col) {
        int bottom = row;
        while (bottom < rows - 1 && hasLetter[bottom + 1][col]) {
            bottom++;
        }
        return bottom;
    }

    /**
     * Reads the letters of one row or column between two indices, inclusive.
     *
     * @param line       the row index when {@code horizontal}, otherwise the
     *                   column index
     * @param from       the first index along the line
     * @param to         the last index along the line
     * @param horizontal {@code true} to read along a row
     * @return the letters read, in order
     */
    private String readLine(int line, int from, int to, boolean horizontal) {
        var word = new StringBuilder(to - from + 1);
        for (int i = from; i <= to; i++) {
            word.append(horizontal ? matrix[line][i] : matrix[i][line]);
        }
        return word.toString();
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