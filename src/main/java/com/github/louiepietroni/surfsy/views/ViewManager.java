package com.github.louiepietroni.surfsy.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.louiepietroni.surfsy.Location;

import javafx.stage.Stage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ViewManager {

	private final Stage primaryStage;
	private List<Location> locations;

	private FavouritesView favouritesView;

	private AddMapView addMapView;

	private boolean locationsHaveChanged;
	private final Map<Location, LocationView> locationViews = new HashMap<>();

	private String defaultTheme = "sunset.css";

	public ViewManager(Stage primaryStage) {
		// Set up the main viewing window
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Surfsy");
		primaryStage.setResizable(false);
	}

	public void initializeViews(){
		// Load the saved locations
		locationsHaveChanged = false;
		loadLocations();

		// This will create a location view for the first location and show its scene
		setSceneToLocationView(locations.get(0));

		// Show the primary stage
		primaryStage.show();
	}

	protected FavouritesView getFavouritesView() {
		return favouritesView;
	}

	protected Map<Location, LocationView> getLocationViews() {
		return locationViews;
	}

	protected String getDefaultTheme(){
		return defaultTheme;
	}

	protected void setDefaultTheme(String theme){
		defaultTheme = theme;
	}

	private void loadLocations() {


		// Location.loadFavourites(), passing in a file or something and save all these
		// to locations as a list
		//Parsing from locations.json


		locations = Location.loadFromFile("locations.json");


	}

	private void createLocationView(Location location) {
		// Create a location view of the given location
		LocationView locationView = new LocationView(location);
		locationViews.put(location, locationView);
	}

	public void setSceneToLocationView(Location location) {
		// If we haven't already created the location view for this location, create it
		if (!locationViews.containsKey(location)) {
			createLocationView(location);
		}
		// Get the scene for this location view and show it
		primaryStage.setScene(locationViews.get(location).getScene());
	}

	private void createFavouritesView() {
		// Create the favourites view from the list of locations
		loadLocations();
		locationsHaveChanged = false;
		favouritesView = new FavouritesView(locations);
	}

	public void setSceneToFavouritesView() {
		// If the favourites view hasn't been created yet, then make it
		if (favouritesView == null || locationsHaveChanged) {
			createFavouritesView();
		}
		// Get the favourites scene and show it
		primaryStage.setScene(favouritesView.getScene());
	}

	public void setSceneToAddSuggestedView() {
		locationsHaveChanged = true;
		Location.addToFile(new Location(30,30,"test beach"), "locations.json");

		// TODO: create scene
	}
	public void createAddMapView(){
		addMapView = new AddMapView();
	}
	public void setSceneToAddMapView() {
		if (addMapView == null){
			createAddMapView();
		}

		primaryStage.setScene(addMapView.getScene());
		// TODO: create scene
	}

	public void setLocationsHaveChanged(){
		locationsHaveChanged = true;
	}
}
