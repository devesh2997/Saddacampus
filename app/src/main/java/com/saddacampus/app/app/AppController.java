package com.saddacampus.app.app;

import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.instamojo.android.Instamojo;
import com.saddacampus.app.helper.CartManager;
import com.saddacampus.app.helper.DBManager;
import com.saddacampus.app.helper.SessionManager;
import com.saddacampus.app.helper.UserManager;
import com.zopim.android.sdk.api.ZopimChat;

/**
 * Created by Devesh Anand on 13-05-2017.
 */

public class AppController extends Application{
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private CartManager cartManager;

    private SessionManager sessionManager;

    private DBManager dbManager;

    private UserManager userManager;

    private static AppController mInstance;

    Typeface sansationRegular ;
    Typeface sansationLight ;

    Typeface junctionLight;
    Typeface junctionRegular;
    Typeface junctionBold;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mInstance = this;
        cartManager = new CartManager(this);
        sessionManager = new SessionManager(this);
        dbManager = new DBManager(this);
        userManager = new UserManager(this);
        Instamojo.initialize(this);
        ZopimChat.init("4BmfjBmUP6R94lneZtYWogbq3CMfGNDK");

        sansationRegular = Typeface.createFromAsset(this.getAssets(),  "fonts/Sansation-Regular.ttf");
        sansationLight = Typeface.createFromAsset(this.getAssets(),  "fonts/Sansation-Light.ttf");

        junctionBold = Typeface.createFromAsset(this.getAssets(),  "fonts/junction-bold.ttf");
        junctionLight = Typeface.createFromAsset(this.getAssets(),  "fonts/junction-light.ttf");
        junctionRegular = Typeface.createFromAsset(this.getAssets(),  "fonts/junction-regular.ttf");
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public UserManager getUserManager(){return userManager;}

    public CartManager getCartManager(){
        return cartManager;
    }

    public SessionManager getSessionManager(){
        return sessionManager;
    }

    public DBManager getDbManager(){
        return dbManager;
    }

    public Typeface getSansationRegular() {
        return sansationRegular;
    }

    public Typeface getSansationLight() {
        return sansationLight;
    }

    public Typeface getJunctionLight() {
        return junctionLight;
    }

    public Typeface getJunctionRegular() {
        return junctionRegular;
    }

    public Typeface getJunctionBold() {
        return junctionBold;
    }
}
