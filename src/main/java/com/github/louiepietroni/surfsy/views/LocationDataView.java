package com.github.louiepietroni.surfsy.views;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Random;

public class LocationDataView {
    public static StackPane createDataView(String name, List<Double> data) {
        StackPane dataView = new StackPane();
        Rectangle rect = new Rectangle(330, 120);

        Random rand = new Random();
        rect.setFill(Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
        dataView.getChildren().add(rect);
        return dataView;
    }
}
