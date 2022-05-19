package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Surfsy;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CameraView {
    private final HBox cameraContainer = new HBox();
    private final VBox BigBox = new VBox(cameraContainer);

    private final Scene scene = new Scene(BigBox,350,700);

    public Scene getScene() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
        return scene;
    }
}
