package com.saddacampus.app.app.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.RestaurantItemListAdapter;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantCategoryItemsFragment extends Fragment {

    private static String TAG = RestaurantCategoryItemsFragment.class.getSimpleName();


    public RestaurantCategoryItemsFragment() {
        // Required empty public constructor
    }

    private JSONObject categoryObject;
    private JSONArray itemArray;
    private Restaurant restaurant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.restaurant_menu_fragment, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String objectString = bundle.getString("restaurantMenu");
            String restaurantString = bundle.getString("restaurant");
            try{
                categoryObject = new JSONObject(objectString);

                itemArray = categoryObject.getJSONArray("items");

                Gson gson = new Gson();
                restaurant = gson.fromJson(restaurantString, Restaurant.class);
            }catch (JSONException e){
                Log.d(TAG,e.getMessage());
            }
        }

       /* RestaurantCategory activity = (RestaurantCategory) getActivity();
        Category myDataFromActivity = activity.getMyData(position);

        Toast.makeText(getContext(),myDataFromActivity.getCategoryName(),Toast.LENGTH_LONG).show();*/


        final ArrayList<RestaurantItem> restaurantItems = new ArrayList<RestaurantItem>();

        for(int i=0; i<itemArray.length(); i++){


            JSONObject itemObject;
            String itemName;
            String itemCode;
            String itemCategory;
            double itemPrice;
            boolean itemIsVeg;
            boolean itemIsFavourite;
            float itemRating;
            int itemRatingCount;
            double itemOffer;
            String itemCurrentRatingByUser;
            int itemOrderCount;

            try{
                itemObject = itemArray.getJSONObject(i);
                itemName = itemObject.getString("item_name");
                itemCode = itemObject.getString("item_code");
                itemCategory = itemObject.getString("item_restaurant_category");
                itemPrice = ((double) itemObject.getInt("item_price"));
                itemIsVeg = itemObject.getBoolean("item_is_veg");
                itemOffer = Double.parseDouble(itemObject.getString("offer_price"));
                itemRating = Float.parseFloat(itemObject.getString("item_rating"));
                itemRatingCount = Integer.parseInt(itemObject.getString("item_rating_count"));
                itemCurrentRatingByUser = itemObject.getString("item_current_rating_by_user");
                itemIsFavourite = itemObject.getBoolean("item_is_favourite");
                itemOrderCount = Integer.parseInt(itemObject.getString("item_order_count"));
                boolean itemIsRecommended = itemObject.getBoolean("item_is_recommended");
                String itemImageResource = itemObject.getString("item_image_resource");
                Boolean hasOffer=false;
                if(itemOffer>0){

                    hasOffer=true;
                    double temp=itemOffer;
                    itemOffer=itemPrice;
                    itemPrice=temp;
                }
                RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);
                restaurantItems.add(restaurantItem);
            }catch (JSONException e){
                Log.d(TAG,e.getMessage());
            }

        }

        RestaurantItemListAdapter adapter = new RestaurantItemListAdapter(restaurantItems, getContext());

        RecyclerView rv = (RecyclerView)rootView.findViewById(R.id.restaurant_menu_recycler) ;
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        LinearLayoutManager la = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(la);
        rv.setHasFixedSize(true);


        rv.setAdapter(adapter);

        return rootView;
    }

}
