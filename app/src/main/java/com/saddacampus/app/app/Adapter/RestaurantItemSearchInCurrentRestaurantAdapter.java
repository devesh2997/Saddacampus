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
import com.saddacampus.app.app.DataObjects.RestaurantItemsSearch;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantItemSearchInCurrentRestaurantAdapter extends RecyclerView.Adapter<RestaurantItemSearchInCurrentRestaurantAdapter.RestaurantItemSearchInCurrentRestaurantViewHolder> {

    private static String TAG = RestaurantItemSearchInCurrentRestaurantAdapter.class.getSimpleName();

    private RestaurantItemsSearch currentRestaurantItemsSearch;
    private Context context;

    public RestaurantItemSearchInCurrentRestaurantAdapter (RestaurantItemsSearch currentRestaurantItemsSearch){
        this.currentRestaurantItemsSearch = currentRestaurantItemsSearch;
    }

    @Override
    public RestaurantItemSearchInCurrentRestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantItemSearchInCurrentRestaurantItem = R.layout.restaurant_item_search_in_current_restaurant_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantItemSearchInCurrentRestaurantItem,parent,shouldAttachToParentImmediately);

        RestaurantItemSearchInCurrentRestaurantViewHolder restaurantItemSearchInCurrenRestaurantViewHolder = new RestaurantItemSearchInCurrentRestaurantViewHolder(view);

        return restaurantItemSearchInCurrenRestaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantItemSearchInCurrentRestaurantViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return currentRestaurantItemsSearch.getFoundItemsCount();
    }

    public class RestaurantItemSearchInCurrentRestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView restaurantItemFoundName;
        ImageView restaurantItemFoundIsVegView;
        TextView restaurantItemFoundCategory;
        TextView restaurantItemFoundPrice;
        ImageView restaurantItemFoundRatingView;

        @Override
        public void onClick(View v) {
            //// TODO: 30-06-2017
            Log.d(TAG,"item Clicked");
            Intent intent = new Intent(context, ItemInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_item",currentRestaurantItemsSearch.getFoundRestaurantItems().get(getAdapterPosition()) );
            bundle.putInt("isOtherRes",0);
            // bundle.putSerializable("restaurant", restaurant);
            intent.putExtras(bundle);
            context.startActivity(intent);
            Log.d(TAG,"item Clicked");
        }

        public RestaurantItemSearchInCurrentRestaurantViewHolder(View restaurantItemSearchInCurrentRestaurantView){
            super(restaurantItemSearchInCurrentRestaurantView);
            restaurantItemSearchInCurrentRestaurantView.setOnClickListener(this);

            restaurantItemFoundName = (TextView)restaurantItemSearchInCurrentRestaurantView.findViewById(R.id.restaurant_item_search_in_current_restaurant_name);
            restaurantItemFoundIsVegView = (ImageView)restaurantItemSearchInCurrentRestaurantView.findViewById(R.id.restaurant_item_search_in_current_restaurant_is_veg);
            restaurantItemFoundPrice = (TextView)restaurantItemSearchInCurrentRestaurantView.findViewById(R.id.restaurant_item_search_in_current_restaurant_price);
            restaurantItemFoundCategory = (TextView) restaurantItemSearchInCurrentRestaurantView.findViewById(R.id.restaurant_item_search_in_current_restaurant_category);
            restaurantItemFoundRatingView = (ImageView)restaurantItemSearchInCurrentRestaurantView.findViewById(R.id.restaurant_item_search_in_current_restaurant_rating);


        }

        void bind(int listIndex){
            RestaurantItem foundRestaurantItem = currentRestaurantItemsSearch.getFoundRestaurantItems().get(listIndex);
            restaurantItemFoundName.setText(foundRestaurantItem.getName());
            restaurantItemFoundCategory.setText(foundRestaurantItem.getCategory());

            String priceString = "\u20B9"+String.valueOf(foundRestaurantItem.getPrice());
            restaurantItemFoundPrice.setText(priceString);

            int rating = (int)foundRestaurantItem.getRating();
            if(foundRestaurantItem.getRatingCount()==0){
                restaurantItemFoundRatingView.setVisibility(View.INVISIBLE);
            }else{
                restaurantItemFoundRatingView.setVisibility(View.VISIBLE);
                String ratingImageResource = "star_".concat(String.valueOf(rating));
                switch (rating){
                    case 0:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_0);
                        break;
                    case 1:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_1);
                        break;
                    case 2:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_2);
                        break;
                    case 3:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_3);
                        break;
                    case 4:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_4);
                        break;
                    case 5:
                        restaurantItemFoundRatingView.setImageResource(R.drawable.star_5);
                        break;
                }
            }

            if(foundRestaurantItem.isVeg()){
                restaurantItemFoundIsVegView.setImageResource(R.drawable.veg_icon);
            }else{
                restaurantItemFoundIsVegView.setImageResource(R.drawable.nonvegicon);
            }
        }
    }
}
