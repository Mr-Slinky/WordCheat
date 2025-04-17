package com.slinky.wordcheat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating permutations of character arrays.
 *
 * <p>
 * Provides methods to produce all possible permutations of a given array of
 * characters. Each permutation uses every character exactly once, arranged in
 * every possible order.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> If the input array is null or empty, an empty list is
 * returned.
 * </p>
 */
public final class PermutationUtils {

    // Prevent instantiation
    private PermutationUtils() {
    }

    /**
     * Generates all permutations of the input character array.
     *
     * <p>
     * Each permutation is represented as a {@code String} containing all
     * characters exactly once. The order of returned permutations is not
     * guaranteed.</p>
     *
     * @param chars the array of characters to permute; may be null or empty
     * @return a list of permutation strings; empty if input is null or length
     *         zero
     */
    public static List<String> generateAllPermutations(char[] chars) {
        List<String> result = new ArrayList<>();
        if (chars == null || chars.length == 0) {
            return result;
        }
        // Work on a copy to avoid modifying the original array
        generate(chars.clone(), chars.length, result);
        return result;
    }

    /**
     * Recursively builds permutations using Heap's algorithm.
     *
     * @param arr  the working array of characters
     * @param n    the current boundary length for permutation
     * @param list the list that collects completed permutations
     */
    private static void generate(char[] arr, int n, List<String> list) {
        if (n == 1) {
            list.add(new String(arr));
            return;
        }
        
        for (int i = 0; i < n; i++) {
            generate(arr, n - 1, list);
            // Swap to prepare next permutation
            if (isEven(n)) {
                swap(arr, i, n - 1);
            } else {
                swap(arr, 0, n - 1);
            }
        }
    }

    /**
     * Swaps two elements in the array.
     *
     * @param arr the array in which to swap elements
     * @param i   index of the first element
     * @param j   index of the second element
     */
    private static void swap(char[] arr, int i, int j) {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * Checks if the given number is even.
     *
     * @param number the integer to test
     * @return {@code true} if {@code number} is even; {@code false} otherwise
     */
    private static boolean isEven(int number) {
        return (number & 1) == 0;
    }

}