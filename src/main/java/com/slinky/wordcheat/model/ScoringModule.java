package com.slinky.wordcheat.model;

/**
 * Functional interface for scoring modules.
 * 
 * <p>
 * This interface allows different scoring strategies to be swapped in or even
 * multiple modules to be chained. The module takes a {@code Board} as input and
 * calculates its score.
 * </p>
 *
 * @see Board
 */
@FunctionalInterface
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
    
}