package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class LocationView {
    private final Location location;
//    The widgetVBox holds all widgets, such as name, map and all weather graphs
    private final VBox widgetVBox = new VBox();
//    The scroll VBox is in the scroll pane and holds the widget Vbox and the edit Vbox when needed
    private final VBox scrollVBox = new VBox(widgetVBox);
//    The scroll pane holds the widgetVBox and allows us to scroll down it if needed
    private final ScrollPane widgetScrollPane = new ScrollPane(scrollVBox);
//    The daysHBox holds the day buttons along the bottom and the menu button
    private final HBox daysHBox = new HBox();
//    The outside VBox holds the scroll pane for the widgets and the daysHBox for the options along the bottom
    private final VBox outsideVBox = new VBox(widgetScrollPane, daysHBox);
//    The scene holds the whole view, inside it is the outside VBox
    private final Scene scene = new Scene(outsideVBox, 350, 700);
//    Keep track of the day we are showing data for, with 0 being today
    private int day = 0;

//    The button which will be pressed to change into edit mode
    private StackPane editFeatureButton;

//    The Vbox which will contain all the toggles for showing features and will be shown when in edit mode
    private final VBox editListVBox = new VBox();


    public LocationView(Location location) {
        this.location = location;
//        Configure views such as widgetVBox and widgetScrollPane properties
        configureViews();

//        Create title view
        addLocationTitle();

//        Create map view
        addLocationMap();

//        Create edit feature button
        addEditFeatureButton();

//        Create feature views
        addLocationFeatures();

//        Create day buttons
        addDayButtons();

//        Create menu button
        addMenuButton();

//        Create edit list view
        createEditListView();
    }

    private void configureViews() {
//        Set up with widget scroll pane
        widgetScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        widgetScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        widgetScrollPane.setBorder(Border.EMPTY);
        widgetScrollPane.setMinSize(352, 658);

//        Set up the widget view box
        widgetVBox.setSpacing(5);
        widgetVBox.setAlignment(Pos.CENTER);
        widgetVBox.setPadding(new Insets(0, 0, 5, 0));
        widgetVBox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, null, null)));

        editListVBox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, null, null)));
        editListVBox.setPadding(new Insets(0, 0, 0, 50));
        editListVBox.setSpacing(4);

        outsideVBox.setMinSize(350, 700);
        outsideVBox.setAlignment(Pos.CENTER);
    }

    public Scene getScene() {
//        Return the scene for this page, also reset the scroll so its always back at the top
        widgetScrollPane.setVvalue(0);
        return scene;
    }

    private void addLocationFeatures() {
//        For each feature of this location, get its data for the current day and add the feature
        for (String feature : location.getWeatherFeatures()) {
            List<Double> data = location.getData(feature, day);
            StackPane featureView = createFeatureView(feature, data);
            widgetVBox.getChildren().add(widgetVBox.getChildren().size() - 1, featureView);
        }
    }

    private StackPane createFeatureView(String name, List<Double> data) {
//        Create the view for a feature, with its name and graph
// TODO: Have the name of the feature and a nice plot, which will need to be made with lines

        StackPane dataView = new StackPane();
        dataView.setMinSize(330, 120);
        Rectangle rect = new Rectangle(330, 120);
        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        dataView.getChildren().add(rect);

        Text text = new Text(name);
        dataView.getChildren().add(text);
        Text dataText = new Text(Double.toString(data.get(0)));
        StackPane.setAlignment(dataText, Pos.BOTTOM_CENTER);

        dataView.getChildren().add(dataText);

        return dataView;
    }

    private void addLocationMap() {
//        Create the location map
//        TODO: Create a map view of the location
        StackPane mapView = new StackPane();
        mapView.setMinSize(330, 240);
        Rectangle rect = new Rectangle(330, 240);
        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        mapView.getChildren().add(rect);

        Text mapText = new Text("Map");
        mapView.getChildren().add(mapText);

        widgetVBox.getChildren().add(mapView);
    }

    private void addLocationTitle() {
//        Create and style the location title
//        TODO: Style this title as desired
        Text text = new Text(location.getName());
        text.setFont(Font.font ("Verdana", 40));
        text.setWrappingWidth(350);
        text.setTextAlignment(TextAlignment.CENTER);
        widgetVBox.getChildren().add(text);
    }

    private void addDayButtons() {
//        Create a button for each day and add to the daysHBox
        for (int i = 0; i < 7; i++) {
            StackPane dayButton = createDayButton(i);
            daysHBox.getChildren().add(dayButton);
        }
    }

    private StackPane createDayButton(int buttonDay) {
//        Creates a button for a day, which will update the displayed data
//        TODO: Style this to be a button corresponding to i days from today
        StackPane dayButton = new StackPane();
        dayButton.setMinSize(42, 42);
        dayButton.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, null, null)));
        dayButton.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        dayButton.setOnMouseClicked(e -> updateDay(buttonDay));

        Text dayName = new Text(Integer.toString(buttonDay));
        dayButton.getChildren().add(dayName);

        return dayButton;
    }

    private void addMenuButton() {
//        Add the menu button to the daysHBox
//        TODO: Style the menu button
        StackPane menuButton = new StackPane();
        menuButton.setMinSize(56, 42);
        menuButton.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, null, null)));
        menuButton.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        menuButton.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToFavouritesView());

        Text menuText = new Text("=");
        menuButton.getChildren().add(menuText);

        daysHBox.getChildren().add(menuButton);
    }

    private void addEditFeatureButton() {
//        Add the edit button which appears below the features and starts edit mode
        editFeatureButton = new StackPane();
        editFeatureButton.setMaxSize(100, 40);
        editFeatureButton.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, new CornerRadii(10), null)));
        editFeatureButton.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN)));
        editFeatureButton.setOnMouseClicked(e -> enterEditMode());

        Text editText = new Text("Edit widgets");
        editFeatureButton.getChildren().add(editText);

        widgetVBox.getChildren().add(editFeatureButton);
    }

    private void createEditListView() {
//        Create the radioButtons which will be used to toggle features and are stored in the editListVBox
        for (String possibleFeature : Location.getAllWeatherFeatures()) {
            boolean selected = location.getWeatherFeatures().contains(possibleFeature);
            RadioButton editListButton = createEditListButton(possibleFeature, selected);
            editListVBox.getChildren().add(editListButton);
        }
    }

    private RadioButton createEditListButton(String feature, boolean selected) {
//        Create a radio button for the feature
//        TODO: Style this radio button as desired
        RadioButton editListButton = new RadioButton(feature);
        editListButton.setSelected(selected);
        return editListButton;
    }

    private void enterEditMode() {
//        Called by the edit button, shows the editVBox which includes all the toggles
        scrollVBox.getChildren().add(editListVBox);
        editFeatureButton.setOnMouseClicked(e -> exitEditMode());
        editFeatureButton.getChildren().clear();
        Text editText = new Text("Confirm");
        editFeatureButton.getChildren().add(editText);
    }

    private void exitEditMode() {
//        Called by the edit button when in edit mode to exit edit mode
        scrollVBox.getChildren().remove(editListVBox);
        editFeatureButton.setOnMouseClicked(e -> enterEditMode());
        editFeatureButton.getChildren().clear();
        Text editText = new Text("Edit widgets");
        editFeatureButton.getChildren().add(editText);

        saveEditListToLocation();
    }

    private void saveEditListToLocation() {
//        Take the data from the radio buttons and update the location with the new features, then save the location to file
        for (Node editListItem :  editListVBox.getChildren()) {
            RadioButton editListButton = (RadioButton) editListItem;
            String feature = editListButton.getText();
            boolean selected = editListButton.isSelected();
            location.updateFeature(feature, selected);
        }
        location.saveLocation();
        updateLocationFeatures();
    }


    private void updateDay(int day) {
//        Update the day, then call updateLocationFeatures to redraw the updated features
        this.day = day;
        updateLocationFeatures();
    }

    private void updateLocationFeatures() {
//        Remove the features and then call the function to create them, so they are all updated
        widgetVBox.getChildren().remove(2, widgetVBox.getChildren().size() - 1);
        addLocationFeatures();
    }
}

