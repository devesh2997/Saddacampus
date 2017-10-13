package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AddToCartCustomButton;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.RestaurantItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shubham Vishwakarma on 04-07-2017.
 */

public class FavouriteItemListAdapter extends RecyclerView.Adapter<FavouriteItemListAdapter.FavouriteItemViewHolder> {

    private static final String TAG = RestaurantListAdapter.class.getSimpleName();

    private ArrayList<RestaurantItem> favouriteItemsArrayList ;

    private Context context;

    public FavouriteItemListAdapter(ArrayList<RestaurantItem> favouriteItemsArrayList) {
        this.favouriteItemsArrayList = favouriteItemsArrayList;
    }

    @Override
    public FavouriteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForFavouriteItem = R.layout.favourite_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForFavouriteItem, parent, shouldAttachToParentImmediately);
        FavouriteItemViewHolder favouriteItemViewHolder = new FavouriteItemViewHolder(view);

        return favouriteItemViewHolder;
    }

    @Override
    public void onBindViewHolder(FavouriteItemViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return favouriteItemsArrayList.size();
    }

    public void removeItem(int position){

        updateFavouriteStatus(favouriteItemsArrayList.get(position).getProductCode(),AppController.getInstance().getDbManager().getUserUid());
        favouriteItemsArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }

    public class FavouriteItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView restaurantItemNameView;
        TextView restaurantNameView;
        TextView price;
        ViewSwitcher restaurantItemAddToCartSwitcher;
        RelativeLayout restaurantItemAddView;
        RelativeLayout restaurantItemQuantityContainer;
        TextView restaurantItemQuantity;
        TextView restaurantItemIncreaseQuantity;
        TextView restaurantItemDecreaseQuantity;
        ImageView isVeg;
        AddToCartCustomButton addToCartCustomButton;
        LinearLayout restaurantItemContainer;
        RestaurantItem favouriteItem;
        LinearLayout itemRatingContainer;
        SimpleRatingBar itemRating;
        TextView ratingCount;



        @Override
        public void onClick(View v) {

        }

        public FavouriteItemViewHolder(View favouriteItemView){
            super(favouriteItemView);
            restaurantItemNameView = (TextView) favouriteItemView.findViewById(R.id.restaurant_item_name_view);
            restaurantNameView = (TextView)favouriteItemView.findViewById(R.id.restaurant_name_view);
            price = (TextView) favouriteItemView.findViewById(R.id.price);
            isVeg = (ImageView)favouriteItemView.findViewById(R.id.veg);
            restaurantItemAddToCartSwitcher = (ViewSwitcher)itemView.findViewById(R.id.favourite_restaurant_item_add_to_cart_switcher);
            restaurantItemAddView = (RelativeLayout)itemView.findViewById(R.id.favourite_restaurant_item_add);
            restaurantItemQuantityContainer = (RelativeLayout)itemView.findViewById(R.id.favourite_restaurant_item_quantity_container);
            restaurantItemQuantity = (TextView)itemView.findViewById(R.id.favourite_restaurant_item_quantity);
            restaurantItemIncreaseQuantity = (TextView)itemView.findViewById(R.id.favourite_restaurant_item_increase_quantity);
            restaurantItemDecreaseQuantity = (TextView)itemView.findViewById(R.id.favourite_restaurant_item_decrease_quantity);
            itemRatingContainer = (LinearLayout)favouriteItemView.findViewById(R.id.item_rating_container);
            itemRating = (SimpleRatingBar) favouriteItemView.findViewById(R.id.item_rating);
            ratingCount = (TextView)favouriteItemView.findViewById(R.id.item_rating_count);


        }

        void bind (int listIndex){
            Log.d(TAG,"binding");

            favouriteItem = favouriteItemsArrayList.get(listIndex);

            addToCartCustomButton = new AddToCartCustomButton(context,favouriteItem,restaurantItemAddToCartSwitcher,restaurantItemAddView,restaurantItemQuantityContainer,restaurantItemQuantity,restaurantItemIncreaseQuantity,restaurantItemDecreaseQuantity);
            addToCartCustomButton.show();

            restaurantItemNameView.setText(favouriteItem.getName());


            restaurantItemNameView.setTypeface(AppController.getInstance().getJunctionRegular());
            restaurantNameView.setText(favouriteItem.getItemRestaurant().getName());
            restaurantNameView.setTypeface(AppController.getInstance().getJunctionLight());
            String priceString = "Rs. "+String.valueOf(favouriteItem.getPrice());
            price.setTypeface(AppController.getInstance().getJunctionBold());
            price.setText(priceString);

            int rating = (int)favouriteItem.getRating();
            if(favouriteItem.getRatingCount()==0){
                itemRatingContainer.setVisibility(View.INVISIBLE);
            }else{
                itemRating.setRating(favouriteItem.getRating());
                ratingCount.setText("("+String.valueOf(favouriteItem.getRatingCount())+")");
            }


            if(favouriteItem.isVeg()){
                isVeg.setImageResource(R.drawable.veg_icon);
            }else{
                isVeg.setImageResource(R.drawable.nonvegicon);
            }

        }





    }

    void updateFavouriteStatus(final String itemCode, final String UID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_UPDATE_FAVOURITE_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("error")){
                        Toast.makeText(context,"Unknown Error in submitting Rating.",Toast.LENGTH_SHORT).show();
                    }else{


                    }
                }catch (JSONException e){
                    Log.d("RateItemDialogFragment",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("item_code", itemCode);
                params.put("UID", UID);

                Log.d(TAG,params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
