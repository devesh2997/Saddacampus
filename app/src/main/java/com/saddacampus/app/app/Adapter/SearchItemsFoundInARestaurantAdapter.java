package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.activity.ItemInfoActivity;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class SearchItemsFoundInARestaurantAdapter extends RecyclerView.Adapter<SearchItemsFoundInARestaurantAdapter.SearchItemsFoundInARestaurantViewHolder> {

    private static String TAG = SearchItemsFoundInARestaurantAdapter.class.getSimpleName();

    private ArrayList<RestaurantItem> foundRestaurantItemsInARestaurant = new ArrayList<>();

    private Context context;

    public SearchItemsFoundInARestaurantAdapter(ArrayList foundRestaurantItemsInARestaurant){
        this.foundRestaurantItemsInARestaurant = foundRestaurantItemsInARestaurant;
    }

    @Override
    public SearchItemsFoundInARestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForFoundItemsInARestaurant = R.layout.restaurant_item_search_item_info_recycler_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFoundItemsInARestaurant,parent,shouldAttachToParentImmediately);

        SearchItemsFoundInARestaurantViewHolder searchItemsFoundInARestaurantViewHolder = new SearchItemsFoundInARestaurantViewHolder(view);

        return searchItemsFoundInARestaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchItemsFoundInARestaurantViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return foundRestaurantItemsInARestaurant.size();
    }

    public class SearchItemsFoundInARestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView foundItemName;
        TextView foundItemCategory;
        TextView foundItemPrice;
        ImageView foundItemIsVeg;
        ImageView foundItemRatingView;

        @Override
        public void onClick(View v) {
            //// TODO: 30-06-2017
            Intent intent = new Intent(context, ItemInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_item",foundRestaurantItemsInARestaurant.get(getPosition()) );
            bundle.putInt("isOtherRes",1);
            // bundle.putSerializable("restaurant", restaurant);
            intent.putExtras(bundle);
            context.startActivity(intent);
            Log.d(TAG,"item Clicked");
        }

        public SearchItemsFoundInARestaurantViewHolder(View searchItemsFoundInARestaurantView){
            super(searchItemsFoundInARestaurantView);
            searchItemsFoundInARestaurantView.setOnClickListener(this);

            foundItemName = (TextView)searchItemsFoundInARestaurantView.findViewById(R.id.found_restaurant_item_name);
            foundItemCategory = (TextView)searchItemsFoundInARestaurantView.findViewById(R.id.found_restaurant_item_category);
            foundItemPrice = (TextView)searchItemsFoundInARestaurantView.findViewById(R.id.found_restaurant_item_price);
            foundItemIsVeg = (ImageView)searchItemsFoundInARestaurantView.findViewById(R.id.found_restaurant_item_is_veg);
            foundItemRatingView = (ImageView)searchItemsFoundInARestaurantView.findViewById(R.id.found_restaurant_item_rating);

        }

        void bind(int listIndex){
            foundItemName.setText(foundRestaurantItemsInARestaurant.get(listIndex).getName());
            foundItemCategory.setText(foundRestaurantItemsInARestaurant.get(listIndex).getCategory());

            String priceString = "\u20B9"+String.valueOf(foundRestaurantItemsInARestaurant.get(listIndex).getPrice());
            foundItemPrice.setText(priceString);

            int rating = (int)foundRestaurantItemsInARestaurant.get(listIndex).getRating();
            if(foundRestaurantItemsInARestaurant.get(listIndex).getRatingCount()==0){
                foundItemRatingView.setVisibility(View.INVISIBLE);
            }else{
                foundItemRatingView.setVisibility(View.VISIBLE);
                String ratingImageResource = "star_".concat(String.valueOf(rating));
                switch (rating){
                    case 0:
                        foundItemRatingView.setImageResource(R.drawable.star_0);
                        break;
                    case 1:
                        foundItemRatingView.setImageResource(R.drawable.star_1);
                        break;
                    case 2:
                        foundItemRatingView.setImageResource(R.drawable.star_2);
                        break;
                    case 3:
                        foundItemRatingView.setImageResource(R.drawable.star_3);
                        break;
                    case 4:
                        foundItemRatingView.setImageResource(R.drawable.star_4);
                        break;
                    case 5:
                        foundItemRatingView.setImageResource(R.drawable.star_5);
                        break;
                }
            }

            if(foundRestaurantItemsInARestaurant.get(listIndex).isVeg()){
                foundItemIsVeg.setImageResource(R.drawable.veg_icon);
            }else{
                foundItemIsVeg.setImageResource(R.drawable.nonvegicon);
            }
        }
    }


}

