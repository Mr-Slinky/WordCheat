package com.slinky.wordcheat.view.layouts;

import com.slinky.wordcheat.view.CellType;

/**
 * The {@code WWFLayout} class provides a 15×15 board layout for Words With
 * Friends, implementing the {@link BoardLayout} interface.
 *
 * <p>
 * This version is designed as a singleton, ensuring that only one instance is
 * ever created.
 * </p>
 *
 * @see BoardLayout
 * @see CellType
 */
public final class WWFLayout implements BoardLayout {

    /**
     * The singleton instance of {@code WWFLayout}.
     */
    private static final WWFLayout SINGLETON = new WWFLayout();

    /**
     * Retrieves the singleton instance of {@code WWFLayout}.
     *
     * @return the single instance of {@code WWFLayout}
     */
    public static WWFLayout getInstance() {
        return SINGLETON;
    }

    /**
     * The dimension of the Words With Friends board (15×15).
     */
    private static final int SIZE = 15;

    /**
     * The two-dimensional array representing the board layout.
     */
    private final CellType[][] boardLayout;

    /**
     * Private constructor initialises the board layout.
     */
    private WWFLayout() {
        boardLayout = new CellType[SIZE][SIZE];
        // 1) Set all cells to NORMAL by default.
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boardLayout[r][c] = CellType.NORMAL;
            }
        }

        // Manually set specific cells.
        boardLayout[3][7]  = CellType.DW;
        boardLayout[11][7] = CellType.DW;
        boardLayout[7][3]  = CellType.DW;
        boardLayout[7][11] = CellType.DW;

        // Populate the board using symmetry.
        setSym(boardLayout, 0, 3, CellType.TW);
        setSym(boardLayout, 1, 2, CellType.DL);
        setSym(boardLayout, 2, 1, CellType.DL);
        setSym(boardLayout, 3, 0, CellType.TW);

        setSym(boardLayout, 0, 6, CellType.TL);
        setSym(boardLayout, 1, 5, CellType.DW);
        setSym(boardLayout, 2, 4, CellType.DL);
        setSym(boardLayout, 3, 3, CellType.TL);
        setSym(boardLayout, 4, 2, CellType.DL);
        setSym(boardLayout, 5, 1, CellType.DW);
        setSym(boardLayout, 6, 0, CellType.TL);

        setSym(boardLayout, 4, 6, CellType.DL);
        setSym(boardLayout, 5, 5, CellType.TL);
        setSym(boardLayout, 6, 4, CellType.DL);
    }

    /**
     * Helper method to assign a CellType to the specified row/column in the
     * top-left quadrant and mirror it to the three corresponding quadrants.
     *
     * @param layout the board layout array
     * @param row the row index in the top-left quadrant
     * @param col the column index in the top-left quadrant
     * @param type the {@link CellType} to assign
     */
    private void setSym(CellType[][] layout, int row, int col, CellType type) {
        layout[row][col] = type;
        layout[row][SIZE - 1 - col] = type;
        layout[SIZE - 1 - row][col] = type;
        layout[SIZE - 1 - row][SIZE - 1 - col] = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned 2D array is the actual layout. Modifying it directly will
     * affect the singleton's state. If immutability is desired, consider
     * returning a copy.
     * </p>
     */
    @Override
    public CellType[][] getLayout() {
        return boardLayout;
    }

}