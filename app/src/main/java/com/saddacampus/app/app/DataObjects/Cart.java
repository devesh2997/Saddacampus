package com.saddacampus.app.app.DataObjects;

import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Devesh Anand on 24-06-2017.
 */

public class Cart implements Serializable{

    private static String TAG = Cart.class.getSimpleName();

    private Restaurant restaurantInCart;
    private LinkedList<CartItem> cartItems;
    private int discount;



    public Cart() {
        this.cartItems = new LinkedList<>();
        restaurantInCart = null;
    }

    public Cart(Restaurant restaurantInCart, LinkedList<CartItem> cartItems) {
        this.restaurantInCart = restaurantInCart;
        this.cartItems = cartItems;

        if(restaurantInCart.getHasNewOffer()==0){
            this.discount = restaurantInCart.getOffer();

        }else{
            this.discount = restaurantInCart.getNewOffer();
        }
        Log.d(TAG,String.valueOf(discount));
    }

    public Restaurant getRestaurantInCart() {
        return restaurantInCart;
    }

    public void setRestaurantInCart(Restaurant restaurantInCart) {
        this.restaurantInCart = restaurantInCart;
    }

    public LinkedList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(LinkedList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public int getCartSize(){
        return cartItems.size();
    }

    public void addItem(CartItem cartItem){
        Boolean found = false;
        for(int i =0 ;i<getCartItems().size(); i++){
            RestaurantItem restaurantItem = getCartItems().get(i).getRestaurantItem();
            if(restaurantItem.getProductCode().equals(cartItem.getRestaurantItem().getProductCode())){
                found = true;
                cartItems.set(i,cartItem);
            }
        }
        if(!found){
            cartItems.add(cartItem);
            restaurantInCart = cartItem.getRestaurantItem().getItemRestaurant();
        }

    }

    public void deleteItem(CartItem cartItem){
        for(int i =0 ;i<getCartItems().size(); i++){
            RestaurantItem restaurantItem = getCartItems().get(i).getRestaurantItem();
            if(restaurantItem.getProductCode().equals(cartItem.getRestaurantItem().getProductCode())){
                cartItems.remove(i);
            }
        }
        if(getCartItems().size()==0){
            clearCart();
        }
    }

    public void updateItemQuantity(CartItem cartItem, int itemQuantity){
        CartItem newCartItem = new CartItem(cartItem.getRestaurantItem(), itemQuantity);
        int oldItemIndex = cartItems.indexOf(cartItem);
        if(itemQuantity == 0){
            deleteItem(cartItem);
        }else{
            cartItems.remove(cartItem);
            cartItems.add(oldItemIndex,newCartItem);
        }

    }

    public double getDiscount(){
        int discount = 0;
        if(!isCartEmpty()){

            if(getRestaurantInCart().getHasNewOffer()==0){
                discount=getRestaurantInCart().getOffer();
            }else{
                discount=getRestaurantInCart().getNewOffer();
            }
        }

        return discount;
    }

    public double getTotalBeforeDiscount(){
        Double price = 0.0;
        for (CartItem cartItem:getCartItems() ) {
            price = price+cartItem.getCartItemPrice();
        }

        String priceString = String.valueOf(price);
        if(priceString.lastIndexOf('.')==(priceString.length()-1)){
            priceString = priceString.substring(0,priceString.lastIndexOf('.')+1);
        }else{
            priceString = priceString.substring(0,priceString.lastIndexOf('.')+2);
        }

        price = Double.parseDouble(priceString);
        return price;
    }

    public double getTotalAfterDiscount(){
        Double price = getTotalBeforeDiscount();
        int discount = 0;
        if(getRestaurantInCart().getHasNewOffer()==0){
            discount=getRestaurantInCart().getOffer();
        }else{
            discount=getRestaurantInCart().getNewOffer();
        }
        price = price - (price*(discount/100.0));
        String priceString = String.valueOf(price);
        if(priceString.lastIndexOf('.')==(priceString.length()-1)){
            priceString = priceString.substring(0,priceString.lastIndexOf('.')+1);
        }else{
            priceString = priceString.substring(0,priceString.lastIndexOf('.')+2);
        }

        price = Double.parseDouble(priceString);

        return price;
    }

    public Cart clearCart(){
        this.cartItems = new LinkedList<>();

        setRestaurantInCart(null);
        this.discount = 0;

        return new Cart();
    }

    public boolean isCartEmpty(){
        if(cartItems.size()==0){
            return true;
        }else {
            return false;
        }
    }

}
