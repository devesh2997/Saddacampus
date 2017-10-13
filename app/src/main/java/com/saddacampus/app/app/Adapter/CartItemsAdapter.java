package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.activity.CartActivity;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import java.util.LinkedList;

/**
 * Created by Devesh Anand on 09-07-2017.
 */

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder> {

    private static final String TAG = CartItemsAdapter.class.getSimpleName();

    private LinkedList<CartItem> cartItems;

    private Context context;

    public CartItemsAdapter(LinkedList<CartItem> cartItems){
        this.cartItems = cartItems;
    }

    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForCartItem = R.layout.cart_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForCartItem, parent, shouldAttachToParentImmediately);
        CartItemViewHolder cartItemViewHolder = new CartItemViewHolder(view);

        return cartItemViewHolder;
    }

    @Override
    public void onBindViewHolder(CartItemViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position){
        AppController.getInstance().getCartManager().deleteItemFromCart(cartItems.get(position));
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        ImageView cartItemIsVegView;
        TextView cartItemName;
        TextView cartItemDecreaseQuantity;
        TextView cartItemQuantity;
        TextView cartItemIncreaseQuantity;
        TextView cartItemPrice;

        @Override
        public void onClick(View v) {

        }

        public CartItemViewHolder(View cartItemView){
            super(cartItemView);
            cartItemView.setOnClickListener(this);


            cartItemIsVegView = (ImageView)cartItemView.findViewById(R.id.cart_item_is_veg);
            cartItemName = (TextView)cartItemView.findViewById(R.id.cart_item_name);
            cartItemDecreaseQuantity = (TextView)cartItemView.findViewById(R.id.cart_decrease_quantity);
            cartItemIncreaseQuantity = (TextView)cartItemView.findViewById(R.id.cart_increase_quantity);
            cartItemQuantity = (TextView)cartItemView.findViewById(R.id.cart_item_quantity);
            cartItemPrice = (TextView)cartItemView.findViewById(R.id.cart_item_price);

        }

        void bind (final int listIndex){

            final CartItem cartItem = cartItems.get(listIndex);
            final RestaurantItem restaurantItem = cartItem.getRestaurantItem();


            if(cartItems.get(listIndex).getRestaurantItem().isVeg()){
                cartItemIsVegView.setImageResource(R.drawable.veg_icon);
                /*Drawable vegDrawable = context.getDrawable(R.drawable.veg_icon);
                Bitmap bitmap = ((BitmapDrawable) vegDrawable).getBitmap();
                Drawable vegScaled = new BitmapDrawable(context.getResources(),Bitmap.createScaledBitmap(bitmap,40,40,true));
                cartItemName.setCompoundDrawablesWithIntrinsicBounds(vegScaled,null,null,null);*/
            }else {
                cartItemIsVegView.setImageResource(R.drawable.nonvegicon);
               /* Drawable nonVegDrawable = context.getDrawable(R.drawable.veg_icon);
                Bitmap bitmap = ((BitmapDrawable) nonVegDrawable).getBitmap();
                Drawable nonVegScaled = new BitmapDrawable(context.getResources(),Bitmap.createScaledBitmap(bitmap,40,40,true));
                cartItemName.setCompoundDrawablesWithIntrinsicBounds(nonVegScaled,null,null,null);*/
            }
            cartItemName.setText(cartItems.get(listIndex).getRestaurantItem().getName());
            cartItemQuantity.setText(String.valueOf(cartItems.get(listIndex).getItemQuantity()));
            cartItemPrice.setText(String.valueOf(cartItems.get(listIndex).getCartItemPrice()));

            cartItemIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartItem changedCartItem = new CartItem(restaurantItem,(cartItem.getItemQuantity())+1);
                    if(AppController.getInstance().getCartManager().addItemToCart(changedCartItem,context)){
                        cartItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(cartItems.get(listIndex).getRestaurantItem()).getItemQuantity()));
                    }
                    cartItems = AppController.getInstance().getCartManager().getItemsInCart();
                    notifyDataSetChanged();
                    ((CartActivity)context).updateCartViews();

                }
            });

            cartItemDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getInstance().getCartManager().decreaseItemQuantity(restaurantItem);
                    if(AppController.getInstance().getCartManager().isItemInCart(restaurantItem)){
                        cartItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                    }
                    cartItems = AppController.getInstance().getCartManager().getItemsInCart();
                    notifyDataSetChanged();
                    ((CartActivity)context).updateCartViews();

                }
            });
        }

    }

}
