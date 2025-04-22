package com.slinky.wordcheat.io;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code FilePrep} class is responsible for preparing the dictionary files
 * used in the Scrabble Cheat application.
 * 
 * <p>
 * This utility reads the original dictionary file, creates a reversed version
 * of the words, sorts both arrays, generates a mapping between the reversed
 * words and the original words, and finally writes the processed data back to
 * disk. The reversed file is used later to efficiently perform suffix
 * (ends-with) searches.
 * <p>
 * 
 * The class also filters out invalid two-letter words by checking against a
 * predefined set of allowed letter pairs.
 *
 * @author Kheagen Haskins
 */
public class FilePrep {

    // ================================[ Static Constants ]================================

    /**
     * The name of the main dictionary file.
     */
    private static final String MAIN_FILE_NAME = "/words_alpha.txt";

    /**
     * The name of the reversed dictionary file.
     */
    private static final String REV_FILE_NAME = "/words_alpha_reversed.txt";

    /**
     * The directory in which the dictionary files are stored.
     */
    private static final String DIR = "/words";

    /**
     * The full resource path to the main dictionary file.
     */
    private static final String MAIN_FILE_PATH = DIR + MAIN_FILE_NAME;

    // =============================[ API Methods ]==============================
    /**
     * Reads, processes, and writes the dictionary files.
     * <p>
     * This method performs the following steps:
     * <ol>
     * <li>Reads the main dictionary file into an array of words.</li>
     * <li>Generates a reversed array of the words.</li>
     * <li>Creates a mapping array to link each reversed word back to its
     * original index.</li>
     * <li>Sorts both the main and reversed arrays.</li>
     * <li>Updates the mapping array by searching for the original word
     * corresponding to each reversed word.</li>
     * <li>Writes the processed main dictionary and the reversed dictionary
     * (with mapping) to output files.</li>
     * </ol>
     */
    public static void prepareFiles() {
        String[] words    = readFile(MAIN_FILE_PATH);
        String[] revWords = reverse(words);
        int[] mapping     = getRange(0, words.length);

        // Sort both arrays alphabetically.
        Arrays.sort(words);
        Arrays.sort(revWords);

        // Update the mapping: for each reversed word, find the corresponding index in the main words array.
        for (int i = 0; i < mapping.length; i++) {
            String originalWord = new StringBuilder(revWords[i]).reverse().toString();
            mapping[i] = search(originalWord, words);
        }

        // Write the processed arrays to files. The leading slash is removed from the file name.
        write(words, MAIN_FILE_NAME.substring(1));
        write(revWords, mapping, REV_FILE_NAME.substring(1));
    }

    // ============================[ Private Helper Methods ]============================
    /**
     * Reads a resource file and returns its contents as an array of strings.
     * <p>
     * Only words longer than one character are added.
     *
     * @param path the resource path of the file to read
     * @return an array of words read from the file
     * @throws Error if the resource cannot be located
     */
    private static String[] readFile(String path) {
        var res = FilePrep.class.getResourceAsStream(path);

        if (res == null) {
            throw new Error("Critical error: Cannot locate file '%s' in resources directory.".formatted(path));
        }

        List<String> list = new ArrayList<>();
        try (Scanner in = new Scanner(res)) {
            while (in.hasNextLine()) {
                String word = in.nextLine();
                if (word.length() > 1 && word.length() <= 15) {
                    list.add(word.toLowerCase());
                }
            }
        } catch (Throwable e) {
            System.out.println("Unforeseen Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * Creates a new array of strings where each element is the reverse of the
     * corresponding element in the input array.
     *
     * @param arr the original array of words
     * @return a new array with each word reversed
     */
    private static String[] reverse(String[] arr) {
        String[] rev = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            rev[i] = new StringBuilder(arr[i]).reverse().toString();
        }
        return rev;
    }

    /**
     * Performs a binary search for the target string within a sorted array.
     * <p>
     * The search is case-insensitive.
     *
     * @param target the string to search for
     * @param arr a sorted array of strings in which to search
     * @return the index of the target string if found; otherwise, -1
     */
    private static int search(String target, String[] arr) {
        int left  = 0;
        int right = arr.length - 1;
        target    = target.toLowerCase();

        while (left <= right) {
            int midIndex = left + (right - left) / 2; // Calculate the middle index
            int comparison = arr[midIndex].compareTo(target);

            if (comparison > 0) {
                right = midIndex - 1;
            } else if (comparison < 0) {
                left = midIndex + 1;
            } else {
                return midIndex;
            }
        }
        
        return -1; // Target not found
    }

    /**
     * Generates an integer array representing a range of values.
     * <p>
     * For the given {@code start} and {@code end}, the method returns an array
     * of length {@code (end - start)} where each element is initialised to its
     * index value.
     *
     * @param start the starting value (inclusive)
     * @param end the ending value (exclusive)
     * @return an array containing a range of integer values
     */
    private static int[] getRange(int start, int end) {
        int[] range = new int[end - start];
        for (int i = 0; i < range.length; i++) {
            range[i] = i;
        }
        return range;
    }

    /**
     * Writes an array of words along with a corresponding mapping array to a
     * file.
     * <p>
     * Each line in the output file will have the format:
     * {@code word, mappingValue}.
     *
     * @param words the array of words to write
     * @param mapping the array of mapping indices corresponding to each word
     * @param filename the name of the output file
     */
    private static void write(String[] words, int[] mapping, String filename) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            int finalIndex = words.length - 1;
            final String template = "%s, %d";
            for (int i = 0; i < words.length; i++) {
                out.print(template.formatted(words[i], mapping[i]) + (i == finalIndex ? "" : "\n"));
            }
        } catch (IOException ex) {
            System.err.println("ERROR writing to file: " + filename);
        }
    }

    /**
     * Writes an array of words to a file.
     * <p>
     * If a word is exactly two characters long, it is only written if it is a
     * valid two-letter combination.
     *
     * @param words the array of words to write
     * @param filename the name of the output file
     */
    private static void write(String[] words, String filename) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            int finalIndex = words.length - 1;
            for (int i = 0; i < words.length; i++) {
                out.print(words[i] + (i == finalIndex ? "" : "\n"));
            }
        } catch (IOException ex) {
            System.err.println("ERROR writing to file: " + filename);
        }
    }
    
}