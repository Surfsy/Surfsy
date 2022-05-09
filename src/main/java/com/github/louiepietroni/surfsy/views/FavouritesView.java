package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Random;

public class FavouritesView {
    private final List<Location> favourites;
//    The vbox holds the title and all the favourite locations
    private final VBox favouritesVBox = new VBox();
//    The scroll pane holds the favouritesVBox and allows it to scroll if needed
    private final ScrollPane favouritesScrollPane = new ScrollPane(favouritesVBox);
//    The scene holds the whole view which is inside the scroll pane
    private final Scene scene = new Scene(favouritesScrollPane, 350, 700);


    public FavouritesView(List<Location> favourites) {
        this.favourites = favourites;

//        Configure the views of the scene
        configureViews();

//        Create title view
        addFavouritesTitle();

//        Create location summaries
        addFavouritesSummaries();
    }

    private void configureViews() {
//        Setup the scrollpane
        favouritesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        favouritesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        favouritesScrollPane.setBorder(Border.EMPTY);


//        Setup the favourites vBox
        favouritesVBox.setSpacing(5);
        favouritesVBox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, null, null)));
        favouritesVBox.setMinSize(350, 700);


    }

    public Scene getScene() {
//        Return the scene and reset the scroll
        favouritesScrollPane.setVvalue(0);
        return scene;
    }

    private void addFavouritesSummaries() {
//        Add all location summaries
        for (Location location : favourites) {
            StackPane locationSummary = createLocationSummary(location);
            addWidgetToFavouritesVBox(locationSummary);
        }
    }

    private StackPane createLocationSummary(Location location) {
//        Create a summary of a location
//        TODO: Style the summary and include information about the locations data
        StackPane locationSummary = new StackPane();
        locationSummary.setMinSize(330, 120);

        Rectangle rect = new Rectangle(330, 120);
        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        locationSummary.getChildren().add(rect);
        Text text = new Text(location.getName());
        locationSummary.getChildren().add(text);
        locationSummary.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToLocationView(location));

        return locationSummary;
    }

    private void addFavouritesTitle() {
//        Add the favourites title
//        TODO: Style to favourites title
        Text text = new Text("Favourites");
        text.setFont(Font.font ("Verdana", 40));
        text.setWrappingWidth(350);
        text.setTextAlignment(TextAlignment.CENTER);
        addWidgetToFavouritesVBox(text);
    }

    private void addWidgetToFavouritesVBox(Node widget) {
//        Adds widgets to the favouritesVBox
        favouritesVBox.getChildren().add(widget);
    }
}

