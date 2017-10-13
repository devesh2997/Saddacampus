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
import com.saddacampus.app.app.Adapter.RestaurantItemSearchInCurrentRestaurantAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;
import com.saddacampus.app.app.DataObjects.SearchResults;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantItemSearchInCurrentRestaurantFragment extends Fragment {

    private static String TAG = RestaurantItemSearchInCurrentRestaurantFragment.class.getSimpleName();

    private RestaurantItemSearchInCurrentRestaurantAdapter restaurantItemSearchInCurrentRestaurantAdapter;

    private RecyclerView restaurantItemSearchInCurrentRestaurantRecycler;

    LinearLayout noItems;

    public RestaurantItemSearchInCurrentRestaurantFragment(){

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurant_item_search_results_in_current_restaurant_fragment,container,false);

        Bundle bundle = this.getArguments();
        String currentRestaurantCode = bundle.getString("current_restaurant_code");
        String currentRestaurantName = bundle.getString("current_restaurant_name");
        SearchResults searchResults;
        noItems = (LinearLayout)rootView.findViewById(R.id.no_items_container);
        noItems.setVisibility(View.GONE);

        Gson gson = new Gson();
        searchResults = gson.fromJson(AppController.getInstance().getDbManager().getSearchResultJson(),SearchResults.class);

        restaurantItemSearchInCurrentRestaurantRecycler = (RecyclerView)rootView.findViewById(R.id.restaurant_item_search_results__in_current_restaurant_recycler);

        final LinearLayoutManager restaurantItemSearchInCurrentRestaurantLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

        RestaurantItemsSearch currentRestaurantItemsSearch = null;
        ArrayList<RestaurantItemsSearch> restaurantItemsSearchArrayList = searchResults.getRestaurantItemSearchResults();

        for(int i= 0;i < restaurantItemsSearchArrayList.size(); i++){
            Restaurant restaurant = restaurantItemsSearchArrayList.get(i).getRestaurantSearched();
            if(restaurant.getCode().equals(currentRestaurantCode)&&restaurant.getName().equals(currentRestaurantName)){
                currentRestaurantItemsSearch = restaurantItemsSearchArrayList.get(i);
            }

        }

        if(currentRestaurantItemsSearch.getFoundItemsCount()==0){
            noItems.setVisibility(View.VISIBLE);
        }

        RestaurantItemSearchInCurrentRestaurantAdapter restaurantItemSearchInCurrentRestaurantAdapter = new RestaurantItemSearchInCurrentRestaurantAdapter(currentRestaurantItemsSearch);

        restaurantItemSearchInCurrentRestaurantRecycler.setLayoutManager(restaurantItemSearchInCurrentRestaurantLayoutManager);
        restaurantItemSearchInCurrentRestaurantRecycler.setHasFixedSize(true);
        restaurantItemSearchInCurrentRestaurantRecycler.setAdapter(restaurantItemSearchInCurrentRestaurantAdapter);

        return rootView;

    }
}
