package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.sothawo.mapjfx.Configuration;
import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;

import javafx.beans.binding.Bindings;
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
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Projection;
import com.sothawo.mapjfx.offline.OfflineCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationView {

	static final double maxWidgetWidth = 330;

	abstract static class Widget {
		protected Node rootNode;

		public Widget() {
			rootNode = null;
		}

		public Node getNode() {
			assert rootNode != null;

			return rootNode;
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

		private final String name;
		private final XYChart.Series<Number, Number> series;

		/**
		 * Create the view for a feature, with its name and graph
		 */
		public GraphWidget(String name) {

			super();

			// Create the label text for this view
			Text text = new Text(name);
			text.getStyleClass().add("text");
			text.getStyleClass().add("p");

			// defining the axes
			final var xAxis = new NumberAxis(0, 23, 3);
			final var yAxis = new NumberAxis();

			// creating the chart
			final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

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

			// Create the holder and populate it
			var mapHolder = new BorderPane();

			// TODO: integrate with theme
			mapHolder.getStyleClass().add("border-pane");
			mapHolder.setMinSize(maxWidgetWidth, 240);
			mapHolder.setMaxWidth(maxWidgetWidth);
			mapHolder.setTop(mapText);
			mapHolder.setCenter(mapView);

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
	private StackPane editFeatureButton;

	/**
	 * Every widget currently on screen
	 */
	private List<Widget> activeWidgets;

	// The Vbox which will contain all the toggles for showing features and will be
	// shown when in edit mode
	private final VBox editListVBox = new VBox();

	public LocationView(Location location) {
		scene.getStylesheets().add("styles.css");
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
		for (int i = 0; i < 7; i++) {
			StackPane dayButton = createDayButton(i);
			daysHBox.getChildren().add(dayButton);
		}
		updateDayButtons(0);
	}

	private StackPane createDayButton(int buttonDay) {
		// Creates a button for a day, which will update the displayed data
		// TODO: Style this to be a button corresponding to i days from today
		StackPane dayButton = new StackPane();
		dayButton.setMinSize(42, 42);
		dayButton.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
		dayButton.setOnMouseClicked(e -> updateDay(buttonDay));

		Text dayName = new Text(Integer.toString(buttonDay));
		dayName.getStyleClass().add("text");
		dayButton.getChildren().add(dayName);

		return dayButton;
	}

	private void addMenuButton() {
		// Add the menu button to the daysHBox
		// TODO: Style the menu button
		StackPane menuButton = new StackPane();
		menuButton.setMinSize(56, 42);
		menuButton.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, null, null)));
		menuButton
				.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
		menuButton.setOnMouseClicked(e -> Surfsy.getViewManager().setSceneToFavouritesView());

		Text menuText = new Text("=");
		menuText.getStyleClass().add("text");
		menuButton.getChildren().add(menuText);

		daysHBox.getChildren().add(menuButton);
	}

	private void addEditFeatureButton() {
		// Add the edit button which appears below the features and starts edit mode
		editFeatureButton = new StackPane();
		editFeatureButton.setMaxSize(100, 40);
		editFeatureButton.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, new CornerRadii(10), null)));
		editFeatureButton.setBorder(new Border(
				new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN)));
		editFeatureButton.setOnMouseClicked(e -> enterEditMode());

		Text editText = new Text("Edit widgets");
		editText.getStyleClass().add("text");
		editFeatureButton.getChildren().add(editText);

		widgetVBox.getChildren().add(editFeatureButton);
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
		editFeatureButton.setOnMouseClicked(e -> exitEditMode());
		editFeatureButton.getChildren().clear();
		Text editText = new Text("Confirm");
		editText.getStyleClass().add("text");
		editFeatureButton.getChildren().add(editText);
	}

	private void exitEditMode() {
		// Called by the edit button when in edit mode to exit edit mode
		scrollVBox.getChildren().remove(editListVBox);
		editFeatureButton.setOnMouseClicked(e -> enterEditMode());
		editFeatureButton.getChildren().clear();
		Text editText = new Text("Edit widgets");
		editText.getStyleClass().add("text");
		editFeatureButton.getChildren().add(editText);

		saveEditListToLocation();
	}

	private void saveEditListToLocation() {
		// Take the data from the radio buttons and update the location with the new
		// features, then save the location to file
		for (Node editListItem : editListVBox.getChildren()) {
			RadioButton editListButton = (RadioButton) editListItem;
			String feature = editListButton.getText();
			boolean selected = editListButton.isSelected();
			location.updateFeature(feature, selected);
		}
		location.saveLocation();
		// use this new location info to regenerate widgets
		generateWidgets();
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
			StackPane dayButton = (StackPane) daysHBox.getChildren().get(i);
			dayButton.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, null, null)));
		}
		StackPane selectedDayButton = (StackPane) daysHBox.getChildren().get(day);
		selectedDayButton.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
	}

	// private void updateLocationFeatures() {
	// // Remove the features and then call the function to create them, so they are
	// // all updated
	// widgetVBox.getChildren().remove(2, widgetVBox.getChildren().size() - 1);
	// addLocationFeatures();
	// }
}