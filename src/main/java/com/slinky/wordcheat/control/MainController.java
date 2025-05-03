package com.slinky.wordcheat.control;

import com.slinky.wordcheat.io.GameEngineSnapshot;
import com.slinky.wordcheat.language.OxfordDictionary;

import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.TileBonus;

import com.slinky.wordcheat.util.MatrixUtils;

import com.slinky.wordcheat.view.BoardView;
import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.RackView;
import com.slinky.wordcheat.view.TileSetView;

import java.io.IOException;

import javafx.scene.layout.Pane;

import static com.slinky.wordcheat.io.Persistence.loadGame;
import static com.slinky.wordcheat.io.Persistence.saveExists;
import static com.slinky.wordcheat.io.Persistence.saveGame;
import com.slinky.wordcheat.view.TileNode;
import java.util.Arrays;

/**
 * The GameController class serves as the central coordinator for a WordCheat
 * game session. It encapsulates the lifecycle of the game engine, manages
 * persistence of game state, and constructs the JavaFX view hierarchy, wiring
 * together the board, tile set, and rack views.
 *
 * <p>
 * <strong>Responsibilities:</strong>
 * <ul>
 * <li>Initialise the game engine, either by loading a saved state or creating a
 * new one.</li>
 * <li>Set up and configure the main JavaFX view components (board, tile set,
 * rack).</li>
 * <li>Handle persistence operations: saving, loading, and snapshot-based
 * resetting of game state.</li>
 * <li>Delegate drag-and-drop interactions to the DnDController for user
 * moves.</li>
 * <li>Provide utility methods for suggesting top moves to the console.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>
 *   GameController controller = new GameController("game.dat");
 *   Pane mainPane = controller.getMainView();
 *   // Attach mainPane to a JavaFX Scene
 * </pre>
 * </p>
 * <p>
 * <strong>Thread Safety:</strong> This class is not thread-safe and must be
 * used solely on the JavaFX Application Thread.
 * </p>
 *
 * @author Kheagen Haskins
 * @since 1.0
 * @version 1.0
 */
public final class MainController {

    // ================================[ Fields ]================================
    private int           saveVersion;
    private String        filename;
    private GameEngine    engine;
    private MainView      view;
    private DnDController dndController;

    // =============================[ Constructors ]=============================
    /**
     * Creates a new GameController, initializing the game state from the
     * specified file or creating a new game if none exists.
     * 
     * <p>
     * After construction, the main view is configured and drag-and-drop
     * controls are enabled.
     * </p>
     * 
     * @param filename the path to the file used for persisting game state
     */
    public MainController(String filename) {
        this.filename    = filename;
        this.saveVersion = 1;
        initEngine();
        initMainView();
        dndController = new DnDController(engine, view);
        dndController.configure();
    }

    // ============================[ Accessor Methods ]==========================
    /**
     * Returns the filename associated with this controller's persistent
     * storage.
     *
     * @return the persistence filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Provides the main JavaFX pane representing the game UI.
     *
     * @return a Pane containing the board, tile set, and rack views
     */
    public Pane getMainView() {
        return view;
    }

    // =============================[ API Methods ]==============================
    /**
     * Saves the current game state to disk and updates the in-memory snapshot.
     *
     * @throws IOException if an I/O error occurs during saving
     */
    public void save() throws IOException {
        saveGame(engine, filename);
    }

    /**
     * Loads the game state from disk, replacing the current engine instance.
     * 
     * <p>
     * Does not modify the lastSnapshot; use reset() to revert to the previously
     * saved state.
     * </p>
     * 
     * @throws IOException if an I/O error occurs during loading
     */
    public void load() throws IOException {
        this.engine = loadGame(filename);
    }

    /**
     * Computes and prints the top five scoring moves to the console, showing
     * both the move description and a preview of the resulting board state.
     */
    public void printTopFiveMoves() {
        var moves = engine.getAllMoves();
        int limit = Math.min(5, moves.size());
        for (int i = 0; i < limit; i++) {
            var topMove = moves.get(i);
            System.out.println(topMove);
            System.out.println(engine.previewMove(topMove));
        }
    }

    // ============================[ Helper Methods ]============================
    /**
     * Creates a new GameEngine instance with a fresh board, dictionary, and
     * scoring module.
     * 
     * <p>
     * This method is used when no persisted game file exists or loading fails.
     * </p>
     *
     * @param filename the filename to associate with the new game session
     * @return a newly initialized GameEngine ready for play
     */
    private GameEngine initNewEngine() {
        GameBoard gameBoard   = new GameBoard(new char[15][15]);
        MoveFinder moveFinder = new MoveFinder(
                gameBoard,
                new OxfordDictionary(),
                new DefaultScoringModule()
        );
        return new GameEngine(new DefaultTileSet(), moveFinder);
    }

    /**
     * Initialises the GameEngine by loading from persistence if available, or
     * creating a new engine otherwise. Updates the in-memory snapshot.
     */
    private void initEngine() {
        try {
            engine = saveExists(filename) ? loadGame(filename) : initNewEngine();
        } catch (IOException e) {
            engine = initNewEngine();
        }
    }

    /**
     * Builds and configures the MainView, including BoardView, TileSetView, and
     * RackView, based on the current engine state.
     */
    private void initMainView() {
        var counts      = MatrixUtils.shiftLeft(engine.getRemainingTileCounts(), 1);
        var rackLetters = engine.getRackLetters();

        var boardView   = new BoardView(engine.getMatrix(), getScoreMatrix(), getBonusMatrix());
        var tileSetView = new TileSetView(counts);
        var rackView    = new RackView(rackLetters, engine.getRackScores(), 7);

        view = new MainView(boardView, tileSetView, rackView);
        view.setOnResetAction (ev -> syncToBackend());
        view.setOnCommitAction(ev -> syncToFrontend());
    }

    /**
     * Constructs a 2D array of tile scores corresponding to the current board
     * matrix.
     *
     * @return a matrix of integer scores for each board position
     */
    private int[][] getScoreMatrix() {
        char[][] matrix = engine.getMatrix();
        int rows        = matrix.length;
        int cols        = matrix[0].length;
        int[][] scores  = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                scores[r][c] = engine.getScoreOf(matrix[r][c]);
            }
        }

        return scores;
    }

    /**
     * Generates a matrix of bonus labels (e.g., "TW", "DL") for display,
     * matching the classic WWF bonus layout.
     *
     * @return a 2D array of bonus strings or null for standard tiles
     */
    private String[][] getBonusMatrix() {
        var bonusLayout = DefaultScoringModule.getClassicBonusLayout();
        int rows        = bonusLayout.length;
        int cols        = bonusLayout[0].length;
        String[][] bonuses = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileBonus bonus = bonusLayout[r][c];
                bonuses[r][c] = (bonus == null ? null : bonus.toString());
            }
        }

        return bonuses;
    }
    
    /**
     * The opposite of a reset. This method flows data from the frontend to the backend.
     */
    private void syncToFrontend() {
        int rows = view.getBoardRows();
        int cols = view.getBoardColumns();
        char[][] letterMatrix = new char[rows][cols];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                letterMatrix[r][c] = view.getBoardTile(r, c).getLetter();
            }
        }
        
        scoped:
        {
            boolean successful;
            String errMessage;
            TileNode[] tiles = view.getRackTiles();
            int[] codes = Arrays.stream(tiles)
                                .mapToInt(TileNode::getLetter)
                                .toArray();
            char[] letters = new char[codes.length];
            for (int i = 0; i < codes.length; i++) {
                letters[i] = (char) codes[i];
            }

            successful = engine.updateAllAndPreserve(letterMatrix, letters);
            errMessage = successful ? "No Error" : "Invalid Board State";

            if (!successful) {
                System.out.println("Error committing: " + errMessage); // Display error in GUI maybe?
            }
        }
        
        view.graduateNewTiles();
        dndController.configure();
        try {
            saveGame(engine, filename);
        } catch (IOException ex) {
            System.out.println("Error saving to disk: " + ex.getMessage());
        }
    }

    /**
     * Resets the game to the last in-memory snapshot, restoring board, tile
     * set, and rack. This method flows data from the backend to the frontend.
     * 
     * <p>
     * Reconfigures drag-and-drop controls for the restored engine.
     * </p>
     * 
     * @throws IllegalStateException if no snapshot is available (save() has not
     *                               been called)
     */
    private void syncToBackend() {
        int[] scores = MatrixUtils.shiftLeft(engine.getRemainingTileCounts(), 1);

        view.updateBoard(engine.getMatrix(), getScoreMatrix(), getBonusMatrix());
        view.updateTileSet(scores);
        view.updateRack(engine.getRackLetters(), engine.getRackScores());

        dndController.configure();
    }

}