package com.saddacampus.app.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.activity.NoInternetActivity;
import com.saddacampus.app.app.Adapter.MostOrderedItemsAdapter;
import com.saddacampus.app.app.Adapter.TopRatedItemsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 10-07-2017.
 */

public class RecommendationManager {

    private static String TAG = RecommendationManager.class.getSimpleName();

    private Context context;
    public RecyclerView topRatedRecycler;
    public RecyclerView mostOrderedRecycler;
    private String restaurantCode;
    private String itemCode;
    private int recommendationKey;

    public RecommendationManager(Context context, RecyclerView topRatedRecycler, RecyclerView mostOrderedRecycler, String restaurantCode, String itemCode, int recommendationKey) {
        this.context = context;
        this.topRatedRecycler = topRatedRecycler;
        this.mostOrderedRecycler = mostOrderedRecycler;
        this.restaurantCode = restaurantCode;
        this.itemCode = itemCode;
        this.recommendationKey = recommendationKey;
    }

    public void getRecommendations(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RECOMMENDATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);
                JSONObject responseObject;
                JSONObject recommendationsObject;
                JSONArray topRatedJSONArray;
                JSONArray mostOrderedJSONArray;
                ArrayList<RestaurantItem> topRatedItems = new ArrayList<>();
                ArrayList<RestaurantItem> mostOrderedItems = new ArrayList<>();
                try{
                    responseObject = new JSONObject(response);
                    if(!responseObject.getBoolean("error")){
                        recommendationsObject = responseObject.getJSONObject("recommendations");
                        topRatedJSONArray = recommendationsObject.getJSONArray("top_rated");
                        mostOrderedJSONArray = recommendationsObject.getJSONArray("most_ordered");

                        //Converting jsonarray to arraylist.
                        for(int i=0; i<topRatedJSONArray.length(); i++){
                            JSONObject currentTopRatedItem = topRatedJSONArray.getJSONObject(i);
                            JSONObject currentTopRatedItemRestaurant = currentTopRatedItem.getJSONObject("restaurant");
                            String itemName = currentTopRatedItem.getString("item_name");
                            double itemPrice = Double.parseDouble(currentTopRatedItem.getString("item_price")) ;
                            Double itemOffer;
                            if(currentTopRatedItem.getString("offer_price")!=null){
                                itemOffer = Double.parseDouble(currentTopRatedItem.getString("offer_price"));
                            }else{
                                itemOffer = 0.0;
                            }
                            String itemCode = currentTopRatedItem.getString("item_code");
                            String itemRestaurantCategory =  currentTopRatedItem.getString("item_restaurant_category");
                            float itemRating = Float.parseFloat( currentTopRatedItem.getString("item_rating"));
                            int itemRatingCount = Integer.parseInt( currentTopRatedItem.getString("item_rating_count"));
                            String itemCurrentRatingByUser = currentTopRatedItem.getString("item_current_rating_by_user");
                            int itemOrderCount = Integer.parseInt( currentTopRatedItem.getString("item_order_count"));
                            Boolean itemIsFavourite = currentTopRatedItem.getBoolean("item_is_favourite");
                            Boolean itemIsVeg = currentTopRatedItem.getBoolean("item_is_veg");
                            boolean itemIsRecommended = currentTopRatedItem.getBoolean("item_is_recommended");
                            String itemImageResource = currentTopRatedItem.getString("item_image_resource");
                            Boolean hasOffer=false;


                            String name = currentTopRatedItemRestaurant.getString("name");
                            String contact = currentTopRatedItemRestaurant.getString("contact");
                            String address = currentTopRatedItemRestaurant.getString("address");
                            String status = (currentTopRatedItemRestaurant.getString("open").equals("1")) ? "open" : "close";
                            String minOrderAmount = currentTopRatedItemRestaurant.getString("minOrderAmount");
                            String timing = currentTopRatedItemRestaurant.getString("timing");
                            float deliveryCharges = Float.parseFloat(currentTopRatedItemRestaurant.getString("delivery_charges"));
                            float deliveryChargeSlab = Float.parseFloat(currentTopRatedItemRestaurant.getString("delivery_charge_slab"));
                            boolean acceptsOnlinePayment = currentTopRatedItemRestaurant.getInt("accepts_online_payment")==1;
                            String code = currentTopRatedItemRestaurant.getString("code");
                            int hasNewOffer = currentTopRatedItemRestaurant.getInt("has_new_offer");
                            int newOffer = currentTopRatedItemRestaurant.getInt("new_offer");
                            int offer = currentTopRatedItemRestaurant.getInt("offer");
                            String imageResourceId = currentTopRatedItemRestaurant.getString("imageResourceId");
                            String speciality = currentTopRatedItemRestaurant.getString("speciality");
                            float rating = (float)currentTopRatedItemRestaurant.getDouble("rating");
                            int messageStatus = currentTopRatedItemRestaurant.getInt("message_status");
                            String message;
                            if(messageStatus!=0){
                                message = currentTopRatedItemRestaurant.getString("message");
                            }else{
                                message = "";
                            }
                            Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

                            if(itemOffer>0){

                                hasOffer=true;
                                double temp=itemOffer;
                                itemOffer=itemPrice;
                                itemPrice=temp;
                            }
                            RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);

                            topRatedItems.add(restaurantItem);
                        }

                        for(int i=0; i<mostOrderedJSONArray.length(); i++){
                            JSONObject currentMostOrderedItem = mostOrderedJSONArray.getJSONObject(i);
                            JSONObject currentMostOrderedItemRestaurant = currentMostOrderedItem.getJSONObject("restaurant");
                            String itemName = currentMostOrderedItem.getString("item_name");
                            double itemPrice = Double.parseDouble(currentMostOrderedItem.getString("item_price")) ;
                            String itemCode = currentMostOrderedItem.getString("item_code");
                            String itemRestaurantCategory =  currentMostOrderedItem.getString("item_restaurant_category");
                            float itemRating = Float.parseFloat( currentMostOrderedItem.getString("item_rating"));
                            int itemRatingCount = Integer.parseInt( currentMostOrderedItem.getString("item_rating_count"));
                            String itemCurrentRatingByUser = currentMostOrderedItem.getString("item_current_rating_by_user");
                            int itemOrderCount = Integer.parseInt( currentMostOrderedItem.getString("item_order_count"));
                            Boolean itemIsFavourite = currentMostOrderedItem.getBoolean("item_is_favourite");
                            Boolean itemIsVeg = currentMostOrderedItem.getBoolean("item_is_veg");
                            boolean itemIsRecommended = currentMostOrderedItem.getBoolean("item_is_recommended");
                            String itemImageResource = currentMostOrderedItem.getString("item_image_resource");
                            Double itemOffer;
                            if(currentMostOrderedItem.getString("offer_price")!=null){
                                itemOffer = Double.parseDouble(currentMostOrderedItem.getString("offer_price"));
                            }else{
                                itemOffer = 0.0;
                            }
                            Boolean hasOffer=false;

                            String name = currentMostOrderedItemRestaurant.getString("name");
                            String contact = currentMostOrderedItemRestaurant.getString("contact");
                            String address = currentMostOrderedItemRestaurant.getString("address");
                            String status = (currentMostOrderedItemRestaurant.getString("open").equals("1")) ? "open" : "close";
                            String minOrderAmount = currentMostOrderedItemRestaurant.getString("minOrderAmount");
                            String timing = currentMostOrderedItemRestaurant.getString("timing");
                            float deliveryCharges = Float.parseFloat(currentMostOrderedItemRestaurant.getString("delivery_charges"));
                            float deliveryChargeSlab = Float.parseFloat(currentMostOrderedItemRestaurant.getString("delivery_charge_slab"));
                            boolean acceptsOnlinePayment = currentMostOrderedItemRestaurant.getInt("accepts_online_payment")==1;
                            String code = currentMostOrderedItemRestaurant.getString("code");
                            int hasNewOffer = currentMostOrderedItemRestaurant.getInt("has_new_offer");
                            int newOffer = currentMostOrderedItemRestaurant.getInt("new_offer");
                            int offer = currentMostOrderedItemRestaurant.getInt("offer");
                            String imageResourceId = currentMostOrderedItemRestaurant.getString("imageResourceId");
                            String speciality = currentMostOrderedItemRestaurant.getString("speciality");
                            float rating = (float)currentMostOrderedItemRestaurant.getDouble("rating");
                            int messageStatus = currentMostOrderedItemRestaurant.getInt("message_status");
                            String message;
                            if(messageStatus!=0){
                                message = currentMostOrderedItemRestaurant.getString("message");
                            }else{
                                message = "";
                            }
                            Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);
                            if(itemOffer>0){

                                hasOffer=true;
                                double temp=itemOffer;
                                itemOffer=itemPrice;
                                itemPrice=temp;
                            }
                            RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);

                            mostOrderedItems.add(restaurantItem);
                        }
                    }
                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                }


                LinearLayoutManager topRatedItemsLLM = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
                LinearLayoutManager mostOrderedItemsLLM = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);

                TopRatedItemsAdapter topRatedItemsAdapter = new TopRatedItemsAdapter(context,topRatedItems);
                MostOrderedItemsAdapter mostOrderedItemsAdapter = new MostOrderedItemsAdapter(context,mostOrderedItems);

                topRatedRecycler.setLayoutManager(topRatedItemsLLM);
                topRatedRecycler.setAdapter(topRatedItemsAdapter);

                mostOrderedRecycler.setLayoutManager(mostOrderedItemsLLM);
                mostOrderedRecycler.setAdapter(mostOrderedItemsAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"onErrorResponse");
                Intent intent = new Intent(context,NoInternetActivity.class);
                context.startActivity(intent);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                if(recommendationKey==Config.KEY_GET_RECOMMENDATION_FOR_RESTAURANT){
                    params.put("restaurant_code", restaurantCode);
                    params.put("UID", AppController.getInstance().getDbManager().getUserUid());
                    params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());
                }else if(recommendationKey==Config.KEY_GET_RECOMMENDATION_FOR_ITEM){
                    params.put("item_code", itemCode);
                    params.put("UID", AppController.getInstance().getDbManager().getUserUid());
                    params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());
                }else{
                    params.put("UID", AppController.getInstance().getDbManager().getUserUid());
                    params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());
                }

                Log.d(TAG,params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
