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
 * 
 */
public class DnDController {

    // ================================[ Fields ]================================ \\
    private final GameEngine engine;
    private final MainView view;
    private boolean dropSuccessful;
    
    // =============================[ Constructors ]============================= \\
    public DnDController(GameEngine engine, MainView view) {
        this.engine = engine;
        this.view   = view;
    }

    // =============================[ API Methods ]============================== \\
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
    private void configureDragSourceTile(TileNode tile) {
        tile.setOnDragDetected(evt -> handleDragDetected(evt, tile));
        tile.setOnDragDone    (evt -> handleDragDone    (evt, tile));
        
        tile.setDraggable(true);
    }
    
    private void configureDragTargetTile(TileNode tile) {
        tile.setOnDragOver(evt -> {
            handleDragOver(evt);
        });

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
    
    private void configureDragSourceContainer(Pane container) {
        container.setOnDragOver(ev -> {
            handleDragOver(ev);
        });
        
        container.setOnDragDropped(evt -> {
            handleContainerDrop(evt, container);
        });
    }
    
    private void handleDragDetected(Event evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) return;
        else {
            boolean isPoolTile   = sourceTile.getSubstrate() == POOL;
            boolean hasRemaining = sourceTile.getCount() <= 0;
            if (isPoolTile && hasRemaining) {
                return;
            }
        }
        
        var db     = sourceTile.startDragAndDrop(TransferMode.COPY);
        var params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        
        Image img = sourceTile.snapshot(params, null);
        db.setDragView(img, img.getWidth() / 2, img.getHeight() / 2);
        
        var content = new ClipboardContent();
        content.putString(sourceTile.toString());
        db.setContent(content);
        
        evt.consume();
    }
    
    private void handleDragOver(DragEvent evt) {
        if (evt.getGestureSource() instanceof TileNode) {
            evt.acceptTransferModes(TransferMode.COPY);
        }

        evt.consume();
    }
    
    private void handleTileDrop(DragEvent evt, TileNode target) {
        var src = evt.getGestureSource();
        if (!target.isDropTarget() || !(src instanceof TileNode)) {
            evt.setDropCompleted(false);
            dropSuccessful = false;
            // let event bubble
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
        
        switch (source.getSubstrate()) {
            case BOARD -> view.emptyTile(source);
            // break;
        }
        

        dropSuccessful = true;
        evt.setDropCompleted(true);
        evt.consume();
    }
    
    private void handleDragDone(DragEvent evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) return;
        
        if (dropSuccessful) {
            switch (sourceTile.getSubstrate()) {
                case POOL -> view.updateTileCount(sourceTile.getLetter(), sourceTile.getCount() - 1);
                case RACK -> view.removeTileFromRack(sourceTile.getLetter());
            }
        }
        
        dropSuccessful = false; // reset for next event
        evt.consume();
    }
    
    private void handleContainerDrop(DragEvent evt, Pane container) {
        if (!(evt.getGestureSource() instanceof TileNode)) return;
        
        var sourceTile  = (TileNode) evt.getGestureSource();
        if (sourceTile.getSubstrate() != BOARD) return;

        boolean removed = false;
        char letter     = sourceTile.getLetter();
        
        if (container instanceof RackView) {
            view.addTileToRack(letter, engine.getScoreOf(letter));
            removed = true;
        } else if (container instanceof TileSetView) {
            view.updateTileCount(letter, view.getPoolTile(letter).getCount() + 1);
            removed = true;
        }
        
        if (removed) {
            view.emptyTile(sourceTile);
        }
        
        evt.consume();
    }
}