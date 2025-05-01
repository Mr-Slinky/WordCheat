package com.slinky.wordcheat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.slinky.wordcheat.control.GameController;

/**
 * Entry point for the WordCheat JavaFX application.
 *
 * <p>
 * Initialises and launches the JavaFX environment, constructs the primary
 * {@link Stage}, and displays the main application view managed by
 * {@link com.slinky.wordcheat.control.GameController}.
 * </p>
 *
 * <p>
 * The application creates a {@code GameController} with a default save file
 * name, retrieves its {@code MainView} as the root of the scene graph, and
 * presents it within a window of fixed dimensions.
 * </p>
 *
 * @author Kheagen Haskins
 * @version 1.0
 */
public class App extends Application {

    /**
     * Controller coordinating game logic, persistence, and user interface.
     */
    private GameController controller;

    /**
     * Called when the JavaFX application is started.
     * 
     * <p>
     * Creates a {@code GameController}, initialises the scene graph with its
     * main view, and shows the primary stage.
     * </p>
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        // Initialise controller with default save file name
        controller = new GameController("mom");

        // Create and set scene using the controller's main view
        Scene scene = new Scene(controller.getMainView(), 900, 800);
        stage.setScene(scene);

        // Display the primary window
        stage.show();
    }

    /**
     * Main method for launching the JavaFX application.
     *
     * @param args runtime arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}