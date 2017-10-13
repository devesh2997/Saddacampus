package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.PreviousOrderCartItemsExpandableListAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 31-07-2017.
 */

public class PostOrderStatusActivity extends AppCompatActivity {

    private static final String TAG = PostOrderStatusActivity.class.getSimpleName();

    ProgressBar orderProcessingProgressBar;

    TextView orderStatusView;
    LinearLayout orderDetailsContainer;

    TextView currentOrderId;
    TextView currentOrderPaymentMode;
    TextView currentOrderTotal;

    ImageView currentOrderRestaurantImage;
    TextView currentOrderRestaurantName;
    TextView currentOrderRestaurantAddress;

    ExpandableListView currentOrderItems;

    Bundle extras;

    String mode;
    String orderId;
    String transactionId;
    String paymentId;
    String accessToken;
    Cart currentOrderCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order_status);

        orderProcessingProgressBar = (ProgressBar)findViewById(R.id.processing_order_progress_bar);
        ChasingDots chasingDots = new ChasingDots();
        chasingDots.setColor(getResources().getColor(R.color.colorAccent));
        orderProcessingProgressBar.setIndeterminateDrawable(chasingDots);
        orderProcessingProgressBar.setVisibility(View.VISIBLE);

        orderStatusView = (TextView)findViewById(R.id.order_status_view);


        orderDetailsContainer = (LinearLayout)findViewById(R.id.current_order_details_cpntainer);
        orderDetailsContainer.setVisibility(View.GONE);
        currentOrderId = (TextView)findViewById(R.id.current_order_id_view);
        currentOrderPaymentMode = (TextView)findViewById(R.id.current_order_payment_mode);
        currentOrderTotal = (TextView)findViewById(R.id.current_order_total);

        currentOrderRestaurantImage = (ImageView)findViewById(R.id.current_order_restaurant_image_view);
        currentOrderRestaurantName = (TextView)findViewById(R.id.current_order_restaurant_name);
        currentOrderRestaurantAddress = (TextView)findViewById(R.id.current_order_restaurant_address);

        currentOrderItems = (ExpandableListView)findViewById(R.id.current_order_item_expandable_list);


        extras = getIntent().getExtras();

        mode = extras.getString("mode");

        if(mode.equals("COD")){
            orderStatusView.setText(getResources().getString(R.string.order_placed_successfully));
            orderProcessingProgressBar.setVisibility(View.GONE);
            transactionId = extras.getString("order_id");
            currentOrderCart = (Cart)extras.getSerializable("current_order_cart");
            populateOrderDetails();
            //animatedCircleLoadingView.stopOk();
        }else{
            orderId = extras.getString("order_id");
            transactionId = extras.getString("transaction_id");
            paymentId = extras.getString("payment_id");
            accessToken = extras.getString("access_token");
            currentOrderCart = (Cart)extras.getSerializable("current_order_cart");

            Log.d(TAG,currentOrderCart.getRestaurantInCart().getName());

            getPaymentStatus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==999){
            Intent intent = new Intent(PostOrderStatusActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }else{
            getPaymentStatus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PostOrderStatusActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void getPaymentStatus(){
        final ProgressDialog progressDialog = new ProgressDialog(PostOrderStatusActivity.this);
        progressDialog.setMessage("Getting order status...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_INSTAMOJO_PAYMENT_STATUS, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("success")){
                        JSONObject paymentObject = responseObject.getJSONObject("payment");
                        if(paymentObject.getString("status").equals("Credit")){
                            orderStatusView.setText(getResources().getString(R.string.order_placed_successfully));
                            orderProcessingProgressBar.setVisibility(View.GONE);
                            //animatedCircleLoadingView.stopOk();
                            populateOrderDetails();
                        }else{
                            orderStatusView.setText("Payment not received at the moment...");
                            orderProcessingProgressBar.setVisibility(View.GONE);
                            //animatedCircleLoadingView.stopFailure();
                        }

                    }else{
                        Log.d(TAG,responseObject.getString("message"));
                        orderStatusView.setText("Error!");
                        orderProcessingProgressBar.setVisibility(View.GONE);
                        //animatedCircleLoadingView.stopFailure();
                        Toast.makeText(PostOrderStatusActivity.this,"Oops!Some Error occurred in getting your order status. Try again later.",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    Toast.makeText(PostOrderStatusActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Log.e(TAG,error.getMessage());
                orderProcessingProgressBar.setVisibility(View.GONE);
                Intent intent = new Intent(PostOrderStatusActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("instamojo_transaction_id", paymentId);

                Log.d(TAG,params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void rateUs(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.saddacampus.app&hl=en"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{

            startActivityForResult(intent,999);

        }
        catch (ActivityNotFoundException e){
            startActivityForResult(intent,999);
        }
        //startActivity(intent);
        //finish();
    }



    @Override
    protected void onPause() {
        //goHome();
        super.onPause();
    }

    public void goHome(){
        Intent intent = new Intent(PostOrderStatusActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void goHome(View view){

        Intent intent = new Intent(PostOrderStatusActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    void populateOrderDetails(){
        orderDetailsContainer.setVisibility(View.VISIBLE);
        currentOrderId.setText(transactionId);
        currentOrderTotal.setText("Rs. ".concat(String.valueOf(currentOrderCart.getTotalAfterDiscount())));

        if(mode.equals("COD")){
            currentOrderPaymentMode.setText("COD");
        }else{
            currentOrderPaymentMode.setText("Online Transaction");
        }


        ArrayList<CartItem> cartItems = new ArrayList<>();
        for(int i=0 ; i<currentOrderCart.getCartItems().size();i++){
            cartItems.add(currentOrderCart.getCartItems().get(i));
        }

        PreviousOrderCartItemsExpandableListAdapter previousOrderCartItemsExpandableListAdapter = new PreviousOrderCartItemsExpandableListAdapter(PostOrderStatusActivity.this,cartItems);
        currentOrderItems.setAdapter(previousOrderCartItemsExpandableListAdapter);

        String url = Config.URL_GET_IMAGE_ASSETS+"Restaurants/".concat(cartItems.get(0).getRestaurantItem().getItemRestaurant().getImageResourceId());
        Picasso.with(PostOrderStatusActivity.this).load(url).fit().placeholder(R.drawable.placeholder).into(currentOrderRestaurantImage);

        currentOrderRestaurantName.setText(cartItems.get(0).getRestaurantItem().getItemRestaurant().getName());
        currentOrderRestaurantAddress.setText(cartItems.get(0).getRestaurantItem().getItemRestaurant().getAddress());

        currentOrderItems.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(parent.isGroupExpanded(groupPosition)){
                    parent.collapseGroup(groupPosition);
                }else{
                    parent.expandGroup(groupPosition,true);
                }
                return true;
            }
        });
    }


}
