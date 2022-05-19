package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class CameraView {
    private final Location location;
    private final HBox cameraContainer = new HBox();

    private final BorderPane exitBox = new BorderPane();

    private final VBox BigBox = new VBox(cameraContainer, exitBox);

    private final Scene scene = new Scene(BigBox,350,700);

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

    private void setImage(){
        cameraContainer.setPrefSize(350,640);
        cameraContainer.setStyle("-fx-background-image: url(camera-images/beach1.png)");
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
