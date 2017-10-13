package com.saddacampus.app.app.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.saddacampus.app.app.DataObjects.SearchResults;
import com.saddacampus.app.app.Fragment.RestaurantItemSearchInCurrentRestaurantFragment;
import com.saddacampus.app.app.Fragment.RestaurantItemSearchResultsFragment;
import com.saddacampus.app.app.Fragment.RestaurantSearchResultsFragment;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class SearchActivityFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String TAG =SearchActivityFragmentPagerAdapter.class.getSimpleName();

    private SearchResults searchResults;
    private int numberOfTabs;
    private boolean isRestaurantActivity;
    String currentRestaurantCode;
    String currentRestaurantName;

    private Fragment fragment;

    public SearchActivityFragmentPagerAdapter (FragmentManager fm,int numberOfTabs, SearchResults searchResults, Boolean isRestaurantActivity, String currentRestaurantCode, String currentRestaurantName){
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.searchResults = searchResults;
        this.isRestaurantActivity = isRestaurantActivity;
        this.currentRestaurantCode = currentRestaurantCode;
        this.currentRestaurantName = currentRestaurantName;
    }

    @Override
    public Fragment getItem(int position) {
        if(!isRestaurantActivity && position==0){
            fragment = new RestaurantSearchResultsFragment();
            Bundle bundle = new Bundle();/*
            Gson gson = new Gson();
            String json = gson.toJson(searchResults);
            bundle.putString("restaurant_search",json);*/
            fragment.setArguments(bundle);
        }else if(!isRestaurantActivity && position==1){
            fragment = new RestaurantItemSearchResultsFragment();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();/*
            String json = gson.toJson(searchResults);
            bundle.putString("restaurant_items_search",json);*/
            bundle.putBoolean("is_restaurant_activity",isRestaurantActivity);
            bundle.putString("current_restaurant_code",currentRestaurantCode);
            bundle.putString("current_restaurant_name",currentRestaurantName);
            fragment.setArguments(bundle);
        }else if(isRestaurantActivity && position==0){
            fragment = new RestaurantItemSearchInCurrentRestaurantFragment();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String json = gson.toJson(searchResults);
            bundle.putString("restaurant_items_search",json);
            bundle.putString("current_restaurant_code",currentRestaurantCode);
            bundle.putString("current_restaurant_name",currentRestaurantName);
            fragment.setArguments(bundle);

        }else{
            fragment = new RestaurantItemSearchResultsFragment();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();/*
            String json = gson.toJson(searchResults);
            bundle.putString("restaurant_items_search",json);*/
            bundle.putBoolean("is_restaurant_activity",isRestaurantActivity);
            bundle.putString("current_restaurant_code",currentRestaurantCode);
            bundle.putString("current_restaurant_name",currentRestaurantName);
            fragment.setArguments(bundle);
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
        String title = "Search Results";
        // Generate title based on item position
        if(!isRestaurantActivity && position==0){
            title = "Restaurants";
        }else if(!isRestaurantActivity && position==1){
            title = "Dishes";
        }else if(isRestaurantActivity && position==0){
            title = currentRestaurantName;
        }else if(isRestaurantActivity && position==1){
            title = "Other Restaurants";
        }
        return title;
    }
}
