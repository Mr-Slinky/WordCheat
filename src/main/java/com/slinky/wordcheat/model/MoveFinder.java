package com.slinky.wordcheat.model;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.WordGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@code WordFinder} class is the central component responsible for
 * identifying and generating a list of possible word suggestions that may be
 * played on a game board given a set of available letters.
 *
 * <p>
 * This class leverages a provided dictionary to confirm the validity of words
 * and a word generator to produce candidate words from a given letter rack. It
 * performs exhaustive searches for both horizontal and vertical word
 * placements, and additionally examines anchor positions where words may
 * connect with pre-existing letters on the board.
 * </p>
 *
 * <p>
 * This class uses a brute force approach with many micro-optimisations aimed at
 * reducing the search space and amount of computations performed, such as
 * binary searches, row and column skipping
 * </p>
 *
 * @author Kheagen Haskins
 */
public class MoveFinder {

    // ================================[ Fields ]================================ \\
    /**
     * The game board representing the current state of the board including
     * placed letters.
     */
    private final GameBoard board;

    /**
     * The scoring module used to calculate the score for word placements.
     */
    private final ScoringModule scoreModule;

    /**
     * The dictionary used to validate words.
     */
    private Dictionary dictionary;

    /**
     * The word generator used to produce candidate words from the available
     * letters.
     */
    private WordGenerator wordGen;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code WordFinder} with the specified game board,
     * dictionary and scoring module.
     *
     * <p>
     * The constructor initialises the {@code WordFinder} with the given
     * parameters and creates an instance of the word generator, restricting
     * candidate word lengths to between 2 and 15 characters.
     * </p>
     *
     * @param gameBoard     the game board representing the current state of the
     *                      board; must not be {@code null}.
     * @param dictionary    the dictionary used for validating words; must not be
     *                      {@code null}.
     * @param scoringModule the scoring module for computing the score of word
     *                      placements; must not be {@code null}.
     * @throws NullPointerException if any of the provided parameters are
     *                              {@code null}.
     */
    public MoveFinder(GameBoard gameBoard, Dictionary dictionary, ScoringModule scoringModule) {
        this.board       = Objects.requireNonNull(gameBoard,     "GameMatrix cannot be null");
        this.dictionary  = Objects.requireNonNull(dictionary,    "Dictionary cannot be null");
        this.scoreModule = Objects.requireNonNull(scoringModule, "ScoreModule cannot be null");

        wordGen = new WordGenerator(this.dictionary, 2, 15);
    }
    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Retrieves a direct copy of the of game board.
     * 
     * @return a direct copy of the of game board.
     */
    GameBoard getBoard() {
        return board;
    }
    
    
    // =============================[ API Methods ]============================== \\
    /**
     * Retrieves a comprehensive list of word suggestions based on the available
     * letters provided by the letter rack.
     *
     * <p>
     * This method performs an exhaustive search across the game board,
     * analysing possible horizontal and vertical placements as well as anchor
     * positions where new words can connect with existing letters. It combines
     * candidate words generated from the letter rack with potential placements
     * on the board, validating each word using the provided dictionary and
     * calculating its score with the scoring module.
     * </p>
     *
     * @param rack the {@code LetterRack} containing the available letters for
     *             generating candidate words.
     * @return a {@code List} of {@code Suggestion} objects representing valid
     *         word placements along with their scores.
     */
    public List<Move> getMoves(LetterRack rack) {
        return exhaustiveFind(rack);
    }

    /**
     * Finds the best first move when the board is empty using a seven-letter
     * rack. It generates all moves from the rack (without filtering by word
     * length) and tests each move so that it intersects cell (7, 7). The move
     * with the highest score is returned.
     *
     * @param rack the LetterRack containing the available seven letters.
     * @return the best Move that intersects cell (7, 7), or {@code null} if no
     *         valid move is found.
     * @throws IllegalStateException if the board is not empty.
     */
    public Move getFirstMove(LetterRack rack) {
        // Ensure the board is empty before attempting a first move.
        if (!board.isEmpty()) {
            throw new IllegalStateException("Board must be empty for the first move.");
        }

        // Generate all candidate words from the seven-letter rack.
        List<String> candidateWords = wordGen.generateAllWords(rack.getLetters());
        if (candidateWords == null || candidateWords.isEmpty()) {
            return null;
        }

        List<Move> validMoves = new ArrayList<>();
        // Define the centre cell coordinates (row 7, column 7).
        final int centreRow = 7;
        final int centreCol = 7;

        // Try horizontal placements.
        // For a horizontal placement, the word is placed on row 7 and must extend across column 7.
        for (String word : candidateWords) {
            // The starting column is chosen so that the placement covers the centre column.
            int startCmin = Math.max(0, centreCol - word.length() + 1);
            int startCmax = Math.min(centreCol, board.getCols() - word.length());
            for (int c = startCmin; c <= startCmax; c++) {
                // Verify that column 7 lies within the word's span.
                if (c <= centreCol && (c + word.length() > centreCol)) {
                    if (board.placeWord(word, centreRow, c, true)) { // 'true' indicates horizontal placement.
                        if (board.areAllWordsValid(dictionary)) {
                            int score = scoreModule.calculateScore(board);
                            if (score > 0) {
                                validMoves.add(new Move(word, score, centreRow, c, false));
                            }
                        }
                    }
                    
                    board.reset();
                }
            }
        }

        // Try vertical placements.
        // For a vertical placement, the word is placed in column 7 and must span row 7.
        for (String word : candidateWords) {
            // The starting row is chosen so that the placement covers the centre row.
            int startRmin = Math.max(0, centreRow - word.length() + 1);
            int startRmax = Math.min(centreRow, board.getRows() - word.length());
            for (int r = startRmin; r <= startRmax; r++) {
                // Verify that row 7 lies within the word's span.
                if (r <= centreRow && (r + word.length() > centreRow)) {
                    if (board.placeWord(word, r, centreCol, false)) { // 'false' indicates vertical placement.
                        if (board.areAllWordsValid(dictionary)) {
                            int score = scoreModule.calculateScore(board);
                            if (score > 0) {
                                validMoves.add(new Move(word, score, r, centreCol, true));
                            }
                        }
                    }
                    
                    board.reset();
                }
            }
        }

        // If no valid move could be found, return null.
        if (validMoves.isEmpty()) {
            return null;
        }

        // Choose and return the move with the highest score.
        Move bestMove = validMoves.get(0);
        for (Move m : validMoves) {
            if (m.score() > bestMove.score()) {
                bestMove = m;
            }
        }
        
        return bestMove;
    }


    
    // ============================[ Helper Methods ]============================ \\
    /**
     * Performs an exhaustive search for valid word placements based on the
     * available letters.
     *
     * @param rack the {@code LetterRack} containing the available letters.
     * @return a {@code List} of {@code Suggestion} objects representing all
     *         valid word placements found.
     */
    private List<Move> exhaustiveFind(LetterRack rack) {
        List<Move> suggestions = new ArrayList<>();
        var words = wordGen.generateAllWords(rack.getLetters());
        // Strategy one: try all placements for each candidate word.
        for (String word : words) {
            suggestions.addAll(horizontalPlacements(word));
            suggestions.addAll(verticalPlacements(word));
        }

        suggestions.addAll(verticalAnchorFind(rack));
        suggestions.addAll(horizontalAnchorFind(rack));
        
        Collections.sort(suggestions);
        
        return suggestions;
    }

    /**
     * Searches for all valid horizontal placements of the given word on the
     * game board.
     *
     * @param word the candidate word to be placed horizontally.
     * @return a {@code List} of {@code Suggestion} objects representing valid
     *         horizontal placements.
     */
    private List<Move> horizontalPlacements(String word) {
        List<Move> suggestions = new ArrayList<>();

        boolean vertical = false; // Horizontal placement
        int rows         = board.getRows();
        int cols         = board.getCols();
        for (int r = 0; r < rows; r++) {
            boolean has      = board.rowHasLetters(r);
            boolean hasAbove = r > 0 && board.rowHasLetters(r - 1);
            boolean hasBelow = r < rows - 1 && board.rowHasLetters(r + 1);
            if (!(has || hasAbove || hasBelow)) continue;

            for (int c = 0; c <= cols - word.length(); c++) {
                if (board.placeWord(word, r, c, !vertical)) {
                    if (board.areAllWordsValid(dictionary)) {
                        addMove(word, r, c, vertical, suggestions);
                    }
                }

                board.reset();
            }
        }

        return suggestions;
    }

    /**
     * Searches for all valid vertical placements of the given word on the game
     * board.
     *
     * @param word the candidate word to be placed vertically.
     * @return a {@code List} of {@code Suggestion} objects representing valid
     *         vertical placements.
     */
    private List<Move> verticalPlacements(String word) {
        List<Move> suggestions = new ArrayList<>();
        
        int rows = board.getRows();
        int cols = board.getCols();

        boolean vertical = true; // Indicates vertical placements.
        for (int c = 0; c < cols; c++) {
            boolean has      = board.colHasLetters(c);
            boolean hasLeft  = c > 0 && board.colHasLetters(c - 1);
            boolean hasRight = c < cols - 1 && board.colHasLetters(c + 1);
            if (!(has || hasLeft || hasRight)) continue;
            // For vertical placements, iterate over possible starting rows.
            for (int r = 0; r <= rows - word.length(); r++) {
                if (board.placeWord(word, r, c, !vertical) && board.areAllWordsValid(dictionary)) {
                    addMove(word, r, c, vertical, suggestions);
                }

                board.reset();
            }
        }

        return suggestions;
    }

    /**
     * Adds a new word suggestion to the list of suggestions and updates the
     * highest scoring word if necessary.
     *
     * @param word        the word to be added as a suggestion.
     * @param row         the starting row index (zero-based) for the word placement.
     * @param col         the starting column index (zero-based) for the word placement.
     * @param vertical    {@code true} if the word is placed vertically,
     *                    {@code false} if horizontally.
     * @param suggestions the list of suggestions to which the new suggestion
     *                    will be added.
     */
    private void addMove(String word, int row, int col, boolean vertical, List<Move> suggestions) {
        int score = scoreModule.calculateScore(board);
        if (score <= 0) {
            return;
        }
        
        var sug = new Move(word, score, row, col, vertical);
        suggestions.add(sug);
    }

    /**
     * Searches for valid horizontal word placements by analysing anchor
     * positions on the game board.
     *
     * @param rack the {@code LetterRack} containing the available letters.
     * @return a {@code List} of {@code Suggestion} objects representing valid
     *         horizontal placements found via anchors.
     */
    private List<Move> horizontalAnchorFind(LetterRack rack) {
        List<Move> suggestions = new ArrayList<>();

        for (int r = 0; r < board.getRows(); r++) {
            // Skip empty rows.
            if (!board.rowHasLetters(r)) {
                continue;
            }

            for (int c = 0; c < board.getCols(); c++) {
                // Skip empty columns.
                if (!board.colHasLetters(c)) {
                    continue;
                }
                // If a letter exists to the left, this prefix has already been explored.
                if (c > 0 && board.hasLetterAt(r, c - 1)) {
                    continue;
                }

                if (board.hasLetterAt(r, c)) {
                    String substring = horizontalSubstringFrom(r, c);
                    rack.addLetters(substring);
                    var candidates = wordGen.generateAllWords(rack.getLetters())
                            .stream()
                            .parallel()
                            .filter(word -> word.contains(substring) && !word.equalsIgnoreCase(substring))
                            .collect(Collectors.toList());
                    for (String candidate : candidates) {
                        testAndAddHorizontalCandidate(candidate, substring, r, c, suggestions);
                    }

                    rack.remove(substring.length());
                }
            }
        }

        return suggestions;
    }

    /**
     * Tests a horizontal candidate word for validity and adds it as a
     * suggestion if appropriate.
     *
     * @param word        the candidate word to be tested.
     * @param substring   the substring extracted from the board that must be
     *                    contained in the candidate word.
     * @param row         the row index of the anchor cell.
     * @param col         the column index of the anchor cell.
     * @param suggestions the list of suggestions to which a valid candidate
     *                    will be added.
     */
    private void testAndAddHorizontalCandidate(String word, String substring, int row, int col, List<Move> suggestions) {
        int substringStart = word.indexOf(substring);
        int c = col - substringStart;

        // Test if the word can fit within the board boundaries.
        if (c < 0) {
            return;
        } else if (c + word.length() > board.getCols()) {
            return;
        }

        if (board.placeWord(word, row, c, true)) {
            if (board.areAllWordsValid(dictionary)) {
                addMove(word, row, c, false, suggestions);
            }
        }

        board.reset();
    }

    /**
     * Extracts a horizontal substring starting from the specified cell on the
     * board.
     *
     * @param row the row index (zero-based) from which to begin extraction.
     * @param col the column index (zero-based) from which to begin extraction.
     * @return the contiguous horizontal substring starting at the specified
     *         cell.
     */
    private String horizontalSubstringFrom(int row, int col) {
        StringBuilder prefix = new StringBuilder();

        int r = row;
        int c = col;
        while (board.hasLetterAt(r, c)) {
            prefix.append(board.getLetterAt(r, c++));
            if (c == board.getCols()) {
                return prefix.toString();
            }
        }

        return prefix.toString();
    }

    /**
     * Searches for valid vertical word placements by analysing anchor positions
     * on the game board.
     *
     * @param rack the {@code LetterRack} containing the available letters.
     * @return a {@code List} of {@code Suggestion} objects representing valid
     *         vertical placements found via anchors.
     */
    private List<Move> verticalAnchorFind(LetterRack rack) {
        List<Move> suggestions = new ArrayList<>();

        int rows = board.getRows();
        int cols = board.getCols();

        for (int c = 0; c < cols; c++) {
            // Skip columns that are completely empty.
            if (!board.colHasLetters(c)) {
                continue;
            }

            for (int r = 0; r < rows; r++) {
                // Skip rows that are empty in this column.
                if (!board.rowHasLetters(r)) {
                    continue;
                }
                // If there is a letter above, the vertical prefix has already been explored.
                if (r > 0 && board.hasLetterAt(r - 1, c)) {
                    continue;
                }

                    if (board.hasLetterAt(r, c)) {
                    String substring = verticalSubstringFrom(r, c);
                    rack.addLetters(substring);
                    var candidates = wordGen.generateAllWords(rack.getLetters())
                            .stream()
                            .parallel()
                            .filter(word -> word.contains(substring) && !word.equalsIgnoreCase(substring))
                            .collect(Collectors.toList());
                    for (String candidate : candidates) {
                        testAndAddVerticalCandidate(candidate, substring, r, c, suggestions);
                    }

                    rack.remove(substring.length());
                }
            }
        }

        return suggestions;
    }

    /**
     * Tests a vertical candidate word for validity and adds it as a suggestion
     * if appropriate.
     *
     * <p>
     * The method adjusts the starting row based on the location of the provided
     * substring within the candidate word. It then verifies that the candidate
     * word fits vertically on the board and that the placement is valid
     * according to the dictionary. If the candidate word passes these checks, a
     * new suggestion is added.
     * </p>
     *
     * @param word        the candidate word to test.
     * @param substring   the vertical substring extracted from the board that
     *                    must be contained in the candidate word.
     * @param row         the row index of the anchor cell.
     * @param col         the column index of the anchor cell.
     * @param suggestions the list of suggestions to which a valid candidate
     *                    will be added.
     */
    private void testAndAddVerticalCandidate(String word, String substring, int row, int col, List<Move> suggestions) {
        int substringStart = word.indexOf(substring);
        // Adjust the starting row based on the substring's position.
        int r = row - substringStart;

        if (word.equalsIgnoreCase("mojito")) {
            System.out.println("");
        }

        // Check if the word fits vertically.
        if (r < 0 || r + word.length() > board.getRows()) {
            return;
        }

        // Attempt to place the word vertically (horizontal flag set to false).
        if (board.placeWord(word, r, col, false)) {
            if (board.areAllWordsValid(dictionary)) {
                addMove(word, r, col, true, suggestions);
            }
        }

        board.reset();
    }

    /**
     * Extracts a contiguous vertical substring starting from the specified cell
     * on the board.
     *
     * @param row the row index (zero-based) from which to begin extraction.
     * @param col the column index (zero-based) from which to begin extraction.
     * @return the contiguous vertical substring starting at the specified cell.
     */
    private String verticalSubstringFrom(int row, int col) {
        StringBuilder prefix = new StringBuilder();
        int r = row;
        while (r < board.getRows() && board.hasLetterAt(r, col)) {
            prefix.append(board.getLetterAt(r, col));
            r++;
        }

        return prefix.toString();
    }

}