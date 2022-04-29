package com.github.louiepietroni.surfsy;

// Import the necessary stuff for JavaFX
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// The main class has to extend Application
public class ExampleJavaFX extends Application {
//    This is so the app can run even without certain JavaFX configurations
    public static void main(String[] args) {
        launch(args);
    }

//    This is the main starting point of the app
    @Override
    public void start(Stage primaryStage) {
//        The primary stage is like the window which opens
        primaryStage.setTitle("Surfsy");

//        A button is a node, which can be created
        Button btn = new Button();
//        Setting the text and giving it an event handler, which will be used when clicked
        btn.setText("This button hasn't been pressed");
        btn.setOnAction(event -> {
            System.out.println("Button pressed");
            btn.setText("This button has been pressed");
        });

//        The stack pane is a resizable layout node which we will draw all things to
        StackPane root = new StackPane();
//        We add the button to the root by getting its children then calling add with the button
        root.getChildren().add(btn);
//        The scene sits inside the window and makes it visible
        primaryStage.setScene(new Scene(root, 350, 700));
//        Make the window visible
        primaryStage.show();
    }
}
