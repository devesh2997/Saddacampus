package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.PreviousOrderRateItemsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 09-08-2017.
 */

public class OrderRatingActivity extends AppCompatActivity {

    private static final String TAG = OrderRatingActivity.class.getSimpleName();

    RecyclerView previousOrderRateItemsRecycler;
    Button submitPreviousOrderRatingButton;
    TextView orderNotReceivedView;
    TextView skipOrderRatingView;

    ImageView restaurantInCartImageView;
    TextView restaurantInCartNameView;
    TextView restaurantInCartAddress;

    Cart cart;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_rating);

        restaurantInCartImageView = (ImageView)findViewById(R.id.cart_restaurant_image_view);
        restaurantInCartNameView = (TextView)findViewById(R.id.cart_restaurant_name);
        restaurantInCartAddress = (TextView)findViewById(R.id.cart_restaurant_address);

        previousOrderRateItemsRecycler = (RecyclerView)findViewById(R.id.rate_previous_order_recycler);
        submitPreviousOrderRatingButton = (Button)findViewById(R.id.submit_order_rating_button);
        //orderNotReceivedView = (TextView)findViewById(R.id.order_not_received_view);
        skipOrderRatingView = (TextView)findViewById(R.id.skip_order_rating_view);

        cart = AppController.getInstance().getSessionManager().getPreviousOrderCart();

        String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(cart.getRestaurantInCart().getImageResourceId());
        Picasso.with(OrderRatingActivity.this).load(url).fit().placeholder(R.drawable.place_holder_color).into(restaurantInCartImageView);
        restaurantInCartNameView.setText(cart.getRestaurantInCart().getName());
        restaurantInCartAddress.setText(cart.getRestaurantInCart().getAddress());

        PreviousOrderRateItemsAdapter previousOrderRateItemsAdapter = new PreviousOrderRateItemsAdapter(this,cart.getCartItems());
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        previousOrderRateItemsRecycler.setAdapter(previousOrderRateItemsAdapter);
        previousOrderRateItemsRecycler.setHasFixedSize(true);
        previousOrderRateItemsRecycler.setLayoutManager(linearLayoutManager);

        submitPreviousOrderRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(OrderRatingActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_SUBMIT_ORDER_RATING, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG,response);
                        JSONObject responseObject;
                        try{
                            responseObject = new JSONObject(response);

                            if(!responseObject.getBoolean("error")){
                                if(responseObject.getInt("success")==1){
                                    Toast.makeText(OrderRatingActivity.this,"Thanks for your feedback !",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(OrderRatingActivity.this,"Error in submitting your ratings !",Toast.LENGTH_SHORT).show();

                                }
                            }else{
                                Toast.makeText(OrderRatingActivity.this,"Error in submitting your ratings !",Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(OrderRatingActivity.this, MainActivity.class);
                            AppController.getInstance().getSessionManager().setPreviousOrderRatingStatus(true);
                            startActivity(intent);
                            finish();

                        }catch (JSONException e){
                            Log.d(TAG,e.getMessage());
                            Toast.makeText(OrderRatingActivity.this,"Error in submitting your ratings !",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderRatingActivity.this, MainActivity.class);
                            AppController.getInstance().getSessionManager().setPreviousOrderRatingStatus(true);
                            startActivity(intent);
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.getMessage());
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(OrderRatingActivity.this,"Error in connecting !",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderRatingActivity.this, MainActivity.class);
                        AppController.getInstance().getSessionManager().setPreviousOrderRatingStatus(true);
                        startActivity(intent);
                        finish();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("item_ratings", createOrderItemsJsonArray().toString());
                        params.put("UID", AppController.getInstance().getDbManager().getUserUid());
                        params.put("city",AppController.getInstance().getSessionManager().getSelectedCity());

                        Log.d(TAG,params.toString());

                        return params;
                    }
                };

                AppController.getInstance().addToRequestQueue(stringRequest);


            }
        });

        skipOrderRatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderRatingActivity.this, MainActivity.class);
                AppController.getInstance().getSessionManager().setPreviousOrderRatingStatus(true);
                startActivity(intent);
                finish();
            }
        });
    }

    private JSONArray createOrderItemsJsonArray(){


        JSONArray orderItemsRatingArray = new JSONArray();

        for(int i = 0; i<cart.getCartItems().size(); i++){
            CartItem cartItem = cart.getCartItems().get(i);
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("item_code",cartItem.getRestaurantItem().getProductCode());

                PreviousOrderRateItemsAdapter.PreviousOrderRateItemsItemsViewHolder holder = (PreviousOrderRateItemsAdapter.PreviousOrderRateItemsItemsViewHolder)previousOrderRateItemsRecycler.findViewHolderForAdapterPosition(i);
                jsonObject.put("item_submitted_rating",holder.getItemRating());

                orderItemsRatingArray.put(jsonObject);
            }catch (JSONException e){
                Log.d(TAG,e.getMessage());
            }

        }

        return orderItemsRatingArray;
    }





}
