package com.slinky.wordcheat;

import com.slinky.wordcheat.language.OxfordDictionary;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.view.TileView;
import com.slinky.wordcheat.view.MatrixView;
import com.slinky.wordcheat.view.layouts.RackLayout;
import com.slinky.wordcheat.view.layouts.SetLayout;
import com.slinky.wordcheat.view.layouts.WWFLayout;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * The main controller for the application. Manages communication between the
 * model and the view.
 * 
 * @author Kheagen Haskins
 */
public class Controller {

    // ================================[ Static ]================================ \\
    private static final int EST_CELL_SIZE = 40;
    
    // ================================[ Fields ]================================ \\
    private OxfordDictionary dictionary;
    private GameBoard matrix;
    private DefaultTileSet   tileData;
    
    private BorderPane matrices;
    
    // =============================[ Constructors ]============================= \\
    public Controller() {
        loadData();
        loadViews();
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    public Pane getMainView() {
        return matrices;
    }
    
    // ===========================[ Mutator Methods ]============================ \\
    
    
    // =============================[ API Methods ]============================== \\
    
    
    // ============================[ Helper Methods ]============================ \\
    private void loadData() {
        dictionary = new OxfordDictionary();
        tileData   = new DefaultTileSet();
        
        var layout = WWFLayout.getInstance().getLayout(); // A 2D array of Cell Types
        int rows   = layout.length;
        int cols   = layout[0].length;
        
//        matrix = new GameBoard(rows, cols);
    }
    
    private void loadViews() {
        MatrixView gameGrid = new MatrixView(WWFLayout.getInstance(),  5, 5);
        MatrixView setGrid  = new MatrixView(SetLayout.getInstance(),  2, 2);
        MatrixView rackGrid = new MatrixView(RackLayout.getInstance(), 1, 0);
        
        setGrid .setBackground(Background.fill(Color.gray(0.9)));
        rackGrid.setBackground(Background.fill(Color.gray(0.9)));
        
        char c = 'A';
        for (TileView tile : setGrid) {
            tile.setLetter(c, TileView.DEFAULT_LETTER_COLOR);
            tile.setScore(tileData.getRemainingTileCount(c++));
        }
        
        matrices = new BorderPane();
        matrices.setCenter(gameGrid);
        matrices.setRight(setGrid);
        matrices.setBottom(rackGrid);

        BorderPane.setAlignment(setGrid,  Pos.CENTER);
        BorderPane.setAlignment(rackGrid, Pos.CENTER);
    }
    
}