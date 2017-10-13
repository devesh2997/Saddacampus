package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.DBManager;
import com.saddacampus.app.helper.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 04-06-2017.
 */

public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();

    private TextInputLayout mobileInput;
    private TextInputEditText mobileInputEditText;
    private TextInputLayout passwordInput ;
    private TextInputEditText passwordInputEditText;
    private TextView forgotPasswordView;
    private TextView signUpLink;
    private Button signInButton;
    private ProgressBar signInProgressBar;
    private ViewSwitcher buttonProgressSwitcher;

    private DBManager dbManager;
    private SessionManager sessionManager;

    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.saddacampus.app" +
                            "",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        fbLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");

        dbManager = new DBManager(LoginActivity.this);
        sessionManager = new SessionManager(LoginActivity.this);

        mobileInput = (TextInputLayout)findViewById(R.id.mobileInputLayout);
        mobileInputEditText = (TextInputEditText)findViewById(R.id.input_mobile);

        passwordInput = (TextInputLayout) findViewById(R.id.passwordInputLayout);
        passwordInputEditText = (TextInputEditText)findViewById(R.id.input_password);
        passwordInput.setPasswordVisibilityToggleEnabled(true);

        forgotPasswordView = (TextView)findViewById(R.id.forgot_password);

        buttonProgressSwitcher=(ViewSwitcher)findViewById(R.id.buttonProgressSwitcher);

        signInButton = (Button)findViewById(R.id.signInButton);

        signInProgressBar =(ProgressBar)findViewById(R.id.signInProgressBar);

        if(sessionManager.isLoggedIn()){
            goToMainActivity();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation signInButtonAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.sign_in_button_anim);
                //signInButton.startAnimation(signInButtonAnimation);
                buttonProgressSwitcher.showNext();
                submitForm();
            }
        });

        forgotPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordInit();
            }
        });


        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.show();
                dbManager.deleteUsers();
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    final JSONObject object,
                                    GraphResponse graphResponse) {

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_CREATE_FACEBOOK_USER, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            Log.d(TAG,response);
                                            try{
                                                JSONObject responseObject = new JSONObject(response);
                                                if(!responseObject.getBoolean("error")){
                                                   if(dbManager.addFacebookUser(object.getString("id"),object.getString("name"))){
                                                        progressDialog.dismiss();
                                                       sessionManager.setLogin(true,Config.KEY_FB_AUTH_PROVIDER);
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            }catch (JSONException e){
                                                Log.d(TAG, e.getMessage());

                                                progressDialog.dismiss();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this,NoInternetActivity.class);
                                            startActivityForResult(intent,0);

                                            Log.e(TAG,"onErrorResponse");
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            try{

                                                params.put("name", object.getString("name"));
                                                params.put("FUID", object.getString("id"));

                                            }catch (JSONException e){
                                                Log.d(TAG, e.getMessage());
                                            }
                                            return params;
                                        }


                                    };

                                    AppController.getInstance().addToRequestQueue(stringRequest);


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();

                }


            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        signUpLink = (TextView)findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void submitForm(){
        String mobile = mobileInputEditText.getText().toString().trim();
        String password = passwordInputEditText.getText().toString().trim();

        if(!checkMobile(mobile)){
            mobileInput.setError("Invalid Email Address.");
            mobileInputEditText.setText("");
            buttonProgressSwitcher.showNext();
            return;
        }
        if(!checkPassword(password)){
            passwordInput.setError("Invalid password.");
            passwordInputEditText.setText("");
            buttonProgressSwitcher.showNext();
            return;
        }

        loginUser(mobile, password);
    }


    private boolean checkMobile(String mobile){
        if(mobile.isEmpty() || mobile.length()<10){
            mobileInput.setError("Invalid Mobile number");
            //Toast.makeText(this, "Invalid Email Address",Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean checkPassword(String password){
        if(password.length()< 6){
            passwordInput.setError("Password should be greater than 5 characters");
            //Toast.makeText(this, "Password should be greater than 5 characters.", Toast.LENGTH_SHORT).show();

            requestFocus(passwordInput);
            return false;
        }
        return true;
    }

    private void requestFocus(View view){
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void loginUser(final String mobile, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response:" + response);

                try {
                    JSONObject responseObj = new JSONObject(response);
                    boolean signInError = responseObj.getBoolean("error");

                    if (!signInError) {
                        if (!sessionManager.isLoggedIn()) {
                            sessionManager.setLogin(true, Config.KEY_CUSTOM_AUTH_PROVIDER);
                        }

                        String uid = responseObj.getString("uid");

                        JSONObject userObject = responseObj.getJSONObject("user");
                        String name = userObject.getString("name");
                        String email = userObject.getString("email");
                        String created_at = userObject.getString("created_at");
                        String updated_at = userObject.getString("updated_at");
                        String mobile = userObject.getString("mobile");

                        dbManager.deleteUsers();
                        if (dbManager.addUser(uid, name, email, created_at, updated_at, mobile)) {
                            Animation progressBarAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.sign_in_progress_bar_anim);
                            //signInProgressBar.setForeground(getDrawable(R.drawable.view_switcher_inflate));
                            progressBarAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            buttonProgressSwitcher.startAnimation(progressBarAnimation);

                        }


                    }else{
                        String error_message = responseObj.getString("error_msg");
                        mobileInputEditText.setText("");
                        passwordInputEditText.setText("");
                        buttonProgressSwitcher.showNext();
                        Toast.makeText(LoginActivity.this, error_message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "heeheee " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Your current Network has a proxy issue kindly swith to other network or use your data pack to continue using this app.", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("password", password);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void goToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void forgotPasswordInit(){
        Intent intent = new Intent(LoginActivity.this,ForgotPasswordEnterMobileActivity.class);
        startActivity(intent);
    }

}
