package com.slinky.wordcheat.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Objects;

/**
 * Displays the current letters in the player's rack as TileNode instances.
 *
 * <p>
 * This view does not resize itself dynamically; it remains fixed and relies on
 * the parent layout for scaling.
 * </p>
 *
 */
public final class RackView extends StackPane {

    // ==============================[ Fields ]============================== \\
    private final HBox rackBox;
    private final TileNode[] tiles;
    private final int maxSize;

    // ===========================[ Constructors ]=========================== \\
    /**
     * Creates an empty RackView with a specified maximum size.
     *
     * @param maxSize the maximum number of tiles the rack can contain
     */
    public RackView(int maxSize) {
        this.maxSize = maxSize;
        this.tiles = new TileNode[maxSize];

        rackBox = new HBox(5);
        rackBox.setAlignment(Pos.CENTER);

        setPrefWidth(USE_COMPUTED_SIZE);
        setPrefHeight(USE_COMPUTED_SIZE);

        getChildren().add(rackBox);
    }

    /**
     * Creates a RackView pre-populated with the given letters and scores.
     *
     * @param letters letters to display
     * @param scores  corresponding scores for each letter
     * @param maxSize the maximum number of tiles the rack can contain
     */
    public RackView(char[] letters, int[] scores, int maxSize) {
        this(maxSize);
        updateRack(letters, scores);
    }

    // ===========================[ Accessor Methods ]=========================== \\
    /**
     * Returns all tiles in the rack.
     *
     * @return array of TileNode instances
     */
    public TileNode[] getTiles() {
        return tiles.clone();
    }

    // ===========================[ Public API ]============================== \\
    /**
     * Refreshes the displayed rack letters.
     *
     * @param letters array of letters to display
     * @param scores array of corresponding scores
     * @throws NullPointerException if letters or scores are null
     * @throws IllegalArgumentException if arrays differ in length or exceed
     *                                  maximum size
     */
    public void updateRack(char[] letters, int[] scores) {
        Objects.requireNonNull(letters, "letters must not be null");
        Objects.requireNonNull(scores, "scores must not be null");
        
        if (letters.length != scores.length) {
            throw new IllegalArgumentException("letters and scores must have the same length");
        }
        
        if (letters.length > maxSize) {
            throw new IllegalArgumentException("RackView cannot hold more than " + maxSize + " tiles");
        }

        rackBox.getChildren().clear();
        for (int i = 0; i < maxSize; i++) {
            TileNode tile;
            if (i < letters.length) {
                tile = TileFactory.createRackTile(letters[i], scores[i]);
            } else {
                tile = TileFactory.createRackTile(' ', 0); // Empty tile
            }
            tiles[i] = tile;
            rackBox.getChildren().add(tile);
        }
    }

    /**
     * Returns the TileNode at the specified index.
     *
     * @param index the index of the tile to retrieve
     * @return the TileNode at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public TileNode getTile(int index) {
        if (index < 0 || index >= maxSize) {
            throw new IndexOutOfBoundsException(
                    "Tile index " + index + " out of bounds for rack size " + maxSize
            );
        }
        return tiles[index];
    }

}
