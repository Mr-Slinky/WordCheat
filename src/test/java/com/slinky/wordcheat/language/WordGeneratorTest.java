package com.slinky.wordcheat.language;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
        
/**
 *
 * @author Kheagen
 */
public class WordGeneratorTest {

    private Dictionary dictionary = new OxfordDictionary();
    
    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("CLOHODT", List.of("LO", "OLD", "HO", "LOCH", "DO", "LOOT", "LOCO", "HOOT", "COOT", "CLOT", "TOLD", "LOTH", "OD", "COOL", "OHO", "OH", "DOC", "COD", "THO", "HOD", "HOLD", "CHOLO", "DOTH", "OO", "DOL", "COL", "CLOD", "COO", "DOT", "TOD", "OOH", "COT", "CLOTH", "COLT", "COHO", "HOT", "LOO", "HOLT", "COOLTH", "LOT", "DOLT", "OOT", "TOO", "CLOOT", "COLD", "TO", "HOOD", "TOOL")),
                Arguments.of("OJITOAM", List.of("MAT", "TAM", "OAT", "TAO", "MOJO", "JO", "JIAO", "ATOM", "JATO", "JOTA", "MA", "OI", "MI", "AMI", "OM", "MOA", "OO", "MO", "MOOT", "AIM", "IO", "AI", "IT", "AIT", "AM", "MOO", "OMIT", "TA", "JOT", "IOTA", "MOAT", "AT", "MOT", "TOM", "TI", "TOO", "OOT", "JA", "JAM", "TO", "TAJ", "MOJITO", "TOOM")),
                Arguments.of("CA?", List.of("CAT", "SAC", "CAM", "CAR", "CAP", "CAN"))
        );
    }
    
    @ParameterizedTest
    @MethodSource("provideTestData")
    public void testGenerator(String letters, List<String> words) {
        WordGenerator testGenerator = new WordGenerator(dictionary, 2, 30);
        var genWords = testGenerator.generateAllWords(letters.toCharArray());
        assertAll(
            () -> {
                for (String word : words) {
                    assertTrue(genWords.contains(word),
                        "%s did not generate from letters %s".formatted(word, letters));
                }
            }
        );
    }

    @Test
    void testGenerateAllWords_withUnsortedLetters_LeavesInputUnchanged() {
        var generator = new WordGenerator(dictionary, 2, 7);
        var letters   = "TAC".toCharArray();

        generator.generateAllWords(letters);

        assertArrayEquals("TAC".toCharArray(), letters);
    }
}

//    @Test
//    public void doPerms() {
//        List<String> perms = new ArrayList<>();
//        MainUtil.generateAllPermutations("OJITOAM".toCharArray(), 7, perms);
//        Set<String> words = new HashSet<>();
//        StringBuilder prefix = new StringBuilder();
//        for (String perm : perms) {
//            prefix.setLength(0);
//            int len = perm.length();
//            for (int i = 0; i < len; i++) {
//                prefix.append(perm.charAt(i));
//                
//                if (dictionary.isWord(prefix.toString())) {
//                    words.add(prefix.toString());
//                }
//            }
//        }
//        for (String word : words) {
//            System.out.print("\"%s\", ".formatted(word));
//        }
//        System.out.println("");
//    }
//    