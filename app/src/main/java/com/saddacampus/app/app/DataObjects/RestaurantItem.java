package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;

/**
 * Created by Shubham Vishwakarma on 08-06-2017.
 */

public class RestaurantItem implements Serializable {


    private String name;
    private String productCode;
    private double itemOffer;
    private String category;
    private boolean isRecommended;
    private String imageResource;
    private Restaurant itemRestaurant;
    private double price;
    private boolean isVeg;
    private boolean isFavourite;
    private String currentRatingByUser;
    private float rating;
    private int ratingCount;
    private  boolean hasOffer;
    private int orderCount;

    public RestaurantItem(String name, String productCode, String category,boolean isRecommended,String imageResource, Restaurant itemRestaurant, double price, boolean isVeg, boolean isFavourite, String currentRatingByUser, float rating, int ratingCount, int orderCount, double itemOffer,boolean hasOffer) {
        this.name = name;
        this.itemOffer = itemOffer;
        this.productCode = productCode;
        this.category = category;
        this.isRecommended = isRecommended;
        this.imageResource = imageResource;
        this.itemRestaurant = itemRestaurant;
        this.price = price;
        this.isVeg = isVeg;
        this.isFavourite = isFavourite;
        this.currentRatingByUser = currentRatingByUser;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.orderCount = orderCount;
        this.hasOffer=hasOffer;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public String getImageResource() {
        return imageResource;
    }

    public boolean isHasOffer() {
        return hasOffer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public double getItemOffer(){return this.itemOffer;}

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isVeg() {
        return isVeg;
    }

    public void setVeg(boolean veg) {
        isVeg = veg;
    }

    public Restaurant getItemRestaurant() {
        return itemRestaurant;
    }

    public void setItemRestaurant(Restaurant itemRestaurant) {
        this.itemRestaurant = itemRestaurant;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getCurrentRatingByUser() {
        return currentRatingByUser;
    }

    public void setCurrentRatingByUser(String currentRatingByUser) {
        this.currentRatingByUser = currentRatingByUser;
    }
}
