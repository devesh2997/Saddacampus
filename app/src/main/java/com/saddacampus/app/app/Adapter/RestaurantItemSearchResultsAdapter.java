package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.app.DataObjects.SearchResults;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantItemSearchResultsAdapter extends RecyclerView.Adapter<RestaurantItemSearchResultsAdapter.RestaurantItemSearchViewHolder> {

    private static String TAG = RestaurantItemSearchResultsAdapter.class.getSimpleName();

    private SearchResults searchResults;
    private Context context;

    public RestaurantItemSearchResultsAdapter(SearchResults searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public RestaurantItemSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantItemSearchItem = R.layout.restaurant_item_search_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantItemSearchItem,parent,shouldAttachToParentImmediately);
        RestaurantItemSearchViewHolder restaurantItemSearchViewHolder= new RestaurantItemSearchViewHolder(view);
        return restaurantItemSearchViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantItemSearchViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return searchResults.getRestaurantItemSearchResults().size();

    }

    public class RestaurantItemSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ArrayList<RestaurantItemsSearch> restaurantItemsFound = searchResults.getRestaurantItemSearchResults();

        RecyclerView foundItemsRecycler;
        TextView restaurantItemFoundRestaurantName;

        @Override
        public void onClick(View v) {
            //// TODO: 30-06-2017
        }

        public RestaurantItemSearchViewHolder(View restaurantItemSearchView){
            super(restaurantItemSearchView);
            restaurantItemSearchView.setOnClickListener(this);

            foundItemsRecycler = (RecyclerView)restaurantItemSearchView.findViewById(R.id.found_items_recycler);
            restaurantItemFoundRestaurantName = (TextView)restaurantItemSearchView.findViewById(R.id.restaurant_item_search_item_restaurant_name);
        }

        void bind(int listIndex){
            RestaurantItemsSearch restaurantItemsSearch = restaurantItemsFound.get(listIndex);
            Restaurant restaurant = restaurantItemsSearch.getRestaurantSearched();
            restaurantItemFoundRestaurantName.setText(restaurant.getName());


            SearchItemsFoundInARestaurantAdapter searchItemsFoundInARestaurantAdapter = new SearchItemsFoundInARestaurantAdapter(restaurantItemsSearch.getFoundRestaurantItems());
            final LinearLayoutManager foundItemsInARestaurantLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            foundItemsRecycler.setAdapter(searchItemsFoundInARestaurantAdapter);
            foundItemsRecycler.setLayoutManager(foundItemsInARestaurantLayoutManager);
            foundItemsRecycler.setHasFixedSize(true);
        }
    }
}
