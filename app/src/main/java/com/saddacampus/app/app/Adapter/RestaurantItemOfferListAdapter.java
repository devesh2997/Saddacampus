package com.saddacampus.app.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.activity.itemPopUpActivity;
import com.saddacampus.app.app.AddToCartCustomButton;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import java.util.ArrayList;

/**
 * Created by Shubham Vishwakarma on 14-06-2017.
 */

public class RestaurantItemOfferListAdapter extends RecyclerView.Adapter<RestaurantItemOfferListAdapter.RestaurantItemViewHolder> {

    private static final String TAG = RestaurantListAdapter.class.getSimpleName();

    private ArrayList<RestaurantItem> restaurantItemArrayList;

    private Context context;


    public RestaurantItemOfferListAdapter(ArrayList<RestaurantItem> restaurantArrayList, Context context) {
        this.restaurantItemArrayList = restaurantArrayList;
    }

    @Override
    public RestaurantItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantListItem = R.layout.restaurant_menu_offer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantListItem, parent, shouldAttachToParentImmediately);
        RestaurantItemViewHolder restaurantViewHolder = new RestaurantItemViewHolder(view);

        return restaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantItemViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }


    @Override
    public int getItemCount() {
        return restaurantItemArrayList.size();
    }

    public class RestaurantItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView restaurantItemNameView;
        TextView price;
        ImageView isVeg;
        RelativeLayout restaurantItemContainer;
        RestaurantItem restaurantItem;
        SimpleRatingBar ratingView;
        TextView ratingCount;
        ViewSwitcher restaurantItemAddToCartSwitcher;
        RelativeLayout restaurantItemAddView;
        RelativeLayout restaurantItemQuantityContainer;
        TextView restaurantItemQuantity;
        TextView restaurantItemIncreaseQuantity;
        TextView restaurantItemDecreaseQuantity;
        AddToCartCustomButton addToCartCustomButton;
        TextView itemOffer;
        View scratch;

        TextView restaurantName;




        @Override
        public void onClick(View v) {
            //showPopup(context,restaurantItem);
            /*ItemPopupWindow itemPopupWindow = new ItemPopupWindow(context,restaurantItem,new PopupWindow(context));
            itemPopupWindow.showItemPopup();*/

            Intent intent = new Intent(context,itemPopUpActivity.class);
            intent.putExtra("restaurant_item",restaurantItem);
            context.startActivity(intent);
            Activity activity = (Activity)context;
            activity.overridePendingTransition(R.anim.popup_in,R.anim.popup_out);

        }

        public RestaurantItemViewHolder(View restaurantItemView) {
            super(restaurantItemView);
            restaurantItemView.setOnClickListener(this);

            restaurantItemContainer = (RelativeLayout) restaurantItemView.findViewById(R.id.restaurant_item_container);

            restaurantItemNameView = (TextView) restaurantItemView.findViewById(R.id.restaurant_item_name_view);
            price = (TextView) restaurantItemView.findViewById(R.id.price);
            isVeg = (ImageView)restaurantItemView.findViewById(R.id.veg);
            ratingCount = (TextView)restaurantItemView.findViewById(R.id.ratingCount) ;
            ratingView = (SimpleRatingBar)restaurantItemView.findViewById(R.id.ratingView);
            restaurantItemAddToCartSwitcher = (ViewSwitcher)itemView.findViewById(R.id.restaurant_item_add_to_cart_switcher);
            restaurantItemAddView = (RelativeLayout)itemView.findViewById(R.id.restaurant_item_add);
            restaurantItemQuantityContainer = (RelativeLayout)itemView.findViewById(R.id.restaurant_item_quantity_container);
            restaurantItemQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_quantity);
            restaurantItemIncreaseQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_increase_quantity);
            restaurantItemDecreaseQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_decrease_quantity);
            itemOffer = (TextView)itemView.findViewById(R.id.offer_price);
            scratch = (View)itemView.findViewById(R.id.scratch_off);
            restaurantName = (TextView)itemView.findViewById(R.id.restaurant_name);

        }

        void bind(int listIndex) {

            restaurantItem = restaurantItemArrayList.get(listIndex);
            double offer = restaurantItem.getItemOffer();
            double pricee= restaurantItem.getPrice();
            if (restaurantItem.isHasOffer()){

                itemOffer.setVisibility(View.VISIBLE);
                itemOffer.setText("\u20B9 " + String.valueOf(pricee));
                scratch.setVisibility(View.VISIBLE);

                String priceString = "\u20B9. "+String.valueOf(restaurantItem.getItemOffer());
                price.setText(priceString);


            }

            else {

                String priceString = "\u20B9. "+String.valueOf(restaurantItem.getPrice());
                price.setText(priceString);

                scratch.setVisibility(View.GONE);
                itemOffer.setVisibility(View.GONE);
            }
            restaurantItemNameView.setText(restaurantItem.getName());

            addToCartCustomButton = new AddToCartCustomButton(context,restaurantItem,restaurantItemAddToCartSwitcher,restaurantItemAddView,restaurantItemQuantityContainer,restaurantItemQuantity,restaurantItemIncreaseQuantity,restaurantItemDecreaseQuantity);
            addToCartCustomButton.show();

            if(restaurantItem.isVeg()){
                isVeg.setImageResource(R.drawable.veg_icon);
            }else{
                isVeg.setImageResource(R.drawable.nonvegicon);
            }


            String restaurantNameString = "Served By : " + restaurantItem.getItemRestaurant().getName();
            restaurantName.setText(restaurantNameString);



            int rating = (int)restaurantItem.getRating();
            if(restaurantItem.getRatingCount()==0){
                ratingView.setVisibility(View.INVISIBLE);
                ratingCount.setVisibility(View.INVISIBLE);
            }else{
                ratingCount.setText(String.valueOf(restaurantItem.getRatingCount()));
                switch (rating){
                    case 0:
                        ratingView.setRating(0);
                        break;
                    case 1:
                        ratingView.setRating(1);
                        break;
                    case 2:
                        ratingView.setRating(2);
                        break;
                    case 3:
                        ratingView.setRating(3);
                        break;
                    case 4:
                        ratingView.setRating(4);
                        break;
                    case 5:
                        ratingView.setRating(5);
                        break;
                }
            }



        }




    }

}
