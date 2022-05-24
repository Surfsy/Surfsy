package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;

import javafx.css.PseudoClass;
import javafx.scene.input.MouseEvent;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class FavouritesView {
	private final List<Location> favourites;
	// The vbox holds the title and all the favourite locations
	private final VBox favouritesVBox = new VBox();
	private final HBox plusBox = new HBox();
	// The scroll pane holds the favouritesVBox and allows it to scroll if needed
	private final ScrollPane favouritesScrollPane = new ScrollPane(favouritesVBox);

	private final VBox BigBox = new VBox(favouritesScrollPane, plusBox); // Lost in a dream, snake eater
	private final StackPane rootStack = new StackPane(BigBox);

	// The scene holds the whole view which is inside the scroll pane
	private final Scene scene = new Scene(rootStack, 350, 700);

	private BorderPane titlePane;

	private JFXButton themeButton;

	private List<JFXButton> locationSummaryButtons = new LinkedList<>();
	private static boolean favouritesViewInEditMode = false;
	private JFXNodesList editButtonNodeList = new JFXNodesList();

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

		//When the page is reloaded we want it to maintain the state it was in before
		setState();
	}

	private void configureViews() {
		// Setup the scrollpane
		favouritesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		favouritesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		favouritesScrollPane.setBorder(Border.EMPTY);
		favouritesScrollPane.setMinSize(350, 630);
		favouritesScrollPane.setStyle("-fx-padding: 0");

		// Setup the favourites vBox
		favouritesVBox.setSpacing(15);
		favouritesVBox.setAlignment(Pos.TOP_CENTER);
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
			var locationSummary = createLocationSummary(location, (favourites.size() > 1));
			locationSummaryButtons.add(locationSummary);
			addWidgetToFavouritesVBox(locationSummary);
		}
	}

	private void deleteLocation(Location location) {
		Location.removeFromFile(location, "locations.json");
		Surfsy.getViewManager().setLocationsHaveChanged();
		Surfsy.getViewManager().setSceneToFavouritesView();

	}

	private JFXButton createLocationSummary(Location location, boolean moreThanOneLocation) {
		// Create a summary of a location
		// TODO: Style the summary and include information about the locations data

		var currentHour = Integer.parseInt(new SimpleDateFormat("hh").format(new Date()));

		var windSpeed = location.getDataAtTime("Wind Speed", 0, currentHour);
		var airTemperature = location.getDataAtTime("Air Temperature", 0, currentHour);

		Node temperatureBox = new VBox(ViewManager.createParagraphText(String.format("%.2f mph", windSpeed)));
		Node windBox = new VBox(ViewManager.createParagraphText(String.format("%.2f ºC", airTemperature)));
		Node dataBox = new VBox(temperatureBox, windBox);

		JFXButton delete = ViewManager.createButton("🚮");
		delete.setButtonType(JFXButton.ButtonType.RAISED);
		delete.setStyle("-fx-background-radius: 30");
		delete.setPrefSize(60, 60);
		delete.setVisible(false);

		// Create the deletion dialogue
		var dialog = new JFXDialog();

		var dialogue_contents = new BorderPane();
		var dialogue_label = ViewManager.createParagraphText("Are you sure you wish to delete this?");
		final var spacer0 = new Region();
		final var spacer1 = new Region();
		final var spacer2 = new Region();
		// Make it always grow or shrink according to the available space
		HBox.setHgrow(spacer0, Priority.ALWAYS);
		HBox.setHgrow(spacer1, Priority.ALWAYS);
		HBox.setHgrow(spacer2, Priority.ALWAYS);

		var dialogue_confirm = ViewManager.createButton("Confirm");
		var dialogue_cancel = ViewManager.createButton("Cancel");

		var dialogue_buttons = new HBox(spacer0, dialogue_confirm, spacer1, dialogue_cancel, spacer2);

		Insets insets = new Insets(10);

		dialogue_label.getStyleClass().add("p");
		dialogue_contents.setCenter(dialogue_label);
		dialogue_contents.setBottom(dialogue_buttons);
		dialogue_contents.getStyleClass().add("border-pane");
		// Give a nice margin around elements
		BorderPane.setMargin(dialogue_label, insets);
		BorderPane.setMargin(dialogue_buttons, insets);

		dialog.setContent(dialogue_contents);
		// Only way to access background property of dialogue
		((StackPane) dialogue_contents.getParent()).setBackground(null);

		dialogue_confirm.setOnAction(e -> deleteLocation(location));
		dialogue_cancel.setOnAction(e -> dialog.close());
		delete.setOnAction(e -> dialog.show(rootStack));

		VBox buffer = new VBox();
		buffer.setMinSize(10, 10);
		HBox graphic;
		//if (moreThanOneLocation) {
			graphic = new HBox(delete, buffer, dataBox);
		//} else {
		//	graphic = new HBox(dataBox);
		//}
		graphic.setAlignment(Pos.CENTER_LEFT);

		var locationSummary = new JFXButton(location.getName(), graphic);
		locationSummary.setButtonType(ButtonType.RAISED);
		locationSummary.getStyleClass().addAll("widget-favourite-button", "widget-labelled");
		locationSummary.setPrefSize(330, 120);
		locationSummary.setAlignment(Pos.BASELINE_LEFT);
		locationSummary.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToLocationView(location));



		return locationSummary;

		// locationSummary.setMinSize(330, 120);

		// Rectangle rect = new Rectangle(330, 120);
		// rect.getStyleClass().add("widget-rectangle");
		// locationSummary.getChildren().add(rect);
		// Text text = new Text(location.getName());
		// text.getStyleClass().add("text");
		// locationSummary.getChildren().add(text);
		// locationSummary.setOnMouseClicked(e ->
		// Surfsy.getViewManager().setSceneToLocationView(location));

		// return locationSummary;
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
		BorderPane themeButtonBox = new BorderPane();
		themeButton = new JFXButton();
		themeButton.getStyleClass().add("theme-button");
		themeButton.setPrefSize(45, 45);
		if (Objects.equals(Surfsy.getViewManager().getDefaultTheme(), "sunset.css")) {
			themeButton.setOnAction(e -> changeThemeToSunrise());
		} else {
			themeButton.setOnAction(e -> changeThemeToSunset());
		}

		themeButtonBox.setCenter(themeButton);
		HBox themeButtonRightBuffer = new HBox();
		themeButtonRightBuffer.setPrefSize(5, 45);
		themeButtonBox.setRight(themeButtonRightBuffer);
		titlePane.setRight(themeButtonBox);
		HBox leftBuffer = new HBox();
		leftBuffer.setPrefSize(50, 45);
		titlePane.setLeft(leftBuffer);
		if (Objects.equals(Surfsy.getViewManager().getDefaultTheme(), "sunrise.css")) {
			themeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("light"), true);
		}
	}

	private void changeThemeToSunrise() {
		Surfsy.getViewManager().setDefaultTheme("sunrise.css");
		themeButton.setOnAction(e -> changeThemeToSunset());
		Scene scene = Surfsy.getViewManager().getFavouritesView().getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add("sunrise.css");
		for (LocationView view : Surfsy.getViewManager().getLocationViews().values()) {
			scene = view.getScene();
			scene.getStylesheets().clear();
			scene.getStylesheets().add("sunrise.css");
		}
		for (CameraView view : Surfsy.getViewManager().getCameraViews().values()) {
			scene = view.getScene();
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
		for (CameraView view : Surfsy.getViewManager().getCameraViews().values()) {
			scene = view.getScene();
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

		JFXButton plusButton = new JFXButton("Edit");
		plusButton.setButtonType(JFXButton.ButtonType.RAISED);
		plusButton.setStyle("-fx-background-radius: 30");
		plusButton.getStyleClass().add("plus-button");
		plusButton.setPrefSize(45, 45);

		plusButton.setOnAction(e -> {toggleEditMode(); setDeleteButtonVisibility(favouritesViewInEditMode);});

		JFXButton suggestedSearch = new JFXButton("❤");
		suggestedSearch.setButtonType(JFXButton.ButtonType.RAISED);
		suggestedSearch.setStyle("-fx-background-radius: 90");
		suggestedSearch.getStyleClass().add("plus-button");
		suggestedSearch.setPrefSize(35, 35);
		suggestedSearch.setOnAction(e -> Surfsy.getViewManager().setSceneToAddSuggestedView());

		JFXButton mapSearch = new JFXButton("🔍");
		mapSearch.setButtonType(JFXButton.ButtonType.RAISED);
		mapSearch.setStyle("-fx-background-radius: 30");
		mapSearch.getStyleClass().add("plus-button");
		mapSearch.setPrefSize(35, 35);
		mapSearch.setOnAction(e -> Surfsy.getViewManager().setSceneToAddMapView());

		editButtonNodeList.setRotate(180);
		editButtonNodeList.addAnimatedNode(plusButton);
		editButtonNodeList.addAnimatedNode(suggestedSearch);
		editButtonNodeList.addAnimatedNode(mapSearch);
		editButtonNodeList.setSpacing(10d);

		plus.getChildren().add(editButtonNodeList);

		plusBox.getChildren().add(buff);
		plusBox.getChildren().add(plus);
		//plus.onMouseClickedProperty();

	}

	private void toggleEditMode(){

		favouritesViewInEditMode = !favouritesViewInEditMode;

	}

	private void setState(){

		if(favouritesViewInEditMode){
			editButtonNodeList.animateList();
			setDeleteButtonVisibility(true);
		}

	}

	private void setDeleteButtonVisibility(boolean visible){
		for (JFXButton summary: locationSummaryButtons) {
			HBox graphic = (HBox) summary.getGraphic();
			graphic.getChildren().get(0).setVisible(visible);
		}
	}
}
