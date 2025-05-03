package com.slinky.wordcheat.view;

import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The primary layout container for the WordCheat UI, arranging the game board,
 * tile set, and player rack in a coherent and responsive visual structure.
 * 
 * <p>
 * The board and tile set are displayed side by side within a horizontally
 * aligned HBox, centred within the view. Below this, the rack of letter
 * tiles is presented in a separate section. Padding and spacing values
 * are applied to ensure consistent margins and gaps between elements.
 * </p>
 * 
 * <p>
 * Through its public API, MainView provides methods to query the current
 * board dimensions, retrieve individual TileNode instances from the board,
 * tile set, or rack, and update the view to reflect changes in the game
 * state, including tile counts, board contents, and rack contents.
 * </p>
 * 
 * <p>
 * Example usage:
 * <pre>
 *     var counts      = MatrixUtils.shiftLeft(engine.getRemainingTileCounts(), 1);
       var rackLetters = engine.getRackLetters();

       var boardView   = new BoardView(engine.getMatrix(), getScoreMatrix(), getBonusMatrix());
       var tileSetView = new TileSetView(counts);
       var rackView    = new RackView(rackLetters, engine.getRackScores(), rackLetters.length);
        
       MainView view   = new MainView(boardView, tileSetView, rackView);
 * </pre>
 * </p>
 *
 * @author Kheagen Haskins
 * @since  1.0
 */
public class MainView extends VBox {

    // ================================[ Fields ]================================ \\
    private final BoardView   boardView;
    private final TileSetView setView;
    private final RackView    rackView;
    
    private final Button btnBack;
    private final Button btnForward;
    private final Button btnReset;
    private final Button btnShowMove;
    private final Button btnNextMove;
    private final Button btnCommit;
    
    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a MainView with the specified BoardView, TileSetView, and RackView.
     *
     * @param boardView the view displaying the game board; must not be null
     * @param setView   the view displaying the available tile set; must not be null
     * @param rackView  the view displaying the player's rack; must not be null
     * @throws NullPointerException if any parameter is null
     */
    public MainView(BoardView boardView, TileSetView setView, RackView rackView) {
        super(10);
        
        this.boardView = Objects.requireNonNull(boardView, "BoardView cannot be null");
        this.setView   = Objects.requireNonNull(setView,   "SetView cannot be null");
        this.rackView  = Objects.requireNonNull(rackView,  "RackView cannot be null");

        // Top Buttons
        this.btnBack     = new SimpleButton("<--");
        this.btnForward  = new SimpleButton("-->");
        
        // Bottom Buttons
        this.btnReset    = new SimpleButton("Reset");
        this.btnShowMove = new SimpleButton("Best Move");
        this.btnNextMove = new SimpleButton("Next Move");
        this.btnCommit   = new SimpleButton("Commit");
        
        HBox buttonBarBottom = new HBox(12, btnReset, btnShowMove, btnNextMove, btnCommit);
        buttonBarBottom.setAlignment(Pos.CENTER);
        buttonBarBottom.setPadding(new Insets(5));
        
        var boardAndSet = new HBox(10, boardView, setView);
        boardAndSet.setAlignment(Pos.CENTER);
        
        setPadding(new Insets(15, 5, 5, 5));
        getChildren().addAll(boardAndSet, rackView, buttonBarBottom);
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns the view managing the pool of remaining tiles.
     *
     * @return the TileSetView instance
     */
    public TileSetView getPoolView() {
        return setView;
    }
    
    /**
     * Returns the view managing the player's rack of tiles.
     *
     * @return the RackView instance
     */
    public RackView getRackView() {
        return rackView;
    }

    /**
     * Retrieves the current number of tiles in the rack.
     *
     * @return the number of tiles in the rack
     */
    public int getRackSize() {
        return rackView.getSize();
    }
    
    /**
     * Retrieves the number of rows on the game board.
     *
     * @return the board row count
     */
    public int getBoardRows() {
        return boardView.getRows();
    }
    
    /**
     * Retrieves the number of columns on the game board.
     *
     * @return the board column count
     */
    public int getBoardColumns() {
        return boardView.getCols();
    }
    
    /**
     * Retrieves the TileNode at the specified board coordinates.
     *
     * @param row the zero-based row index
     * @param col the zero-based column index
     * @return the TileNode at the given position
     */
    public TileNode getBoardTile(int row, int col) {
        return boardView.getTile(row, col);
    }
    
    /**
     * Retrieves the TileNode for the given letter from the tile set view.
     *
     * @param letter the letter of the tile to retrieve
     * @return the corresponding TileNode
     */
    public TileNode getPoolTile(char letter) {
        return setView.getTile(letter);
    }
    
    /**
     * Returns all TileNode instances currently displayed on the board.
     *
     * @return array of all board TileNodes
     */
    public TileNode[] getBoardTiles() {
        return boardView.getAllTiles();
    }
    
    /**
     * Returns all TileNode instances in the tile set view.
     *
     * @return array of all pool TileNodes
     */
    public TileNode[] getPoolTiles() {
        return setView.getAllTiles();
    }
    
    /**
     * Returns all TileNode instances currently in the rack view.
     *
     * @return array of rack TileNodes
     */
    public TileNode[] getRackTiles() {
        var tiles     = new TileNode[rackView.getSize()];
        var rackTiles = rackView.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = rackTiles[i];
        }
        
        return tiles;
    }
    
    // =============================[ API Methods ]============================== \\
    /**
     * Updates the displayed count for a specific letter in the tile set.
     *
     * @param letter the letter whose count to update
     * @param count  the new count value
     */
    public void updateTileCount(char letter, int count) {
        setView.updateCount(letter, count);
    }
    
    /**
     * Adds a single letter tile to the rack.
     *
     * @param letter the letter to add
     * @param score  the score associated with the letter
     */
    public void addTileToRack(char letter, int score) {
        rackView.addTile(letter, score);
    }

    /**
     * Adds multiple letter tiles to the rack in one operation.
     *
     * @param letters array of letters to add
     * @param scores  corresponding scores for each letter
     */
    public void addTilesToRack(char[] letters, int[] scores) {
        rackView.addTiles(letters, scores);
    }

    /**
     * Removes the first occurrence of the specified letter from the rack.
     *
     * @param letter the letter to remove
     * @return true if removal succeeded; false otherwise
     */
    public boolean removeTileFromRack(char letter) {
        return rackView.removeTile(letter);
    }

    /**
     * Removes and returns the TileNode at the specified index in the rack.
     *
     * @param index the index of the tile to remove
     * @return the removed TileNode
     */
    public TileNode removeTileAtIndex(int index) {
        return rackView.removeTileAt(index);
    }

    /**
     * Removes a specified number of tiles from the end of the rack.
     *
     * @param count how many tiles to remove
     */
    public void removeTilesFromRack(int count) {
        rackView.removeTiles(count);
    }

    /**
     * Clears the specified tile on the board, making it empty.
     *
     * @param tile the TileNode to clear
     */
    public void emptyTile(TileNode tile) {
        boardView.emptyTile(tile);
    }
    
    /**
     * Updates the board view to match the provided letter and score arrays.
     *
     * @param letters two-dimensional array of board letters
     * @param scores  two-dimensional array of letter scores
     * @param bonuses two-dimensional array of bonus identifiers (currently unused)
     */
    public void updateBoard(char[][] letters, int[][] scores, String[][] bonuses) {
        boardView.updateBoard(letters, scores);
    }
    
    /**
     * Updates the tile set view with the provided counts for each letter.
     *
     * @param counts integer array representing remaining tile counts
     */
    public void updateTileSet(int[] counts) {
            setView.updateCounts(counts);
        }
    
    /**
     * Synchronises the rack view with the provided letters and scores.
     *
     * @param letters array of letters currently in rack
     * @param scores  corresponding score values
     */
    public void updateRack(char[] letters, int[] scores) {
        rackView.updateRack(letters, scores);
    }
    
    public void graduateNewTiles() {
        for (TileNode tile : boardView.getAllTiles()) {
            if (tile.isNewlyPlaced()) {
                tile.setNewlyPlaced(false);
                tile.syncView();
            }
        }
    }
    
    /**
     * Assigns the handler to be invoked when Reset is clicked.
     */
    public void setOnResetAction(EventHandler<ActionEvent> handler) {
        btnReset.setOnAction(handler);
    }

    /**
     * Assigns the handler to be invoked when Best Move is clicked.
     */
    public void setOnShowMoveAction(EventHandler<ActionEvent> handler) {
        btnShowMove.setOnAction(handler);
    }

    /**
     * Assigns the handler to be invoked when Next Move is clicked.
     */
    public void setOnNextMoveAction(EventHandler<ActionEvent> handler) {
        btnNextMove.setOnAction(handler);
    }
    
    /**
     * Assigns the handler to be invoked when Commit is clicked.
     */
    public void setOnCommitAction(EventHandler<ActionEvent> handler) {
        btnCommit.setOnAction(handler);
    }
    
}