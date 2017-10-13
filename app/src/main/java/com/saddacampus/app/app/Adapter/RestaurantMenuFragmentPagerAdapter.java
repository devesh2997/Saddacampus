package com.saddacampus.app.app.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.google.gson.Gson;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.Fragment.RestaurantCategoryItemsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 05-06-2017.
 */

public class RestaurantMenuFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private String TAG = RestaurantMenuFragmentPagerAdapter.class.getSimpleName();


    private int numberOfTabs=0;
    private Restaurant restaurant;
    private Fragment fragment;
    private ArrayList<String> restaurantCategoriesArrayList;
    private JSONArray restaurantMenu;


    public RestaurantMenuFragmentPagerAdapter(FragmentManager fm, int numberOfTabs, ArrayList<String> restaurantCategoriesArrayList, JSONArray restaurantMenu, Restaurant restaurant) {
        super(fm);
        this.numberOfTabs=numberOfTabs;
        this.restaurantCategoriesArrayList = restaurantCategoriesArrayList;
        this.restaurantMenu = restaurantMenu;
        this.restaurant = restaurant;
    }


    @Override
    public Fragment getItem(int position) {
        for (int i = 0; i < numberOfTabs ; i++) {
            if (i == position) {
                fragment =new RestaurantCategoryItemsFragment();
                Bundle bundle = new Bundle();
                try {
                    JSONObject restaurantMenuCategory = restaurantMenu.getJSONObject(position);
                    bundle.putString("restaurantMenu",restaurantMenuCategory.toString());
                    Gson gson = new Gson();
                    String json = gson.toJson(restaurant);
                    bundle.putString("restaurant",json);
                    fragment.setArguments(bundle);
                    break;
                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                }

            }
        }
        return fragment;

    }


    @Override
    public int getCount()
    {
        return numberOfTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return restaurantCategoriesArrayList.get(position);
    }
}
