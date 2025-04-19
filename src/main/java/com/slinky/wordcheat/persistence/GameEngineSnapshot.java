package com.slinky.wordcheat.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.slinky.wordcheat.model.*;
import com.slinky.wordcheat.language.Dictionary;
import java.util.*;

/**
 * A serialisable snapshot of a {@link GameEngine}'s state at a moment in time.
 * <p>
 * This Data Transfer Object (DTO) captures the minimal information required
 * to persist and later restore a {@code GameEngine}.  It is designed to be
 * written/read as JSON via Jackson, but could equally be used with other
 * formats.
 * </p>
 * <p>
 * The snapshot contains:
 * <ul>
 *   <li>{@code boardMatrix}: the complete character grid representing the
 *       board's placement of tiles</li>
 *   <li>{@code rackLetters}: the exact sequence of tiles in the player's rack</li>
 *   <li>{@code tileCounts}: a mapping of each tile (A–Z plus wildcard) to its
 *       remaining count in the pool</li>
 *   <li>{@code timestamp}: the millisecond epoch when the snapshot was taken</li>
 * </ul>
 * </p>
 */
public class GameEngineSnapshot {

    // =============================[ Fields ]============================== \\
    /**
     * A deep copy of the game board, rows then columns, capturing every placed
     * character and empty cell ('\0' or space if not set).
     */
    private final char[][] boardMatrix;

    /**
     * Ordered list of characters currently in the player's rack.  Wildcards
     * appear as the designated {@code TileSet.WILDCARD} character.
     */
    private final List<Character> rackLetters;

    /**
     * Map from each tile character (A–Z plus wildcard) to how many remain
     * undrawn in the central tile pool.  This mirrors {@code TileSet}
     * counts at snapshot time.
     */
    private final Map<Character, Integer> tileCounts;

    /**
     * Epoch milliseconds when {@link #fromEngine(GameEngine)} was called.
     * Useful for autosaving, versioning or user feedback.
     */
    private final long timestamp;

    // ==========================[ Constructors ]=========================== \\

    /**
     * Primary constructor used by Jackson during deserialisation.
     *
     * @param boardMatrix  the saved board character matrix
     * @param rackLetters  the saved rack contents as a list
     * @param tileCounts   the saved counts of each tile in the pool
     * @param timestamp    when this snapshot was captured
     */
    @JsonCreator
    public GameEngineSnapshot(
        @JsonProperty("boardMatrix") char[][] boardMatrix,
        @JsonProperty("rackLetters") List<Character> rackLetters,
        @JsonProperty("tileCounts")  Map<Character,Integer> tileCounts,
        @JsonProperty("timestamp")   long timestamp
    ) {
        this.boardMatrix = boardMatrix;
        this.rackLetters = rackLetters;
        this.tileCounts  = tileCounts;
        this.timestamp   = timestamp;
    }

    /**
     * Create a new snapshot capturing the full state of the given {@code engine}.
     * <p>
     * This method reads from all public getters of {@link GameEngine}:
     * {@link GameEngine#getMatrix()},
     * {@link GameEngine#getLetters()},
     * {@link GameEngine#getRemainingTileCount(char)}, and
     * {@link GameEngine#getRemainingWildcardCount()}.
     * </p>
     *
     * @param engine  the live engine whose state should be captured
     * @return a fresh {@code GameEngineSnapshot}
     */
    public static GameEngineSnapshot fromEngine(GameEngine engine) {
        // 1. Capture board state
        char[][] M = engine.getMatrix();

        // 2. Capture rack letters in insertion order
        List<Character> rack = new ArrayList<>();
        for (char c : engine.getLetters()) {
            rack.add(c);
        }

        // 3. Capture tile pool counts A–Z
        Map<Character,Integer> counts = new LinkedHashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            counts.put(c, engine.getRemainingTileCount(c));
        }
        // Finally, include the wildcard count
        counts.put(TileSet.WILDCARD, engine.getRemainingWildcardCount());

        // 4. Snapshot timestamp
        long now = System.currentTimeMillis();
        return new GameEngineSnapshot(M, rack, counts, now);
    }

    /**
     * Reconstruct a {@link GameEngine} from this snapshot.
     * <p>
     * Steps:
     * <ol>
     *   <li>Build a {@link GameBoard} from {@code boardMatrix}.</li>
     *   <li>Create a fresh {@link DefaultTileSet}, then adjust it by removing
     *       or adding tiles until its public counts match {@code tileCounts}.</li>
     *   <li>Create a new {@link LetterRack} and call
     *       {@link LetterRack#addLetters(char...)} on the array form of
     *       {@code rackLetters}.</li>
     *   <li>Instantiate {@link MoveFinder} with the restored board,
     *       provided {@code Dictionary} and {@code ScoringModule}.</li>
     *   <li>Wrap all in a new {@link GameEngine} and return.</li>
     * </ol>
     * 
     * @param dict   the dictionary to validate words
     * @param scoringModule the module to compute move scores
     * @return a fully restored {@code GameEngine}
     * @throws NullPointerException if {@code dict} or {@code scoringModule}
     *         is null
     */
    public GameEngine toEngine(Dictionary dict,
                               ScoringModule scoringModule) {
        // 1. Restore GameBoard
        GameBoard board = new GameBoard(boardMatrix);

        // 2. Restore tile pool
        DefaultTileSet ts = new DefaultTileSet();
        for (var entry : tileCounts.entrySet()) {
            char letter      = entry.getKey();
            int desiredCount = entry.getValue();
            int currentCount = ts.getRemainingTileCount(letter);
            int delta = currentCount - desiredCount;

            if (delta > 0) {
                for (int i = 0; i < delta; i++) {
                    ts.removeLetter(letter);
                }
            } else if (delta < 0) {
                for (int i = 0; i < -delta; i++) {
                    ts.addLetter(letter);
                }
            }
        }

        // 3. Restore rack
        LetterRack rack = new LetterRack();
        char[] rackArr = new char[rackLetters.size()];
        for (int i = 0; i < rackArr.length; i++) {
            rackArr[i] = rackLetters.get(i);
        }
        rack.addLetters(rackArr);

        // 4. Recreate MoveFinder + GameEngine
        MoveFinder finder = new MoveFinder(board, dict, scoringModule);
        return new GameEngine(ts, finder, rack);
    }

    // ========================[ Accessor Methods ]========================= \\

    /** @return the saved board matrix */
    public char[][] getBoardMatrix()       { return boardMatrix; }
    /** @return the saved rack letters */
    public List<Character> getRackLetters(){ return rackLetters; }
    /** @return the saved tile counts */
    public Map<Character,Integer> getTileCounts() { return tileCounts; }
    /** @return the epoch ms when this snapshot was taken */
    public long getTimestamp()            { return timestamp; }
    
}