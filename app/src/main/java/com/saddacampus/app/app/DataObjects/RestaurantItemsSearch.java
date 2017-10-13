package com.saddacampus.app.app.DataObjects;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class RestaurantItemsSearch {

    private Restaurant restaurantSearched;
    private ArrayList<RestaurantItem> foundRestaurantItems;
    private int foundItemsCount;

    public RestaurantItemsSearch(Restaurant restaurantSearched, ArrayList<RestaurantItem> foundRestaurantItems, int foundItemsCount) {
        this.restaurantSearched = restaurantSearched;
        this.foundRestaurantItems = foundRestaurantItems;
        this.foundItemsCount = foundItemsCount;
    }

    public Restaurant getRestaurantSearched() {
        return restaurantSearched;
    }

    public void setRestaurantSearched(Restaurant restaurantSearched) {
        this.restaurantSearched = restaurantSearched;
    }

    public ArrayList<RestaurantItem> getFoundRestaurantItems() {
        return foundRestaurantItems;
    }

    public void setFoundRestaurantItems(ArrayList<RestaurantItem> foundRestaurantItems) {
        this.foundRestaurantItems = foundRestaurantItems;
    }

    public int getFoundItemsCount() {
        return foundItemsCount;
    }

    public void setFoundItemsCount(int foundItemsCount) {
        this.foundItemsCount = foundItemsCount;
    }
}
