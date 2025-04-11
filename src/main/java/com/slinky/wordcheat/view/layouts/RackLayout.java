package com.slinky.wordcheat.view.layouts;

import com.slinky.wordcheat.view.CellType;

/**
 * A simple layout representing a tile rack, typically holding 7 tiles.
 * <p>
 * This layout arranges tiles in a single row (1×7) for easy horizontal display.
 * All cells are assigned the {@link CellType#NORMAL} type.
 * </p>
 * <p>
 * This layout is implemented as a singleton, ensuring that only one instance
 * is ever created.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     RackLayout rackLayout = RackLayout.getInstance();
 *     CellType[][] rackCells = rackLayout.getLayout(); // 1 row, 7 columns
 * </pre>
 * </p>
 */
public final class RackLayout implements BoardLayout {

    /**
     * The number of rows for the rack layout.
     */
    private static final int ROWS = 1;

    /**
     * The number of columns for the rack layout (i.e. tiles).
     */
    private static final int COLS = 7;

    /**
     * A 2D array representing the layout for the rack.
     */
    private final CellType[][] layout;

    /**
     * The singleton instance of {@code RackLayout}.
     */
    private static final RackLayout SINGLETON = new RackLayout();

    /**
     * Private constructor initialises a 1×7 board where each cell is set to {@link CellType#NORMAL}.
     */
    private RackLayout() {
        layout = new CellType[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                layout[r][c] = CellType.NORMAL;
            }
        }
    }

    /**
     * Retrieves the singleton instance of {@code RackLayout}.
     *
     * @return the single instance of {@code RackLayout}.
     */
    public static RackLayout getInstance() {
        return SINGLETON;
    }

    /**
     * Returns the size of this layout. Since this layout is rectangular rather
     * than square, we return the maximum dimension to maintain consistency with
     * {@link WWFLayout}.
     *
     * @return the larger dimension of the rack layout, i.e. 7.
     */
    @Override
    public int getSize() {
        return Math.max(ROWS, COLS);
    }

    /**
     * Returns the 2D array representing the rack layout.
     *
     * @return a 2D array of {@link CellType} with 1 row and 7 columns.
     */
    @Override
    public CellType[][] getLayout() {
        return layout;
    }
    
}