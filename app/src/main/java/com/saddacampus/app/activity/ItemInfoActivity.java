package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.app.Fab;
import com.saddacampus.app.helper.RecommendationManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 10-07-2017.
 */

public class ItemInfoActivity extends AppCompatActivity {

    private static String TAG = ItemInfoActivity.class.getSimpleName();

    CoordinatorLayout coordinatorLayout;
    TextView itemName;
    MaterialFavoriteButton isItemFavouriteView;
    RelativeLayout cartIconContainer;
    TextView cartBadge;
    TextView itemPrice;
    RelativeLayout itemRatingContainer;
    TextView itemRating;
    TextView itemRatingCount;
    TextView itemRatingByUser;
    TextView itemOrderCount;
    SimpleRatingBar itemRatingBar;
    TextView rateItem;
    TextView addToFavourites;
    LinearLayout offerContainer;
    TextView itemOffer;
    RelativeLayout restaurantLink;
    ImageView itemRestaurantImage;
    TextView itemRestaurantName;
    TextView itemRestaurantMinOrderAmount;
    TextView itemRestaurantDelivetyCharge;
    TextView itemRestaurantState;
    TextView itemTotalView;
    NumberPicker itemQuantityPicker;
    Button addToCart;
    Button quickCheckout;
    RecyclerView topRatedItemsRecycler;
    RecyclerView mostOrderedItemsRecycler;
    
    
    Fab fab;
    View sheetView;
    TextView favouriteButtonOfFab;
    View overlay;


    MaterialSheetFab materialSheetFab;


    RestaurantItem restaurantItem;

    ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        Intent intent = getIntent();
        restaurantItem = (RestaurantItem) intent.getSerializableExtra("restaurant_item");

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.item_info_container);
        itemName = (TextView) findViewById(R.id.item_name);
        isItemFavouriteView = (MaterialFavoriteButton) findViewById(R.id.favourite_star_view);
        cartIconContainer = (RelativeLayout)findViewById(R.id.cart_icon_container);
        cartBadge = (TextView)findViewById(R.id.item_info_activity_cart_count_badge);
        itemPrice = (TextView) findViewById(R.id.item_price);
        itemRatingContainer = (RelativeLayout) findViewById(R.id.item_rating_container);
        itemRating = (TextView) findViewById(R.id.item_rating);
        itemRatingCount = (TextView) findViewById(R.id.item_rating_count);
        itemRatingByUser = (TextView) findViewById(R.id.item_rating_by_user);
        itemOrderCount = (TextView) findViewById(R.id.item_order_count);
        itemRatingBar = (SimpleRatingBar) findViewById(R.id.item_rating_bar);

        rateItem = (TextView) findViewById(R.id.fab_sheet_item_rate_item);
        addToFavourites = (TextView) findViewById(R.id.fab_sheet_item_add_to_favourites);

        offerContainer = (LinearLayout) findViewById(R.id.offer_container);
        itemOffer = (TextView) findViewById(R.id.item_offer);

        restaurantLink = (RelativeLayout)findViewById(R.id.restaurant_info_and_link);

        itemRestaurantImage = (ImageView) findViewById(R.id.restaurant_image);
        itemRestaurantName = (TextView) findViewById(R.id.restaurant_name);
        itemRestaurantMinOrderAmount = (TextView) findViewById(R.id.min_order_amount);
        itemRestaurantDelivetyCharge = (TextView) findViewById(R.id.delivery_charge);
        itemRestaurantState = (TextView) findViewById(R.id.restaurant_state);

        itemTotalView = (TextView) findViewById(R.id.item_total);
        itemQuantityPicker = (NumberPicker) findViewById(R.id.item_quantity_numberpicker);
        addToCart = (Button) findViewById(R.id.add_to_cart_button);
        //quickCheckout = (Button) findViewById(R.id.quick_checkout_button);
        
        topRatedItemsRecycler = (RecyclerView)findViewById(R.id.top_rated_items_recycler);
        mostOrderedItemsRecycler = (RecyclerView)findViewById(R.id.most_ordered_items_recycler); 


        fab = (Fab) findViewById(R.id.fab);
        sheetView = findViewById(R.id.fab_sheet);
        favouriteButtonOfFab = (TextView) sheetView.findViewById(R.id.fab_sheet_item_add_to_favourites);
        overlay = findViewById(R.id.overlay);
        int sheetColor = this.getResources().getColor(R.color.background_card);
        int fabColor = this.getResources().getColor(R.color.colorAccent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        cartBadge.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cartIconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(ItemInfoActivity.this);
            }
        });



        populateViews();

    }


    @Override
    protected void onStart() {
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);
        super.onStart();
    }

    @Override
    protected void onResume() {
        cartBadge.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(itemAddedReceiver);
        unregisterReceiver(itemDeletedReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode==1){

                if(itemRatingContainer.getVisibility()==View.INVISIBLE){
                    itemRatingContainer.setVisibility(View.VISIBLE);
                }

                if(restaurantItem.getRatingCount()==0){

                }

                String submittedRating = data.getStringExtra("submitted_rating");
                int rating = Integer.parseInt(submittedRating);
                if(!restaurantItem.getCurrentRatingByUser().equals("null")){
                    float oldRating = restaurantItem.getRating();
                    int oldRatingByUser = Integer.parseInt(restaurantItem.getCurrentRatingByUser());
                    int ratingCount = restaurantItem.getRatingCount();
                    float newRating = (((oldRating*ratingCount)-oldRatingByUser)+rating)/ratingCount;
                    itemRating.setText(String.valueOf(newRating));
                    itemRatingByUser.setText("Your Rating : ".concat(submittedRating));
                    itemRatingBar.setRating(rating);
                }else{
                    float oldRating = restaurantItem.getRating();
                    int ratingCount = restaurantItem.getRatingCount();
                    float newRating = ((oldRating*ratingCount)+rating)/(ratingCount+1);
                    itemRatingCount.setText("(".concat(String.valueOf(ratingCount+1).concat(" times)")));
                    itemRating.setText(String.valueOf(newRating));
                    itemRatingByUser.setText("Your Rating : ".concat(submittedRating));
                    itemRatingBar.setRating(rating);
                }
            }
        }
    }

    private void populateViews() {

        String item_price = "Price : Rs.".concat(String.valueOf(restaurantItem.getPrice()));
        String item_rating_count = "(".concat(String.valueOf(restaurantItem.getRatingCount())).concat(" times)");
        String item_order_count = "Ordered ".concat(String.valueOf(restaurantItem.getOrderCount())).concat(" times");
        String item_restaurant_min_order_amount = "Min. SaddaOrder Amount :".concat(restaurantItem.getItemRestaurant().getMinOrderAmount());
        String item_restaurant_delivery_charge = "Delivery Charge :".concat(String.valueOf(restaurantItem.getItemRestaurant().getDeliveryCharges()));
        String url = Config.URL_GET_IMAGE_ASSETS + "Restaurants/".concat(restaurantItem.getItemRestaurant().getImageResourceId());
        itemRatingBar.setRating(restaurantItem.getRating());

        itemName.setText(restaurantItem.getName());
        itemRating.setText(String.valueOf(restaurantItem.getRating()));
        itemRatingCount.setText(item_rating_count);

        itemPrice.setText(item_price);

        if (!restaurantItem.getCurrentRatingByUser().equals("null")) {
            String item_rating_by_user_string = "Your Rating : ".concat(restaurantItem.getCurrentRatingByUser());
            itemRatingByUser.setText(item_rating_by_user_string);
        } else {
            String item_rating_by_user_string = "You have not yet rated this item.";
            itemRatingByUser.setText(item_rating_by_user_string);
        }


        if (restaurantItem.isFavourite()) {
            isItemFavouriteView.setFavorite(true);
            favouriteButtonOfFab.setText(R.string.fab_item_remove_from_favourites);

        } else {
            isItemFavouriteView.setFavorite(false);

        }

        if (restaurantItem.getRatingCount() == 0) {
            itemRatingContainer.setVisibility(View.INVISIBLE);
            String item_rating_by_user_string = "Be the first to rate this item!";
            itemRatingByUser.setText(item_rating_by_user_string);
            itemRatingByUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemInfoActivity.this, RateItemDialogActivity.class);
                    intent.putExtra("item_code", restaurantItem.getProductCode());
                    intent.putExtra("item_name", restaurantItem.getName());
                    startActivityForResult(intent,100);
                }
            });
        }


        itemOrderCount.setText(item_order_count);

        if (restaurantItem.getOrderCount() < 1000) {
            itemOrderCount.setVisibility(View.INVISIBLE);
        } else {
            itemOrderCount.setVisibility(View.INVISIBLE);
        }

        if (restaurantItem.getItemRestaurant().getHasNewOffer()==0) {
            if(restaurantItem.getItemRestaurant().getOffer()==0){
                offerContainer.setVisibility(View.INVISIBLE);
            }else{
                offerContainer.setVisibility(View.VISIBLE);
                itemOffer.setText(String.valueOf(restaurantItem.getItemRestaurant().getOffer()));
            }

        } else {
            offerContainer.setVisibility(View.VISIBLE);
            itemOffer.setText(String.valueOf(restaurantItem.getItemRestaurant().getNewOffer()));
        }

        restaurantLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),RestaurantMenu.class);
                intent.putExtra("restaurant",restaurantItem.getItemRestaurant());
                v.getContext().startActivity(intent);
            }
        });

        Picasso.with(ItemInfoActivity.this).load(url).placeholder(R.drawable.place_holder_color).fit().into(itemRestaurantImage);
        itemRestaurantName.setText(restaurantItem.getItemRestaurant().getName());
        itemRestaurantMinOrderAmount.setText(item_restaurant_min_order_amount);
        itemRestaurantDelivetyCharge.setText(item_restaurant_delivery_charge);
        if (restaurantItem.getItemRestaurant().getStatus().equals("open")) {
            itemRestaurantState.setTextColor(ItemInfoActivity.this.getResources().getColor(R.color.colorGreen));
            itemRestaurantState.setText("OPEN NOW");
        } else {
            itemRestaurantState.setTextColor(ItemInfoActivity.this.getResources().getColor(R.color.colorPinkDark));
            itemRestaurantState.setText("CLOSED NOW");
        }

        itemQuantityPicker.setMinValue(1);
        itemQuantityPicker.setMaxValue(100);
        itemQuantityPicker.setWrapSelectorWheel(false);
        itemQuantityPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        updateUpTotal(itemQuantityPicker.getValue());

        itemQuantityPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateUpTotal(newVal);
            }
        });




        rateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSubmitRatingDialog();
                materialSheetFab.hideSheet();
                Intent intent = new Intent(ItemInfoActivity.this, RateItemDialogActivity.class);
                intent.putExtra("item_code", restaurantItem.getProductCode());
                intent.putExtra("item_name", restaurantItem.getName());
                startActivityForResult(intent,100);
            }
        });

        addToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                updateFavouriteStatus(restaurantItem.getProductCode(), AppController.getInstance().getDbManager().getUserUid());

            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(restaurantItem, itemQuantityPicker.getValue());
                AppController.getInstance().getCartManager().addItemToCart(cartItem, ItemInfoActivity.this);

            }
        });

        isItemFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFavouriteStatus(restaurantItem.getProductCode(), AppController.getInstance().getDbManager().getUserUid());
            }
        });




        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                // Set darker status bar color to match the dim overlay
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
            }
        });

        RecommendationManager recommendationManager = new RecommendationManager(ItemInfoActivity.this,topRatedItemsRecycler,mostOrderedItemsRecycler,null,restaurantItem.getProductCode(),Config.KEY_GET_RECOMMENDATION_FOR_ITEM);
        recommendationManager.getRecommendations();

    }

    void updateUpTotal(int itemQuantity){
        double totalPrice = restaurantItem.getPrice()*itemQuantity;
        String itemTotal = "Total : Rs.".concat(String.valueOf(totalPrice));
        itemTotalView.setText(itemTotal);

    }

    void updateFavouriteStatus(final String itemCode, final String UID){
        final ProgressDialog progressDialog = new ProgressDialog(ItemInfoActivity.this);
        progressDialog.setMessage("Updating item favourite status...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_UPDATE_FAVOURITE_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(ItemInfoActivity.class.getSimpleName(),response);
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("error")){
                        Toast.makeText(ItemInfoActivity.this,"Unknown Error in submitting Rating.",Toast.LENGTH_SHORT).show();
                    }else{
                        if(isItemFavouriteView.isFavorite()){
                            isItemFavouriteView.setFavorite(false);
                            favouriteButtonOfFab.setText(R.string.fab_item_add_to_favourites);

                        }else{
                            isItemFavouriteView.setFavorite(true);
                            favouriteButtonOfFab.setText(R.string.fab_item_remove_from_favourites);
                        }

                    }
                }catch (JSONException e){
                    Log.d("RateItemDialogFragment",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Intent intent = new Intent(ItemInfoActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("item_code", itemCode);
                params.put("UID", UID);

                Log.d("ItemupWindow",params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    //Broadcast receivers.
    private class ItemAddedReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context arg0, Intent arg1) {
            cartBadge.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            final CartItem cartItem = (CartItem)arg1.getSerializableExtra("cart_item");
            String cartItemName = cartItem.getRestaurantItem().getName();
            if(cartItemName.length()>20){
                cartItemName = cartItemName.substring(0,20);
                cartItemName = cartItemName.concat("...");
            }
            String cartItemQuantity = "(".concat(String.valueOf(cartItem.getItemQuantity()).concat(")"));
            String snackBarText = "Added : ".concat(cartItemName).concat(cartItemQuantity);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, snackBarText, Snackbar.LENGTH_LONG)
                    .setAction("Remove", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppController.getInstance().getCartManager().deleteItemFromCart(cartItem);
                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Item removed from Cart.", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            cartBadge.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Item Deleted From Cart.", Snackbar.LENGTH_LONG);

            snackbar.show();
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class RatingSubmittedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Rating Submitted Successfully", Snackbar.LENGTH_LONG);

            snackbar.show();
            Log.d(TAG,"Rating submitted successfully broadcast received");
        }
    }
}
