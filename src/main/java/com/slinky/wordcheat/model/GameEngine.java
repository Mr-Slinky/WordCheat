package com.slinky.wordcheat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A facade that encapsulates all major backend models and provides a
 * centralised point of interaction for querying and modifying the game state.
 * This class delegates the majority of its operations to underlying objects
 * including the {@code GameBoard}, {@code LetterRack}, {@code TileSet} and
 * {@code MoveFinder}.
 *
 * <p>
 * It is responsible for synchronising the state between the board and the
 * available tile pool. As such, it ensures that letters already present on the
 * board are removed from the tile set and that the letter rack never exceeds
 * its maximum capacity.
 *
 * <p>
 * Typical usage involves retrieving the current state of the board and letter
 * rack, analysing word suggestions based on available letters, and accepting a
 * valid move (suggestion) to update the game state.
 *
 * <p>
 * A front end copies a real game in with {@link #commit(char[][], int[][], char[])}
 * and then asks for moves:
 *
 * <pre>
 *     engine.commit(boardLetters, new int[][] {{7, 7}}, "RETAIN?".toCharArray());
 *     Move best = engine.getBestMove(); // null when the rack cannot play
 * </pre>
 *
 * <p>
 * The tile set tracks the tiles that are not on the board, so it still counts
 * the letters on the rack. {@link #getUnseenTileCounts()} leaves the rack out
 * as well, which gives the tiles an opponent could be holding.
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
     * @param tileSet    the tile set containing the available letter tiles; must
     *                   not be {@code null}
     * @param moveFinder the word finder used to generate word suggestions; must
     *                   not be {@code null}
     * @param letterRack the initial letters for the letter rack; may be empty but
     *                   must not be {@code null}
     * @throws NullPointerException if {@code board}, {@code tileSet} or
     *                              {@code wordFinder} is {@code null}
     */
    public GameEngine(TileSet tileSet, MoveFinder moveFinder, LetterRack letterRack) {
        this.tileSet    = Objects.requireNonNull(tileSet,    "TileSet cannot be null");
        this.moveFinder = Objects.requireNonNull(moveFinder, "WordFinder cannot be null");
        this.letterRack = Objects.requireNonNull(letterRack, "LetterRack cannot be null");
        this.board      = Objects.requireNonNull(moveFinder.getBoard(), "GameBoard cannot be null");
        this.scoreMod   = Objects.requireNonNull(moveFinder.getScoringModule(), "ScoringModule cannot be null");

        this.movesCalculated = false;

        // Remove letters that are already on the board from the tile set.
        removeBoardLettersFromTileSet(false);
    }

    /**
     * Constructs a new {@code GameEngine} with the given backend components and
     * empty rack.
     *
     * @param tileSet    the tile set containing the available letter tiles; must
     *                   not be {@code null}
     * @param moveFinder the word finder used to generate word suggestions; must
     *                   not be {@code null}
     * @throws NullPointerException if {@code board}, {@code tileSet} or
     *                              {@code wordFinder} is {@code null}
     */
    public GameEngine(TileSet tileSet, MoveFinder moveFinder) {
        this(tileSet, moveFinder, new LetterRack());
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Retrieves a deep copy of the current board matrix.
     *
     * @return a deep copy of the board's matrix representing the current game
     * state
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
     * Retrieves every move the letters on the rack can play, highest score
     * first.
     *
     * <p>
     * The moves come from the {@code MoveFinder}. The engine keeps the list
     * until the board or rack changes, so a second call returns the same list.
     *
     * @return a sorted list of {@link Move}s; empty when the rack cannot play
     */
    public List<Move> getAllMoves() {
        if (movesCalculated) return moves;

        moves           = moveFinder.getMoves(letterRack.getLetters());
        movesCalculated = true;
        return moves;
    }

    /**
     * Retrieves the highest scoring move.
     *
     * <p>
     * If moves have not yet been calculated during this cycle, the method
     * will trigger the calculation of all moves.
     *
     * @return the highest scoring {@link Move}, or {@code null} when the rack
     *         cannot play
     */
    public Move getBestMove() {
        var all = getAllMoves();
        return all.isEmpty() ? null : all.get(0);
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
     * by the {@code TileSet}
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
     * Retrieves the positions of all wildcards placed on the game board.
     *
     * @return a two-dimensional int array where each element is a pair [row,
     * col] indicating the location of a wildcard on the board.
     */
    public int[][] getWildCardPositions() {
        return board.getWildCardPositions();
    }

    /**
     * Returns the score value associated with the given letter.
     *
     * @param letter the character for which to retrieve the score
     * @return the number of points that the specified letter is worth
     */
    public int getScoreOf(char letter) {
        return scoreMod.getPointsOf(letter);
    }

    /**
     * Provides the counts of all remaining tiles in the pool, including
     * wildcards.
     * <p>
     * The returned array has length 27. The element at index 0 is the number of
     * wildcards remaining, and the elements at indices 1 through 26 correspond
     * to the counts for letters 'A' through 'Z', respectively.
     *
     * @return an int array of length 27 containing the remaining tile counts
     */
    public int[] getRemainingTileCounts() {
        int[] counts = new int[27];
        char letter = 'A';

        // Index 0 holds the wildcard count
        counts[0] = this.getRemainingWildcardCount();
        // Indices 1-26 correspond to letters A-Z
        for (int i = 1; i < counts.length; i++) {
            counts[i] = this.getRemainingTileCount(letter++);
        }

        return counts;
    }

    /**
     * Computes the score for each tile currently in the player's rack.
     *
     * @return an int array of length equal to the rack size, where each element
     * is the point value of the corresponding letter in the rack
     */
    public int[] getRackScores() {
        int size = letterRack.getSize();
        int[] scores = new int[size];
        for (int i = 0; i < size; i++) {
            scores[i] = scoreMod.getPointsOf(letterRack.charAt(i));
        }

        return scores;
    }

    /**
     * Checks whether the letter at the given cell of the board is a blank
     * tile.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return {@code true} if the cell holds a blank
     * @throws IndexOutOfBoundsException if the cell lies outside the board
     */
    public boolean isWildCard(int row, int col) {
        return board.isWildCard(row, col);
    }

    /**
     * Provides the counts of the tiles that are neither on the board nor on
     * the rack, which are the tiles an opponent could hold or draw.
     *
     * <p>
     * The array has the layout of {@link #getRemainingTileCounts()}: the
     * element at index 0 counts blanks, and indices 1 to 26 count 'A' to 'Z'.
     * For a new game with {@code A, A, B, ?} on the rack, index 0 is 1, index
     * 1 is 7 and index 2 is 1.
     *
     * @return a new int array of length 27
     */
    public int[] getUnseenTileCounts() {
        int[] counts = getRemainingTileCounts();
        for (char letter : letterRack.getLetters()) {
            int index = letter == TileSet.WILDCARD ? 0 : letter - 'A' + 1;
            counts[index] = Math.max(0, counts[index] - 1);
        }

        return counts;
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
     *
     * @param letters the letters to add to the rack; must not be {@code null}
     * @throws IllegalArgumentException if the resulting rack size would exceed
     * the maximum allowed size
     * @throws IllegalStateException if the {@code TileSet} does not have enough
     * tiles for a letter
     */
    public void addLettersToRack(char[] letters) {
        letters = Objects.requireNonNull(letters);
        if (letters.length == 0) return;

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
     * Replaces the board, blanks and rack with a state copied from a real
     * game.
     *
     * <p>
     * Every letter on the new board counts as already played. The tile set is
     * rebuilt from the board, with each blank taken from the blank tiles. The
     * method checks everything before it changes anything, so a rejected call
     * leaves the engine as it was.
     *
     * <pre>
     *     engine.commit(letters, new int[][] {{7, 7}}, "DOG".toCharArray());
     *     engine.isWildCard(7, 7); // true
     * </pre>
     *
     * @param letterMatrix      the board letters, the size of the current
     *                          board; any character outside A to Z is an
     *                          empty cell
     * @param wildcardPositions the {@code [row, column]} pair of each blank on
     *                          the board; may be empty
     * @param rackLetters       the rack letters, with {@link TileSet#WILDCARD}
     *                          for a blank; may be empty
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if the board is the wrong size, a blank
     *                                  sits on an empty cell, the rack holds
     *                                  more than seven letters, or the rack
     *                                  holds a character other than A to Z or
     *                                  a blank
     * @throws IllegalStateException    if the board and rack together hold
     *                                  more of a tile than the game has, or
     *                                  more than two blanks
     */
    public void commit(char[][] letterMatrix, int[][] wildcardPositions, char[] rackLetters) {
        Objects.requireNonNull(letterMatrix,      "letterMatrix must not be null");
        Objects.requireNonNull(wildcardPositions, "wildcardPositions must not be null");
        Objects.requireNonNull(rackLetters,       "rackLetters must not be null");
        if (rackLetters.length > MAX_RACK_SIZE) {
            throw new IllegalArgumentException("Rack cannot contain more than %d letters at a time".formatted(MAX_RACK_SIZE));
        }

        var staged = new GameBoard(letterMatrix);
        if (staged.getRows() != board.getRows() || staged.getCols() != board.getCols()) {
            throw new IllegalArgumentException("Board must be %d by %d, but was %d by %d"
                    .formatted(board.getRows(), board.getCols(), staged.getRows(), staged.getCols()));
        }
        staged.setWildCards(wildcardPositions);

        var newRack = new LetterRack(rackLetters);
        var newPool = buildPool(staged);
        var check   = buildPool(staged);
        for (char letter : newRack.getLetters()) {
            removeFromPool(check, letter);
        }

        board.setBoard(letterMatrix);
        board.setWildCards(wildcardPositions);
        tileSet         = newPool;
        letterRack      = newRack;
        movesCalculated = false;
    }

    /**
     * Lists the words on the given board that the dictionary does not
     * contain, leaving out any that are already on the current board.
     *
     * <p>
     * A front end calls this before {@link #commit(char[][], int[][], char[])}
     * to warn about words that WordCheat does not know.
     *
     * @param letterMatrix the board letters to check
     * @return a new list of the unknown words, each once; empty when every
     *         new word is known
     * @throws NullPointerException     if {@code letterMatrix} is {@code null}
     * @throws IllegalArgumentException if {@code letterMatrix} is not
     *                                  rectangular
     */
    public List<String> findUnknownWords(char[][] letterMatrix) {
        Objects.requireNonNull(letterMatrix, "letterMatrix must not be null");
        var known   = moveFinder.findUnknownWords(board);
        var unknown = new ArrayList<>(moveFinder.findUnknownWords(new GameBoard(letterMatrix)));
        unknown.removeAll(known);
        return unknown;
    }

    /**
     * Captures the board and rack as they stand, and returns a search that
     * finds their moves on its own copy of the board.
     *
     * <p>
     * The search leaves the engine untouched, so a caller can run it on
     * another thread while the engine carries on:
     *
     * <pre>
     *     var search = engine.prepareMoveSearch();
     *     CompletableFuture.supplyAsync(search).thenAccept(this::showMoves);
     * </pre>
     *
     * @return a supplier of the moves, highest score first
     */
    public Supplier<List<Move>> prepareMoveSearch() {
        var finder = moveFinder.copy();
        var rack   = letterRack.getLetters();
        return () -> finder.getMoves(rack);
    }

    /**
     * Accepts a word suggestion, placing the word on the board and updating the
     * game state.
     *
     * <p>
     * The method attempts to place the suggested word onto the board. If
     * placement is successful, it removes the corresponding letters from the
     * {@code TileSet} and calls {@code board.preserve()} to solidify the move.
     * If placement fails, an {@code IllegalStateException} is thrown, as this
     * indicates a bug in the generation of suggestions.
     *
     * @param move the word suggestion to accept; must not be {@code null}
     * @throws IllegalStateException if the word suggestion cannot be placed on
     *                               the board
     */
    public void acceptMove(Move move) {
        placeMove(move);

        // Remove the placed tiles from the rack. A blank on the board came from a blank on the rack.
        for (int row = 0; row < board.getRows(); row++) {
            if (!board.rowHasLetters(row)) continue;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.isNewLetter(row, col)) continue;

                char tile = board.isWildCard(row, col) ? TileSet.WILDCARD : board.getLetterAt(row, col);
                if (!letterRack.removeLetter(tile)) {
                    throw new IllegalStateException("Could not remove '%c' from the rack for move %s".formatted(tile, move));
                }
            }
        }

        removeBoardLettersFromTileSet(true);
        board.preserve(); // Finalise the move on the board. Must be called last
        movesCalculated = false;
    }

    /**
     * Applies a move temporarily to the board and returns a deep copy of the
     * resulting board state.
     *
     * <p>
     * This method uses {@link #placeMove(Move)} to apply the move, clones the
     * board via {@link GameBoard#clone()}, then resets the original board to
     * its prior state using {@link GameBoard#reset()}. It is typically used for
     * move previews or UI rendering.
     *
     * @param move the move to preview.
     * @return a cloned {@code GameBoard} reflecting the state after the move is
     * placed.
     */
    public GameBoard previewMove(Move move) {
        placeMove(move);
        var clone = board.clone();
        board.reset();
        return clone;
    }

    /**
     * Resets the {@code TileSet} to its initial state.
     *
     * <p>
     * This method may be used to restart a game or to resynchronise the state
     * of the {@code TileSet}.
     *
     */
    public void resetTiles() {
        tileSet.reset();
    }

    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder();
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
     * Builds a full tile set with every tile on the given board taken out. A
     * blank on the board takes a blank tile rather than its letter.
     *
     * @param source the board whose tiles to take out
     * @return a new tile set
     * @throws IllegalStateException if the board holds more of a tile than
     *                               the game has
     */
    private static TileSet buildPool(GameBoard source) {
        var pool = new DefaultTileSet();
        for (int row = 0; row < source.getRows(); row++) {
            for (int col = 0; col < source.getCols(); col++) {
                if (source.hasLetterAt(row, col)) {
                    removeFromPool(pool, source.isWildCard(row, col) ? TileSet.WILDCARD : source.getLetterAt(row, col));
                }
            }
        }
        return pool;
    }

    /**
     * Takes one tile out of the pool, explaining a shortage in terms a player
     * understands.
     */
    private static void removeFromPool(TileSet pool, char tile) {
        try {
            pool.removeLetter(tile);
        } catch (TileSet.InvalidTileRemovalException ex) {
            String name = tile == TileSet.WILDCARD ? "blank" : "'%c'".formatted(tile);
            throw new IllegalStateException("The board and rack hold more %s tiles than the game has".formatted(name), ex);
        }
    }

    /**
     * Removes letters present on the board from the {@code TileSet}.
     *
     * <p>
     * Depending on the value of {@code newLetters}, this method either removes
     * only the letters that are newly placed on the board (if {@code true}) or
     * removes any letter present on the board (if {@code false}).
     *
     * @param newLetters if {@code true}, only newly added letters are removed;
     * if {@code false}, all letters on the board are removed
     */
    private void removeBoardLettersFromTileSet(boolean newLetters) {
        for (int row = 0; row < board.getRows(); row++) {
            if (!board.rowHasLetters(row)) {
                continue;
            }

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.colHasLetters(col)) {
                    continue;
                }

                boolean shouldRemove;
                if (newLetters) {
                    shouldRemove = board.isNewLetter(row, col);
                } else {
                    shouldRemove = board.hasLetterAt(row, col);
                }

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
     *
     * @param letters the letters to check; must not be {@code null}
     * @throws IllegalStateException if the tile set does not have enough tiles
     * for a given letter
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

    /**
     * Attempts to place the specified move on the game board.
     *
     * <p>
     * This method delegates to
     * {@link GameBoard#placeWord(String, int, int, boolean)} to lay down the
     * word on the board. If placement fails, this indicates an inconsistency in
     * the move generation logic and triggers an {@link IllegalStateException}.
     *
     * @param move the move to be placed on the board.
     * @throws IllegalStateException if the move cannot be placed (e.g. invalid
     * suggestion).
     */
    private void placeMove(Move move) {
        boolean placed = board.placeWord(
                move.word(), move.row(), move.col(), !move.verticallyPlaced()
        );

        if (!placed) {
            throw new IllegalStateException("Critical error: could not place move " + move);
        }
    }

}