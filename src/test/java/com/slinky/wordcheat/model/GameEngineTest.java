package com.slinky.wordcheat.model;


import com.slinky.wordcheat.language.OxfordDictionary;
import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
/**
 *
 * @author Kheagen Haskins
 */
public class GameEngineTest {
    
    private GameEngine testEngine1;
    private GameBoard  testBoard1;
    private TileSet    testSet1;
    private MoveFinder testFinder1;
    
    private GameEngine testEngine2;
    private GameBoard  testBoard2;
    private TileSet    testSet2;
    private MoveFinder testFinder2;
    
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
    
     private static final char[][] TEST_MATRIX_2 = {
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  0
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  1
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //  2
            {' ', ' ', ' ', ' ', 'F', 'R', 'E', 'T', ' ', ' ', ' ', 'I', ' ', ' ', 'T'}, //  3
            {' ', ' ', ' ', ' ', ' ', ' ', 'M', 'O', 'F', 'O', ' ', 'N', ' ', ' ', 'I'}, //  4
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'V', ' ', 'D', ' ', 'N', 'E'}, //  5
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'G', ' ', 'A', ' ', 'E', ' ', 'O', ' '}, //  6
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'H', 'E', 'L', 'I', 'X', ' ', 'V', ' '}, //  7
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' ', 'D', 'E', 'B'}, //  8
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'S', 'E', 'I', 'S', 'M', 'A', 'L', ' '}, //  9
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', ' ', 'O', ' ', ' ', ' '}, // 10
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', 'J', 'E', 'E', 'R'}, // 11
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', 'I', ' ', ' ', ' '}, // 12
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'C', 'A', 'S', 'T', 'E', 'S', ' '}, // 13
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' '} // 14
          //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
    };

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }
    
    @BeforeEach
    public void setUp() {
        testBoard1 = new GameBoard(TEST_MATRIX_1);
        testBoard1.setWildCardPosition(9, 10);
        testBoard1.setWildCardPosition(3, 11);

        testBoard2 = new GameBoard(TEST_MATRIX_2);
        testBoard2.setWildCardPosition(6, 9);
        testBoard2.setWildCardPosition(9, 11);

        testSet1    = new DefaultTileSet();
        testSet2    = new DefaultTileSet();
        testFinder1 = new MoveFinder(testBoard1, new OxfordDictionary(), new DefaultScoringModule());
        testFinder2 = new MoveFinder(testBoard2, new OxfordDictionary(), new DefaultScoringModule());

        testEngine1 = new GameEngine(testSet1, testFinder1, new LetterRack("SKQNDSA".toCharArray()));
        testEngine2 = new GameEngine(testSet2, testFinder2, new LetterRack("TTEONNR".toCharArray()));
    }

    @Test
    public void testInitialState() {
        // TODO
    }
    
    @Test
    public void testHighestSuggestion() {
        assertEquals(new Move("KA",   26, 12, 4, false), testEngine1.getBestMove());
        assertEquals(new Move("NEON", 22,  2, 3, false), testEngine2.getBestMove());
    }
    
    @Test
    public void testAcceptHighestSuggestion() {
        int tileCount = testEngine1.getRemainingTileCount();
        var sugLength = testEngine1.getBestMove().word().length();
        testEngine1.acceptMove(testEngine1.getBestMove());
        int afterTileCount = testEngine1.getRemainingTileCount();
        
        assertEquals(tileCount - sugLength, afterTileCount);
    }
    
    @Test
    public void testTileSetWildCardCount() {
        assertTrue(testEngine1.getRemainingTileCount() < testEngine1.getMaxTileCount());
    }
    
}
//        // Create tile pool of equal points to extract from
//        int[] scoreCounts = new int[8];
//        for (char c = 'A'; c <= 'Z'; c++) {
//            int points = scoreMod.getPointsOf(c);
//            switch (points) {
//                case 1, 2, 3, 4, 5:
//                    scoreCounts[points] += tileSet.getTileMaxCount(c);
//                    break;
//                case 8:
//                    scoreCounts[6] += tileSet.getTileMaxCount(c);
//                    break;
//                case 10:
//                    scoreCounts[7] += tileSet.getTileMaxCount(c);
//                    break;
//                default:
//                    throw new RuntimeException("Unknow point value %d for %c".formatted(points, c));
//            }
//        }
//        
//        System.out.println(Arrays.toString(scoreCounts));