package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CameraView {
    private final Location location;
    private final HBox cameraContainer = new HBox();

    private final BorderPane exitBox = new BorderPane();

    private final VBox BigBox = new VBox(cameraContainer, exitBox);

    private final Scene scene = new Scene(BigBox,350,700);

    private static ArrayList<String> filenames = new ArrayList<>();

    public CameraView(Location location){
        this.location = location;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());

        setImage();

        addBackButton();
    }

    public Scene getScene() {
        return scene;
    }

    public static void loadImages(){
        File[] files = new File("src/main/resources/camera-images").listFiles();
        for (File f:files) {
            filenames.add(f.getName());
        }
    }


    private void setImage(){
        cameraContainer.setPrefSize(350,640);
        cameraContainer.setStyle(String.format("-fx-background-image: url(camera-images/%s)", filenames.get(ThreadLocalRandom.current().nextInt(0, filenames.size()))));
    }

    private void addBackButton(){
        StackPane plus = new StackPane();
        plus.setMinSize(60, 60);

        StackPane buff = new StackPane();
        buff.setMinSize(290, 60);

        JFXButton exitButton = new JFXButton();
        exitButton.setButtonType(JFXButton.ButtonType.RAISED);
        exitButton.setStyle("-fx-background-radius: 30");
        exitButton.getStyleClass().add("plus-button");
        exitButton.setPrefSize(45, 45);
        exitButton.setOnAction(e -> Surfsy.getViewManager().setSceneToLocationView(location));

        plus.getChildren().add(exitButton);

        exitBox.setCenter(exitButton);
        exitBox.setLeft(buff);
    }
}
