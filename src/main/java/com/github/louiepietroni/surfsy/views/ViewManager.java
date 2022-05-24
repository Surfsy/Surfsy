package com.github.louiepietroni.surfsy.views;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.github.louiepietroni.surfsy.Location;
import com.github.louiepietroni.surfsy.Surfsy;
import com.jfoenix.controls.JFXButton;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ViewManager {

	private final Stage primaryStage;
	private List<Location> locations;

	private FavouritesView favouritesView;

	private AddMapView addMapView;
	private AddSuggestedView addSuggestedView;

	private boolean locationsHaveChanged;
	private final Map<Location, LocationView> locationViews = new HashMap<>();
	private final Map<Location, CameraView> cameraViews = new HashMap<>();

	private String defaultTheme = "sunset.css";
	private String defaultPage = "favourites";
	private String defaultPageName = "null";
	private static final DropShadow dropShadow;

	static {
		dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0, 0, 0, 0.3));
	}

	public ViewManager(Stage primaryStage) {
		// Setup common components

		// Set up the main viewing window
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Surfsy");
		primaryStage.setResizable(false);

		loadDefaults("defaults.json");

		CameraView.loadImages();

	}

	private void loadDefaults(String filename) {
		try {
			JSONParser parser = new JSONParser();
			FileReader r = new FileReader(String.format("src/main/resources/%s", filename));
			JSONObject jo = (JSONObject) parser.parse(r);
			defaultTheme = (String) jo.get("theme");
			JSONObject page = (JSONObject) jo.get("page");
			defaultPage = (String) page.get("type");
			defaultPageName = (String) page.get("name");
		} catch (IOException e) {
			throw new RuntimeException("Reader Exception on loading default settings", e);
		} catch (ParseException e) {
			throw new RuntimeException("Parse Exception on loading default settings", e);
		}
	}

	private void setDefaultValue(String object, Object value){
		String filename = "defaults.json";
		try {
			JSONParser parser = new JSONParser();
			FileReader r = new FileReader(String.format("src/main/resources/%s", filename));
			JSONObject jo = (JSONObject) parser.parse(r);
			jo.put(object,value);
			FileWriter w = new FileWriter(String.format("src/main/resources/%s", filename));
			w.write(jo.toJSONString());
			w.flush();
		} catch (IOException e) {
			throw new RuntimeException("IO Exception on changing default settings", e);
		} catch (ParseException e) {
			throw new RuntimeException("Parse Exception on changing default settings", e);
		}
	}

	public void initializeViews() {
		// Load the saved locations
		locationsHaveChanged = false;
		loadLocations();

		// This will create a location view for the first location and show its scene
		if (Objects.equals(defaultPage, "favourites")){
			setSceneToFavouritesView();
		}
		else {
			try{
				boolean found = false;
				for (Location location: locations){
					if (Objects.equals(location.getName(), defaultPageName)) {
						found = true;
						setSceneToLocationView(location);
						break;
					}
				}
				if (!found) {
					setSceneToFavouritesView();
				}
			} catch (Exception e) {
				setSceneToFavouritesView();
			}
		}


		// Show the primary stage
		primaryStage.show();
	}

	public static DropShadow GetDropShadow() {
		return dropShadow;
	}

	public static JFXButton createButton(String name) {
		var n = new JFXButton(name);
		n.getStyleClass().addAll("plus-button", "widget-labelled");
		n.setPrefHeight(40);
		return n;
	}

	public static Text createParagraphText(String name) {
		var n = new Text(name);
		n.getStyleClass().add("p");
		return n;
	}

	public static Text createHeadingText(String name) {
		var n = new Text(name);
		n.getStyleClass().add("h2");
		return n;
	}

	protected FavouritesView getFavouritesView() {
		return favouritesView;
	}

	protected Map<Location, LocationView> getLocationViews() {
		return locationViews;
	}

	protected Map<Location, CameraView> getCameraViews() {
		return cameraViews;
	}

	protected String getDefaultTheme() {
		return defaultTheme;
	}

	protected void setDefaultTheme(String theme) {
		defaultTheme = theme;
		setDefaultValue("theme", theme);
	}

	protected void setDefaultPage(JSONObject page) {
		setDefaultValue("page", page);
	}

	private void loadLocations() {

		// Location.loadFavourites(), passing in a file or something and save all these
		// to locations as a list
		// Parsing from locations.json

		locations = Location.loadFromFile("locations.json");

	}

	private void createLocationView(Location location) {
		// Create a location view of the given location
		LocationView locationView = new LocationView(location);
		locationViews.put(location, locationView);
	}

	public void setSceneToLocationView(Location location) {
		JSONObject page = new JSONObject();
		page.put("type", "beach");
		page.put("name", location.getName());
		setDefaultValue("page",page);
		// If we haven't already created the location view for this location, create it
		if (!locationViews.containsKey(location)) {
			createLocationView(location);
		}
		// Get the scene for this location view and show it
		primaryStage.setScene(locationViews.get(location).getScene());
	}

	private void createCameraView(Location location) {
		// Create the camera view from the list of locations
		CameraView cameraView = new CameraView(location);
		cameraViews.put(location, cameraView);
	}

	public void setSceneToCameraView(Location location) {
		// If we haven't already created the camera view for this location, create it
		if (!cameraViews.containsKey(location)) {
			createCameraView(location);
		}
		// Get the scene for this location view and show it
		primaryStage.setScene(cameraViews.get(location).getScene());
	}

	private void createFavouritesView() {
		// Create the favourites view from the list of locations
		loadLocations();
		locationsHaveChanged = false;
		favouritesView = new FavouritesView(locations);
	}

	public void setSceneToFavouritesView() {
		JSONObject page = new JSONObject();
		page.put("type", "favourites");
		page.put("name", "null");
		setDefaultValue("page",page);
		// If the favourites view hasn't been created yet, then make it
		if (favouritesView == null || locationsHaveChanged) {
			createFavouritesView();
		}
		// Get the favourites scene and show it
		primaryStage.setScene(favouritesView.getScene());
	}

	private void createAddSuggestedView() {
		addSuggestedView = new AddSuggestedView();
	}

	public void setSceneToAddSuggestedView() {
		if (addSuggestedView == null) {
			createAddSuggestedView();
		}
		primaryStage.setScene(addSuggestedView.getScene());

		// TODO: create scene
	}

	private void createAddMapView() {
		addMapView = new AddMapView();
	}

	public void setSceneToAddMapView() {

		createAddMapView();

		primaryStage.setScene(addMapView.getScene());
		// TODO: create scene
	}

	public void setLocationsHaveChanged() {
		locationsHaveChanged = true;
	}
}
