package com.slinky.wordcheat;

import com.slinky.wordcheat.language.OxfordDictionary;

import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.persistence.Persistence;
import java.io.IOException;

/**
 *
 * @author Kheagen
 */
public class GameController {

    // ================================[ Static ]================================ \\
    
    // ================================[ Fields ]================================ \\
    private GameEngine engine; 
    
    // =============================[ Constructors ]============================= \\
    public GameController() {
        GameBoard gameBoard      = new GameBoard(new char[15][15]);
        MoveFinder moveFinder    = new MoveFinder(
                                           gameBoard, 
                                           new OxfordDictionary(), 
                                           new DefaultScoringModule()
                                   );
        engine = new GameEngine(new DefaultTileSet(), moveFinder);
    } // End of Constructor
    
    public GameController(String filename) throws IOException {
        this.engine = Persistence.loadGame(filename);
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    
    // ===========================[ Mutator Methods ]============================ \\

    // =============================[ API Methods ]============================== \\
    public void save(String filename) throws IOException {
        Persistence.saveGame(engine, filename);
    }
    
    public void load(String filename) throws IOException {
        this.engine = Persistence.loadGame(filename);
    }
    // ============================[ Helper Methods ]============================ \\

    // ============================[ Helper Classes ]============================ \\

}