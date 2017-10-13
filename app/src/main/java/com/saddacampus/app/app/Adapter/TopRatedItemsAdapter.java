package com.saddacampus.app.app.Adapter;

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

import com.saddacampus.app.R;
import com.saddacampus.app.activity.ItemInfoActivity;
import com.saddacampus.app.app.AddToCartCustomButton;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 10-07-2017.
 */

public class TopRatedItemsAdapter extends RecyclerView.Adapter<TopRatedItemsAdapter.TopRatedItemsViewHolder>{

    private static String TAG = TopRatedItemsAdapter.class.getSimpleName();

    private Context context;

    private ArrayList<RestaurantItem> topRatedItems;

    /*ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;*/

    public TopRatedItemsAdapter(Context context, ArrayList<RestaurantItem> topRatedItems) {
        this.context = context;
        this.topRatedItems = topRatedItems;

        /*itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        context.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        context.registerReceiver(itemDeletedReceiver, itemDeletedFilter);*/
    }


    @Override
    public TopRatedItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutForFoundItemsInARestaurant = R.layout.top_rated_recommendation_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFoundItemsInARestaurant,parent,shouldAttachToParentImmediately);

        TopRatedItemsViewHolder topRatedItemsViewHolder = new TopRatedItemsViewHolder(view);

        return topRatedItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(TopRatedItemsViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }



    @Override
    public int getItemCount() {
        return topRatedItems.size();
    }

    public class TopRatedItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView topRatedItemName;
        TextView topRatedItemRestaurantName;
        TextView topRatedItemPrice;
        ImageView topRatedItemIsVeg;
        ImageView topRatedItemRatingView;
        ViewSwitcher topRatedItemAddToCartSwitcher;
        RelativeLayout addToCartContainer;
        RelativeLayout topRatedItemAddView;
        RelativeLayout topRatedItemQuantityContainer;
        TextView topRatedItemQuantity;
        TextView topRatedItemIncreaseQuantity;
        TextView topRatedItemDecreaseQuantity;
        AddToCartCustomButton addToCartCustomButton;

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemInfoActivity.class);
            intent.putExtra("restaurant_item",topRatedItems.get(getAdapterPosition()));
            context.startActivity(intent);
        }

        public TopRatedItemsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            topRatedItemName = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_name);
            topRatedItemRestaurantName = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_restaurant_name);
            topRatedItemPrice = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_price);
            topRatedItemIsVeg = (ImageView)itemView.findViewById(R.id.top_rated_recommendation_item_is_veg);
            topRatedItemRatingView = (ImageView)itemView.findViewById(R.id.top_rated_recommendation_item_rating);
            topRatedItemAddToCartSwitcher = (ViewSwitcher)itemView.findViewById(R.id.top_rated_item_add_to_cart_switcher);
            topRatedItemAddView = (RelativeLayout)itemView.findViewById(R.id.top_rated_recommendation_item_add);
            topRatedItemQuantityContainer = (RelativeLayout)itemView.findViewById(R.id.top_rated_item_quantity_container);
            topRatedItemQuantity = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_quantity);
            topRatedItemIncreaseQuantity = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_increase_quantity);
            topRatedItemDecreaseQuantity = (TextView)itemView.findViewById(R.id.top_rated_recommendation_item_decrease_quantity);

        }

        void bind(int listIndex){
            RestaurantItem restaurantItem = topRatedItems.get(listIndex);
            topRatedItemAddToCartSwitcher.reset();
            topRatedItemAddToCartSwitcher.setVisibility(View.VISIBLE);

            if(AppController.getInstance().getCartManager().isItemInCart(topRatedItems.get(listIndex))){
                topRatedItemAddToCartSwitcher.setDisplayedChild(1);
                topRatedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(topRatedItems.get(listIndex)).getItemQuantity()));
                /*if(topRatedItemAddToCartSwitcher.getCurrentView()==topRatedItemAddView){
                    topRatedItemAddToCartSwitcher.setDisplayedChild(1);
                    topRatedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(topRatedItems.get(listIndex)).getItemQuantity()));
                }else{
                    topRatedItemAddToCartSwitcher.setDisplayedChild(0);
                }*/

            }else{
                topRatedItemAddToCartSwitcher.setDisplayedChild(0);
                /*if(!(topRatedItemAddToCartSwitcher.getCurrentView()==topRatedItemAddView)){
                    topRatedItemAddToCartSwitcher.setDisplayedChild(0);
                }else{
                    topRatedItemAddToCartSwitcher.setDisplayedChild(1);
                }*/
            }

            addToCartCustomButton = new AddToCartCustomButton(context,restaurantItem,topRatedItemAddToCartSwitcher,topRatedItemAddView,topRatedItemQuantityContainer,topRatedItemQuantity,topRatedItemIncreaseQuantity,topRatedItemDecreaseQuantity);
            addToCartCustomButton.show();

            topRatedItemName.setText(restaurantItem.getName());
            topRatedItemRestaurantName.setText(restaurantItem.getItemRestaurant().getName());

            String priceString = "\u20B9"+String.valueOf(topRatedItems.get(listIndex).getPrice());
            topRatedItemPrice.setText(priceString);

            int rating = (int)topRatedItems.get(listIndex).getRating();
            if(topRatedItems.get(listIndex).getRatingCount()==0){
                topRatedItemRatingView.setVisibility(View.INVISIBLE);
            }else{
                topRatedItemRatingView.setVisibility(View.VISIBLE);
                String ratingImageResource = "star_".concat(String.valueOf(rating));
                switch (rating){
                    case 0:
                        topRatedItemRatingView.setImageResource(R.drawable.star_0);
                        break;
                    case 1:
                        topRatedItemRatingView.setImageResource(R.drawable.star_1);
                        break;
                    case 2:
                        topRatedItemRatingView.setImageResource(R.drawable.star_2);
                        break;
                    case 3:
                        topRatedItemRatingView.setImageResource(R.drawable.star_3);
                        break;
                    case 4:
                        topRatedItemRatingView.setImageResource(R.drawable.star_4);
                        break;
                    case 5:
                        topRatedItemRatingView.setImageResource(R.drawable.star_5);
                        break;
                }
            }

            if(topRatedItems.get(listIndex).isVeg()){
                topRatedItemIsVeg.setImageResource(R.drawable.veg_icon);
            }else{
                topRatedItemIsVeg.setImageResource(R.drawable.nonvegicon);
            }









        }
    }




    /*private class ItemAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            notifyDataSetChanged();
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            notifyDataSetChanged();
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }*/




}
