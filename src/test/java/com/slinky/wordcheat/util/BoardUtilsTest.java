package com.slinky.wordcheat.util;

import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.TileBonus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BoardUtils}.
 * <p>
 * Verifies correct behaviour of findColumnBound and setSymmetry under
 * standard, edge and invalid input scenarios using parameterised tests
 * and assertAll for multi-assertion cases.
 */
class BoardUtilsTest {

    @Nested
    class FindColumnBoundTests {

        static Stream<Arguments> standardCases() {
            return Stream.of(
                // full row of letters: bound is first empty column (3)
                Arguments.of(new char[][]{
                                {'a','b','c'},
                                {'d','e','f'},
                                {'g','h','i'}
                             },
                             1, 0, true, 3),
                // blank cell at (0,1): searching right from col 0 stops at 1
                Arguments.of(new char[][]{
                                {'a',' ','c'},
                                {'d','e','f'}
                             },
                             0, 0, true, 1),
                // searching left from col 2 in a full row: inclusive bound 0
                Arguments.of(new char[][]{
                                {'x','y','z'}
                             },
                             0, 2, false, 0)
            );
        }

        @ParameterizedTest
        @MethodSource("standardCases")
        void testFindColumnBoundStandard(char[][] grid,
                                         int row,
                                         int col,
                                         boolean toRight,
                                         int expected) {
            GameBoard board = new GameBoard(grid);
            int bound = BoardUtils.findColumnBound(board, row, col, toRight);
            assertEquals(expected,
                         bound,
                         () -> "Expected bound " + expected +
                               " for row=" + row +
                               ", col=" + col +
                               ", toRight=" + toRight);
        }

        static Stream<Arguments> abnormalCases() {
            GameBoard single = new GameBoard(new char[][]{{'A'}});
            return Stream.of(
                Arguments.of(single, -1, 0, true),
                Arguments.of(single, 0, -1, true),
                Arguments.of(single, 1, 0, true),
                Arguments.of(single, 0, 1, false)
            );
        }

        @ParameterizedTest
        @MethodSource("abnormalCases")
        void testFindColumnBoundAbnormal(GameBoard board,
                                         int row,
                                         int col,
                                         boolean toRight) {
            assertThrows(IllegalArgumentException.class,
                         () -> BoardUtils.findColumnBound(board, row, col, toRight));
        }
    }

    @Nested
    class SetSymmetryTests {

        static Stream<Arguments> symmetryCases() {
            // use first enum constant for testing
            TileBonus type = TileBonus.values()[0];
            return Stream.of(
                // size 4, primary at (1,2)
                Arguments.of(4, 1, 2, type),
                // size 5, primary at (0,0)
                Arguments.of(5, 0, 0, type)
            );
        }

        @ParameterizedTest
        @MethodSource("symmetryCases")
        void testSetSymmetry(int size,
                             int row,
                             int col,
                             TileBonus type) {
            TileBonus[][] matrix = new TileBonus[size][size];
            BoardUtils.setSymmetry(matrix, row, col, type);

            int lastRow = size - 1;
            int lastCol = size - 1;

            assertAll("Symmetric assignments for (" + row + "," + col + ")",
                () -> assertEquals(type, matrix[row][col],
                                   "Primary position"),
                () -> assertEquals(type, matrix[row][lastCol - col],
                                   "Horizontal mirror"),
                () -> assertEquals(type, matrix[lastRow - row][col],
                                   "Vertical mirror"),
                () -> assertEquals(type, matrix[lastRow - row][lastCol - col],
                                   "Diagonal mirror")
            );
        }

        @Test
        void testSetSymmetryOutOfBounds() {
            TileBonus[][] matrix = new TileBonus[3][3];
            TileBonus dummy = TileBonus.values()[0];

            assertAll("Out-of-range indices should throw",
                () -> assertThrows(IllegalArgumentException.class,
                                   () -> BoardUtils.setSymmetry(matrix, 3, 0, dummy)),
                () -> assertThrows(IllegalArgumentException.class,
                                   () -> BoardUtils.setSymmetry(matrix, 0, 3, dummy))
            );
        }
    }
}