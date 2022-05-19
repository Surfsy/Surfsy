package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.offline.OfflineCache;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javafx.scene.control.TextField;


public class AddMapView {
    //TODO: Implement new scene for adding favourite via lat&long/map##
    private final VBox titleContainer = new VBox();
    private final VBox textEntryNameContainer = new VBox();
    private final HBox textEntryCoordsContainer = new HBox();
    private final HBox navButtons = new HBox();

    private final HBox mapContainer = new HBox();

    private final VBox BigBox = new VBox(titleContainer, textEntryNameContainer,textEntryCoordsContainer,mapContainer, navButtons);
    private final Scene scene = new Scene(BigBox,350,700);

    private final TextField nameEntry = new TextField();
    private final TextField latEntry = new TextField();
    private final TextField longEntry = new TextField();
    private MapView mapView;
    public AddMapView(){
        textEntryNameContainer.setAlignment(Pos.CENTER);
        textEntryCoordsContainer.setAlignment(Pos.CENTER);
        mapContainer.setMinSize(300,200);
        mapContainer.setAlignment(Pos.CENTER);
        navButtons.setAlignment(Pos.CENTER);
        addTitle();
        addNameEntryTextField();
        addLongLatEntry();
        addMap();
        addNavigation();
    }

    public Scene getScene() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
        return scene;
    }

    private void addTitle(){
        BorderPane titlePane = new BorderPane();
        Text text = new Text("Add Unregistered Beach");
        text.getStyleClass().add("h2");
        text.setWrappingWidth(200);
        text.setTextAlignment(TextAlignment.CENTER);
        titlePane.setCenter(text);
        titleContainer.getChildren().add(titlePane);
    }

    private void addNameEntryTextField(){
        nameEntry.setPromptText("Name");
        nameEntry.setFocusTraversable(false);
        nameEntry.getStyleClass().add("textEntry");
        nameEntry.setPrefSize(300, 25);
        nameEntry.setMaxSize(300,25);
        textEntryNameContainer.getChildren().add(nameEntry);
    }

    private void addLongLatEntry(){

        longEntry.setPromptText("Longitude");
        longEntry.setFocusTraversable(false);
        longEntry.getStyleClass().add("textEntry");
        longEntry.setPrefSize(100,25);
        longEntry.setMaxSize(100,25);
        textEntryCoordsContainer.getChildren().add(longEntry);

        VBox buffer1 = new VBox();
        buffer1.setMinSize(45,25);
        textEntryCoordsContainer.getChildren().add(buffer1);

        latEntry.setPromptText("Latitude");
        latEntry.setFocusTraversable(false);
        latEntry.getStyleClass().add("textEntry");
        latEntry.setPrefSize(100,25);
        latEntry.setMaxSize(100,25);
        textEntryCoordsContainer.getChildren().add(latEntry);

        VBox buffer2 = new VBox();
        buffer2.setMinSize(30,25);
        textEntryCoordsContainer.getChildren().add(buffer2);

        JFXButton plusButton = new JFXButton("+");
        plusButton.setButtonType(JFXButton.ButtonType.RAISED);
        plusButton.setStyle("-fx-background-radius: 10");
        plusButton.getStyleClass().add("plus-button");
        plusButton.setPrefSize(25, 25);
        plusButton.setOnAction(e -> setLatLong());
        textEntryCoordsContainer.getChildren().add(plusButton);
    }

    private void addMap(){
        mapView = new MapView();

        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mapView.setCenter(new Coordinate(51d,0d));
                mapView.setZoom(5);
                // mapView.setBingMapsApiKey("Ao6_AVBvLaiiXjCikwmox14Fp4m4yzayjvBJDUSq0-ZeXPhRCnj5ch1B1S0hQls2");
                mapView.setMapType(MapType.BINGMAPS_AERIAL);
            }
        });

        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());

        BorderPane mapBorder = new BorderPane();
        mapBorder.getStyleClass().add("border-pane");
        mapBorder.setMinSize(300,200);
        mapBorder.setCenter(mapView);
        mapContainer.getChildren().add(mapBorder);
    }

    private void setLatLong(){
        latEntry.setText(String.valueOf(mapView.centerProperty().get().getLatitude()));
        longEntry.setText(String.valueOf(mapView.centerProperty().get().getLongitude()));
    }

    private void addNavigation(){
        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        cancelButton.setStyle("-fx-background-radius: 10");
        cancelButton.getStyleClass().add("plus-button");
        cancelButton.setPrefSize(100, 50);
        cancelButton.setOnAction(e -> Surfsy.getViewManager().setSceneToFavouritesView());
        navButtons.getChildren().add(cancelButton);

        VBox buffer = new VBox();
        buffer.setMinSize(50,50);
        navButtons.getChildren().add(buffer);

        JFXButton addButton = new JFXButton("Add");
        addButton.setButtonType(JFXButton.ButtonType.RAISED);
        addButton.setStyle("-fx-background-radius: 10");
        addButton.getStyleClass().add("plus-button");
        addButton.setPrefSize(150, 50);
        addButton.setOnAction(e -> addNewLocation());
        navButtons.getChildren().add(addButton);
    }

    private void addNewLocation(){
        try {
            String name = nameEntry.getText();
            if (name.equals("")){
                throw new IllegalArgumentException();
            }
            double longitude = Double.parseDouble(longEntry.getText());
            double latitude = Double.parseDouble(latEntry.getText());
            Location loc = new Location(latitude,longitude,name);
            Location.addToFile(loc, "locations.json");
            Surfsy.getViewManager().setLocationsHaveChanged();
            Surfsy.getViewManager().setSceneToFavouritesView();

        } catch (NumberFormatException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please enter valid coordinates");
            a.show();
        } catch (IllegalArgumentException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please enter a name");
            a.show();
        }

    }


}
