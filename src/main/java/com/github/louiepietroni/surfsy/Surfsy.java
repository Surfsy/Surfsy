package com.github.louiepietroni.surfsy;

import com.github.louiepietroni.surfsy.views.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;


// The main class has to extend Application
public class Surfsy extends Application {
    private static ViewManager viewManager;
    //    This is so the app can run even without certain JavaFX configurations
    public static void main(String[] args) {
        launch(args);
    }

    //    This is the main starting point of the app
    @Override
    public void start(Stage primaryStage) {
//        Create a view manager which handles the operation of the app
        viewManager = new ViewManager(primaryStage);
        viewManager.initializeViews();
    }

    public static ViewManager getViewManager() {
//        Provide access to the view manager so that we access its functions
        return viewManager;
    }
}
