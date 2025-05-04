package com.slinky.wordcheat.language;

import java.util.List;
import java.util.function.Predicate;

/**
 * The Dictionary interface provides a contract for loading, managing and
 * searching a dictionary of words for the Scrabble Cheat application.
 * 
 * <p>
 * Implementations of this interface should provide efficient methods for:
 * <ul>
 *   <li>Exact word search</li>
 *   <li>Prefix search</li>
 *   <li>Suffix search</li>
 *   <li>Substring search</li>
 *   <li>Filtering words based on a custom predicate</li>
 *   <li>Checking if a word exists</li>
 *   <li>Validating whether a given prefix can start any word in the
 *       dictionary</li>
 * </ul>
 * 
 */
public interface Dictionary {

    /**
     * Searches for an exact match of the specified target word within the
     * dictionary.
     * 
     * <p>
     * This method should be implemented using an efficient search algorithm,
     * such as binary search.
     * 
     *
     * @param target the full word to search for.
     * @return the index of the target word if found; otherwise, -1.
     */
    int search(String target);

    /**
     * Retrieves a list of words from the dictionary that begin with the
     * specified prefix.
     * 
     * <p>
     * The search should be case-insensitive.
     * 
     *
     * @param prefix the prefix to search for.
     * @return a list of words starting with the specified prefix; an empty list
     *         if no match is found.
     */
    List<String> startsWith(String prefix);

    /**
     * Retrieves a list of words from the dictionary that end with the specified
     * suffix.
     * 
     * <p>
     * Implementations may convert the ends-with query into a starts-with query
     * on a reversed dictionary.
     * 
     *
     * @param suffix the suffix to search for.
     * @return a list of words ending with the specified suffix; an empty list
     *         if no match is found.
     */
    List<String> endsWith(String suffix);

    /**
     * Retrieves a list of words that contain the specified substring.
     *
     * @param substring the substring to search for.
     * @return a list of words containing the specified substring; an empty list
     *         if no match is found.
     */
    List<String> contains(String substring);

    /**
     * Filters the dictionary's words using the provided predicate.
     * 
     * <p>
     * The implementation should return a list of words that satisfy the custom
     * filtering condition.
     * 
     *
     * @param predicate a predicate representing the filtering condition.
     * @return a list of words that satisfy the predicate.
     */
    List<String> filter(Predicate<String> predicate);

    /**
     * Checks if the specified word exists in the dictionary.
     *
     * @param word the word to check.
     * @return true if the word exists in the dictionary, false otherwise.
     */
    boolean isWord(String word);

    /**
     * Determines if the provided prefix is valid for any word in the
     * dictionary.
     * 
     * <p>
     * This method should quickly verify if at least one word in the dictionary
     * begins with the given prefix.
     * 
     *
     * @param prefix the prefix to validate.
     * @return true if there is at least one word that begins with the prefix,
     *         false otherwise.
     */
    boolean isPrefix(String prefix);

}