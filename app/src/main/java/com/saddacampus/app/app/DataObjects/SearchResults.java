package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class SearchResults implements Serializable{

    private RestaurantSearch restaurantSearchResults;
    private ArrayList<RestaurantItemsSearch> restaurantItemSearchResults = new ArrayList<>();

    public SearchResults() {
    }

    public SearchResults(RestaurantSearch restaurantSearchResults, ArrayList<RestaurantItemsSearch> restaurantItemSearchResults) {
        this.restaurantSearchResults = restaurantSearchResults;
        this.restaurantItemSearchResults = restaurantItemSearchResults;
    }

    public RestaurantSearch getRestaurantSearchResults() {
        return restaurantSearchResults;
    }

    public void setRestaurantSearchResults(RestaurantSearch restaurantSearchResults) {
        this.restaurantSearchResults = restaurantSearchResults;
    }

    public ArrayList<RestaurantItemsSearch> getRestaurantItemSearchResults() {
        return restaurantItemSearchResults;
    }

    public void setRestaurantItemSearchResults(ArrayList<RestaurantItemsSearch> restaurantItemSearchResults) {
        this.restaurantItemSearchResults = restaurantItemSearchResults;
    }
}
