package com.saddacampus.app.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.activity.RateItemDialogActivity;
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
 * Created by Devesh Anand on 26-06-2017.
 */

public class ItemPopupWindow extends AppCompatActivity {



    private Context context;
    private RestaurantItem restaurantItem;
    private PopupWindow popupWindow;

    //popup Views
    AutofitTextView itemNamePop;
    ImageView isItemFavouriteView;
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

    View popupView;

    MaterialSheetFab materialSheetFab;


    AlertDialog ratingSubmitDialog;
    ViewSwitcher dialogViewSwitcher;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    TextView dialogItemNameView;
    RatingBar dialogItemRating;
    String ratingString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_item_popup);

        Intent intent = getIntent();
        RestaurantItem restaurantItem = (RestaurantItem)intent.getSerializableExtra("restaurant_item");

    }

    public ItemPopupWindow  (final Context context, final RestaurantItem restaurantItem, final PopupWindow popupWindow) {
        this.context = context;
        this.restaurantItem = restaurantItem;
        this.popupWindow = popupWindow;

        popupView = LayoutInflater.from(context).inflate(R.layout.restaurant_item_popup, null);

        itemNamePop = (AutofitTextView)popupView.findViewById(R.id.item_name);
        isItemFavouriteView = (ImageView)popupView.findViewById(R.id.favourite_star_view);
        closePopupPop = (ImageButton)popupView.findViewById(R.id.close_popup_button);
        itemPricePop = (TextView)popupView.findViewById(R.id.item_price);
        itemRatingContainerPop = (RelativeLayout)popupView.findViewById(R.id.item_rating_container);
        itemRatingPop = (TextView)popupView.findViewById(R.id.item_rating);
        itemRatingCountPop = (TextView)popupView.findViewById(R.id.item_rating_count);
        itemRatingByUserPop = (TextView)popupView.findViewById(R.id.item_rating_by_user);
        itemOrderCountPop = (TextView)popupView.findViewById(R.id.item_order_count);
        itemRatingBarPop = (SimpleRatingBar)popupView.findViewById(R.id.item_rating_bar);

        rateItemPop = (TextView)popupView.findViewById(R.id.fab_sheet_item_rate_item);
        addToFavouritesPop = (TextView)popupView.findViewById(R.id.fab_sheet_item_add_to_favourites);

        offerContainer = (LinearLayout)popupView.findViewById(R.id.offer_container);
        itemOffer = (TextView)popupView.findViewById(R.id.item_offer);

        itemRestaurantImage = (ImageView)popupView.findViewById(R.id.restaurant_image);
        itemRestaurantNamePop = (TextView)popupView.findViewById(R.id.restaurant_name);
        itemRestaurantMinOrderAmountPop = (TextView)popupView.findViewById(R.id.min_order_amount) ;
        itemRestaurantDelivetyChargePop = (TextView)popupView.findViewById(R.id.delivery_charge);
        itemRestaurantStatePop = (TextView)popupView.findViewById(R.id.restaurant_state);

        itemTotalPop = (TextView)popupView.findViewById(R.id.item_total);
        itemQuantityPickerPop = (NumberPicker)popupView.findViewById(R.id.item_quantity_numberpicker);
        addToCartPop = (Button)popupView.findViewById(R.id.add_to_cart_button);
        //quickCheckoutPop = (Button)popupView.findViewById(R.id.quick_checkout_button);


        int popupWidth = (int)(context.getResources().getDisplayMetrics().widthPixels );
        int popupHeight = (int)(context.getResources().getDisplayMetrics().heightPixels );

        popupWindow.setContentView(popupView);
        popupWindow.setWidth(popupWidth);
        popupWindow.setHeight(popupHeight);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(5);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);




        fab = (Fab) popupView.findViewById(R.id.fab);
        sheetView = popupView.findViewById(R.id.fab_sheet);
        favouriteButtonOfFab = (TextView)sheetView.findViewById(R.id.fab_sheet_item_add_to_favourites);
        overlay = popupView.findViewById(R.id.overlay);
        int sheetColor = context.getResources().getColor(R.color.background_card);
        int fabColor = context.getResources().getColor(R.color.colorAccent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);





    }


    public void showItemPopup(){

        String item_price = "Price : Rs.".concat(String.valueOf(restaurantItem.getPrice()));
        String item_rating_count = "(".concat(String.valueOf(restaurantItem.getRatingCount())).concat(" times)");
        String item_order_count = "Ordered ".concat(String.valueOf(restaurantItem.getOrderCount())).concat(" times");
        String item_restaurant_min_order_amount = "Min. Order Amount :".concat(restaurantItem.getItemRestaurant().getMinOrderAmount());
        String item_restaurant_delivery_charge = "Delivery Charge :".concat(String.valueOf(restaurantItem.getItemRestaurant().getDeliveryCharges()));
        String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(restaurantItem.getItemRestaurant().getImageResourceId());
        itemRatingBarPop.setRating(restaurantItem.getRating());

        itemNamePop.setText(restaurantItem.getName());
        itemRatingPop.setText(String.valueOf(restaurantItem.getRating()));
        itemRatingCountPop.setText(item_rating_count);

        itemPricePop.setText(item_price);

        if(!restaurantItem.getCurrentRatingByUser().equals("null")){
            String item_rating_by_user_string = "Your Rating : ".concat(restaurantItem.getCurrentRatingByUser());
            itemRatingByUserPop.setText(item_rating_by_user_string);
        }else{
            String item_rating_by_user_string = "You have not yet rated this item.";
            itemRatingByUserPop.setText(item_rating_by_user_string);
        }


        if(restaurantItem.isFavourite()){
            favouriteButtonOfFab.setText(R.string.fab_item_remove_from_favourites);

        }else{
            isItemFavouriteView.setVisibility(View.INVISIBLE);

        }

        if(restaurantItem.getRatingCount()==0){
            itemRatingContainerPop.setVisibility(View.INVISIBLE);
            String item_rating_by_user_string = "Be the first to rate this item!";
            itemRatingByUserPop.setText(item_rating_by_user_string);
        }


        itemOrderCountPop.setText(item_order_count);

        if(restaurantItem.getOrderCount()<1000){
            itemOrderCountPop.setVisibility(View.INVISIBLE);
        }else{
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
            itemOffer.setText(restaurantItem.getItemRestaurant().getNewOffer());
        }

        Picasso.with(context).load(url).placeholder(R.drawable.placeholder).fit().into(itemRestaurantImage);
        itemRestaurantNamePop.setText(restaurantItem.getItemRestaurant().getName());
        itemRestaurantMinOrderAmountPop.setText(item_restaurant_min_order_amount);
        itemRestaurantDelivetyChargePop.setText(item_restaurant_delivery_charge);
        if(restaurantItem.getItemRestaurant().getStatus().equals("open")){
            itemRestaurantStatePop.setTextColor(context.getResources().getColor(R.color.colorGreen));
            itemRestaurantStatePop.setText("OPEN NOW");
        }else{
            itemRestaurantStatePop.setTextColor(context.getResources().getColor(R.color.colorPinkDark));
            itemRestaurantStatePop.setText("CLOSED NOW");
        }

        itemQuantityPickerPop.setMinValue(1);
        itemQuantityPickerPop.setMaxValue(100);
        itemQuantityPickerPop.setWrapSelectorWheel(true);
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
                popupWindow.dismiss();


            }
        });

        rateItemPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSubmitRatingDialog();
                Intent intent = new Intent(context,RateItemDialogActivity.class);
                intent.putExtra("item_code",restaurantItem.getProductCode());
                intent.putExtra("item_name",restaurantItem.getName());
            }
        });

        addToFavouritesPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    updateFavouriteStatus(restaurantItem.getProductCode(),AppController.getInstance().getDbManager().getUserUid());

            }
        });

        addToCartPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(restaurantItem,itemQuantityPickerPop.getValue());
                AppController.getInstance().getCartManager().addItemToCart(cartItem, context);
                popupWindow.dismiss();
            }
        });

        quickCheckoutPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
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


        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0,0);
    }


    void updatePopUpTotal(int itemQuantity){
        double totalPrice = restaurantItem.getPrice()*itemQuantity;
        String itemTotal = "Total : Rs.".concat(String.valueOf(totalPrice));
        itemTotalPop.setText(itemTotal);

    }

    void updateFavouriteStatus(final String itemCode, final String UID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_SET_ITEM_RATING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                params.put("uid", UID);

                Log.d("ItemPopupWindow",params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }



}
