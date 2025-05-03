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
    private final HBox       rackBox;
    private final TileNode[] tiles;
    private final int        maxSize;
    private int size;

    // ===========================[ Constructors ]=========================== \\
    /**
     * Creates an empty RackView with a specified maximum size.
     *
     * @param maxSize the maximum number of tiles the rack can contain
     */
    public RackView(int maxSize) {
        this.maxSize = maxSize;
        this.tiles   = new TileNode[maxSize];
        this.size    = 0;
        
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = TileFactory.createRackTile(' ', 0);
        }
        
        rackBox = new HBox(5);
        rackBox.setAlignment(Pos.CENTER);

        setPrefWidth(USE_COMPUTED_SIZE);
        setPrefHeight(USE_COMPUTED_SIZE);
        setMinHeight(50);

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
    TileNode[] getTiles() {
        return tiles;
    }
    
    /**
     * Returns the current number of tiles in the rack.
     */
    int getSize() {
        return size;
    }

    /**
     * Returns the maximum number of tiles this rack can hold.
     *
     * @return the maximum rack size
     */
    int getMaxSize() {
        return maxSize;
    }

    /**
     * Determines whether the rack is currently full.
     *
     * @return {@code true} if the number of tiles in the rack equals its
     * maximum capacity; {@code false} otherwise
     */
    boolean isFull() {
        return size == maxSize;
    }

    // ===========================[ Public API ]============================== \\
    /**
     * Adds a single tile to the rack.
     *
     * @param letter the letter to add
     * @param score the score associated with the letter
     * @throws IllegalStateException if rack is full
     */
    void addTile(char letter, int score) {
        if (size >= maxSize) {
            throw new IllegalStateException("RackView cannot hold more than " + maxSize + " tiles");
        }
        
        tiles[size].setLetter(letter);
        tiles[size].setScore(score);
        tiles[size].setSubstrate(Substrate.RACK);
        tiles[size].syncView();
        
        rackBox.getChildren().add(tiles[size]);
        size++;
    }
    
    /**
     * Adds multiple tiles to the rack.
     *
     * @param letters letters to add
     * @param scores corresponding scores
     * @throws IllegalArgumentException if input arrays differ in length
     * @throws IllegalStateException if addition exceeds max size
     */
    void addTiles(char[] letters, int[] scores) {
        Objects.requireNonNull(letters, "letters must not be null");
        Objects.requireNonNull(scores,  "scores must not be null");
        if (letters.length != scores.length) {
            throw new IllegalArgumentException("letters and scores must have the same length");
        }
        
        if (size + letters.length > maxSize) {
            throw new IllegalStateException("Addition exceeds maximum rack size (" + maxSize + ")");
        }
        
        for (int i = 0; i < letters.length; i++) {
            addTile(letters[i], scores[i]);
        }
    }

    /**
     * Removes the first occurrence of the specified letter.
     *
     * @param letter the letter to remove
     * @return true if removed, false otherwise
     */
    boolean removeTile(char letter) {
        for (int i = 0; i < size; i++) {
            if (tiles[i].getLetter() == letter) {
                removeTileAt(i);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Removes and returns the specified TileNode reference.
     *
     * @param index the index to remove
     * @return the removed letter
     * @throws IndexOutOfBoundsException if index invalid
     */
    TileNode removeTileAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for rack size " + size);
        }
        
        TileNode removed = tiles[index];
        removed.setSubstrate(Substrate.UNKNOWN);
        rackBox.getChildren().remove(removed);
        // shift left
        for (int i = index; i < size - 1; i++) {
            tiles[i] = tiles[i + 1];
        }
        
        tiles[size - 1] = removed;
        size--;
        
        return removed;
    }

    /**
     * Removes a number of tiles from the end of the rack.
     *
     * @param count number to remove
     * @throws IllegalArgumentException if count negative
     * @throws IllegalStateException if count exceeds current size
     */
    void removeTiles(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Cannot remove negative number of tiles");
        }
        
        if (count > size) {
            throw new IllegalStateException("Cannot remove " + count + " tiles from a rack of size " + size);
        }
        
        for (int i = 0; i < count; i++) {
            removeTileAt(size - 1);
        }
    }

    /**
     * Refreshes the displayed rack letters, replacing all current tiles.
     */
    void updateRack(char[] letters, int[] scores) {
        Objects.requireNonNull(letters, "letters must not be null");
        Objects.requireNonNull(scores,  "scores must not be null");
        if (letters.length != scores.length) {
            throw new IllegalArgumentException("letters and scores must have the same length");
        }
        
        if (letters.length > maxSize) {
            throw new IllegalArgumentException("RackView cannot hold more than " + maxSize + " tiles");
        }

        rackBox.getChildren().clear();
        size = letters.length;
        for (int i = 0; i < size; i++) {
            tiles[i].setLetter(letters[i]);
            tiles[i].setScore (scores[i]);
            tiles[i].setSubstrate(Substrate.RACK);
            tiles[i].syncView();

            rackBox.getChildren().add(tiles[i]);
        }
        
    }    
    
}