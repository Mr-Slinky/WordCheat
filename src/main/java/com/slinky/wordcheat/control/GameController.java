package com.slinky.wordcheat.control;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;
import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.ScoringModule;
import com.slinky.wordcheat.model.TileBonus;
import com.slinky.wordcheat.io.Persistence;
import com.slinky.wordcheat.io.GameEngineSnapshot;
import com.slinky.wordcheat.util.MatrixUtils;
import com.slinky.wordcheat.view.BoardView;
import com.slinky.wordcheat.view.MainView;
import com.slinky.wordcheat.view.RackView;
import com.slinky.wordcheat.view.TileSetView;

import java.io.IOException;

import javafx.scene.layout.Pane;

/**
 *
 * @author Kheagen Haskins
 */
public final class GameController {

    // ================================[ Static ]================================ \\
    
    // ================================[ Fields ]================================ \\
    private String             filename;
    private GameEngine         engine;
    private MainView           view;
    private DnDController      dndController;
    private GameEngineSnapshot lastSnapshot;
    
    // =============================[ Constructors ]============================= \\
    public GameController(String filename) {
        this.filename = filename;
        initEngine();
        initMainView();
        dndController = new DnDController(engine, view);
        dndController.configure();
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    public String getFilename() {
        return filename;
    }
    
    public Pane getMainView() {
        return view;
    }

    // =============================[ API Methods ]============================== \\
    public void save() throws IOException {
        Persistence.saveGame(engine, filename);
        lastSnapshot = GameEngineSnapshot.fromEngine(engine);
    }
    
    public void load() throws IOException {
        this.engine = Persistence.loadGame(filename);
        // do not update snapshot on load; reset() will restore previous state
    }

    public void reset() {
        if (lastSnapshot == null) {
            throw new IllegalStateException("No in-memory snapshot available; call save() first.");
        }
        
        Dictionary dict       = new OxfordDictionary();
        ScoringModule scoring = new DefaultScoringModule();
        engine = lastSnapshot.toEngine(dict, scoring);
        
        view.updateBoard(engine.getMatrix(), getScoreMatrix(), getBonusMatrix());
        view.updateTileSet(engine.getRemainingTileCounts());
        view.updateRack(engine.getRackLetters(), engine.getRackScores());
        
        dndController = new DnDController(engine, view);
        dndController.configure();
    }
    
    public void printTopFiveMoves() {
        var moves = engine.getAllMoves();
        int e = Math.min(5, moves.size());
        for (int i = 0; i < e; i++) {
            var topMove = moves.get(i);
            System.out.println(topMove);
            System.out.println(engine.previewMove(topMove));
        }
    }
    
    // ============================[ Helper Methods ]============================ \\
    private GameEngine createNewGame(String filename) {
        this.filename         = filename;
        GameBoard gameBoard   = new GameBoard(new char[15][15]);
        MoveFinder moveFinder = new MoveFinder(
                                        gameBoard,
                                        new OxfordDictionary(),
                                        new DefaultScoringModule()
                                );
        return new GameEngine(new DefaultTileSet(), moveFinder);
    }
    
    private void initEngine() {
        try {
            engine = Persistence.saveExists(filename) ? Persistence.loadGame(filename)
                                                     : createNewGame(filename);
        } catch (IOException e) {
            engine = createNewGame(filename);
        }
        
        lastSnapshot = GameEngineSnapshot.fromEngine(engine);
    }
    
    private void initMainView() {
        var counts      = MatrixUtils.shiftLeft(engine.getRemainingTileCounts(), 1);
        var rackLetters = engine.getRackLetters();

        var boardView   = new BoardView(engine.getMatrix(), getScoreMatrix(), getBonusMatrix());
        var tileSetView = new TileSetView(counts);
        var rackView    = new RackView(rackLetters, engine.getRackScores(), rackLetters.length);
        
        view = new MainView(boardView, tileSetView, rackView);
    }
    
    private int[][] getScoreMatrix() {
        char[][] matrix = engine.getMatrix();
        int rows = matrix.length;
        int cols = matrix[0].length;
        // Init scores
        int[][] scores = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                scores[r][c] = engine.getScoreOf(matrix[r][c]);
            }
        }
        
        return scores;
    }
    
    private String[][] getBonusMatrix() {
        char[][] matrix = engine.getMatrix();
        int rows = matrix.length;
        int cols = matrix[0].length;
        
        var bonusMatrix    = DefaultScoringModule.getClassicBonusLayout();
        String[][] bonuses = new String[rows][cols];
        for (int r = 0; r < bonusMatrix.length; r++) {
            TileBonus[] bonusRow = bonusMatrix[r];
            for (int c = 0; c < bonusRow.length; c++) {
                TileBonus bonus  = bonusRow[c];
                bonuses[r][c]    = bonus == null ? null : bonus.toString();
            }
        }
        
        return bonuses;
    }
    
}