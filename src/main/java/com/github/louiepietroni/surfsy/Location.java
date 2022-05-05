package com.github.louiepietroni.surfsy;

import java.util.*;

public class Location {
    private final double latitude;
    private final double longitude;
    private String name;
    private Map<String, List<Double>> weatherData = new HashMap<>();
    private List<String> weatherFeatures = Arrays.asList("windSpeed", "waveHeight", "waterTemperature", "windDirection");

    public Location(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public void updateData() {
//TODO: This needs to add a map from name to list of values: windSpeed, [3, 2.3, 6, 3, ...] for first 24 values
        for (String feature : weatherFeatures) {
            List<Double> data = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < 24; i++) {
                data.add(rand.nextDouble());
            }
            weatherData.put(feature, data);
        }
    }

    public List<Double> getData(String feature) {
        if (weatherData == null || weatherData.isEmpty()) {
            updateData();
        }
        return weatherData.get(feature);
    }

    public List<String> getWeatherFeatures() {
        return weatherFeatures;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}
