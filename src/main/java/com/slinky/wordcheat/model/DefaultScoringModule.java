package com.slinky.wordcheat.model;

import com.slinky.wordcheat.util.MainUtil;

/**
 * Default implementation of the {@code ScoringModule} interface.
 *
 * <p>
 * This class is responsible for calculating the score for a given board by
 * considering the point values of newly placed letters and the bonus
 * multipliers available on the board. It supports both custom and classic bonus
 * layouts, with the classic 15×15 layout being the default.
 * </p>
 *
 * <p>
 * The scoring algorithm evaluates the board by processing both rows and
 * columns. The logic for scoring rows and columns is now consolidated into a
 * single generic method that accepts a traversal strategy (horizontal or
 * vertical). In addition, the contiguous boundary detection logic has been
 * extracted into a common helper.
 * </p>
 *
 * @see    ScoringModule
 * @author Kheagen Haskins
 */
public class DefaultScoringModule implements ScoringModule {

    // ================================[ Static ]================================ \\
    /**
     * An array of point values for each letter from A to Z.
     * 
     * <p>
     * The value at index 0 corresponds to 'A', index 1 to 'B', and so on.
     * </p>
     */
    private static final int[] POINTS = {
        1, 4, 4, 2, 1, 4, 3, 3, 1, 10, // A - J
        5, 2, 4, 2, 1, 4, 10, 1, 1, 1, // K - T
        2, 5, 4, 8, 3, 10              // U - Z
    };
    
    /**
     * Retrieves the point value for the specified uppercase letter using the
     * internal scoring array.
     *
     * <p>
     * This method maps characters from 'A' to 'Z' to their corresponding point
     * values based on the game's scoring rules. Each letter's score is defined
     * in a static array where the index 0 corresponds to 'A', index 1 to 'B',
     * and so on up to 'Z'.
     * </p>
     *
     * @param c the uppercase character ('A'–'Z') for which to retrieve the
     *          point value.
     * @return the point value associated with the specified letter.
     * @throws IllegalArgumentException if the character is not in the range
     *                                  'A'–'Z'.
     */
    public static int getPointsOf(char c) {
        if (c == TileSet.WILDCARD) {
            return 0;
        }
        
        if (c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("Invalid character: " + c);
        }

        int index = c - 'A';
        return POINTS[index];
    }
    
    /**
     * Returns the frequency distribution of tiles based on their assigned score
     * values.
     *
     * <p>
     * The frequencies are returned in an array of eight integers corresponding
     * to the following score groups:
     * 
     * <ul>
     *   <li>Index 0: Wild card tiles (score 0) – the count of wild card
     *       tiles.</li>
     *   <li>Index 1: Tiles scoring 1 point (e.g. A, E, I, O, T, R, S).</li>
     *   <li>Index 2: Tiles scoring 2 points (e.g. D, N, L, U).</li>
     *   <li>Index 3: Tiles scoring 3 points (e.g. H, G, Y).</li>
     *   <li>Index 4: Tiles scoring 4 points (e.g. B, C, F, M, P, W).</li>
     *   <li>Index 5: Tiles scoring 5 points (e.g. V, K).</li>
     *   <li>Index 6: Tiles scoring 8 points (e.g. X).</li>
     *   <li>Index 7: Tiles scoring 10 points (e.g. Q, Z, J).</li>
     * </ul>
     * 
     * </p>
     *
     * <p>
     * Note that the natural ordering aligns with the above groups except for
     * indices 6 and 7, which correspond to the rare higher score tiles.
     * </p>
     *
     * @return an array of eight integers representing the frequency of tiles
     *         per score group.
     */
    public static int[] getScoreFrequencies() {
        return new int[]{2, 7, 4, 3, 6, 2, 1, 3};
    }

    // ================================[ Fields ]================================ \\
    private TileBonus[][] bonusTiles;

    // =============================[ Constructors ]============================= \\
    public DefaultScoringModule(TileBonus[][] bonusTiles) {
        this.bonusTiles = bonusTiles == null ? getClassicBonusLayout() : bonusTiles;
    }

    public DefaultScoringModule() {
        this.bonusTiles = getClassicBonusLayout();
    }

    /**
     * Generates the classic bonus layout for the board as used in many WWF
     * games.
     *
     * <p>
     * The layout is defined as a 15×15 matrix where specific positions are
     * assigned bonus types, such as Double Word, Triple Word, Double Letter,
     * and Triple Letter. The bonus layout is populated using symmetry to ensure
     * a balanced distribution across the board.
     * </p>
     *
     * @return a 15×15 {@code TileBonus} matrix representing the classic bonus
     *         layout.
     */
    public static TileBonus[][] getClassicBonusLayout() {
        var bonusMatrix = new TileBonus[15][15];

        bonusMatrix[3] [7] = TileBonus.DW;
        bonusMatrix[11][7] = TileBonus.DW;
        bonusMatrix[7] [3] = TileBonus.DW;
        bonusMatrix[7][11] = TileBonus.DW;

        // Populate the board using symmetry.
        MainUtil.setSym(bonusMatrix, 0, 3, TileBonus.TW);
        MainUtil.setSym(bonusMatrix, 1, 2, TileBonus.DL);
        MainUtil.setSym(bonusMatrix, 2, 1, TileBonus.DL);
        MainUtil.setSym(bonusMatrix, 3, 0, TileBonus.TW);

        MainUtil.setSym(bonusMatrix, 0, 6, TileBonus.TL);
        MainUtil.setSym(bonusMatrix, 1, 5, TileBonus.DW);
        MainUtil.setSym(bonusMatrix, 2, 4, TileBonus.DL);
        MainUtil.setSym(bonusMatrix, 3, 3, TileBonus.TL);
        MainUtil.setSym(bonusMatrix, 4, 2, TileBonus.DL);
        MainUtil.setSym(bonusMatrix, 5, 1, TileBonus.DW);
        MainUtil.setSym(bonusMatrix, 6, 0, TileBonus.TL);

        MainUtil.setSym(bonusMatrix, 4, 6, TileBonus.DL);
        MainUtil.setSym(bonusMatrix, 5, 5, TileBonus.TL);
        MainUtil.setSym(bonusMatrix, 6, 4, TileBonus.DL);

        return bonusMatrix;
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Calculates the total score for the specified board.
     *
     * <p>
     * The score is determined by evaluating both rows and columns that contain
     * new letters, applying the appropriate multipliers, and summing the
     * resulting values. A bonus score is added if the number of new tiles
     * equals the maximum allowed ("Bingo").
     * </p>
     *
     * @param board the {@code GameBoard} representing the current state of the
     *              game.
     * @return the calculated total score.
     */
    @Override
    public int calculateScore(GameBoard board) {
        int newTileCount = board.getNewTileCount();
        if (newTileCount == 0) {
            return 0;
        }
        
        int score = scoreRows(board) + scoreColumns(board);
        return score + (newTileCount == GameBoard.MAX_NEW_TILES ? BINGO : 0);
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Finds the contiguous boundaries for a word along a line.
     *
     * <p>
     * When {@code horizontal} is {@code true}, searches left and right from the
     * starting column in the given row. Otherwise, reaches upward and downward
     * from the starting row in the given column.
     * </p>
     *
     * @param board      the {@code GameBoard} being scored.
     * @param fixedIndex the fixed row (if horizontal) or fixed column (if
     *                   vertical).
     * @param start      the starting index along the variable axis.
     * @param horizontal {@code true} for horizontal search; {@code false} for
     *                   vertical search.
     * @return an array of two integers: index 0 is the lower boundary, index 1
     *         is the upper boundary.
     */
    private int[] findBoundaries(GameBoard board, int fixedIndex, int start, boolean horizontal) {
        int lower = start;
        int upper = start;
        if (horizontal) {
            while (lower > 0 && board.hasLetterAt(fixedIndex, lower - 1)) {
                lower--;
            }
            while (upper < board.getCols() - 1 && board.hasLetterAt(fixedIndex, upper + 1)) {
                upper++;
            }
        } else {
            while (lower > 0 && board.hasLetterAt(lower - 1, fixedIndex)) {
                lower--;
            }
            while (upper < board.getRows() - 1 && board.hasLetterAt(upper + 1, fixedIndex)) {
                upper++;
            }
        }
        
        return new int[]{lower, upper};
    }

    // --- Consolidated Line Scoring Method ---
    /**
     * Computes the score for a contiguous word along a single line (row or
     * column).
     *
     * <p>
     * For horizontal scoring, {@code fixedIndex} is the row and {@code start}
     * is the starting column. For vertical scoring, {@code fixedIndex} is the
     * column and {@code start} is the starting row. If the starting cell does
     * not contain a new letter, or if the contiguous word consists of a single
     * letter, the method returns 0.
     * </p>
     *
     * @param board      the {@code GameBoard} being scored.
     * @param fixedIndex the fixed index (row if horizontal, column if
     *                   vertical).
     * @param start      the starting index along the variable axis.
     * @param horizontal {@code true} for horizontal scoring; {@code false} for
     *                   vertical scoring.
     * @return the calculated score for the contiguous word along that line from
     *         the given anchor point.
     */
    private int scoreLine(GameBoard board, int fixedIndex, int start, boolean horizontal) {
        int lineScore  = 0;
        int multiplier = 0;

        if (horizontal) {
            if (!board.hasLetterAt(fixedIndex, start) || !board.isNewLetter(fixedIndex, start)) {
                return 0;
            }
            
            int[] boundaries = findBoundaries(board, fixedIndex, start, true);
            if (boundaries[0] == boundaries[1]) {
                return 0;
            }
            
            for (int c = boundaries[0]; c <= boundaries[1]; c++) {
                int tileScore = scoreTile(board, fixedIndex, c);
                if (board.isNewLetter(fixedIndex, c)) {
                    multiplier += getWordMultiplier  (fixedIndex, c);
                    tileScore  *= getLetterMultiplier(fixedIndex, c);
                }
                
                lineScore += tileScore;
            }
        } else {
            if (!board.hasLetterAt(start, fixedIndex) || !board.isNewLetter(start, fixedIndex)) {
                return 0;
            }
            
            int[] boundaries = findBoundaries(board, fixedIndex, start, false);
            if (boundaries[0] == boundaries[1]) {
                return 0;
            }
            
            for (int r = boundaries[0]; r <= boundaries[1]; r++) {
                int tileScore = scoreTile(board, r, fixedIndex);
                if (board.isNewLetter(r, fixedIndex)) {
                    multiplier += getWordMultiplier(r, fixedIndex);
                    tileScore *= getLetterMultiplier(r, fixedIndex);
                }
                
                lineScore += tileScore;
            }
        }
        
        return lineScore * Math.max(1, multiplier);
    }

    /**
     * Computes the total score for horizontal words containing new letters.
     *
     * @param board the {@code GameBoard} to be scored.
     * @return the accumulated horizontal score.
     */
    private int scoreRows(GameBoard board) {
        int topBound  = board.getNewRowLowerBound();
        int leftBound = board.getNewColLowerBound();
        int score = 0;
        for (int r = topBound; r < board.getRows(); r++) {
            score += scoreLine(board, r, leftBound, true);
        }
        
        return score;
    }

    /**
     * Computes the total score for vertical words containing new letters.
     *
     * @param board the {@code GameBoard} to be scored.
     * @return the accumulated vertical score.
     */
    private int scoreColumns(GameBoard board) {
        int leftBound = board.getNewColLowerBound();
        int topBound  = board.getNewRowLowerBound();
        int score     = 0;
        for (int c = leftBound; c < board.getCols(); c++) {
            score += scoreLine(board, c, topBound, false);
        }
        
        return score;
    }

    /**
     * Computes the score for the letter at the specified position.
     *
     * @param board the {@code GameBoard} containing the letter.
     * @param row   the row index.
     * @param col   the column index.
     * @return the score for the letter at that position.
     */
    private int scoreTile(GameBoard board, int row, int col) {
        if (board.isWildCard(row, col)) {
            return 0;
        }
        
        return getLetterPoints(board.getLetterAt(row, col));
    }
    
    /**
     * Retrieves the point value for a given letter.
     *
     * <p>
     * The method converts the input letter to uppercase and then computes its
     * index based on the ASCII value of 'A'. The corresponding point value from
     * the {@code POINTS} array is then returned.
     * </p>
     *
     * @param letter the letter for which to retrieve the point value.
     * @return the point value associated with the specified letter.
     * @throws ArrayIndexOutOfBoundsException if the letter is not in the range
     * A-Z.
     */
    public int getLetterPoints(char letter) {
        letter    = Character.toUpperCase(letter);
        int index = letter - 'A';
        return POINTS[index];
    }
    
    /**
     * Retrieves the word multiplier from the bonus layout at the specified
     * position.
     *
     * @param row the row index in the bonus layout.
     * @param col the column index in the bonus layout.
     * @return the bonus multiplier for the word at that position, or 0 if none
     *         applies.
     */
    private int getWordMultiplier(int row, int col) {
        if (row >= bonusTiles.length || col >= bonusTiles[0].length) {
            return 0;
        }
        var bonus = bonusTiles[row][col];
        if (bonus != null) {
            return switch (bonus) {
                case DW, TW ->
                    bonus.value;
                default ->
                    0;
            };
        }
        return 0;
    }

    /**
     * Retrieves the letter multiplier from the bonus layout at the specified
     * position.
     *
     * @param row the row index in the bonus layout.
     * @param col the column index in the bonus layout.
     * @return the bonus multiplier for the letter at that position, or 1 if
     *         none applies.
     */
    private int getLetterMultiplier(int row, int col) {
        if (row >= bonusTiles.length || col >= bonusTiles[0].length) {
            return 1;
        }
        
        var bonus = bonusTiles[row][col];
        if (bonus != null) {
            return switch (bonus) {
                case DL, TL ->
                    bonus.value;
                default ->
                    1;
            };
        }
        return 1;
    }

}