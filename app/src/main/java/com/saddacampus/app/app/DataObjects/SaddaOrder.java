package com.saddacampus.app.app.DataObjects;

import com.saddacampus.app.app.AppController;

import java.io.Serializable;

/**
 * Created by Devesh Anand on 23-07-2017.
 */

public class SaddaOrder implements Serializable{

    private static final String TAG = SaddaOrder.class.getSimpleName();

    private String userName;
    private String userMobile;
    private String userEmail;
    private String userCity;
    private String userCollege;
    private String userHostel;
    private String userRoom;
    private double cartTotal;
    private Restaurant restaurant;
    private Cart cart;

    public SaddaOrder(String userName, String userMobile, String userEmail, String userCollege, String userHostel, String userRoom) {
        this.userName = userName;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
        this.userCollege = userCollege;
        this.userHostel = userHostel;
        this.userRoom = userRoom;

        this.cartTotal = AppController.getInstance().getCartManager().getCart().getTotalAfterDiscount();

        this.userCity = getSelectedCity();
        this.restaurant = AppController.getInstance().getCartManager().getCurrentRestaurantInCart();
        this.cart = AppController.getInstance().getCartManager().getCart();
    }

    public double getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(double cartTotal) {
        this.cartTotal = cartTotal;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    private String getSelectedCity(){
        return AppController.getInstance().getSessionManager().getSelectedCity();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCollege() {
        return userCollege;
    }

    public void setUserCollege(String userCollege) {
        this.userCollege = userCollege;
    }

    public String getUserHostel() {
        return userHostel;
    }

    public void setUserHostel(String userHostel) {
        this.userHostel = userHostel;
    }

    public String getUserRoom() {
        return userRoom;
    }

    public void setUserRoom(String userRoom) {
        this.userRoom = userRoom;
    }

}
