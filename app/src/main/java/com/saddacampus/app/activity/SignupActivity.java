package com.saddacampus.app.activity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 04-06-2017.
 */

public class SignupActivity extends AppCompatActivity {

    private String TAG = SignupActivity.class.getSimpleName();

    private SessionManager sessionManager;

    private RelativeLayout rootLayout;
    private TextInputLayout nameInputLayoutSignUp;
    private EditText nameInput;
    private TextInputLayout emailInputLayoutSignUp;
    private EditText emailInput;
    private TextInputLayout passwordInputLayoutSignUp;
    private EditText passwordInput;
    private TextInputLayout mobileInputLayoutSignUp;
    private EditText mobileInput;
    private Button signUpButton;

    String name;
    String email;
    String password;
    String mobile;

    boolean requestForSmsError = true;
    String requestForSmsMessage;

    //int value
    private int MY_PERMISSIONS_REQUEST_SMS = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);
        nameInputLayoutSignUp = (TextInputLayout)findViewById(R.id.nameInputLayoutSignUp);
        nameInput = (EditText)findViewById(R.id.nameInput);
        emailInputLayoutSignUp = (TextInputLayout)findViewById(R.id.emailInputLayoutSignUp);
        emailInput = (EditText)findViewById(R.id.emailInput);
        passwordInputLayoutSignUp = (TextInputLayout)findViewById(R.id.passwordInputLayoutSignUp);
        passwordInputLayoutSignUp.setPasswordVisibilityToggleEnabled(true);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        mobileInputLayoutSignUp = (TextInputLayout)findViewById(R.id.mobileInputLayoutSignUp);
        mobileInput = (EditText)findViewById(R.id.mobileInput);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }

        //Requesting Sms permission from user
      /*  int permissionCheck = ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.RECEIVE_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"test1");
        }else{
            Log.d(TAG,"test2");
            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},Config.MY_PERMISSIONS_REQUEST_READ_SMS);
        }*/




        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setEnabled(false);
                submitform();


            }
        });

        nameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInputLayoutSignUp.setError(null);
                nameInput.getBackground().clearColorFilter();
            }
        });

        emailInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInputLayoutSignUp.setError(null);
                emailInput.getBackground().clearColorFilter();
            }
        });

        passwordInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInputLayoutSignUp.setError(null);
                passwordInput.getBackground().clearColorFilter();
            }
        });

        mobileInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileInputLayoutSignUp.setError(null);
                mobileInput.getBackground().clearColorFilter();
            }
        });


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

    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() / 3;
        int cy = rootLayout.getHeight() / 3;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(350);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private void submitform(){
        name = nameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        mobile = mobileInput.getText().toString().trim();

        if(name.equals("")){
            nameInputLayoutSignUp.setError("Enter your name.");
            nameInput.setText("");
            nameInput.getBackground().clearColorFilter();
            signUpButton.setEnabled(true);
            return;
        }

        if(!checkEmail(email)){
            emailInput.getBackground().clearColorFilter();
            emailInput.setText("");
            signUpButton.setEnabled(true);
            return;
        }

        if(!checkPassword(password)){
            passwordInput.setText("");
            passwordInput.getBackground().clearColorFilter();
            signUpButton.setEnabled(true);
            return;
        }

        if(!checkMobile(mobile)){
            signUpButton.setEnabled(true);
            return;
        }

        requestForSms(mobile);


    }

    private boolean checkEmail(String email){
        email = emailInput.getText().toString().trim();
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInputLayoutSignUp.setError("Invalid Email Address");
            //Toast.makeText(this, "Invalid Email Address",Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean checkPassword(String password){
        password = passwordInput.getText().toString().trim();
        if(password.length()< 6){
            passwordInputLayoutSignUp.setError("Password should be greater than 5 characters");
            //Toast.makeText(this, "Password should be greater than 5 characters.", Toast.LENGTH_SHORT).show();

            requestFocus(passwordInput);
            return false;
        }
        return true;
    }

    private boolean checkMobile(String mobile){
        mobile = mobileInput.getText().toString().trim();
        if(mobile.length()<10){
            mobileInputLayoutSignUp.setError("Invalid Mobile Number");
            //Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            mobileInput.setText("");
            mobileInput.getBackground().clearColorFilter();
            return false;
        }
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void requestForSms(final String mobile){
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_REQUEST_SMS, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObj = new JSONObject(response);

                    requestForSmsError = responseObj.getBoolean("error");
                    requestForSmsMessage = responseObj.getString("message");

                    if(!requestForSmsError){
                        Intent intent = new Intent(SignupActivity.this, SubmitOtpActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        intent.putExtra("mobile",mobile);
                        intent.putExtra("is_register_user",true);
                        startActivity(intent);
                        finish();
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), requestForSmsMessage, Toast.LENGTH_SHORT).show();
                        signUpButton.setEnabled(true);
                    }


                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "Your current Network has a proxy issue kindly swith to other network or use your data pack to continue using this app.", Toast.LENGTH_LONG).show();
                    signUpButton.setEnabled(true);
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Intent intent = new Intent(SignupActivity.this,NoInternetActivity.class);
                startActivity(intent);
                signUpButton.setEnabled(true);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",mobile);
                //Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
