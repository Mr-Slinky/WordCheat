package com.slinky.wordcheat.model;

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
 * @author Kheagen Haskins
 */
public class GameEngine {

    // ================================[ Static ]================================ \\
    private static final int MAX_RACK_SIZE = 7;

    // ================================[ Fields ]================================ \\
    private GameBoard     board;
    private LetterRack    letterRack;
    private TileSet       tileSet;
    private MoveFinder    moveFinder;
    private List<Move>    moves;
    private ScoringModule scoreMod;

    private boolean movesCalculated;

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
     * @param moveFinder the word finder used to generate word suggestions; must
     *                   not be {@code null}
     * @param letters    the initial letters for the letter rack; may be empty but
     *                   must not be {@code null}
     * @throws NullPointerException if {@code board}, {@code tileSet} or
     *                              {@code wordFinder} is {@code null}
     */
    public GameEngine(TileSet tileSet, MoveFinder moveFinder, LetterRack letterRack) {
        this.tileSet    = Objects.requireNonNull(tileSet,       "TileSet cannot be null");
        this.moveFinder = Objects.requireNonNull(moveFinder, "WordFinder cannot be null");
        this.letterRack = Objects.requireNonNull(letterRack, "LetterRack cannot be null");
        this.board      = Objects.requireNonNull(moveFinder.getBoard(),             "GameBoard cannot be null");
        this.scoreMod   = Objects.requireNonNull(moveFinder.getScoringModule(), "ScoringModule cannot be null");
        
        this.movesCalculated = false;

        // Remove letters that are already on the board from the tile set.
        removeBoardLettersFromTileSet(false);
    }
    
    public GameEngine(TileSet tileSet, MoveFinder wordFinder) {
        this.tileSet    = Objects.requireNonNull(tileSet,       "TileSet cannot be null");
        this.moveFinder = Objects.requireNonNull(wordFinder, "WordFinder cannot be null");
        this.board      = Objects.requireNonNull(wordFinder.getBoard(), "GameBoard cannot be null");
        this.letterRack = new LetterRack();

        this.movesCalculated = false;

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
    public char[] getRackLetters() {
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
    public List<Move> getAllMoves() {
        if (movesCalculated) {
            return moves;
        }
        
        moves = moveFinder.getMoves(letterRack.getLetters());

        movesCalculated = true;
        return moves;
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
    public Move getBestMove() {
        if (board.isEmpty()) {
            return moveFinder.getFirstMove(letterRack.getLetters());
        }

        return getAllMoves().get(0);
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
    
    /**
     * 
     * @return 
     */
    public int[][] getWildCardPositions() {
        return board.getWildCardPositions();
    }
    
    /**
     * 
     * @param letter
     * @return 
     */
    public int getScoreOf(char letter) {
        return scoreMod.getPointsOf(letter);
    }
    
    // ===========================[ Mutator Methods ]============================ \\
    /**
     * Set the letter rack for the game board.
     * 
     * @param letterRack the new LetterRack instance
     */
    public void setLetterRack(LetterRack letterRack) {
        this.letterRack = Objects.requireNonNull(letterRack);
        movesCalculated = false;
    }
    
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
        movesCalculated = false;
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
     * @param move the word suggestion to accept; must not be {@code null}
     * @throws Error if the word suggestion cannot be placed on the board
     */
    public void acceptMove(Move move) {
        placeMove(move);

        // Remove letters from the rack and handle any wildcards
        for (int row = 0; row < board.getRows(); row++) {
            if (!board.rowHasLetters(row)) continue;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.colHasLetters(col)) continue;

                if (board.isNewLetter(row, col)) {
                    char letter        = board.getLetterAt(row, col);
                    boolean isWildCard = false;
                    boolean removed    = letterRack.removeLetter(letter) || (isWildCard = letterRack.removeLetter(TileSet.WILDCARD));
                    if (!removed) {
                        throw new IllegalStateException(
                                "Could not remove %c from LetterRack. Does not seem to be a Wildcard"
                                .formatted(letter)
                        );
                    }
                    
                    if (isWildCard) {
                        board.setWildCardPosition(row, col);
                    }
                }
            }
        }

        removeBoardLettersFromTileSet(true);
        board.preserve(); // Finalise the move on the board. Must be called last
        movesCalculated = false;
    }
    
    public GameBoard previewMove(Move move) {
        placeMove(move);
        
        GameBoard clone = null;
        try {
            clone = board.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
        
        board.reset();
        return clone;
    }
    
    /**
     * Delegate Method
     */
    public void setWildCardPosition(int row, int col) {
        board.setWildCardPosition(row, col);
        tileSet.addLetter(board.getLetterAt(row, col));
        tileSet.removeLetter(TileSet.WILDCARD);
    }
    
    /**
    * Delegate Method
    */
    public void setWildCardPosition(int row, int col, int index) {
        board.setWildCardPosition(row, col, index);
        tileSet.addLetter(board.getLetterAt(row, col));
        tileSet.removeLetter(TileSet.WILDCARD);
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

    @Override
    public String toString() {
        StringBuilder outp   = new StringBuilder();
        final String newLine = System.lineSeparator();
        
        final String cellTemplate    = "|%c";
        final String tileSetTemplate = "%c (%2d)\t";
        
        char letter = 'A';
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                outp.append(cellTemplate.formatted(board.getLetterAt(r, c)));
            }
            outp.append("|");

            if (r < 13) {
                outp.append("\t    ");
                outp.append(tileSetTemplate.formatted(letter, tileSet.getRemainingTileCount(letter)));
                letter++;
                outp.append(tileSetTemplate.formatted(letter, tileSet.getRemainingTileCount(letter)));
                letter++;
            } else if (r == 14) {
                outp.append("\t    ");
                outp.append("Wildcards left: %d".formatted(tileSet.getRemainingWildcardCount()));
            }

            outp.append(newLine);
        }
        
        outp.append(newLine);
        if (!letterRack.isEmpty()) {
            for (Character character : letterRack) {
                outp.append(cellTemplate.formatted(character));
            }
            outp.append("|");
        }

        return outp.toString();
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
                    tileSet.removeLetter(board.isWildCard(row, col) ? TileSet.WILDCARD : letter);
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
    
    private void placeMove(Move move) {
        boolean placed = board.placeWord(
                move.word(), move.row(), move.col(), !move.verticallyPlaced()
        );

        if (!placed) {
            // Suggestions should only be generated if they are valid; 
            // a failure here indicates an error in the suggestion generation 
            // logic.
            throw new IllegalStateException("Critical error: could not place move " + move);
        }
    }
    
}