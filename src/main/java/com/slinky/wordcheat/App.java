package com.slinky.wordcheat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.slinky.wordcheat.control.MainController;

/**
 * Entry point for the WordCheat JavaFX application.
 *
 * <p>
 * Initialises and launches the JavaFX environment, constructs the primary
 * {@link Stage}, and displays the main application view managed by
 * {@link com.slinky.wordcheat.control.MainController}.
 *
 * <p>
 * The game is saved under the name given by the {@code --save} argument, and
 * under {@value #DEFAULT_SAVE} when there is none. A name may use letters,
 * digits, hyphens and underscores:
 *
 * <pre>
 *     mvn javafx:run -Djavafx.args="--save=dad"
 * </pre>
 *
 * @author Kheagen Haskins
 * @version 1.0
 */
public class App extends Application {

    /**
     * The save name used when no {@code --save} argument is given.
     */
    public static final String DEFAULT_SAVE = "mom";

    /**
     * Controller coordinating game logic, persistence, and user interface.
     */
    private MainController controller;

    /**
     * Called when the JavaFX application is started.
     *
     * <p>
     * Creates a {@code MainController}, initialises the scene graph with its
     * main view, and shows the primary stage.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        controller = new MainController(resolveSaveName());

        Scene scene = new Scene(controller.getMainView(), 900, 830);
        stage.setTitle("WordCheat");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method for launching the JavaFX application.
     *
     * @param args runtime arguments; {@code --save=<name>} picks the save file
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Reads the save name from the {@code --save} argument, falling back to
     * {@link #DEFAULT_SAVE} when it is missing or holds other characters.
     */
    private String resolveSaveName() {
        String name = getParameters().getNamed().get("save");
        return name != null && name.matches("[A-Za-z0-9_-]+") ? name : DEFAULT_SAVE;
    }

}
