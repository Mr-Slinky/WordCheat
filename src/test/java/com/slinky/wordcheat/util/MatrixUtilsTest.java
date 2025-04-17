package com.slinky.wordcheat.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MatrixUtils}.
 * Covers standard, extreme and abnormal inputs via parameterised methods,
 * using assertAll for multiple assertions.
 */
class MatrixUtilsTest {

    @Nested
    class RotateCharMatrix {

        static Stream<Arguments> standardCases() {
            return Stream.of(
                // wrap in Object to prevent varargs flattening of char[][]
                Arguments.of((Object) new char[][]{{'a','b','c'},{'d','e','f'}}, true,
                             new char[][]{{'d','a'},{'e','b'},{'f','c'}}),
                Arguments.of((Object) new char[][]{{'a','b','c'},{'d','e','f'}}, false,
                             new char[][]{{'c','f'},{'b','e'},{'a','d'}})
            );
        }

        @ParameterizedTest(name = "[{index}] rotate(char[{0}.length][…], cw={1})")
        @MethodSource("standardCases")
        void rotateStandard(char[][] input, boolean clockwise, char[][] expected) {
            assertArrayEquals(expected,
                MatrixUtils.rotate(input, clockwise),
                "Rotation should match expected");
        }

        static Stream<Arguments> extremeCases() {
            return Stream.of(
                Arguments.of((Object) new char[][]{{'X'}}, true,  new char[][]{{'X'}}),
                Arguments.of((Object) new char[][]{{'X'}}, false, new char[][]{{'X'}})
            );
        }

        @ParameterizedTest(name = "[{index}] 1×1 rotate(cw={1})")
        @MethodSource("extremeCases")
        void rotateExtreme(char[][] input, boolean clockwise, char[][] expected) {
            assertAll("1×1 matrix remains unchanged",
                () -> assertArrayEquals(expected, MatrixUtils.rotate(input, clockwise)),
                () -> assertArrayEquals(input,    MatrixUtils.rotate(expected, !clockwise))
            );
        }

        static Stream<Arguments> abnormalCases() {
            return Stream.of(
                Arguments.of((Object) null, true),
                Arguments.of((Object) new char[0][], false)
            );
        }

        @ParameterizedTest(name = "[{index}] rotate(abnormal, cw={1}) throws")
        @MethodSource("abnormalCases")
        void rotateAbnormal(char[][] input, boolean clockwise) {
            assertThrows(IllegalArgumentException.class,
                () -> MatrixUtils.rotate(input, clockwise),
                "Expected an IllegalArgumentException for null or empty input");
        }
    }

    @Nested
    class DeepCopyCharMatrix {

        static Stream<Arguments> standardCases() {
            return Stream.of(
                Arguments.of((Object) new char[][]{{'a','b'},{'c','d'}}),
                Arguments.of((Object) new char[0][])
            );
        }

        @ParameterizedTest(name = "[{index}] deepCopy({0})")
        @MethodSource("standardCases")
        void deepCopyStandard(char[][] input) {
            char[][] copy = MatrixUtils.deepCopy(input);
            assertAll("Copy must equal original but be distinct",
                () -> assertArrayEquals(input, copy,      "Contents should match"),
                () -> assertNotSame(input,   copy,      "Should not be same instance")
            );
        }

        @Test
        void deepCopyNullThrows() {
            assertThrows(IllegalArgumentException.class,
                () -> MatrixUtils.deepCopy((char[][]) null),
                "Expected IllegalArgumentException for null input");
        }
    }

    @Nested
    class ToStringCharMatrix {

        static Stream<Arguments> cases() {
            return Stream.of(
                Arguments.of((Object) new char[][]{{'a','1'},{'2','B'}},
                    " |0|1|\n" +
                    "0|a| |\n" +
                    "1| |B|\n")
            );
        }

        @ParameterizedTest(name = "[{index}] toString({0})")
        @MethodSource("cases")
        void toStringStandard(char[][] input, String expected) {
            assertEquals(expected,
                MatrixUtils.toString(input),
                "Rendered string should match expected format");
        }

        @Test
        void toStringNullThrows() {
            assertThrows(NullPointerException.class,
                () -> MatrixUtils.toString((char[][]) null),
                "Expected NullPointerException for null input");
        }
    }
}