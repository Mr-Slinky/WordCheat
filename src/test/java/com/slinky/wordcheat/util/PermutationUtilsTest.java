package com.slinky.wordcheat.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PermutationUtils}.
 * Exercises standard, duplicate‑element and abnormal inputs via parameterised tests,
 * using assertAll for multi‑assertion scenarios.
 */
class PermutationUtilsTest {

    @Nested
    class StandardPermutations {

        static Stream<Arguments> cases() {
            return Stream.of(
                Arguments.of((Object) new char[]{'a', 'b'},
                             new String[]{"ab", "ba"}),
                Arguments.of((Object) new char[]{'x', 'y', 'z'},
                             new String[]{"xyz", "xzy", "yxz", "yzx", "zxy", "zyx"})
            );
        }

        @ParameterizedTest(name = "[{index}] standard")
        @MethodSource("cases")
        void generateStandard(char[] input, String[] expected) {
            List<String> actual = PermutationUtils.generateAllPermutations(input);
            Set<String> actualSet = new HashSet<>(actual);
            Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));

            assertAll("Correct permutations",
                () -> assertEquals(expectedSet.size(), actual.size(),
                    "Should produce exactly " + expectedSet.size() + " permutations"),
                () -> assertEquals(expectedSet, actualSet,
                    "Should contain all expected permutations")
            );
        }
    }

    @Nested
    class DuplicateElementPermutations {

        static Stream<Arguments> duplicateCases() {
            return Stream.of(
                Arguments.of((Object) new char[]{'a', 'a'}, 2, "aa"),
                Arguments.of((Object) new char[]{'b', 'b', 'b'}, 6, "bbb")
            );
        }

        @ParameterizedTest(name = "[{index}] duplicates")
        @MethodSource("duplicateCases")
        void generateDuplicates(char[] input, int expectedCount, String expectedString) {
            List<String> actual = PermutationUtils.generateAllPermutations(input);
            assertAll("Duplicates handled",
                () -> assertEquals(expectedCount, actual.size(),
                    "Should generate factorial(n) permutations even if duplicates"),
                () -> assertTrue(actual.stream().allMatch(s -> s.equals(expectedString)),
                    "Every permutation should equal \"" + expectedString + "\"")
            );
        }
    }

    @Nested
    class AbnormalInputs {

        @ParameterizedTest(name = "[{index}] null-or-empty")
        @NullAndEmptySource
        void generateNullOrEmpty(char[] input) {
            List<String> actual = PermutationUtils.generateAllPermutations(input);
            assertTrue(actual.isEmpty(),
                "Null or empty input should yield an empty list");
        }
    }
    
}