package com.saddacampus.app.app.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.saddacampus.app.app.Animation.AnimateViewSwitcherFlip;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.helper.CartManager;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 10-07-2017.
 */

public class MostOrderedItemsAdapter extends RecyclerView.Adapter<MostOrderedItemsAdapter.MostOrderedItemsViewHolder>{

    private static String TAG = TopRatedItemsAdapter.class.getSimpleName();

    private Context context;

    private ArrayList<RestaurantItem> mostOrderedItems;

    public MostOrderedItemsAdapter(Context context, ArrayList<RestaurantItem> mostOrderedItems) {
        this.context = context;
        this.mostOrderedItems = mostOrderedItems;
    }


    @Override
    public MostOrderedItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutForFoundItemsInARestaurant = R.layout.most_ordered_recommendation_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFoundItemsInARestaurant,parent,shouldAttachToParentImmediately);

        MostOrderedItemsViewHolder mostOrderedItemsViewHolder = new MostOrderedItemsViewHolder(view);

        return mostOrderedItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(MostOrderedItemsViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mostOrderedItems.size();
    }

    public class MostOrderedItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mostOrderedItemName;
        TextView mostOrderedItemRestaurantName;
        TextView mostOrderedItemPrice;
        ImageView mostOrderedItemIsVeg;
        ImageView mostOrderedItemRatingView;
        ViewSwitcher mostOrderedItemAddToCartSwitcher;
        RelativeLayout mostOrderedItemAddView;
        RelativeLayout mostOrderedItemQuantityContainer;
        TextView mostOrderedItemQuantity;
        TextView mostOrderedItemIncreaseQuantity;
        TextView mostOrderedItemDecreaseQuantity;


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemInfoActivity.class);
            intent.putExtra("restaurant_item",mostOrderedItems.get(getAdapterPosition()));
            context.startActivity(intent);
        }

        public MostOrderedItemsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mostOrderedItemName = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_name);
            mostOrderedItemRestaurantName = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_restaurant_name);
            mostOrderedItemPrice = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_price);
            mostOrderedItemIsVeg = (ImageView)itemView.findViewById(R.id.most_ordered_recommendation_item_is_veg);
            mostOrderedItemRatingView = (ImageView)itemView.findViewById(R.id.most_ordered_recommendation_item_rating);
            mostOrderedItemAddToCartSwitcher = (ViewSwitcher)itemView.findViewById(R.id.most_ordered_item_add_to_cart_switcher);
            mostOrderedItemAddView = (RelativeLayout)itemView.findViewById(R.id.most_ordered_recommendation_item_add);
            mostOrderedItemQuantityContainer = (RelativeLayout)itemView.findViewById(R.id.most_ordered_item_quantity_container);
            mostOrderedItemQuantity = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_quantity);
            mostOrderedItemIncreaseQuantity = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_increase_quantity);
            mostOrderedItemDecreaseQuantity = (TextView)itemView.findViewById(R.id.most_ordered_recommendation_item_decrease_quantity);

            
        }

        void bind(int listIndex){
            final RestaurantItem restaurantItem = mostOrderedItems.get(listIndex);
            mostOrderedItemName.setText(mostOrderedItems.get(listIndex).getName());
            mostOrderedItemRestaurantName.setText(mostOrderedItems.get(listIndex).getItemRestaurant().getName());

            String priceString = "\u20B9"+String.valueOf(mostOrderedItems.get(listIndex).getPrice());
            mostOrderedItemPrice.setText(priceString);

            int rating = (int)mostOrderedItems.get(listIndex).getRating();
            if(mostOrderedItems.get(listIndex).getRatingCount()==0){
                mostOrderedItemRatingView.setVisibility(View.INVISIBLE);
            }else{
                mostOrderedItemRatingView.setVisibility(View.VISIBLE);
                String ratingImageResource = "star_".concat(String.valueOf(rating));
                switch (rating){
                    case 0:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_0);
                        break;
                    case 1:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_1);
                        break;
                    case 2:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_2);
                        break;
                    case 3:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_3);
                        break;
                    case 4:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_4);
                        break;
                    case 5:
                        mostOrderedItemRatingView.setImageResource(R.drawable.star_5);
                        break;
                }
            }

            if(mostOrderedItems.get(listIndex).isVeg()){
                mostOrderedItemIsVeg.setImageResource(R.drawable.veg_icon);
            }else{
                mostOrderedItemIsVeg.setImageResource(R.drawable.nonvegicon);
            }

            if(AppController.getInstance().getCartManager().isItemInCart(restaurantItem)){
                if(mostOrderedItemAddToCartSwitcher.getCurrentView()==mostOrderedItemAddView){
                    mostOrderedItemAddToCartSwitcher.setDisplayedChild(1);
                }
                mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
            }else{
                if(!(mostOrderedItemAddToCartSwitcher.getCurrentView()==mostOrderedItemAddView)){
                    mostOrderedItemAddToCartSwitcher.setDisplayedChild(0);
                }
            }

            mostOrderedItemAddView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AnimateViewSwitcherFlip animateViewSwitcherFlip = new AnimateViewSwitcherFlip(mostOrderedItemAddToCartSwitcher);
                    final CartManager cartManager = AppController.getInstance().getCartManager();
                    final Cart currentCart = AppController.getInstance().getCartManager().getCart();
                    if(cartManager.getCart().isCartEmpty()){
                        cartManager.addItemToCart(new CartItem(restaurantItem,1),context);
                        animateViewSwitcherFlip.animateFlip();
                        mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                    }
                    else if(cartManager.getCurrentRestaurantInCart().getCode().equals(restaurantItem.getItemRestaurant().getCode())){
                        cartManager.addItemToCart(new CartItem(restaurantItem,1),context);
                        animateViewSwitcherFlip.animateFlip();
                        mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                    }else{
                        Log.d(TAG,"Only one restaurant is allowed at once.");
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.different_restaurant_alert_dialog_message).setTitle(R.string.different_restaurant_alert_dialog_title);
                        builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentCart.clearCart();
                                currentCart.addItem(new CartItem(restaurantItem,1));
                                cartManager.updateCartState(currentCart);
                                cartManager.broadcastItemAddedToCartMessage(new CartItem(restaurantItem,1));
                                animateViewSwitcherFlip.animateFlip();
                                mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                                Log.d(TAG,"Item added to cart");
                                //notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG,"User Cancelled The adding of item to cart.");

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

            mostOrderedItemIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartItem cartItem = new CartItem(restaurantItem,Integer.parseInt(mostOrderedItemQuantity.getText().toString())+1);
                    if(AppController.getInstance().getCartManager().addItemToCart(cartItem,context)){
                        mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                    }
                }
            });

            mostOrderedItemDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getInstance().getCartManager().decreaseItemQuantity(restaurantItem);
                    if(AppController.getInstance().getCartManager().isItemInCart(restaurantItem)){
                        mostOrderedItemQuantity.setText(String.valueOf(AppController.getInstance().getCartManager().getCartItemForRestauratnItem(restaurantItem).getItemQuantity()));
                    }else{
                        AnimateViewSwitcherFlip animateViewSwitcherFlip = new AnimateViewSwitcherFlip(mostOrderedItemAddToCartSwitcher);
                        animateViewSwitcherFlip.animateFlip();
                    }

                }
            });
        }
    }
}
