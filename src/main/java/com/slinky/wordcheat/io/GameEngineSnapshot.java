package com.slinky.wordcheat.io;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.LetterRack;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.ScoringModule;

import java.util.Objects;

/**
 * A serialisable snapshot of a {@link GameEngine}'s state, including where the blank tiles sit on
 * the board.
 *
 * <p>Captures exactly:
 * <ul>
 *   <li>{@link #boardMatrix}: the 2D array of letters on the board</li>
 *   <li>{@link #wildcardPositions}: one {@code [row, col]} pair for each blank on the board</li>
 *   <li>{@link #rackLetters}: the rack contents as a simple char[]</li>
 *   <li>{@link #timestamp}: epoch ms when the snapshot was taken</li>
 * </ul>
 *
 * <p>
 * The tile pool is left out, since {@link #toEngine(Dictionary, ScoringModule)} rebuilds it from
 * the board.
 *
 * @author Kheagen Haskins
 */
public class GameEngineSnapshot {

    /**
     * full copy of the board's character grid (rows × cols)
     */
    private final char[][] boardMatrix;
    /**
     * each element is a two-int array [row, col] marking a blank on the board
     */
    private final int[][] wildcardPositions;
    /**
     * the rack contents in order, blanks as TileSet.WILDCARD
     */
    private final char[] rackLetters;
    /**
     * when this snapshot was captured (System.currentTimeMillis())
     */
    private final long timestamp;

    /**
     * Used by Jackson to rehydrate from JSON.
     *
     * <p>
     * A save written by version 0.1.0 may list unused blank slots as {@code [-1, -1]}, and may
     * leave {@code wildcardPositions} out. {@link #toEngine(Dictionary, ScoringModule)} skips the
     * unused slots, and a missing list counts as empty.
     *
     * @param boardMatrix       saved board grid
     * @param wildcardPositions saved blank [row,col] pairs; may be null
     * @param rackLetters       saved rack as char[]; may be null
     * @param timestamp         when snapshot was taken
     */
    @JsonCreator
    public GameEngineSnapshot(
        @JsonProperty("boardMatrix")       char[][] boardMatrix,
        @JsonProperty("wildcardPositions") int[][]  wildcardPositions,
        @JsonProperty("rackLetters")       char[]   rackLetters,
        @JsonProperty("timestamp")         long     timestamp
    ) {
        this.boardMatrix       = Objects.requireNonNull(boardMatrix, "boardMatrix is missing from the save");
        this.wildcardPositions = wildcardPositions == null ? new int[0][] : wildcardPositions;
        this.rackLetters       = rackLetters == null ? new char[0] : rackLetters;
        this.timestamp         = timestamp;
    }

    /**
     * @return the saved board matrix
     */
    public char[][] getBoardMatrix() {
        return boardMatrix;
    }

    /**
     * @return the saved blank positions (row,col pairs)
     */
    public int[][] getWildcardPositions() {
        return wildcardPositions;
    }

    /**
     * @return the saved rack letters
     */
    public char[] getRackLetters() {
        return rackLetters;
    }

    /**
     * @return when the snapshot was taken
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Snapshots a live {@link GameEngine}, including blank positions.
     *
     * @param engine the engine to capture
     * @return a new snapshot ready for JSON serialization
     */
    public static GameEngineSnapshot fromEngine(GameEngine engine) {
        return new GameEngineSnapshot(
            engine.getMatrix(),
            engine.getWildCardPositions(),
            engine.getRackLetters(),
            System.currentTimeMillis()
        );
    }

    /**
     * Rebuilds a {@link GameEngine} from this snapshot.
     * <ol>
     *   <li>Reconstructs the {@link GameBoard} and marks each saved blank on it.</li>
     *   <li>Creates a fresh {@link DefaultTileSet} and lets the {@link GameEngine} constructor
     *       remove the board's tiles, a blank tile for each blank.</li>
     *   <li>Creates a {@link LetterRack} from {@link #rackLetters}.</li>
     *   <li>Instantiates a {@link MoveFinder} and wraps up in {@link GameEngine}.</li>
     * </ol>
     *
     * <p>
     * A saved blank position outside the board, or on an empty cell, is skipped.
     *
     * @param dict          dictionary for word validation
     * @param scoringModule scoring rules
     * @return the restored GameEngine
     * @throws NullPointerException     if dict or scoringModule is null
     * @throws IllegalArgumentException if the saved board or rack holds invalid content
     * @throws IllegalStateException    if the saved board holds more of a tile than the game has
     */
    public GameEngine toEngine(Dictionary dict, ScoringModule scoringModule) {
        GameBoard board = new GameBoard(boardMatrix);
        for (int[] position : wildcardPositions) {
            if (isOnLetter(board, position)) {
                board.setWildCardPosition(position[0], position[1]);
            }
        }

        MoveFinder finder = new MoveFinder(board, dict, scoringModule);
        return new GameEngine(new DefaultTileSet(), finder, new LetterRack(rackLetters));
    }

    /**
     * Checks that a saved blank position lies on the board and on a letter.
     */
    private static boolean isOnLetter(GameBoard board, int[] position) {
        return position != null
            && position.length == 2
            && position[0] >= 0 && position[0] < board.getRows()
            && position[1] >= 0 && position[1] < board.getCols()
            && board.hasLetterAt(position[0], position[1]);
    }

}
