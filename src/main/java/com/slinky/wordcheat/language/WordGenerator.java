package com.slinky.wordcheat.language;

import com.slinky.wordcheat.model.TileSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code WordGenerator} class is responsible for generating all possible
 * valid words from a given set of letters. It employs a recursive backtracking
 * algorithm to form combinations of letters, honouring a minimum and maximum
 * word length. The generation process also supports wildcards, represented by
 * the {@code '?'} character, which can substitute for any letter from A to Z.
 *
 * <p>
 * The class depends on an external {@code Dictionary} to verify whether a
 * constructed string is a valid word or a valid prefix of any word. This
 * ensures that only plausible word combinations are considered.
 * 
 *
 * @author Kheagen Haskins
 * @version 1.0
 */
public class WordGenerator {

    // ================================[ Fields ]================================ \\
    /**
     * The dictionary used to verify valid words and prefixes.
     */
    private Dictionary dictionary;

    /**
     * The minimum allowed length for generated words.
     */
    private int minLength;

    /**
     * The maximum allowed length for generated words.
     */
    private int maxLength;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a {@code WordGenerator} with the specified dictionary and word
     * length bounds.
     *
     * @param dictionary the {@code Dictionary} used for word and prefix
     *                   verification.
     * @param minLength  the minimum length a generated word must have.
     * @param maxLength  the maximum length a generated word may have.
     */
    public WordGenerator(Dictionary dictionary, int minLength, int maxLength) {
        this.dictionary = dictionary;
        this.minLength  = minLength;
        this.maxLength  = maxLength;
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Generates all valid words that can be formed from the supplied array of
     * letters. The method honours the specified minimum and maximum word
     * lengths. It also manages duplicates efficiently by sorting the letters
     * and employing a backtracking approach.
     * 
     * <p>
     * Wildcard characters (denoted by {@code '?'}) in the array are substituted
     * by every letter from A to Z during the generation process.
     * 
     *
     * @param letters an array of characters representing the letters available
     *                for forming words; wildcards are indicated by {@code '?'}.
     * @return a {@code List} of valid words that exist in the dictionary.
     */
    public List<String> generateAllWords(char[] letters) {
        Set<String> words = new HashSet<>();
        // Sort letters to handle duplicates efficiently.
        Arrays.sort(letters);
        
        StringBuilder currentWord = new StringBuilder();
        boolean[] used            = new boolean[letters.length];
        backtrack(letters, used, currentWord, words);
        
        return new ArrayList<>(words);
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * A helper method that employs recursive backtracking to generate valid
     * words from the given letters. This method builds the current word one
     * letter at a time, ensuring at each step that the partial word is a valid
     * prefix or a valid word according to the dictionary.
     * 
     * <p>
     * The recursion stops once the maximum allowed word length is reached. For
     * wildcards, each possible substitution (from A to Z) is attempted.
     * 
     *
     * @param letters     the sorted array of characters (including wildcards)
     *                    available for word construction.
     * @param used        a boolean array indicating whether a letter has been
     *                    used in the current construction.
     * @param currentWord a {@code StringBuilder} holding the current sequence
     *                    of letters being evaluated.
     * @param words       a {@code Set} to collect valid words without duplicates.
     */
    private void backtrack(char[] letters, boolean[] used, StringBuilder currentWord, Set<String> words) {
        // If the current string is within valid length bounds, check if it's a valid word and add it.
        if (currentWord.length() >= minLength && currentWord.length() <= maxLength) {
            String candidate = currentWord.toString();
            if (dictionary.isWord(candidate)) {
                words.add(candidate);
            }
        }
        // Stop extending if the maximum length has been reached.
        if (currentWord.length() == maxLength) return;

        for (int i = 0; i < letters.length; i++) {
            // For non-wildcard letters, skip duplicate letters on the same recursion level.
            if (letters[i] != TileSet.WILDCARD && i > 0 && letters[i] == letters[i - 1] && !used[i - 1]) {
                continue;
            }

            if (!used[i]) {
                if (letters[i] == TileSet.WILDCARD) {
                    // For a wildcard, try every letter from A to Z.
                    for (char sub = 'A'; sub <= 'Z'; sub++) {
                        currentWord.append(sub);
                        String currentStr = currentWord.toString();
                        // Continue if the current candidate is a valid prefix or a valid word.
                        if (dictionary.isPrefix(currentStr) || dictionary.isWord(currentStr)) {
                            used[i] = true;
                            backtrack(letters, used, currentWord, words);
                            used[i] = false;
                        }
                        currentWord.deleteCharAt(currentWord.length() - 1);
                    }
                } else {
                    // For regular letters, proceed as usual.
                    currentWord.append(letters[i]);
                    String currentStr = currentWord.toString();

                    if (dictionary.isPrefix(currentStr) || dictionary.isWord(currentStr)) {
                        used[i] = true;
                        backtrack(letters, used, currentWord, words);
                        used[i] = false;
                    }
                    
                    currentWord.deleteCharAt(currentWord.length() - 1);
                }
            } // end of if...else
        } // end of loop
    } // end of method

}