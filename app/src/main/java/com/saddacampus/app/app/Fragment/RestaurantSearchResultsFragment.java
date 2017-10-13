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
import com.saddacampus.app.app.Adapter.RestaurantSearchResultsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.SearchResults;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantSearchResultsFragment extends Fragment {

    private String TAG = RestaurantSearchResultsFragment.class.getSimpleName();

    private RestaurantSearchResultsAdapter restaurantSearchResultsAdapter;
    private RecyclerView restaurantSearchResultsRecycler;
    LinearLayout noRestaurantContainer;

    public RestaurantSearchResultsFragment(){

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurant_search_results_fragment,container,false);

        Bundle bundle = this.getArguments();
        SearchResults searchResults ;
        noRestaurantContainer = (LinearLayout)rootView.findViewById(R.id.no_restaurant_container);
        noRestaurantContainer.setVisibility(View.GONE);

        Gson gson = new Gson();
        searchResults = gson.fromJson(AppController.getInstance().getDbManager().getSearchResultJson(),SearchResults.class);
        if(searchResults.getRestaurantSearchResults().getRestaurantsFound().size()==0){
            noRestaurantContainer.setVisibility(View.VISIBLE);
        }


            restaurantSearchResultsRecycler = (RecyclerView)rootView.findViewById(R.id.restaurant_search_results_recycler);
        final LinearLayoutManager restaurantSearchLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);



        restaurantSearchResultsAdapter = new RestaurantSearchResultsAdapter(searchResults);

        restaurantSearchResultsRecycler.setLayoutManager(restaurantSearchLayoutManager);
        restaurantSearchResultsRecycler.setHasFixedSize(true);
        restaurantSearchResultsRecycler.setAdapter(restaurantSearchResultsAdapter);

        return rootView;

    }
}
