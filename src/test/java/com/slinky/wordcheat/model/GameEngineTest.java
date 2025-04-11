package com.slinky.wordcheat.model;

import java.util.stream.Stream;

import com.slinky.wordcheat.language.OxfordDictionary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Kheagen Haskins
 */
public class GameEngineTest {
    
    private GameEngine testEngine;
    private GameBoard  testBoard;
    private TileSet    testSet;
    private WordFinder testFinder;
   
    @BeforeEach
    public void setUp() {
        testBoard = new GameBoard(TEST_MATRIX_1);
        testBoard.setWildCardPosition(9, 10);
        testBoard.setWildCardPosition(3, 11);

        testSet    = new DefaultTileSet();
        testFinder = new WordFinder(testBoard, new OxfordDictionary(), new DefaultScoringModule());

        testEngine = new GameEngine(testBoard, testSet, testFinder, "SKQNDSA".toCharArray());
    }
    
    private static final char[][] TEST_MATRIX_1 = {
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
            {' ', ' ', 'G', 'R', 'O', 'P', 'E', ' ', 'O', 'F', ' ', 'W', ' ', ' ', ' '}, //  0
            {' ', ' ', 'U', ' ', ' ', ' ', 'T', 'H', 'R', 'I', 'V', 'E', 'N', ' ', ' '}, //  1
            {' ', ' ', 'M', 'A', 'Y', 'A', ' ', ' ', ' ', ' ', ' ', 'I', ' ', ' ', ' '}, //  2
            {' ', ' ', ' ', 'H', 'E', 'A', 'D', 'L', 'I', 'N', 'E', 'R', ' ', ' ', 'C'}, //  3
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', 'O'}, //  4
            {' ', ' ', 'F', 'U', 'G', 'U', ' ', 'V', ' ', ' ', ' ', 'B', 'O', 'G', 'S'}, //  5
            {' ', 'R', 'E', 'T', 'I', 'T', 'L', 'E', ' ', ' ', 'J', 'O', 'E', ' ', ' '}, //  6
            {' ', ' ', 'T', 'E', 'E', ' ', ' ', 'R', 'U', 'L', 'E', 'R', 'S', ' ', ' '}, //  7
            {' ', ' ', ' ', ' ', 'S', 'I', 'N', ' ', 'N', 'O', 'T', 'A', ' ', ' ', ' '}, //  8
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'C', ' ', 'A', 'S', ' ', ' ', ' ', ' '}, //  9
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', 'H', 'M', ' ', ' ', ' ', ' ', ' '}, // 10
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'B', 'A', ' ', ' ', ' ', ' ', ' ', ' '}, // 11
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' '}, // 12
            {' ', ' ', ' ', 'D', 'I', 'T', 'Z', 'I', 'E', 'R', ' ', ' ', ' ', ' ', ' '}, // 13
            {' ', ' ', 'W', 'E', 'D', ' ', ' ', ' ', 'D', 'E', 'A', 'T', 'H', 'Y', ' '}  // 14
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
    };
    
    private static final int[][] WILD_CARD_POS_1 = {{9, 10}, {3, 11}};
    
    private static final char[][] TEST_MATRIX_2 = {
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', 'F', 'R', 'E', 'T', ' ', ' ', ' ', 'I', ' ', ' ', 'T'},
        {' ', ' ', ' ', ' ', ' ', ' ', 'M', 'O', 'F', 'O', ' ', 'N', ' ', ' ', 'I'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'V', ' ', 'D', ' ', 'N', 'E'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'G', ' ', 'A', 'G', 'E', ' ', 'O', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'H', 'E', 'L', 'I', 'X', ' ', 'V', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', 'P', ' ', 'D', 'E', 'B'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'S', 'E', 'I', 'S', 'M', 'A', 'L', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'Y', 'O', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', 'J', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', 'I', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' '}
    };

    private static final char[][] TEST_MATRIX_3 = {
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'C', 'L', 'O', 'T', 'H', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'Q', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'F', 'O', 'U', 'N', 'D'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'H', 'E', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'I', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'K', 'A', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', 'E', 'N', 'I', 'S', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', 'F', 'O', 'R', 'E', 'M', 'A', 'N', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', 'E', 'W', ' ', 'E', ' ', ' ', 'G', ' ', 'W', 'E', 'B'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'N', ' ', ' ', 'S', 'L', 'A', 'Y', ' '},
        {' ', ' ', ' ', 'B', 'E', 'A', 'U', 'S', ' ', ' ', ' ', 'I', 'D', 'E', 'M'},
        {' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', 'Z', 'E', 'N', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'T', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
    };

    private static final char[][] TEST_MATRIX_4 = {
        {' ', ' ', 'G', 'R', 'O', 'P', 'E', ' ', 'O', 'F', ' ', 'W', 'A', 'P', ' '},
        {' ', 'X', 'U', ' ', ' ', ' ', 'T', 'H', 'R', 'I', 'V', 'E', 'N', ' ', ' '},
        {' ', ' ', 'M', 'A', 'Y', 'A', ' ', ' ', ' ', ' ', ' ', 'I', 'D', ' ', ' '},
        {' ', ' ', ' ', 'H', 'E', 'A', 'D', 'L', 'I', 'N', 'E', 'R', 'S', ' ', 'C'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', 'O'},
        {' ', ' ', 'F', 'U', 'G', 'U', ' ', 'V', ' ', ' ', ' ', 'B', 'O', 'G', 'S'},
        {' ', 'R', 'E', 'T', 'I', 'T', 'L', 'E', ' ', ' ', 'J', 'O', 'E', ' ', ' '},
        {' ', ' ', 'T', 'E', 'E', ' ', ' ', 'R', 'U', 'L', 'E', 'R', 'S', ' ', ' '},
        {' ', ' ', ' ', ' ', 'S', 'I', 'N', ' ', 'N', 'O', 'T', 'A', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'C', ' ', 'A', 'S', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', 'H', 'M', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'B', 'A', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', 'K', 'A', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'D', 'I', 'T', 'Z', 'I', 'E', 'R', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', 'W', 'E', 'D', ' ', ' ', ' ', 'D', 'E', 'A', 'T', 'H', 'Y', ' '}
    };

    private static final char[][] TEST_MATRIX_5 = {
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'M', 'I', 'N', 'O', 'R', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'M', 'U', 'S', 'I', 'C', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'I', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'S', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' '}
    };

    private static final char[][] TEST_MATRIX_6 = {
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'H', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'I', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'N', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'E', 'M', 'E', 'R', 'Y', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'P', 'A', 'N', 'E', 'E', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'A', 'X', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'C', ' ', ' ', ' ', ' ', 'A', 'U', 'T', 'O', ' ', ' ', ' ', ' ', ' ', ' '},
        {'T', ' ', ' ', 'W', 'A', 'N', 'T', 'O', 'N', 'S', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'O', ' ', 'D', ' ', ' ', ' ', 'C', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'R', ' ', 'S', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', 'D', ' ', ' ', ' ', ' ', 'S', 'W', 'U', 'N', 'G', ' ', ' '}
    };

    // Minimal board for extreme board dimensions.
    private static final char[][] MINIMAL_BOARD = {
        {'T', ' '},
        {'O', 'P'}
    };

    // --- Parameterised Providers ---
    
    /**
     * Provides valid board data and letter strings.
     */
    static Stream<Arguments> provideValidInputData() {
        return Stream.of(
//            Arguments.of(TEST_MATRIX_1, "SKQNDSA", 9, 10, 3, 11),
            Arguments.of(TEST_MATRIX_2, "SCRABBL"),
            Arguments.of(TEST_MATRIX_3, "SCRABBL"),
            Arguments.of(TEST_MATRIX_4, "SCRABBL"),
//            Arguments.of(TEST_MATRIX_5, "MINOR"),
            Arguments.of(TEST_MATRIX_6, "PANEEE"),
            Arguments.of(MINIMAL_BOARD, "MINI")
        );
    }

    /**
     * Provides board data for tests using an empty letters array.
     */
    static Stream<Arguments> provideEmptyLetters() {
        return Stream.of(
            Arguments.of(TEST_MATRIX_1, new char[0]),
            Arguments.of(TEST_MATRIX_2, new char[0])
        );
    }

    /**
     * Provides board data and letter strings exceeding the maximum allowed rack size.
     */
    static Stream<Arguments> provideLettersExceedingMax() {
        return Stream.of(
            Arguments.of(TEST_MATRIX_1, "ABCDEFGHI"),
            Arguments.of(TEST_MATRIX_3, "JKLMNOPQR")
        );
    }

    // --- Parameterised Tests using Soft Assertions ---
    @ParameterizedTest(name = "Valid input test (board variant #{index}, letters: \"{1}\")")
    @MethodSource("provideValidInputData")
    public void testValidInputConstructor(char[][] boardData, String lettersStr) {
        GameBoard board = new GameBoard(boardData);
        TileSet tileSet = new DefaultTileSet();
        WordFinder wordFinder = new WordFinder(board, new OxfordDictionary(), new DefaultScoringModule());
        char[] letters = lettersStr.toCharArray();

        GameEngine engine = new GameEngine(board, tileSet, wordFinder, letters);

        assertAll("Valid Input Constructor assertions",
                () -> assertNotNull(engine, "Engine instance should not be null"),
                () -> assertArrayEquals(letters, engine.getLetters(), "Letter rack should match the provided letters"),
                () -> {
                    char[][] engineMatrix = engine.getMatrix();
                    boolean hasEmpty = false;
                    for (char[] row : engineMatrix) {
                        for (char cell : row) {
                            if (cell == ' ') {
                                hasEmpty = true;
                                break;
                            }
                        }
                        if (hasEmpty) {
                            break;
                        }
                    }
                    assertTrue(hasEmpty, "Board must have at least one empty cell");
                }
        );
    }


    @ParameterizedTest(name = "Empty letters test (board variant #{index})")
    @MethodSource("provideEmptyLetters")
    public void testEmptyLettersArray(char[][] boardData, char[] emptyLetters) {
        GameBoard board = new GameBoard(boardData);
        TileSet tileSet = new DefaultTileSet();
        WordFinder wordFinder = new WordFinder(board, new OxfordDictionary(), new DefaultScoringModule());

        GameEngine engine = new GameEngine(board, tileSet, wordFinder, emptyLetters);

        assertAll("Empty Letters Array assertions",
                () -> assertNotNull(engine, "Engine instance should not be null"),
                () -> assertEquals(0, engine.getLetters().length, "Letter rack should be empty")
        );
    }

    @ParameterizedTest(name = "Oversized letters array test (board variant #{index}, letters: \"{1}\")")
    @MethodSource("provideLettersExceedingMax")
    public void testLettersArrayExceedingMaxSize(char[][] boardData, String longLetters) {
        GameBoard board = new GameBoard(boardData);
        TileSet tileSet = new DefaultTileSet();
        WordFinder wordFinder = new WordFinder(board, new OxfordDictionary(), new DefaultScoringModule());
        char[] letters = longLetters.toCharArray();

        GameEngine engine = new GameEngine(board, tileSet, wordFinder, letters);

        assertAll("Oversized Letters Array assertions",
                () -> assertNotNull(engine, "Engine instance should not be null"),
                () -> assertArrayEquals(letters, engine.getLetters(), "Letter rack should match the provided oversized array")
        );
    }

    // --- Abnormal (Null) Input Tests ---
    @Test
    @DisplayName("Null GameBoard throws NullPointerException")
    public void testNullGameBoard() {
        char[] letters = "SCRABBL".toCharArray();
        TileSet tileSet = new DefaultTileSet();
        WordFinder wordFinder = new WordFinder(new GameBoard(TEST_MATRIX_1), new OxfordDictionary(), new DefaultScoringModule());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new GameEngine(null, tileSet, wordFinder, letters));

        assertAll("Null GameBoard exception assertions",
                () -> assertTrue(exception.getMessage().contains("GameBoard cannot be null"),
                        "Exception message should mention 'GameBoard cannot be null'")
        );
    }

    @Test
    @DisplayName("Null TileSet throws NullPointerException")
    public void testNullTileSet() {
        char[] letters = "SCRABBL".toCharArray();
        GameBoard board = new GameBoard(TEST_MATRIX_1);
        WordFinder wordFinder = new WordFinder(board, new OxfordDictionary(), new DefaultScoringModule());

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new GameEngine(board, null, wordFinder, letters));

        assertAll("Null TileSet exception assertions",
                () -> assertTrue(exception.getMessage().contains("TileSet cannot be null"),
                        "Exception message should mention 'TileSet cannot be null'")
        );
    }

    @Test
    @DisplayName("Null WordFinder throws NullPointerException")
    public void testNullWordFinder() {
        char[] letters  = "SCRABBL".toCharArray();
        GameBoard board = new GameBoard(TEST_MATRIX_1);
        TileSet tileSet = new DefaultTileSet();

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new GameEngine(board, tileSet, null, letters));

        assertAll(
                "Null WordFinder exception assertions",
                () -> assertTrue(exception.getMessage().contains("WordFinder cannot be null"),
                        "Exception message should mention 'WordFinder cannot be null'")
        );
    }

    @Test
    @DisplayName("Null Letters Array throws NullPointerException")
    public void testNullLettersArray() {
        GameBoard board = new GameBoard(TEST_MATRIX_1);
        TileSet tileSet = new DefaultTileSet();
        WordFinder wordFinder = new WordFinder(board, new OxfordDictionary(), new DefaultScoringModule());

        var exception = assertThrows(
                RuntimeException.class,
                () -> new GameEngine(board, tileSet, wordFinder, null)
        );

        assertAll(
                "Null Letters Array exception assertions",
                () -> assertNotNull(exception, "Exception should not be null")
        );
    }

    
    @Test
    public void testInitialState() {
        
    }
    
    @Test
    public void testHighestSuggestion() {
        assertEquals(testEngine.getHighestSuggestion(), new Suggestion("KA", 26, 12, 4, false));
    }
    
    @Test
    public void testAcceptHighestSuggestion() {
        int tileCount = testEngine.getRemainingTileCount();
        var sugLength = testEngine.getHighestSuggestion().word().length();
        testEngine.acceptSuggestion(testEngine.getHighestSuggestion());
        int afterTileCount = testEngine.getRemainingTileCount();
        
        assertEquals(tileCount - sugLength, afterTileCount);
    }
    
    @Test
    public void testTileSetWildCardCount() {
        assertTrue(testEngine.getRemainingTileCount() < testEngine.getMaxTileCount());
    }
    
}