package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    TextInputLayout oldPassword;
    TextInputLayout newPassword;
    TextInputLayout confirmNewPassword;
    TextInputEditText oldPasswordEditText;
    TextInputEditText newPasswordEditText;
    TextInputEditText confirmNewPasswordEditText;

    Button submitNewPasswordButton;

    String mobile;
    String UID;

    Bundle extras;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = (TextInputLayout)findViewById(R.id.old_password);
        newPassword = (TextInputLayout)findViewById(R.id.new_password);
        confirmNewPassword = (TextInputLayout)findViewById(R.id.confirm_new_password);

        oldPassword.setPasswordVisibilityToggleEnabled(true);
        newPassword.setPasswordVisibilityToggleEnabled(true);
        confirmNewPassword.setPasswordVisibilityToggleEnabled(true);

        oldPasswordEditText = (TextInputEditText)findViewById(R.id.old_password_edittext);
        newPasswordEditText = (TextInputEditText)findViewById(R.id.new_password_edittext);
        confirmNewPasswordEditText = (TextInputEditText)findViewById(R.id.confirm_new_password_edittext);

        submitNewPasswordButton = (Button)findViewById(R.id.submit_new_password);


        if(savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras.getBoolean("is_forgot_password")){
                mobile = extras.getString("mobile");
                oldPassword.setVisibility(View.GONE);
            }else{
                UID = extras.getString("UID");
            }

        } else{
            if((Boolean)savedInstanceState.getSerializable("is_forgot_password")){
                mobile = (String) savedInstanceState.getSerializable("mobile");
            }else{
                mobile = (String) savedInstanceState.getSerializable("UID");
            }

        }

        submitNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewPasswordButton.setEnabled(false);
                if(extras.getBoolean("is_forgot_password")){
                    if(newPasswordEditText.getText().toString().trim().equals(confirmNewPasswordEditText.getText().toString().trim())){
                        changePassword(mobile);
                        submitNewPasswordButton.setEnabled(false);
                    }else{
                        confirmNewPasswordEditText.setText("");
                        newPasswordEditText.setError("Entered passwords do not match!");
                        submitNewPasswordButton.setEnabled(true);
                    }
                }else{
                    if(newPasswordEditText.getText().toString().trim().equals(confirmNewPasswordEditText.getText().toString().trim())){
                        updatePassword(UID);
                        submitNewPasswordButton.setEnabled(false);
                    }else{
                        confirmNewPasswordEditText.setText("");
                        newPasswordEditText.setError("Entered passwords do not match!");
                        submitNewPasswordButton.setEnabled(true);
                    }

                }
            }
        });
    }



    private void changePassword(final String mobile){
        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setMessage("Updating your password...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_FORGOT_PASSWORD_SET_NEW_PASSWORD, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{

                    JSONObject responseObj = new JSONObject(response);

                    boolean changePasswordError = responseObj.getBoolean("error");

                    String message = "";
                    try{
                        message = responseObj.getString("message");
                    }
                    catch (Exception e){
                        Log.d(TAG, "No error_msg");
                    }

                    Toast.makeText(ChangePasswordActivity.this,message,Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"going to main activity");
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                    submitNewPasswordButton.setEnabled(true);
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                 Log.e(TAG,error.getMessage());
                submitNewPasswordButton.setEnabled(true);
                Intent intent = new Intent(ChangePasswordActivity.this,NoInternetActivity.class);
                startActivity(intent);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",mobile);
                params.put("new_password",confirmNewPasswordEditText.getText().toString().trim());
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void updatePassword(final String UID){
        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setMessage("Updating your password...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_UPDATE_USER_PASSWORD, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{

                    JSONObject responseObj = new JSONObject(response);

                    boolean updateUserPasswordError = responseObj.getBoolean("error");

                    String message = "";
                    try{
                        message = responseObj.getString("message");
                    }
                    catch (Exception e){
                        Log.d(TAG, "No error_msg");
                    }

                    if(!updateUserPasswordError){
                        Toast.makeText(ChangePasswordActivity.this,"Your password has been updated. Login with your new password to continue.",Toast.LENGTH_SHORT);
                        Log.d(TAG,"going to main activity");
                        if(AppController.getInstance().getSessionManager().logoutUser()) {
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                    }else{
                        Toast.makeText(ChangePasswordActivity.this,message,Toast.LENGTH_SHORT);
                        submitNewPasswordButton.setEnabled(true);
                    }



                }catch (JSONException e){
                    Log.d(TAG,e.getMessage());
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //Log.e(TAG,error.getMessage());
                Intent intent = new Intent(ChangePasswordActivity.this,NoInternetActivity.class);
                startActivity(intent);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID",UID);
                params.put("old_password",oldPasswordEditText.getText().toString().trim());
                params.put("new_password",confirmNewPasswordEditText.getText().toString().trim());
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);

    }





}
