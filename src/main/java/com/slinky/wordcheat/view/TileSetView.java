package com.slinky.wordcheat.view;

import static com.slinky.wordcheat.view.FontConstants.LABEL_FONT_DEFAULT;
import static com.slinky.wordcheat.view.FontConstants.LABEL_FONT_HEADING;

import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Front‑end view of the full tile pool. Displays each letter tile with its
 * remaining count, plus summary labels for total and wildcard counts. Does not
 * depend on the backend; it takes a simple counts array (A–Z + blank) and
 * renders TileNode instances accordingly.
 *
 * @author Kheagen Haskins
 */
public class TileSetView extends VBox {

    // ==============================[ Static ]============================== \\
    /**
     * number of distinct tile types (A–Z plus blank)
     */
    private static final int TILE_TYPES = 27;
    private static final int COLUMNS    = 3;
    private static final int GAP        = 5;

    // ==============================[ Fields ]============================== \\
    private final GridPane   grid;
    private final Label      totalRemainingLabel;
    private final Label      wildcardRemainingLabel;
    private final TileNode[] tiles = new TileNode[TILE_TYPES];

    // ===========================[ Constructors ]=========================== \\
    /**
     * @param counts an array of length ≥27 giving remaining counts for A–Z and
     * blank
     */
    public TileSetView(int[] counts) {
        Objects.requireNonNull(counts, "counts must not be null");
        if (counts.length < TILE_TYPES) {
            throw new IllegalArgumentException(
                    "counts array must have at least " + TILE_TYPES + " elements"
            );
        }

        // Set up layout
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        
        // Header
        Label header = new Label("Letter Pool");
        header.setFont(LABEL_FONT_HEADING);
        header.setTextFill(Color.BLACK);
        getChildren().add(header);

        // Grid of tiles
        grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP * 2);
        grid.setAlignment(Pos.CENTER);
        getChildren().add(grid);

        // Summary panel
        totalRemainingLabel    = buildCustomLabel();
        wildcardRemainingLabel = buildCustomLabel();
        getChildren().addAll(totalRemainingLabel, wildcardRemainingLabel);

        // Populate
        initTiles(counts);
        updateSummary(counts);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    TileNode getTile(char letter) {
        int index = TILE_TYPES - 1;
        if (!(letter < 'A' || letter > 'Z')) {
            index = letter - 'A';
        }
        
        return tiles[index];
    }

    /**
     * Returns all TileNode instances representing the letter pool (A–Z plus
     * blank).
     */
    TileNode[] getAllTiles() {
        return tiles;
    }
    
    // ============================[ Public API ]============================ \\
    /**
     * Refreshes the tile counts and summary from a new counts array.
     *
     * @param counts an array of length ≥27 giving remaining counts for A–Z and blank
     */
    void updateCounts(int[] counts) {
        Objects.requireNonNull(counts, "counts must not be null");
        if (counts.length < TILE_TYPES) {
            throw new IllegalArgumentException(
                "counts array must have at least " + TILE_TYPES + " elements"
            );
        }

        for (int i = 0; i < TILE_TYPES; i++) {
            tiles[i].setCount(counts[i]);
            tiles[i].syncView();
        }

        updateSummary(counts);
    }

    /**
     *
     * @param letter
     * @param count 
     */
    void updateCount(char letter, int count) {
        for (TileNode tile : tiles) {
            if (tile.getLetter() == letter) {
                tile.setCount(count);
                tile.syncView();
                break;
            }
        }
        
        updateSummary();
    }
    
    // ==========================[ Helper Methods ]========================== \\
    private void initTiles(int[] counts) {
        // Create one TileNode per letter + blank
        for (int i = 0; i < TILE_TYPES; i++) {
            char letter = (i < 26) ? (char) ('A' + i) : ' ';
            int row     = i / COLUMNS;
            int col     = i % COLUMNS;

            TileNode node = TileFactory.createTileSetTile(letter, counts[i]);
            tiles[i]      = node;
            grid.add(node, col, row);
        }
    }
    
    private void updateSummary(int[] counts) {
        int total = 0;
        for (int c : counts) {
            total += c;
        }
        
        totalRemainingLabel   .setText(total      + " tiles remaining");
        wildcardRemainingLabel.setText(counts[26] + " wildcards remaining");
    }
    
    private void updateSummary() {
        int total = 0;
        int wild  = 0;
        for (TileNode tile : tiles) {
            int count = tile.getCount();
            total    += count;
            
            if (tile.getLetter() == ' ') {
                wild = count;
            }
        }
        
        totalRemainingLabel   .setText(total + " tiles remaining");
        wildcardRemainingLabel.setText(wild  + " wildcards remaining");
    }
    
    private Label buildCustomLabel() {
        Label label = new Label();

        // rounded‐corner background:
        label.setBackground(new Background(new BackgroundFill(
                Color.rgb(55, 81, 95),
                new CornerRadii(6), 
                Insets.EMPTY
        )));
        label.setTextFill(Color.WHITE);
        label.setFont(LABEL_FONT_DEFAULT);
        
        // Label should take up as much horizontal space as possible
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(5));
        
        return label;
    }
    
}