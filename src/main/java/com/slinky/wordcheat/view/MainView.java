package com.slinky.wordcheat.view;

import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        
        setPadding(new Insets(5));
        getChildren().addAll(boardAndSet, rackView);
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    
    // ===========================[ Mutator Methods ]============================ \\

    // =============================[ API Methods ]============================== \\
    
    // ============================[ Helper Methods ]============================ \\

    // ============================[ Helper Classes ]============================ \\

}