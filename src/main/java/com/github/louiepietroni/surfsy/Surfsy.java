package com.github.louiepietroni.surfsy;

// Import the necessary stuff for JavaFX
import com.github.louiepietroni.surfsy.views.LocationView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

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
        primaryStage.setResizable(false);



        Location devonBeach = new Location(50.28044, -3.89490, "Devon beach");
        LocationView devonBeachView = new LocationView(devonBeach);
        Scene devonViewScene = devonBeachView.getScene();


        primaryStage.setScene(devonViewScene);
        primaryStage.show();

//        Text beach_name = new Text("Big beach");
//        beach_name.setFont(Font.font ("Verdana", 40));
//        beach_name.setWrappingWidth(350);
//        beach_name.setTextAlignment(TextAlignment.CENTER);
//
//        Rectangle rect = new Rectangle(350, 100);
//        Rectangle rect2 = new Rectangle(100, 50);


//        VBox vbox = new VBox();
//        vbox.getChildren().addAll(beach_name, rect);
//
//        Scene beach_view = new Scene(vbox, 350, 700);
//        primaryStage.setScene(beach_view);
//        primaryStage.show();
    }
}
