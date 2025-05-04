/**
 * Defines the WordCheat application module.
 *
 * <p>
 * This module encapsulates all components of the WordCheat application,
 * including its core logic, view layer, and IO handling. It uses JavaFX for the
 * user interface and Jackson for JSON serialisation.
 *
 * <p>
 * <strong>Module Dependencies:</strong>
 * <ul>
 *   <li>{@code javafx.controls} — For building the graphical user interface.</li>
 *   <li>{@code javafx.swing} — Required for optional JavaFX-Swing
 *       interoperability.</li>
 *   <li>{@code java.desktop} — Enables image IO and clipboard access, used in
 *       drag-and-drop.</li>
 *   <li>{@code com.fasterxml.jackson.databind} — For serialising game state to
 *       and from JSON.</li>
 * </ul>
 *
 * <p>
 * <strong>Exported Packages:</strong>
 * <ul>
 *   <li>{@code com.slinky.wordcheat} — The root package, including the JavaFX
 *       App launcher.</li>
 *   <li>{@code com.slinky.wordcheat.io} — Handles game persistence and JSON
 *       snapshot serialisation.</li>
 * </ul>
 *
 * <p>
 * <strong>Opened Packages:</strong>
 * <ul>
 *   <li>{@code com.slinky.wordcheat.io} — Opened to Jackson for reflective
 *       access during serialisation.</li>
 * </ul>
 * 
 * @author  Kheagen Haskins
 * @since   1.0
 * @version 1.0
 */
module com.slinky.wordcheat {
    requires javafx.controls;
    requires javafx.swing;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    exports com.slinky.wordcheat;
    exports com.slinky.wordcheat.control;
    exports com.slinky.wordcheat.io;
    exports com.slinky.wordcheat.language;
    exports com.slinky.wordcheat.model;
    exports com.slinky.wordcheat.util;
    exports com.slinky.wordcheat.view;

    opens com.slinky.wordcheat.io to com.fasterxml.jackson.databind;
}