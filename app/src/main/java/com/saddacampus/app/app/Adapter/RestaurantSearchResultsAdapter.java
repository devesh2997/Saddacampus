package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.activity.RestaurantMenu;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.SearchResults;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 30-06-2017.
 */

public class RestaurantSearchResultsAdapter extends RecyclerView.Adapter<RestaurantSearchResultsAdapter.RestaurantSearchViewHolder> {

    private static String TAG = RestaurantSearchResultsAdapter.class.getSimpleName();
    private SearchResults searchResults = new SearchResults();
    private Context context;

    public RestaurantSearchResultsAdapter(SearchResults searchResults){
        this.searchResults = searchResults;
        //Log.d(TAG, searchResults.getRestaurantSearchResults().getRestaurantsFound().get(0).getName());
    }

    @Override
    public RestaurantSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantSearchItem = R.layout.restaurant_search_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantSearchItem,parent,shouldAttachToParentImmediately);
        RestaurantSearchViewHolder restaurantSearchViewHolder= new RestaurantSearchViewHolder(view);
        return restaurantSearchViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantSearchViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return searchResults.getRestaurantSearchResults().getRestaurantsFoundCount();
        //return 1;
    }

    public class RestaurantSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ArrayList<Restaurant> restaurantsFound = searchResults.getRestaurantSearchResults().getRestaurantsFound();

        ImageView restaurantImageView ;
        SimpleRatingBar restaurantRatingView ;
        TextView restaurantNameView;
        TextView restaurantStatusView;
        TextView restaurantMinOrderAmountView;
        TextView restaurantDeliveryChargeView;
        TextView restaurantAddressView;
        TextView restaurantSpecialityView;
        TextView restaurantOfferView;

        @Override
        public void onClick(View v) {
            //// TODO: 30-06-2017
            Intent myIntent = new Intent(v.getContext(), RestaurantMenu.class);
            final Restaurant restaurant = searchResults.getRestaurantSearchResults().getRestaurantsFound().get(getPosition());
            String message;
            if(restaurant.getMessageStatus()!=0){
                message = restaurant.getMessage();
            }else {
                message = "";
            }
            myIntent.putExtra("restaurant", restaurant);
            myIntent.putExtra("message",message);
            v.getContext().startActivity(myIntent);
        }

        public RestaurantSearchViewHolder(View restaurantSearchView){
            super(restaurantSearchView);
            restaurantSearchView.setOnClickListener(this);



            restaurantImageView = (ImageView)restaurantSearchView.findViewById(R.id.restaurant_image_view);
            restaurantRatingView = (SimpleRatingBar) restaurantSearchView.findViewById(R.id.restaurant_rating_view);
            restaurantNameView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_item_name_view);
            restaurantStatusView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_status_view);
            restaurantMinOrderAmountView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_minOrderAmount_view);
            restaurantDeliveryChargeView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_deliveryCharge_view);
            restaurantAddressView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_address_view);
            restaurantSpecialityView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_speciality_view);
            restaurantOfferView = (TextView)restaurantSearchView.findViewById(R.id.restaurant_offer_view);

        }

        void bind (int listIndex){
            Restaurant restaurant = restaurantsFound.get(listIndex);

            String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(restaurantsFound.get(listIndex).getImageResourceId());

            Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.placeholder).fit().into(restaurantImageView);
            restaurantNameView.setText(restaurantsFound.get(listIndex).getName());
            restaurantRatingView.setRating(restaurantsFound.get(listIndex).getRating());
            if(restaurant.getStatus().equals("open")){
                restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorGreen));
                restaurantStatusView.setText("OPEN NOW");
            }else{
                restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorPinkDark));
                restaurantStatusView.setText("CLOSED NOW");
            }

            restaurantAddressView.setText("("+restaurantsFound.get(listIndex).getAddress()+")");
            restaurantSpecialityView.setText("Speciality : "+restaurantsFound.get(listIndex).getSpeciality());

            restaurantMinOrderAmountView.setText("Minimum Order Amount : "+"\u20B9"+restaurantsFound.get(listIndex).getMinOrderAmount());
            if(restaurant.getDeliveryCharges()==0){
                //restaurantDeliveryChargeView.setCompoundDrawables(null,null,context.getDrawable(R.drawable.ic_free_delivery),null);
                restaurantDeliveryChargeView.setText("Delivery Charges : Free");

            }
            else{
                restaurantDeliveryChargeView.setText("Delivery Charges : "+String.valueOf(restaurantsFound.get(listIndex).getDeliveryCharges()));
                restaurantDeliveryChargeView.setCompoundDrawables(null,null,null,null);
            }

            if(restaurant.getHasNewOffer()==0){
                if(restaurant.getOffer()==0){
                    restaurantOfferView.setVisibility(View.INVISIBLE);
                }else{
                    restaurantOfferView.setBackgroundResource(R.drawable.offer_text_background);
                    restaurantOfferView.setText(restaurant.getOffer()+"% OFF !");

                }

            }else {
                restaurantOfferView.setBackgroundResource(R.drawable.offer_text_background);
                restaurantOfferView.setText(String.valueOf(restaurant.getNewOffer()).concat("% OFF !"));
            }


        }
    }
}
