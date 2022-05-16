package com.github.louiepietroni.surfsy.views;

import com.github.louiepietroni.surfsy.Location;
import javafx.stage.Stage;

import java.util.*;

public class ViewManager {
	private final Stage primaryStage;
	private List<Location> locations;

	private FavouritesView favouritesView;
	private final Map<Location, LocationView> locationViews = new HashMap<>();

	public ViewManager(Stage primaryStage) {
		// Set up the main viewing window
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Surfsy");
		primaryStage.setResizable(false);

		// Load the saved locations
		loadLocations();

		// This will create a location view for the first location and show its scene
		setSceneToLocationView(locations.get(0));

		// Show the primary stage
		primaryStage.show();
	}

	private void loadLocations() {
		// TODO: create a location for each saved location by calling
		// Location.loadFavourites(), passing in a file or something and save all these
		// to locations as a list

		Location thurlestoneBeach = new Location(50.25993, -3.86041, "Thurlestone Beach");
		Location fistralBay = new Location(50.41747, -5.10384, "Fistral Bay");
		locations = new ArrayList<>(Arrays.asList(thurlestoneBeach, fistralBay));
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
		favouritesView = new FavouritesView(locations);
	}

	public void setSceneToFavouritesView() {
		// If the favourites view hasn't been created yet, then make it
		if (favouritesView == null) {
			createFavouritesView();
		}
		// Get the favourites scene and show it
		primaryStage.setScene(favouritesView.getScene());
	}

	public void setSceneToAddSuggestedView(){
		//TODO: create scene
	}

	public void setSceneToAddMapView(){
		//TODO: create scene
	}
}
