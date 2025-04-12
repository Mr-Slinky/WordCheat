package com.slinky.wordcheat.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A façade that encapsulates all major backend models and provides a
 * centralised point of interaction for querying and modifying the game state.
 * This class delegates the majority of its operations to underlying objects
 * including the {@code GameBoard}, {@code LetterRack}, {@code TileSet} and
 * {@code WordFinder}.
 * 
 * <p>
 * It is responsible for synchronising the state between the board and the
 * available tile pool. As such, it ensures that letters already present on the
 * board are removed from the tile set and that the letter rack never exceeds
 * its maximum capacity.
 * </p>
 * 
 * <p>
 * Typical usage involves retrieving the current state of the board and letter
 * rack, analysing word suggestions based on available letters, and accepting a
 * valid move (suggestion) to update the game state.
 * </p>
 *
 * <pre>
 * GameEngine engine = new GameEngine(board, tileSet, wordFinder, startingLetters);
 * char[][] currentMatrix = engine.getMatrix(); // deep copy of the board state
 * engine.addLettersToRack(newLetters);         // add letters to the rack
 * List&lt;Suggestion&gt; suggestions = engine.getAllSuggestions(); // get word suggestions
 * engine.acceptSuggestion(suggestion);         // accept a suggestion and update state
 * </pre>
 *
 * @author Kheagen Haskins
 */
public class GameEngine {

    // ================================[ Static ]================================ \\
    private static final int MAX_RACK_SIZE = 7;

    // ================================[ Fields ]================================ \\
    private GameBoard  board;
    private LetterRack letterRack;
    private TileSet    tileSet;
    private WordFinder wordFinder;

    private boolean scoreCalculated;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code GameEngine} with the given backend components and
     * initial letters.
     * 
     * <p>
     * The provided {@code GameBoard} is expected to be pre-configured. The
     * tileSet is immediately synchronised by removing any letters that are
     * already placed on the board.
     * </p>
     *
     * @param board      the game board; must not be {@code null}
     * @param tileSet    the tile set containing the available letter tiles; must
     *                   not be {@code null}
     * @param wordFinder the word finder used to generate word suggestions; must
     *                   not be {@code null}
     * @param letters    the initial letters for the letter rack; may be empty but
     *                   must not be {@code null}
     * @throws NullPointerException if {@code board}, {@code tileSet} or
     *                              {@code wordFinder} is {@code null}
     */
    public GameEngine(GameBoard board, TileSet tileSet, WordFinder wordFinder, LetterRack letterRack) {
        this.board      = Objects.requireNonNull(board,      "GameBoard cannot be null");
        this.tileSet    = Objects.requireNonNull(tileSet,    "TileSet cannot be null");
        this.wordFinder = Objects.requireNonNull(wordFinder, "WordFinder cannot be null");
        this.letterRack = Objects.requireNonNull(letterRack, "LetterRack cannot be null");

        this.scoreCalculated = false;

        // Remove letters that are already on the board from the tile set.
        removeBoardLettersFromTileSet(false);
    }
    
    public GameEngine(GameBoard board, TileSet tileSet, WordFinder wordFinder) {
        this.board      = Objects.requireNonNull(board,      "GameBoard cannot be null");
        this.tileSet    = Objects.requireNonNull(tileSet,    "TileSet cannot be null");
        this.wordFinder = Objects.requireNonNull(wordFinder, "WordFinder cannot be null");
        this.letterRack = new LetterRack();

        this.scoreCalculated = false;

        // Remove letters that are already on the board from the tile set.
        removeBoardLettersFromTileSet(false);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Retrieves a deep copy of the current board matrix.
     *
     * @return a deep copy of the board's matrix representing the current game
     *         state
     */
    public char[][] getMatrix() {
        return board.getMatrix();
    }

    /**
     * Retrieves a deep copy of the current letters in the letter rack.
     *
     * @return a deep copy of the letter rack's letters
     */
    public char[] getLetters() {
        return letterRack.getLetters();
    }

    /**
     * Retrieves all word suggestions based on the current state of the letter
     * rack.
     * 
     * <p>
     * The suggestions are obtained from the {@code WordFinder} and sorted
     * before being returned. This call also marks the suggestion scores as
     * calculated.
     * </p>
     *
     * @return a sorted list of word {@code Suggestion}s
     */
    public List<Suggestion> getAllSuggestions() {
        var suggestionList = wordFinder.getWordSuggestions(letterRack);
        Collections.sort(suggestionList);

        scoreCalculated = true;
        return suggestionList;
    }

    /**
     * Retrieves the highest scoring word suggestion.
     * 
     * <p>
     * If suggestions have not yet been calculated during this cycle, the method
     * will trigger the calculation of all suggestions.
     * </p>
     *
     * @return the highest scoring word {@code Suggestion}
     */
    public Suggestion getHighestSuggestion() {
        if (!scoreCalculated) {
            getAllSuggestions();
        }

        return wordFinder.getHighestScoringWord();
    }

    /**
     * Returns the maximum number of tiles that the TileSet can hold.
     *
     * @return the maximum tile count
     */
    public int getMaxTileCount() {
        return tileSet.getMaxTileCount();
    }

    /**
     * Retrieves the remaining count of tiles for the given letter in the
     * {@code TileSet}.
     *
     * @param letter the letter for which to retrieve the remaining tile count
     * @return the remaining tile count for the specified letter
     * @throws IllegalArgumentException if the letter is invalid or unsupported
     *                                  by the {@code TileSet}
     */
    public int getRemainingTileCount(char letter) {
        return tileSet.getRemainingTileCount(letter);
    }

    /**
     * Retrieves the total remaining tile count in the {@code TileSet}.
     *
     * @return the total remaining tile count
     */
    public int getRemainingTileCount() {
        return tileSet.getRemainingTileCount();
    }

    /**
     * Retrieves the remaining number of blank (wildcard) tiles in the
     * {@code TileSet}.
     *
     * @return the remaining number of blank tiles
     */
    public int getRemainingWildcardCount() {
        return tileSet.getRemainingWildcardCount();
    }

    /**
     * Retrieves the count of wildcard markers present on the game board.
     *
     * @return the number of wildcards on the board
     */
    public int getWildcardCount() {
        return board.getWildcardCount();
    }

    // ===========================[ Mutator Methods ]============================ \\
    /**
     * Adds the specified letters to the letter rack.
     * 
     * <p>
     * This method first checks that adding the new letters will not exceed the
     * maximum rack size. It also verifies that the {@code TileSet} contains a
     * sufficient count of each letter before allowing the operation.
     * </p>
     *
     * @param letters the letters to add to the rack; must not be {@code null}
     * @throws IllegalArgumentException if the resulting rack size would exceed
     *                                  the maximum allowed size
     * @throws IllegalStateException    if the {@code TileSet} does not have 
     *                                  enough tiles for a letter
     */
    public void addLettersToRack(char[] letters) {
        letters = Objects.requireNonNull(letters);
        if (letters.length == 0) {
            return;
        }

        int newRackSize = letterRack.getSize() + letters.length;
        if (newRackSize > MAX_RACK_SIZE) {
            throw new IllegalArgumentException("Rack cannot contain more than %d letters at a time"
                    .formatted(MAX_RACK_SIZE));
        }

        requireNonZeroLetterCount(letters);

        letterRack.addLetters(letters);
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Determines whether the letters in the letter rack allow the given word to
     * be constructed.
     * 
     * <p>
     * Initially, this method attempts to match each character in the provided
     * word with letters in the rack. If any letter is not available, it
     * delegates to the {@code TileSet} to check whether the missing letters can
     * be provided from the remaining tiles.
     * </p>
     *
     * @param word the word to check for constructability
     * @return {@code true} if the word can be constructed from the letter rack
     *         (or supplemented by the {@code TileSet}); {@code false} otherwise
     */
    public boolean canCreateWord(String word) {
        boolean[] used = new boolean[letterRack.getSize()];
        char[] letters = letterRack.getLetters();

        for (int i = 0; i < word.length(); i++) {
            char c        = word.charAt(i);
            boolean found = false;
            for (int j = 0; j < letters.length; j++) {
                if (letters[j] == c && !used[j]) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }

            if (!found) {
                return tileSet.canConstructWord(word);
            }
        }

        return true;
    }

    /**
     * Accepts a word suggestion, placing the word on the board and updating the
     * game state.
     * 
     * <p>
     * The method attempts to place the suggested word onto the board. If
     * placement is successful, it removes the corresponding letters from the
     * {@code TileSet} and calls {@code board.preserve()} to solidify the move.
     * If placement fails, an {@code Error} is thrown, as this indicates a bug
     * in the generation of suggestions.
     * </p>
     *
     * @param suggestion the word suggestion to accept; must not be {@code null}
     * @throws Error if the word suggestion cannot be placed on the board
     */
    public void acceptSuggestion(Suggestion suggestion) {
        boolean placed = board.placeWord(
                suggestion.word(), suggestion.row(), suggestion.col(), !suggestion.verticallyPlaced()
        );

        if (!placed) {
            // Suggestions should only be generated if they are valid; 
            // a failure here indicates an error in the suggestion generation 
            // logic.
            throw new Error("Could not place suggestion " + suggestion);
        }

        removeBoardLettersFromTileSet(true);
        board.preserve(); // Finalise the move on the board.
    }

    /**
     * Resets the {@code TileSet} to its initial state.
     * 
     * <p>
     * This method may be used to restart a game or to resynchronise the state
     * of the {@code TileSet}.
     * </p>
     */
    public void resetTiles() {
        tileSet.reset();
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Removes letters present on the board from the {@code TileSet}.
     * 
     * <p>
     * Depending on the value of {@code newLetters}, this method either removes
     * only the letters that are newly placed on the board (if {@code true}) or
     * removes any letter present on the board (if {@code false}).
     * </p>
     *
     * @param newLetters if {@code true}, only newly added letters are removed;
     *                   if {@code false}, all letters on the board are removed
     */
    private void removeBoardLettersFromTileSet(boolean newLetters) {
        for (int row = 0; row < board.getRows(); row++) {
            if (!board.rowHasLetters(row)) continue;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.colHasLetters(col)) continue;

                boolean shouldRemove;
                if (newLetters) shouldRemove = board.isNewLetter(row, col);
                else            shouldRemove = board.hasLetterAt(row, col);

                if (shouldRemove) {
                    char letter = board.getLetterAt(row, col);
                    // If the letter is a wildcard, use the BLANK_TILE marker
                    tileSet.removeLetter(board.isWildCard(row, col) ? TileSet.BLANK_TILE : letter);
                }
            }
        }
    }

    /**
     * Validates that the {@code TileSet} has sufficient quantity for each of
     * the specified letters.
     * 
     * <p>
     * The method computes a frequency count for the provided {@code letters}
     * and checks that the tile set holds at least as many of each letter. If
     * not, an {@code IllegalStateException} is thrown.
     * </p>
     *
     * @param letters the letters to check; must not be {@code null}
     * @throws IllegalStateException if the tile set does not have enough tiles
     *                               for a given letter
     */
    private void requireNonZeroLetterCount(char[] letters) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (char letter : letters) {
            frequency.merge(letter, 1, Integer::sum);
        }
        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            if (tileSet.getRemainingTileCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalStateException("Not enough '%c' tiles available".formatted(entry.getKey()));
            }
        }
    }
}