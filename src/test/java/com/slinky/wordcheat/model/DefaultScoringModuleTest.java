package com.slinky.wordcheat.model;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Kheagen Haskins
 */
public class DefaultScoringModuleTest {
    
    private static char[][] getTestBoard1() {
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
    
    private static Stream<Arguments> provideValidPlacementsWithScores() {
        var grid = getTestBoard1();
        
        return Stream.of(
                Arguments.of(grid, "VOLANTE", 6, 14, false, 27),
                Arguments.of(grid, "C",       7, 10, true,  25),
                Arguments.of(grid, "S",       7,  0, true,  13),
                Arguments.of(grid, "S",       13, 0, true,  22),
                Arguments.of(grid, "G",      14, 10, true,  22),
                Arguments.of(grid, "AG",     12,  7, true,  25)
        );
    }
    
    @ParameterizedTest
    @MethodSource("provideValidPlacementsWithScores")
    public void testBoard1(char[][] grid, String word, int rowStart, int colStart, boolean horizontal, int expectedScore) {
        GameBoard testBoard = new GameBoard(grid);
        DefaultScoringModule scoreModule = new DefaultScoringModule();
        
        testBoard.placeWord(word, rowStart, colStart, horizontal);
        int score = scoreModule.calculateScore(testBoard);
        
        assertEquals(expectedScore, score);
    }
    
        private static Stream<Arguments> provideAllUppercaseLetters() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars()
                .mapToObj(c -> Arguments.of((char) c, expectedScoreFor((char) c)));
    }

    private static Stream<Arguments> provideAllLowercaseLetters() {
        return "abcdefghijklmnopqrstuvwxyz".chars()
                .mapToObj(c -> Arguments.of((char) c));
    }

    private static Stream<Arguments> provideAbnormalCharacters() {
        return Stream.of(
                Arguments.of('!'),
                Arguments.of('['),
                Arguments.of('@'),
                Arguments.of('\0'),
                Arguments.of('中')
        );
    }

    private static int expectedScoreFor(char c) {
        return switch (c) {
            case 'A' -> 1;  case 'B' -> 4;  case 'C' -> 4;  case 'D' -> 2;
            case 'E' -> 1;  case 'F' -> 4;  case 'G' -> 3;  case 'H' -> 3;
            case 'I' -> 1;  case 'J' -> 10; case 'K' -> 5;  case 'L' -> 2;
            case 'M' -> 4;  case 'N' -> 2;  case 'O' -> 1;  case 'P' -> 4;
            case 'Q' -> 10; case 'R' -> 1;  case 'S' -> 1;  case 'T' -> 1;
            case 'U' -> 2;  case 'V' -> 5;  case 'W' -> 4;  case 'X' -> 8;
            case 'Y' -> 3;  case 'Z' -> 10;
            default -> throw new IllegalArgumentException("Unexpected char: " + c);
        };
    }

    @ParameterizedTest(name = "Letter {0} should score {1}")
    @MethodSource("provideAllUppercaseLetters")
    public void testGetPointsOf_ValidUppercaseLetters(char input, int expected) {
        assertEquals(expected, DefaultScoringModule.getPointsOf(input));
    }

    @ParameterizedTest(name = "Lowercase letter {0} should throw IllegalArgumentException")
    @MethodSource("provideAllLowercaseLetters")
    public void testGetPointsOf_LowercaseLetters_Throws(char input) {
        assertThrows(IllegalArgumentException.class, () -> DefaultScoringModule.getPointsOf(input));
    }

    @ParameterizedTest(name = "Abnormal character {0} should throw IllegalArgumentException")
    @MethodSource("provideAbnormalCharacters")
    public void testGetPointsOf_AbnormalInputs_Throws(char input) {
        assertThrows(IllegalArgumentException.class, () -> DefaultScoringModule.getPointsOf(input));
    }
    
      
    /* ===============================================
     * Test for getScoreFrequencies()
     * ===============================================
     */
    
    @Test
    public void testGetScoreFrequencies() {
        // Expected frequency distribution:
        // Index 0: Wild cards           => 2
        // Index 1: Letters scoring 1x   => 7  (A, E, I, O, T, R, S)
        // Index 2: Letters scoring 2x   => 4  (D, N, L, U)
        // Index 3: Letters scoring 3x   => 3  (H, G, Y)
        // Index 4: Letters scoring 4x   => 6  (B, C, F, M, P, W)
        // Index 5: Letters scoring 5x   => 2  (V, K)
        // Index 6: Letters scoring 8x   => 1  (X)
        // Index 7: Letters scoring 10x  => 3  (Q, Z, J)
        int[] expectedFrequencies = {2, 7, 4, 3, 6, 2, 1, 3};
        
        // Retrieve the frequency array from the production method.
        int[] actualFrequencies = DefaultScoringModule.getScoreFrequencies();
        
        // Use an assertAll block to ensure each frequency value is as expected.
        assertAll("Verify score frequencies",
            () -> assertEquals(expectedFrequencies[0], actualFrequencies[0], "Wild cards frequency (score 0)"),
            () -> assertEquals(expectedFrequencies[1], actualFrequencies[1], "Frequency of tiles scoring 1 point"),
            () -> assertEquals(expectedFrequencies[2], actualFrequencies[2], "Frequency of tiles scoring 2 points"),
            () -> assertEquals(expectedFrequencies[3], actualFrequencies[3], "Frequency of tiles scoring 3 points"),
            () -> assertEquals(expectedFrequencies[4], actualFrequencies[4], "Frequency of tiles scoring 4 points"),
            () -> assertEquals(expectedFrequencies[5], actualFrequencies[5], "Frequency of tiles scoring 5 points"),
            () -> assertEquals(expectedFrequencies[6], actualFrequencies[6], "Frequency of tiles scoring 8 points"),
            () -> assertEquals(expectedFrequencies[7], actualFrequencies[7], "Frequency of tiles scoring 10 points")
        );
    }
}