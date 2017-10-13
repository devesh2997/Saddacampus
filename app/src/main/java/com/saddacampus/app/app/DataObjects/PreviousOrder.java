package com.saddacampus.app.app.DataObjects;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-07-2017.
 */

public class PreviousOrder {

    private static final String TAG = PreviousOrder.class.getSimpleName();

    private String orderId;
    private String orderTotal;
    private String orderDate;
    private String orderTime;
    private String orderMode;
    private String orderCity;
    private ArrayList<CartItem> cartItems;

    public PreviousOrder(String orderId, String orderTotal, String orderDate, String orderTime, String orderMode, String orderCity, ArrayList<CartItem> cartItems) {
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderMode = orderMode;
        this.orderCity = orderCity;
        this.cartItems = cartItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public String getOrderCity() {
        return orderCity;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }
}
