package com.slinky.wordcheat.model;

import java.util.stream.Stream;
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

}
