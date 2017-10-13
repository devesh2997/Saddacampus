package com.saddacampus.app.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.saddacampus.app.R;
import com.saddacampus.app.activity.CartActivity;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Devesh Anand on 24-06-2017.
 */

public class CartManager {

    private static String TAG = CartManager.class.getSimpleName();

    private SessionManager sessionManager;
    private Cart cart;
    private Context context;

    private boolean dialogOk ;

    public CartManager(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        if(sessionManager.isCurrentCartSet()){
            this.cart = getCart();
        }else {
            this.cart = new Cart();
        }
    }

    public Cart getCart(){
        if(sessionManager.getCurrentCartState()!=null){
            return sessionManager.getCurrentCartState();
        }else {
            return new Cart();
        }

    }

    public void updateCartState(Cart cart){
        sessionManager.setCurrentCartState(cart);
        Log.d(TAG,"Cart State Updated");
    }

    public boolean addItemToCart(final CartItem cartItem, final Context context){
        final Cart cartToAddItemTo = getCart();
        if(cartToAddItemTo.isCartEmpty()){
            cartToAddItemTo.addItem(cartItem);
            updateCartState(cartToAddItemTo);
            broadcastItemAddedToCartMessage(cartItem);
            Log.d(TAG,"Item added to cart");
            return true;
        }else if(cartItem.getRestaurantItem().getItemRestaurant().getName().equals(cartToAddItemTo.getRestaurantInCart().getName())||cartToAddItemTo.isCartEmpty()){
            cartToAddItemTo.addItem(cartItem);
            updateCartState(cartToAddItemTo);
            broadcastItemAddedToCartMessage(cartItem);
            Log.d(TAG,"Item added to cart");
            return true;

        }else{

            Log.d(TAG,"Only one restaurant is allowed at once.");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.different_restaurant_alert_dialog_message).setTitle(R.string.different_restaurant_alert_dialog_title);
            builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cartToAddItemTo.clearCart();
                    cartToAddItemTo.addItem(cartItem);
                    updateCartState(cartToAddItemTo);
                    broadcastItemAddedToCartMessage(cartItem);
                    Log.d(TAG,"Item added to cart");
                    dialogOk = true ;
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG,"User Cancelled The adding of item to cart.");
                    dialogOk = false;

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;

        }

    }


    public void addMultipleItemsToCart(final ArrayList<CartItem> cartItems, final Context context){

        if(!cartItems.isEmpty()){
            final Cart cartToAddItemTo = getCart();
            if(cartToAddItemTo.isCartEmpty()){
                for(int i=0; i<cartItems.size();i++){
                    cartToAddItemTo.addItem(cartItems.get(i));
                    updateCartState(cartToAddItemTo);
                    broadcastItemAddedToCartMessage(cartItems.get(i));
                    Log.d(TAG,"Item added to cart");
                }
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);

            }else if(cartItems.get(0).getRestaurantItem().getItemRestaurant().getName().equals(cartToAddItemTo.getRestaurantInCart().getName())||cartToAddItemTo.isCartEmpty()){
                for(int i=0; i<cartItems.size();i++){
                    cartToAddItemTo.addItem(cartItems.get(i));
                    updateCartState(cartToAddItemTo);
                    broadcastItemAddedToCartMessage(cartItems.get(i));
                    Log.d(TAG,"Item added to cart");
                }
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);


            }else{

                Log.d(TAG,"Only one restaurant is allowed at once.");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.different_restaurant_alert_dialog_message).setTitle(R.string.different_restaurant_alert_dialog_title);
                builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cartToAddItemTo.clearCart();
                        for(int i=0; i<cartItems.size();i++){
                            cartToAddItemTo.addItem(cartItems.get(i));
                            updateCartState(cartToAddItemTo);
                            broadcastItemAddedToCartMessage(cartItems.get(i));
                            Log.d(TAG,"Item added to cart");
                        }
                        Intent intent = new Intent(context, CartActivity.class);
                        context.startActivity(intent);
                        dialogOk = true ;
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"User Cancelled The adding of item to cart.");
                        dialogOk = false;

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }


        }else{
            Log.d(TAG,"cartItems is empty in add multiple items to cart.");
        }


    }



    public void deleteItemFromCart(CartItem cartItem){
        Cart cartToDeleteItemFrom = getCart();
        cartToDeleteItemFrom.deleteItem(cartItem);
        updateCartState(cartToDeleteItemFrom);
        broadcastItemDeletedFromCartMessage(cartItem);
        if(getCart().getCartItems().size()==0){
            clearCart();
            Log.d(TAG,"cart cleared test");
        }
        Log.d(TAG,"Item deleted from cart");
    }

    public void deleteLastAddedItem(){
        Cart cartToDeleteItemFrom = getCart();
        CartItem cartItem = cartToDeleteItemFrom.getCartItems().getLast();
        cartToDeleteItemFrom.getCartItems().removeLast();
        if(cartToDeleteItemFrom.isCartEmpty()){
            cartToDeleteItemFrom.setRestaurantInCart(null);
        }
        updateCartState(cartToDeleteItemFrom);
        broadcastItemDeletedFromCartMessage(cartItem);
        Log.d(TAG,"Item deleted from cart");

    }

    public void decreaseItemQuantity(RestaurantItem restaurantItem){
        Cart cartToDecreaseItemQuantityFrom = getCart();
        CartItem cartItem = getCartItemForRestauratnItem(restaurantItem);
        if(cartItem.getItemQuantity()==1){
            deleteItemFromCart(cartItem);
        }else{
            addItemToCart(new CartItem(restaurantItem,cartItem.getItemQuantity()-1),context);
        }
    }

    public Restaurant getCurrentRestaurantInCart(){
        return getCart().getRestaurantInCart();
    }

    public void setCurrentRestaurantInCart(Restaurant restaurant){
        Cart cartToSetRestaurantTo = getCart();
        cartToSetRestaurantTo.setRestaurantInCart(restaurant);
        updateCartState(cartToSetRestaurantTo);
    }

    public LinkedList<CartItem> getItemsInCart(){
        return getCart().getCartItems();
    }

    public void setCartItemsToCart(LinkedList<CartItem> cartItems){
        Cart cartToSetItemsTo = getCart();
        cartToSetItemsTo.setCartItems(cartItems);
        updateCartState(cartToSetItemsTo);
    }

    public int getCartCount(){
        return getCart().getCartSize();
    }

    public void updateItemQuantityInCart(CartItem cartItem, int itemQuantity){
        Cart cartToUpdateItemQuantityTo = getCart();
        cartToUpdateItemQuantityTo.updateItemQuantity(cartItem,itemQuantity);
        updateCartState(cartToUpdateItemQuantityTo);
    }

    public double getDiscountToApply(){
        return getCart().getDiscount();
    }

    public double getTotalAmountBeforeDiscount(){
        return getCart().getTotalBeforeDiscount();
    }

    public double getTotalAmountAfterDiscount(){
        return getCart().getTotalAfterDiscount();
    }

    public void clearCart(){
        Cart cartToClear = getCart();
        Cart newEmptyCart = cartToClear.clearCart();
        updateCartState(newEmptyCart);
        broadcastCartCleared();
    }

    public void broadcastItemAddedToCartMessage(CartItem cartItem){
        Intent intent=new Intent();
        intent.setAction("Item Added To Cart");
        intent.putExtra("cart_item",cartItem);
        context.sendBroadcast(intent);
    }

    public void broadcastItemDeletedFromCartMessage(CartItem cartItem){
        Intent intent=new Intent();
        intent.setAction("Item Deleted From Cart");
        intent.putExtra("cart_item",cartItem);
        context.sendBroadcast(intent);
    }

    public void broadcastCartCleared(){
        Intent intent=new Intent();
        intent.setAction("Cart Cleared.");
        context.sendBroadcast(intent);
    }

    public void showCart(Activity activity ){
        Intent intent = new Intent(activity, CartActivity.class);
        activity.startActivity(intent);
    }

    public boolean isItemInCart(RestaurantItem restaurantItem){
        LinkedList<CartItem> cartItems = getItemsInCart();
        boolean found = false;
        for (int i =0 ; i<cartItems.size(); i++){
            RestaurantItem cartRestaurantItem = cartItems.get(i).getRestaurantItem();
            if(cartRestaurantItem.getProductCode().equals(restaurantItem.getProductCode())){
                found = true;
            }else{
                found = false;
            }
        }
        return found;
    }

    public CartItem getCartItemForRestauratnItem(RestaurantItem restaurantItem){
        CartItem cartItem = new CartItem(restaurantItem,1);
        LinkedList<CartItem> cartItems = getItemsInCart();
        for (int i =0 ; i<cartItems.size(); i++){
            RestaurantItem cartRestaurantItem = cartItems.get(i).getRestaurantItem();
            if(cartRestaurantItem.getProductCode().equals(restaurantItem.getProductCode())){
                cartItem = cartItems.get(i);
            }
        }

        return cartItem;
    }

    public boolean isOrderAmountValid(){
        if(getCart().getTotalAfterDiscount()>=Double.parseDouble(getCart().getRestaurantInCart().getMinOrderAmount())){
            return true;
        }else{
            return false;
        }
    }
}
