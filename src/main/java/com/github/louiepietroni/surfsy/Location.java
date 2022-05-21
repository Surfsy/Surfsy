package com.github.louiepietroni.surfsy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Location {
	public static final String WindSpeed = "Wind Speed",
			WindDirection = "Wind Direction",
			WaveHeight = "Wave Height",
			WavePeriod = "Wave Period",
			AirTemperature = "Air Temperature",
			SeaLevel = "Sea Level",
			WaterTemperature = "Water Temperature",
			CloudCover = "Cloud Cover",
			CurrentDirection = "Current Direction", // TODO: What does this mean?
			CurrentSpeed = "Current Speed", // TODO: What does this mean?
			Precipitation = "Precipitation",
			Visibility = "Visibility",
			WaveDirection = "Wave Direction";
	// List of all possible weather features for the API
	private static final List<String> allWeatherFeatures = Arrays.asList(WindSpeed, WindDirection, WaveHeight,
			WavePeriod, AirTemperature, SeaLevel, WaterTemperature, CloudCover, CurrentDirection,
			CurrentSpeed, Precipitation, Visibility, WaveDirection);

	private static final Map<String, String> allWeatherFormats;

	static {
		allWeatherFormats = new HashMap<>();

		for (String f : List.of(WaveHeight, SeaLevel)) {
			allWeatherFormats.put(f, "%.02fm");
		}

		for (String f : List.of(CurrentSpeed, WindSpeed)) {
			allWeatherFormats.put(f, "%.02fm/s");
		}

		for (String f : List.of(WaterTemperature, AirTemperature)) {
			allWeatherFormats.put(f, "%.02f°C");
		}

		for (String f : List.of(WaveDirection, WindDirection, CurrentDirection)) {
			allWeatherFormats.put(f, "%.02f°");
		}

		// IDK what these units are supposed to be
		for (String f : List.of(Precipitation, Visibility, CloudCover, WavePeriod)) {
			allWeatherFormats.put(f, "%.02f");
		}

	}

	public static String GetFormatForFeature(String feature) {
		return allWeatherFormats.get(feature);
	}

	private final double latitude;
	private final double longitude;
	private String name;

	// Map from name of a weather feature, to its data
	private final Map<String, List<Double>> weatherData;
	// List of weather features which will be shown for this location
	private final List<String> weatherFeatures;

	public Location(double latitude, double longitude, String name) {
		// Create a new default location
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.weatherFeatures = new ArrayList<>(
				Arrays.asList("Wind Speed", "Wave Height", "Water Temperature", "Wind Direction"));
		this.weatherData = APIHandler.fetchLocation(latitude, longitude);
	}

	public Location(double latitude, double longitude, String name, List<String> weatherFeatures) {
		// Create a new location when all the data already exists, this will be when
		// loading from a file
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.weatherFeatures = weatherFeatures;
		this.weatherData = APIHandler.fetchLocation(latitude, longitude);
	}

	public List<Double> getData(String feature, int day) {
		// Return the weather data for the 24 hours corresponding to the day specified
		return weatherData.get(convertFeatureNameForAPI(feature)).subList(24 * day, 24 * (day + 1));
	}

	public double getDataAtTime(String feature, int day, double time) {
		if (time >= 23) {
			return weatherData.get(convertFeatureNameForAPI(feature)).get(24 * day + 23);
		}
		int hour_before = (int) Math.floor(time);
		List<Double> two_hours = weatherData.get(convertFeatureNameForAPI(feature)).subList(24 * day + hour_before,
				24 * day + hour_before + 2);
		double proportion = time % 1;
		return (two_hours.get(1) - two_hours.get(0)) * proportion + two_hours.get(0);
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
		List<String> allWeatherFeaturesForAPI = new ArrayList<>();
		for (String feature : allWeatherFeatures) {
			allWeatherFeaturesForAPI.add(convertFeatureNameForAPI(feature));
		}
		return allWeatherFeaturesForAPI;
	}

	public static String convertFeatureNameForAPI(String feature) {
		// Convert from feature name to format for the API
		return feature.substring(0, 1).toLowerCase() + feature.substring(1).replaceAll("\\s+", "");
	}

	public static ArrayList<Location> loadFromFile(String filename) {
		// "src/main/java/com/github/louiepietroni/surfsy/locations.json"
		try {
			JSONParser parser = new JSONParser();
			ArrayList<Location> locations = new ArrayList<>();
			FileReader r = new FileReader(String.format("src/main/resources/%s", filename));
			Object ob = parser.parse(r);
			JSONArray ja = (JSONArray) ob;
			for (Object o : ja) {
				JSONObject jo = (JSONObject) o;
				Location loc = new Location((double) jo.get("latitude"), (double) jo.get("longitude"),
						(String) jo.get("name"));
				locations.add(loc);
			}

			return locations;
		} catch (IOException e) {
			throw new RuntimeException("Reader Exception, location loading", e);
		} catch (ParseException e) {
			throw new RuntimeException("Parse Exception, location loading", e);
		}
	}

	public static void addToFile(Location location, String filename) {
		try {
			ArrayList<Location> locations = loadFromFile(filename);
			locations.add(location);
			JSONArray jsonArray = new JSONArray();
			for (Location l : locations) {
				JSONObject lj = new JSONObject();
				lj.put("latitude", l.getLatitude());
				lj.put("longitude", l.getLongitude());
				lj.put("name", l.getName());
				jsonArray.add(lj);
			}
			FileWriter w = new FileWriter(String.format("src/main/resources/%s", filename));
			w.write(jsonArray.toJSONString());
			w.flush();

		} catch (IOException e) {
			throw new RuntimeException("IO Exception, location appending", e);
		}

	}

	public static void removeFromFile(Location location, String filename) {
		try {
			ArrayList<Location> locations = loadFromFile(filename);
			locations.add(location);
			JSONArray jsonArray = new JSONArray();
			for (Location l : locations) {
				if (!(l.getName().equals(location.getName()) && l.getLongitude() == location.getLongitude()
						&& l.getLatitude() == location.getLatitude())) {
					JSONObject lj = new JSONObject();
					lj.put("latitude", l.getLatitude());
					lj.put("longitude", l.getLongitude());
					lj.put("name", l.getName());
					jsonArray.add(lj);
				}
			}
			FileWriter w = new FileWriter(String.format("src/main/resources/%s", filename));
			w.write(jsonArray.toJSONString());
			w.flush();

		} catch (IOException e) {
			throw new RuntimeException("IO Exception, location appending", e);
		}
	}
}
