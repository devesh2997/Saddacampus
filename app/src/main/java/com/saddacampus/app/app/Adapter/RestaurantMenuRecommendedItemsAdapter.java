package com.saddacampus.app.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
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
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anand on 04-10-2017.
 */

public class RestaurantMenuRecommendedItemsAdapter extends RecyclerView.Adapter<RestaurantMenuRecommendedItemsAdapter.RestaurantMenuRecommendedItemViewHolder>{

    private static String TAG = RestaurantMenuRecommendedItemsAdapter.class.getSimpleName();

    private ArrayList<RestaurantItem> restaurantRecommendedItemArrayList;

    private Context context;


    public RestaurantMenuRecommendedItemsAdapter(ArrayList<RestaurantItem> restaurantRecommendedItemArrayList, Context context) {
        this.restaurantRecommendedItemArrayList = restaurantRecommendedItemArrayList;
    }

    @Override
    public RestaurantMenuRecommendedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForRestaurantListItem = R.layout.restaurant_menu_recommended_items_container;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForRestaurantListItem, parent, shouldAttachToParentImmediately);
        RestaurantMenuRecommendedItemViewHolder restaurantMenuRecommendedItemViewHolder = new RestaurantMenuRecommendedItemViewHolder(view);

        return restaurantMenuRecommendedItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantMenuRecommendedItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return restaurantRecommendedItemArrayList.size();
    }

    public class RestaurantMenuRecommendedItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView restaurantItemNameView;
        TextView restaurantItemCategory;
        TextView price;
        ImageView isVeg;
        RelativeLayout restaurantItemContainer;
        RestaurantItem restaurantItem;
        ViewSwitcher restaurantItemAddToCartSwitcher;
        RelativeLayout restaurantItemAddView;
        RelativeLayout restaurantItemQuantityContainer;
        TextView restaurantItemQuantity;
        TextView restaurantItemIncreaseQuantity;
        TextView restaurantItemDecreaseQuantity;
        AddToCartCustomButton addToCartCustomButton;

        ImageView restaurantItemImage;


        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context,itemPopUpActivity.class);
            intent.putExtra("restaurant_item",restaurantItem);
            context.startActivity(intent);
            Activity activity = (Activity)context;
            activity.overridePendingTransition(R.anim.popup_in,R.anim.popup_out);

        }

        public RestaurantMenuRecommendedItemViewHolder(View restaurantItemView) {
            super(restaurantItemView);
            restaurantItemView.setOnClickListener(this);

            restaurantItemContainer = (RelativeLayout) restaurantItemView.findViewById(R.id.restaurant_item_container);

            restaurantItemNameView = (TextView) restaurantItemView.findViewById(R.id.restaurant_item_name_view);
            restaurantItemCategory = (TextView)restaurantItemView.findViewById(R.id.item_category);
            price = (TextView) restaurantItemView.findViewById(R.id.item_price);
            isVeg = (ImageView)restaurantItemView.findViewById(R.id.veg);
            restaurantItemAddToCartSwitcher = (ViewSwitcher)itemView.findViewById(R.id.restaurant_item_add_to_cart_switcher);
            restaurantItemAddView = (RelativeLayout)itemView.findViewById(R.id.restaurant_item_add);
            restaurantItemQuantityContainer = (RelativeLayout)itemView.findViewById(R.id.restaurant_item_quantity_container);
            restaurantItemQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_quantity);
            restaurantItemIncreaseQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_increase_quantity);
            restaurantItemDecreaseQuantity = (TextView)itemView.findViewById(R.id.restaurant_item_decrease_quantity);

            restaurantItemImage = (ImageView)itemView.findViewById(R.id.item_image);

        }

        void bind(int listIndex) {

            restaurantItem = restaurantRecommendedItemArrayList.get(listIndex);
            double itemPrice = restaurantItem.getPrice();
            double offer = restaurantItem.getItemOffer();
            if (restaurantItem.isHasOffer()){
                String priceString = "\u20B9 "+String.valueOf(restaurantItem.getItemOffer());
                price.setText(priceString);
            }
            else {
                String priceString = "\u20B9 "+String.valueOf(restaurantItem.getPrice());
                price.setText(priceString);
            }
            restaurantItemNameView.setText(restaurantItem.getName());
            restaurantItemCategory.setText(restaurantItem.getCategory());

            addToCartCustomButton = new AddToCartCustomButton(context,restaurantItem,restaurantItemAddToCartSwitcher,restaurantItemAddView,restaurantItemQuantityContainer,restaurantItemQuantity,restaurantItemIncreaseQuantity,restaurantItemDecreaseQuantity);
            addToCartCustomButton.show();

            if(restaurantItem.isVeg()){
                isVeg.setImageResource(R.drawable.veg_icon);
            }else{
                isVeg.setImageResource(R.drawable.nonvegicon);
            }

            if(!restaurantItem.getImageResource().equals("-")){
                String url = Config.URL_GET_IMAGE_ASSETS + "cities/".concat(AppController.getInstance().getSessionManager().getSelectedCity())+"/".concat(restaurantItem.getItemRestaurant().getCode())+"/".concat(restaurantItem.getImageResource());
                Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.place_holder_black_).fit().into(restaurantItemImage);
            }

        }




    }
}
