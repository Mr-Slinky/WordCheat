package com.slinky.wordcheat.view;

/**
 * Enum representing the possible containers (substrates) from which a {@link TileNode}
 * originates.  Primarily used to drive drag‑and‑drop behaviour between the game board,
 * the player's rack, and the communal tile pool.
 *
 * <p>Each constant identifies one of the recognised sources for a TileNode during
 * gameplay interaction.
 *
 * @author Kheagen Haskins
 * @since 0.1.0
 */
public enum Substrate {

    /** A tile displayed on the main game board. */
    BOARD,

    /** A tile currently held in the player's rack. */
    RACK,

    /** A tile drawn from the communal pool of remaining tiles. */
    POOL,

    /** The tile's origin is unknown or not yet assigned. */
    UNKNOWN;

}