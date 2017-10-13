package com.saddacampus.app.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.CartItemsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Devesh Anand on 09-07-2017.
 */

public class CartActivity extends AppCompatActivity {

    private static final String TAG = CartActivity.class.getSimpleName();

    LinkedList<CartItem> cartItems;
    Cart cart;

    ImageView restaurantInCartImageView;
    TextView restaurantInCartNameView;
    TextView restaurantInCartAddress;
    RecyclerView cartItemsRecycler;

    TextView cartRestaurantDiscount;

    TextView billTotalView;
    TextView discountByRestaurantView;
    ProgressBar discountLoading;
    ProgressBar deliveryChargeLoading;
    TextView deliveryCharge;
    TextView payableAmountView;
    LinearLayout emptyLayout;
    Button checkoutButton;
    RelativeLayout cartContainer;

    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_cart);

        cartItems = AppController.getInstance().getCartManager().getItemsInCart();
        cart = AppController.getInstance().getCartManager().getCart();

        restaurantInCartImageView = (ImageView)findViewById(R.id.cart_restaurant_image_view);
        restaurantInCartNameView = (TextView)findViewById(R.id.cart_restaurant_name);
        restaurantInCartAddress = (TextView)findViewById(R.id.cart_restaurant_address);
        cartItemsRecycler = (RecyclerView)findViewById(R.id.cart_items_recycler);
        billTotalView = (TextView)findViewById(R.id.bill_total_view);
        discountByRestaurantView = (TextView)findViewById(R.id.discount_applied);
        discountLoading = (ProgressBar)findViewById(R.id.discount_loading);
        deliveryChargeLoading = (ProgressBar)findViewById(R.id.delivery_charge_loading);
        deliveryCharge = (TextView)findViewById(R.id.delivery_charge);
        payableAmountView = (TextView)findViewById(R.id.amount_to_pay);
        cartRestaurantDiscount = (TextView)findViewById(R.id.cart_restaurant_discount);

        checkoutButton = (Button) findViewById(R.id.checkout_button);
        emptyLayout = (LinearLayout)findViewById(R.id.empty_cart_layout);
        cartContainer = (RelativeLayout)findViewById(R.id.cart_container);
        emptyLayout.setVisibility(View.GONE);

        populateViews();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        populateViews();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateRestaurantInfo(){
        checkoutButton.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RESTAURANT_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);

                try{
                    JSONObject responseObject = new JSONObject(response);
                    if(!responseObject.getBoolean("error")){
                        JSONObject restaurantObject = responseObject.getJSONObject("result");

                        String name = restaurantObject.getString("name");
                        String contact = restaurantObject.getString("contact");

                        String address = restaurantObject.getString("address");
                        String status = (restaurantObject.getString("open").equals("1")) ? "open" : "close";
                        String minOrderAmount = restaurantObject.getString("minOrderAmount");
                        String timing = restaurantObject.getString("timing");
                        float deliveryCharges = Float.parseFloat(restaurantObject.getString("delivery_charges"));
                        float deliveryChargeSlab = Float.parseFloat(restaurantObject.getString("delivery_charge_slab"));
                        boolean acceptsOnlinePayment = restaurantObject.getInt("accepts_online_payment")==1;
                        String code = restaurantObject.getString("code");
                        int hasNewOffer = restaurantObject.getInt("has_new_offer");
                        int newOffer = restaurantObject.getInt("new_offer");
                        int offer = restaurantObject.getInt("offer");
                        String imageResourceId = restaurantObject.getString("imageResourceId");
                        String speciality = restaurantObject.getString("speciality");
                        float rating = (float)restaurantObject.getDouble("rating");
                        int messageStatus = restaurantObject.getInt("message_status");
                        String message;
                        if(messageStatus!=0){
                            message = restaurantObject.getString("message");
                        }else{
                            message = "";
                        }
                        Restaurant restaurant = new Restaurant(name,contact, acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

                        AppController.getInstance().getCartManager().setCurrentRestaurantInCart(restaurant);
                        updateCartViews();

                    }
                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Intent intent = new Intent(CartActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());
                params.put("restaurant_code", AppController.getInstance().getCartManager().getCurrentRestaurantInCart().getCode());

                return params;
            }
        };

        AppController.getInstance().getRequestQueue().add(stringRequest);

    }

    private void populateViews(){

        if(AppController.getInstance().getCartManager().getCart().isCartEmpty()){

            cartContainer.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

        }else{
            String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(cart.getRestaurantInCart().getImageResourceId());
            Picasso.with(CartActivity.this).load(url).fit().placeholder(R.drawable.place_holder_color).into(restaurantInCartImageView);
            restaurantInCartNameView.setText(cart.getRestaurantInCart().getName());
            restaurantInCartAddress.setText(cart.getRestaurantInCart().getAddress());

            updateRestaurantInfo();

            final CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(cartItems);
            LinearLayoutManager cartItemsLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

            cartItemsRecycler.setLayoutManager(cartItemsLayoutManager);
            cartItemsRecycler.setAdapter(cartItemsAdapter);


        }

    }

    public void updateCartViews(){
        showSnackBar();

        if(AppController.getInstance().getCartManager().getCart().isCartEmpty()){

            cartContainer.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

        }else{
            Cart cart = AppController.getInstance().getCartManager().getCart();
            deliveryChargeLoading.setVisibility(View.GONE);
            discountLoading.setVisibility(View.GONE);
            float payableAmount = (float)cart.getTotalAfterDiscount();
            if(cart.getRestaurantInCart().getDeliveryChargeSlab()> cart.getTotalBeforeDiscount()){
                deliveryCharge.setText(String.valueOf(cart.getRestaurantInCart().getDeliveryCharges()));
                payableAmount = payableAmount + cart.getRestaurantInCart().getDeliveryCharges();
            }else{
                deliveryCharge.setText("0");
            }

            billTotalView.setText(String.valueOf(AppController.getInstance().getCartManager().getCart().getTotalBeforeDiscount()));
            cartRestaurantDiscount.setText("Restaurant Discount ".concat("(").concat(String.valueOf(AppController.getInstance().getCartManager().getCart().getDiscount())).concat("%)"));
            double discountApplied = cart.getTotalBeforeDiscount()-cart.getTotalAfterDiscount();
            String discountAppliedString = String.valueOf(discountApplied);
            if(discountAppliedString.lastIndexOf('.')==(discountAppliedString.length()-1)){
                discountAppliedString = discountAppliedString.substring(0,discountAppliedString.lastIndexOf('.')+1);
            }else{
                discountAppliedString = discountAppliedString.substring(0,discountAppliedString.lastIndexOf('.')+2);
            }

            discountApplied = Double.parseDouble(discountAppliedString);
            discountByRestaurantView.setText(String.valueOf(discountApplied));


            payableAmountView.setText(String.valueOf(payableAmount));
            checkoutButton.setVisibility(View.VISIBLE);
            checkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppController.getInstance().getCartManager().isOrderAmountValid()){
                        Intent intent = new Intent(v.getContext(),CheckoutActivity.class);
                        startActivity(intent);
                    }else{
                        showSnackBar();
                    }

                }
            });
        }


    }

    public void showSnackBar(){
        if(!AppController.getInstance().getCartManager().getCart().isCartEmpty()){
            if(!AppController.getInstance().getCartManager().isOrderAmountValid()){
                Snackbar snackbar =  Snackbar.make(coordinatorLayout,"Minimum order amount is : ".concat(String.valueOf(AppController.getInstance().getCartManager().getCurrentRestaurantInCart().getMinOrderAmount())),Snackbar.LENGTH_INDEFINITE);
                View snackbarLayout = snackbar.getView();
                TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                Drawable warningDrwable = getResources().getDrawable(R.drawable.warning,null);
                warningDrwable.setBounds(5,5,15,15);
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.warning_drawable,0,0,0);
                textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.sheet_item_spacing));
                snackbar.show();
            }
        }

    }

    public void orderNow(View view){
        Intent intent=new Intent(CartActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
