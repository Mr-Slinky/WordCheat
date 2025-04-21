package com.slinky.wordcheat.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.LetterRack;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.ScoringModule;
import com.slinky.wordcheat.model.TileSet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A serialisable snapshot of a {@link GameEngine}'s state—now including where
 * wildcards were placed on the board.
 *
 * <p>Captures exactly:
 * <ul>
 *   <li>{@link #boardMatrix}: the 2D array of letters on the board</li>
 *   <li>{@link #wildcardPositions}: list of [row,col] pairs for each wildcard</li>
 *   <li>{@link #rackLetters}: the rack contents as a simple char[]</li>
 *   <li>{@link #tileCounts}: remaining tile counts in the pool</li>
 *   <li>{@link #timestamp}: epoch ms when snapshot was taken</li>
 * </ul>
 * </p>
 */
public class GameEngineSnapshot {

    /**
     * full copy of the board’s character grid (rows × cols)
     */
    private final char[][] boardMatrix;
    /**
     * each element is a two‑int array [row, col] marking a wildcard cell
     */
    private final int[][] wildcardPositions;
    /**
     * the rack contents in order, wildcards as TileSet.WILDCARD
     */
    private final char[] rackLetters;
    /**
     * counts of each tile (A→Z plus wildcard) remaining in the pool
     */
    private final Map<Character, Integer> tileCounts;
    /**
     * when this snapshot was captured (System.currentTimeMillis())
     */
    private final long timestamp;

    /**
     * Used by Jackson to rehydrate from JSON.
     *
     * @param boardMatrix       saved board grid
     * @param wildcardPositions saved wildcard [row,col] pairs
     * @param rackLetters       saved rack as char[]
     * @param tileCounts        saved pool counts
     * @param timestamp         when snapshot was taken
     */
    @JsonCreator
    public GameEngineSnapshot(
        @JsonProperty("boardMatrix")       char[][]               boardMatrix,
        @JsonProperty("wildcardPositions") int[][]                wildcardPositions,
        @JsonProperty("rackLetters")       char[]                 rackLetters,
        @JsonProperty("tileCounts")        Map<Character,Integer> tileCounts,
        @JsonProperty("timestamp")         long                   timestamp
    ) {
        this.boardMatrix       = boardMatrix;
        this.wildcardPositions = wildcardPositions;
        this.rackLetters       = rackLetters;
        this.tileCounts        = tileCounts;
        this.timestamp         = timestamp;
    }
    
    /**
     * @return the saved board matrix
     */
    public char[][] getBoardMatrix() {
        return boardMatrix;
    }

    /**
     * @return the saved wildcard positions (row,col pairs)
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
     * @return the saved tile‑pool counts
     */
    public Map<Character, Integer> getTileCounts() {
        return tileCounts;
    }

    /**
     * @return when the snapshot was taken
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Snapshots a live {@link GameEngine}, including wildcard positions.
     *
     * @param engine the engine to capture
     * @return a new snapshot ready for JSON serialization
     */
    public static GameEngineSnapshot fromEngine(GameEngine engine) {
        // 1) board letters
        char[][] matrix = engine.getMatrix();

        // 2) wildcard positions
        //    getWildCardPositions() returns int[][] of [row,col] pairs
        int[][] wilds = engine.getWildCardPositions();

        // 3) rack contents
        char[] rack = engine.getLetters();

        // 4) tile‑pool counts
        Map<Character,Integer> counts = new LinkedHashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            counts.put(c, engine.getRemainingTileCount(c));
        }
        counts.put(TileSet.WILDCARD, engine.getRemainingWildcardCount());

        // 5) timestamp
        long now = System.currentTimeMillis();

        return new GameEngineSnapshot(matrix, wilds, rack, counts, now);
    }

    /**
     * Rebuilds a {@link GameEngine} from this snapshot.
     * <ol>
     *   <li>Reconstructs the {@link GameBoard}.</li>
     *   <li>Creates a fresh {@link DefaultTileSet} and lets the
     *       {@link GameEngine} constructor remove board‑placed letters.</li>
     *   <li>Creates a {@link LetterRack} from {@link #rackLetters}.</li>
     *   <li>Instantiates a {@link MoveFinder} and wraps up in {@link GameEngine}.</li>
     *   <li>Replays each saved wildcard by calling
     *       {@code engine.setWildCardPosition(row,col,index)}.</li>
     * </ol>
     *
     * @param dict          dictionary for word validation
     * @param scoringModule scoring rules
     * @return the restored GameEngine
     * @throws NullPointerException if dict or scoringModule is null
     */
    public GameEngine toEngine(Dictionary dict,
                               ScoringModule scoringModule) {
        // 1) board
        GameBoard board = new GameBoard(boardMatrix);

        // 2) tile‑pool + rack
        DefaultTileSet ts = new DefaultTileSet();
        LetterRack     lr = new LetterRack(rackLetters);

        // 3) finder + engine (removes board letters from ts)
        MoveFinder mf     = new MoveFinder(board, dict, scoringModule);
        GameEngine engine = new GameEngine(ts, mf, lr);

        // 4) replay wildcards (using index = array order)
        for (int i = 0; i < wildcardPositions.length; i++) {
            int row = wildcardPositions[i][0];
            int col = wildcardPositions[i][1];
            engine.setWildCardPosition(row, col, i);
        }

        return engine;
    }

}
