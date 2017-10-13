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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.SubCategoryItemsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Category;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.app.DataObjects.RestaurantSearch;
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

public class SubCategoryActivity extends AppCompatActivity {

    private String TAG = SubCategoryActivity.class.getSimpleName();


    private String userId;
    private String selectedCity;


    ProgressBar progressBar;


    Toolbar toolbar;
    CartManager cartManager;
    TextView txtViewCount;
    RecyclerView itemsRecyclerView;

    ProgressDialog progressDialog;

    SubCategoryItemsAdapter subCategoryItemsAdapter;

    Category category;

    String isVeg;
    String isOffer;
    String tagString;
    int numberOfTags;


    ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(SubCategoryActivity.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View notifications = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) notifications.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(SubCategoryActivity.this);
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
        setContentView(R.layout.activity_category_sub);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(SubCategoryActivity.this);
        String keyy=sessionManager.getfirebaseKey();
        //Log.e("firebase",key);
        while (keyy.equals("0")){

        }



        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra("category");
        getSupportActionBar().setTitle(category.getCategoryName());
        tagString=category.getTags();
        numberOfTags=category.getNumberOfTags();
        isVeg = category.getIsVeg();
        isOffer = category.getIsOffer();
        itemsRecyclerView = (RecyclerView) findViewById(R.id.sub_category_item_recycler);

        userId = AppController.getInstance().getDbManager().getUserUid();
        selectedCity = AppController.getInstance().getSessionManager().getSelectedCity();

        progressDialog = new ProgressDialog(SubCategoryActivity.this);
        progressDialog.setMessage("Downloading Dishes ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getItems();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getItems();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getItems() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_SUB_CATEGORY_ITEMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("inside subCategory = ", response);

                progressDialog.dismiss();

                JSONObject responseObject;
                JSONObject searchResultObject;
                JSONArray restaurantItemsFoundArray;
                RestaurantSearch restaurantSearch;
                restaurantItemsSearchArrayList = new ArrayList<>();
                try {
                    responseObject = new JSONObject(response);
                    if (responseObject.getBoolean("error")) {
                       // Toast.makeText(SubCategoryActivity.this, "Unknown Error in getting result", Toast.LENGTH_SHORT).show();
                    } else {
                        searchResultObject = responseObject.getJSONObject("result");
                        restaurantItemsFoundArray = searchResultObject.getJSONArray("food_items");


                        for (int i = 0; i < restaurantItemsFoundArray.length(); i++) {

                            JSONObject restaurantItemsFoundObject = restaurantItemsFoundArray.getJSONObject(i).getJSONObject("restaurant");
                            JSONArray restaurantItemsFound = restaurantItemsFoundObject.getJSONArray("found_items");
                            ArrayList<RestaurantItem> itemsFoundInRestaurant = new ArrayList<>();
                            String name = restaurantItemsFoundObject.getString("name");
                            String contact = restaurantItemsFoundObject.getString("contact");
                            String address = restaurantItemsFoundObject.getString("address");
                            String status = (restaurantItemsFoundObject.getString("open").equals("1")) ? "open" : "close";
                            String minOrderAmount = restaurantItemsFoundObject.getString("minOrderAmount");
                            String timing = restaurantItemsFoundObject.getString("timing");
                            float deliveryCharges = Float.parseFloat(restaurantItemsFoundObject.getString("delivery_charges"));
                            float deliveryChargeSlab = Float.parseFloat(restaurantItemsFoundObject.getString("delivery_charge_slab"));
                            boolean acceptsOnlinePayment = restaurantItemsFoundObject.getInt("accepts_online_payment")==1;
                            String code = restaurantItemsFoundObject.getString("code");
                            int hasNewOffer = restaurantItemsFoundObject.getInt("has_new_offer");
                            int newOffer = restaurantItemsFoundObject.getInt("new_offer");
                            int offer = restaurantItemsFoundObject.getInt("offer");
                            String imageResourceId = restaurantItemsFoundObject.getString("imageResourceId");
                            String speciality = restaurantItemsFoundObject.getString("speciality");
                            float rating = (float) restaurantItemsFoundObject.getDouble("rating");
                            int messageStatus = restaurantItemsFoundObject.getInt("message_status");
                            String message;
                            if(messageStatus!=0){
                                message = restaurantItemsFoundObject.getString("message");
                            }else{
                                message = "";
                            }
                            Restaurant restaurant = new Restaurant(name, contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

                            for (int j = 0; j < restaurantItemsFound.length(); j++) {
                                JSONObject itemObject = restaurantItemsFound.getJSONObject(j);
                                String itemName = itemObject.getString("item_name");
                                double itemPrice = Double.parseDouble(itemObject.getString("item_price"));
                                String itemCode = itemObject.getString("item_code");
                                Double itemOffer;
                                if(itemObject.getString("offer_price")!=null){
                                    itemOffer = Double.parseDouble(itemObject.getString("offer_price"));
                                }else{
                                    itemOffer = 0.0;
                                }
                                String itemRestaurantCategory = itemObject.getString("item_restaurant_category");
                                float itemRating = Float.parseFloat(itemObject.getString("item_rating"));
                                int itemRatingCount = Integer.parseInt(itemObject.getString("item_rating_count"));
                                String itemCurrentRatingByUser = itemObject.getString("item_current_rating_by_user");
                                int itemOrderCount = Integer.parseInt(itemObject.getString("item_order_count"));
                                Boolean itemIsFavourite = itemObject.getBoolean("item_is_favourite");
                                Boolean hasOffer=false;
                                Boolean itemIsVeg = itemObject.getBoolean("item_is_veg");
                                boolean itemIsRecommended = itemObject.getBoolean("item_is_recommended");
                                String itemImageResource = itemObject.getString("item_image_resource");
                                if(itemOffer>0){

                                    hasOffer=true;
                                    double temp=itemOffer;
                                    itemOffer=itemPrice;
                                    itemPrice=temp;
                                }
                                RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);
                                itemsFoundInRestaurant.add(restaurantItem);
                            }
                            RestaurantItemsSearch restaurantItemsSearch = new RestaurantItemsSearch(restaurant, itemsFoundInRestaurant, itemsFoundInRestaurant.size());
                            if(restaurantItemsSearch.getFoundItemsCount()>0)
                            restaurantItemsSearchArrayList.add(restaurantItemsSearch);
                          //  Log.d("Inside que + ", " "+ restaurantItemsSearch.getRestaurantSearched().getName());
                        }

                        final LinearLayoutManager restaurantItemSearchLayoutManager = new LinearLayoutManager(SubCategoryActivity.this, LinearLayoutManager.VERTICAL, false);
                        subCategoryItemsAdapter = new SubCategoryItemsAdapter(restaurantItemsSearchArrayList);

                        itemsRecyclerView.setLayoutManager(restaurantItemSearchLayoutManager);
                        itemsRecyclerView.setHasFixedSize(true);
                        itemsRecyclerView.setAdapter(subCategoryItemsAdapter);

                    }
                } catch (JSONException e) {
                   // Log.d("inside sub category", e.getMessage());
                  //  Toast.makeText(SubCategoryActivity.this, "Unknown Error in line 210 getting result", Toast.LENGTH_SHORT).show();
                   // gettingSearchDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               progressDialog.dismiss();
                Intent intent = new Intent(SubCategoryActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

              //  Log.e("Sub Category Activity  line no. 204", error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tags", tagString);
                params.put("UID", userId);
                params.put("city", selectedCity);
                params.put("number", String.valueOf(numberOfTags));
                params.put("is_veg", isVeg);
                params.put("is_offer", isOffer);

                Log.d("ItemPopupWindow", params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);




    }
}

