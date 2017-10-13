package com.saddacampus.app.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.saddacampus.app.R;
import com.saddacampus.app.app.Animation.AnimateViewSwitcherFlip;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.helper.CartManager;


public class AddToCartCustomButton {
    private static final String TAG = AddToCartCustomButton.class.getSimpleName();

    private Context context;

    private RestaurantItem restaurantItem;
    private ViewSwitcher itemAddToCartViewSwitcher;
    private RelativeLayout itemAddView;
    private RelativeLayout itemQuantityContainer;
    private TextView itemQuantity;
    private TextView itemIncreaseQuantity;
    private TextView itemDecreaseQuantity;


    public AddToCartCustomButton(Context context, RestaurantItem restaurantItem, ViewSwitcher itemAddToCartViewSwitcher, RelativeLayout itemAddView, RelativeLayout itemQuantityContainer, TextView itemQuantity, TextView itemIncreaseQuantity, TextView itemDecreaseQuantity) {
        this.context = context;
        this.restaurantItem = restaurantItem;
        this.itemAddToCartViewSwitcher = itemAddToCartViewSwitcher;
        this.itemAddView = itemAddView;
        this.itemQuantityContainer = itemQuantityContainer;
        this.itemQuantity = itemQuantity;
        this.itemIncreaseQuantity = itemIncreaseQuantity;
        this.itemDecreaseQuantity = itemDecreaseQuantity;
    }

    public void show(){
        itemAddToCartViewSwitcher.reset();
        if(AppController.getInstance().getCartManager().isItemInCart(restaurantItem)){
            itemAddToCartViewSwitcher.setDisplayedChild(1);
            itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
            /*if(itemAddToCartViewSwitcher.getCurrentView()==itemAddView){
                itemAddToCartViewSwitcher.setDisplayedChild(1);
            }else{
                itemAddToCartViewSwitcher.setDisplayedChild(0);
            }
            itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));*/
        }else {
            itemAddToCartViewSwitcher.setDisplayedChild(0);
            /*if (!(itemAddToCartViewSwitcher.getCurrentView() == itemAddView)) {
                itemAddToCartViewSwitcher.setDisplayedChild(0);
            } else {
                itemAddToCartViewSwitcher.setDisplayedChild(1);
            }*/
        }

        itemAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AnimateViewSwitcherFlip animateViewSwitcherFlip = new AnimateViewSwitcherFlip(itemAddToCartViewSwitcher);
                final CartManager cartManager = AppController.getInstance().getCartManager();
                final Cart currentCart = AppController.getInstance().getCartManager().getCart();
                if(AppController.getInstance().getCartManager().getCart().isCartEmpty()){
                    AppController.getInstance().getCartManager().addItemToCart(new CartItem(restaurantItem,1),context);
                    animateViewSwitcherFlip.animateFlip();
                    itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                }
                else if(AppController.getInstance().getCartManager().getCurrentRestaurantInCart().getCode().equals(restaurantItem.getItemRestaurant().getCode())){
                    AppController.getInstance().getCartManager().addItemToCart(new CartItem(restaurantItem,1),context);
                    animateViewSwitcherFlip.animateFlip();
                    itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                }else{
                    Log.d(TAG,"Only one restaurant is allowed at once.");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.different_restaurant_alert_dialog_message).setTitle(R.string.different_restaurant_alert_dialog_title);
                    builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppController.getInstance().getCartManager().clearCart();
                            AppController.getInstance().getCartManager().addItemToCart(new CartItem(restaurantItem,1),context);
                            //AppController.getInstance().getCartManager().updateCartState(currentCart);
                            //AppController.getInstance().getCartManager().broadcastItemAddedToCartMessage(new CartItem(restaurantItem,1));
                            animateViewSwitcherFlip.animateFlip();
                            itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                            Log.d(TAG,"Item added to cart");

                        }
                    });
                    builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG,"User Cancelled The adding of item to cart.");

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        itemIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(restaurantItem,Integer.parseInt(itemQuantity.getText().toString())+1);
                if(AppController.getInstance().getCartManager().addItemToCart(cartItem,context)){
                    itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                }
            }
        });

        itemDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().decreaseItemQuantity(restaurantItem);
                if(AppController.getInstance().getCartManager().isItemInCart(restaurantItem)){
                    itemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                }else{
                    AnimateViewSwitcherFlip animateViewSwitcherFlip = new AnimateViewSwitcherFlip(itemAddToCartViewSwitcher);
                    animateViewSwitcherFlip.animateFlip();
                }

            }
        });
    }


}
