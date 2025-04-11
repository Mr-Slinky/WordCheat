package com.slinky.wordcheat.view.layouts;

import com.slinky.wordcheat.view.CellType;

/**
 * The {@code BoardLayout} interface defines a contract for classes that
 * provide a 2D array of {@link CellType} representing a board layout.
 * 
 * <p>
 * Concrete implementations can represent different board games or
 * variants (e.g., Words With Friends, Scrabble, or custom layouts).
 * </p>
 */
public interface BoardLayout {

    /**
     * Returns the size (dimension) of the board. 
     * <p>
     * For a standard board game like Words With Friends or Scrabble,
     * this value is typically 15.
     * </p>
     *
     * @return the dimension of the board (assuming a square board).
     */
    int getSize();

    /**
     * Returns a 2D array of {@link CellType} that defines the layout of
     * bonus squares (e.g. double word, triple letter) for the board.
     * 
     * <p>
     * Implementations may choose to mirror or otherwise generate the layout
     * in a static block, or construct it on-the-fly. The returned array
     * should have dimensions {@code getSize()} × {@code getSize()}.
     * </p>
     *
     * @return a 2D array of {@code CellType} representing the board layout.
     */
    CellType[][] getLayout();
    
}