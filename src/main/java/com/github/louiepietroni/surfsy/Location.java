package com.github.louiepietroni.surfsy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Location {
	// List of all possible weather features for the API
	private static final List<String> allWeatherFeatures = Arrays.asList("Wind Speed", "Wind Direction", "Wave Height",
			"Wave Period", "Air Temperature", "Sea Level", "Water Temperature", "Cloud Cover", "Current Direction",
			"Current Speed", "Precipitation", "Visibility", "Wave Direction");

	private final double latitude;
	private final double longitude;
	private String name;

	// The data string returned from the API
	private String rawData;

	// Map from name of a weather feature, to its data
	private final Map<String, List<Double>> weatherData = new HashMap<>();
	// List of weather features which will be shown for this location
	private final List<String> weatherFeatures;

	public Location(double latitude, double longitude, String name) {
		// Create a new default location
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.weatherFeatures = new ArrayList<>(
				Arrays.asList("Wind Speed", "Wave Height", "Water Temperature", "Wind Direction"));
	}

	public Location(double latitude, double longitude, String name, String rawData, List<String> weatherFeatures) {
		// Create a new location when all the data already exists, this will be when
		// loading from a file
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.rawData = rawData;
		this.weatherFeatures = weatherFeatures;
	}

	private void pullDataFromAPI() {
		// Set the raw data by making a call to the API, which should request all
		// features from allPossibeWeatherFeatures
		// TODO: Make a call and save the string returned from the API to rawData;
		rawData = "This should be the string from the API";
	}

	private void parseRawData() {
		// Parse the raw data for each weather feature of this location
		for (String feature : weatherFeatures) {
			parseFeature(feature);
		}
	}

	public void updateData() {
		// This will update the data by making an API request, then parsing each weather
		// feature
		pullDataFromAPI();
		parseRawData();
	}

	private void loadDataIfNeeded() {
		// If there is no raw data, update the data which will make an API call and
		// parse it
		if (rawData == null) {
			updateData();
		}
		// If there is raw data but it hasn't been parsed, then parse it
		else if (weatherData.isEmpty()) {
			parseRawData();
		}
	}

	public List<Double> getData(String feature, int day) {
		// Check if the data has already been loaded, if not then load it
		loadDataIfNeeded();
		// Return the weather data for the 24 hours corresponding to the day specified
		return weatherData.get(feature).subList(24 * day, 24 * (day + 1));
	}

	private void parseFeature(String feature) {
		// Parse raw data to get out the data corresponding to the passes in feature
		// TODO: Parse the string in rawData for the feature passed in and save in the
		// weatherData map from string to list of double. Will likely want to convert to
		// API string
		List<Double> data = new ArrayList<>();
		Random rand = new Random();
		for (int i = 0; i < 168; i++) {
			data.add(rand.nextDouble());
		}
		weatherData.put(feature, data);
	}

	/** List of weather features we have selected */
	public List<String> getWeatherFeatures() {
		return weatherFeatures;
	}

	private void removeFeature(String feature) {
		// Remove a feature from this locations weather features
		weatherFeatures.remove(feature);
		weatherData.remove(feature);
	}

	private void addFeature(String feature) {
		// Add a feature to this locations weather features
		if (!weatherFeatures.contains(feature)) {
			weatherFeatures.add(feature);
			parseFeature(feature);
		}
	}

	public void updateFeature(String feature, boolean include) {
		// Add or remove a feature from the weather features for this location
		if (include) {
			addFeature(feature);
		} else {
			removeFeature(feature);
		}
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

	public void saveLocation() {
		// TODO: Save this location to a file. This should save name, lat, long, rawData
		// and weatherFeatures
	}

	public String getName() {
		return name;
	}

	public static List<String> getAllWeatherFeatures() {
		// Return a list of all the weather features available from the API
		return allWeatherFeatures;
	}

	public static Location loadFavourites() {
		// Return a list of all locations which have breen saved
		// TODO: Return a list of all locations which have breen saved
		return new Location(1, 2, "");
	}

	public static List<String> getAllWeatherFeaturesForAPI() {
		// Get list of all features in the format needed for the API
		List<String> allWeatherFeaturesForAPI = new ArrayList<String>();
		for (String feature : allWeatherFeatures) {
			allWeatherFeaturesForAPI.add(convertFeatureNameForAPI(feature));
		}
		return allWeatherFeaturesForAPI;
	}

	public static String convertFeatureNameForAPI(String feature) {
		// Convert from feature name to format for the API
		return feature.substring(0, 1).toLowerCase() + feature.substring(1).replaceAll("\\s+", "");
	}

	public static ArrayList<Location> loadFromFile() throws IOException, ParseException {
		//"src/main/java/com/github/louiepietroni/surfsy/locations.json"
		JSONParser parser = new JSONParser();
		ArrayList<Location> locations = new ArrayList<>();
		try(FileReader r = new FileReader("src/main/java/com/github/louiepietroni/surfsy/locations.json")) {
			Object ob = parser.parse(r);
			JSONArray ja = (JSONArray) ob;
			for (Object o : ja) {
				JSONObject jo = (JSONObject) o;
				Location loc = new Location((double) jo.get("latitude"), (double) jo.get("longitude"), (String) jo.get("name"));
				locations.add(loc);
			}
		}
		return locations;



	}
}
