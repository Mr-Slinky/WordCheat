package com.slinky.wordcheat.view;

import com.slinky.wordcheat.view.layouts.BoardLayout;
import com.slinky.wordcheat.view.layouts.WWFLayout;
import com.slinky.wordcheat.view.layouts.SetLayout;
import com.slinky.wordcheat.view.layouts.RackLayout;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javafx.geometry.Insets;

/**
 * A flexible grid capable of displaying different board layouts.
 * 
 * <p>
 * This class extends {@link GridPane} to arrange {@link TileView} instances in rows
 * and columns. It relies on a {@link BoardLayout} to determine both the
 * dimensions of the grid and the {@link CellType} for each position.
 * </p>
 * 
 * <p>
 * In practice, this class is used to render:
 * <ul>
 *   <li>A 15×15 Words With Friends board (via {@link WWFLayout}).</li>
 *   <li>A rack layout (e.g. 1×7 or 7×1), (via {@link RackLayout}).</li>
 *   <li>A set layout (e.g. 13×2), (via {@link SetLayout}).</li>
 * </ul>
 * 
 * Each cell is created using the appropriate subclass of {@link TileView}
 * (for example, {@link GridCell} for the main board or {@link SetCell} for the
 * letter set) based on the provided layout.
 * </p>
 */
public final class MatrixView extends GridPane implements Iterable<TileView> {

    /**
     * The 2D array of {@link TileView} objects displayed by this grid.
     */
    private final TileView[][] cells;
    private final BoardLayout layout;
    
    /**
     * Creates a new {@code TileMatrix} using the given layout.
     * 
     * <p>
     * The constructor inspects the provided {@link BoardLayout} to determine
     * the row and column counts, then populates this {@code GridPane} with
     * instances of {@link TileView} (either {@link GridCell} or {@link SetCell},
     * depending on which layout implementation is provided).
     * </p>
     *
     * @param layout the {@link BoardLayout} specifying grid size and cell types.
     * @throws IllegalArgumentException if the layout is null or if its array
     *                                  dimensions are invalid.
     */
    public MatrixView(BoardLayout layout, int hGap, int vGap) {
        if (layout == null) {
            throw new IllegalArgumentException("Layout cannot be null.");
        }
        
        if (hGap < 0 || vGap < 0) {
            throw new IllegalArgumentException("HGap and VGap cannot be negative");
        }
        
        this.layout = layout;
        setHgap(hGap);
        setVgap(hGap);
        setPadding(new Insets(10));
        
        // Obtain the 2D array of CellTypes from the layout.
        CellType[][] cellTypes = layout.getLayout();
        if (cellTypes == null || cellTypes.length == 0 || cellTypes[0].length == 0) {
            throw new IllegalArgumentException("Layout's cell type array must be non-empty.");
        }

        int rows   = cellTypes.length;
        int cols   = cellTypes[0].length;
        this.cells = new TileView[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileView tile = createTile(cellTypes[r][c]);
                cells[r][c] = tile;
                this.add(tile, c, r);
            }
        }
        
        // Calculate the maximum total width and height for this grid.
        double maxWidth  = cols * TileView.MAX_SIZE + (cols - 1) * hGap + 20;
        double maxHeight = rows * TileView.MAX_SIZE + (rows - 1) * vGap + 20;

        setMaxSize (maxWidth, maxHeight);
        setMinSize (maxWidth, maxHeight);
        setPrefSize(maxWidth, maxHeight);
        
    }
    
    // ===========================[ Accessor Methods ]===========================
    /**
     * Retrieves the {@link TileView} at the specified row and column.
     *
     * @param row the row index.
     * @param col the column index.
     * @return the {@code TileView} at the specified position.
     * @throws IndexOutOfBoundsException if row or col are out of range.
     */
    public TileView getCellAt(int row, int col) {
        return cells[row][col];
    }
    
    /**
     * Sets a custom font for all tiles in this matrix.
     * <p>
     * This method iterates over every tile and applies the custom font using
     * the tile's {@link TileView#setFont(Font)} method.
     * </p>
     *
     * @param font the custom font to apply to all tiles; must not be null.
     * @throws IllegalArgumentException if font is null.
     */
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Font cannot be null.");
        }
        
        for (TileView[] row : cells) {
            for (TileView tile : row) {
                tile.setFont(font);
            }
        }
    }
    
    /**
     * Sets the centre text at the specified cell.
     * <p>
     * If the provided text is a single character, the tile's letter flag is set and
     * a special appearance may be applied.
     * </p>
     *
     * @param row the row index.
     * @param col the column index.
     * @param text the text to display.
     */
    public void setTextAt(int row, int col, String text) {
        int r = cells.length;
        int c = cells[0].length;
        if (row < 0 || row >= r || col < 0 || col >= c) {
            throw new IndexOutOfBoundsException(
                    "Row and/or column index [%d][%d] out of bounds for [%d][%d]".
                            formatted(row, col, r, c)
            );
        }
        
        if (text == null || text.isBlank()) {
            cells[row][col].setCenterText("");
        } else if (text.length() == 1) {
            // Assume setLetter method is defined to handle single letters.
            cells[row][col].setLetter(text.charAt(0), TileView.DEFAULT_LETTER_COLOR);
        } else {
            cells[row][col].setCenterText(text);
        }
    }
    
    /**
     * Returns an iterator over all the tiles in this matrix in row-major order.
     *
     * @return an Iterator over TileView objects.
     */
    @Override
    public Iterator<TileView> iterator() {
        return new Iterator<>() {
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext() {
                return row < cells.length && col < cells[0].length;
            }

            @Override
            public TileView next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                TileView current = cells[row][col];
                col++;
                if (col >= cells[0].length) {
                    col = 0;
                    row++;
                }
                
                return current;
            }
        };
    }
    
    // ============================[ Helper Methods ]============================
    private TileView createTile(CellType type) {
        Color color = Color.rgb(type.getRed(), type.getBlue(), type.getGreen());

        TileView tile = new TileView(type);
        tile.setCenterText(type.getText());
        tile.setBackgroundColor(color);
        tile.setFont(Font.font("Arial"));
        tile.setTextColor(Color.WHITE);

        return tile;
    }
    
}