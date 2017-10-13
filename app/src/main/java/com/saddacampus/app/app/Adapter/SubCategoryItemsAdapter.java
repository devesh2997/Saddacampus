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

import java.util.ArrayList;

/**
 * Created by Shubham Vishwakarma on 07-07-2017.
 */

public class SubCategoryItemsAdapter extends RecyclerView.Adapter<SubCategoryItemsAdapter.RestaurantItemSearchViewHolder> {

    private static String TAG = SubCategoryItemsAdapter.class.getSimpleName();

    ArrayList<RestaurantItemsSearch> restaurantItemsFound;
    private Context context;

    public SubCategoryItemsAdapter(ArrayList<RestaurantItemsSearch> items) {
        this.restaurantItemsFound = items;
    }

    @Override
    public RestaurantItemSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantItemSearchItem = R.layout.sub_category_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantItemSearchItem,parent,shouldAttachToParentImmediately);
        RestaurantItemSearchViewHolder restaurantItemSearchViewHolder= new RestaurantItemSearchViewHolder(view);
        return restaurantItemSearchViewHolder;
    }

    @Override
    public void onBindViewHolder(SubCategoryItemsAdapter.RestaurantItemSearchViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return restaurantItemsFound.size();

    }

    public class RestaurantItemSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RecyclerView foundItemsRecycler;
        TextView restaurantItemFoundRestaurantName;

        @Override
        public void onClick(View v) {

           /* RestaurantItem resItem = searchResults.getRestaurantItemSearchResults().get(getPosition());
            Intent intent = new Intent(context, SearchedItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("value",searchResults.getRestaurantItemSearchResults().get(getPosition()) );
            // bundle.putSerializable("restaurant", restaurant);
            intent.putExtras(bundle);
            context.startActivity(intent);*/
            Log.d(TAG,"item clicked" + getPosition());
        }

        public RestaurantItemSearchViewHolder(View restaurantItemSearchView){
            super(restaurantItemSearchView);
            restaurantItemSearchView.setOnClickListener(this);

            foundItemsRecycler = (RecyclerView)restaurantItemSearchView.findViewById(R.id.sub_category_items);
            restaurantItemFoundRestaurantName = (TextView)restaurantItemSearchView.findViewById(R.id.restaurant_name);
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
