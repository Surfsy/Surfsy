package com.github.louiepietroni.surfsy;

import java.util.*;

public class Location {
    private static final List<String> allWeatherFeatures = Arrays.asList("airTemperature", "cloudCover", "currentDirection", "currentSpeed", "precipitation", "seaLevel", "visibility", "waterTemperature", "waveDirection", "waveHeight", "wavePeriod", "windDirection", "windSpeed");

    private final double latitude;
    private final double longitude;
    private String name;

//    The data string returned from the API
    private String rawData;

//    Map from name of a weather feature, to its data
    private final Map<String, List<Double>> weatherData = new HashMap<>();
    private final List<String> weatherFeatures = Arrays.asList("windSpeed", "waveHeight", "waterTemperature", "windDirection");

    public Location(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Location(double latitude, double longitude, String name, String rawData) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.rawData = rawData;
    }

    private void pullDataFromAPI() {
//        TODO: Make a call and save the string returned from the API to rawData;
        rawData = "This should be the string from the API";
    }

    private void parseRawData() {
//        TODO: This needs to parse rawData and save to the map from name to list of values
        for (String feature : weatherFeatures) {
            List<Double> data = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < 168; i++) {
                data.add(rand.nextDouble());
            }
            weatherData.put(feature, data);
        }
    }

    public void updateData() {
//        This will update the data by making an API request, then parsing each weather feature
        pullDataFromAPI();
        parseRawData();
    }

    private void loadDataIfNeeded() {
//        If there is no raw data, update the data which will make an API call and parse it
        if (rawData == null) {
            updateData();
        }
//        If there is raw data but it hasn't been parsed, then parse it
        else if (weatherData.isEmpty()) {
            parseRawData();
        }
    }

    public List<Double> getData(String feature, int day) {
//        Check if the data has already been loaded, if not then load it
        loadDataIfNeeded();
//        Return the weather data for the 24 hours corresponding to the day specified
        return weatherData.get(feature).subList(24 * day, 24 * (day + 1));
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

    public static List<String> getAllWeatherFeatures() {
        return allWeatherFeatures;
    }
}
