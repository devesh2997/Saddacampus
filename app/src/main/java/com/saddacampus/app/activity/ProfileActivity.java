package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.CartManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String TAG = ProfileActivity.class.getSimpleName();

    int isJustOpened=0;

    String city;


    Spinner spinner;
    Toolbar toolbar;
    CartManager cartManager;
    LinearLayout profileContainer;
    RelativeLayout editProfileContainer;
    LinearLayout userEmailContainer;
    LinearLayout userMobileContainer;
    TextView txtViewCount;
    TextView editProfileButton;
    CircleImageView profileImageView;
    EditText userNameView;
    EditText userEmailView;
    EditText userMobileView;
    Button submitUserProfileButton;
    Button changePasswordButton;
    Button logoutButton;


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(ProfileActivity.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View cart = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) cart.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(ProfileActivity.this);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);





        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        profileContainer = (LinearLayout)findViewById(R.id.profile_container);
        userEmailContainer = (LinearLayout)findViewById(R.id.user_email_container);
        userMobileContainer = (LinearLayout)findViewById(R.id.user_mobile_container);
        editProfileButton = (TextView) findViewById(R.id.edit_profile_button);
        profileImageView = (CircleImageView)findViewById(R.id.profile_image_view);
        userNameView = (EditText) findViewById(R.id.user_name);
        userEmailView = (EditText)findViewById(R.id.user_email);
        userMobileView = (EditText)findViewById(R.id.user_mobile);
        submitUserProfileButton = (Button)findViewById(R.id.submit_user_profile_button);
        changePasswordButton = (Button) findViewById(R.id.change_password_button);
        logoutButton = (Button)findViewById(R.id.logout_button);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);



        city=AppController.getInstance().getSessionManager().getSelectedCity();


        updateProfile();




        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.show();
                if(AppController.getInstance().getSessionManager().logoutUser()) {
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();

                }
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,ChangePasswordActivity.class);
                intent.putExtra("UID",AppController.getInstance().getDbManager().getUserUid());
                intent.putExtra("is_forgot_password",false);
                startActivity(intent);
            }
        });





    }



    public void updateProfile(){
        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
            setupFacebookProfile();
        }else{
            setupCustomProfile();
        }
    }

    public void getCities(){

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Getting served locations...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_GET_CITIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e(TAG, response.toString());
                try {
                    Log.d(TAG, "line 67 of Splash activity");
                    JSONObject responseObj = new JSONObject(response);

                    JSONObject resultObj = responseObj.getJSONObject("result");
                    JSONArray citiesArray = resultObj.getJSONArray("cities");
                    //citiesList= new ArrayList<>();
                    List<String> cities = new ArrayList<String>();


                    //converting jsonArray to ArrayList
                    if (citiesArray != null) {
                        if(!AppController.getInstance().getSessionManager().isCitySelected()) {
                            String item = citiesArray.getJSONObject(0).getString("name");
                            if (item.equals("IIT (ISM) Dhanbad")) {
                                AppController.getInstance().getSessionManager().setSelectedCity("Dhanbad");
                                FirebaseMessaging.getInstance().subscribeToTopic("Dhanbad");
                            }else if(item.equals("RNT Medical College (Udaipur)")){
                                AppController.getInstance().getSessionManager().setSelectedCity("Udaipur");
                                FirebaseMessaging.getInstance().subscribeToTopic("Udaipur");
                            } else {
                                AppController.getInstance().getSessionManager().setSelectedCity(item);
                                String firebaseTopic = item.replace(" ", "");
                                firebaseTopic = firebaseTopic.replace("(", "");
                                firebaseTopic = firebaseTopic.replace(")", "");
                                FirebaseMessaging.getInstance().subscribeToTopic(firebaseTopic);
                            }
                        }
                        for (int i = 0; i < citiesArray.length(); i++) {

                            String city = citiesArray.getJSONObject(i).getString("name");
                            if(city.equals("Dhanbad")){cities.add("IIT (ISM) Dhanbad");}
                            else if(city.equals("Udaipur")){cities.add("RNT Medical College (Udaipur)");}
                            else
                                cities.add((city));
                            //Toast.makeText(SplashActivity.this,citiesArray.getJSONObject(i).getString("name"),Toast.LENGTH_SHORT).show();
                        }
                    }


                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_item, cities);
                    int position;
                    if(city.equals("Dhanbad")){
                        position=dataAdapter.getPosition("IIT (ISM) Dhanbad");
                    } else if(city.equals("Udaipur")){
                        position=dataAdapter.getPosition("RNT Medical College (Udaipur)");
                    }
                    else{
                        position=dataAdapter.getPosition(city);
                    }

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    spinner.setSelection(position);
                } catch (JSONException e) {
                    Log.d(TAG, "exception on setting cities slider");
                    Toast.makeText(getApplicationContext(), "Your current Network has a proxy issue kindly swith to other network or use your data pack to continue using this app.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG,error.getMessage());
                // Toast.makeText(getApplicationContext(),
                //       error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent intent = new Intent(ProfileActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,1);

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        String college = AppController.getInstance().getSessionManager().getSelectedCity();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(college);

        {

            if(item.equals("IIT (ISM) Dhanbad")){
                AppController.getInstance().getSessionManager().setSelectedCity("Dhanbad");
                FirebaseMessaging.getInstance().subscribeToTopic("Dhanbad");
            }
            else if(item.equals("RNT Medical College (Udaipur)")){
                AppController.getInstance().getSessionManager().setSelectedCity("Udaipur");
                FirebaseMessaging.getInstance().subscribeToTopic("Udaipur");
            }else{
                AppController.getInstance().getSessionManager().setSelectedCity(item);
                String firebaseTopic = item.replace(" ","");
                firebaseTopic = firebaseTopic.replace("(","");
                firebaseTopic = firebaseTopic.replace(")","");
                FirebaseMessaging.getInstance().subscribeToTopic(firebaseTopic);
            }




            if(isJustOpened==1){
                Toast.makeText(ProfileActivity.this, "Selected city is " + item, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }


        }

        isJustOpened=1;


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void setupFacebookProfile(){

        getCities();

        editProfileButton.setVisibility(View.GONE);


        userEmailContainer.setVisibility(View.GONE);
        userMobileContainer.setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.GONE);
        submitUserProfileButton.setVisibility(View.GONE);
        /*profileContainer.removeView(editProfileContainer);
        profileContainer.removeView(userEmailContainer);
        profileContainer.removeView(userMobileContainer);
        profileContainer.removeView(changePasswordButton);
        profileContainer.removeView(submitUserProfileButton);*/
        userNameView.setEnabled(false);

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            final JSONObject object,
                            GraphResponse graphResponse) {

                        try{
                            userNameView.setText(object.getString("name"));
                            JSONObject userImageObject = object.getJSONObject("picture");
                            JSONObject userImageData = userImageObject.getJSONObject("data");
                            String userProfileImageUrl = "https://graph.facebook.com/"+AppController.getInstance().getDbManager().getUserUid()+"/picture?type=normal";

                            Log.d(TAG,userProfileImageUrl);
                            Picasso.with(ProfileActivity.this).load(userProfileImageUrl).fit().centerCrop().placeholder(R.drawable.placeholder).into(profileImageView);
                        }catch (JSONException e){
                            Log.d(TAG,e.getMessage());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setupCustomProfile(){


        getCities();

        profileImageView.setVisibility(View.GONE);
        submitUserProfileButton.setVisibility(View.GONE);
        /*profileContainer.removeView(profileImageView);
        profileContainer.removeView(submitUserProfileButton);*/
        userNameView.setEnabled(false);
        userEmailView.setEnabled(false);
        userMobileView.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_USER_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG,response);
                try{
                    JSONObject responseObject = new JSONObject(response);
                    if(!responseObject.getBoolean("error")){
                        userNameView.setText(responseObject.getString("name"));
                        userEmailView.setText(responseObject.getString("email"));
                        userMobileView.setText(responseObject.getString("mobile"));
                    }
                }catch (JSONException e){
                    Log.d(TAG, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG,"onErrorResponse");
                Intent intent = new Intent(ProfileActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("UID", AppController.getInstance().getDbManager().getUserUid());

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameView.setEnabled(true);
                userEmailView.setEnabled(true);
                userMobileView.setEnabled(true);
                editProfileButton.setVisibility(View.GONE);
                changePasswordButton.setVisibility(View.GONE);
                submitUserProfileButton.setVisibility(View.VISIBLE);
                /*profileContainer.removeView(editProfileContainer);
                profileContainer.removeView(changePasswordButton);
                profileContainer.addView(submitUserProfileButton);*/
                submitUserProfileButton.setEnabled(true);
            }
        });

        submitUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUserProfileButton.setEnabled(false);
                ProgressDialog submitProfileProgressDialog = new ProgressDialog(ProfileActivity.this);
                submitUserProfile();
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void submitUserProfile(){
        String changedUserName = userNameView.getText().toString().trim();
        String changedUserEmail = userEmailView.getText().toString().trim();
        String changedUserMobile = userMobileView.getText().toString().trim();

        if(changedUserName.equals("")){
            userNameView.setError("Enter your name.");
            userNameView.setText("");
            userNameView.getBackground().clearColorFilter();
            submitUserProfileButton.setEnabled(true);
            return;
        }

        if(!checkEmail(changedUserEmail)){
            userEmailView.getBackground().clearColorFilter();
            userEmailView.setText("");
            userEmailView.setEnabled(true);
            return;
        }


        if(!checkMobile(changedUserMobile)){
            submitUserProfileButton.setEnabled(true);
            return;
        }

        submitForm(changedUserName,changedUserEmail,changedUserMobile);

        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivityForResult(intent,1);


    }

    private boolean checkEmail(String email){
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmailView.setError("Invalid Email Address");
            //Toast.makeText(this, "Invalid Email Address",Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean checkMobile(String mobile){
        if(mobile.length()<10){
            userMobileView.setError("Invalid Mobile Number");
            //Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            userMobileView.setText("");
            userMobileView.getBackground().clearColorFilter();
            return false;
        }
        return true;
    }


    private void submitForm(final String newUserName,final String newUserEmail, final String newUserMobile){
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_UPDATE_USER_PROFILE, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try{
                    JSONObject responseObj = new JSONObject(response);

                    if(!responseObj.getBoolean("error")){
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        userNameView.setText(responseObj.getString("name"));
                        userNameView.setEnabled(false);
                        userEmailView.setText(responseObj.getString("email"));
                        userEmailView.setEnabled(false);
                        userMobileView.setText(responseObj.getString("mobile"));
                        userMobileView.setEnabled(false);
                        submitUserProfileButton.setVisibility(View.GONE);
                        editProfileButton.setVisibility(View.VISIBLE);
                        changePasswordButton.setVisibility(View.VISIBLE);
                    }else{
                    }


                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG,error.getMessage());
                Intent intent = new Intent(ProfileActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID",AppController.getInstance().getDbManager().getUserUid());
                params.put("name",newUserName);
                params.put("email",newUserEmail);
                params.put("mobile",newUserMobile);
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateProfile();
        super.onActivityResult(requestCode, resultCode, data);
    }


}

