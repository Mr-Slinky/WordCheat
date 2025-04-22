package com.slinky.wordcheat.model;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.WordGenerator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates valid Scrabble move suggestions based on the current board state
 * and an array of available letters.
 * 
 * <p>
 * <code>MoveFinder</code> uses a {@link Dictionary} to verify word validity
 * and a {@link WordGenerator} to enumerate candidate words. It supports both
 * straight placements and anchor-based extensions in horizontal or vertical
 * orientation. Generated moves are scored via a {@link ScoringModule} and
 * returned in descending order of score.
 * </p>
 *
 * @see GameBoard
 * @see ScoringModule
 * @see Dictionary
 * @see WordGenerator
 */
public class MoveFinder {

    // =============================[ Fields ]============================== \\
    private final GameBoard     board;
    private final ScoringModule scoreModule;
    private final Dictionary    dictionary;
    private final WordGenerator wordGen;

    // ==========================[ Constructors ]=========================== \\
    /**
     * Constructs a MoveFinder for the specified board, dictionary, and scoring
     * rules.
     *
     * @param gameBoard     the game board; must not be null
     * @param dictionary    the dictionary for validating words; must not be null
     * @param scoringModule the scoring module to compute move scores; must not
     *                      be null
     * @throws NullPointerException if any argument is null
     */
    public MoveFinder(GameBoard gameBoard,
                      Dictionary dictionary,
                      ScoringModule scoringModule) {
        this.board       = Objects.requireNonNull(gameBoard,     "GameBoard cannot be null");
        this.dictionary  = Objects.requireNonNull(dictionary,    "Dictionary cannot be null");
        this.scoreModule = Objects.requireNonNull(scoringModule, "ScoringModule cannot be null");
        int maxDim = Math.max(gameBoard.getCols(), gameBoard.getRows());
        this.wordGen     = new WordGenerator(this.dictionary, 2, maxDim);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the underlying game board instance.
     *
     * @return the {@link GameBoard} in use
     */
    GameBoard getBoard() {
        return board;
    }

    ScoringModule getScoringModule() {
        return scoreModule;
    }
    
    // =============================[ API Methods ]============================== \\
    /**
     * Generates and scores all valid moves for the given rack letters.
     * <p>
     * Considers both linear placements and anchor-based extensions in
     * horizontal and vertical orientations. Results are sorted descending by score.
     * </p>
     *
     * @param rackLetters the array of available letters; must not be null
     * @return a sorted list of valid {@link Move} instances (highest score first)
     * @throws NullPointerException if {@code rackLetters} is null
     */
    public List<Move> getMoves(char[] rackLetters) {
        Objects.requireNonNull(rackLetters, "rackLetters cannot be null");
        List<Move> suggestions = new ArrayList<>();
        for (String word : wordGen.generateAllWords(rackLetters)) {
            suggestions.addAll(linearPlacements(word, true));   // horizontal
            suggestions.addAll(linearPlacements(word, false));  // vertical
        }
        suggestions.addAll(anchorPlacements(rackLetters, true));
        suggestions.addAll(anchorPlacements(rackLetters, false));
        Collections.sort(suggestions);
        return suggestions;
    }

    /**
     * Determines the best opening move when the board is empty.
     * <p>
     * Places each candidate word so it crosses the central cell, validates
     * board crossings, scores them, and returns the top move.
     * </p>
     *
     * @param rackLetters an array of seven letters for the first move; must not
     *                    be null
     * @return the highest-scoring {@link Move} intersecting center, or null if
     *         none found
     * @throws NullPointerException if {@code rackLetters} is null
     * @throws IllegalStateException if the board is not empty
     */
    public Move getFirstMove(char[] rackLetters) {
        Objects.requireNonNull(rackLetters, "rackLetters cannot be null");
        if (!board.isEmpty()) {
            throw new IllegalStateException("Board must be empty for the first move.");
        }
        List<String> candidateWords = wordGen.generateAllWords(rackLetters);
        if (candidateWords.isEmpty()) {
            return null;
        }

        List<Move> validMoves = new ArrayList<>();
        int centre            = board.getCols() / 2;

        for (String word : candidateWords) {
            int len = word.length();
            int minC = Math.max(0, centre - len + 1);
            int maxC = Math.min(centre, board.getCols() - len);
            // horizontal
            for (int c = minC; c <= maxC; c++) {
                if (board.placeWord(word, centre, c, true) && areAllWordsValid()) {
                    int score = scoreModule.calculateScore(board);
                    if (score > 0) validMoves.add(new Move(word, score, centre, c, false));
                }
                board.reset();
            }
            // vertical
            int minR = Math.max(0, centre - len + 1);
            int maxR = Math.min(centre, board.getRows() - len);
            for (int r = minR; r <= maxR; r++) {
                if (board.placeWord(word, r, centre, false) && areAllWordsValid()) {
                    int score = scoreModule.calculateScore(board);
                    if (score > 0) validMoves.add(new Move(word, score, r, centre, true));
                }
                board.reset();
            }
        }

        if (validMoves.isEmpty()) {
            return null;
        }
        
        Collections.sort(validMoves);
        return validMoves.get(0);
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Performs straight-line (horizontal/vertical) placements of a single word.
     */
    private List<Move> linearPlacements(String word, boolean horizontal) {
        List<Move> moves = new ArrayList<>();
        int pLimit       = horizontal ? board.getRows() : board.getCols();
        int sLimit       = horizontal ? board.getCols() : board.getRows();

        for (int p = 0; p < pLimit; p++) {
            boolean hasLine = horizontal ? board.rowHasLetters(p) : board.colHasLetters(p);
            boolean hasBefore = p > 0 && (horizontal
                                     ? board.rowHasLetters(p - 1)
                                     : board.colHasLetters(p - 1));
            boolean hasAfter = p < pLimit - 1 && (horizontal
                                     ? board.rowHasLetters(p + 1)
                                     : board.colHasLetters(p + 1));
            if (!(hasLine || hasBefore || hasAfter)) continue;

            for (int s = 0; s <= sLimit - word.length(); s++) {
                int row = horizontal ? p : s;
                int col = horizontal ? s : p;
                if (board.placeWord(word, row, col, horizontal) && areAllWordsValid()) {
                    int score = scoreModule.calculateScore(board);
                    if (score > 0) moves.add(new Move(word, score, row, col, !horizontal));
                }
                board.reset();
            }
        }
        
        return moves;
    }

    /**
     * Performs anchor-based expansions using existing board letters.
     */
    private List<Move> anchorPlacements(char[] rackLetters, boolean horizontal) {
        List<Move> moves = new ArrayList<>();
        int pLimit       = horizontal ? board.getRows() : board.getCols();
        int sLimit       = horizontal ? board.getCols() : board.getRows();

        // Iterate over each row (if horizontal) or column (if vertical) that has letters
        for (int primaryIndex = 0; primaryIndex < pLimit; primaryIndex++) {
            if (!(horizontal ? board.rowHasLetters(primaryIndex) : board.colHasLetters(primaryIndex))) continue;

            // Scan along the line to find anchor points
            for (int secondaryIndex = 0; secondaryIndex < sLimit; secondaryIndex++) {
                // Skip positions directly following an existing letter to avoid duplicate extensions
                boolean hasLetterBefore = horizontal
                    ? (secondaryIndex > 0 && board.hasLetterAt(primaryIndex, secondaryIndex - 1))
                    : (secondaryIndex > 0 && board.hasLetterAt(secondaryIndex - 1, primaryIndex));
                if (hasLetterBefore) continue;

                // Check for an anchor letter at this position
                boolean isAnchor = horizontal
                    ? board.hasLetterAt(primaryIndex, secondaryIndex)
                    : board.hasLetterAt(secondaryIndex, primaryIndex);
                if (!isAnchor) continue;

                // Extract the contiguous anchor substring
                String anchor = extractAnchor(primaryIndex, secondaryIndex, horizontal);

                // Extend the rack letters with anchor letters for candidate generation
                char[] extendedRack = Arrays.copyOf(rackLetters, rackLetters.length + anchor.length());
                for (int i = 0; i < anchor.length(); i++) {
                    extendedRack[rackLetters.length + i] = anchor.charAt(i);
                }

                // Generate candidate words containing the anchor
                List<String> candidates = wordGen.generateAllWords(extendedRack).stream()
                    .filter(w -> w.contains(anchor) && !w.equalsIgnoreCase(anchor))
                    .collect(Collectors.toList());

                // Test each candidate for valid placement
                for (String cand : candidates) {
                    testAndAdd(cand, anchor, primaryIndex, secondaryIndex, horizontal, moves);
                }
            }
        }
        
        return moves;
    }

    /**
     * Extracts the contiguous anchor substring from the board starting at the
     * given line and position.
     */
    private String extractAnchor(int lineIndex, int startIndex, boolean horizontal) {
        StringBuilder sb = new StringBuilder();
        int limit        = horizontal ? board.getCols() : board.getRows();
        // Walk along the line until a blank cell is encountered
        for (int offset = startIndex; offset < limit; offset++) {
            boolean hasLetter = horizontal
                ? board.hasLetterAt(lineIndex, offset)
                : board.hasLetterAt(offset, lineIndex);
            if (!hasLetter) break;
            // Append the letter at the current position
            char ch = horizontal
                ? board.getLetterAt(lineIndex, offset)
                : board.getLetterAt(offset, lineIndex);
            sb.append(ch);
        }
        
        return sb.toString();
    }

    /**
     * Tests a candidate word containing an anchor and adds valid moves to the list.
     */
    private void testAndAdd(String word,
                            String anchor,
                            int lineIndex,
                            int anchorPos,
                            boolean horizontal,
                            List<Move> out) {
        // Determine how far back to place the word so that the anchor aligns
        int anchorStart = word.indexOf(anchor);
        int row = horizontal ? lineIndex : anchorPos - anchorStart;
        int col = horizontal ? anchorPos - anchorStart : lineIndex;
        // Skip invalid starting positions
        if (row < 0 || col < 0) return;

        // Validate that the word fits within board boundaries
        boolean fits = horizontal
            ? col + word.length() <= board.getCols()
            : row + word.length() <= board.getRows();
        if (!fits) return;

        // Attempt to place the word and verify validity
        if (board.placeWord(word, row, col, horizontal) && areAllWordsValid()) {
            int score = scoreModule.calculateScore(board);
            if (score > 0) out.add(new Move(word, score, row, col, !horizontal));
        }
        // Reset the board to its previous state
        board.reset();
    }
    
    /**
     * Checks that every word on the board exists in the dictionary.
     *
     * @return true if all board-extracted words are valid; false otherwise
     */
    private boolean areAllWordsValid() {
        for (String w : board.getWords()) {
            if (dictionary.search(w) < 0) {
                return false;
            }
        }
        
        return true;
    }
}