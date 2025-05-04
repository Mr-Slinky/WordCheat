package com.slinky.wordcheat.model;

/**
 * Interface for scoring modules.
 * 
 * <p>
 * This interface allows different scoring strategies to be swapped in or even
 * multiple modules to be chained. The module takes a {@code Board} as input and
 * calculates its score.
 * 
 * @see GameBoard
 */
public interface ScoringModule {
    
    /**
     * The bonus score awarded for placing all 7 tiles in one move.
     */
    public static final int BINGO = 35;
    
    /**
     * Calculates the score for the given board.
     *
     * @param board the {@code Board} instance to calculate the score for.
     * @return the total score based on the board's current state.
     */
    int calculateScore(GameBoard board);
    
    /**
     * Retrieves the point value for the specified letter using the internal
     * scoring array.
     *
     * <p>
     * This method maps characters from 'A' to 'Z' to their corresponding point
     * values based on the game's scoring rules. 
     * 
     * @param c the uppercase character ('A'–'Z') for which to retrieve the
     *          point value.
     * @return the point value associated with the specified letter.
     * @throws IllegalArgumentException if the character is not in the range
     *                                  'A'–'Z'.
     */
    int getPointsOf(char c);
    
}