package com.slinky.wordcheat.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Objects;

/**
 * Displays the current letters in the player's rack as TileNode instances.
 * 
 * This view does not resize itself dynamically; it remains fixed and relies
 * on the parent layout for scaling.
 * 
 * It does not depend on any backend model classes.
 */
public final class RackView extends StackPane {

    // ==============================[ Fields ]============================== \
    private final HBox rackBox;

    // ===========================[ Constructors ]=========================== \
    /**
     * Creates an empty RackView. Use updateRack(...) to populate.
     */
    public RackView() {
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
     */
    public RackView(char[] letters, int[] scores) {
        this();
        updateRack(letters, scores);
    }

    // ===========================[ Public API ]============================== \

    /**
     * Refreshes the displayed rack letters.
     *
     * @param letters array of letters to display
     * @param scores  array of corresponding scores
     * @throws NullPointerException     if letters or scores are null
     * @throws IllegalArgumentException if arrays differ in length
     */
    public void updateRack(char[] letters, int[] scores) {
        Objects.requireNonNull(letters, "letters must not be null");
        Objects.requireNonNull(scores, "scores must not be null");

        if (letters.length != scores.length) {
            throw new IllegalArgumentException("letters and scores must have the same length");
        }

        rackBox.getChildren().clear();
        for (int i = 0; i < letters.length; i++) {
            TileNode tile = TileFactory.createRackTile(letters[i], scores[i]);
            rackBox.getChildren().add(tile);
        }
    }

}