package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.CheckedOutputStream;

public class LocationView {
    private final Location location;
    private final VBox vBox = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(vBox);
    private final HBox hbox = new HBox();

    private final VBox outside = new VBox(scrollPane, hbox);

    private final Scene scene = new Scene(outside, 350, 700);
    private final Text title;
    private StackPane map;
    private final List<StackPane> weatherFeatures = new ArrayList<>();


    public LocationView(Location location) {
        this.location = location;
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setBorder(Border.EMPTY);
        hbox.setSpacing(-1);

//        Create title view
        title = createTextView();

//        Create map view
        map = createMapView();

//        Create feature views
        for (String feature : location.getWeatherFeatures()) {
            List<Double> data = location.getData(feature);
            weatherFeatures.add(createDataView(feature, data));
        }

//        Create day views
        for (int i = 0; i < 7; i++) {
            hbox.getChildren().add(createDayView(i));
        }
        Button button = new Button();
        button.setPrefSize(100, 42);
        button.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
        hbox.getChildren().add(button);
        button.setOnAction(e -> System.out.println("This"));

        button.getOnMouseClicked();

        showElements();
    }

    private void showElements() {
        vBox.setSpacing(5);
        vBox.getChildren().addAll(title, map);
        vBox.getChildren().addAll(weatherFeatures);
    }

    public Scene getScene() {
        return scene;
    }

    public static StackPane createDataView(String name, List<Double> data) {
        StackPane dataView = new StackPane();
        dataView.setMinSize(330, 120);
//TODO: We need to plot the data, we need to create our own plot class which plots using lines
        NumberAxis xAxis = new NumberAxis(0, 24, 1);
        NumberAxis yAxis = new NumberAxis(0, 1, 1);
        LineChart linechart = new LineChart(xAxis, yAxis);
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < 24; i++) {
            series.getData().add(new XYChart.Data(i, data.get(i)));
        }
        linechart.getData().add(series);
        linechart.setMaxSize(300, 40);

        Rectangle rect = new Rectangle(330, 120);
        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        dataView.getChildren().add(rect);

//        dataView.getChildren().add(linechart);
        return dataView;
    }

    public StackPane createMapView() {
        StackPane mapView = new StackPane();
        Rectangle rect = new Rectangle(330, 240);

        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        mapView.getChildren().add(rect);
        return mapView;
    }

    public Text createTextView() {
        Text text = new Text(location.getName());
        text.setFont(Font.font ("Verdana", 40));
        text.setWrappingWidth(350);
        text.setTextAlignment(TextAlignment.CENTER);
        return text;
    }

    public Button createDayView(int i) {
        Button button = new Button(Integer.toString(i));
        button.setMinSize(42, 42);
        button.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        button.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, null, null)));
        return button;
    }
}

