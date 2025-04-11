package com.slinky.wordcheat.model;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Kheagen
 */
public class WordFinderTest {
    
    private static char[][] getTestBoard() {
       return new char[][] {
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  0
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  1
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  2
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', 'C'}, //  3
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', 'O'}, //  4
            {' ', ' ', 'F', 'U', 'G', 'U', ' ', 'V', ' ', ' ', ' ', 'B', 'O', 'G', 'S'}, //  5
            {' ', 'R', 'E', 'T', 'I', 'T', 'L', 'E', ' ', ' ', 'J', 'O', 'E', ' ', ' '}, //  6
            {' ', ' ', 'T', 'E', 'E', ' ', ' ', 'R', 'U', 'L', 'E', 'R', 'S', ' ', ' '}, //  7
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'N', 'O', 'T', 'A', ' ', ' ', ' '}, //  8
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'C', ' ', 'A', 'S', ' ', ' ', ' ', ' '}, //  9
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', 'H', 'M', ' ', ' ', ' ', ' ', ' '}, // 10
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'B', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // 11
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // 12
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // 13
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}  // 14
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
        };
    }
    
    static Stream<Arguments> provideTestCaseSetOne() {
        var testBoard = getTestBoard();
        var dictionary = new OxfordDictionary();
        Suggestion highestScoringWord = new Suggestion("HEADLINE", 54, 3, 3, false);
        char[] letters = "HEADIEN".toCharArray();
        
        return Stream.of(
                Arguments.of(testBoard, dictionary, highestScoringWord, letters)
        );
    }
    
    @ParameterizedTest
    @MethodSource("provideTestCaseSetOne")
    public void testHighestScoringWords(char[][] matrix, Dictionary lexicon, Suggestion highestScoringWord, char[] letters) {
        GameBoard board           = new GameBoard(matrix);
        ScoringModule scoreModule = new DefaultScoringModule();
        WordFinder testObject     = new WordFinder(board, lexicon, scoreModule);
       
        var sugList = testObject.getWordSuggestions(new LetterRack(letters));
        if (sugList.isEmpty()) {
            fail("No suggestions found when suggestions do exist");
        }
        
        var highestFoundWord = testObject.getHighestScoringWord();
        
        assertAll(
                () -> assertEquals(highestScoringWord.word(), highestFoundWord.word()),
                () -> assertEquals(highestScoringWord.score(), highestFoundWord.score()),
                () -> assertEquals(highestScoringWord.col(), highestFoundWord.col()),
                () -> assertEquals(highestScoringWord.row(), highestFoundWord.row()),
                () -> assertEquals(highestScoringWord.verticallyPlaced(), highestFoundWord.verticallyPlaced())
        );
    }
    
}
