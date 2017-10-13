package com.saddacampus.app.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.RestaurantItemSearchResultsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.app.DataObjects.SearchResults;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantItemSearchResultsFragment extends Fragment {

    private String TAG = RestaurantItemSearchResultsFragment.class.getSimpleName();

    private RestaurantItemSearchResultsAdapter restaurantItemSearchResultsAdapter;
    private RecyclerView restaurantItemSearchResultsRecycler;
    LinearLayout noItems;

    public RestaurantItemSearchResultsFragment(){

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.restaurant_item_search_results_fragment,container,false);

        Bundle bundle = this.getArguments();
        String currentRestaurantCode = bundle.getString("current_restaurant_code");
        String currentRestaurantName = bundle.getString("current_restaurant_name");
        SearchResults searchResults;

        noItems = (LinearLayout)rootview.findViewById(R.id.no_items_container);
        noItems.setVisibility(View.GONE);

        Gson gson = new Gson();
        searchResults = gson.fromJson(AppController.getInstance().getDbManager().getSearchResultJson(),SearchResults.class);

        restaurantItemSearchResultsRecycler = (RecyclerView)rootview.findViewById(R.id.restaurant_item_search_results_recycler);
        final LinearLayoutManager restaurantItemSearchLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

        if(bundle.getBoolean("is_restaurant_activity")){
            ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList = searchResults.getRestaurantItemSearchResults();
            for(int i= 0;i < restaurantItemsSearchArrayList.size(); i++){
                Restaurant restaurant = restaurantItemsSearchArrayList.get(i).getRestaurantSearched();
                if(restaurant.getCode().equals(currentRestaurantCode)&&restaurant.getName().equals(currentRestaurantName)){
                    restaurantItemsSearchArrayList.remove(i);
                    i--;
                }
            }
            for(int i= 0;i < restaurantItemsSearchArrayList.size(); i++){
                if(restaurantItemsSearchArrayList.get(i).getFoundItemsCount()==0){
                    restaurantItemsSearchArrayList.remove(i);
                    i--;
                }

            }

            searchResults.setRestaurantItemSearchResults(restaurantItemsSearchArrayList);
        }else{
            ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList = searchResults.getRestaurantItemSearchResults();
            for(int i= 0;i < restaurantItemsSearchArrayList.size(); i++){
                if(restaurantItemsSearchArrayList.get(i).getFoundItemsCount()==0){
                    restaurantItemsSearchArrayList.remove(i);
                    i--;
                }

            }

            searchResults.setRestaurantItemSearchResults(restaurantItemsSearchArrayList);
        }

        if(searchResults.getRestaurantItemSearchResults().size()==0){
            noItems.setVisibility(View.VISIBLE);
        }

        restaurantItemSearchResultsAdapter = new RestaurantItemSearchResultsAdapter(searchResults);

        restaurantItemSearchResultsRecycler.setLayoutManager(restaurantItemSearchLayoutManager);
        restaurantItemSearchResultsRecycler.setHasFixedSize(true);
        restaurantItemSearchResultsRecycler.setAdapter(restaurantItemSearchResultsAdapter);

        return rootview;

    }
}
