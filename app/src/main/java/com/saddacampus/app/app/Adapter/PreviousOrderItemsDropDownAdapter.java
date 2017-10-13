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
import com.saddacampus.app.app.DataObjects.CartItem;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 02-08-2017.
 */

public class PreviousOrderItemsDropDownAdapter extends RecyclerView.Adapter<PreviousOrderItemsDropDownAdapter.PreviousOrderItemsDropDownViewHolder>{

    private static final String TAG = PreviousOrderItemsDropDownAdapter.class.getSimpleName();

    private ArrayList<CartItem> cartItems;
    private Context context;

    public PreviousOrderItemsDropDownAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public PreviousOrderItemsDropDownViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForFavouriteItem = R.layout.previous_order_cart_items_expandable_child_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFavouriteItem, parent, shouldAttachToParentImmediately);


        PreviousOrderItemsDropDownViewHolder previousOrderItemsDropDownViewHolder = new PreviousOrderItemsDropDownViewHolder(view);
        return previousOrderItemsDropDownViewHolder;
    }

    @Override
    public void onBindViewHolder(PreviousOrderItemsDropDownViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class PreviousOrderItemsDropDownViewHolder extends RecyclerView.ViewHolder{

        ImageView orderItemIsVeg ;
        TextView orderItemName ;
        TextView orderItemPrice ;
        TextView orderItemQuantity;


        public PreviousOrderItemsDropDownViewHolder(View previousOrderItemView){
            super(previousOrderItemView);

            orderItemIsVeg = (ImageView)previousOrderItemView.findViewById(R.id.order_item_is_veg);
            orderItemName = (TextView)previousOrderItemView.findViewById(R.id.order_item_name);
            orderItemPrice = (TextView)previousOrderItemView.findViewById(R.id.order_item_price);
            orderItemQuantity = (TextView)previousOrderItemView.findViewById(R.id.order_item_quantity);
        }

        void bind(int listIndex){
            CartItem cartItem = cartItems.get(listIndex);

            if(cartItem.getRestaurantItem().isVeg()){
                orderItemIsVeg.setImageResource(R.drawable.veg_icon);
            }else{
                orderItemIsVeg.setImageResource(R.drawable.nonvegicon);
            }

            orderItemName.setText(cartItem.getRestaurantItem().getName());
            orderItemPrice.setText(String.valueOf(cartItem.getRestaurantItem().getPrice()));
            orderItemQuantity.setText(" * ".concat(String.valueOf(cartItem.getItemQuantity())));

        }

    }
}
