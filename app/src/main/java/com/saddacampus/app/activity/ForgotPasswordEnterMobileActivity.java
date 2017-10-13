package com.saddacampus.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 26-07-2017.
 */

public class ForgotPasswordEnterMobileActivity extends AppCompatActivity {

    private static final String TAG = ForgotPasswordEnterMobileActivity.class.getSimpleName();

    EditText enterMobileView;
    Button submitMobileButton;

    boolean requestForSmsError = true;
    String requestForSmsMessage;

    String mobile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_enter_mobile);

        enterMobileView = (EditText)findViewById(R.id.enter_mobile);
        submitMobileButton = (Button)findViewById(R.id.submit_mobile);

        submitMobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMobileForOtp();
            }
        });

        //Requesting Sms permission from user
        int permissionCheck = ContextCompat.checkSelfPermission(ForgotPasswordEnterMobileActivity.this, Manifest.permission.RECEIVE_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"test1");
        }else{
            Log.d(TAG,"test2");
            ActivityCompat.requestPermissions(ForgotPasswordEnterMobileActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},Config.MY_PERMISSIONS_REQUEST_READ_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"test3");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG,"test4");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void submitMobileForOtp(){

        mobile=enterMobileView.getText().toString().trim();
        if(checkMobile(mobile)){
            requestForSms(mobile);
        }

    }

    private boolean checkMobile(String mobile){
        mobile = enterMobileView.getText().toString().trim();
        if(mobile.length()<10){
            enterMobileView.setError("Invalid Mobile Number");
            //Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            enterMobileView.setText("");
            return false;
        }
        return true;
    }

    private void requestForSms(final String mobile){
        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordEnterMobileActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_FORGOT_PASSWORD_OTP_REQUEST, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObj = new JSONObject(response);

                    requestForSmsError = responseObj.getBoolean("error");
                    requestForSmsMessage = responseObj.getString("message");

                    if(!requestForSmsError){
                        Intent intent = new Intent(ForgotPasswordEnterMobileActivity.this,SubmitOtpActivity.class);
                        intent.putExtra("is_register_user",false);
                        intent.putExtra("mobile",enterMobileView.getText().toString().trim());
                        startActivity(intent);
                        finish();
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), requestForSmsMessage, Toast.LENGTH_SHORT).show();
                    }


                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Log.e(TAG,error.getMessage());
               /* Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(ForgotPasswordEnterMobileActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",mobile);
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


}
