package com.github.louiepietroni.surfsy.views;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddMapView {
    //TODO: Implement new scene for adding favourite via lat&long/map##
    private final HBox titleContainer = new HBox();
    private final HBox textEntryNameContainer = new HBox();
    private final HBox textEntryCoordsContainer = new HBox();
    private final HBox floorBuffer = new HBox();

    private final HBox mapContainer = new HBox();

    private final VBox BigBox = new VBox(titleContainer, textEntryNameContainer,textEntryCoordsContainer,mapContainer, floorBuffer);
    private final Scene scene = new Scene(BigBox,350,700);
    public AddMapView(){

    }

    public Scene getScene() {
        return scene;
    }
}
