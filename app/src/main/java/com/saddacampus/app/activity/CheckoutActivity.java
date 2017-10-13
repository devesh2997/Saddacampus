package com.saddacampus.app.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.instamojo.android.helpers.Constants;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Cart;
import com.saddacampus.app.app.DataObjects.SaddaOrder;
import com.saddacampus.app.helper.OrderManager;
import com.saddacampus.app.helper.SessionManager;
import com.saddacampus.app.receiver.RatingAlertReceiver;

import okhttp3.HttpUrl;
/**
 * Created by Devesh Anand on 18-07-2017.
 */

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();

    private CoordinatorLayout checkoutCoordinator;

    private EditText userNameView;
    private EditText userEmailView;
    private EditText userMobileView;
    private EditText userHostelView;

    private RelativeLayout codPayView;
    private ImageView codCheckView;
    private Button codPayButton;

    private RelativeLayout onlinePayView;
    private ImageView onlineCheckView;
    private Button onlinePayButton;

    private ProgressDialog dialog;

    String accessToken = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutCoordinator = (CoordinatorLayout)findViewById(R.id.checkout_coordinator);

        dialog = new ProgressDialog(CheckoutActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);

        userNameView = (EditText)findViewById(R.id.user_name_view);
        userMobileView = (EditText)findViewById(R.id.user_mobile_view);
        userMobileView.setEnabled(true);
        userEmailView = (EditText)findViewById(R.id.user_email_view);
        userHostelView = (EditText)findViewById(R.id.user_hostel_view);

        codPayView = (RelativeLayout)findViewById(R.id.payment_option_cod);
        codCheckView = (ImageView)findViewById(R.id.cod_check);
        codPayButton = (Button)findViewById(R.id.cod_pay_button);
        onlinePayView = (RelativeLayout)findViewById(R.id.payment_option_online);
        onlineCheckView = (ImageView)findViewById(R.id.online_check);
        onlinePayButton = (Button)findViewById(R.id.online_pay_button);

        updateOrderDetails();

        deactivateOnline();

        deactivateCOD();

        codPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDetails()){
                    activateCOD();
                }

            }
        });

        onlinePayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDetails()){
                    Log.d(TAG,"test");
                    if(AppController.getInstance().getCartManager().getCart().getRestaurantInCart().acceptsOnlinePayment()){
                        Log.d(TAG,"test1");
                        activateOnline();
                    }else{
                        Log.d(TAG,"test2");
                        Snackbar snackbar = Snackbar
                                .make(checkoutCoordinator, "This restaurant does not accept online payment . Try COD.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);
            accessToken = data.getStringExtra("access_token");
            Cart currentOrderCart = (Cart)data.getSerializableExtra("current_order_cart");

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                Intent intent = new Intent(CheckoutActivity.this, PostOrderStatusActivity.class);
                intent.putExtra("mode","ONLINE");
                intent.putExtra("order_id",orderID);
                intent.putExtra("transaction_id",transactionID);
                intent.putExtra("payment_id",paymentID);
                intent.putExtra("access_token",accessToken);
                intent.putExtra("current_order_cart",AppController.getInstance().getCartManager().getCart());

                SessionManager sessionManager = AppController.getInstance().getSessionManager();

                Intent alertIntent=new Intent(CheckoutActivity.this, RatingAlertReceiver.class);
                PendingIntent p1=PendingIntent.getBroadcast(getApplicationContext(),0, alertIntent,0);
                AlarmManager a=(AlarmManager)getSystemService(ALARM_SERVICE);
                a.set(AlarmManager.RTC,System.currentTimeMillis() + Config.RATING_ALERT_DURATION,p1);
                Log.e(TAG,"Rating alert started.");


                sessionManager.setPreviousOrderCart(sessionManager.getCurrentCartState());
                sessionManager.setPreviousOrderId(transactionID);
                sessionManager.setPreviousOrderRatingStatus(false);
                AppController.getInstance().getCartManager().clearCart();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CheckoutActivity.this,"Oops!! Payment was cancelled",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void activateCOD(){
        deactivateOnline();

        codPayButton.setVisibility(View.VISIBLE);
        codPayButton.setEnabled(true);

        codCheckView.setImageResource(R.drawable.check_purple);

        codPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CheckoutActivity.this);
                builder.setMessage("Please confirm your mobile number : ".concat(userMobileView.getText().toString().trim())).setTitle("Confirm order details");
                builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaddaOrder saddaOrder = getOrderDetails();

                        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
                            AppController.getInstance().getSessionManager().setFbUserSavedEmail(userEmailView.getText().toString().trim());
                            AppController.getInstance().getSessionManager().setFbUserSavedMobile(userMobileView.getText().toString().trim());
                        }

                        OrderManager orderManager = new OrderManager(CheckoutActivity.this, saddaOrder);
                        orderManager.initOrder(Config.KEY_PAYMENT_COD);
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();



            }
        });
    }

    private void activateOnline(){
        deactivateCOD();

        onlinePayButton.setVisibility(View.VISIBLE);
        onlinePayButton.setEnabled(true);

        onlineCheckView.setImageResource(R.drawable.check_purple);

        onlinePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CheckoutActivity.this);
                builder.setMessage("Please confirm your mobile number : ".concat(userMobileView.getText().toString().trim())).setTitle("Confirm order details");
                builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaddaOrder saddaOrder = getOrderDetails();

                        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
                            AppController.getInstance().getSessionManager().setFbUserSavedEmail(userEmailView.getText().toString().trim());
                            AppController.getInstance().getSessionManager().setFbUserSavedMobile(userMobileView.getText().toString().trim());
                        }

                        OrderManager orderManager = new OrderManager(CheckoutActivity.this, saddaOrder);
                        orderManager.initOrder(Config.KEY_PAYMENT_ONLINE);
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

    }

    private void deactivateCOD(){
        codPayButton.setVisibility(View.GONE);
        codPayButton.setEnabled(false);

        codCheckView.setImageResource(R.drawable.check_gray);

    }

    private void deactivateOnline(){
        onlinePayButton.setVisibility(View.GONE);
        onlinePayButton.setEnabled(false);

        onlineCheckView.setImageResource(R.drawable.check_gray);
    }

    private void updateOrderDetails(){
        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
            String userName = AppController.getInstance().getDbManager().getFacebookUserDetails().get("name");

            userNameView.setText(userName);
            userNameView.setEnabled(false);
            userNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userNameView.setEnabled(true);
                    requestFocus(userNameView);
                }
            });

            if(AppController.getInstance().getSessionManager().getFbUserSavedEmail()!=null){
                userEmailView.setText(AppController.getInstance().getSessionManager().getFbUserSavedEmail());
                requestFocus(userHostelView);
            }else{
                requestFocus(userEmailView);
            }

            if(AppController.getInstance().getSessionManager().getFbUserSavedMobile()!=null){
                userMobileView.setText(AppController.getInstance().getSessionManager().getFbUserSavedMobile());
                requestFocus(userHostelView);
            }else{
                requestFocus(userEmailView);
            }

            userEmailView.setEnabled(true);

            userMobileView.setEnabled(true);
            userHostelView.setEnabled(true);

        }else if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_CUSTOM_AUTH_PROVIDER)){
            final String userName = AppController.getInstance().getDbManager().getUserDetails().get("name");
            String userEmail = AppController.getInstance().getDbManager().getUserDetails().get("email");
            String userMobile = AppController.getInstance().getDbManager().getUserDetails().get("mobile");

            userNameView.setText(userName);
            userNameView.setEnabled(false);
            userEmailView.setText(userEmail);
            userMobileView.setText(userMobile);

            userEmailView.setEnabled(false);
            userMobileView.setEnabled(true);

            userNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userNameView.setEnabled(true);
                    requestFocus(userNameView);
                }
            });

            userEmailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userEmailView.setEnabled(true);
                    requestFocus(userEmailView);
                }
            });

            userMobileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userMobileView.setEnabled(true);
                    requestFocus(userMobileView);
                }
            });

            userHostelView.setEnabled(true);
            requestFocus(userHostelView);

        }
    }

    private boolean checkDetails(){
        String userName = userNameView.getText().toString().trim();
        String userEmail = userEmailView.getText().toString().trim();
        String userMobile = userMobileView.getText().toString().trim();
        String userHostel = userHostelView.getText().toString().trim();

        if(userName.equals(null)){
            userNameView.setError("Enter Your Name");
            requestFocus(userNameView);
            return false;
        }else if(userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            userEmailView.setError("Invalid Email Address");
            Toast.makeText(this, "Invalid Email Address",Toast.LENGTH_SHORT).show();
            requestFocus(userEmailView);
            return false;
        }else if(userMobile.length()<10){
            userMobileView.setError("Invalid Mobile Number");
            Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            userMobileView.setText("");
            requestFocus(userMobileView);
            return false;
        }else if(userHostel.equals(null)){
            userEmailView.setError("Enter Your Hostel");
            Toast.makeText(this, "Enter Your Hostel", Toast.LENGTH_SHORT).show();
            requestFocus(userHostelView);
            return false;
        }
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private SaddaOrder getOrderDetails(){
        String userName = userNameView.getText().toString().trim();
        String userMobile = userMobileView.getText().toString().trim();
        String userEmail = userEmailView.getText().toString().trim();
        String userHostel = userHostelView.getText().toString().trim();
        String userRoom = "Test";
        SaddaOrder saddaOrder = new SaddaOrder(userName,userMobile,userEmail,"IIT ISM",userHostel,userRoom);
        return saddaOrder;
    }



    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("sample-sdk-server.instamojo.com");
    }


}
