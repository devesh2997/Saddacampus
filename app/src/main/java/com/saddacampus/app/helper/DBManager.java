package com.saddacampus.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Devesh Anand on 25-05-2017.
 */

public class DBManager extends SQLiteOpenHelper {
    private static final String TAG = DBManager.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "saddaDatabase";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_FACEBOOK_USER = "fb_user";

    // Restaurants info table name
    private static final String TABLE_RESTAUTANTS_INFO = "RestaurantsInfo";

    // Search Result info table name;
    private static final String TABLE_SEARCH_RESULTS = "SearchResults";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_FUID = "fuid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";
    private static final String KEY_MOBILE  = "mobile";

    //Constructor
    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Overriding onCreate() method.
    //creates database.

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTables(db);

        Log.d(TAG, "Database tables created");
    }

    public void createTables(SQLiteDatabase db){
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_UID + " VARCHAR(23) NOT NULL UNIQUE,"
                + KEY_NAME + " VARCHAR(50) NOT NULL,"
                + KEY_EMAIL + " VARCHAR(100) NOT NULL UNIQUE,"
                + KEY_CREATED_AT + " DATETIME,"
                + KEY_UPDATED_AT + " DATETIME,"
                + KEY_MOBILE + " VARCHAR" + ");";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_FB_LOGIN_TABLE = "CREATE TABLE " + TABLE_FACEBOOK_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FUID + " VARCHAR(23) NOT NULL UNIQUE,"
                + KEY_NAME + " VARCHAR(50) NOT NULL" + ");";
        db.execSQL(CREATE_FB_LOGIN_TABLE);



        Log.d(TAG, "Database tables created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACEBOOK_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public boolean addUser(String uid, String name, String email, String created_at, String updated_at, String mobile) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_UID, uid); // Email
            values.put(KEY_NAME, name); // Name
            values.put(KEY_EMAIL, email); // Email
            values.put(KEY_CREATED_AT, created_at); // Created At
            values.put(KEY_UPDATED_AT, updated_at); //Updated At
            values.put(KEY_MOBILE, mobile); //Mobile


            // Inserting Row
            long id = db.insert(TABLE_USER, null, values);
            db.close(); // Closing database connection

            Log.d(TAG, "New user inserted into sqlite: " + id);
            return true;
        }catch (SQLiteException e){
            Log.d(TAG,e.getMessage());
            return false;
        }

    }

    /**
     * Storing facebok user details in database
     * */
    public boolean addFacebookUser(final String fuid, final String name) {

       try{
           SQLiteDatabase db = DBManager.this.getWritableDatabase();
           ContentValues values = new ContentValues();
           values.put(KEY_FUID, fuid); // Email
           values.put(KEY_NAME, name); // Name


           // Inserting Row
           long id = db.insert(TABLE_FACEBOOK_USER, null, values);
           db.close(); // Closing database connection

           Log.d(TAG, "New facebook user inserted into sqlite: " + id);
           return true;

           }catch (SQLiteException e){
            Log.d(TAG,e.getMessage());
           return false;
            }

    }


    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("uid", cursor.getString(cursor.getColumnIndex("uid")));
            user.put("name", cursor.getString(cursor.getColumnIndex("name")));
            user.put("email", cursor.getString(cursor.getColumnIndex("email")));
            user.put("created_at", cursor.getString(cursor.getColumnIndex("created_at")));
            user.put("updated_at", cursor.getString(cursor.getColumnIndex("updated_at")));
            user.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting facebook user data from database
     * */
    public HashMap<String, String> getFacebookUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_FACEBOOK_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("fuid", cursor.getString(cursor.getColumnIndex("fuid")));
            user.put("name", cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching facebook user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_FACEBOOK_USER,null,null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public String getUserUid(){
        String uuid;
        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_CUSTOM_AUTH_PROVIDER)){

            String selectQuery = "SELECT  * FROM " + TABLE_USER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                uuid =  cursor.getString(cursor.getColumnIndex("uid"));
            }else{
                uuid = null;
            }
            cursor.close();
            db.close();
            // return user
            Log.d(TAG, "Fetching userUid from Sqlite: " + uuid);

            return uuid;
        }else if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
            String selectQuery = "SELECT  * FROM " + TABLE_FACEBOOK_USER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                uuid =  cursor.getString(cursor.getColumnIndex("fuid"));
            }else{
                uuid = null;
            }
            cursor.close();
            db.close();
            // return user
            Log.d(TAG, "Fetching facebook userUid from Sqlite: " + uuid);

            return uuid;
        }else {
            return null;
        }

    }


    /**
     * Adding table for restaurant categories.
     * */
   /* public boolean storeRestaurantData(String restaurantNameInLowerCaseWithoutSpaces , String restaurantDataStringResponse){


        String restaurantCategoriesTableName = restaurantNameInLowerCaseWithoutSpaces.concat("_categories");
        String restaurantMenuTableName = restaurantNameInLowerCaseWithoutSpaces.concat("_menu");

        SQLiteDatabase db = this.getReadableDatabase();

        try{
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + restaurantCategoriesTableName);
            db.execSQL("DROP TABLE IF EXISTS " + restaurantMenuTableName);

            //CREATE TABLE AGAIN
            String create_restaurant_categories_table = "CREATE TABLE " + restaurantCategoriesTableName + "(id int(5) not null primary key auto_increment, category_name varchar(49) not null );";
            db.execSQL(create_restaurant_categories_table);
            String create_restaurant_menu_table = "CREATE TABLE " + restaurantMenuTableName + "(id int(5) not null primary key auto_increment,\n" +
                    "item_name varchar(32) not null , item_price varchar(3) not null,\n" +
                    "item_code varchar(5) not null, item_restaurant_category varchar(50) not null\n" +
                    ", item_rating float , item_rating_count int(11) , item_current_rating_by_user float,\n" +
                    ",item_order_count int(11), item_is_favourite boolean, item_is_veg boolean);";
            db.execSQL(create_restaurant_menu_table);


        }catch (SQLiteException e){
            Log.d(TAG,e.getMessage());
        }

        ArrayList<String> restaurantCategoriesArrayList = new ArrayList<>();
        ArrayList<RestaurantItem> restaurantMenuItems = new ArrayList<>();
        try{
            JSONObject restaurantDataResponse = new JSONObject(restaurantDataStringResponse);
            JSONObject restaurantDateResult = restaurantDataResponse.getJSONObject("result");
            JSONArray restaurantCategoriesJSONArray = restaurantDateResult.getJSONArray("categories");
            JSONArray restaurantMenu = restaurantDateResult.getJSONArray("menu");

            JSONArray restaurantCategoryItems ;

            ArrayList<RestaurantItem> restaurantItems;

            //converting jsonArray to ArrayList
            if (restaurantCategoriesJSONArray != null) {
                for (int i = 0; i < restaurantCategoriesJSONArray.length(); i++) {
                    JSONObject categoryObject = restaurantCategoriesJSONArray.getJSONObject(i);
                    String categoryName = categoryObject.getString("category_name");
                    restaurantCategoriesArrayList.add(categoryName);

                    restaurantCategoryItems = restaurantMenu.getJSONObject(i).getJSONArray("items");

                    for(int j =0 ;j<restaurantCategoryItems.length();j++){
                        String itemName = restaurantCategoryItems.getJSONObject(i).getString("item_name");
                        double itemPrice = Double.parseDouble(restaurantCategoryItems.getJSONObject(i).getString("item_price")) ;
                        String itemCode = restaurantCategoryItems.getJSONObject(i).getString("item_code");
                        String itemRestaurantCategory =  restaurantCategoryItems.getJSONObject(i).getString("item_restaurant_category");
                        String itemRating = restaurantCategoryItems.getJSONObject(i).getString("item_rating");
                        String itemRatingCount = restaurantCategoryItems.getJSONObject(i).getString("item_rating_count");
                        String itemCurrentRatingByUser = restaurantCategoryItems.getJSONObject(i).getString("item_current_rating_by_user");
                        String itemOrderCount = restaurantCategoryItems.getJSONObject(i).getString("item_order_count");
                        String itemIsFavourite = restaurantCategoryItems.getJSONObject(i).getString("item_is_favourite");
                        String itemIsVeg = restaurantCategoryItems.getJSONObject(i).getString("item_is_veg");

                        RestaurantItem restaurantItem = new RestaurantItem(itemName,itemCode)


                    }
                }
            }


        }catch (JSONException e){
            Log.d(TAG,e.getMessage());
        }



    }*/

    /**
     * Storing restaurants information in database.
     * */
    public boolean storeRestaurantsData(String restaurantInfo){
        ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();

        try{
            JSONObject restaurantInfoObj = new JSONObject(restaurantInfo);

            JSONObject resultObj = restaurantInfoObj.getJSONObject("result");
            JSONArray restaurantsJSONArray = resultObj.getJSONArray("restaurants");

            restaurantArrayList = new ArrayList<>();

            //converting jsonArray to ArrayList
            if (restaurantsJSONArray != null) {
                for (int i = 0; i < restaurantsJSONArray.length(); i++) {
                    JSONObject restaurantObject = restaurantsJSONArray.getJSONObject(i);
                    String name = restaurantObject.getString("name");
                    String contact = restaurantObject.getString("contact");

                    String address = restaurantObject.getString("address");
                    String status = (restaurantObject.getString("open").equals("1")) ? "open" : "close";
                    String minOrderAmount = restaurantObject.getString("minOrderAmount");
                    String timing = restaurantObject.getString("timing");
                    float deliveryCharges = Float.parseFloat(restaurantObject.getString("delivery_charges"));
                    float deliveryChargeSlab = Float.parseFloat(restaurantObject.getString("delivery_charge_slab"));
                    boolean acceptsOnlinePayment = restaurantObject.getInt("accepts_online_payment")==1;
                    String code = restaurantObject.getString("code");
                    int hasNewOffer = restaurantObject.getInt("has_new_offer");
                    int newOffer = restaurantObject.getInt("new_offer");
                    int offer = restaurantObject.getInt("offer");
                    String imageResourceId = restaurantObject.getString("imageResourceId");
                    String speciality = restaurantObject.getString("speciality");
                    float rating = (float)restaurantObject.getDouble("rating");
                    int messageStatus = restaurantObject.getInt("message_status");
                    String message;
                    if(messageStatus!=0){
                        message = restaurantObject.getString("message");
                    }else{
                        message = "";
                    }
                    Restaurant restaurant = new Restaurant(name,contact, acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);
                    restaurantArrayList.add(restaurant);
                }
            }
            try{
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAUTANTS_INFO);
                String create_restaurants_info_table = "CREATE TABLE " + TABLE_RESTAUTANTS_INFO + "(id INTEGER not null primary key AUTOINCREMENT, name varchar(49) not null,\n" +
                        "contact varchar(10) not null ,accepts_online_payment int not null, restaurant_code varchar(4) not null , address varchar(250) not null,\n" +
                        "open_status varchar(10) not null, min_order_amount varchar(6) not null, timing varchar(40) not null,\n" +
                        "delivery_charges varchar(50) not null,delivery_charge_slab varchar(22) not null, new_offer int not null,\n" +
                        "offer int not null, has_new_offer int not null, image_resource_id varchar(20) not null, speciality varchar(200) not null,\n" +
                        "rating float not null, message_status int not null, message varchar(250) );";
                db.execSQL(create_restaurants_info_table);

                for(int i=0;i<restaurantArrayList.size();i++){
                    Restaurant restaurant = restaurantArrayList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("name", restaurant.getName()); //
                    values.put("contact", restaurant.getContact()); //
                    values.put("accepts_online_payment",restaurant.acceptsOnlinePayment()?1:0);
                    values.put("restaurant_code", restaurant.getCode()); //
                    values.put("address", restaurant.getAddress()); //
                    values.put("open_status", restaurant.getStatus()); //
                    values.put("min_order_amount", restaurant.getMinOrderAmount()); //
                    values.put("timing", restaurant.getTiming()); //
                    values.put("delivery_charges", restaurant.getDeliveryCharges()); //
                    values.put("delivery_charge_slab",restaurant.getDeliveryChargeSlab());//
                    values.put("new_offer", restaurant.getNewOffer()); //
                    values.put("has_new_offer",restaurant.getHasNewOffer());
                    values.put("offer", restaurant.getOffer()); //
                    values.put("image_resource_id", restaurant.getImageResourceId()); //
                    values.put("speciality", restaurant.getSpeciality()); //
                    values.put("rating", restaurant.getRating()); //
                    values.put("message_status",restaurant.getMessageStatus());
                    values.put("message",restaurant.getMessage());
                    // Inserting Row
                    long id = db.insert(TABLE_RESTAUTANTS_INFO, null, values);

                    Log.d(TAG, "New restaurant inserted into sqlite: " + id);
                }
                db.close();
                return true;
            }catch (SQLiteException e){
                Log.d(TAG,e.getMessage());
                return false;
            }

        }catch (JSONException e){
            Log.d(TAG,e.getMessage());
            return false;
        }

    }

    public ArrayList<Restaurant> getRestaurantsData() {

        ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_RESTAUTANTS_INFO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            for(int i = 0; i<cursor.getCount();i++){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String contact = cursor.getString(cursor.getColumnIndex("contact"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String status = cursor.getString(cursor.getColumnIndex("open_status"));
                String minOrderAmount = cursor.getString(cursor.getColumnIndex("min_order_amount"));
                String timing = cursor.getString(cursor.getColumnIndex("timing"));
                float deliveryCharges = Float.parseFloat(cursor.getString(cursor.getColumnIndex("delivery_charges")));
                float deliveryChargeSlab = Float.parseFloat(cursor.getString(cursor.getColumnIndex("delivery_charge_slab")));
                boolean acceptsOnlinePayment = cursor.getInt(cursor.getColumnIndex("delivery_charge_slab"))==1;
                String code = cursor.getString(cursor.getColumnIndex("restaurant_code"));
                int newOffer = cursor.getInt(cursor.getColumnIndex("new_offer"));
                int offer = cursor.getInt(cursor.getColumnIndex("offer"));
                int hasNewOffer = cursor.getInt(cursor.getColumnIndex("has_new_offer"));
                String imageResourceId = cursor.getString(cursor.getColumnIndex("image_resource_id"));
                String speciality = cursor.getString(cursor.getColumnIndex("speciality"));
                float rating = Float.parseFloat(cursor.getString(cursor.getColumnIndex("rating")));
                int messageStatus = cursor.getInt(cursor.getColumnIndex("message_status"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);
                restaurantArrayList.add(restaurant);
                cursor.moveToNext();
            }

        }else{
            Log.d(TAG,"testing");
           restaurantArrayList = null;
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching restaurants data ");

        return restaurantArrayList;

    }

    public void storeSearchResultJson (String json){

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_RESULTS);
            String create_search_results_table = "CREATE TABLE " + TABLE_SEARCH_RESULTS + "(id INTEGER not null primary key AUTOINCREMENT, result text );";
            db.execSQL(create_search_results_table);

            ContentValues values = new ContentValues();
            values.put("result", json);

            db.insert(TABLE_SEARCH_RESULTS, null, values);

            Log.d(TAG,"search results stored");
        }catch (SQLiteException e){
            Log.d(TAG,e.getMessage());
        }

    }

    public String getSearchResultJson (){

        String searchResults;
        try{

            String selectQuery = "SELECT  * FROM " + TABLE_SEARCH_RESULTS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                searchResults =  cursor.getString(cursor.getColumnIndex("result"));
            }else{
                searchResults = null;
            }
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Log.d(TAG,e.getMessage());
            searchResults = null;
        }
        return searchResults;
    }



}
