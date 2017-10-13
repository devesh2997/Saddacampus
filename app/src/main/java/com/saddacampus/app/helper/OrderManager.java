package com.saddacampus.app.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.activity.CheckoutActivity;
import com.saddacampus.app.activity.NoInternetActivity;
import com.saddacampus.app.activity.PostOrderStatusActivity;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.SaddaOrder;
import com.saddacampus.app.receiver.RatingAlertReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Devesh Anand on 18-07-2017.
 */

public class OrderManager {

    private static final String TAG = OrderManager.class.getSimpleName();

    private Context context;
    private SaddaOrder saddaOrder;

    final ProgressDialog progressDialog;


    public OrderManager(Context context, SaddaOrder saddaOrder) {
        this.context = context;
        this.saddaOrder = saddaOrder;
        progressDialog = new ProgressDialog(context);
    }

    public SaddaOrder getSaddaOrder() {
        return saddaOrder;
    }

    public void setSaddaOrder(SaddaOrder saddaOrder) {
        this.saddaOrder = saddaOrder;
    }

    public void initOrder(String paymentMode){
        if(paymentMode.equals(Config.KEY_PAYMENT_COD)){
            initialiseCodOrder();
        }else if(paymentMode.equals(Config.KEY_PAYMENT_ONLINE)){
            initialiseOnlinePayment();
        }
    }

    private void initialiseCodOrder(){
        progressDialog.setMessage("Creating Your SaddaOrder...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.e(TAG,"initialising cod order.");

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_PROCESS_COD_ORDER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try{
                    JSONObject responseObject = new JSONObject(response);
                    progressDialog.dismiss();
                    if(!responseObject.getBoolean("error")){

                        if(responseObject.getBoolean("is_open")){
                            //Todo
                            Intent intent = new Intent(context, PostOrderStatusActivity.class);
                            intent.putExtra("mode","COD");
                            intent.putExtra("current_order_cart",AppController.getInstance().getCartManager().getCart());
                            intent.putExtra("order_id",responseObject.getString("order_id"));

                            SessionManager sessionManager = AppController.getInstance().getSessionManager();
                            Intent alertIntent=new Intent(context, RatingAlertReceiver.class);
                            PendingIntent p1=PendingIntent.getBroadcast(context,0, alertIntent,0);
                            AlarmManager a=(AlarmManager)context.getSystemService(ALARM_SERVICE);
                            a.set(AlarmManager.RTC,System.currentTimeMillis() + Config.RATING_ALERT_DURATION,p1);
                            Log.e(TAG,"Rating alert started.");


                            sessionManager.setPreviousOrderCart(sessionManager.getCurrentCartState());
                            sessionManager.setPreviousOrderId(responseObject.getString("order_id"));
                            sessionManager.setPreviousOrderRatingStatus(false);
                            AppController.getInstance().getCartManager().clearCart();
                            context.startActivity(intent);
                            ((CheckoutActivity)context).finish();
                            Log.d(TAG,"Testing done");
                        }else{
                            Toast.makeText(context,"This restaurant is closed at the moment. Cannot process your order.",Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(context,"Oops! Some error occurred. Your order could not be processed.",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    //Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.e(TAG,e.getMessage());
                    Toast.makeText(context,"Oops! Some error occurred in processing your order.",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG,error.getMessage());
                Log.e(TAG,"no response error listener");
                progressDialog.dismiss();
                Intent intent = new Intent(context,NoInternetActivity.class);
                context.startActivity(intent);
                //Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID",AppController.getInstance().getDbManager().getUserUid());
                params.put("city",AppController.getInstance().getSessionManager().getSelectedCity());
                params.put("restaurant_code",AppController.getInstance().getCartManager().getCart().getRestaurantInCart().getCode());
                params.put("order_info",createOrderJsonObject().toString());

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(25000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq);

    }


    private void initialiseOnlinePayment(){
        progressDialog.setMessage("Creating Your SaddaOrder...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_INITIATE_ONLINE_PAYMENT, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObject = new JSONObject(response);
                    progressDialog.dismiss();
                    if(!responseObject.getBoolean("error")){

                        if(responseObject.getBoolean("is_open")){
                            initialiseInstamojoPayment(responseObject.getString("order_id"));
                            Log.d(TAG,"Testing done");
                        }else{
                            Toast.makeText(context,"This restaurant is closed at the moment. Cannot process your order.",Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(context,"Oops! Some error occurred in processing your order.",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    progressDialog.dismiss();
                    Log.d(TAG,"initialiseOnlinePayment json exception");
                    Toast.makeText(context,"Oops! Some error occurred in processing your order.",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
                Intent intent = new Intent(context,NoInternetActivity.class);
                context.startActivity(intent);
                //Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID",AppController.getInstance().getDbManager().getUserUid());
                params.put("city",AppController.getInstance().getSessionManager().getSelectedCity());
                params.put("restaurant_code",AppController.getInstance().getCartManager().getCart().getRestaurantInCart().getCode());
                params.put("order_info",createOrderJsonObject().toString());

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(25000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public JSONObject createOrderJsonObject(){

        SaddaOrder saddaOrder = getSaddaOrder();

        Map<String,String > orderMap = new HashMap<>();
        orderMap.put("college", saddaOrder.getUserCollege());
        orderMap.put("hostel", saddaOrder.getUserHostel());
        orderMap.put("room_no", saddaOrder.getUserRoom());
        orderMap.put("name", saddaOrder.getUserName());
        orderMap.put("email", saddaOrder.getUserEmail());
        orderMap.put("mobile", saddaOrder.getUserMobile());

        JSONObject jsonObject = new JSONObject(orderMap);

        try{
            jsonObject.put("total", saddaOrder.getCart().getTotalAfterDiscount());
            jsonObject.put("items",createOrderItemsJsonArray());
        }catch (JSONException e){
            Log.d(TAG,e.getMessage());
        }
        return jsonObject;
    }

    private JSONArray createOrderItemsJsonArray(){
        JSONArray orderItemsArray = new JSONArray();
        LinkedList<CartItem> cartItems = AppController.getInstance().getCartManager().getItemsInCart();

        for(int i = 0; i<cartItems.size(); i++){
            CartItem cartItem = cartItems.get(i);
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("item_code",cartItem.getRestaurantItem().getProductCode());
                jsonObject.put("item_quantity",cartItem.getItemQuantity());

                orderItemsArray.put(jsonObject);
            }catch (JSONException e){
                Log.d(TAG,e.getMessage());
            }

        }

        return orderItemsArray;
    }


    public void initialiseInstamojoPayment(final String orderID){
        progressDialog.setMessage("Initialising payment request...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_TEST_GET_INSTAMOJO_ACCESS_TOKEN, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObject = new JSONObject(response);
                    if(responseObject.isNull("error")){
                        //Todo
                        //Create successfull activity.
                        String accessToken = responseObject.getString("access_token");
                        Log.d(TAG,"Access token:"+accessToken);
                        //Toast.makeText(context,accessToken,Toast.LENGTH_SHORT).show();
                        InstaMojoOrder instaMojoOrder = new InstaMojoOrder(accessToken,orderID,saddaOrder,context);

                    }else{
                        Log.d(TAG,"here is the problem");

                    }

                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG,error.getMessage());
                //Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
                Intent intent = new Intent(context,NoInternetActivity.class);
                context.startActivity(intent);
                //Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id",Config.KEY_INSTAMOJO_CLIENT_ID);
                params.put("client_secret",Config.KEY_INSTAMOJO_CLIENT_SECRET);
                params.put("grant_type",Config.KEY_INSTAMOJO_GRANT_TYPE);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


}
