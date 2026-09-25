package com.slinky.wordcheat.control;

import static com.slinky.wordcheat.view.Substrate.BOARD;
import static com.slinky.wordcheat.view.Substrate.POOL;
import static com.slinky.wordcheat.view.Substrate.RACK;

import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.TileSet;

import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.RackView;
import com.slinky.wordcheat.view.Substrate;
import com.slinky.wordcheat.view.TileNode;
import com.slinky.wordcheat.view.TileSetView;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.Event;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Drag-and-drop controller for managing tile movements between the board, pool,
 * and rack.
 *
 * <p>
 * The {@code DnDController} wires up drag sources and drop targets for all
 * {@link com.slinky.wordcheat.view.TileNode} instances and container panes in
 * the {@link com.slinky.wordcheat.view.MainView}. It handles the full lifecycle
 * of drag events, from detection through drag-over and drop to completion, by:
 *
 * <ul>
 *   <li>Configuring individual tiles and containers to initiate and accept
 *       drags.</li>
 *   <li>Creating a transparent drag visual snapshot via
 *       {@link javafx.scene.SnapshotParameters}.</li>
 *   <li>Validating drop targets based on substrate types (<code>BOARD</code>,
 *       <code>POOL</code>, <code>RACK</code>).</li>
 *   <li>Updating the UI (tile counts, rack contents, board state) upon
 *       successful drops.</li>
 *   <li>Asking which letter a blank stands for when a blank lands on the
 *       board, and sending it back where it came from if the player
 *       cancels.</li>
 * </ul>
 *
 * @author Kheagen Haskins
 *
 * @see javafx.scene.input.DragEvent
 * @see javafx.scene.input.TransferMode
 */
public class DnDController {

    // ================================[ Fields ]================================ \\
    private final GameEngine engine;
    private final MainView view;

    /** A board tile holding a blank whose letter the player has yet to choose. */
    private TileNode  pendingBlank;

    /** Where the pending blank was dragged from, so a cancelled choice can return it. */
    private Substrate pendingBlankOrigin;

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code DnDController} for managing drag-and-drop logic
     * between the frontend view and backend engine.
     *
     * @param engine the game engine used for tile scoring
     * @param view   the main view providing access to tiles and UI containers
     */
    public DnDController(GameEngine engine, MainView view) {
        this.engine = engine;
        this.view = view;
    }

    // =============================[ API Methods ]============================== \\
    /**
     * Initialises drag-and-drop behaviour for all interactive elements in the
     * main view.
     *
     * <p>
     * This method wires up the following:
     *
     * <ul>
     *   <li><b>Board tiles</b>: configured as both drag sources and drop
     *       targets; only empty slots accept drops, and only tiles placed
     *       since the last commit can be dragged.</li>
     *   <li><b>Pool tiles</b>: configured as drag sources; tiles with zero
     *       count are made non-draggable.</li>
     *   <li><b>Rack tiles</b>: configured as drag sources to allow moving
     *       letters onto the board.</li>
     *   <li><b>Container panes</b> (pool and rack): configured to accept drop
     *       events from any tile.</li>
     * </ul>
     *
     * <p>
     * A front end calls this again whenever it redraws the board, so the drop
     * targets match the tiles now showing.
     */
    public void configure() {
        for (TileNode tile : view.getBoardTiles()) {
            configureDragSourceTile(tile);
            configureDragTargetTile(tile);
            // Only tiles placed since the last commit can move again.
            tile.setDraggable(tile.isNewlyPlaced() && !tile.isEmpty());
            tile.setDropTarget(tile.isEmpty());
        }

        for (TileNode tile : view.getPoolTiles()) {
            configureDragSourceTile(tile);
            if (tile.getCount() <= 0) {
                tile.setDraggable(false);
            }
        }

        for (TileNode tile : view.getRackTiles()) {
            configureDragSourceTile(tile);
        }

        configureDragSourceContainer(view.getPoolView());
        configureDragSourceContainer(view.getRackView());
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Configures a tile to act as a drag source.
     *
     * @param tile the tile to configure
     */
    private void configureDragSourceTile(TileNode tile) {
        tile.setOnDragDetected(evt -> handleDragDetected(evt, tile));
        tile.setOnDragDone(evt -> handleDragDone(evt, tile));
        tile.setDraggable(true);
    }

    /**
     * Configures a tile to act as a drop target: accepting drags, showing a
     * hover highlight, and handling the drop.
     *
     * @param tile the tile to configure
     */
    private void configureDragTargetTile(TileNode tile) {
        tile.setOnDragOver(evt -> handleDragOver(evt));
        tile.setOnDragEntered(evt -> {
            tile.setHovered(true);
            tile.syncView();
            evt.consume();
        });
        tile.setOnDragExited(evt -> {
            tile.setHovered(false);
            tile.syncView();
            evt.consume();
        });
        tile.setOnDragDropped(evt -> handleTileDrop(evt, tile));
    }

    /**
     * Configures a container pane (pool or rack) to accept drops.
     *
     * @param container the pane to configure
     */
    private void configureDragSourceContainer(Pane container) {
        container.setOnDragOver(ev -> handleDragOver(ev));
        container.setOnDragDropped(evt -> handleContainerDrop(evt, container));
    }

    /**
     * Starts a drag from a tile, unless the tile is not draggable or is a pool
     * tile with none left.
     *
     * @param evt        the drag-detected event
     * @param sourceTile the tile being dragged
     */
    private void handleDragDetected(Event evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) {
            return;
        }

        boolean isUsedUpPoolTile = sourceTile.getSubstrate() == POOL && sourceTile.getCount() <= 0;
        if (isUsedUpPoolTile) {
            return;
        }

        var db = sourceTile.startDragAndDrop(TransferMode.COPY);
        var params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image img = sourceTile.snapshot(params, null);
        db.setDragView(img, img.getWidth() / 2, img.getHeight() / 2);

        var content = new ClipboardContent();
        content.putString(sourceTile.toString());
        db.setContent(content);

        evt.consume();
    }

    /**
     * Accepts a drag over a tile or container when it comes from a tile.
     *
     * @param evt the drag-over event
     */
    private void handleDragOver(DragEvent evt) {
        if (evt.getGestureSource() instanceof TileNode) {
            evt.acceptTransferModes(TransferMode.COPY);
        }
        evt.consume();
    }

    /**
     * Handles a tile dropped onto a board tile. The target takes the source's
     * letter and blank flag. A blank from the pool or rack has no letter yet,
     * so the target waits for the player to choose one once the drag is done.
     *
     * @param evt    the drag-dropped event
     * @param target the board tile receiving the drop
     */
    private void handleTileDrop(DragEvent evt, TileNode target) {
        if (!target.isDropTarget() || !(evt.getGestureSource() instanceof TileNode source)) {
            evt.setDropCompleted(false);
            evt.consume();
            return;
        }

        char letter      = source.getLetter();
        boolean isBlank  = source.isWildcard() || letter == TileSet.WILDCARD;
        target.setLetter(letter);
        target.setScore(isBlank ? 0 : engine.getScoreOf(letter));
        target.setWildcard(isBlank);
        target.setNewlyPlaced(true);
        target.setDraggable(true);
        target.setDropTarget(false);
        target.syncView();

        if (letter == TileSet.WILDCARD) {
            pendingBlank       = target;
            pendingBlankOrigin = source.getSubstrate();
        }

        if (source.getSubstrate() == BOARD) {
            view.emptyTile(source);
        }

        evt.setDropCompleted(true);
        evt.consume();
    }

    /**
     * Finalises a drag on its source tile. A completed drag from the pool
     * lowers that letter's count, and one from the rack removes the tile from
     * the rack. A blank waiting for its letter then gets the letter prompt.
     *
     * @param evt        the drag-done event
     * @param sourceTile the tile the drag started from
     */
    private void handleDragDone(DragEvent evt, TileNode sourceTile) {
        boolean completed = evt.getTransferMode() != null;
        if (completed) {
            switch (sourceTile.getSubstrate()) {
                case POOL -> view.updateTileCount(sourceTile.getLetter(), sourceTile.getCount() - 1);
                case RACK -> view.removeTileFromRack(sourceTile.getLetter());
                default   -> { }
            }
        }

        if (pendingBlank != null) {
            var target = pendingBlank;
            var origin = pendingBlankOrigin;
            pendingBlank       = null;
            pendingBlankOrigin = null;
            // Asked after the drag has fully ended, since a dialog cannot open inside a drag gesture.
            Platform.runLater(() -> chooseBlankLetter(target, origin));
        }

        evt.consume();
    }

    /**
     * Asks the player which letter a blank on the board stands for. When the
     * player cancels, the blank leaves the board and goes back to the pool or
     * rack it came from.
     *
     * @param target the board tile holding the blank
     * @param origin where the blank was dragged from
     */
    private void chooseBlankLetter(TileNode target, Substrate origin) {
        var letters = new ArrayList<Character>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letters.add(c);
        }

        var dialog = new ChoiceDialog<>('A', letters);
        dialog.setTitle("Blank tile");
        dialog.setHeaderText("Which letter does this blank stand for?");
        dialog.setContentText("Letter:");

        var choice = dialog.showAndWait();
        if (choice.isPresent()) {
            target.setLetter(choice.get());
            target.syncView();
            return;
        }

        view.emptyTile(target);
        if (origin == POOL) {
            view.updateTileCount(TileSet.WILDCARD, view.getPoolTile(TileSet.WILDCARD).getCount() + 1);
        } else if (origin == RACK) {
            view.addTileToRack(TileSet.WILDCARD, 0);
        }
        configure();
    }

    /**
     * Handles a tile dropped onto the rack or pool container. A blank taken
     * off the board goes back as a blank, whatever letter it stood for.
     *
     * @param evt             the drag-dropped event
     * @param targetContainer the container receiving the drop
     * @throws IllegalArgumentException if the container is neither the rack
     *                                  nor the pool
     */
    private void handleContainerDrop(DragEvent evt, Pane targetContainer) {
        if (!(evt.getGestureSource() instanceof TileNode sourceTile)) {
            return;
        }

        var sourceContainer = sourceTile.getSubstrate();
        char letter = sourceTile.isWildcard() ? TileSet.WILDCARD : sourceTile.getLetter();

        if (targetContainer instanceof RackView) {
            if (sourceContainer == RACK || view.isRackFull()) {
                evt.setDropCompleted(false);
                evt.consume();
                return;
            }

            view.addTileToRack(letter, engine.getScoreOf(letter));
        } else if (targetContainer instanceof TileSetView) {
            if (sourceContainer == POOL) {
                evt.setDropCompleted(false);
                evt.consume();
                return;
            }

            view.updateTileCount(letter, view.getPoolTile(letter).getCount() + 1);
        } else {
            evt.consume();
            throw new IllegalArgumentException("Invalid drop container: " + targetContainer.getClass().getName());
        }

        if (sourceContainer == BOARD) {
            view.emptyTile(sourceTile);
        }

        evt.setDropCompleted(true);
        evt.consume();
        configure();
    }

}
