package com.slinky.wordcheat.view.layouts;

import com.slinky.wordcheat.view.CellType;

/**
 * A simple layout representing the "set" of tiles remaining in the game,
 * arranged in 13 rows and 2 columns.
 * <p>
 * Each cell is assigned the {@link CellType#NORMAL} type by default, but you
 * can adjust or enhance this layout if you need special styling for certain
 * rows/columns.
 * </p>
 * <p>
 * This layout is implemented as a singleton, ensuring that only one instance
 * is ever created.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     SetLayout setLayout = SetLayout.getInstance();
 *     CellType[][] setCells = setLayout.getLayout(); // 13 rows, 2 columns
 * </pre>
 * </p>
 */
public final class SetLayout implements BoardLayout {

    /**
     * The number of rows in the set layout (13).
     */
    private static final int ROWS = 13;

    /**
     * The number of columns in the set layout (2).
     */
    private static final int COLS = 2;

    /**
     * A 2D array representing the layout for the tile set.
     */
    private final CellType[][] layout;

    /**
     * The singleton instance of {@code SetLayout}.
     */
    private static final SetLayout SINGLETON = new SetLayout();

    /**
     * Private constructor initialises a 13×2 board where each cell is set to {@link CellType#NORMAL}.
     */
    private SetLayout() {
        layout = new CellType[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                layout[r][c] = CellType.NORMAL;
            }
        }
    }

    /**
     * Retrieves the singleton instance of {@code SetLayout}.
     *
     * @return the single instance of {@code SetLayout}.
     */
    public static SetLayout getInstance() {
        return SINGLETON;
    }

    /**
     * Returns the size of this layout. Since this layout is rectangular rather
     * than square, we return the larger dimension (13) to maintain consistency
     * with {@link WWFLayout}.
     *
     * @return the larger dimension of the set layout, i.e. 13.
     */
    @Override
    public int getSize() {
        return Math.max(ROWS, COLS);
    }

    /**
     * Returns the 2D array representing the set layout.
     *
     * @return a 2D array of {@link CellType} with 13 rows and 2 columns.
     */
    @Override
    public CellType[][] getLayout() {
        return layout;
    }
}
