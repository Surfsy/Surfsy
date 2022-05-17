package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;

import javafx.css.PseudoClass;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FavouritesView {
	private final List<Location> favourites;
	// The vbox holds the title and all the favourite locations
	private final VBox favouritesVBox = new VBox();
	private final HBox plusBox = new HBox();
	// The scroll pane holds the favouritesVBox and allows it to scroll if needed
	private final ScrollPane favouritesScrollPane = new ScrollPane(favouritesVBox);

	private final VBox BigBox = new VBox(favouritesScrollPane, plusBox);// Lost in a dream, snake eater
	// The scene holds the whole view which is inside the scroll pane
	private final Scene scene = new Scene(BigBox, 350, 700);

	private BorderPane titlePane;

	private JFXButton themeButton;

	public FavouritesView(List<Location> favourites) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
		this.favourites = favourites;

		// Configure the views of the scene
		configureViews();

		// Create title view
		addFavouritesTitle();

		// Create button for changing themes
		addThemeButton();

		// Create location summaries
		addFavouritesSummaries();
		// Add new beach
		addFavouritesAddition();
	}

	private void configureViews() {
		// Setup the scrollpane
		favouritesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		favouritesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		favouritesScrollPane.setBorder(Border.EMPTY);
		favouritesScrollPane.setMinSize(350, 630);
		favouritesScrollPane.setStyle("-fx-padding: 0");

		// Setup the favourites vBox
		favouritesVBox.setSpacing(5);
		favouritesVBox.setMinSize(350, Math.max(125 * favourites.size() + 100, 630));

		BigBox.setMinSize(350, 700);

	}

	public Scene getScene() {
		// Return the scene and reset the scroll
		favouritesScrollPane.setVvalue(0);
		return scene;
	}

	private void addFavouritesSummaries() {
		// Add all location summaries
		for (Location location : favourites) {
			StackPane locationSummary = createLocationSummary(location);
			addWidgetToFavouritesVBox(locationSummary);
		}
	}

	private StackPane createLocationSummary(Location location) {
		// Create a summary of a location
		// TODO: Style the summary and include information about the locations data
		StackPane locationSummary = new StackPane();
		locationSummary.setMinSize(330, 120);

		Rectangle rect = new Rectangle(330, 120);
		rect.getStyleClass().add("widget-rectangle");
		locationSummary.getChildren().add(rect);
		Text text = new Text(location.getName());
		text.getStyleClass().add("text");
		locationSummary.getChildren().add(text);
		locationSummary.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToLocationView(location));

		return locationSummary;
	}

	private void addFavouritesTitle() {
		// Add the favourites title
		// TODO: Style to favourites title
		titlePane = new BorderPane();
		Text text = new Text("Favourites");
		text.getStyleClass().add("h1");
		text.setWrappingWidth(200);
		text.setTextAlignment(TextAlignment.CENTER);
		titlePane.setCenter(text);
		addWidgetToFavouritesVBox(titlePane);
	}

	private void addThemeButton() {

		themeButton = new JFXButton();
		themeButton.setStyle("-fx-background-radius: 30");
		themeButton.getStyleClass().add("theme-button");
		themeButton.setPrefSize(35, 35);
		themeButton.setOnAction(e -> changeThemeToSunrise());
		titlePane.setRight(themeButton);
		HBox leftBuffer = new HBox();
		leftBuffer.setPrefSize(35, 35);
		titlePane.setLeft(leftBuffer);
		if (Objects.equals(Surfsy.getViewManager().getDefaultTheme(), "sunrise.css")){
			themeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("light"), true);
		}
	}

	private void changeThemeToSunrise() {
		Surfsy.getViewManager().setDefaultTheme("sunrise.css");
		themeButton.setOnAction(e -> changeThemeToSunset());
		Scene scene = Surfsy.getViewManager().getFavouritesView().getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add("sunrise.css");
		for (LocationView lview : Surfsy.getViewManager().getLocationViews().values()) {
			scene = lview.getScene();
			scene.getStylesheets().clear();
			scene.getStylesheets().add("sunrise.css");
		}
		themeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("light"), true);
	}

	private void changeThemeToSunset() {
		Surfsy.getViewManager().setDefaultTheme("sunset.css");
		themeButton.setOnAction(e -> changeThemeToSunrise());
		Scene scene = Surfsy.getViewManager().getFavouritesView().getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add("sunset.css");
		for (LocationView lview : Surfsy.getViewManager().getLocationViews().values()) {
			scene = lview.getScene();
			scene.getStylesheets().clear();
			scene.getStylesheets().add("sunset.css");
		}
		themeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("light"), false);
	}

	private void addWidgetToFavouritesVBox(Node widget) {
		// Adds widgets to the favouritesVBox
		favouritesVBox.getChildren().add(widget);
	}

	private void addFavouritesAddition() {
		HBox box = new HBox();
		// box.rotateProperty();
		StackPane plus = new StackPane();
		plus.setMinSize(60, 60);

		StackPane buff = new StackPane();
		buff.setMinSize(290, 60);

		JFXButton plusButton = new JFXButton("+");
		plusButton.setButtonType(JFXButton.ButtonType.RAISED);
		plusButton.setStyle("-fx-background-radius: 30");
		plusButton.getStyleClass().add("plus-button");
		plusButton.setPrefSize(45, 45);

		JFXButton suggestedSearch = new JFXButton("R");
		suggestedSearch.setButtonType(JFXButton.ButtonType.RAISED);
		suggestedSearch.setStyle("-fx-background-radius: 90");
		suggestedSearch.getStyleClass().add("plus-button");
		suggestedSearch.setPrefSize(35, 35);
		suggestedSearch.setOnAction(e -> Surfsy.getViewManager().setSceneToAddSuggestedView());

		JFXButton mapSearch = new JFXButton("+");
		mapSearch.setButtonType(JFXButton.ButtonType.RAISED);
		mapSearch.setStyle("-fx-background-radius: 30");
		mapSearch.getStyleClass().add("plus-button");
		mapSearch.setPrefSize(35, 35);
		mapSearch.setOnAction(e -> Surfsy.getViewManager().setSceneToAddMapView());

		JFXNodesList nodesList = new JFXNodesList();
		nodesList.setRotate(180);
		nodesList.addAnimatedNode(plusButton);
		nodesList.addAnimatedNode(suggestedSearch);
		nodesList.addAnimatedNode(mapSearch);
		nodesList.setSpacing(10d);

		plus.getChildren().add(nodesList);

		plusBox.getChildren().add(buff);
		plusBox.getChildren().add(plus);
		// plus.onMouseClickedProperty(foo());

	}
}
