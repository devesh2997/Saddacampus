package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Devesh Anand on 24-06-2017.
 */

public class RestaurantCategory implements Serializable {

    private String categoryName;
    private ArrayList<RestaurantItem> categoryItems;

    public RestaurantCategory(String categoryName, ArrayList<RestaurantItem> categoryItems) {
        this.categoryName = categoryName;
        this.categoryItems = categoryItems;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<RestaurantItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(ArrayList<RestaurantItem> categoryItems) {
        this.categoryItems = categoryItems;
    }
}
