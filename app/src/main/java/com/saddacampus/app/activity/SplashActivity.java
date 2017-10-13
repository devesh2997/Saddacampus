package com.saddacampus.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Devesh Anand on 04-06-2017.
 */

public class SplashActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String TAG=SplashActivity.class.getSimpleName();

    Spinner spinner;

    String city;
    int isJustOpened=0;
    Button rating;
    Button general;
    ArrayList<String> citiesList;

    private SessionManager currentSession;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getCities();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        city=AppController.getInstance().getSessionManager().getSelectedCity();


        if(AppController.getInstance().getSessionManager().isCitySelected()){
            submitLocation();
        }else{
            int permissionCheck = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECEIVE_SMS);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"test1");
                getCities();
            }else{
                Log.d(TAG,"test2");
                getCities();
                ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},Config.MY_PERMISSIONS_REQUEST_READ_SMS);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCities();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    getCities();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void getCities(){

        final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setMessage("Getting served locations...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.URL_GET_CITIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);

                    JSONObject resultObj = responseObj.getJSONObject("result");
                    JSONArray citiesArray = resultObj.getJSONArray("cities");
                    //citiesList= new ArrayList<>();
                    List<String> cities = new ArrayList<String>();


                    //converting jsonArray to ArrayList
                    if (citiesArray != null) {
                        if(!AppController.getInstance().getSessionManager().isCitySelected()) {
                            String item = citiesArray.getJSONObject(0).getString("name");
                            if(item.equals("IIT (ISM) Dhanbad")){
                                AppController.getInstance().getSessionManager().setSelectedCity("Dhanbad");
                                FirebaseMessaging.getInstance().subscribeToTopic("Dhanbad");
                            }else if(item.equals("RNT Medical College (Udaipur)")){
                                AppController.getInstance().getSessionManager().setSelectedCity("Udaipur");
                                FirebaseMessaging.getInstance().subscribeToTopic("Udaipur");
                            }
                            else{
                                AppController.getInstance().getSessionManager().setSelectedCity(item);
                                String firebaseTopic = item.replace(" ","");
                                firebaseTopic = firebaseTopic.replace("(","");
                                firebaseTopic = firebaseTopic.replace(")","");
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

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SplashActivity.this, android.R.layout.simple_spinner_item, cities);
                    int position;
                    if(city.equals("Dhanbad")){
                        position=dataAdapter.getPosition("IIT (ISM) Dhanbad");
                    }
                    else if(city.equals("Udaipur")){
                        position=dataAdapter.getPosition("RNT Medical College (Udaipur)");
                    }
                    else{
                        position=dataAdapter.getPosition(city);
                    }

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    spinner.setSelection(position);

                } catch (JSONException e) {
                    Log.d(TAG, "json exception on setCategory slider");
                    Toast.makeText(getApplicationContext(), "Your current Network has a proxy issue kindly switch to other network or use your data pack to continue using this app.", Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG,error.getMessage());
               // Toast.makeText(getApplicationContext(),
                 //       error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent intent = new Intent(SplashActivity.this,NoInternetActivity.class);
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
            }else if(item.equals("RNT Medical College (Udaipur)")){
                AppController.getInstance().getSessionManager().setSelectedCity("Udaipur");
                FirebaseMessaging.getInstance().subscribeToTopic("Udaipur");
            }
            else{
                AppController.getInstance().getSessionManager().setSelectedCity(item);
                String firebaseTopic = item.replace(" ","");
                firebaseTopic = firebaseTopic.replace("(","");
                firebaseTopic = firebaseTopic.replace(")","");
                FirebaseMessaging.getInstance().subscribeToTopic(firebaseTopic);
            }

             if(isJustOpened==1)

            Toast.makeText(SplashActivity.this, "Selected city is " + item, Toast.LENGTH_SHORT).show();
        }

        isJustOpened=1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void submitLocation(){

        currentSession = new SessionManager(getApplicationContext());

        Intent intent;

        if(!AppController.getInstance().getSessionManager().getPreviousOrderRatingStatus()){
            Date currentDate = new Date(System.currentTimeMillis());
            Date orderDate = AppController.getInstance().getSessionManager().getPreviousOrderTime();

            long timeDiff = currentDate.getTime() - orderDate.getTime();

            long timeDiffHours = timeDiff / (60*60*1000);
            int timeDiffDays = (int)((timeDiff / (60*60*1000))/(1000*60*60*24));
            long timeDiffMinutes = timeDiff / (60*1000)%60;

            Log.d("Splash timediffinhours",String.valueOf(timeDiffHours));
            Log.d("Splash timediffindays",String.valueOf(timeDiffDays));
            Log.d("Splash timediffinmin",String.valueOf(timeDiffMinutes));


            if(timeDiffDays > 0 || timeDiffHours>1 ){

                intent = new Intent(this, OrderRatingActivity.class);

            }else{

                if(currentSession.isLoggedIn()){
                    intent = new Intent(this, MainActivity.class);
                }
                else{
                    intent = new Intent(this, LoginActivity.class);
                }

            }

        }else{
            if(currentSession.isLoggedIn()){
                intent = new Intent(this, MainActivity.class);
            }
            else{
                intent = new Intent(this, LoginActivity.class);
            }
        }


        startActivity(intent);
        finish();

    }

    public void submitLocationButton(View view){

        currentSession = new SessionManager(getApplicationContext());

        Intent intent;

        if(!AppController.getInstance().getSessionManager().getPreviousOrderRatingStatus()){
            Date currentDate = new Date(System.currentTimeMillis());
            Date orderDate = AppController.getInstance().getSessionManager().getPreviousOrderTime();

            long timeDiff = currentDate.getTime() - orderDate.getTime();

            long timeDiffHours = timeDiff / (60*60*1000);
            int timeDiffDays = (int)((timeDiff / (60*60*1000))/(1000*60*60*24));
            long timeDiffMinutes = timeDiff / (60*1000)%60;

            Log.d("Splash timediffinhours",String.valueOf(timeDiffHours));
            Log.d("Splash timediffindays",String.valueOf(timeDiffDays));
            Log.d("Splash timediffinmin",String.valueOf(timeDiffMinutes));


            if(timeDiffDays > 0 || timeDiffHours>1 || timeDiffMinutes>1 ){

                intent = new Intent(this, OrderRatingActivity.class);

            }else{

                if(currentSession.isLoggedIn()){
                    intent = new Intent(this, MainActivity.class);
                }
                else{
                    intent = new Intent(this, LoginActivity.class);
                }

            }

        }else{
            if(currentSession.isLoggedIn()){
                intent = new Intent(this, MainActivity.class);
            }
            else{
                intent = new Intent(this, LoginActivity.class);
            }
        }


        startActivity(intent);
        finish();

    }


}
