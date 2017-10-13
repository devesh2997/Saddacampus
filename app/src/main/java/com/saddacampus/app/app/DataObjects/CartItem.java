package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;

/**
 * Created by Devesh Anand on 24-06-2017.
 */

public class CartItem implements Serializable{

    private RestaurantItem restaurantItem;
    private int itemQuantity;

    public RestaurantItem getRestaurantItem() {
        return restaurantItem;
    }

    public void setRestaurantItem(RestaurantItem restaurantItem) {
        this.restaurantItem = restaurantItem;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public CartItem(RestaurantItem restaurantItem, int itemQuantity) {

        this.restaurantItem = restaurantItem;
        this.itemQuantity = itemQuantity;
    }

    public double getCartItemPrice(){
        return ((double)itemQuantity*restaurantItem.getPrice());
    }

}
