package com.slinky.wordcheat.view;

import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Kheagen
 */
public class MainView extends VBox {

    // ================================[ Static ]================================ \\
    
    // ================================[ Fields ]================================ \\
    public final BoardView   boardView;
    public final TileSetView setView;
    public final RackView    rackView;
    
    // =============================[ Constructors ]============================= \\
    public MainView(BoardView boardView, TileSetView setView, RackView rackView) {
        super(10);
        
        this.boardView  = Objects.requireNonNull(boardView,  "BoardView cannot be null");
        this.setView    = Objects.requireNonNull(setView,    "SetView cannot be null");
        this.rackView   = Objects.requireNonNull(rackView,   "RackView cannot be null");
        
        var boardAndSet = new HBox(10, boardView, setView);
        boardAndSet.setAlignment(Pos.CENTER);
        
        setPadding(new Insets(15, 5, 5, 5));
        getChildren().addAll(boardAndSet, rackView);
        configureDragAndDrop();
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    
    // ===========================[ Mutator Methods ]============================ \\

    // =============================[ API Methods ]============================== \\
    
    // ============================[ Helper Methods ]============================ \\

    // ============================[ Helper Classes ]============================ \\
    private void configureDragAndDrop() {
        for (int r = 0; r < boardView.getRows(); r++) {
            for (int c = 0; c < boardView.getCols(); c++) {
                var tile = boardView.getTile(r, c);
                if (tile.isEmpty()) {
                    configureDragTarget(tile);
                }
            }
        }
        
        configureDragSource(setView.getTile(' '));
        for (char c = 'A'; c <= 'Z'; c++) {
            var tile = setView.getTile(c);
            configureDragSource(tile);
        }
        
        for (var tile : rackView.getTiles()) {
            configureDragSource(tile);
        }
    }
    
    private void configureDragSource(TileNode tile) {
        tile.setOnDragDetected(evt -> {
            var db     = tile.startDragAndDrop(TransferMode.COPY);
            var params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            
            Image img = tile.snapshot(params, null);
            db.setDragView(img, img.getWidth() / 2, img.getHeight() / 2);

            var clipContent = new ClipboardContent();
            clipContent.putString(String.valueOf(tile.getLetter()));
            db.setContent(clipContent);

            evt.consume();
        });

        tile.setOnDragDone(evt -> {
            if (evt.isDropCompleted()) {
                ((Pane) tile.getParent()).getChildren().remove(tile);
            }

            evt.consume();
        });
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
        
        tile.setOnDragDropped(ev -> {
            handleTileDrop(ev, tile);
        });
    }
    
    private void handleTileDrop(DragEvent evt, TileNode tile) {
        Object src = evt.getGestureSource();
        boolean success = false;

        if (src instanceof TileNode t) {
            tile.setLetter(t.getLetter());
            tile.setScore(t.getScore());
            tile.setCount(t.getCount());
            tile.setWildcard(t.isWildcard());

            tile.setBackgroundFill(ColorConstants.DEFAULT_TILE_COLOR);
            setView.updateCount(tile.getLetter(), tile.getCount() - 1);

            tile.applyStyle();
            tile.refresh();
            success = true;
        }

        evt.setDropCompleted(success);
        evt.consume();
    }
    
}