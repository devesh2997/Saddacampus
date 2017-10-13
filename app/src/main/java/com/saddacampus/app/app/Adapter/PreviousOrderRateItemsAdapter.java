package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.app.DataObjects.CartItem;

import java.util.LinkedList;

/**
 * Created by Devesh Anand on 09-08-2017.
 */

public class PreviousOrderRateItemsAdapter extends RecyclerView.Adapter<PreviousOrderRateItemsAdapter.PreviousOrderRateItemsItemsViewHolder> {

    private static String TAG = PreviousOrderRateItemsAdapter.class.getSimpleName();

    private Context context;
    private LinkedList<CartItem> previousOrderItems;

    public PreviousOrderRateItemsAdapter(Context context, LinkedList<CartItem> previousOrderItems) {
        this.context = context;
        this.previousOrderItems = previousOrderItems;
    }

    @Override
    public PreviousOrderRateItemsItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutForFoundItemsInARestaurant = R.layout.previous_order_rating_recycler;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFoundItemsInARestaurant,parent,shouldAttachToParentImmediately);

        PreviousOrderRateItemsItemsViewHolder previousOrderRateItemsItemsViewHolder = new PreviousOrderRateItemsItemsViewHolder(view);

        return previousOrderRateItemsItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(PreviousOrderRateItemsItemsViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return previousOrderItems.size();
    }

    public class PreviousOrderRateItemsItemsViewHolder extends RecyclerView.ViewHolder{

        TextView previousOrderItemName;
        SimpleRatingBar previousOrderItemRatingBar;

        public PreviousOrderRateItemsItemsViewHolder(View itemView){
            super(itemView);

            previousOrderItemName = (TextView)itemView.findViewById(R.id.previous_order_item_name);
            previousOrderItemRatingBar = (SimpleRatingBar)itemView.findViewById(R.id.previous_order_item_rating_bar);
        }

        void bind(int listIndex){
            CartItem cartItem = previousOrderItems.get(listIndex);
            previousOrderItemName.setText(cartItem.getRestaurantItem().getName());
        }

        public int getItemRating(){
            return (int)previousOrderItemRatingBar.getRating();
        }
    }



}
