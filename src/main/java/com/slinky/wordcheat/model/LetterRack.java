package com.slinky.wordcheat.model;

import static com.slinky.wordcheat.model.TileSet.WILDCARD;
import com.slinky.wordcheat.util.MainUtil;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Represents a logical rack of letters that can contain up to 21 letters,
 * including 2 wildcards (represented by {@link #WILDCARD}). This class provides
 * methods to add, remove, sort, and search letters within the rack. The rack is
 * implemented as a flat array to improve cache efficiency, and its maximum
 * capacity is enforced by the constant {@link #MAX_SIZE}.
 *
 * <p>
 * Main functionalities include:</p>
 * <ul>
 *   <li>Initialising the rack with an array or string of letters.</li>
 *   <li>Adding a letter or multiple letters (from a char array or String) to the rack.</li>
 *   <li>Removing letters by value, by index, or removing the last letter.</li>
 *   <li>Removing a specified number of letters from the end of the rack.</li>
 *   <li>Sorting the letters using an in-place insertion sort algorithm, either with natural ordering or a custom comparator.</li>
 *   <li>Searching for a contiguous subsequence within the rack.</li>
 *   <li>Checking if a given word can be constructed from the letters in the rack, using available letters and wildcards.</li>
 *   <li>Iterating over the letters via the {@link Iterable} interface.</li>
 *   <li>Tracking the number of wildcards and providing a method {@code hasWildcard()}.</li>
 * </ul>
 *
 * <p>
 * This class also implements the {@link CharSequence} interface, allowing it to be used in contexts
 * where a character sequence is required.</p>
 *
 * @author Kheagen Haskins
 */
public final class LetterRack implements CharSequence, Iterable<Character> {

    // ================================[ Static ]================================ \\
    public static final int MAX_SIZE  = 21;

    // ================================[ Fields ]================================ \\
    private char[] letters;
    private int    size;
    private int    wildcardCount = 0;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new LetterRack with the provided letters.
     *
     * @param letters an array of characters to initialise the rack with.
     * @throws IllegalArgumentException if the input array exceeds {@link #MAX_SIZE} characters.
     */
    public LetterRack(char... letters) {
        this.letters = new char[MAX_SIZE];
        this.size    = 0;

        if (letters != null && letters.length > 0) {
            if (letters.length > MAX_SIZE) {
                throw new IllegalArgumentException("LetterRack cannot exceed " + MAX_SIZE + " characters");
            }
            for (int i = 0; i < letters.length; i++) {
                char toAdd = Character.toUpperCase(letters[i]);
                // Use addLetter to ensure all extra checks (including wildcard tracking) occur.
                addLetter(toAdd);
            }
        }
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the current number of letters in the rack.
     *
     * @return the number of letters.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns a deep copy of the current letters in the rack.
     *
     * @return a new array containing the letters.
     */
    public char[] getLetters() {
        char[] copy = new char[size];
        System.arraycopy(letters, 0, copy, 0, size);
        return copy;
    }
    
    /**
     * Returns true if the rack currently contains one or more wildcard characters.
     *
     * @return true if there is at least one wildcard in the rack, false otherwise.
     */
    public boolean hasWildcard() {
        return wildcardCount > 0;
    }

    // ===========================[ Mutator Methods ]============================ \\
    /**
     * Adds a letter to the rack.
     *
     * @param letter the letter to add.
     * @throws IllegalStateException    if the rack is already full or if adding a wildcard
     *                                  would exceed the allowed limit (2).
     * @throws IllegalArgumentException if the letter is not a valid character.
     */
    public void addLetter(char letter) {
        if (size >= MAX_SIZE) {
            throw new IllegalStateException("Cannot add more than " + MAX_SIZE + " letters; rack is full.");
        }
        
        letter = Character.toUpperCase(letter);
        if (!(MainUtil.isLetter(letter) || letter == WILDCARD)) {
            throw new IllegalArgumentException("Invalid character: " + letter);
        }
        
        // Enforce that no more than 2 wildcards are allowed.
        if (letter == WILDCARD) {
            if (wildcardCount >= 2) {
                throw new IllegalStateException("Cannot add more than 2 wildcards; rack already has " + wildcardCount);
            }
            
            wildcardCount++;
        }
        
        letters[size++] = letter;
    }

    /**
     * Removes the first occurrence of the given letter from the rack.
     *
     * @param letter the letter to remove.
     * @return true if the letter was found and removed, false otherwise.
     */
    public boolean removeLetter(char letter) {
        for (int i = 0; i < size; i++) {
            if (letters[i] == letter) {
                char removed = letters[i];
                // Shift elements left to fill the gap.
                for (int j = i; j < size - 1; j++) {
                    letters[j] = letters[j + 1];
                }
                
                size--;
                
                if (removed == WILDCARD) {
                    wildcardCount--;
                }
                
                return true;
            }
        }
        
        return false;
    }

    /**
     * Removes the letter at the specified index.
     *
     * @param index the index of the letter to remove.
     * @return the removed character.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    public char removeLetterAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
        
        char removed = letters[index];
        for (int j = index; j < size - 1; j++) {
            letters[j] = letters[j + 1];
        }
        
        size--;
        if (removed == WILDCARD) {
            wildcardCount--;
        }
        
        return removed;
    }

    /**
     * Removes and returns the last letter in the rack.
     *
     * @return the removed character.
     * @throws IllegalStateException if the rack is empty.
     */
    public char pop() {
        if (size == 0) {
            throw new IllegalStateException("Cannot pop from an empty rack.");
        }
        
        char removed = letters[size - 1];
        size--;
        if (removed == WILDCARD) {
            wildcardCount--;
        }
        
        return removed;
    }

    /**
     * Removes the specified number of letters from the end of the rack.
     *
     * @param count the number of letters to remove.
     * @throws IllegalArgumentException if {@code count} is negative.
     * @throws IllegalStateException if there are not enough letters in the rack.
     */
    public void remove(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Cannot remove a negative number of letters.");
        }
        
        if (count > size) {
            throw new IllegalStateException("Cannot remove " + count + " letters from a rack of size " + size + ".");
        }
        
        for (int i = 0; i < count; i++) {
            char removed = letters[--size];
            if (removed == WILDCARD) {
                wildcardCount--;
            }
            
            letters[size] = 0;
        }
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Adds multiple letters to the rack.
     *
     * <p>
     * This method accepts a varargs array of characters and attempts to add
     * them to the rack. It ensures that the total number of letters does not
     * exceed {@link #MAX_SIZE}.
     * </p>
     *
     * @param letters the characters to add.
     * @throws IllegalStateException if adding the letters would exceed the
     *                               maximum allowed letters.
     */
    public void addLetters(char... letters) {
        if (letters == null) {
            return;
        }
        
        if (this.size + letters.length > MAX_SIZE) {
            throw new IllegalStateException(
                "Cannot add letters: addition exceeds maximum rack size (" + MAX_SIZE + ")."
            );
        }
        
        for (char letter : letters) {
            // addLetter already converts to uppercase.
            addLetter(letter);
        }
    }

    /**
     * Adds multiple letters to the rack from a String.
     *
     * <p>
     * This method converts the provided string to a character array and
     * delegates the addition to the varargs method. It ensures that the total
     * number of letters does not exceed {@link #MAX_SIZE}.
     * </p>
     *
     * @param letters the string containing the characters to add.
     * @throws IllegalStateException if adding the letters would exceed the
     *                               maximum allowed letters.
     */
    public void addLetters(String letters) {
        if (letters == null) {
            return;
        }
        
        addLetters(letters.toCharArray());
    }

    /**
     * Sorts all letters in the rack in-place using natural ordering. This
     * method uses an in-place insertion sort algorithm.
     */
    public void sortAll() {
        insertionSort(letters, 0, size);
    }

    /**
     * Sorts all letters in the rack in-place using the provided comparator.
     * This method uses an in-place insertion sort algorithm.
     *
     * @param comparator the comparator to determine the order of the letters.
     */
    public void sort(Comparator<Character> comparator) {
        insertionSort(letters, 0, size, comparator);
    }

    /**
     * Checks if the specified word can be constructed from the letters in the
     * rack. A letter from the rack can only be used once per word construction.
     * Wildcards (represented by {@link #WILDCARD}) can match any letter.
     *
     * @param word the word to check.
     * @return true if the word can be constructed, false otherwise.
     */
    public boolean canConstruct(CharSequence word) {
        if (word == null) {
            return false;
        }

        char[] available = getLetters();
        int availableSize = size;
        for (int i = 0; i < word.length(); i++) {
            char target = word.charAt(i);
            boolean found = false;
            for (int j = 0; j < availableSize; j++) {
                if (available[j] == target) {
                    available[j] = available[availableSize - 1];
                    availableSize--;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                for (int j = 0; j < availableSize; j++) {
                    if (available[j] == WILDCARD) {
                        available[j] = available[availableSize - 1];
                        availableSize--;
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Checks if the specified word (as a character array) can be constructed
     * from the letters in the rack. This method delegates to
     * {@link #canConstruct(CharSequence)}.
     *
     * @param word the array of characters representing the word.
     * @return true if the word can be constructed, false otherwise.
     */
    public boolean canConstruct(char[] word) {
        if (word == null) {
            return false;
        }
        
        return canConstruct(new String(word));
    }

    /**
     * Checks if the rack contains the specified character.
     *
     * @param c the character to search for.
     * @return true if the character is found, false otherwise.
     */
    public boolean contains(char c) {
        for (int i = 0; i < size; i++) {
            if (letters[i] == c) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks if the rack contains the specified contiguous subsequence of
     * characters.
     *
     * @param str the sequence of characters to search for.
     * @return true if the sequence is found, false otherwise.
     */
    public boolean contains(CharSequence str) {
        int len = str.length();
        for (int i = 0; i <= size - len; i++) {
            CharSequence subSeq = subSeq(i, i + len);
            if (subSeq.equals(str)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns a string representation of the letter rack.
     *
     * @return a string composed of the letters in the rack.
     */
    @Override
    public String toString() {
        return new String(getLetters());
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for " + size);
        }
        
        return letters[index];
    }

    @Override
    public boolean isEmpty() {
        return size <= 0;
    }

    /**
     * Returns a subsequence of the rack as a {@link CharSequence}.
     *
     * @param start the starting index (inclusive).
     * @param end   the ending index (exclusive).
     * @return a {@link CharSequence} representing the subsequence.
     * @throws IndexOutOfBoundsException if start or end are out of bounds.
     * @throws IllegalArgumentException if start is greater than end.
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > size) {
            throw new IndexOutOfBoundsException("Bounds " + start + ", " + end + " out of bounds for " + size);
        }
        
        if (start > end) {
            throw new IllegalArgumentException("Start (" + start + ") cannot be greater than end (" + end + ")");
        }
        
        return subSeq(start, end);
    }

    /**
     * Returns an iterator over the letters in this rack.
     *
     * @return an Iterator of Character.
     */
    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public Character next() {
                return letters[currentIndex++];
            }
        };
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * An in-place insertion sort algorithm using the provided comparator. Sorts
     * the subarray from index {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param array      the array containing the elements to sort.
     * @param start      the starting index (inclusive).
     * @param end        the ending index (exclusive).
     * @param comparator the comparator to determine the order of the elements.
     */
    private void insertionSort(char[] array, int start, int end, Comparator<Character> comparator) {
        for (int i = start + 1; i < end; i++) {
            char key = array[i];
            int j = i - 1;
            while (j >= start && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            
            array[j + 1] = key;
        }
    }

    /**
     * An in-place insertion sort algorithm using natural ordering. Sorts the
     * subarray from index {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param array the array containing the elements to sort.
     * @param start the starting index (inclusive).
     * @param end   the ending index (exclusive).
     */
    private void insertionSort(char[] array, int start, int end) {
        for (int i = start + 1; i < end; i++) {
            char key = array[i];
            int j = i - 1;
            while (j >= start && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            
            array[j + 1] = key;
        }
    }

    /**
     * Constructs a subsequence of the letters without additional validation.
     *
     * @param start the starting index (inclusive).
     * @param end   the ending index (exclusive).
     * @return a {@link CharSequence} representing the subsequence.
     */
    private CharSequence subSeq(int start, int end) {
        StringBuilder outp = new StringBuilder();
        for (int i = start; i < end; i++) {
            outp.append(letters[i]);
        }
        
        return outp;
    }
    
}