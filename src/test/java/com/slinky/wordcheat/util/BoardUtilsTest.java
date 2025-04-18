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