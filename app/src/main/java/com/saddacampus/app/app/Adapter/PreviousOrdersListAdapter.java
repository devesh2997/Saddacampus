package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.PreviousOrder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-07-2017.
 */

public class PreviousOrdersListAdapter extends RecyclerView.Adapter<PreviousOrdersListAdapter.PreviousOrderViewHolder>{

    private static final String TAG = PreviousOrdersListAdapter.class.getSimpleName();

    private ArrayList<PreviousOrder> previousOrdersArrayList;

    private Context context;

    public PreviousOrdersListAdapter(ArrayList<PreviousOrder> previousOrdersArrayList) {
        this.previousOrdersArrayList = previousOrdersArrayList;
    }

    @Override
    public PreviousOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForFavouriteItem = R.layout.previous_order_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFavouriteItem, parent, shouldAttachToParentImmediately);
        PreviousOrderViewHolder previousOrderViewHolder = new PreviousOrderViewHolder(view);

        return previousOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(PreviousOrderViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return previousOrdersArrayList.size();
    }

    public class PreviousOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdView;
        TextView orderDateView;
        TextView orderTimeView;
        TextView orderPaymentModeView;
        TextView orderTotalView;

        ImageView orderRestaurantImage;
        TextView orderRestaurantName;
        TextView orderRestaurantAddress;

        RecyclerView orderItemsRecycler;
        TextView orderItemsTextView;
        Boolean isOrderItemsVisible;


        Button reorderButton;



        public PreviousOrderViewHolder(View previousOrderItemView){
            super(previousOrderItemView);

            orderIdView = (TextView)previousOrderItemView.findViewById(R.id.order_id_view);
            orderDateView = (TextView)previousOrderItemView.findViewById(R.id.order_date_view);
            orderTimeView = (TextView)previousOrderItemView.findViewById(R.id.order_time_view);
            orderPaymentModeView = (TextView)previousOrderItemView.findViewById(R.id.order_payment_mode);
            orderTotalView = (TextView)previousOrderItemView.findViewById(R.id.order_total);

            orderRestaurantImage = (ImageView)previousOrderItemView.findViewById(R.id.order_restaurant_image_view);
            orderRestaurantAddress = (TextView)previousOrderItemView.findViewById(R.id.order_restaurant_address);
            orderRestaurantName = (TextView)previousOrderItemView.findViewById(R.id.order_restaurant_name);

            orderItemsRecycler = (RecyclerView)previousOrderItemView.findViewById(R.id.order_item_recycler);
            orderItemsTextView = (TextView)previousOrderItemView.findViewById(R.id.order_items_text_view);

            reorderButton = (Button)previousOrderItemView.findViewById(R.id.reorder_button);

            isOrderItemsVisible = false;
        }

        void bind(int listIndex){
            final PreviousOrder previousOrder = previousOrdersArrayList.get(getItemCount()-listIndex-1);
            ArrayList<CartItem>cartItems = previousOrder.getCartItems();

            orderIdView.setText(previousOrder.getOrderId());
            orderDateView.setText("Date : ".concat(previousOrder.getOrderDate()));
            orderTimeView.setText("Time : ".concat(previousOrder.getOrderTime()));
            orderTotalView.setText("Rs. ".concat(previousOrder.getOrderTotal()));

            if(previousOrder.getOrderMode().equals("COD")){
                orderPaymentModeView.setText("COD");
            }else{
                orderPaymentModeView.setText("Online Transaction");
            }

            String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(cartItems.get(0).getRestaurantItem().getItemRestaurant().getImageResourceId());
            Picasso.with(context).load(url).fit().placeholder(R.drawable.placeholder).into(orderRestaurantImage);

            orderRestaurantName.setText(cartItems.get(0).getRestaurantItem().getItemRestaurant().getName());
            orderRestaurantAddress.setText(cartItems.get(0).getRestaurantItem().getItemRestaurant().getAddress());

            PreviousOrderItemsDropDownAdapter previousOrderItemsDropDownAdapter = new PreviousOrderItemsDropDownAdapter(cartItems);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            orderItemsRecycler.setLayoutManager(linearLayoutManager);
            orderItemsRecycler.setAdapter(previousOrderItemsDropDownAdapter);

            orderItemsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOrderItemsVisible){
                        isOrderItemsVisible = false;
                        orderItemsRecycler.setVisibility(View.GONE);
                    }else{
                        isOrderItemsVisible = true;
                        orderItemsRecycler.setVisibility(View.VISIBLE);
                    }
                }
            });

            reorderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"reorder button clicked");
                    addOrderToCart(previousOrder);
                }
            });

        }

        private void addOrderToCart(PreviousOrder previousOrder){
            ArrayList<CartItem> cartItems = previousOrder.getCartItems();
            AppController.getInstance().getCartManager().addMultipleItemsToCart(cartItems,context);

        }
    }
}
