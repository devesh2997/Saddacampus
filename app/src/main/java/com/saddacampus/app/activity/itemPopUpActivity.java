package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Devesh Anand on 28-06-2017.
 */

public class itemPopUpActivity extends AppCompatActivity {

    //popup Views
    AutofitTextView itemNamePop;
    MaterialFavoriteButton isItemFavouriteView;
    ImageButton closePopupPop;
    TextView itemPricePop;
    RelativeLayout itemRatingContainerPop;
    TextView itemRatingPop;
    TextView itemRatingCountPop;
    TextView itemRatingByUserPop;
    TextView itemOrderCountPop;
    SimpleRatingBar itemRatingBarPop;
    TextView rateItemPop;
    TextView addToFavouritesPop;
    LinearLayout offerContainer;
    TextView itemOffer;
    ImageView itemRestaurantImage;
    TextView itemRestaurantNamePop;
    TextView itemRestaurantMinOrderAmountPop;
    TextView itemRestaurantDelivetyChargePop;
    TextView itemRestaurantStatePop;
    TextView itemTotalPop;
    NumberPicker itemQuantityPickerPop;
    Button addToCartPop;
    Button quickCheckoutPop;
    Fab fab;
    View sheetView;
    TextView favouriteButtonOfFab;
    View overlay;


    MaterialSheetFab materialSheetFab;


    RestaurantItem restaurantItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_item_popup);

        Intent intent = getIntent();
        restaurantItem = (RestaurantItem) intent.getSerializableExtra("restaurant_item");

        itemNamePop = (AutofitTextView) findViewById(R.id.item_name);
        isItemFavouriteView = (MaterialFavoriteButton) findViewById(R.id.favourite_star_view);
        closePopupPop = (ImageButton) findViewById(R.id.close_popup_button);
        itemPricePop = (TextView) findViewById(R.id.item_price);
        itemRatingContainerPop = (RelativeLayout) findViewById(R.id.item_rating_container);
        itemRatingPop = (TextView) findViewById(R.id.item_rating);
        itemRatingCountPop = (TextView) findViewById(R.id.item_rating_count);
        itemRatingByUserPop = (TextView) findViewById(R.id.item_rating_by_user);
        itemOrderCountPop = (TextView) findViewById(R.id.item_order_count);
        itemRatingBarPop = (SimpleRatingBar) findViewById(R.id.item_rating_bar);

        rateItemPop = (TextView) findViewById(R.id.fab_sheet_item_rate_item);
        addToFavouritesPop = (TextView) findViewById(R.id.fab_sheet_item_add_to_favourites);

        offerContainer = (LinearLayout) findViewById(R.id.offer_container);
        itemOffer = (TextView) findViewById(R.id.item_offer);

        itemRestaurantImage = (ImageView) findViewById(R.id.restaurant_image);
        itemRestaurantNamePop = (TextView) findViewById(R.id.restaurant_name);
        itemRestaurantMinOrderAmountPop = (TextView) findViewById(R.id.min_order_amount);
        itemRestaurantDelivetyChargePop = (TextView) findViewById(R.id.delivery_charge);
        itemRestaurantStatePop = (TextView) findViewById(R.id.restaurant_state);

        itemTotalPop = (TextView) findViewById(R.id.item_total);
        itemQuantityPickerPop = (NumberPicker) findViewById(R.id.item_quantity_numberpicker);
        addToCartPop = (Button) findViewById(R.id.add_to_cart_button);
        //quickCheckoutPop = (Button) findViewById(R.id.quick_checkout_button);


        fab = (Fab) findViewById(R.id.fab);
        sheetView = findViewById(R.id.fab_sheet);
        favouriteButtonOfFab = (TextView) sheetView.findViewById(R.id.fab_sheet_item_add_to_favourites);
        overlay = findViewById(R.id.overlay);
        int sheetColor = this.getResources().getColor(R.color.background_card);
        int fabColor = this.getResources().getColor(R.color.colorAccent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        showItemPopup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode==1){

                if(itemRatingContainerPop.getVisibility()==View.INVISIBLE){
                    itemRatingContainerPop.setVisibility(View.VISIBLE);
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
                    itemRatingPop.setText(String.valueOf(newRating));
                    itemRatingByUserPop.setText("Your Rating : ".concat(submittedRating));
                    itemRatingBarPop.setRating(rating);
                }else{
                    float oldRating = restaurantItem.getRating();
                    int ratingCount = restaurantItem.getRatingCount();
                    float newRating = ((oldRating*ratingCount)+rating)/(ratingCount+1);
                    itemRatingCountPop.setText("(".concat(String.valueOf(ratingCount+1).concat(" times)")));
                    itemRatingPop.setText(String.valueOf(newRating));
                    itemRatingByUserPop.setText("Your Rating : ".concat(submittedRating));
                    itemRatingBarPop.setRating(rating);
                }
            }
        }
    }

    public void showItemPopup() {

        String item_price = "Price : Rs.".concat(String.valueOf(restaurantItem.getPrice()));
        String item_rating_count = "(".concat(String.valueOf(restaurantItem.getRatingCount())).concat(" times)");
        String item_order_count = "Ordered ".concat(String.valueOf(restaurantItem.getOrderCount())).concat(" times");
        String item_restaurant_min_order_amount = "Min. Order Amount :".concat(restaurantItem.getItemRestaurant().getMinOrderAmount());
        String item_restaurant_delivery_charge = "Delivery Charge :".concat(String.valueOf(restaurantItem.getItemRestaurant().getDeliveryCharges()));
        String url = Config.URL_GET_IMAGE_ASSETS + "Restaurants/".concat(restaurantItem.getItemRestaurant().getImageResourceId());
        itemRatingBarPop.setRating(restaurantItem.getRating());

        itemNamePop.setText(restaurantItem.getName());
        itemRatingPop.setText(String.valueOf(restaurantItem.getRating()));
        itemRatingCountPop.setText(item_rating_count);

        itemPricePop.setText(item_price);

        if (!restaurantItem.getCurrentRatingByUser().equals("null")) {
            String item_rating_by_user_string = "Your Rating : ".concat(restaurantItem.getCurrentRatingByUser());
            itemRatingByUserPop.setText(item_rating_by_user_string);
        } else {
            String item_rating_by_user_string = "You have not yet rated this item.";
            itemRatingByUserPop.setText(item_rating_by_user_string);
        }


        if (restaurantItem.isFavourite()) {
            isItemFavouriteView.setFavorite(true);
            favouriteButtonOfFab.setText(R.string.fab_item_remove_from_favourites);

        } else {
            isItemFavouriteView.setFavorite(false);

        }

        if (restaurantItem.getRatingCount() == 0) {
            itemRatingContainerPop.setVisibility(View.INVISIBLE);
            String item_rating_by_user_string = "Be the first to rate this item!";
            itemRatingByUserPop.setText(item_rating_by_user_string);
            itemRatingByUserPop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemPopUpActivity.this, RateItemDialogActivity.class);
                    intent.putExtra("item_code", restaurantItem.getProductCode());
                    intent.putExtra("item_name", restaurantItem.getName());
                    startActivityForResult(intent,100);
                }
            });
        }


        itemOrderCountPop.setText(item_order_count);

        if (restaurantItem.getOrderCount() < 1000) {
            itemOrderCountPop.setVisibility(View.INVISIBLE);
        } else {
            itemOrderCountPop.setVisibility(View.INVISIBLE);
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

        Picasso.with(itemPopUpActivity.this).load(url).placeholder(R.drawable.place_holder_color).fit().into(itemRestaurantImage);
        itemRestaurantNamePop.setText(restaurantItem.getItemRestaurant().getName());
        itemRestaurantMinOrderAmountPop.setText(item_restaurant_min_order_amount);
        itemRestaurantDelivetyChargePop.setText(item_restaurant_delivery_charge);
        if (restaurantItem.getItemRestaurant().getStatus().equals("open")) {
            itemRestaurantStatePop.setTextColor(itemPopUpActivity.this.getResources().getColor(R.color.colorGreen));
            itemRestaurantStatePop.setText("OPEN");
        } else {
            itemRestaurantStatePop.setTextColor(itemPopUpActivity.this.getResources().getColor(R.color.colorPinkDark));
            itemRestaurantStatePop.setText("CLOSED");
        }

        itemQuantityPickerPop.setMinValue(1);
        itemQuantityPickerPop.setMaxValue(100);
        itemQuantityPickerPop.setWrapSelectorWheel(false);
        itemQuantityPickerPop.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        updatePopUpTotal(itemQuantityPickerPop.getValue());

        itemQuantityPickerPop.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updatePopUpTotal(newVal);
            }
        });


        closePopupPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.popup_in,R.anim.popup_out);
            }
        });

        rateItemPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSubmitRatingDialog();
                materialSheetFab.hideSheet();
                Intent intent = new Intent(itemPopUpActivity.this, RateItemDialogActivity.class);
                intent.putExtra("item_code", restaurantItem.getProductCode());
                intent.putExtra("item_name", restaurantItem.getName());
                startActivityForResult(intent,100);
            }
        });

        addToFavouritesPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                updateFavouriteStatus(restaurantItem.getProductCode(), AppController.getInstance().getDbManager().getUserUid());

            }
        });

        addToCartPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(restaurantItem, itemQuantityPickerPop.getValue());
                if(AppController.getInstance().getCartManager().addItemToCart(cartItem, itemPopUpActivity.this)){
                    finish();
                    overridePendingTransition(R.anim.popup_in,R.anim.popup_out);
                }

            }
        });

       isItemFavouriteView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               updateFavouriteStatus(restaurantItem.getProductCode(), AppController.getInstance().getDbManager().getUserUid());
           }
       });

        /*quickCheckoutPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });*/

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

    }

    void updatePopUpTotal(int itemQuantity){
        double totalPrice = restaurantItem.getPrice()*itemQuantity;
        String itemTotal = "Total : Rs.".concat(String.valueOf(totalPrice));
        itemTotalPop.setText(itemTotal);

    }

    void updateFavouriteStatus(final String itemCode, final String UID){
        final ProgressDialog progressDialog = new ProgressDialog(itemPopUpActivity.this);
        progressDialog.setMessage("Updating item favourite status...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_UPDATE_FAVOURITE_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(itemPopUpActivity.class.getSimpleName(),response);
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("error")){
                        Toast.makeText(itemPopUpActivity.this,"Unknown Error in submitting Rating.",Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(itemPopUpActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("item_code", itemCode);
                params.put("UID", UID);

                Log.d("ItemPopupWindow",params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.popup_in,R.anim.popup_out);
    }
}
