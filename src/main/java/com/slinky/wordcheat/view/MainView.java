package com.slinky.wordcheat.view;

import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Kheagen Haskins
 */
public class MainView extends VBox {

    // ================================[ Static ]================================ \\
    
    // ================================[ Fields ]================================ \\
    private final BoardView   boardView;
    private final TileSetView setView;
    private final RackView    rackView;
    
    // =============================[ Constructors ]============================= \\
    public MainView(BoardView boardView, TileSetView setView, RackView rackView) {
        super(10);
        
        this.boardView = Objects.requireNonNull(boardView, "BoardView cannot be null");
        this.setView   = Objects.requireNonNull(setView,   "SetView cannot be null");
        this.rackView  = Objects.requireNonNull(rackView,  "RackView cannot be null");
        
        var boardAndSet = new HBox(10, boardView, setView);
        boardAndSet.setAlignment(Pos.CENTER);
        
        setPadding(new Insets(15, 5, 5, 5));
        getChildren().addAll(boardAndSet, rackView);
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    public TileSetView getPoolView() {
        return setView;
    }
    
    public RackView getRackView() {
        return rackView;
    }

    /**
     * Returns the current number of tiles in the rack.
     */
    public int getRackSize() {
        return rackView.getSize();
    }
    
    public int getBoardRows() {
        return boardView.getRows();
    }
    
    public int getBoardColumns() {
        return boardView.getCols();
    }
    
    public TileNode getBoardTile(int row, int col) {
        return boardView.getTile(row, col);
    }
    
    public TileNode getPoolTile(char letter) {
        return setView.getTile(letter);
    }
    
    public TileNode[] getBoardTiles() {
        return boardView.getAllTiles();
    }
    
    public TileNode[] getPoolTiles() {
        return setView.getAllTiles();
    }
    
    public TileNode[] getRackTiles() {
        return rackView.getTiles();
    }
    
    // =============================[ API Methods ]============================== \\
    public void updateTileCount(char letter, int count) {
        setView.updateCount(letter, count);
    }
    
    /**
     * Adds a single tile to the rack.
     *
     * @param letter the letter to add
     * @param score the score associated with the letter
     */
    public void addTileToRack(char letter, int score) {
        rackView.addTile(letter, score);
    }

    /**
     * Adds multiple tiles to the rack.
     *
     * @param letters array of letters to add
     * @param scores corresponding scores for each letter
     */
    public void addTilesToRack(char[] letters, int[] scores) {
        rackView.addTiles(letters, scores);
    }

    /**
     * Removes the first occurrence of the specified letter from the rack.
     *
     * @param letter the letter to remove
     * @return true if a tile was removed, false otherwise
     */
    public boolean removeTileFromRack(char letter) {
        return rackView.removeTile(letter);
    }

    /**
     * Removes and returns the letter at the specified index in the rack.
     *
     * @param index the position to remove
     * @return the removed letter
     */
    public TileNode removeTileAtIndex(int index) {
        return rackView.removeTileAt(index);
    }

    /**
     * Removes a number of tiles from the end of the rack.
     *
     * @param count how many tiles to remove
     */
    public void removeTilesFromRack(int count) {
        rackView.removeTiles(count);
    }

    public void emptyTile(TileNode tile) {
        boardView.emptyTile(tile);
    }
    
    public void updateBoard(char[][] letters, int[][] scores, String[][] bonuses) {
        boardView.updateBoard(letters, scores);
    }
    
    public void updateTileSet(int[] counts) {
        setView.updateCounts(counts);
    }
    
    public void updateRack(char[] letters, int[] scores) {
        rackView.updateRack(letters, scores);
    }

    // ============================[ Helper Methods ]============================ \\

    // ============================[ Helper Classes ]============================ \\
    
}