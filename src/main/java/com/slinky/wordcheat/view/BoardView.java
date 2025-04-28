package com.slinky.wordcheat.view;

import static com.slinky.wordcheat.view.ColorConstants.DEFAULT_TILE_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.DOUBLE_LETTER_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.DOUBLE_WORD_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.EMPTY_TILE_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.TRIPLE_LETTER_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.TRIPLE_WORD_COLOR;

import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * A fully programmatic grid view for displaying {@link TileNode} instances,
 * showing letter, score, and bonus for each cell in a tabular layout.
 * 
 * <p>
 * This view constructs the grid based on provided letter, score, and bonus
 * matrices, and allows resetting individual tiles to their default empty state.
 * </p>
 *
 * @author WordCheat Team
 */
public final class BoardView extends GridPane {

    // ================================[ Fields ]================================
    /** Number of rows in the board. */
    private final int rows;
    /** Number of columns in the board. */
    private final int cols;
    /** 2D array of {@link TileNode} references for quick access. */
    private final TileNode[][] tiles;

    // =============================[ Constructors ]=============================
    /**
     * Creates a new {@code BoardView} with the given matrices.
     * <p>
     * Validates input dimensions, then populates this view with tiles.
     * </p>
     *
     * @param letters  2D char array of letters ('A'–'Z') or other for empty
     * @param scores   2D int array of tile scores matching the dimensions
     * @param bonuses  2D String array of bonus text (e.g. "DL", "TW"), same dims
     * @throws NullPointerException      if any matrix is null
     * @throws IllegalArgumentException  if matrices differ in dimensions or are empty
     */
    public BoardView(char[][] letters, int[][] scores, String[][] bonuses) {
        validConstructorParams(letters, scores, bonuses);

        this.rows  = letters.length;
        this.cols  = letters[0].length;
        this.tiles = new TileNode[rows][cols];
        
        initialiseGrid(letters, scores, bonuses);
    }

    // ===========================[ Public API Methods ]=========================
    /**
     * Retrieves the {@link TileNode} at the specified position.
     *
     * @param row zero‑based row index
     * @param col zero‑based column index
     * @return the tile at (row, col)
     * @throws IndexOutOfBoundsException if indices are out of bounds
     */
    public TileNode getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException(
                String.format("Index [%d, %d] out of bounds for [%d, %d]", row, col, rows, cols)
            );
        }
        return tiles[row][col];
    }

    /**
     * Returns all {@link TileNode} instances in row-major order.
     *
     * @return flat array of all tiles
     */
    public TileNode[] getAllTiles() {
        TileNode[] flat = new TileNode[rows * cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                flat[idx++] = tiles[r][c];
            }
        }
        return flat;
    }

    /**
     * Returns the number of rows in this board view.
     *
     * @return row count
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in this board view.
     *
     * @return column count
     */
    public int getCols() {
        return cols;
    }

    /**
     * Resets the specified tile to its default empty state.
     * 
     * <p>
     * Ensures the tile is part of this board before clearing its content;
     * then sets letter to blank, score & count to -1, clears bonus,
     * resets wildcard flag, and restores empty background.
     * </p>
     *
     * @param tile the {@link TileNode} to reset
     * @throws NullPointerException      if {@code tile} is null
     * @throws IllegalArgumentException  if the tile is not part of this board
     */
    public void emptyTile(TileNode tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c] == tile) {
                    tile.setLetter(' ');
                    tile.setScore(-1);
                    tile.setCount(-1);
                    tile.setWildcard(false);
                    tile.setDraggable(false);
                    tile.setDropTarget(true);
                    assignBonusColor(tile, tile.getBonus());

                    tile.applyStyle();
                    tile.refresh();
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Tile not part of this BoardView");
    }

    // ============================[ Helper Methods ]============================
    /**
     * Validates that all provided matrices are non-null, non-empty, and
     * share the same dimensions.
     *
     * @param letters  letter matrix to validate
     * @param scores   score matrix to validate
     * @param bonuses  bonus matrix to validate
     * @throws NullPointerException     if any matrix is null
     * @throws IllegalArgumentException if dimensions mismatch or zero size
     */
    private void validConstructorParams(char[][] letters, int[][] scores, String[][] bonuses) {
        Objects.requireNonNull(letters, "Letters matrix cannot be null");
        Objects.requireNonNull(scores,  "Scores matrix cannot be null");
        Objects.requireNonNull(bonuses, "Bonuses matrix cannot be null");
        if (letters.length == 0 || letters[0].length == 0) {
            throw new IllegalArgumentException("Letters matrix cannot be empty");
        }
        int rc = letters.length;
        int cc = letters[0].length;
        if (scores.length != rc || bonuses.length != rc) {
            throw new IllegalArgumentException("All matrices must have the same row count");
        }
        for (int r = 0; r < rc; r++) {
            if (letters[r].length != cc || scores[r].length != cc || bonuses[r].length != cc) {
                throw new IllegalArgumentException(
                    String.format("All matrices must have %d columns; row %d lengths: letters=%d, scores=%d, bonuses=%d",
                                   cc, r, letters[r].length, scores[r].length, bonuses[r].length)
                );
            }
        }
    }

    /**
     * Initializes and lays out all {@link TileNode} instances according to the
     * provided data matrices.
     *
     * @param letters  2D char array of letters
     * @param scores   2D int array of scores
     * @param bonuses  2D String array of bonus texts
     */
    private void initialiseGrid(char[][] letters, int[][] scores, String[][] bonuses) {
        setHgap(4);
        setVgap(4);
        setAlignment(Pos.CENTER);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch       = letters[r][c];
                int score     = scores[r][c];
                String bonus  = bonuses[r][c];
                TileNode tile = TileFactory.createBoardTile(r, c, String.valueOf(ch));

                boolean hasLetter = (ch >= 'A' && ch <= 'Z');
                tile.setScore(score);
                tile.setBonus(bonus == null ? "" : bonus);

                if (!(hasLetter || bonus == null)) {
                    assignBonusColor(tile, bonus);
                } else if (hasLetter) {
                    tile.setBackgroundFill(DEFAULT_TILE_COLOR);
                }
                
                tile.setDropTarget(!hasLetter);
                tile.applyStyle();
                tile.refresh();

                tiles[r][c] = tile;
                add(tile, c, r);
            }
        }
    }
    
    private void assignBonusColor(TileNode tile, String bonus) {
        switch (bonus) {
            case "DW": tile.setBackgroundFill(DOUBLE_WORD_COLOR);   break;
            case "DL": tile.setBackgroundFill(DOUBLE_LETTER_COLOR); break;
            case "TW": tile.setBackgroundFill(TRIPLE_WORD_COLOR);   break;
            case "TL": tile.setBackgroundFill(TRIPLE_LETTER_COLOR); break;
            default:   tile.setBackgroundFill(EMPTY_TILE_COLOR);
        }
    }
    
}