package com.slinky.wordcheat.model;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

/**
 * Unit tests for the GameBoard class.
 * 
 * @author Kheagen
 */
@TestInstance(Lifecycle.PER_CLASS)
public class GameBoardConstructorTest {
    
    private char[][] getTinyEmptyGrid() {
        return new char[][] {
            {' '}
        };
    }
    
    private char[][] getTinyGrid() {
        return new char[][] {
            {'A'}
        };
    }
    
    private char[][] getLargeTestGrid() {
        return new char[][] {
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', 'O', 'R', 'B', ' ', ' ', ' '},
            {' ', ' ', ' ', 'E', 'N', 'R', 'O', 'B', 'E', 'D', ' ', 'Y', ' ', ' ', ' '},
            {'P', 'H', 'E', 'W', ' ', ' ', 'H', 'O', 'N', 'E', 'S', 'T', 'Y', ' ', ' '},
            {'L', 'I', 'T', 'E', ' ', ' ', ' ', 'W', ' ', ' ', ' ', 'E', 'O', ' ', ' '},
            {'U', ' ', ' ', ' ', ' ', ' ', 'J', 'E', 'E', ' ', ' ', ' ', 'D', ' ', ' '},
            {'G', ' ', ' ', 'P', ' ', ' ', 'A', 'R', 'F', ' ', ' ', 'C', 'H', 'I', 'T'},
            {'S', ' ', ' ', 'A', ' ', ' ', 'R', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' '},
            {' ', 'R', 'I', 'T', 'U', 'A', 'L', ' ', ' ', 'T', 'E', 'A', 'M', ' ', ' '},
            {' ', ' ', ' ', 'E', ' ', ' ', 'S', 'I', 'Z', 'E', ' ', 'D', ' ', ' ', ' '}
        };
    }
    
    private char[][] getSmallTestGrid() {
        return new char[][] {
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', 'F', 'O', 'R', 'B'},
            {' ', 'E', 'N', 'R', 'O', 'B', 'E', 'D', ' ', 'Y'},
            {' ', ' ', ' ', ' ', 'H', 'O', 'N', 'E', 'S', 'T'},
            {' ', ' ', ' ', ' ', ' ', 'W', ' ', ' ', ' ', 'E'},
            {' ', ' ', ' ', ' ', 'J', 'E', 'E', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', 'A', 'R', 'F', ' ', ' ', ' '}
        };
    }
    
    private char[][] getEmptyTestGrid() {
        return new char[][] {
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
        };
    }
    
    // A constant for the blank tile for test comparisons (should match TileData.BLANK_TILE)
    private final char BLANK_TILE = ' ';
    
    // Method source for valid grid test cases
    private Stream<Arguments> provideStandardGrids() {
        return Stream.of(
            Arguments.of(getTinyGrid(), "Tiny Grid"),
            Arguments.of(getTinyEmptyGrid(), "Tiny Empty Grid"),
            Arguments.of(getSmallTestGrid(), "Small Test Grid"),
            Arguments.of(getLargeTestGrid(), "Large Test Grid"),
            Arguments.of(getEmptyTestGrid(), "Empty Test Grid")
        );
    }
    
    // Method source for abnormal grid test cases (non-rectangular grids)
    private Stream<Arguments> provideAbnormalGrids() {
        char[][] nonRectangularGrid = new char[][] {
            {'A', 'B'},
            {'C'}  // Second row is shorter, making this grid non-rectangular.
        };
        return Stream.of(
            Arguments.of(nonRectangularGrid, "Non-Rectangular Grid")
        );
    }
    
    @ParameterizedTest(name = "{1} should construct without exception")
    @MethodSource("provideStandardGrids")
    public void testConstructor_StandardData_NoThrow(char[][] grid, String gridName) {
        // Ensure the constructor does not throw and the board dimensions are correctly set.
        assertDoesNotThrow(() -> {
            GameBoard board = new GameBoard(grid);
            assertEquals(grid.length, board.getRows(), gridName + ": row count mismatch");
            assertEquals(grid[0].length, board.getCols(), gridName + ": column count mismatch");
        }, gridName + " should not throw an exception during GameBoard construction.");
    }
    
    @ParameterizedTest(name = "{1} should throw exception during construction")
    @MethodSource("provideAbnormalGrids")
    public void testConstructor_AbnormalData_ThrowsException(char[][] grid, String gridName) {
        // Expect an IllegalArgumentException due to non-rectangular grid.
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(grid),
                     gridName + " should throw IllegalArgumentException due to non-rectangular grid.");
    }
    
}