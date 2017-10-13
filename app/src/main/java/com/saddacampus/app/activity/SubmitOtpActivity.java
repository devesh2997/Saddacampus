package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.DBManager;
import com.saddacampus.app.helper.SessionManager;
import com.saddacampus.app.receiver.SmsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 05-06-2017.
 */

public class SubmitOtpActivity extends AppCompatActivity {

    private String TAG = SubmitOtpActivity.class.getSimpleName();

    private SmsReceiver smsReceiver ;

    static EditText otpInput;
    static ViewSwitcher otpVerificationSwitcher;
    public ImageButton verifyOtpButton ;
    ProgressBar otpVerificationProgressBar;

    String name;
    String email;
    String password;
    String mobile;

    DBManager dbManager;
    SessionManager sessionManager;

    Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_otp);

        otpInput = (EditText)findViewById(R.id.otpInput);
        otpVerificationSwitcher = (ViewSwitcher)findViewById(R.id.otpVerificationSwitcher);
        verifyOtpButton = (ImageButton)findViewById(R.id.verifyOtpButton);
        otpVerificationProgressBar = (ProgressBar)findViewById(R.id.otpVerificationProgressBar);

        smsReceiver = new SmsReceiver();
        smsReceiver.setMainActivityHandler(this);
        IntentFilter filter_sms_received = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter_sms_received);

        dbManager = new DBManager(SubmitOtpActivity.this);
        sessionManager = new SessionManager(SubmitOtpActivity.this);

        if(savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras.getBoolean("is_register_user")){
                name = extras.getString("name");
                email = extras.getString("email");
                password = extras.getString("password");
                mobile = extras.getString("mobile");
                Log.d(TAG,name+email+password+mobile);
            }else{
                mobile = extras.getString("mobile");
            }

        } else{
            if((Boolean)savedInstanceState.getSerializable("is_register_user")){
                name = (String) savedInstanceState.getSerializable("STRING_I_NEED");
                email = (String) savedInstanceState.getSerializable("email");
                password = (String) savedInstanceState.getSerializable("password");
                mobile = (String) savedInstanceState.getSerializable("mobile");
                Log.d(TAG,name+email+password+mobile);
            }else{
                mobile = (String) savedInstanceState.getSerializable("mobile");
            }

        }


        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(smsReceiver);
                verifyOtp(mobile,otpInput.getText().toString().trim());


            }
        });
    }

    public void receivedSms(String message)
    {
        try
        {
            Log.d(TAG, "signupotp receivedsms try");
            otpInput.setText(message);
            //super.onCreate(null);
            //ImageButton verifyOtpButton=(ImageButton)findViewById(R.id.verifyOtpButton);
            //view.performClick();
            //verifyOtp(mobile,message);
        }
        catch (Exception e)
        {
            Log.d(TAG, "signupotp receivedsms catch :"+e.getMessage());
        }
    }



    public void verifyOtp(final String mobile , final String otp){

        final ProgressDialog progressDialog = new ProgressDialog(SubmitOtpActivity.this);
        progressDialog.setMessage("Verifying otp...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(!otp.isEmpty()){
            otpVerificationSwitcher.showNext();
            StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_VERIFY_OTP, new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.d(TAG, response.toString());
                    try{
                        JSONObject responseObj = new JSONObject(response);


                        boolean verifyError = responseObj.getBoolean("error");
                        boolean status = responseObj.getBoolean("status");
                        String message = responseObj.getString("message");

                        if(!verifyError){
                            if(extras.getBoolean("is_register_user")){
                                registerUser(name,email,password,mobile);
                            }else{
                                goToChangePasswordActivity(mobile);
                            }

                        }else{
                            otpVerificationSwitcher.showNext();
                            otpInput.setText("");
                            Toast.makeText(getApplicationContext(),"Oops...Some error occurred in verifying your OTP.",Toast.LENGTH_SHORT).show();
                        }


                    }catch (JSONException e){
                        Toast.makeText(getApplicationContext(),"Oops...Some error occurred in processing your request.",Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Log.e(TAG,error.getMessage());
                 //   Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    otpVerificationSwitcher.showNext();
                    Intent intent = new Intent(SubmitOtpActivity.this,NoInternetActivity.class);
                    startActivity(intent);

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mobile",mobile);
                    params.put("otp",otp);
                    Log.e(TAG, "Posting params: " + params.toString());

                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq);

        }
    }

    private void registerUser(final String name,final String email, final String password, final String mobile){
        final ProgressDialog progressDialog = new ProgressDialog(SubmitOtpActivity.this);
        progressDialog.setMessage("Registering your account...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_REGISTER_USER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{
                    Log.d(TAG,"line 167 of signupOtp activity");
                    JSONObject responseObj = new JSONObject(response);

                    Log.d(TAG,"line 170 of signupOtp activity");

                    boolean registerError = responseObj.getBoolean("error");

                    String error_message = "";
                    try{
                        error_message = responseObj.getString("error_msg");
                    }
                    catch (Exception e){
                        Log.d(TAG, "No error_msg");
                    }


                    if(!registerError){

                        Log.d(TAG,"line 177 of signupOtp activity");

                        JSONObject userObj = responseObj.getJSONObject("user");

                        Log.d(TAG,"line 181 of signupOtp activity");

                        String responseUid = responseObj.getString("uid");
                        Log.d(TAG, "responseUid :"+responseUid);
                        String responseName = userObj.getString("name");
                        Log.d(TAG, "responseName :"+responseName);
                        String responseEmail = userObj.getString("email");
                        Log.d(TAG, "responseEmail :"+responseEmail);
                        String responseCreatedAt = userObj.getString("created_at");
                        Log.d(TAG, "responseCreatedAt :"+responseCreatedAt);
                        String responseUpdatedAt = userObj.getString("updated_at");
                        Log.d(TAG, "responseUpdatedAt :"+responseUpdatedAt);
                        String responseMobile = userObj.getString("mobile");
                        Log.d(TAG, "responseMobile :"+responseMobile);

                        if(dbManager.addUser(responseUid,responseName,responseEmail,responseCreatedAt,responseUpdatedAt,responseMobile)){
                            if(!sessionManager.isLoggedIn()){
                                sessionManager.setLogin(true,Config.KEY_CUSTOM_AUTH_PROVIDER);
                            }
                            Log.d(TAG,"going to main activity");
                            Intent intent = new Intent(SubmitOtpActivity.this, MainActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }





                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }else{
                        otpVerificationSwitcher.showNext();
                        Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    otpVerificationSwitcher.showNext();
                    Log.d(TAG,"line 215 of signupOtp activity");
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
                otpVerificationSwitcher.showNext();
                Intent intent = new Intent(SubmitOtpActivity.this,NoInternetActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("email",email);
                params.put("password",password);
                params.put("mobile",mobile);
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void goToChangePasswordActivity(String mobile){
        Intent intent = new Intent(SubmitOtpActivity.this,ChangePasswordActivity.class);
        intent.putExtra("is_forgot_password",true);
        intent.putExtra("mobile",mobile);
        startActivity(intent);
        finish();
    }

/*
    private void goToMainActivity(){
        Log.d(TAG,"going to main activity");
        Intent intent = new Intent(SubmitOtpActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void updateSqliteAndSession(){
        DBManager dbManager = new DBManager(SubmitOtpActivity.this);
        SessionManager sessionManager = new SessionManager(SubmitOtpActivity.this);

        if(dbManager.addUser(responseUid,responseName,responseEmail,responseCreatedAt,responseUpdatedAt,responseMobile)){
            if(!sessionManager.isLoggedIn()){
                sessionManager.setLogin(true);
            }
            goToMainActivity();
        }
    }
*/


}
