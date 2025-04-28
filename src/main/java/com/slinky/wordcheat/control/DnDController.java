package com.slinky.wordcheat.control;

import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.view.ColorConstants;

import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.TileNode;
import static com.slinky.wordcheat.view.TileType.BOARD;
import static com.slinky.wordcheat.view.TileType.POOL;
import static com.slinky.wordcheat.view.TileType.RACK;

import javafx.event.Event;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

/**
 * 
 */
public class DnDController {

    // ================================[ Fields ]================================ \\
    private final GameEngine engine;
    private final MainView view;
    
    // =============================[ Constructors ]============================= \\
    public DnDController(GameEngine engine, MainView view) {
        this.engine = engine;
        this.view   = view;
    }

    // =============================[ API Methods ]============================== \\
    public void configure() {
        for (TileNode tile : view.getBoardTiles()) {
            if (tile.isEmpty()) {
                configureDragSource(tile);
                configureDragTarget(tile);
                tile.setDraggable(false);
            }
        }

        for (TileNode tile : view.getPoolTiles()) {
            configureDragSource(tile);
        }
        
        for (TileNode tile : view.getRackTiles()) {
            configureDragSource(tile);
        }
    }

    // ============================[ Helper Methods ]============================ \\
    private void configureDragSource(TileNode tile) {
        tile.setOnDragDetected(evt -> handleDragDetected(evt, tile));
        tile.setOnDragDone    (evt -> handleDragDone    (evt, tile));
        
        tile.setDraggable(true);
    }
    
    private void configureDragTarget(TileNode tile) {
        tile.setOnDragOver(evt -> {
            if (evt.getGestureSource() instanceof TileNode) {
                evt.acceptTransferModes(TransferMode.COPY);
            }
            
            evt.consume();
        });

        tile.setOnDragEntered(evt -> {
            tile.setHovered(true);
            tile.applyStyle();
            
            evt.consume();
        });

        tile.setOnDragExited(evt -> {
            tile.setHovered(false);
            tile.applyStyle();

            evt.consume();
        });

        tile.setOnDragDropped(evt -> handleTileDrop(evt, tile));
    }

    private void handleDragDetected(Event evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) return;
        
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

    private void handleDragDone(DragEvent evt, TileNode sourceTile) {
        if (!sourceTile.isDraggable()) return;
        
        switch (sourceTile.getType()) {
            case POOL:
                view.updateTileCount(sourceTile.getLetter(), sourceTile.getCount() - 1);
                break;
            case RACK:
                view.removeTileFromRack(sourceTile.getLetter());
                break;
            default:
            // assume BOARD
        }

        evt.consume();
    }
    
    private void handleTileDrop(DragEvent evt, TileNode target) {
        var src = evt.getGestureSource();
        if (!target.isDropTarget() || !(src instanceof TileNode)) {
            evt.setDropCompleted(false);
            // let event bubble
            return;
        }
        
        var source = (TileNode) src;
        var letter = source.getLetter();

        target.setLetter(letter);
        target.setScore(engine.getScoreOf(letter));
        target.setWildcard(source.isWildcard());
        target.setBackgroundFill(ColorConstants.DEFAULT_TILE_COLOR);
        target.setDraggable(true);
        target.setDropTarget(false);
        
        switch (source.getType()) {
            case BOARD:
                view.emptyTile(source);
            // break;
        }
        
        target.applyStyle();
        target.refresh();

        evt.setDropCompleted(true);
        evt.consume();
    }
    
}