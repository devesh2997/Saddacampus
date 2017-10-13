package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.PreviousOrdersListAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.PreviousOrder;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.helper.CartManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 29-07-2017.
 */

public class PreviousOrdersActivity extends AppCompatActivity {

    private static final String TAG = PreviousOrdersActivity.class.getSimpleName();

    ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;

    Toolbar toolbar;

    CartManager cartManager;
    TextView txtViewCount;

    RecyclerView previousOrdersRecycler;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    CoordinatorLayout coordinatorLayout;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(PreviousOrdersActivity.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View cart = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_previous_orders);

        txtViewCount = (TextView) cart.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(PreviousOrdersActivity.this);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

        previousOrdersRecycler = (RecyclerView)findViewById(R.id.previous_orders_recycler);
        /*progressBar = (ProgressBar)findViewById(R.id.previous_orders_progress);
        ChasingDots chasingDots = new ChasingDots();
        chasingDots.setColor(getResources().getColor(R.color.colorAccent));
        progressBar.setIndeterminateDrawable(chasingDots);
        progressBar.setVisibility(View.VISIBLE);*/


        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        //setting up receiver for updating cart count badge.
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Previous Orders");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getPreviousOrders();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PreviousOrdersActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPreviousOrders();
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getPreviousOrders(){

        progressDialog = new ProgressDialog(PreviousOrdersActivity.this);
        progressDialog.setMessage("Getting your previous orders...");
        progressDialog.setCancelable(false);
        //progressDialog.setIndeterminateDrawable(chasingDots);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_PREVIOUS_ORDERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                //progressBar.setVisibility(View.GONE);

                final ArrayList<PreviousOrder> previousOrders = new ArrayList<>();
                Log.d(TAG,response);
                try{
                    JSONObject responseObject = new JSONObject(response);
                    Boolean isError = responseObject.getBoolean("error");

                    if(!isError){
                        JSONArray resultArray = responseObject.getJSONArray("result");

                        for(int i = 0 ; i<resultArray.length(); i++){
                            JSONObject currentOrder = resultArray.getJSONObject(i);
                            JSONArray orderItems = currentOrder.getJSONArray("items");

                            String orderId = currentOrder.getString("order_id");
                            String orderTotal = currentOrder.getString("order_total");
                            String orderDate = currentOrder.getString("order_date");
                            String orderTime = currentOrder.getString("order_time");
                            String orderMode = currentOrder.getString("order_mode");
                            String orderCity = currentOrder.getString("order_city");

                            ArrayList<CartItem> cartItems = new ArrayList<>();

                            for(int j=0; j<orderItems.length(); j++){
                                JSONObject itemObject = orderItems.getJSONObject(j);
                                int itemQuantity = itemObject.getInt("quantity");
                                JSONObject itemInfo = itemObject.getJSONObject("info");

                                JSONObject restaurantObject = itemInfo.getJSONObject("restaurant");

                                String name = restaurantObject.getString("name");
                                String contact = restaurantObject.getString("contact");
                                String address = restaurantObject.getString("address");
                                String status = (restaurantObject.getString("open").equals("1")) ? "open" : "close";
                                String minOrderAmount = restaurantObject.getString("minOrderAmount");
                                String timing = restaurantObject.getString("timing");
                                float deliveryCharges = Float.parseFloat(restaurantObject.getString("delivery_charges"));
                                float deliveryChargeSlab = Float.parseFloat(restaurantObject.getString("delivery_charge_slab"));
                                boolean acceptsOnlinePayment = restaurantObject.getInt("accepts_online_payment")==1;
                                String code = restaurantObject.getString("code");
                                int hasNewOffer = restaurantObject.getInt("has_new_offer");
                                int newOffer = restaurantObject.getInt("new_offer");
                                int offer = restaurantObject.getInt("offer");
                                String imageResourceId = restaurantObject.getString("imageResourceId");
                                String speciality = restaurantObject.getString("speciality");
                                float rating = (float)restaurantObject.getDouble("rating");
                                int messageStatus = restaurantObject.getInt("message_status");
                                String message;
                                if(messageStatus!=0){
                                    message = restaurantObject.getString("message");
                                }else{
                                    message = "";
                                }
                                Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

                                String itemName = itemInfo.getString("item_name");
                                double itemPrice = Double.parseDouble(itemInfo.getString("item_price")) ;
                                String itemCode = itemInfo.getString("item_code");
                                Double itemOffer;
                                if(itemInfo.getString("offer_price")!=null){
                                    itemOffer = Double.parseDouble(itemInfo.getString("offer_price"));
                                }else{
                                    itemOffer = 0.0;
                                }

                                String itemRestaurantCategory =  itemInfo.getString("item_restaurant_category");
                                float itemRating = Float.parseFloat( itemInfo.getString("item_rating"));
                                int itemRatingCount = Integer.parseInt( itemInfo.getString("item_rating_count"));
                                String itemCurrentRatingByUser = itemInfo.getString("item_current_rating_by_user");
                                int itemOrderCount = Integer.parseInt( itemInfo.getString("item_order_count"));
                                Boolean itemIsFavourite = itemInfo.getBoolean("item_is_favourite");
                                Boolean itemIsVeg = itemInfo.getBoolean("item_is_veg");
                                boolean itemIsRecommended = itemInfo.getBoolean("item_is_recommended");
                                String itemImageResource = itemInfo.getString("item_image_resource");
                                Boolean hasOffer=false;
                                RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);

                                CartItem cartItem = new CartItem(restaurantItem,itemQuantity);
                                cartItems.add(cartItem);
                                Log.d(TAG,"item Added");

                            }
                            PreviousOrder previousOrder = new PreviousOrder(orderId,orderTotal,orderDate,orderTime,orderMode,orderCity,cartItems);
                            previousOrders.add(previousOrder);
                        }
                    }



                    if(previousOrders.size()==0){
                        Snackbar snackbar =  Snackbar.make(coordinatorLayout,"No previous orders.",Snackbar.LENGTH_INDEFINITE);
                        View snackbarLayout = snackbar.getView();
                        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                        Drawable warningDrwable = getResources().getDrawable(R.drawable.warning,null);
                        warningDrwable.setBounds(5,5,15,15);
                        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.warning_drawable,0,0,0);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.sheet_item_spacing));
                        snackbar.show();
                    }else{
                        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
                        LinearLayoutManager la = new LinearLayoutManager(PreviousOrdersActivity.this, LinearLayoutManager.VERTICAL, false);
                        previousOrdersRecycler.setLayoutManager(la);
                        previousOrdersRecycler.setHasFixedSize(true);

                        final PreviousOrdersListAdapter adapter = new PreviousOrdersListAdapter(previousOrders);

                        previousOrdersRecycler.setAdapter(adapter);
                    }


                }catch (JSONException e){
                    Log.d(TAG, e.getMessage());
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
                Intent intent = new Intent(PreviousOrdersActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
                /*Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", AppController.getInstance().getDbManager().getUserUid());

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private class ItemAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));

            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }
}
