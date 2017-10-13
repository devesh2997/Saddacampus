package com.saddacampus.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Cart;

import java.util.Date;

/**
<<<<<<< HEAD
 * This class is used for user persistance. As in if the user is already logged in or not.
=======
>>>>>>> b0639f1206ca2cd771d617c3ef0577abee82dcea
 * Created by Devesh Anand on 13-05-2017.
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "saddaCampusSession";

    // Application keys
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_AUTH_PROVIDER = "authProvider";

    private static final String KEY_SELECTED_CITY = "selectedCity";

    private static final String KEY_SELECTED_SERVICE = "selectedService";

    private static final String KEY_FIREBASE = "firebase";

    private static final String KEY_CART = "currentCartState";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String FB_USER_SAVED_MOBILE = "fbUserSavedMobile";

    private static final String FB_USER_SAVED_EMAIL = "fbUserSavedEmail";

    private static final String PREVIOUS_ORDER_ID = "previousOrderId";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Welcome Activity {
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setFbUserSavedMobile(String mobile){
        editor.putString(FB_USER_SAVED_MOBILE, mobile);
        editor.commit();

    }

    public void setFbUserSavedEmail(String email){
        editor.putString(FB_USER_SAVED_EMAIL, email);
        editor.commit();
    }

    public void setPreviousOrderId(String orderId){
        editor.putString(PREVIOUS_ORDER_ID, orderId);
        editor.commit();
    }

    public String getPreviousOrderId(){return pref.getString(PREVIOUS_ORDER_ID, "");}

    public String getFbUserSavedMobile() {
        return pref.getString(FB_USER_SAVED_MOBILE, null);
    }

    public String getFbUserSavedEmail() {
        return pref.getString(FB_USER_SAVED_EMAIL, null);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    //}}}}....welcome Activity.

    public void setLogin(boolean isLoggedIn,String authProvider) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_AUTH_PROVIDER, authProvider);


        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getKeyAuthProvider(){
        return pref.getString(KEY_AUTH_PROVIDER, null);
    }

    public String getSelectedCity(){return  pref.getString(KEY_SELECTED_CITY, "");}

    public boolean isCitySelected(){
        if(getSelectedCity().equals("")){
            return false;
        }else{
            return true;
        }
    }

    public String getfirebaseKey(){return  pref.getString(KEY_FIREBASE, "1");}

    public void setfirebaseKey(String key) {
        editor.putString(KEY_FIREBASE, key);
        editor.commit();
    }


    public void setSelectedCity(String city) {
        AppController.getInstance().getCartManager().clearCart();
        editor.putString(KEY_SELECTED_CITY, city);
        editor.commit();
    }

    public String getSelectedService(){return pref.getString(KEY_SELECTED_SERVICE, "food");}

    public void setCurrentCartState(Cart cart){
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        editor.putString(KEY_CART, json);

        editor.commit();

        Log.d(TAG, "Cart state updated");
    }

    public Cart getCurrentCartState(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_CART, null);
        Cart cart = gson.fromJson(json, Cart.class);

        return cart;
    }

    public boolean isCurrentCartSet(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_CART, null);
        if(json!=null){
            return true;
        }else{
            return false;
        }
    }

    public boolean logoutUser(){
        if(getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
            AppController.getInstance().getDbManager().deleteUsers();
            AppController.getInstance().getCartManager().clearCart();
            LoginManager.getInstance().logOut();
            setLogin(false,null);
            return true;
        }else{
            AppController.getInstance().getDbManager().deleteUsers();
            AppController.getInstance().getCartManager().clearCart();
            setLogin(false,null);
            return true;
        }
    }

    //get previous order rating.
    private static final String IS_PREVIOUS_ORDER_RATED = "isPreviousOrderRated";

    private static final String PREVIOUS_ORDER_CART = "previousOrderCart";

    private static final String PREVIOUS_ORDER_TIME = "previousOrderTime";

    public void setPreviousOrderRatingStatus(boolean isOrderRated){


        editor.putBoolean(IS_PREVIOUS_ORDER_RATED,isOrderRated);

        editor.commit();

        Log.d(TAG, "Order rating status changed");
    }

    public boolean getPreviousOrderRatingStatus(){
        return pref.getBoolean(IS_PREVIOUS_ORDER_RATED, true);
    }

    public Date getPreviousOrderTime(){
        Date date = new Date(pref.getLong(PREVIOUS_ORDER_TIME,0));

        return date;
    }

    public void setPreviousOrderCart(Cart cart){

        Date date = new Date(System.currentTimeMillis());
        long previousOrderTime = date.getTime();

        Gson gson = new Gson();
        String json = gson.toJson(cart);
        editor.putString(PREVIOUS_ORDER_CART, json);
        editor.putLong(PREVIOUS_ORDER_TIME,previousOrderTime);

        editor.commit();

        Log.d(TAG, "Previous order cart saved");

    }

    public Cart getPreviousOrderCart(){
        Gson gson = new Gson();
        String json = pref.getString(PREVIOUS_ORDER_CART, null);
        Cart cart = gson.fromJson(json, Cart.class);

        return cart;
    }


}
