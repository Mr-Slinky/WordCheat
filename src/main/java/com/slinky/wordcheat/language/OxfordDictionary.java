package com.slinky.wordcheat.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The {@code OxfordDictionary} class is responsible for loading, managing and
 * searching a dictionary of words for the Scrabble Cheat application.
 * <p>
 * This class loads two versions of the dictionary:
 * <ul>
 *   <li>
 *     The main dictionary file, which contains the words in their normal
 *     (forward) order.
 *   </li>
 *   <li>
 *     A reversed dictionary file, where each word has been reversed. This is
 *     used to efficiently perform ends-with queries by converting them into
 *     starts-with queries on the reversed words.
 *   </li>
 * </ul>
 * In addition, a mapping array is maintained to link each reversed word back to
 * its original index in the main dictionary. This prevents the reversed words
 * from being redundantly reversed twice
 * <p>
 * All lookups are performed via efficient binary search routines.
 * <p>
 * <b>Assumptions:</b>
 * <ul>
 *   <li>
 *     The dictionary files are placed in the resources directory and have the
 *     expected formatting.
 *   </li>
 *   <li>
 *     The total number of words is fixed and defined by
 *     {@code WORD_COUNT}.
 *   </li>
 * </ul>
 *
 * @author Kheagen Haskins
 */
public final class OxfordDictionary implements Dictionary {

    // ================================[ Static Constants ]================================ \\
    /**
     * The resource path for the main dictionary file containing words in
     * forward order.
     */
    private static final String MAIN_FILE_PATH = "/words/words_alpha.txt";

    /**
     * The resource path for the reversed dictionary file containing words in
     * reversed order.
     */
    private static final String REV_FILE_PATH = "/words/words_alpha_reversed.txt";

    private static final String[] TWO_LETTER_WORDS = {
        "AA", "AB", "AD", "AE", "AG", "AH", "AI", "AL", "AM", "AN",
        "AR", "AS", "AT", "AW", "AX", "AY", "BA", "BE", "BI", "BO",
        "BY", "DA", "DE", "DI", "DO", "ED", "EE", "EF", "EH", "EL",
        "EM", "EN", "EO", "ER", "ES", "ET", "EW", "EX", "FA", "FE",
        "FI", "FU", "GI", "GO", "HA", "HE", "HI", "HM", "HO", "ID",
        "IF", "IN", "IO", "IS", "IT", "JA", "JE", "JO", "KA", "KI",
        "LA", "LI", "LO", "MA", "ME", "MI", "MM", "MO", "MU", "MY",
        "NA", "NE", "NO", "NU", "OD", "OE", "OF", "OH", "OI", "OK",
        "OM", "ON", "OO", "OP", "OR", "OS", "OU", "OW", "OX", "OY",
        "PA", "PE", "PI", "PO", "QI", "RE", "SH", "SI", "SO", "TA",
        "TE", "TI", "TO", "UH", "UM", "UN", "UP", "US", "UT", "VU",
        "WE", "WO", "XI", "XU", "YA", "YE", "YI", "YO", "ZA"
    };


    // ================================[ Fields ]================================ \\
    /**
     * The array containing all words in the dictionary in their original
     * (forward) order. This array is assumed to be sorted and is used for exact
     * searches and prefix matching.
     */
    private final List<String> words = new ArrayList<>(168555);

    /**
     * The array containing all words in reversed order. Each element in this
     * array is the reversed version of the corresponding word in {@code words}.
     */
    private final List<String> revWords = new ArrayList<>(168555);

    /**
     * The mapping array which links indices in the reversed words array to
     * their corresponding indices in the original dictionary. For example, if
     * {@code map[10] == 43}, then the reversed word at index 10 corresponds to
     * the original word at index 43.
     */
    private int[] map;
    
    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code Dictionary} instance by loading the dictionary
     * data from the resources.
     * <p>
     * The constructor initialises both the forward and reversed dictionaries by
     * calling the {@code readFile} helper method. When reading the reversed
     * dictionary file, mapping information is also extracted and stored.
     */
    public OxfordDictionary() {
        readFile(words,    MAIN_FILE_PATH, false);
        readFile(revWords, REV_FILE_PATH,  true);
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Searches for an exact match of the specified target word within the
     * dictionary.
     * <p>
     * The search is performed using a binary search algorithm on the sorted
     * {@code words} array. This search is case-insensitive as the target word
     * is converted to lowercase prior to searching.
     * <p>
     * The algorithm will take at most 19 iterations in the worst case.
     *
     * @param  target the full word to search for.
     * @return the index of the target word within the {@code words} array if
     *         found; otherwise, -1.
     */
    public int search(String target) {
        if (target.length() == 2) {
            return searchTwoLetterWords(target);
        }
        
        int left  = 0;
        int right = words.size() - 1;
        target    = target.toLowerCase();
        
        while (left <= right) {
            int midIndex = (right - left) / 2 + left; // Calculate middle index
            int comparison = words.get(midIndex).compareTo(target);

            if (comparison > 0) {
                right = midIndex - 1;
            } else if (comparison < 0) {
                left  = midIndex + 1;
            } else {
                return midIndex;
            }
        }

        return -1; // Word not found
    }
    
    /**
     * Retrieves a list of words from the dictionary that begin with the
     * specified prefix.
     * <p>
     * The method employs an efficient binary search approach to locate the
     * range of words that match the prefix, ensuring optimal performance even
     * for large datasets. The search is case-insensitive.
     *
     * @param  prefix the prefix to search for; it will be converted to lowercase
     *         to ensure case-insensitive matching.
     * @return a list of words starting with the specified prefix; if no words
     *         match, an empty list is returned.
     */
    public List<String> startsWith(String prefix) {
        prefix = prefix.toLowerCase();
        return startsWith(prefix, words, null);
    }
    
    /**
     * Retrieves a list of words from the dictionary that end with the specified
     * suffix.
     * <p>
     * This method converts the ends-with query into a starts-with query by
     * reversing the suffix and performing a binary search on the reversed
     * dictionary ({@code revWords}). The mapping array ({@code map}) is used to
     * retrieve the original words from the forward dictionary.
     *
     * @param suffix the suffix to search for.
     * @return a list of words that end with the specified suffix; if no words
     *         match, an empty list is returned.
     */
    public List<String> endsWith(String suffix) {
        // Reverse the suffix so that an endsWith query becomes a startsWith query on the reversed dictionary.
        String reversedSuffix = new StringBuilder(suffix).reverse().toString();
        return startsWith(reversedSuffix, revWords, map);
    }

    /**
     * Retrieves a list of words that contain the specified substring.
     * 
     * <p>
     * The method creates a parallel stream from the {@code words} array,
     * filters out words that contain the substring, and collects the results
     * into a list. The stream is converted back to sequential mode to ensure
     * the final result maintains the original ordering.
     * 
     * @param  substring the substring to search for within each word.
     * @return a list of words that contain the specified substring; if no words
     *         match, an empty list is returned.
     */
    public List<String> contains(String substring) {
        return words.stream()
                .parallel()
                .filter(word -> word.contains(substring))
                .sequential() // Ensure the final output maintains original ordering
                .collect(Collectors.toList());
    }

    /**
     * Filters the dictionary's words using the provided predicate.
     * 
     * <p>
     * This method creates a parallel stream from the dictionary's word array,
     * applies the given {@link Predicate} to filter the words, and collects the
     * results into a list. This parallel approach is designed to improve
     * performance on large datasets by leveraging multi-core processors.
     * 
     * 
     * <p>
     * For example, a client may supply a predicate to filter words by length,
     * pattern, or any custom condition.
     * 
     *
     * @param predicate a {@link Predicate<String>} representing the filtering
     *                  condition to be applied to each word.
     * @return a list of words from the dictionary that satisfy the predicate.
     */
    public List<String> filter(Predicate<String> predicate) {
        return words.stream()
                .parallel()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the specified word exists in the dictionary.
     *
     * @param word the word to check for
     * @return true if the word exists in the dictionary, false otherwise
     */
    public boolean isWord(String word) {
        return search(word) >= 0;
    }

    /**
     * Determines if the provided prefix is valid for any word in the
     * dictionary. This method performs a binary search on the sorted words
     * array and returns true if at least one word starts with the given prefix.
     *
     * @param prefix the prefix to validate
     * @return true if there is at least one word in the dictionary that begins
     *         with the prefix, false otherwise
     */
    public boolean isPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return true;
        }

        prefix = prefix.toLowerCase();
        int left = 0;
        int right = words.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String candidate = words.get(mid).toLowerCase(); // Ensure both are in lowercase
            int minLength    = Math.min(candidate.length(), prefix.length());
            
            int cmp = candidate.substring(0, minLength).compareTo(prefix.substring(0, minLength));
            if (cmp == 0) {
                if (candidate.length() >= prefix.length()) {
                    return true;
                } else {
                    // Candidate is a prefix of 'prefix', so consider it as "less than"
                    cmp = -1;
                }
            }

            if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    // ============================[ Private Helper Methods ]============================ \\
    /**
     * Reads words from a resource file and populates the provided array.
     * <p>
     * If the {@code hasMap} parameter is {@code true}, the file is expected to
     * contain lines with comma-separated values, where the first value is the
     * word and the second is its mapping index. This mapping is stored in the
     * {@code map} array. Otherwise, each line is read as a word.
     * <p>
     * If the resource cannot be located, an {@code Error} is thrown.
     *
     * @param  words the array to populate with words.
     * @param  path the resource path to the file.
     * @param  hasMap indicates whether the file includes mapping data.
     * @throws Error if the file cannot be found in the resources directory.
     */
    private void readFile(List<String> words, String path, boolean hasMap) {
        // Retrieve the resource as an InputStream.
        var res = getClass().getResourceAsStream(path);

        if (res == null) {
            throw new Error("Critical error: Cannot locate file '%s' in resources directory.".formatted(path));
        }
        
        if (hasMap && map == null) {
            if (this.words.isEmpty()) {
                throw new IllegalStateException("Cannot create map before words have been read");
            }
            
            map = new int[this.words.size()];
        }

        try (Scanner in = new Scanner(res)) {
            int i = 0;
            while (in.hasNextLine()) {
                if (hasMap) {
                    // Expect a line containing a word and a mapping index, separated by a comma.
                    String[] lineData = in.nextLine().split(",");
                    words.add(lineData[0]);
                    map[i++] = Integer.parseInt(lineData[1].strip());
                } else {
                    // Read the whole line as a word.
                    words.add(in.nextLine());
                }
            }
            
            
        } catch (Throwable e) {
            System.out.println("Unforeseen Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * A helper method to perform a binary search for words that start with the
     * specified prefix.
     * <p>
     * This method is used to support both {@code startsWith} and
     * {@code endsWith} queries. It performs two binary searches:
     * <ol>
     *   <li>
     *     One to locate the first occurrence (lower bound) of a word that
     *     matches the prefix.
     *   </li>
     *   <li>
     *     Another to locate the last occurrence (upper bound) of a word that
     *     matches the prefix.
     *   </li>
     * </ol>
     * If a mapping array is provided (i.e. not {@code null}), then the matching
     * words are retrieved from the original {@code words} array using the
     * mapping indices.
     *
     * @param  prefix the prefix to search for (must be in lowercase).
     * @param  list the array to search (either the normal or reversed
     *         dictionary).
     * @param  map an optional mapping array. If {@code null}, words from
     *         {@code arr} are returned directly. Otherwise, the mapping is used to
     *         retrieve the original word from {@code words}.
     * @return a list of words that match the given prefix; if no match is
     *         found, an empty list is returned.
     */
    private List<String> startsWith(String prefix, List<String> list, int[] map) {
        // Ensure the prefix is in lowercase.
        prefix = prefix.toLowerCase();
        int n  = list.size();
        int lowerBound = -1;

        // First binary search: find the lower bound (first occurrence) of a matching word.
        int left = 0, right = n - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String candidate = list.get(mid);
            if (candidate.length() < prefix.length()) {
                left = mid + 1;
                continue;
            }

            int cmp = candidate.substring(0, prefix.length()).compareTo(prefix);
            if (cmp < 0) {
                left = mid + 1;
            } else if (cmp > 0) {
                right = mid - 1;
            } else {
                lowerBound = mid;
                right = mid - 1; // Continue searching left to find the first occurrence.
            }
        }

        if (lowerBound == -1) {
            // No words starting with the prefix were found.
            return new ArrayList<>();
        }

        // Second binary search: find the upper bound (last occurrence) of a matching word.
        int upperBound = lowerBound;
        left = lowerBound;
        right = n - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String candidate = list.get(mid);
            if (candidate.length() < prefix.length()) {
                left = mid + 1;
                continue;
            }

            int cmp = candidate.substring(0, prefix.length()).compareTo(prefix);
            if (cmp == 0) {
                upperBound = mid;
                left = mid + 1; // Continue searching right for the last occurrence.
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // Construct the list of matching words.
        List<String> matches = new ArrayList<>();
        for (int i = lowerBound; i <= upperBound; i++) {
            if (map == null) {
                // No mapping provided; add the word directly from the array.
                matches.add(list.get(i));
            } else {
                // Mapping provided; use it to retrieve the corresponding original word.
                matches.add(words.get(map[i]));
            }
        }

        return matches;
    }
    
    private int searchTwoLetterWords(String target) {
        int left = 0;
        int right = TWO_LETTER_WORDS.length - 1;
        target = target.toUpperCase();

        while (left <= right) {
            int midIndex = (right - left) / 2 + left; // Calculate middle index
            int comparison = TWO_LETTER_WORDS[midIndex].compareTo(target);

            if (comparison > 0) {
                right = midIndex - 1;
            } else if (comparison < 0) {
                left = midIndex + 1;
            } else {
                return midIndex;
            }
        }

        return -1; // Word not found
    }
    
}