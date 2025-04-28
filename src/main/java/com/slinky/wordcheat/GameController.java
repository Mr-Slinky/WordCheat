package com.slinky.wordcheat;

import com.slinky.wordcheat.language.OxfordDictionary;

import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.DefaultTileSet;
import com.slinky.wordcheat.model.GameBoard;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.MoveFinder;
import com.slinky.wordcheat.model.TileBonus;

import com.slinky.wordcheat.io.Persistence;

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
    private String     filename;
    private GameEngine engine; 
    private Pane       mainView;
    
    // =============================[ Constructors ]============================= \\
    public GameController(String filename) {
        this.filename = filename;
        
        initEngine();
        initMainView();
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    public String getFilename() {
        return filename;
    }
    
    public Pane getMainView() {
        return mainView;
    }

    // =============================[ API Methods ]============================== \\
    public void save() throws IOException {
        Persistence.saveGame(engine, filename);
    }
    
    public void load() throws IOException {
        this.engine = Persistence.loadGame(filename);
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
        this.filename = filename;
        GameBoard gameBoard   = new GameBoard(new char[15][15]);
        MoveFinder moveFinder = new MoveFinder(
                                        gameBoard,
                                        new OxfordDictionary(),
                                        new DefaultScoringModule()
                                );

        System.out.println("New game created: " + filename); // DEBUG REMOVE
        return new GameEngine(new DefaultTileSet(), moveFinder);
    }
    
    // ============================[ Helper Classes ]============================ \\
    private void initEngine() {
        try {
            engine = Persistence.saveExists(filename) ? Persistence.loadGame(filename)
                                                      : createNewGame(filename);
        } catch (IOException e) {
            engine = createNewGame(filename);
        }
    }
    
    private void initMainView() {
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
        
        // Init bonuses
        var bonusMatrix    = DefaultScoringModule.getClassicBonusLayout();
        String[][] bonuses = new String[rows][cols];
        for (int r = 0; r < bonusMatrix.length; r++) {
            TileBonus[] bonusRow = bonusMatrix[r];
            for (int c = 0; c < bonusRow.length; c++) {
                TileBonus bonus  = bonusRow[c];
                bonuses[r][c]    = bonus == null ? null : bonus.toString();
            }
        }
        
        int[] counts = new int[27];
        char letter  = 'A';
        
        counts[0] = engine.getRemainingWildcardCount();
        for (int i = 0; i < counts.length - 1; i++) {
            counts[i] = engine.getRemainingTileCount(letter++);
        }
        
        char[] rackLetters = engine.getRackLetters();
        int[] rackScores   = new int[rackLetters.length]; 
        for (int i = 0; i < rackLetters.length; i++) {
            letter        = rackLetters[i];
            rackScores[i] = engine.getScoreOf(letter);
        }
        
        var boardView   = new BoardView(matrix, scores, bonuses);
        var tileSetView = new TileSetView(counts);
        var rackView    = new RackView(rackLetters, rackScores, 7);
        
        mainView = new MainView(boardView, tileSetView, rackView);
    }
    
}