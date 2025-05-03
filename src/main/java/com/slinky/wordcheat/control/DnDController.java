package com.slinky.wordcheat.control;

import static com.slinky.wordcheat.view.Substrate.BOARD;
import static com.slinky.wordcheat.view.Substrate.POOL;
import static com.slinky.wordcheat.view.Substrate.RACK;

import com.slinky.wordcheat.model.GameEngine;

import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.RackView;
import com.slinky.wordcheat.view.TileNode;
import com.slinky.wordcheat.view.TileSetView;

import javafx.event.Event;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Drag-and-drop controller for managing tile movements between the board, pool, and rack.
 *
 * <p>
 * The {@code DnDController} wires up drag sources and drop targets for all
 * {@link com.slinky.wordcheat.view.TileNode} instances and container panes in
 * the {@link com.slinky.wordcheat.view.MainView}. It handles the full lifecycle
 * of drag events—including detection, over, drop, and completion - by:
 * </p>
 * <ul>
 *   <li>Configuring individual tiles and containers to initiate and accept
 *       drags.</li>
 *   <li>Creating a transparent drag visual snapshot via
 *       {@link javafx.scene.SnapshotParameters}.</li>
 *   <li>Validating drop targets based on substrate types (<code>BOARD</code>,
 *       <code>POOL</code>, <code>RACK</code>).</li>
 *   <li>Updating the {@link com.slinky.wordcheat.model.GameEngine} and UI 
 *       (tile counts, rack contents, board state) upon successful drops.</li>
 *   <li>Reverting pool counts or rack contents on drag completion if
 *       necessary.</li>
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
    private boolean dropSuccessful;
    
    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a new {@code DnDController} for managing drag-and-drop logic
     * between the frontend view and backend engine.
     *
     * @param engine the game engine used for tile scoring and state tracking
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
     * </p>
     * <ul>
     *   <li><b>Board tiles</b> – configured as both drag sources and drop
     *       targets; empty slots are enabled to accept drops.</li>
     *   <li><b>Pool tiles</b> – configured as drag sources; tiles with zero 
     *       count are made non-draggable.</li>
     *   <li><b>Rack tiles</b> – configured as drag sources to allow moving
     *       letters back to the rack.</li>
     *   <li><b>Container panes</b> (pool and rack) – configured to accept drop
     *       events from any tile.</li>
     * </ul>
     *
     * <p>
     * After this call, all {@link com.slinky.wordcheat.view.TileNode} and
     * container {@code Pane}s in the {@link com.slinky.wordcheat.view.MainView}
     * will be fully prepared to handle drag detection, drag-over, drop, and
     * drag-done events according to their substrate (<code>BOARD</code>,
     * <code>POOL</code>, or <code>RACK</code>).
     * </p>
     */
    public void configure() {
        for (TileNode tile : view.getBoardTiles()) {
            configureDragSourceTile(tile);
            configureDragTargetTile(tile);
            tile.setDraggable(false);
            if (tile.isEmpty()) {
                tile.setDropTarget(true);
            }
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
     * Configures a TileNode as a drag source by registering handlers for drag
     * start and drag completion, and marks it as draggable.
     *
     * @param tile the tile to make draggable
     */
    private void configureDragSourceTile(TileNode tile) {
        // when user initiates a drag gesture, delegate to handler
        tile.setOnDragDetected(evt -> handleDragDetected(evt, tile));
        // when drag finishes, update counts or rollback
        tile.setOnDragDone(evt -> handleDragDone(evt, tile));
        tile.setDraggable(true);
    }

    /**
     * Configures a TileNode as a drag target by registering handlers for
     * drag-over, drag-enter, drag-exit, and drag-dropped events.
     *
     * @param tile the tile that can accept drops
     */
    private void configureDragTargetTile(TileNode tile) {
        // allow dropping onto this tile
        tile.setOnDragOver(evt -> handleDragOver(evt));

        // highlight on drag enter
        tile.setOnDragEntered(evt -> {
            tile.setHovered(true);
            tile.syncView();
            evt.consume();
        });

        // remove highlight on drag exit
        tile.setOnDragExited(evt -> {
            tile.setHovered(false);
            tile.syncView();
            evt.consume();
        });

        // handle the actual drop
        tile.setOnDragDropped(evt -> handleTileDrop(evt, tile));
    }

    /**
     * Configures a container pane (e.g. pool or rack) to accept drops by
     * registering handlers for drag-over and drag-dropped events.
     *
     * @param container the Pane to configure as a drop target
     */
    private void configureDragSourceContainer(Pane container) {
        // allow items to be dragged over the container
        container.setOnDragOver(ev -> handleDragOver(ev));
        // handle dropping onto the container
        container.setOnDragDropped(evt -> handleContainerDrop(evt, container));
    }

    /**
     * Begins a drag-and-drop gesture for the given tile if it is draggable and
     * has available count.
     *
     * <p>
     * Creates a transparent snapshot of the tile as the drag view and places
     * its string representation onto the clipboard content.
     * </p>
     *
     * @param evt        the event that triggered drag detection
     * @param sourceTile the tile node from which the drag originates
     */
    private void handleDragDetected(Event evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) {
            return;
        }
        boolean isPoolTile = sourceTile.getSubstrate() == POOL;
        boolean hasRemaining = sourceTile.getCount() <= 0;
        if (isPoolTile && hasRemaining) {
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
     * Allows a drag gesture to be recognised when the source is a TileNode.
     *
     * <p>
     * Accepts the <code>COPY</code> transfer mode and consumes the event to
     * prevent further propagation.
     * </p>
     *
     * @param evt the drag event fired when an object is dragged over a target
     */
    private void handleDragOver(DragEvent evt) {
        if (evt.getGestureSource() instanceof TileNode) {
            evt.acceptTransferModes(TransferMode.COPY);
        }
        evt.consume();
    }

    /**
     * Handles dropping a tile onto a target TileNode, updating its letter,
     * score, and state if the drop is valid.
     *
     * <p>
     * If the target is not drop-enabled or the source is not a TileNode, the
     * drop is rejected. Otherwise, the tile’s properties are set and the
     * previous source slot is cleared if it came from the board.
     * </p>
     *
     * @param evt    the drag event carrying the drop data
     * @param target the tile node receiving the dropped tile
     */
    private void handleTileDrop(DragEvent evt, TileNode target) {
        var src = evt.getGestureSource();
        if (!target.isDropTarget() || !(src instanceof TileNode)) {
            evt.setDropCompleted(false);
            dropSuccessful = false;
            return;
        }

        var source = (TileNode) src;
        var letter = source.getLetter();

        target.setLetter(letter);
        target.setScore(engine.getScoreOf(letter));
        target.setWildcard(source.isWildcard());
        target.setNewlyPlaced(true);
        target.setDraggable(true);
        target.setDropTarget(false);
        target.syncView();

        if (source.getSubstrate() == BOARD) {
            view.emptyTile(source);
        }

        dropSuccessful = true;
        evt.setDropCompleted(true);
        evt.consume();
    }

    /**
     * Finalises a drag gesture for a source tile.
     *
     * <p>
     * If the drop was successful, this method updates the UI:
     * <ul>
     *   <li>From the pool: decrements the tile count display.</li>
     *   <li>From the rack: removes the tile from the rack view.</li>
     * </ul>
     * Regardless of outcome, the internal <code>dropSuccessful</code> flag is
     * reset.
     * </p>
     *
     * @param evt        the drag event indicating the drag-and-drop operation has
     *                   completed
     * @param sourceTile the tile node that was dragged
     */
    private void handleDragDone(DragEvent evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) {
            return;
        }

        if (dropSuccessful) {
            switch (sourceTile.getSubstrate()) {
                case POOL ->
                    view.updateTileCount(sourceTile.getLetter(), sourceTile.getCount() - 1);
                case RACK ->
                    view.removeTileFromRack(sourceTile.getLetter());
            }
        }

        dropSuccessful = false; // reset for next drag cycle
        evt.consume();
    }

    /**
     * Handles a drop action on a container pane (rack or pool).
     *
     * <p>
     * This method determines the source substrate and target container type:
     * <ul>
     * <li><b>RackView</b>:
     * <ul>
     * <li>If the rack is not full and the tile did not originate from the rack,
     * adds the tile to the rack.</li>
     * </ul>
     * </li>
     * <li><b>TileSetView</b>:
     * <ul>
     * <li>If the tile did not originate from the pool, increments its count in
     * the pool.</li>
     * </ul>
     * </li>
     * </ul>
     * An <code>IllegalArgumentException</code> is thrown if the target is
     * neither.</p>
     *
     * @param evt the drag event carrying the dropped tile
     * @param targetContainer the {@code Pane} receiving the drop (either
     * {@code RackView} or {@code TileSetView})
     * @throws IllegalArgumentException if the target container is unsupported
     */
    private void handleContainerDrop(DragEvent evt, Pane targetContainer) {
        if (!(evt.getGestureSource() instanceof TileNode)) {
            return;
        }

        var sourceTile = (TileNode) evt.getGestureSource();
        var sourceContainer = sourceTile.getSubstrate();
        char letter = sourceTile.getLetter();

        // Handle drop into rack
        if (targetContainer instanceof RackView) {
            if (sourceContainer == RACK || view.isRackFull()) {
                evt.consume();
                return;
            }
            view.addTileToRack(letter, engine.getScoreOf(letter));

            // Handle drop into pool
        } else if (targetContainer instanceof TileSetView) {
            if (sourceContainer == POOL) {
                evt.consume();
                return;
            }
            view.updateTileCount(letter, view.getPoolTile(letter).getCount() + 1);

            // Unsupported container
        } else {
            String className = targetContainer.getClass().getName();
            evt.consume();
            throw new IllegalArgumentException("Invalid source container: " + className);
        }

        dropSuccessful = true;
        if (sourceContainer == BOARD) {
            view.emptyTile(sourceTile);
        }

        evt.consume();
    }

}