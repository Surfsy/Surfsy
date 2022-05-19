package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class AddSuggestedView {

    private final List<Location> suggested;
    private final VBox titleContainer = new VBox();
    private final VBox suggestedBox = new VBox();
    private final ScrollPane locationScroll = new ScrollPane(suggestedBox);
    private final HBox navButtons = new HBox();
    private final VBox BigBox = new VBox(titleContainer,locationScroll,navButtons);//Engravings offer no tactical advantage whatsoever
    private final Scene scene = new Scene(BigBox, 350, 700);

    public AddSuggestedView(){
        this.suggested = Location.loadFromFile("suggestedLocations.json");
        navButtons.setMinSize(350,60);
        navButtons.setSpacing(5);
        configureScrollPane();
        addButtonsToScroll();
        addTitle();
        addCancelButton();

    }
    public Scene getScene(){
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
        locationScroll.setVvalue(0);
        return scene;
    }

    private void configureScrollPane(){
        locationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        locationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        locationScroll.setBorder(Border.EMPTY);
        locationScroll.setMinSize(350,630);
        locationScroll.setStyle("-fx-padding: 0");

        suggestedBox.setSpacing(5);
        suggestedBox.setAlignment(Pos.TOP_CENTER);
        suggestedBox.setMinSize(350,Math.max(125 * suggested.size() + 100, 630));

        BigBox.setMinSize(350,700);
    }

    private void addButtonsToScroll(){
        //Stolen from favouritesView
        for (Location location: suggested){
            suggestedBox.getChildren().add(createSuggestedButton(location));
        }

    }
    private JFXButton createSuggestedButton(Location location){
        var button = new JFXButton(location.getName());

        button.getStyleClass().addAll("widget-favourite-button", "widget-labelled");
        button.setPrefSize(330,120);
        button.setAlignment(Pos.BASELINE_CENTER);
        button.setOnAction(e -> addToList(location));

        return button;

    }

    private void addToList(Location location){
        Location.addToFile(location,"locations.json");
        Surfsy.getViewManager().setLocationsHaveChanged();
        Surfsy.getViewManager().setSceneToFavouritesView();
    }

    private void addTitle(){
        BorderPane titlePane = new BorderPane();
        Text text = new Text("Add Suggested Beach");
        text.getStyleClass().add("h2");
        text.setWrappingWidth(200);
        text.setTextAlignment(TextAlignment.CENTER);
        titlePane.setCenter(text);
        titleContainer.getChildren().add(titlePane);
    }
    private void addCancelButton(){
        JFXButton cancel = new JFXButton("Cancel");
        cancel.getStyleClass().add("plus-button");
        cancel.setPrefSize(140,40);
        cancel.setMinSize(140,40);
        cancel.setMaxSize(140,40);
        cancel.setOnAction(e -> Surfsy.getViewManager().setSceneToFavouritesView());
        navButtons.getChildren().add(cancel);
    }

}
