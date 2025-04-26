package com.slinky.wordcheat;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {
    
    private GameController controller;
    
    @Override
    public void start(Stage stage) {
        controller = new GameController("mom");
        Scene scene = new Scene(controller.getMainView(), 900, 800);
        
        stage.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
}