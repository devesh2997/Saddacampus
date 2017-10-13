package com.saddacampus.app.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shubham on 24/07/17.
 *
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    public static final String PUSH_NOTIFICATION_KEY = "https://firebasesaddacampus.000webhostapp.com/get_firebase.php";


    SessionManager sm;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    public void getFireBaseKey(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.PUSH_NOTIFICATION_KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONObject resultObj = responseObj.getJSONObject("result");
                    String key = resultObj.getString("key");
                    AppController.getInstance().getSessionManager().setfirebaseKey(key);
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);




    }

    public void getFireBaseId(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, PUSH_NOTIFICATION_KEY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObj = new JSONObject(response);
                    JSONObject resultObj = responseObj.getJSONObject("result");
                    String key = resultObj.getString("key");
                    AppController.getInstance().getSessionManager().setfirebaseKey(key);
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);




    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}

