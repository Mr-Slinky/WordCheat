package com.slinky.wordcheat.model;

import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
/**
 * Unit tests for the LetterRack constructor.
 *
 * <p>
 * This test class covers both valid and invalid scenarios for the LetterRack
 * constructor. Valid tests are implemented as parameterised tests, while the
 * invalid input test is implemented as a standard test.
 * </p>
 */
public class LetterRackTest {

    /**
     * Provides valid test cases for the LetterRack constructor. Each test case
     * includes:
     * <ul>
     *   <li>An input char array (or null).</li>
     *   <li>The expected rack size.</li>
     *   <li>The expected content as a String.</li>
     * </ul>
     *
     * @return a stream of arguments for valid constructor tests.
     */
    static Stream<Arguments> validConstructorProvider() {
        return Stream.of(
            // Test with null input (should create an empty rack)
            Arguments.of(null, 0, ""),
            // Test with an empty array
            Arguments.of(new char[]{}, 0, ""),
            // Test with a single valid letter
            Arguments.of(new char[]{'A'}, 1, "A"),
            // Test with multiple valid letters (alphabetical order)
            Arguments.of(new char[]{'A', 'B', 'C'}, 3, "ABC"),
            // Test with maximum allowed size (21 letters) including a wildcard
            Arguments.of(
                new char[]{
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                    '?', // wildcard included as per design
                    'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U'
                },
                21,
                "ABCDEFGHIJK?MNOPQRSTU"
            ),
            // Additional variety test cases:
            // Non-alphabetical order
            Arguments.of(new char[]{'D','A','C','B'}, 4, "DACB"),
            // Duplicate letters
            Arguments.of(new char[]{'M','M','N'}, 3, "MMN"),
            // Mix of wildcards and letters in random positions
            Arguments.of(new char[]{'?', 'A', '?', 'B'}, 4, "?A?B"),
            // Reverse alphabetical order
            Arguments.of(new char[]{'Z','Y','X','W'}, 4, "ZYXW"),
            // Mixed-case letters
            Arguments.of(new char[]{'a','Z','m'}, 3, "AZM")
        );
    }

    /**
     * Tests the constructor with valid inputs.
     *
     * @param input           the input character array (or null)
     * @param expectedSize    the expected number of letters in the rack
     * @param expectedContent the expected content of the rack as a String
     */
    @ParameterizedTest(name = "Valid Constructor Test - Input: {0}")
    @MethodSource("validConstructorProvider")
    @DisplayName("Test valid inputs for LetterRack constructor")
    void testValidConstructor(char[] input, int expectedSize, String expectedContent) {
        LetterRack rack = new LetterRack(input);
        assertAll(
            () -> assertEquals(expectedSize,    rack.getSize(),  "The size of the rack should match the expected value."),
            () -> assertEquals(expectedContent, rack.toString(), "The content of the rack should match the expected letters.")
        );
    }

    /**
     * Tests the constructor with an invalid input that exceeds the maximum allowed size.
     * This test ensures that an IllegalArgumentException is thrown.
     */
    @Test
    @DisplayName("Test invalid input for LetterRack constructor (exceeding maximum allowed size)")
    void testInvalidConstructor() {
        // Create an array of 22 letters (exceeds MAX_SIZE of 21)
        char[] invalidInput = new char[22];
        for (int i = 0; i < invalidInput.length; i++) {
            invalidInput[i] = 'A';
        }

        assertThrows(
            IllegalArgumentException.class,
            () -> new LetterRack(invalidInput),
            "An IllegalArgumentException should be thrown when input exceeds the maximum allowed size."
        );
    }
    
    
    /**
     * Provides valid test cases for verifying getLetters.
     * Each test case includes:
     * <ul>
     *   <li>An input char array (or null).</li>
     *   <li>The expected uppercase string result.</li>
     * </ul>
     *
     * @return a stream of arguments for getLetters tests.
     */
    static Stream<Arguments> validLettersProvider() {
        return Stream.of(
            // Null input should yield an empty result.
            Arguments.of(null, ""),
            // Empty array.
            Arguments.of(new char[]{}, ""),
            // Single letter (lowercase input converted to uppercase).
            Arguments.of(new char[]{'a'}, "A"),
            // Mixed-case letters.
            Arguments.of(new char[]{'a', 'B', 'c'}, "ABC"),
            // Wildcard and letters.
            Arguments.of(new char[]{'z', '?', 'y'}, "Z?Y"),
            // Duplicate letters with mixed cases.
            Arguments.of(new char[]{'m','M','n'}, "MMN")
        );
    }

    /**
     * Test that getLetters returns the valid output for the given input.
     *
     * @param input the input char array (or null)
     * @param expected the expected uppercase string representation.
     */
    @ParameterizedTest(name = "Valid getLetters Test - Input: {0}")
    @MethodSource("validLettersProvider")
    @DisplayName("Test getLetters returns valid output")
    void testGetLettersValid(char[] input, String expected) {
        LetterRack rack = new LetterRack(input);
        String result   = new String(rack.getLetters());
        assertEquals(expected, result, "The returned letters should match the expected uppercase string.");
    }

    /**
     * Test that getLetters always returns letters in uppercase.
     * For each letter that is alphabetic, we assert it is uppercase.
     *
     * @param input the input char array (or null)
     * @param expected the expected uppercase string representation.
     */
    @ParameterizedTest(name = "Uppercase Test for getLetters - Input: {0}")
    @MethodSource("validLettersProvider")
    @DisplayName("Test getLetters returns only uppercase letters")
    void testGetLettersAreUppercase(char[] input, String expected) {
        LetterRack rack = new LetterRack(input);
        char[] letters  = rack.getLetters();
        for (char c : letters) {
            // For alphabetical characters, ensure they are uppercase.
            if (Character.isLetter(c)) {
                assertTrue(Character.isUpperCase(c),
                    "Expected letter '" + c + "' to be uppercase.");
            }
        }
    }

    /**
     * Test that the array returned by getLetters is a defensive copy. Modifying
     * the returned array should not affect the internal state of the
     * LetterRack.
     */
    @Test
    @DisplayName("Test getLetters returns a defensive copy")
    void testGetLettersDefensiveCopy() {
        // Create a LetterRack with known input.
        char[] input    = new char[]{'A', 'B', 'C'};
        LetterRack rack = new LetterRack(input);
        // Retrieve a copy of the letters.
        char[] lettersCopy = rack.getLetters();
        // Modify the returned array.
        lettersCopy[0] = 'Z';
        // Get the letters again.
        String resultAfterModification = new String(rack.getLetters());
        // It should still reflect the original state.
        assertEquals(
                "ABC", resultAfterModification,
                "Modifying the returned array should not affect the internal state of the LetterRack."
        );
    }
    
    
       // VT01: Valid lower-case letters – ensure they are converted to uppercase.
    static Stream<Arguments> validLowerCaseProvider() {
        return Stream.of(
            Arguments.of('a', 'A'),
            Arguments.of('m', 'M'),
            Arguments.of('z', 'Z'),
            Arguments.of('e', 'E')
        );
    }
    
    @ParameterizedTest(name = "Valid Lower-Case Test: {0} -> expected: {1}")
    @MethodSource("validLowerCaseProvider")
    @DisplayName("Test addLetter converts lower-case letters to uppercase")
    void testAddLetterLowerCase(char input, char expected) {
        LetterRack rack = new LetterRack();
        rack.addLetter(input);
        char[] result = rack.getLetters();
        assertAll(
            () -> assertEquals(1, result.length, "Rack size should be 1 after adding one letter."),
            () -> assertEquals(expected, result[0], "The letter should be converted to uppercase.")
        );
    }

    // VT02: Valid uppercase letters – they should be accepted as-is.
    static Stream<Arguments> validUpperCaseProvider() {
        return Stream.of(
            Arguments.of('A', 'A'),
            Arguments.of('M', 'M'),
            Arguments.of('Z', 'Z'),
            Arguments.of('E', 'E')
        );
    }
    
    @ParameterizedTest(name = "Valid Upper-Case Test: {0} -> expected: {1}")
    @MethodSource("validUpperCaseProvider")
    @DisplayName("Test addLetter accepts uppercase letters without change")
    void testAddLetterUpperCase(char input, char expected) {
        LetterRack rack = new LetterRack();
        rack.addLetter(input);
        char[] result = rack.getLetters();
        assertAll(
            () -> assertEquals(1, result.length,    "Rack size should be 1 after adding one letter."),
            () -> assertEquals(expected, result[0], "The letter should remain uppercase.")
        );
    }
    
    // IT01: Invalid characters – test various invalid inputs, including edge cases.
    static Stream<Arguments> invalidLetterProvider() {
        return Stream.of(
            Arguments.of('0'),
            Arguments.of('!'),
            Arguments.of('@'),
            Arguments.of('['), // Character just after 'Z'
            Arguments.of('`'), // Character just before 'a'
            Arguments.of('{'), // Character just after 'z'
            Arguments.of(' '), // Space is invalid
            Arguments.of(';')  // Punctuation
        );
    }
    
    @ParameterizedTest(name = "Invalid Letter Test: {0}")
    @MethodSource("invalidLetterProvider")
    @DisplayName("Test addLetter throws IllegalArgumentException for invalid characters")
    void testAddLetterInvalid(char input) {
        LetterRack rack = new LetterRack();
        assertThrows(
                IllegalArgumentException.class,
                () -> rack.addLetter(input),
                "Adding an invalid character should throw IllegalArgumentException."
        );
    }
    
    // IT02: Adding a letter when the rack is full – test with a valid and an invalid letter.
    static Stream<Arguments> fullRackLetterProvider() {
        return Stream.of(
            Arguments.of('A'), // valid but should fail because rack is full
            Arguments.of('1')  // invalid, but full rack check comes first
        );
    }
    
    @ParameterizedTest(name = "Full Rack Test with input: {0}")
    @MethodSource("fullRackLetterProvider")
    @DisplayName("Test addLetter throws IllegalStateException when rack is full")
    void testAddLetterWhenFull(char input) {
        LetterRack rack = new LetterRack();
        // Pre-fill the rack to its maximum capacity.
        for (int i = 0; i < LetterRack.MAX_SIZE; i++) {
            rack.addLetter('A');
        }
        
        // Attempting to add any letter (valid or invalid) should now throw an exception.
        assertThrows(
                IllegalStateException.class,
                () -> rack.addLetter(input),
                "Adding a letter to a full rack should throw IllegalStateException."
        );
    }
    
     /**
     * Provides test cases for removeLetter(char letter).
     * Each argument includes:
     * <ul>
     *   <li>initial content as a String (to be converted to a char array)</li>
     *   <li>the letter to remove</li>
     *   <li>the expected boolean result</li>
     *   <li>the expected final content as a String after removal</li>
     * </ul>
     */
    static Stream<Arguments> removeLetterProvider() {
        return Stream.of(
            // RL01: Standard removal – remove letter 'B' from "ABC"
            Arguments.of("ABC", 'B', true, "AC"),
            // RL02: Removal where letter appears multiple times – remove first 'A' from "AAB"
            Arguments.of("AAB", 'A', true, "AB"),
            // RL03: Removal of non-existent letter – try to remove 'Z' from "ABC"
            Arguments.of("ABC", 'Z', false, "ABC"),
            // RL04: Removal from an empty rack – attempt removal from ""
            Arguments.of("", 'A', false, "")
        );
    }

    @ParameterizedTest(name = "removeLetter: initial=\"{0}\", remove='{1}'")
    @MethodSource("removeLetterProvider")
    @DisplayName("Test removeLetter(char) with various cases")
    void testRemoveLetter(String initialContent, char letterToRemove, boolean expectedReturn, String expectedFinalContent) {
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        boolean result = rack.removeLetter(letterToRemove);
        // Use soft assertions to check both the boolean result and the final state.
        assertAll(
            () -> assertEquals(expectedReturn, result, "The returned boolean should match expected."),
            () -> assertEquals(expectedFinalContent, rack.toString(), "The final rack content should match expected.")
        );
    }
    
    /**
     * Provides test cases for removeLetterAt tests.
     * Each argument includes:
     * <ul>
     *   <li>the size of the rack (0 to 21)</li>
     *   <li>the expected initial content as a String – the first 'n' letters of the alphabet</li>
     * </ul>
     */
    static Stream<Arguments> removeLetterAtSizeProvider() {
        return IntStream.rangeClosed(0, LetterRack.MAX_SIZE)
                .mapToObj(size -> {
                    // Build initial content as the first 'size' letters starting from 'A'
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        sb.append((char) ('A' + i));
                    }
                    
                    return Arguments.of(size, sb.toString());
                });
    }

    /**
     * RA03: Test valid removal at the last index.
     * For racks with size > 0, remove the letter at index (size - 1) and check:
     * <ul>
     *   <li>The returned letter equals the last letter of the initial content.</li>
     *   <li>The final content equals the initial content without the last letter.</li>
     * </ul>
     */
    @ParameterizedTest(name = "Valid removeLetterAt: size={0}, initial=\"{1}\"")
    @MethodSource("removeLetterAtSizeProvider")
    @DisplayName("Test removeLetterAt(int) valid removal at last index")
    void testRemoveLetterAtValid(int size, String initialContent) {
        // This test applies only if the rack is non-empty.
        assumeTrue(size > 0, "Skipping valid removal test for empty rack.");
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        int validIndex  = size - 1;
        char expectedRemoved = initialContent.charAt(validIndex);
        String expectedFinalContent = initialContent.substring(0, validIndex);

        char removed = rack.removeLetterAt(validIndex);
        assertAll(
            () -> assertEquals(expectedRemoved, removed, "Removed letter should match expected."),
            () -> assertEquals(expectedFinalContent, rack.toString(), "Final rack content should be as expected.")
        );
    }

    /**
     * RA05: Test invalid removal at index equal to the current size.
     * For any rack (including empty), attempting to remove at index 'size' should throw an exception.
     */
    @ParameterizedTest(name = "Invalid removeLetterAt: size={0}, initial=\"{1}\", index={0}")
    @MethodSource("removeLetterAtSizeProvider")
    @DisplayName("Test removeLetterAt(int) throws exception for index equal to size")
    void testRemoveLetterAtInvalid(int size, String initialContent) {
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        int invalidIndex = size; // Always invalid since valid indices are 0 to size-1.
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> rack.removeLetterAt(invalidIndex),
                "Removing at index equal to size should throw IndexOutOfBoundsException."
        );
    }
    
        // ALV01: Test with null input (should be a no-op)
    @Test
    @DisplayName("ALV01: addLetters(null) results in no change to the rack")
    void testAddLettersNullInput() {
        LetterRack rack = new LetterRack();
        int initialSize = rack.getSize();
        rack.addLetters((char[]) null);
        assertEquals(initialSize, rack.getSize(), "Rack size should remain unchanged after null input.");
    }

    // ALV02: Test with an empty array (should be a no-op)
    @Test
    @DisplayName("ALV02: addLetters(empty array) results in no change to the rack")
    void testAddLettersEmptyArray() {
        LetterRack rack = new LetterRack();
        int initialSize = rack.getSize();
        rack.addLetters(new char[]{});
        assertEquals(initialSize, rack.getSize(), "Rack size should remain unchanged after adding an empty array.");
    }

    // ALV03: Valid additions within capacity.
    // Each argument includes: input char array and expected resulting uppercase string.
    static Stream<Arguments> validAddLettersProvider() {
        return Stream.of(
            Arguments.of(new char[]{'a', 'b', 'c'}, "ABC"),
            Arguments.of(new char[]{'d', '?', 'F'}, "D?F"),
            Arguments.of(new char[]{'m'}, "M")
        );
    }
    
    @ParameterizedTest(name = "ALV03: Adding letters {0} produces \"{1}\"")
    @MethodSource("validAddLettersProvider")
    @DisplayName("ALV03: Valid addLetters (varargs) within capacity")
    void testAddLettersValid(char[] lettersToAdd, String expectedResult) {
        LetterRack rack = new LetterRack();
        rack.addLetters(lettersToAdd);
        assertAll(
            () -> assertEquals(expectedResult.length(), rack.getSize(),
                    "Rack size should equal the number of added letters."),
            () -> assertEquals(expectedResult, rack.toString(),
                    "Rack content should match expected uppercase result.")
        );
    }

    // ALV04: Valid additions that exactly fill the remaining capacity.
    // Each argument includes: initial content (String), additional letters (char[]), and expected final result (String).
    static Stream<Arguments> addLettersFillCapacityProvider() {
        return Stream.of(
            // Case 1: Initial rack empty, add exactly MAX_SIZE letters.
            Arguments.of(
                "", 
                "abcdefghijklmnopqrstu".toCharArray(), // 21 letters (will convert to uppercase)
                "ABCDEFGHIJKLMNOPQRSTU" // expected final result
            ),
            // Case 2: Rack with non-empty initial content.
            // Initial: "TEST" (4 letters) then add 17 letters (to reach capacity 21).
            Arguments.of(
                "TEST",
                new char[]{'a','b','c','?','Z','y','x','w','v','u','t','s','r','q','p','o','n'},
                "TESTABC?ZYXWVUTSRQPON"
            )
        );
    }
    
    @ParameterizedTest(name = "ALV04: With initial \"{0}\" and additional letters {1}, final rack should be \"{2}\"")
    @MethodSource("addLettersFillCapacityProvider")
    @DisplayName("ALV04: Valid addLetters (varargs) exactly filling remaining capacity")
    void testAddLettersFillCapacity(String initialContent, char[] lettersToAdd, String expectedFinalContent) {
        // Pre-load the rack with the initial content.
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        int initialSize = rack.getSize();
        // Ensure that the additional letters exactly fill the remaining capacity.
        assertEquals(LetterRack.MAX_SIZE - initialSize, lettersToAdd.length,
                     "Additional letters length should exactly fill rack capacity.");
        rack.addLetters(lettersToAdd);
        assertAll(
            () -> assertEquals(expectedFinalContent, rack.toString(),
                    "Final rack content should match expected uppercase result."),
            () -> assertEquals(LetterRack.MAX_SIZE, rack.getSize(),
                    "Final rack size should equal MAX_SIZE.")
        );
    }
    
    // ALV05: Attempt to add letters that exceed available capacity.
    // Each argument includes: initial content (String) and additional letters (char[]) that make capacity exceeded.
    static Stream<Arguments> addLettersExceedCapacityProvider() {
        return Stream.of(
            // Case 1: Empty initial rack, adding 22 letters (MAX_SIZE is 21).
            Arguments.of(
                "", 
                "abcdefghijklmnopqrstuv".toCharArray() // 22 letters
            ),
            // Case 2: Rack with initial content "TEST" (4 letters), adding 18 letters to exceed capacity (4+18=22).
            Arguments.of(
                "TEST",
                new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r'} // 18 letters
            )
        );
    }
    
    @ParameterizedTest(name = "ALV05: With initial \"{0}\" and additional letters {1} should exceed capacity")
    @MethodSource("addLettersExceedCapacityProvider")
    @DisplayName("ALV05: addLetters (varargs) exceeding capacity throws IllegalStateException")
    void testAddLettersExceedCapacity(String initialContent, char[] lettersToAdd) {
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        assertThrows(IllegalStateException.class,
                     () -> rack.addLetters(lettersToAdd),
                     "Adding letters that exceed rack capacity should throw IllegalStateException.");
    }
    
    // ASL01: Test with null input (should be a no-op)
    @Test
    @DisplayName("ASL01: addLetters(String null) results in no change to the rack")
    void testAddLettersStringNullInput() {
        LetterRack rack = new LetterRack();
        int initialSize = rack.getSize();
        rack.addLetters((String) null);
        assertEquals(initialSize, rack.getSize(), "Rack size should remain unchanged after null input.");
    }

    // ASL02: Test with an empty string (should be a no-op)
    @Test
    @DisplayName("ASL02: addLetters(\"\") results in no change to the rack")
    void testAddLettersEmptyString() {
        LetterRack rack = new LetterRack();
        int initialSize = rack.getSize();
        rack.addLetters("");
        assertEquals(initialSize, rack.getSize(), "Rack size should remain unchanged after adding an empty string.");
    }

    // ASL03: Valid additions within capacity.
    // Each argument includes: input string and expected resulting uppercase string.
    static Stream<Arguments> validAddLettersStringProvider() {
        return Stream.of(
            Arguments.of("abc", "ABC"),
            Arguments.of("d?F", "D?F"),
            Arguments.of("m", "M")
        );
    }
    
    @ParameterizedTest(name = "ASL03: Adding letters \"{0}\" produces \"{1}\"")
    @MethodSource("validAddLettersStringProvider")
    @DisplayName("ASL03: Valid addLetters(String) within capacity")
    void testAddLettersStringValid(String lettersToAdd, String expectedResult) {
        LetterRack rack = new LetterRack();
        rack.addLetters(lettersToAdd);
        assertAll(
            () -> assertEquals(expectedResult.length(), rack.getSize(), 
                    "Rack size should equal the length of the added string."),
            () -> assertEquals(expectedResult, rack.toString(), 
                    "Rack content should match the expected uppercase result.")
        );
    }

    // ASL04: Valid additions that exactly fill the remaining capacity.
    // Each argument includes: initial content (String), additional letters (String), and expected final result (String).
    static Stream<Arguments> addLettersStringFillCapacityProvider() {
        return Stream.of(
            // Case 1: Empty initial rack; add exactly MAX_SIZE letters.
            Arguments.of(
                "", 
                "abcdefghijklmnopqrstu", // 21 letters (will be converted to uppercase)
                "ABCDEFGHIJKLMNOPQRSTU"
            ),
            // Case 2: Initial "TEST" (4 letters); add string of length 17 to reach MAX_SIZE.
            Arguments.of(
                "TEST",
                "abc?zyxwvutsrqpon", // 17 letters
                "TESTABC?ZYXWVUTSRQPON"
            )
        );
    }
    
    @ParameterizedTest(name = "ASL04: With initial \"{0}\" and additional letters \"{1}\", final rack should be \"{2}\"")
    @MethodSource("addLettersStringFillCapacityProvider")
    @DisplayName("ASL04: Valid addLetters(String) exactly filling remaining capacity")
    void testAddLettersStringFillCapacity(String initialContent, String lettersToAdd, String expectedFinalContent) {
        // Pre-load the rack with the initial content.
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        int initialSize = rack.getSize();
        // Verify that the additional string exactly fills available capacity.
        assertEquals(LetterRack.MAX_SIZE - initialSize, lettersToAdd.length(),
                     "Additional string length must exactly fill rack capacity.");
        rack.addLetters(lettersToAdd);
        assertAll(
            () -> assertEquals(expectedFinalContent, rack.toString(), 
                    "Final rack content should match expected uppercase result."),
            () -> assertEquals(LetterRack.MAX_SIZE, rack.getSize(),
                    "Final rack size should equal MAX_SIZE.")
        );
    }
    
    // ASL05: Attempt to add letters that exceed available capacity.
    // Each argument includes: initial content (String) and additional letters (String) that cause capacity exceedance.
    static Stream<Arguments> addLettersStringExceedCapacityProvider() {
        return Stream.of(
            // Case 1: Empty initial rack; adding 22 letters (exceeding MAX_SIZE of 21).
            Arguments.of(
                "", 
                "abcdefghijklmnopqrstuv" // 22 letters
            ),
            // Case 2: Initial "TEST" (4 letters); adding 18 letters to exceed capacity (4+18=22).
            Arguments.of(
                "TEST",
                "abcdefghijklmnopqr" // 18 letters
            )
        );
    }
    
    @ParameterizedTest(name = "ASL05: With initial \"{0}\" and additional letters \"{1}\" should exceed capacity")
    @MethodSource("addLettersStringExceedCapacityProvider")
    @DisplayName("ASL05: addLetters(String) exceeding capacity throws IllegalStateException")
    void testAddLettersStringExceedCapacity(String initialContent, String lettersToAdd) {
        LetterRack rack = new LetterRack(initialContent.toCharArray());
        assertThrows(IllegalStateException.class,
                     () -> rack.addLetters(lettersToAdd),
                     "Adding letters that exceed rack capacity should throw IllegalStateException.");
    }
    
    // SA01: Already sorted input.
    @Test
    @DisplayName("SA01: Already sorted input remains unchanged")
    void testSortAllAlreadySorted() {
        LetterRack rack = new LetterRack("ABC".toCharArray());
        rack.sortAll();
        assertEquals("ABC", rack.toString(), "Sorted rack should remain 'ABC'.");
    }

    // SA02: Reverse sorted input.
    @Test
    @DisplayName("SA02: Reverse sorted input is sorted to natural order")
    void testSortAllReverseSorted() {
        LetterRack rack = new LetterRack("CBA".toCharArray());
        rack.sortAll();
        assertEquals("ABC", rack.toString(), "Sorted rack should be 'ABC' after sorting reverse-ordered input.");
    }

    // SA03: Unsorted input with duplicates.
    @Test
    @DisplayName("SA03: Unsorted input with duplicates sorts correctly")
    void testSortAllUnsortedWithDuplicates() {
        LetterRack rack = new LetterRack("BCAAB".toCharArray());
        rack.sortAll();
        assertEquals("AABBC", rack.toString(), "Sorted rack should be 'AABBC'.");
    }

    // SA04: Input containing wildcards.
    @Test
    @DisplayName("SA04: Input with wildcards sorts naturally, placing wildcards before letters")
    void testSortAllWithWildcards() {
        // Note: The wildcard '?' has an ASCII value of 63 while 'A' = 65.
        LetterRack rack = new LetterRack("B?A".toCharArray());
        rack.sortAll();
        assertEquals("?AB", rack.toString(), "Sorted rack should be '?AB' because '?' comes before 'A'.");
    }

    // SA05: Single element input.
    @Test
    @DisplayName("SA05: Single element input remains unchanged after sorting")
    void testSortAllSingleElement() {
        LetterRack rack = new LetterRack("X".toCharArray());
        rack.sortAll();
        assertEquals("X", rack.toString(), "Sorting a single element should not change it.");
    }

    // SA06: Empty rack.
    @Test
    @DisplayName("SA06: Empty rack remains empty after sorting")
    void testSortAllEmptyRack() {
        LetterRack rack = new LetterRack(new char[] {});
        rack.sortAll();
        assertEquals("", rack.toString(), "An empty rack should remain empty after sorting.");
    }
    
     // SC01: Natural order comparator.
    @Test
    @DisplayName("SC01: Sorting using natural order comparator produces sorted order")
    void testSortWithNaturalOrderComparator() {
        LetterRack rack = new LetterRack("CBA".toCharArray());
        Comparator<Character> naturalComparator = (a, b) -> a.compareTo(b);
        rack.sort(naturalComparator);
        assertEquals("ABC", rack.toString(), "Rack sorted with natural comparator should be 'ABC'.");
    }

    // SC02: Reverse order comparator.
    @Test
    @DisplayName("SC02: Sorting using reverse order comparator produces descending order")
    void testSortWithReverseOrderComparator() {
        LetterRack rack = new LetterRack("ABC".toCharArray());
        Comparator<Character> reverseComparator = (a, b) -> b.compareTo(a);
        rack.sort(reverseComparator);
        assertEquals("CBA", rack.toString(), "Rack sorted with reverse comparator should be 'CBA'.");
    }

    // SC03: Custom comparator that pushes wildcards to the end.
    @Test
    @DisplayName("SC03: Custom comparator pushing wildcards to the end sorts correctly")
    void testSortWithWildcardToEndComparator() {
        LetterRack rack = new LetterRack("A?B".toCharArray());
        Comparator<Character> customComparator = (a, b) -> {
            if (a == '?' && b != '?') {
                return 1;
            }
            if (b == '?' && a != '?') {
                return -1;
            }
            return a.compareTo(b);
        };
        rack.sort(customComparator);
        assertEquals("AB?", rack.toString(), "Rack sorted with custom comparator should be 'AB?'.");
    }

    // SC04: Single element input using any comparator.
    @Test
    @DisplayName("SC04: Single element input remains unchanged using comparator-based sort")
    void testSortWithComparatorSingleElement() {
        LetterRack rack = new LetterRack("Z".toCharArray());
        Comparator<Character> anyComparator = (a, b) -> a.compareTo(b);
        rack.sort(anyComparator);
        assertEquals("Z", rack.toString(), "A single element rack should remain unchanged.");
    }

    // SC05: Empty rack using any comparator.
    @Test
    @DisplayName("SC05: Empty rack remains empty using comparator-based sort")
    void testSortWithComparatorEmptyRack() {
        LetterRack rack = new LetterRack(new char[]{});
        Comparator<Character> anyComparator = (a, b) -> a.compareTo(b);
        rack.sort(anyComparator);
        assertEquals("", rack.toString(), "An empty rack should remain empty after sorting.");
    }
    
    // CC01: Null Input – isolated test.
    @Test
    @DisplayName("CC01: Null input word returns false")
    void testCanConstructNullInput() {
        LetterRack rack = new LetterRack("ABC".toCharArray());
        assertFalse(rack.canConstruct((CharSequence) null), "Null input (CharSequence) should return false.");
        assertFalse(rack.canConstruct((char[]) null), "Null input (char[]) should return false.");
    }

    // CC02: Empty Word.
    // For any given rack state, an empty word should be considered constructible.
    static Stream<Arguments> emptyWordProvider() {
        // Each argument: rack state, empty word ("")
        return Stream.of(
            Arguments.of("", ""),
            Arguments.of("A", ""),
            Arguments.of("ABC", ""),
            Arguments.of("XYZ", ""),
            Arguments.of("A?B", "")
        );
    }

    @ParameterizedTest(name = "CC02: For rack \"{0}\" and empty word, expected true")
    @MethodSource("emptyWordProvider")
    @DisplayName("CC02: Empty word is always constructible")
    void testEmptyWord(String rackState, String word) {
        LetterRack rack         = new LetterRack(rackState.toCharArray());
        boolean resultCharSeq   = rack.canConstruct(word);
        boolean resultCharArray = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertTrue(resultCharSeq, "Empty word should be constructible (CharSequence)."),
            () -> assertTrue(resultCharArray, "Empty word should be constructible (char[] overload).")
        );
    }

    // CC03: Exact Match.
    // The word exactly equals the rack content.
    static Stream<Arguments> exactMatchProvider() {
        return Stream.of(
            Arguments.of("ABC", "ABC"),
            Arguments.of("TEST", "TEST"),
            Arguments.of("A?B", "A?B"),
            Arguments.of("XYZ", "XYZ")
        );
    }

    @ParameterizedTest(name = "CC03: For rack \"{0}\" and exact match word \"{1}\", expected true")
    @MethodSource("exactMatchProvider")
    @DisplayName("CC03: Exact match word is constructible")
    void testExactMatch(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertTrue(result1, "Exact match (CharSequence) should be constructible."),
            () -> assertTrue(result2, "Exact match (char[] overload) should be constructible.")
        );
    }

    // CC04: Different Order.
    // A permutation of the rack letters should be constructible.
    static Stream<Arguments> differentOrderProvider() {
        return Stream.of(
            // For rack "ABC", jumbled word "CAB" is constructible.
            Arguments.of("ABC", "CAB"),
            // For rack "TEST", jumbled word "TETS" is constructible.
            Arguments.of("TEST", "TETS"),
            // For rack "XYZ", jumbled word "YZX" is constructible.
            Arguments.of("XYZ", "YZX"),
            // For rack "A?B", jumbled word "BA?" is constructible.
            Arguments.of("A?B", "BA?")
        );
    }

    @ParameterizedTest(name = "CC04: For rack \"{0}\" and jumbled word \"{1}\", expected true")
    @MethodSource("differentOrderProvider")
    @DisplayName("CC04: Permutations of rack letters are constructible")
    void testDifferentOrder(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertTrue(result1, "Jumbled word should be constructible (CharSequence)."),
            () -> assertTrue(result2, "Jumbled word should be constructible (char[] overload).")
        );
    }

    // CC05: Missing Letter.
    // The word contains a letter that is not in the rack; expected false.
    static Stream<Arguments> missingLetterProvider() {
        return Stream.of(
            // For rack "ABC", word "ABD" is not constructible (missing 'D').
            Arguments.of("ABC", "ABD"),
            // For rack "TEST", word "TESU" is not constructible (missing 'U').
            Arguments.of("TEST", "TESU"),
            // For rack "XYZ", word "XYW" is not constructible (missing 'W').
            Arguments.of("XYZ", "XYW"),
            // For rack "AB", word "ABC" (missing 'C').
            Arguments.of("AB", "ABC")
        );
    }

    @ParameterizedTest(name = "CC05: For rack \"{0}\" and word \"{1}\" (with missing letter), expected false")
    @MethodSource("missingLetterProvider")
    @DisplayName("CC05: Word with a missing letter is not constructible")
    void testMissingLetter(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertFalse(result1, "Word with missing letter should not be constructible (CharSequence)."),
            () -> assertFalse(result2, "Word with missing letter should not be constructible (char[] overload).")
        );
    }

    // CC06: Duplicate Letters – Sufficient.
    // The rack contains enough duplicates to cover the word's requirements.
    static Stream<Arguments> duplicateSufficientProvider() {
        return Stream.of(
            // For rack "AABC", word "AA" is constructible.
            Arguments.of("AABC", "AA"),
            // For rack "BBBA", word "BBB" is constructible.
            Arguments.of("BBBA", "BBB"),
            // For rack "TESTT", word "TT" is constructible.
            Arguments.of("TESTT", "TT")
        );
    }

    @ParameterizedTest(name = "CC06: For rack \"{0}\" and word \"{1}\" with sufficient duplicates, expected true")
    @MethodSource("duplicateSufficientProvider")
    @DisplayName("CC06: Sufficient duplicates yield a constructible word")
    void testDuplicateSufficient(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertTrue(result1, "Word with sufficient duplicates should be constructible (CharSequence)."),
            () -> assertTrue(result2, "Word with sufficient duplicates should be constructible (char[] overload).")
        );
    }

    // CC07: Duplicate Letters – Insufficient.
    // The rack does not contain enough duplicates for the word.
    static Stream<Arguments> duplicateInsufficientProvider() {
        return Stream.of(
            // For rack "AABC", word "AAA" is not constructible.
            Arguments.of("AABC", "AAA"),
            // For rack "BBBA", word "BBBB" is not constructible.
            Arguments.of("BBBA", "BBBB")
        );
    }

    @ParameterizedTest(name = "CC07: For rack \"{0}\" and word \"{1}\" with insufficient duplicates, expected false")
    @MethodSource("duplicateInsufficientProvider")
    @DisplayName("CC07: Insufficient duplicates yield a non-constructible word")
    void testDuplicateInsufficient(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertFalse(result1, "Word with insufficient duplicates should not be constructible (CharSequence)."),
            () -> assertFalse(result2, "Word with insufficient duplicates should not be constructible (char[] overload).")
        );
    }

    // CC08: Wildcard Usage.
    // The wildcard in the rack should cover for a missing letter.
    static Stream<Arguments> wildcardUsageProvider() {
        return Stream.of(
            // For rack "AB?" and word "ABC", '?' covers 'C'.
            Arguments.of("AB?", "ABC"),
            // For rack "A?C" and word "ABC", '?' covers 'B'.
            Arguments.of("A?C", "ABC"),
            // For rack "??XYZ" and word "AXYZ", one '?' covers 'A'.
            Arguments.of("??XYZ", "AXYZ")
        );
    }

    @ParameterizedTest(name = "CC08: For rack \"{0}\" and word \"{1}\" using wildcards, expected true")
    @MethodSource("wildcardUsageProvider")
    @DisplayName("CC08: Wildcards enable word construction")
    void testWildcardUsage(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertTrue(result1, "Word should be constructible via wildcard substitution (CharSequence)."),
            () -> assertTrue(result2, "Word should be constructible via wildcard substitution (char[] overload).")
        );
    }

    // CC09: Wildcard Insufficient.
    // Even with a wildcard, if too many letters are missing, the word is not constructible.
    static Stream<Arguments> wildcardInsufficientProvider() {
        return Stream.of(
            // For rack "A?B" and word "ABCD", one wildcard isn't enough.
            Arguments.of("A?B", "ABCD"),
            // For rack "?ABC" and word "ABCDX", wildcard covers one letter but extra letter remains.
            Arguments.of("?ABC", "ABCDX")
        );
    }

    @ParameterizedTest(name = "CC09: For rack \"{0}\" and word \"{1}\", expected false due to insufficient wildcards")
    @MethodSource("wildcardInsufficientProvider")
    @DisplayName("CC09: Insufficient wildcard coverage results in non-constructible word")
    void testWildcardInsufficient(String rackState, String word) {
        LetterRack rack = new LetterRack(rackState.toCharArray());
        boolean result1 = rack.canConstruct(word);
        boolean result2 = rack.canConstruct(word.toCharArray());
        assertAll(
            () -> assertFalse(result1, "Word should not be constructible with insufficient wildcards (CharSequence)."),
            () -> assertFalse(result2, "Word should not be constructible with insufficient wildcards (char[] overload).")
        );
    }    
    
    @Test
    @DisplayName("Test adding a single wildcard marks rack as having a wildcard")
    void testAddSingleWildcard() {
        LetterRack rack = new LetterRack();
        rack.addLetter('?');
        assertTrue(rack.hasWildcard(), "Rack should report having a wildcard after adding one.");
        long wildcardCount = new String(rack.getLetters()).chars().filter(ch -> ch == '?').count();
        assertEquals(1, wildcardCount, "There should be exactly one wildcard in the rack.");
    }
    
    @Test
    @DisplayName("Test adding two wildcards is allowed")
    void testAddTwoWildcards() {
        LetterRack rack = new LetterRack();
        rack.addLetter('?');
        rack.addLetter('?');
        assertTrue(rack.hasWildcard(), "Rack should report having wildcard(s) after adding two wildcards.");
        long wildcardCount = new String(rack.getLetters()).chars().filter(ch -> ch == '?').count();
        assertEquals(2, wildcardCount, "There should be exactly two wildcards in the rack.");
    }
    
    @Test
    @DisplayName("Test adding a third wildcard throws an exception")
    void testAddThirdWildcardThrowsException() {
        LetterRack rack = new LetterRack();
        rack.addLetter('?');
        rack.addLetter('?');
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rack.addLetter('?'),
            "Adding a third wildcard should throw an IllegalStateException."
        );
        assertTrue(exception.getMessage().toLowerCase().contains("wildcard"),
                "Exception message should mention that the wildcard limit has been exceeded.");
    }
    
    @Test
    @DisplayName("Test removing a wildcard updates the wildcard state")
    void testRemoveWildcard() {
        LetterRack rack = new LetterRack();
        rack.addLetter('?');
        assertTrue(rack.hasWildcard(), "Rack should have a wildcard after adding one.");
        boolean removed = rack.removeLetter('?');
        assertTrue(removed, "Removal of the wildcard should return true.");
        assertFalse(rack.hasWildcard(), "After removing the only wildcard, rack should not report any wildcards.");
    }
    
    @Test
    @DisplayName("Test addLetters(char...) with wildcards does not exceed limit")
    void testAddLettersWithWildcards() {
        LetterRack rack = new LetterRack();
        // This input includes two wildcards among other letters.
        rack.addLetters(new char[]{'A', '?', 'b', '?'});
        
        long wildcardCount = new String(rack.getLetters()).chars().filter(ch -> ch == '?').count();
        assertEquals(2, wildcardCount, "There should be exactly two wildcards in the rack after addLetters.");
        assertTrue(rack.hasWildcard(), "Rack should report having wildcards when at least one exists.");
    }    
    
}