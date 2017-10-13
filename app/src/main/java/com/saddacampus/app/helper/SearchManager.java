package com.saddacampus.app.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.saddacampus.app.activity.NoInternetActivity;
import com.saddacampus.app.activity.SearchActivity;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.app.DataObjects.RestaurantSearch;
import com.saddacampus.app.app.DataObjects.SearchResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class SearchManager {

    private static String TAG = SearchManager.class.getSimpleName();

    ProgressDialog gettingSearchDialog;


    private String query;
    private String userId;
    private String selectedCity;
    private String currentRestaurantCode;
    private boolean isRestaurantActivity;
    private Context context;
    String currentRestaurantName;

    public SearchManager(Context context, String query, String userId, String selectedCity, boolean isRestaurantActivity, String currentRestaurantCode, String currentRestaurantName) {
        this.context = context;
        this.query = query;
        this.userId = userId;
        this.selectedCity = selectedCity;
        this.isRestaurantActivity = isRestaurantActivity;
        this.currentRestaurantCode = currentRestaurantCode;
        this.currentRestaurantName = currentRestaurantName;
    }

    public String getCurrentRestaurantCode() {
        return currentRestaurantCode;
    }

    public void setCurrentRestaurantCode(String currentRestaurantCode) {
        this.currentRestaurantCode = currentRestaurantCode;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public boolean isRestaurantActivity() {
        return isRestaurantActivity;
    }

    public void getSearchResults(){
        gettingSearchDialog = new ProgressDialog(context);
        gettingSearchDialog.setMessage("Searching...");
        gettingSearchDialog.setCancelable(false);
        gettingSearchDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_SEARCH_RESULT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(SearchManager.class.getSimpleName(),response);
                JSONObject responseObject;
                JSONObject searchResultObject;
                JSONArray restaurantsFoundArray ;
                JSONArray restaurantItemsFoundArray;
                RestaurantSearch restaurantSearch;
                ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList = new ArrayList<>();
                ArrayList<Restaurant> restaurantsFoundArrayList = new ArrayList<>();

                try{
                    responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("error")){
                        Toast.makeText(context,"Unknown Error in getting result",Toast.LENGTH_SHORT).show();
                        gettingSearchDialog.dismiss();
                    }else{
                        searchResultObject = responseObject.getJSONObject("result");
                        restaurantsFoundArray = searchResultObject.getJSONArray("restaurants");
                        restaurantItemsFoundArray = searchResultObject.getJSONArray("food_items");

                        for(int i = 0; i< restaurantsFoundArray.length(); i++){
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
                            restaurantsFoundArrayList.add(restaurant);
                        }
                        restaurantSearch = new RestaurantSearch(restaurantsFoundArrayList,restaurantsFoundArrayList.size());

                        for(int i = 0; i<restaurantItemsFoundArray.length(); i++){

                            JSONObject restaurantItemsFoundObject = restaurantItemsFoundArray.getJSONObject(i).getJSONObject("restaurant");
                            JSONArray restaurantItemsFound = restaurantItemsFoundObject.getJSONArray("found_items");
                            ArrayList <RestaurantItem> itemsFoundInRestaurant = new ArrayList<>();
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
                            float rating = (float)restaurantItemsFoundObject.getDouble("rating");
                            int messageStatus = restaurantItemsFoundObject.getInt("message_status");
                            String message;
                            if(messageStatus!=0){
                                message = restaurantItemsFoundObject.getString("message");
                            }else{
                                message = "";
                            }
                            Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

                            for(int j =0 ; j<restaurantItemsFound.length();j++){
                                JSONObject itemObject = restaurantItemsFound.getJSONObject(j);
                                String itemName = itemObject.getString("item_name");
                                double itemPrice = Double.parseDouble(itemObject.getString("item_price")) ;
                                String itemCode = itemObject.getString("item_code");
                                String itemRestaurantCategory =  itemObject.getString("item_restaurant_category");
                                Double itemOffer;
                                if(itemObject.getString("offer_price")!=null){
                                    itemOffer = Double.parseDouble(itemObject.getString("offer_price"));
                                }else{
                                    itemOffer = 0.0;
                                }

                                float itemRating = Float.parseFloat( itemObject.getString("item_rating"));
                                int itemRatingCount = Integer.parseInt( itemObject.getString("item_rating_count"));
                                String itemCurrentRatingByUser = itemObject.getString("item_current_rating_by_user");
                                int itemOrderCount = Integer.parseInt( itemObject.getString("item_order_count"));
                                Boolean itemIsFavourite = itemObject.getBoolean("item_is_favourite");
                                Boolean itemIsVeg = itemObject.getBoolean("item_is_veg");
                                boolean itemIsRecommended = itemObject.getBoolean("item_is_recommended");
                                String itemImageResource = itemObject.getString("item_image_resource");
                                Boolean hasOffer=false;
                                if(itemOffer>0){

                                    hasOffer=true;
                                    double temp=itemOffer;
                                    itemOffer=itemPrice;
                                    itemPrice=temp;
                                }

                                RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);
                                itemsFoundInRestaurant.add(restaurantItem);
                            }
                            RestaurantItemsSearch restaurantItemsSearch = new RestaurantItemsSearch(restaurant,itemsFoundInRestaurant,itemsFoundInRestaurant.size());
                            restaurantItemsSearchArrayList.add(restaurantItemsSearch);
                        }

                        SearchResults searchResults =  new SearchResults(restaurantSearch,restaurantItemsSearchArrayList);


                        Gson gson = new Gson();
                        String json = gson.toJson(searchResults);

                        AppController.getInstance().getDbManager().storeSearchResultJson(json);

                        Intent intent = new Intent(context,SearchActivity.class);/*
                        intent.putExtra("search_results",json);*/
                        intent.putExtra("is_restaurant_activity",isRestaurantActivity);
                        intent.putExtra("current_restaurant_code",currentRestaurantCode);
                        intent.putExtra("current_restaurant_name",currentRestaurantName);
                        intent.putExtra("query",query);

                        context.startActivity(intent);
                        gettingSearchDialog.dismiss();

                    }
                }catch (JSONException e){
                    Log.d(SearchManager.class.getSimpleName(),e.getMessage());
                    Toast.makeText(context,"Unknown Error in getting result",Toast.LENGTH_SHORT).show();
                    gettingSearchDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(SearchManager.class.getSimpleName(),error.getMessage());
                //Toast.makeText(context,"Unknown Error in getting result",Toast.LENGTH_SHORT).show();
                gettingSearchDialog.dismiss();
                Intent intent = new Intent(context,NoInternetActivity.class);
                context.startActivity(intent);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("query", query);
                params.put("UID", userId);
                params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());

                Log.d("ItemPopupWindow",params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
