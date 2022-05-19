package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CameraView {
    private final Location location;
    private final HBox cameraContainer = new HBox();
    private final VBox BigBox = new VBox(cameraContainer);

    private final Scene scene = new Scene(BigBox,350,700);

    public CameraView(Location location){
        this.location = location;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
        cameraContainer.setPrefSize(350,650);
        cameraContainer.setStyle("-fx-background-image: camera-images/beach1.png");
    }

    public Scene getScene() {
        return scene;
    }
}
