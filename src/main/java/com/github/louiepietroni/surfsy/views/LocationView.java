package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;
import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;

import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
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
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Projection;
import com.sothawo.mapjfx.offline.OfflineCache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class LocationView {

	static final double maxWidgetWidth = 330;

	abstract static class Widget {
		protected Node rootNode;

		protected String name;

		public Widget() {
			rootNode = null;
		}

		public Node getNode() {
			assert rootNode != null;

			return rootNode;
		}

		public String getName() {
			assert name != null;

			return name;
		}

		public abstract void updateWidget();
	}

	/**
	 * Header widget, should be placed at the top, page title will inherit it's name
	 */
	class TitleWidget extends Widget {

		public TitleWidget() {
			super();
			Text text = new Text(location.getName());
			text.getStyleClass().add("text");
			text.getStyleClass().add("h1");
			text.setWrappingWidth(350);
			text.setTextAlignment(TextAlignment.CENTER);
			rootNode = text;
			name = "Title";
		}

		@Override
		public void updateWidget() {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Widget for graphs over 24 hours in a day
	 */
	class GraphWidget extends Widget {

		private final XYChart.Series<Number, Number> series;

		/**
		 * Create the view for a feature, with its name and graph
		 */
		public GraphWidget(String name) {

			super();
			// Create the label text for this view
			var text = new Text(name);
			text.getStyleClass().add("text");
			text.getStyleClass().add("p");
			BorderPane.setAlignment(text, Pos.CENTER);

			// defining the axes
			final var xAxis = new NumberAxis(0, 23, 3);
			final var yAxis = new NumberAxis();

			// creating the chart
			final var lineChart = new LineChart<>(xAxis, yAxis);

			// Make the background transparent, and remove the grid lines
			lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent");
			lineChart.lookup(".chart-vertical-grid-lines ").setStyle("-fx-stroke: transparent");
			lineChart.lookup(".chart-horizontal-grid-lines").setStyle("-fx-stroke: transparent");

			lineChart.setCreateSymbols(false);
			lineChart.setLegendVisible(false);

			// defining a series
			series = new XYChart.Series<>();
			lineChart.getData().add(series);

			this.name = name;
			// Init to blank graph
			for (int i = 0; i < 24; i++) {
				series.getData().add(new XYChart.Data<>(i, 0));
			}

			// Remove the tick marks
			xAxis.setTickMarkVisible(false);
			xAxis.setMinorTickVisible(false);
			yAxis.setTickMarkVisible(false);
			yAxis.setMinorTickVisible(false);

			lineChart.setPrefSize(maxWidgetWidth, 100);

			// Create the panel that will hold all the widgets
			var graphPanel = new BorderPane();
			graphPanel.setMinSize(maxWidgetWidth, 120);
			graphPanel.setMaxWidth(maxWidgetWidth);

			rootNode = graphPanel;
			// integrate with theme
			graphPanel.getStyleClass().add("border-pane");

			// Place the widgets in the panel
			graphPanel.setTop(text);
			graphPanel.setCenter(lineChart);
		}

		@Override
		public void updateWidget() {
			var data = location.getData(name, day);

			for (int i = 0; i < 24; i++) {
				series.getData().get(i).YValueProperty().set(data.get(i));

			}

		}
	}

	class MapWidget extends Widget {
		public MapWidget() {
			super();
			name = "Map";
			// Create the location map
			// TODO: Create a map view of the location

			var mapView = new MapView();

			final OfflineCache offlineCache = mapView.getOfflineCache();
			final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
			// watch the MapView's initialized property to finish initialization
			mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					mapView.setCenter(new Coordinate(location.getLatitude(), location.getLongitude()));
					mapView.setZoom(15);
					// mapView.setBingMapsApiKey("Ao6_AVBvLaiiXjCikwmox14Fp4m4yzayjvBJDUSq0-ZeXPhRCnj5ch1B1S0hQls2");
					mapView.setMapType(MapType.BINGMAPS_AERIAL);
				}
			});

			mapView.initialize(Configuration.builder()
					.projection(Projection.WEB_MERCATOR)
					.showZoomControls(false)
					.build());

			// create the text
			Text mapText = new Text();
			mapText.getStyleClass().add("text");
			mapText.textProperty().bind(Bindings.format("Map: %s", mapView.centerProperty()));

			// change scene to

			var camButton = new JFXButton("Cam 1");
			camButton.getStyleClass().add("btn2");
			camButton.setOnAction(e -> Surfsy.getViewManager().setSceneToCameraView(location));

			// Create the holder and populate it
			var mapHolder = new BorderPane();

			var windArrow = new Button();
			windArrow.getStyleClass().add("wind-arrow");
			StackPane.setAlignment(windArrow, Pos.TOP_LEFT);
			windArrow.setPrefSize(40, 40);

			// RotateTransition rt = new RotateTransition(Duration.millis(3000), windArrow);
			// rt.setByAngle(180);
			// rt.setCycleCount(4);
			// rt.setAutoReverse(true);
			// rt.play();

			var mapLayers = new StackPane();
			mapLayers.getChildren().addAll(mapView, new VBox(camButton, windArrow));

			// Add map to border pane
			BorderPane.setMargin(mapLayers, new Insets(12, 12, 12, 12));

			// Create the time of day slider
			var tod_label = new Text();
			var tod_slider = new Slider(0, 23.99, Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
					+ Calendar.getInstance().get(Calendar.MINUTE) / 60d);

			tod_label.getStyleClass().add("p");
			tod_label.textProperty().bind(
					Bindings.createStringBinding(() -> {
						var time = tod_slider.getValue();

						int hours = (int) time;
						int minutes = (int) (60 * (time - hours));
						return String.format("%02d:%02d", hours, minutes);
					}, tod_slider.valueProperty()));
			var tod = new HBox(tod_label, tod_slider);

			windArrow.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
				var time = tod_slider.getValue();

				var w = location.getDataAtTime("Wind Direction", day, time);

				return w;
			}, tod_slider.valueProperty()));

			mapHolder.getStyleClass().add("border-pane");
			mapHolder.setMinSize(maxWidgetWidth, 240);
			mapHolder.setMaxWidth(maxWidgetWidth);
			mapHolder.setTop(mapText);
			mapHolder.setCenter(mapLayers);
			mapHolder.setBottom(tod);
			rootNode = mapHolder;
		}

		@Override
		public void updateWidget() {
			// TODO Auto-generated method stub

		}
	}

	private final Location location;
	// The widgetVBox holds all widgets, such as name, map and all weather graphs
	private final VBox widgetVBox = new VBox();
	// The scroll VBox is in the scroll pane and holds the widget Vbox and the edit
	// Vbox when needed
	private final VBox scrollVBox = new VBox(widgetVBox);
	// The scroll pane holds the widgetVBox and allows us to scroll down it if
	// needed
	private final ScrollPane widgetScrollPane = new ScrollPane(scrollVBox);
	// The daysHBox holds the day buttons along the bottom and the menu button
	private final HBox daysHBox = new HBox();
	// The outside VBox holds the scroll pane for the widgets and the daysHBox for
	// the options along the bottom
	private final VBox outsideVBox = new VBox(widgetScrollPane, daysHBox);
	// The scene holds the whole view, inside it is the outside VBox
	private final Scene scene = new Scene(outsideVBox, 350, 700);
	// Keep track of the day we are showing data for, with 0 being today
	private int day = 0;

	// The button which will be pressed to change into edit mode
	private JFXButton editFeatureButton;

	/**
	 * Every widget currently on screen
	 */
	private List<Widget> activeWidgets;

	// The Vbox which will contain all the toggles for showing features and will be
	// shown when in edit mode
	private final VBox editListVBox = new VBox();

	public LocationView(Location location) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(Surfsy.getViewManager().getDefaultTheme());
		this.location = location;

		// Create widgets and fill them with data
		generateWidgets();

		// Create day buttons
		addDayButtons();

		// Create menu button
		addMenuButton();

		// Create edit list view
		createEditListView();
	}

	/**
	 * Create the widget V box and every widget in the active widget list
	 */
	private void generateWidgets() {
		// Configure views such as widgetVBox and widgetScrollPane properties
		configureViews();

		// Init widget list with title, map, and all selected features in the location
		activeWidgets = new ArrayList<>(List.of(new TitleWidget(), new MapWidget()));
		for (String feature : location.getWeatherFeatures()) {
			activeWidgets.add(new GraphWidget(feature));
		}

		for (Widget widget : activeWidgets) {
			widgetVBox.getChildren().add(widget.getNode());
		}

		updateWidgets();

		// Create edit feature button
		addEditFeatureButton();

	}

	private void configureViews() {
		// Set up with widget scroll pane
		widgetScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		widgetScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		widgetScrollPane.setBorder(Border.EMPTY);
		widgetScrollPane.setMinSize(352, 658);

		// Set up the widget vBox
		widgetVBox.setSpacing(5);
		widgetVBox.setAlignment(Pos.CENTER);
		widgetVBox.setPadding(new Insets(0, 0, 5, 0));
		widgetVBox.getChildren().clear();

		// Set up the edit list vBox
		editListVBox.setPadding(new Insets(0, 0, 0, 50));
		editListVBox.setSpacing(4);

		// Scroll box is the thing being scrolled, contains all widgets + the edit
		// button
		scrollVBox.setAlignment(Pos.CENTER);

		// Set up the outside vBox
		outsideVBox.setMinSize(350, 700);
		outsideVBox.setAlignment(Pos.CENTER);
	}

	/**
	 * Return the scene for this page, also reset the scroll so its always back at
	 * the top
	 */
	public Scene getScene() {
		//
		widgetScrollPane.setVvalue(0);
		return scene;
	}

	// /**
	// * For each feature of this location, get its data for the current day and add
	// * the feature
	// */
	// private void addLocationFeatures() {

	// for (String feature : location.getWeatherFeatures()) {
	// List<Double> data = location.getData(feature, day);
	// StackPane featureView = createFeatureView(feature, data);
	// widgetVBox.getChildren().add(widgetVBox.getChildren().size() - 1,
	// featureView);
	// }
	// }

	private void addDayButtons() {
		// Create a button for each day and add to the daysHBox
		daysHBox.getStyleClass().add("btn");
		for (int i = 0; i < 7; i++) {
			JFXButton dayButton = createDayButton(i);
			daysHBox.getChildren().add(dayButton);
		}
		updateDayButtons(0);
	}

	private JFXButton createDayButton(int buttonDay) {
		// Creates a button for a day, which will update the displayed data
		// Style this to be a button corresponding to i days from today
		var now = LocalDateTime.now().plusDays(buttonDay);

		JFXButton dayButton = new JFXButton();
		dayButton.setButtonType(JFXButton.ButtonType.RAISED);
		dayButton.getStyleClass().add("day-button");
		dayButton.setPrefSize(42, 42);
		dayButton.setOnAction(e -> updateDay(buttonDay));

		dayButton.setText(now.format(DateTimeFormatter.ofPattern("dd")));

		return dayButton;
	}

	private void addMenuButton() {
		// Add the menu button to the daysHBox
		// TODO: Style the menu button
		StackPane menuButton = new StackPane();
		menuButton.setMinSize(56, 42);
		menuButton.getStyleClass().add("menu-button");
		menuButton.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToFavouritesView());

		Text menuText = new Text("=");
		menuText.getStyleClass().add("text");
		menuButton.getChildren().add(menuText);

		daysHBox.getChildren().add(menuButton);
	}

	private void addEditFeatureButton() {
		// Add the edit button which appears below the features and starts edit mode
		editFeatureButton = new JFXButton();
		editFeatureButton.setButtonType(JFXButton.ButtonType.RAISED);
		editFeatureButton.getStyleClass().add("edit-button");
		editFeatureButton.setMaxSize(100, 24);
		editFeatureButton.setOnAction(e -> enterEditMode());
		editFeatureButton.setText("Edit widgets");

		scrollVBox.getChildren().add(editFeatureButton);
	}

	private void createEditListView() {
		// Create the radioButtons which will be used to toggle features and are stored
		// in the editListVBox
		for (String possibleFeature : Location.getAllWeatherFeatures()) {
			boolean selected = location.getWeatherFeatures().contains(possibleFeature);
			RadioButton editListButton = createEditListButton(possibleFeature, selected);
			editListVBox.getChildren().add(editListButton);
		}
	}

	private RadioButton createEditListButton(String feature, boolean selected) {
		// Create a radio button for the feature
		// TODO: Style this radio button as desired
		RadioButton editListButton = new RadioButton(feature);
		editListButton.setSelected(selected);
		return editListButton;
	}

	private void enterEditMode() {
		// Called by the edit button, shows the editVBox which includes all the toggles
		scrollVBox.getChildren().add(editListVBox);
		editFeatureButton.setOnAction(e -> exitEditMode());
		editFeatureButton.setText("Confirm");
	}

	private void exitEditMode() {
		// Called by the edit button when in edit mode to exit edit mode
		scrollVBox.getChildren().remove(editListVBox);
		editFeatureButton.setOnAction(e -> enterEditMode());
		editFeatureButton.setText("Edit widgets");

		saveEditListToLocation();
	}

	private void saveEditListToLocation() {
		var old_features = new HashSet<>(location.getWeatherFeatures());

		var to_remove = new HashSet<String>();
		var to_add = new HashSet<String>();

		// Take the data from the radio buttons and update the location with the new
		// features, then save the location to file
		for (Node editListItem : editListVBox.getChildren()) {
			RadioButton editListButton = (RadioButton) editListItem;
			String feature = editListButton.getText();
			boolean selected = editListButton.isSelected();

			if (selected && !old_features.contains(feature)) {
				to_add.add(feature);
			} else if (!selected && old_features.contains(feature)) {
				to_remove.add(feature);
			}

			location.updateFeature(feature, selected);
		}
		location.saveLocation();

		System.out.println("Removing " + to_remove);
		// Remove all unused widgets
		for (var n : to_remove) {
			Widget r = null;
			for (var w : activeWidgets) {
				if (Objects.equals(w.getName(), n)) {
					r = w;
				}
			}

			widgetVBox.getChildren().remove(r.getNode());
			activeWidgets.remove(r);
		}
		// Add all new widgets
		for (var n : to_add) {

			var w = new GraphWidget(n);
			activeWidgets.add(w);
			widgetVBox.getChildren().add(w.getNode());

		}

		System.out.println("Adding " + to_add);

		// use this new location info to regenerate widgets
		// generateWidgets();
	}

	private void updateWidgets() {
		for (Widget widget : activeWidgets) {
			widget.updateWidget();
		}
	}

	private void updateDay(int day) {
		// Update the day, change the selected day button, then call
		// updateLocationFeatures to redraw the updated features
		this.day = day;
		updateDayButtons(day);
		updateWidgets();
	}

	private void updateDayButtons(int day) {
		// Reset all the buttons, the update the selected one
		for (int i = 0; i < 7; i++) {
			JFXButton dayButton = (JFXButton) daysHBox.getChildren().get(i);
			dayButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
		}
		JFXButton selectedDayButton = (JFXButton) daysHBox.getChildren().get(day);
		selectedDayButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
	}

	// private void updateLocationFeatures() {
	// // Remove the features and then call the function to create them, so they are
	// // all updated
	// widgetVBox.getChildren().remove(2, widgetVBox.getChildren().size() - 1);
	// addLocationFeatures();
	// }
}