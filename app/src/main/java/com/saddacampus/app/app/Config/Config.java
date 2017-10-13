package com.saddacampus.app.app.Config;

/**
 * Created by Devesh Anand on 22-05-2017.
 */

public class Config {
    // URL configuration for live server
    //public static final String URL_SADDACAMPUS_SERVER = "http://www.saddacampus.in/app/";
    //public static final String URL_SADDACAMPUS_SERVER = "http://172.16.133.243/fuck/";
   public static final String URL_SADDACAMPUS_SERVER = "http://saddacampus.com/app/appAPIs/";
    //public static final String URL_SADDACAMPUS_SERVER = "http://192.168.43.16/appAPIs/";

    public static final String URL_REQUEST_SMS = URL_SADDACAMPUS_SERVER + "request_sms.php";
    public static final String URL_VERIFY_OTP = URL_SADDACAMPUS_SERVER + "verify_otp.php";
    public static final String URL_REGISTER_USER = URL_SADDACAMPUS_SERVER + "register.php";
    public static final String URL_LOGIN_USER = URL_SADDACAMPUS_SERVER + "login.php";
    public static final String URL_FORGOT_PASSWORD_SET_NEW_PASSWORD = URL_SADDACAMPUS_SERVER + "forgot_password_set_new_password.php";
    public static final String URL_FORGOT_PASSWORD_OTP_REQUEST = URL_SADDACAMPUS_SERVER + "forgot_password_otp_request.php";
    public static final String URL_CREATE_FACEBOOK_USER = URL_SADDACAMPUS_SERVER+"create_facebook_user.php";
    public static final String URL_GET_CATEGORIES = URL_SADDACAMPUS_SERVER + "get_categories.php";
    public static final String URL_GET_RESTAURANTS = URL_SADDACAMPUS_SERVER + "get_restaurants.php";
    public static final String URL_GET_IMAGE_ASSETS = URL_SADDACAMPUS_SERVER + "Assets/images/";
    public static final String URL_GET_RESTAURANT_MENU = URL_SADDACAMPUS_SERVER + "get_restaurant_menu.php";
    public static final String URL_GET_RESTAURANT_INFO = URL_SADDACAMPUS_SERVER + "get_restaurant_info.php";
    public static final String URL_GET_RESTAURANT_REVIEWS = URL_SADDACAMPUS_SERVER + "get_restaurant_reviews.php";
    public static final String URL_GET_RESTAURANT_REVIEW_BY_USER = URL_SADDACAMPUS_SERVER + "get_restaurant_review_by_user.php";
    public static final String URL_SUBMIT_USER_REVIEW = URL_SADDACAMPUS_SERVER + "submit_user_review.php";
    public static final String URL_SET_ITEM_RATING = URL_SADDACAMPUS_SERVER + "set_item_rating.php";
    public static final String URL_UPDATE_FAVOURITE_ITEM = URL_SADDACAMPUS_SERVER + "update_is_item_favourite.php";
    public static final String URL_GET_SEARCH_RESULT = URL_SADDACAMPUS_SERVER + "get_search_results.php";
    public static final String URL_GET_SUB_CATEGORY_ITEMS = URL_SADDACAMPUS_SERVER + "get_sub_category_items.php";
    public static final String URL_GET_RESTAURANTS_OFFER = URL_SADDACAMPUS_SERVER + "get_offer_restaurants.php";
    public static final String URL_GET_ITEMS_OFFER = URL_SADDACAMPUS_SERVER + "get_item_offers.php";
    public static final String URL_GET_USER_FAVOURITES = URL_SADDACAMPUS_SERVER+"get_user_favourites.php";
    public static final String URL_PREVIOUS_ORDERS = URL_SADDACAMPUS_SERVER+"get_previous_orders.php";
    public static final String URL_GET_USER_PROFILE = URL_SADDACAMPUS_SERVER + "get_user_profile.php";
    public static final String URL_UPDATE_USER_PROFILE = URL_SADDACAMPUS_SERVER + "update_user_profile.php";
    public static final String URL_UPDATE_USER_PASSWORD = URL_SADDACAMPUS_SERVER + "update_user_password.php";
    public static final String URL_GET_RECOMMENDATIONS = URL_SADDACAMPUS_SERVER + "get_recommendations.php";
    public static final String URL_PROCESS_COD_ORDER = URL_SADDACAMPUS_SERVER + "process_cod_order.php";
    public static final String URL_INITIATE_ONLINE_PAYMENT = URL_SADDACAMPUS_SERVER + "initiate_online_payment.php";
    public static final String URL_TEST_GET_INSTAMOJO_ACCESS_TOKEN= "https://www.instamojo.com/oauth2/token/";
    public static final String URL_INSTAMOJO_WEB_HOOK = URL_SADDACAMPUS_SERVER + "instamojo_webhook.php";
    public static final String URL_INSTAMOJO_PAYMENT_STATUS = URL_SADDACAMPUS_SERVER + "instamojo_payment_status.php";

    public static final String URL_SUBMIT_ORDER_RATING= URL_SADDACAMPUS_SERVER + "submit_order_rating.php";

    public static final String URL_GET_CITIES = URL_SADDACAMPUS_SERVER + "get_cities.php";

    public static final int RATING_ALERT_DURATION = 7300000;
    //public static final int RATING_ALERT_DURATION = 61000;

   //app permission keys
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 200;




    //URL configuration for local test server
    /*public static final String URL_REQUEST_SMS = "http://192.168.0.3:8080/appAPIs/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://192.168.0.3:8080/appAPIs/verify_otp.php";
    public static final String URL_REGISTER_USER = "http://192.168.0.3:8080/appAPIs/register.php";
    public static final String URL_LOGIN_USER = "http://192.168.0.3:8080/appAPIs/login.php";
    public static final String URL_GET_CATEGORIES = "http://192.168.0.3:8080/appAPIs/get_categories.php";
    public static final String URL_GET_RESTAURANTS = "http://192.168.0.3:8080/appAPIs/get_restaurants.php";*/


    public static final String KEY_CUSTOM_AUTH_PROVIDER = "custom";
    public static final String KEY_FB_AUTH_PROVIDER = "facebook";

    //recommendation keys.
    public static final int KEY_GET_RECOMMENDATION_FOR_RESTAURANT = 1;
    public static final int KEY_GET_RECOMMENDATION_FOR_ITEM = 2;
    public static final int KEY_GET_RECOMMENDATION_FOR_CITY = 3;

    //payment mode keys.
    public static final String KEY_PAYMENT_COD = "COD";
    public static final String KEY_PAYMENT_ONLINE = "ONLINE";

    /*//test server configuration
    public static final String KEY_INSTAMOJO_CLIENT_ID = "zXkDbexxXBqUnj8hrTxSThD1RhDoSx7qGK6UK4xm";
    public static final String KEY_INSTAMOJO_CLIENT_SECRET = "JAGWfPiRBg53mbmGsXs6VjZ6N4oWJYqVnwGdGfFX7u3h6yXWw7WFPz1kvw0ENWnAkPAoHpDTjjf80P4ENi70S4eKVtvnML7k8Vs0qtk4BfN4XgEolCLXXUXseayfQtTJ";
    public static final String KEY_INSTAMOJO_GRANT_TYPE= "client_credentials";*/

   //live server configuration
   public static final String KEY_INSTAMOJO_CLIENT_ID = "WKkI8Xc43jtT8GSUjaRupof4deAalbLFEWgbkC9v";
   public static final String KEY_INSTAMOJO_CLIENT_SECRET = "lkImdUQ6c15eFy5AkpPYY9dNzNtMIWl5LKWZfZwBMVef03gDfVIgCdQrQehRatsTislkQJxOGiaEJXg6yfDctbL1ovtP35fcwz8DDeQWmuaWjnggQEbNBeQBehCxUcq8";
   public static final String KEY_INSTAMOJO_GRANT_TYPE= "client_credentials";

    //mobicomm senderid
    public static final String SMS_ORIGIN = "INFOSM";

    //special character to prefix the otp.
    public static final String OTP_DELIMITER = ":";


    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION_KEY = "https://firebasesaddacampus.000webhostapp.com/get_firebase.php";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


}
