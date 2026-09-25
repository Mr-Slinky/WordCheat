package com.slinky.wordcheat.model;

import com.slinky.wordcheat.language.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Finds every move a rack of letters can play on a {@link GameBoard}, scored and sorted with the
 * highest score first.
 *
 * <p>
 * A caller builds a {@code MoveFinder} over a board and asks it for moves:
 *
 * <pre>
 *     var finder = new MoveFinder(board, new OxfordDictionary(), new DefaultScoringModule());
 *     List&lt;Move&gt; moves = finder.getMoves("RETAINS".toCharArray());
 *     Move best = moves.isEmpty() ? null : moves.get(0);
 * </pre>
 *
 * <p>
 * On an empty board, every move crosses the centre square. Otherwise, every move touches a letter
 * already on the board. A move is kept when each word it forms is in the {@link Dictionary}; words
 * already on the board are left unchecked. Each placement of tiles appears once in the result.
 *
 * <p>
 * A blank on the rack is written {@link TileSet#WILDCARD}. In a returned {@link Move}, the letter a
 * blank stands for is lowercase, so {@code "CAt"} plays a blank as its T, and that T scores nothing.
 *
 * <p>
 * The search places each candidate on the board and removes it again, so the board must not change
 * while {@link #getMoves(char[])} runs. {@link #copy()} gives a finder over a separate board for a
 * search on another thread.
 *
 * @author Kheagen Haskins
 * @see GameBoard
 * @see ScoringModule
 * @see Dictionary
 */
public class MoveFinder {

    // ========================================================================================== \\
    //                                           Fields                                           \\
    // ========================================================================================== \\

    private final GameBoard     board;
    private final ScoringModule scoreModule;
    private final Dictionary    dictionary;

    // ========================================================================================== \\
    //                                       Constructor(s)                                       \\
    // ========================================================================================== \\

    /**
     * Constructs a MoveFinder for the specified board, dictionary, and scoring rules.
     *
     * @param gameBoard     the game board; must not be null
     * @param dictionary    the dictionary for validating words; must not be null
     * @param scoringModule the scoring module to compute move scores; must not be null
     * @throws NullPointerException if any argument is null
     */
    public MoveFinder(GameBoard gameBoard, Dictionary dictionary, ScoringModule scoringModule) {
        this.board       = Objects.requireNonNull(gameBoard,     "GameBoard cannot be null");
        this.dictionary  = Objects.requireNonNull(dictionary,    "Dictionary cannot be null");
        this.scoreModule = Objects.requireNonNull(scoringModule, "ScoringModule cannot be null");
    }

    // ========================================================================================== \\
    //                                          Getters                                           \\
    // ========================================================================================== \\

    /**
     * Returns the underlying game board instance.
     *
     * @return the {@link GameBoard} in use
     */
    GameBoard getBoard() {
        return board;
    }

    /**
     * Returns the scoring rules used to score each move.
     *
     * @return the {@link ScoringModule} in use
     */
    ScoringModule getScoringModule() {
        return scoreModule;
    }

    // ========================================================================================== \\
    //                                        API Methods                                         \\
    // ========================================================================================== \\

    /**
     * Returns a finder over a copy of this finder's board, sharing the same dictionary and scoring
     * rules. A search on the copy leaves this finder's board untouched.
     *
     * @return a new {@code MoveFinder} over a deep copy of the board
     */
    public MoveFinder copy() {
        return new MoveFinder(board.clone(), dictionary, scoreModule);
    }

    /**
     * Generates and scores all valid moves for the given rack letters.
     *
     * @param rackLetters the letters on the rack, with {@link TileSet#WILDCARD} for a blank; must
     *                    not be null
     * @return a new list of distinct {@link Move} instances, highest score first; empty when the
     *         rack is empty or no move fits
     * @throws NullPointerException if {@code rackLetters} is null
     */
    public List<Move> getMoves(char[] rackLetters) {
        Objects.requireNonNull(rackLetters, "rackLetters cannot be null");
        var rack = new char[rackLetters.length];
        for (int i = 0; i < rack.length; i++) {
            rack[i] = Character.toUpperCase(rackLetters[i]);
        }
        Arrays.sort(rack);
        if (rack.length == 0) {
            return new ArrayList<>();
        }

        // Keyed by the tiles a move places, so a placement found along both axes is kept once.
        Map<String, Move> moves = new LinkedHashMap<>();

        boolean firstMove = board.isEmpty();
        for (boolean horizontal : new boolean[] {true, false}) {
            int lines  = horizontal ? board.getRows() : board.getCols();
            int length = horizontal ? board.getCols() : board.getRows();
            for (int line = 0; line < lines; line++) {
                for (int start = 0; start < length; start++) {
                    for (int end = start + 1; end < length; end++) {
                        searchSpan(line, start, end, horizontal, firstMove, rack, moves);
                    }
                }
            }
        }

        var result = new ArrayList<>(moves.values());
        Collections.sort(result);
        return result;
    }

    /**
     * Lists the words on the given board that the dictionary does not contain.
     *
     * @param gameBoard the board to check; must not be null
     * @return a new list of the unknown words, each once, in the order found; empty when every word
     *         is known
     * @throws NullPointerException if {@code gameBoard} is null
     */
    public List<String> findUnknownWords(GameBoard gameBoard) {
        Objects.requireNonNull(gameBoard, "GameBoard cannot be null");
        return gameBoard.getWords()
                        .stream()
                        .filter(word -> !dictionary.isWord(word))
                        .distinct()
                        .toList();
    }

    // ========================================================================================== \\
    //                                       Helper Methods                                       \\
    // ========================================================================================== \\

    /**
     * Tries every word that fits one span of cells, from {@code start} to {@code end} inclusive
     * along one row or column, and adds the valid moves to {@code moves}.
     */
    private void searchSpan(int line, int start, int end, boolean horizontal, boolean firstMove,
                            char[] rack, Map<String, Move> moves) {
        int limit = horizontal ? board.getCols() : board.getRows();
        // A word ends where the letters end, so the cells either side of the span must be empty.
        if (start > 0 && hasLetter(line, start - 1, horizontal)) return;
        if (end < limit - 1 && hasLetter(line, end + 1, horizontal)) return;

        var pattern   = new char[end - start + 1];
        int emptyCells = 0;
        boolean touches = false;
        for (int i = 0; i < pattern.length; i++) {
            int pos = start + i;
            if (hasLetter(line, pos, horizontal)) {
                pattern[i] = letterAt(line, pos, horizontal);
                touches = true;
            } else {
                emptyCells++;
                touches |= hasCrossNeighbour(line, pos, horizontal);
            }
        }

        if (emptyCells == 0 || emptyCells > rack.length || emptyCells > GameBoard.MAX_NEW_TILES) return;
        if (firstMove ? !isCrossingCentre(line, start, end) : !touches) return;

        int row = horizontal ? line : start;
        int col = horizontal ? start : line;
        var candidates = new ArrayList<String>();
        fillPattern(pattern, 0, rack, new boolean[rack.length], new StringBuilder(), candidates);
        for (String word : candidates) {
            tryPlacement(word, row, col, horizontal, moves);
        }
    }

    /**
     * Builds every dictionary word that matches the pattern, filling each empty cell (a zero
     * character) with an unused rack letter. A blank fills a cell with a lowercase letter.
     */
    private void fillPattern(char[] pattern, int index, char[] rack, boolean[] used,
                             StringBuilder word, List<String> out) {
        if (index == pattern.length) {
            if (dictionary.isWord(word.toString())) {
                out.add(word.toString());
            }
            return;
        }

        if (pattern[index] != 0) {
            extendWord(pattern, index, pattern[index], rack, used, word, out);
            return;
        }

        for (int i = 0; i < rack.length; i++) {
            // The rack is sorted, so equal letters sit together; trying one of them is enough.
            if (used[i] || (i > 0 && rack[i] == rack[i - 1] && !used[i - 1])) continue;

            used[i] = true;
            if (rack[i] == TileSet.WILDCARD) {
                for (char letter = 'a'; letter <= 'z'; letter++) {
                    extendWord(pattern, index, letter, rack, used, word, out);
                }
            } else {
                extendWord(pattern, index, rack[i], rack, used, word, out);
            }
            used[i] = false;
        }
    }

    /**
     * Appends one letter to the word and carries on filling the pattern, as long as some
     * dictionary word still starts with the letters so far. The last letter goes straight to the
     * whole-word check.
     */
    private void extendWord(char[] pattern, int index, char letter, char[] rack, boolean[] used,
                            StringBuilder word, List<String> out) {
        word.append(letter);
        boolean lastLetter = index == pattern.length - 1;
        if (lastLetter || dictionary.isPrefix(word.toString())) {
            fillPattern(pattern, index + 1, rack, used, word, out);
        }
        word.setLength(word.length() - 1);
    }

    /**
     * Places the word on the board, keeps it as a move when every word it forms is in the
     * dictionary, and removes it from the board again.
     */
    private void tryPlacement(String word, int row, int col, boolean horizontal, Map<String, Move> moves) {
        if (board.placeWord(word, row, col, horizontal) && areNewWordsValid()) {
            int score = scoreModule.calculateScore(board);
            moves.putIfAbsent(describeNewTiles(), new Move(word, score, row, col, !horizontal));
        }
        board.reset();
    }

    /**
     * Checks that every word the new letters form exists in the dictionary.
     */
    private boolean areNewWordsValid() {
        for (String word : board.getNewWords()) {
            if (!dictionary.isWord(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Describes the new tiles on the board, one {@code row,col,letter} entry per tile in row order.
     * Two placements of the same tiles give the same description.
     */
    private String describeNewTiles() {
        var key = new StringBuilder();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.isNewLetter(r, c)) {
                    key.append(r).append(',').append(c).append(',')
                       .append(board.isWildCard(r, c) ? Character.toLowerCase(board.getLetterAt(r, c))
                                                      : board.getLetterAt(r, c))
                       .append(';');
                }
            }
        }
        return key.toString();
    }

    private boolean isCrossingCentre(int line, int start, int end) {
        int centre = board.getRows() / 2;
        return line == centre && start <= centre && end >= centre;
    }

    private boolean hasLetter(int line, int pos, boolean horizontal) {
        return horizontal ? board.hasLetterAt(line, pos) : board.hasLetterAt(pos, line);
    }

    private char letterAt(int line, int pos, boolean horizontal) {
        return horizontal ? board.getLetterAt(line, pos) : board.getLetterAt(pos, line);
    }

    /**
     * Checks whether the cell has a letter on either side across the line, which a tile placed in
     * it would join into a cross word.
     */
    private boolean hasCrossNeighbour(int line, int pos, boolean horizontal) {
        int lines = horizontal ? board.getRows() : board.getCols();
        return (line > 0 && hasLetter(line - 1, pos, horizontal))
            || (line < lines - 1 && hasLetter(line + 1, pos, horizontal));
    }

}
