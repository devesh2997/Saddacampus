package com.saddacampus.app.helper;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anand on 19-10-2017.
 */

public class UserManager {

    private static final String TAG = UserManager.class.getSimpleName();

    private Context context;

    public UserManager(Context context){
        this.context = context;
    }

    public void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_UPDATE_USER_FIREBASE_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JSONObject responseObj = new JSONObject(response);//creating json object from string request
                    if(responseObj.getBoolean("error")){
                        //Todo
                        //token updated
                    }else{
                        //Todo
                        //some error occurred
                    }


                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //  Log.e("Sub Category Activity  line no. 204", error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", AppController.getInstance().getDbManager().getUserUid());
                params.put("token",token);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
