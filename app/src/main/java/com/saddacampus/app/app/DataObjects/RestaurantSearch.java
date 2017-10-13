package com.saddacampus.app.app.DataObjects;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class RestaurantSearch {

    private ArrayList<Restaurant> restaurantsFound = new ArrayList<>();
    private int restaurantsFoundCount;

    public RestaurantSearch(ArrayList<Restaurant> restaurantsFound, int restaurantsFoundCount) {
        this.restaurantsFound = restaurantsFound;
        this.restaurantsFoundCount = restaurantsFoundCount;
    }

    public ArrayList<Restaurant> getRestaurantsFound() {
        return restaurantsFound;
    }

    public void setRestaurantsFound(ArrayList<Restaurant> restaurantsFound) {
        this.restaurantsFound = restaurantsFound;
    }

    public int getRestaurantsFoundCount() {
        return restaurantsFoundCount;
    }

    public void setRestaurantsFoundCount(int restaurantsFoundCount) {
        this.restaurantsFoundCount = restaurantsFoundCount;
    }
}
