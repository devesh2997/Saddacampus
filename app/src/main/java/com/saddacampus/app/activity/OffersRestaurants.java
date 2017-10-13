package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.RestaurantListAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Category;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.helper.CartManager;
import com.saddacampus.app.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class OffersRestaurants extends AppCompatActivity {

    private String TAG = OffersRestaurants.class.getSimpleName();


    private String userId;
    private String selectedCity;


    ArrayList<Restaurant> restaurantsFoundArrayList;

    ProgressDialog progressDialog;

    ProgressBar progressBar;


    Toolbar toolbar;
    CartManager cartManager;
    TextView txtViewCount;
    RecyclerView restaurantsRecyclerView;

    RelativeLayout errorLayout;


    String tableName;

    //SubCategoryItemsAdapter subCategoryItemsAdapter;

    Category category;

    public RestaurantListAdapter restaurantOfferListAdapter;
    ArrayList<Restaurant> restaurantOfferArrayList;

    String isVeg;
    String tag;
    String isOffer;
    String tagString;
    int numberOfTags;


    ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(OffersRestaurants.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View notifications = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) notifications.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    TODO
                Intent intent = new Intent(OffersRestaurants.this, CartActivity.class);
                startActivity(intent);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_restaurants);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SessionManager sessionManager = new SessionManager(OffersRestaurants.this);
        String keyy=sessionManager.getfirebaseKey();
        //Log.e("firebase",key);
        while (keyy.equals("0")){

        }



        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra("category");
        tableName = intent.getStringExtra("tableName");
        getSupportActionBar().setTitle(category.getCategoryName());
        tagString = category.getTags();
        numberOfTags=category.getNumberOfTags();
        isVeg = category.getIsVeg();
        restaurantsRecyclerView = (RecyclerView) findViewById(R.id.restaurant_offer_recycler);
       progressDialog = new ProgressDialog(OffersRestaurants.this);
        progressDialog.setMessage("Downloading Restaurants...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        userId = AppController.getInstance().getDbManager().getUserUid();
        selectedCity = AppController.getInstance().getSessionManager().getSelectedCity();


        getItems();


    }

    public void getItems() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RESTAURANTS_OFFER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("inside subCategory fuck= ", response);

                progressDialog.dismiss();
                JSONObject responseObject;
                JSONObject result;
                JSONArray restaurantsFoundArray;
                restaurantsFoundArrayList = new ArrayList<>();

                try {
                    responseObject = new JSONObject(response);
                    if (responseObject.getBoolean("error")) {
                        Log.d(TAG,"unknown error in line 173 of offer Restaurants");
                       //Toast.makeText(OffersRestaurants.this, "Unknown Error LINE 156 OFFER RESTAURANTS in getting result", Toast.LENGTH_SHORT).show();
                    } else {
                        result = responseObject.getJSONObject("result");
                        restaurantsFoundArray = result.getJSONArray("restaurants");
                        for (int i = 0; i < restaurantsFoundArray.length(); i++) {
                            Log.e(TAG,"inside loop");
                            JSONObject restaurantObject = restaurantsFoundArray.getJSONObject(i);
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
                            Log.e(TAG,"inside loopaksdhbfhjs");
                            String imageResourceId = restaurantObject.getString("imageResourceId");
                            String speciality = restaurantObject.getString("speciality");
                            float rating = (float) restaurantObject.getDouble("rating");
                            int messageStatus = restaurantObject.getInt("message_status");
                            //int messageStatus = 0;
                            String message;
                            Log.e(TAG,"inside  5555 loopaksdhbffasdfhjs");
                            if(messageStatus!=0){
                                message = restaurantObject.getString("message");
                            }else{
                                message = "";
                            }

                            Restaurant restaurant = new Restaurant(name, contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);
                            restaurantsFoundArrayList.add(restaurant);
                        }

                        final LinearLayoutManager restaurantOfferListLayoutManager = new LinearLayoutManager(OffersRestaurants.this, LinearLayoutManager.VERTICAL, false);
                        restaurantOfferListAdapter = new RestaurantListAdapter(restaurantsFoundArrayList,restaurantsRecyclerView);
                        restaurantsRecyclerView.setLayoutManager(restaurantOfferListLayoutManager);
                        restaurantsRecyclerView.setHasFixedSize(true);
                        Log.e(TAG,"adapter is set");
                        restaurantsRecyclerView.setAdapter(restaurantOfferListAdapter);
                    }
                } catch (JSONException e) {
                    Log.d("Offer Restaurants", "inside offer restaurants catch");
                    //Toast.makeText(OffersRestaurants.this, "Unknown Error in line 210 getting result", Toast.LENGTH_SHORT).show();
                    // gettingSearchDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               progressDialog.dismiss();
                Intent intent = new Intent(OffersRestaurants.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

                //  Log.e("Sub Category Activity  line no. 204", error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", userId);
                params.put("city", selectedCity);
                params.put("table_name", tableName);

                Log.d("ItemPopupWindow", params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getItems();
        super.onActivityResult(requestCode, resultCode, data);
    }

}

