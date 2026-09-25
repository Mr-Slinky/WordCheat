package com.slinky.wordcheat.control;

import static com.slinky.wordcheat.io.Persistence.backupSave;
import static com.slinky.wordcheat.io.Persistence.loadGame;
import static com.slinky.wordcheat.io.Persistence.saveExists;
import static com.slinky.wordcheat.io.Persistence.saveGame;

import com.slinky.wordcheat.language.OxfordDictionary;

import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.Move;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.TileBonus;
import com.slinky.wordcheat.model.TileSet;

import com.slinky.wordcheat.util.MatrixUtils;

import com.slinky.wordcheat.view.BoardView;
import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.RackView;
import com.slinky.wordcheat.view.TileNode;
import com.slinky.wordcheat.view.TileSetView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

/**
 * The MainController class serves as the central coordinator for a WordCheat
 * game session. It encapsulates the lifecycle of the game engine, manages
 * persistence of game state, and constructs the JavaFX view hierarchy, wiring
 * together the board, tile set, and rack views.
 *
 * <p>
 * <strong>Responsibilities:</strong>
 * <ul>
 * <li>Initialise the game engine, either by loading a saved state or creating a
 * new one. A save that cannot be read is renamed to a backup, and the player
 * is told.</li>
 * <li>Set up and configure the main JavaFX view components (board, tile set,
 * rack).</li>
 * <li>Commit the board and rack shown on screen to the engine, warn about
 * words the dictionary does not know, and save the game.</li>
 * <li>Find moves on a background thread and preview them on the board.</li>
 * <li>Delegate drag-and-drop interactions to the DnDController.</li>
 * </ul>
 *
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>
 *   MainController controller = new MainController("mom");
 *   Pane mainPane = controller.getMainView();
 *   // Attach mainPane to a JavaFX Scene
 * </pre>
 *
 * <p>
 * <strong>Thread Safety:</strong> This class is not thread-safe and must be
 * used solely on the JavaFX Application Thread. The move search runs on its
 * own copy of the board.
 *
 * @author  Kheagen Haskins
 * @since   1.0
 * @version 1.0
 */
public final class MainController {

    // ================================[ Static ]================================
    private static final int RACK_SIZE = 7;

    // ================================[ Fields ]================================
    private final String  filename;
    private GameEngine    engine;
    private MainView      view;
    private DnDController dndController;

    private List<Move> moves = List.of();
    private int currentMove  = -1;

    /** Counts the searches started, so a result from an outdated search is dropped. */
    private int searchGeneration = 0;

    /** Explains a save that could not be loaded; shown once the window is up. */
    private String loadProblem;

    // =============================[ Constructors ]=============================
    /**
     * Creates a new MainController, initialising the game state from the
     * specified save or creating a new game if none exists.
     *
     * <p>
     * After construction, the main view is configured, drag-and-drop controls
     * are enabled, and a move search has started.
     *
     * @param filename the base name of the save file, without ".json"
     * @throws NullPointerException if {@code filename} is {@code null}
     */
    public MainController(String filename) {
        this.filename = Objects.requireNonNull(filename, "filename must not be null");
        initEngine();
        initMainView();
        dndController = new DnDController(engine, view);
        showCommittedState();
        startMoveSearch();

        if (loadProblem != null) {
            Platform.runLater(() -> showError("Saved game not loaded", loadProblem));
        }
    }

    // ============================[ Accessor Methods ]==========================
    /**
     * Returns the base name of the save file used by this controller.
     *
     * @return the save file name, without ".json"
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

    // ============================[ Helper Methods ]============================ \\
    /**
     * Creates a new GameEngine instance with a fresh board, dictionary, and
     * scoring module.
     *
     * @return a newly initialised GameEngine ready for play
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
     * Initialises the GameEngine by loading the save if there is one, or
     * creating a new engine otherwise. A save that cannot be loaded is renamed
     * to a backup so the new game does not overwrite it.
     */
    private void initEngine() {
        if (!saveExists(filename)) {
            engine = initNewEngine();
            return;
        }

        try {
            engine = loadGame(filename);
        } catch (IOException ex) {
            engine      = initNewEngine();
            loadProblem = "WordCheat started a new game because the saved game could not be read.\n\n" + ex.getMessage();
            try {
                loadProblem += "\n\nThe old save is kept at " + backupSave(filename) + ".";
            } catch (IOException backupEx) {
                loadProblem += "\n\nThe old save could not be moved aside, so the next commit will replace it.";
            }
        }
    }

    /**
     * Builds and configures the MainView, including BoardView, TileSetView, and
     * RackView, and wires its buttons.
     */
    private void initMainView() {
        var counts      = MatrixUtils.shiftLeft(engine.getUnseenTileCounts(), 1);
        var letters     = engine.getMatrix();

        var boardView   = new BoardView(letters, buildScoreMatrix(letters, buildWildcardMatrix()), buildBonusMatrix());
        var tileSetView = new TileSetView(counts);
        var rackView    = new RackView(engine.getRackLetters(), engine.getRackScores(), RACK_SIZE);

        view = new MainView(boardView, tileSetView, rackView);
        view.setOnResetAction   (ev -> showCommittedState());
        view.setOnCommitAction  (ev -> commit());
        view.setOnShowMoveAction(ev -> showMove(0));
        view.setOnNextMoveAction(ev -> showMove(currentMove + 1));
    }

    /**
     * Generates a matrix of bonus labels (e.g., "TW", "DL") for display,
     * matching the classic WWF bonus layout.
     *
     * @return a 2D array of bonus strings or null for standard tiles
     */
    private String[][] buildBonusMatrix() {
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
     * Builds a matrix of point values for the given letters, with 0 wherever
     * the letter is a blank.
     *
     * @param letters   the board matrix of letters
     * @param wildcards {@code true} where the letter is a blank
     * @return a matrix of letter scores with the same dimensions as
     *         {@code letters}
     */
    private int[][] buildScoreMatrix(char[][] letters, boolean[][] wildcards) {
        int[][] scores = new int[letters.length][letters[0].length];
        for (int r = 0; r < letters.length; r++) {
            for (int c = 0; c < letters[r].length; c++) {
                scores[r][c] = wildcards[r][c] ? 0 : engine.getScoreOf(letters[r][c]);
            }
        }

        return scores;
    }

    /**
     * Builds a matrix marking where the engine's board holds blanks.
     *
     * @return a matrix with {@code true} at each blank
     */
    private boolean[][] buildWildcardMatrix() {
        var letters = engine.getMatrix();
        var blanks  = new boolean[letters.length][letters[0].length];
        for (int[] position : engine.getWildCardPositions()) {
            blanks[position[0]][position[1]] = true;
        }
        return blanks;
    }

    /**
     * Commits the board and rack shown on screen to the engine, then saves
     * the game and starts a new move search.
     *
     * <p>
     * When the board holds words the dictionary does not know, the player is
     * asked to confirm first. A board or rack the engine rejects, such as one
     * using more of a letter than the game has, is reported and left on screen
     * for the player to correct.
     */
    private void commit() {
        int rows = view.getBoardRows();
        int cols = view.getBoardColumns();
        var letters = new char[rows][cols];
        var blanks  = new ArrayList<int[]>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileNode tile = view.getBoardTile(r, c);
                letters[r][c] = tile.isEmpty() ? TileSet.BLANK_TILE : tile.getLetter();
                if (tile.isWildcard() && !tile.isEmpty()) {
                    blanks.add(new int[] {r, c});
                }
            }
        }

        TileNode[] rackTiles = view.getRackTiles();
        char[] rack = new char[rackTiles.length];
        for (int i = 0; i < rack.length; i++) {
            rack[i] = rackTiles[i].getLetter();
        }

        var unknown = engine.findUnknownWords(letters);
        if (!unknown.isEmpty() && !confirm("Unknown words",
                "These words are not in WordCheat's word list:\n\n" + String.join(", ", unknown)
                + "\n\nCommit the board anyway?")) {
            return;
        }

        try {
            engine.commit(letters, blanks.toArray(new int[0][]), rack);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError("Board not committed", ex.getMessage());
            return;
        }

        try {
            saveGame(engine, filename);
        } catch (IOException ex) {
            showError("Game not saved", "The board was committed, but the game could not be saved.\n\n" + ex.getMessage());
        }

        showCommittedState();
        startMoveSearch();
    }

    /**
     * Redraws the board, pool and rack from the engine, which clears any move
     * being previewed and any tiles dragged since the last commit.
     */
    private void showCommittedState() {
        var letters   = engine.getMatrix();
        var wildcards = buildWildcardMatrix();

        view.updateBoard(letters, buildScoreMatrix(letters, wildcards), wildcards);
        view.updateTileSet(MatrixUtils.shiftLeft(engine.getUnseenTileCounts(), 1));
        view.updateRack(engine.getRackLetters(), engine.getRackScores());

        currentMove = -1;
        dndController.configure();
    }

    /**
     * Starts finding moves for the committed board and rack on a background
     * thread. The move buttons stay disabled until the search finishes.
     */
    private void startMoveSearch() {
        int generation = ++searchGeneration;
        moves       = List.of();
        currentMove = -1;
        view.setMoveButtonsDisabled(true);
        view.setStatus("Finding moves...");

        var search = engine.prepareMoveSearch();
        var task = new Task<List<Move>>() {
            @Override
            protected List<Move> call() {
                return search.get();
            }
        };

        task.setOnSucceeded(ev -> {
            if (generation != searchGeneration) return;

            moves = task.getValue();
            view.setMoveButtonsDisabled(false);
            view.setStatus(moves.isEmpty()
                    ? "No moves found for this rack."
                    : "%d moves found. Press Best Move to see the top one.".formatted(moves.size()));
        });

        task.setOnFailed(ev -> {
            if (generation != searchGeneration) return;

            view.setStatus("The move search failed.");
            showError("Move search failed", String.valueOf(task.getException()));
        });

        var thread = new Thread(task, "move-search");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Previews the move at the given position in the list, wrapping round to
     * the first move after the last.
     *
     * @param index the position of the move; may run past the end
     */
    private void showMove(int index) {
        if (moves.isEmpty()) {
            view.setStatus("No moves found for this rack.");
            return;
        }

        currentMove = Math.floorMod(index, moves.size());
        var move = moves.get(currentMove);
        previewMove(move);
        view.setStatus("Move %d of %d: %s".formatted(currentMove + 1, moves.size(), describeMove(move)));
    }

    /**
     * Renders a given move onto the board as a preview without committing it.
     *
     * <p>
     * The board returns to the committed state, then shows the move with its
     * tiles highlighted. The tiles the move uses leave the rack, so pressing
     * Commit next records the move as played.
     *
     * @param move the move to preview; must not be {@code null}
     */
    private void previewMove(Move move) {
        int shown = currentMove;
        showCommittedState();
        currentMove = shown;

        var preview   = engine.previewMove(move);
        var letters   = preview.getMatrix();
        var wildcards = new boolean[preview.getRows()][preview.getCols()];
        for (int r = 0; r < preview.getRows(); r++) {
            for (int c = 0; c < preview.getCols(); c++) {
                wildcards[r][c] = preview.isWildCard(r, c);
            }
        }

        view.updateBoard(letters, buildScoreMatrix(letters, wildcards), wildcards);
        for (int r = 0; r < preview.getRows(); r++) {
            for (int c = 0; c < preview.getCols(); c++) {
                if (preview.isNewLetter(r, c)) {
                    view.markNewTile(r, c);
                    view.removeTileFromRack(wildcards[r][c] ? TileSet.WILDCARD : letters[r][c]);
                }
            }
        }

        dndController.configure();
    }

    /**
     * Describes a move in words, such as "CATS for 12 points, across from row
     * 8, column 6".
     *
     * @param move the move to describe
     * @return the description
     */
    private static String describeMove(Move move) {
        var text = "%s for %d points, %s from row %d, column %d".formatted(
                move.word(), move.score(), move.verticallyPlaced() ? "down" : "across", move.row() + 1, move.col() + 1);
        boolean usesBlank = !move.word().equals(move.word().toUpperCase());
        return usesBlank ? text + " (lowercase letters are blanks)" : text;
    }

    /**
     * Shows an error dialog and waits for the player to close it.
     */
    private static void showError(String title, String message) {
        var alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    /**
     * Asks the player to confirm an action.
     *
     * @return {@code true} if the player pressed OK
     */
    private static boolean confirm(String title, String message) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(title);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

}
