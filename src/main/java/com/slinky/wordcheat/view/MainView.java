package com.slinky.wordcheat.view;

import java.util.Objects;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author Kheagen
 */
public class MainView extends BorderPane {

    // ================================[ Static ]================================ \\

    // ================================[ Fields ]================================ \\
    public final BoardView   boardView;
    public final TileSetView setView;
//    public final RackView    rackView;
    
    // =============================[ Constructors ]============================= \\
    public MainView(BoardView boardView, TileSetView setView) {
        this.boardView = Objects.requireNonNull(boardView, "BoardView cannot be null");
        this.setView   = Objects.requireNonNull(setView,   "SetView cannot be null");
        
        var center = new HBox(10, boardView, setView);
//        setBottom(rackView);
        
        setCenter(center);
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    
    // ===========================[ Mutator Methods ]============================ \\

    // =============================[ API Methods ]============================== \\
    
    // ============================[ Helper Methods ]============================ \\

    // ============================[ Helper Classes ]============================ \\

}