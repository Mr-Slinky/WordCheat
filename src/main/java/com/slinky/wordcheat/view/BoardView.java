package com.slinky.wordcheat.view;

import static com.slinky.wordcheat.view.ColorConstants.DOUBLE_LETTER_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.DOUBLE_WORD_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.EMPTY_TILE_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.TRIPLE_LETTER_COLOR;
import static com.slinky.wordcheat.view.ColorConstants.TRIPLE_WORD_COLOR;


import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * A fully programmatic grid view for displaying TileNode instances, showing
 * letter, score, and bonus for each cell.
 */
public final class BoardView extends GridPane {

    // ================================[ Fields ]================================ \\
    private final int rows;
    private final int cols;
    private final TileNode[][] tiles;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new grid view from the given data matrices.
     *
     * @param letters  2D char array of letters ('A'–'Z' or other for empty)
     * @param scores   2D int array of tile scores; must match letters dims
     * @param bonuses  2D String array of bonus text (e.g. "DL", "TW"), or null/empty
     * @throws NullPointerException     if any matrix is null
     * @throws IllegalArgumentException if dimensions mismatch or zero size
     */
    public BoardView(char[][] letters, int[][] scores, String[][] bonuses) {
        validConstructorParams(letters, scores, bonuses);
        
        this.rows  = letters.length;
        this.cols  = letters[0].length;
        this.tiles = new TileNode[rows][cols];
        initialiseGrid(letters, scores, bonuses);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Retrieves the TileNode at the specified position.
     *
     * @param row zero‑based row index
     * @param col zero‑based column index
     * @return the TileNode at (row, col)
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

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
    
    // ============================[ Helper Methods ]============================ \\
    private void initialiseGrid(char[][] letters, int[][] scores, String[][] bonuses) {
        setHgap(4);
        setVgap(4);
        setAlignment(Pos.CENTER);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Use factory to create base tile
                char letter   = letters[r][c];
                int score     = scores[r][c];
                String bonus  = bonuses[r][c];
                TileNode node = TileFactory.createBoardTile(r, c, String.valueOf(letter));

                boolean hasLetter = !(letter < 'A' || letter > 'Z');

                // Logical state
                node.setScore(score);
                node.setBonus(bonus);

                // Background color based on bonus or empty
                if (!(hasLetter || bonus == null)) {
                    switch (bonus) {
                        case "DW": node.setBackgroundFill(DOUBLE_WORD_COLOR);   break;
                        case "DL": node.setBackgroundFill(DOUBLE_LETTER_COLOR); break;
                        case "TW": node.setBackgroundFill(TRIPLE_WORD_COLOR);   break;
                        default:   node.setBackgroundFill(TRIPLE_LETTER_COLOR);
                    }
                } else if (!hasLetter) {
                    node.setBackgroundFill(EMPTY_TILE_COLOR);
                }
                
                // Apply style and refresh display
                node.applyStyle();
                node.refresh();

                tiles[r][c] = node;
                add(node, c, r);
            }
        }
    }

    private void validConstructorParams(char[][] letters, int[][] scores, String[][] bonuses) {
        Objects.requireNonNull(letters, "Letters matrix cannot be null");
        Objects.requireNonNull(scores,  "Scores matrix cannot be null");
        Objects.requireNonNull(bonuses, "Bonuses matrix cannot be null");
        if (letters.length == 0 || letters[0].length == 0) {
            throw new IllegalArgumentException("Letters matrix cannot be empty");
        }
        
        int rowCount    = letters.length;
        int columnCount = letters[0].length;
        
        if (scores.length != rowCount || bonuses.length != rowCount) {
            throw new IllegalArgumentException("All matrices must have the same row count");
        }
        
        for (int r = 0; r < rowCount; r++) {
            if (letters[r].length != columnCount || scores[r].length != columnCount || bonuses[r].length != columnCount) {
                throw new IllegalArgumentException(
                    String.format("All matrices must have %d columns; row %d lengths: letters=%d, scores=%d, bonuses=%d",
                                   columnCount, r, letters[r].length, scores[r].length, bonuses[r].length)
                );
            }
        }
    }
    
}