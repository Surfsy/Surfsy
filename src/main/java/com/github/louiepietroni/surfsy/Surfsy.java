package com.github.louiepietroni.surfsy;

// Import the necessary stuff for JavaFX
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// The main class has to extend Application
public class Surfsy extends Application {
    //    This is so the app can run even without certain JavaFX configurations
    public static void main(String[] args) {
        launch(args);
    }

    //    This is the main starting point of the app
    @Override
    public void start(Stage primaryStage) {
//        The primary stage is like the window which opens
        primaryStage.setTitle("Surfsy");

        Button button1 = new Button("Button Number 1");
        Button button2 = new Button("Button Number 2");

        VBox vbox = new VBox();
        vbox.getChildren().add(button1);

        Scene scene = new Scene(vbox, 350, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
