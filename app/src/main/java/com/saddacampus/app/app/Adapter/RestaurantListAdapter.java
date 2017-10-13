package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.activity.RestaurantMenu;
import com.saddacampus.app.app.Animation.RecyclerViewReboundAnimator;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.GrayscaleTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devesh Anand on 10-06-2017.
 */

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private static final String TAG = RestaurantListAdapter.class.getSimpleName();

    private ArrayList<Restaurant> restaurantArrayList;

    private Context context;

    private RecyclerViewReboundAnimator recyclerViewReboundAnimator;

    public RestaurantListAdapter(ArrayList<Restaurant> restaurantArrayList, RecyclerView restaurantsListRecycler) {
        this.restaurantArrayList = restaurantArrayList;
        recyclerViewReboundAnimator = new RecyclerViewReboundAnimator(restaurantsListRecycler);
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantListItem = R.layout.restaurant_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantListItem, parent, shouldAttachToParentImmediately);
        RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(view);

        recyclerViewReboundAnimator.onCreateViewHolder(view);

        return restaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

        recyclerViewReboundAnimator.onBindViewHolder(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView restaurantImageView;
        SimpleRatingBar restaurantRatingView;
        TextView restaurantNameView;
        TextView restaurantStatusView;
        TextView restaurantMinOrderAmountView;
        TextView restaurantDeliveryChargeView;
        TextView restaurantAddressView;
        TextView restaurantSpecialityView;
        LinearLayout restaurantOfferContainer;
        TextView restaurantOfferView;
        /*LinearLayout restaurantOfferContainerTwo;
        TextView restaurantOfferViewTwo;
*/


        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(v.getContext(), RestaurantMenu.class);
            final Restaurant restaurant = restaurantArrayList.get(getPosition());
            String message;
            if(restaurant.getMessageStatus()!=0){
                message = restaurant.getMessage();
            }else {
                message = "";
            }
            myIntent.putExtra("restaurant", restaurant);
            myIntent.putExtra("message",message);
            myIntent.putExtra("online",restaurant.acceptsOnlinePayment());
            v.getContext().startActivity(myIntent);
        }

        public RestaurantViewHolder(View restaurantItemView) {
            super(restaurantItemView);
            restaurantItemView.setOnClickListener(this);

            restaurantImageView = (ImageView) restaurantItemView.findViewById(R.id.restaurant_image_view);
            restaurantRatingView = (SimpleRatingBar) restaurantItemView.findViewById(R.id.restaurant_rating_view);
            restaurantNameView = (TextView) restaurantItemView.findViewById(R.id.restaurant_item_name_view);
            restaurantStatusView = (TextView) restaurantItemView.findViewById(R.id.restaurant_status_view);
            restaurantMinOrderAmountView = (TextView) restaurantItemView.findViewById(R.id.restaurant_minOrderAmount_view);
            restaurantDeliveryChargeView = (TextView) restaurantItemView.findViewById(R.id.restaurant_deliveryCharge_view);
            restaurantAddressView = (TextView) restaurantItemView.findViewById(R.id.restaurant_address_view);
            restaurantSpecialityView = (TextView) restaurantItemView.findViewById(R.id.restaurant_speciality_view);
            restaurantOfferContainer = (LinearLayout) restaurantItemView.findViewById(R.id.restaurant_offer_container);
            restaurantOfferView = (TextView) restaurantItemView.findViewById(R.id.restaurant_offer_view);
            /*restaurantOfferContainerTwo = (LinearLayout)restaurantItemView.findViewById(R.id.restaurant_offer_container_two);
            restaurantOfferViewTwo = (TextView)restaurantItemView.findViewById(R.id.restaurant_offer_view_two);
*/
        }

        void bind(int listIndex) {
            Restaurant restaurant = restaurantArrayList.get(listIndex);

            List<Transformation> transformations = new ArrayList<>();

            transformations.add(new GrayscaleTransformation(Picasso.with(context)));
            //transformations.add(new BlurTransformation(context));

            String url = Config.URL_GET_IMAGE_ASSETS + "Restaurants/".concat(restaurantArrayList.get(listIndex).getImageResourceId());

            if (restaurant.getHasNewOffer()==0) {
                if(restaurant.getOffer()==0){
                    restaurantOfferContainer.setVisibility(View.INVISIBLE);
                    restaurantOfferView.setVisibility(View.INVISIBLE);
                }else{
                    restaurantOfferContainer.setVisibility(View.VISIBLE);
                    restaurantOfferView.setVisibility(View.VISIBLE);
                    restaurantOfferView.setText(String.valueOf(restaurant.getOffer()).concat("%") );
                }

            } else {
                restaurantOfferView.setText(String.valueOf(restaurant.getNewOffer()).concat("%") );
                restaurantOfferContainer.setVisibility(View.VISIBLE);
                restaurantOfferView.setVisibility(View.VISIBLE);
            }





            restaurantNameView.setText(restaurantArrayList.get(listIndex).getName());

            restaurantRatingView.setIndicator(true);

            restaurantRatingView.setRating(restaurantArrayList.get(listIndex).getRating());
            if (restaurant.getStatus().equals("open")) {
                restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorGreen));
                restaurantStatusView.setText("OPEN");

                restaurantSpecialityView.setTextColor(ContextCompat.getColor(context, R.color.colorPurple));
                restaurantRatingView.setFillColor(context.getResources().getColor(R.color.golden_stars));
                restaurantOfferContainer.setBackgroundResource(R.drawable.triangular_background);

                Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.place_holder_color).fit().into(restaurantImageView);
            } else {
                restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorPinkDark));
                restaurantStatusView.setText("CLOSED");

                restaurantOfferContainer.setBackgroundResource(R.drawable.triangular_background_off);
                restaurantRatingView.setFillColor(context.getResources().getColor(R.color.colorGray));
                restaurantSpecialityView.setTextColor(ContextCompat.getColor(context, R.color.colorGray));

                Picasso.with(itemView.getContext()).load(url).transform(transformations).placeholder(R.drawable.place_holder_black_).fit().into(restaurantImageView);
            }

            restaurantAddressView.setText("(" + restaurantArrayList.get(listIndex).getAddress() + ")");
            restaurantSpecialityView.setText("Speciality : " + restaurantArrayList.get(listIndex).getSpeciality());

            restaurantMinOrderAmountView.setText("Minimum Order Amount : " + "\u20B9" + restaurantArrayList.get(listIndex).getMinOrderAmount());
            if (restaurant.getDeliveryCharges()==0 ) {
                //restaurantDeliveryChargeView.setCompoundDrawables(null,null,context.getDrawable(R.drawable.ic_free_delivery),null);
                restaurantDeliveryChargeView.setText("Delivery Charges : Free");

            } else {
                String deliveryChargeString = "Delivery Charges : " + String.valueOf(restaurantArrayList.get(listIndex).getDeliveryCharges());
                deliveryChargeString = deliveryChargeString.concat(" (Below orders of Rs. "+String.valueOf(restaurantArrayList.get(listIndex).getDeliveryChargeSlab())+")");
                restaurantDeliveryChargeView.setText(deliveryChargeString);
                restaurantDeliveryChargeView.setCompoundDrawables(null, null, null, null);
            }


        }


    }
}
