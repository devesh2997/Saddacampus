package com.saddacampus.app.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.CategoryAdapter;
import com.saddacampus.app.app.Adapter.MainActivityFragmentPagerAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.Category;
import com.saddacampus.app.app.Fab;
import com.saddacampus.app.helper.CartManager;
import com.saddacampus.app.helper.SearchManager;
import com.saddacampus.app.helper.SessionManager;
import com.saddacampus.app.receiver.RatingAlertReceiver;
import com.saddacampus.app.service.MyFirebaseInstanceIDService;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.widget.ChatWidgetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 05-06-2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();
    int initialCartCount = 0;
    int filterKey = 0;
    boolean isoffer;
    boolean isNoMinDelivery;
    boolean freeDelivery;


    //CartManager class handles all tasks related to cart.
    CartManager cartManager;

    //Declaring views.
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView categoryRecycler;
    private TabLayout mainActivitySlider;
    private ViewPager mainActivityViewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;

    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    TextView txtViewCount;

    ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;
    CartClearedReceiver cartClearedReceiver;


    //declaring adapters and managers for views;
    private CategoryAdapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(MainActivity.this);
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        final View cart = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) cart.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(MainActivity.this);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.filter) {
            Intent intent = new Intent(MainActivity.this, FilterMainActivity.class);
            MainActivity.this.startActivity(intent);

        }
        /*else if(item.getItemId()==R.id.cart_icon_badge_action)
        {
            Intent intent = new Intent(MainActivity.this,FilterMainActivity.class);
            MainActivity.this.startActivity((intent));
        }*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        if (bundle != null) {
            filterKey = bundle.getInt("key");
            isoffer = bundle.getBoolean("isOffer");
            isNoMinDelivery = bundle.getBoolean("isNoMinDelivery");
            freeDelivery = bundle.getBoolean("freeDelivery");

        }
        //Initialising Views.
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_activity_coordinator_layout);
        categoryRecycler = (RecyclerView) findViewById(R.id.category_recycler);
        mainActivitySlider = (TabLayout) findViewById(R.id.main_activity_slider);
        mainActivityViewPager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setting up toolbars.
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.saddalogotransparent);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        MyFirebaseInstanceIDService f = new MyFirebaseInstanceIDService();
        f.getFireBaseKey();
        //setting up receiver for updating cart count badge.
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        cartClearedReceiver = new CartClearedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);
        IntentFilter cartClearedFilter = new IntentFilter("Cart Cleared.");
        this.registerReceiver(cartClearedReceiver,cartClearedFilter);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "query on submit");
                SessionManager sessionManager = AppController.getInstance().getSessionManager();
                SearchManager searchManager = new SearchManager(MainActivity.this, query, AppController.getInstance().getDbManager().getUserUid(), sessionManager.getSelectedCity(), false, null, null);
                searchManager.getSearchResults();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        //Implementing category slider.
        setCategorySlider();

        setUpTabbedView();

        setupFab();

        if(AppController.getInstance().getSessionManager().getSelectedCity().equals("")){
            Toast.makeText(MainActivity.this,"Please select your college first...",Toast.LENGTH_SHORT).show();
        }

        //Set up visitor info for zopim chat.

        if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_FB_AUTH_PROVIDER)){
            VisitorInfo visitorData = new VisitorInfo.Builder()
                    .name(AppController.getInstance().getDbManager().getFacebookUserDetails().get("name"))
                    .email(AppController.getInstance().getSessionManager().getFbUserSavedEmail())
                    .phoneNumber(AppController.getInstance().getSessionManager().getFbUserSavedMobile())
                    .note(AppController.getInstance().getSessionManager().getPreviousOrderId())
                    .build();
            ZopimChat.setVisitorInfo(visitorData);
        }else if(AppController.getInstance().getSessionManager().getKeyAuthProvider().equals(Config.KEY_CUSTOM_AUTH_PROVIDER)){
            VisitorInfo visitorData = new VisitorInfo.Builder()
                    .name(AppController.getInstance().getDbManager().getUserDetails().get("name"))
                    .email(AppController.getInstance().getDbManager().getUserDetails().get("email"))
                    .phoneNumber(AppController.getInstance().getDbManager().getUserDetails().get("mobile"))
                    .note(AppController.getInstance().getSessionManager().getPreviousOrderId())
                    .build();
            ZopimChat.setVisitorInfo(visitorData);
        }


    }


   /* private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        Toast.makeText(MainActivity.this, regId, Toast.LENGTH_LONG).show();
    }*/

    @Override
    protected void onStart() {
        //setting up receiver for updating cart count badge.
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);
        super.onStart();
    }

    @Override
    protected void onResume() {
        //txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        super.onResume();
    }

    @Override
    protected void onStop() {

        /*unregisterReceiver(itemAddedReceiver);
        unregisterReceiver(itemDeletedReceiver);*/
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //closing transition animations
        //overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);

    }


    public void setCategorySlider() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    Log.d(TAG, "line 228 of MainActivity activity");
                    JSONObject responseObj = new JSONObject(response);//creating json object from string request

                    JSONObject resultObj = responseObj.getJSONObject("result");
                    JSONArray categoriesArray = resultObj.getJSONArray("categories");
                    ArrayList<Category> categoryArrayList = new ArrayList<>();

                    //converting jsonArray to ArrayList
                    if (categoriesArray != null) {
                        for (int i = 0; i < categoriesArray.length(); i++) {
                            //TODO : if error comes first check here
                            categoryArrayList.add(new Category(categoriesArray.getJSONObject(i).getString("name"), categoriesArray.getJSONObject(i).getString("imageResourceId"), categoriesArray.getJSONObject(i).getString("tags")
                                    , categoriesArray.getJSONObject(i).getString("is_offer"), categoriesArray.getJSONObject(i).getString("is_veg")));
                        }
                    }

                    //setting up categories recycler view manager and layout

                    LinearLayoutManager categoryLinearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    categoryRecycler.setLayoutManager(categoryLinearLayoutManager);
                    categoryRecycler.setHasFixedSize(true);
                    mAdapter = new CategoryAdapter(categoryArrayList);
                    categoryRecycler.setAdapter(mAdapter);


                } catch (JSONException e) {
                    Log.e(TAG, "json exception on setCategory slider");
                    Toast.makeText(getApplicationContext(), "Your current Network has a proxy issue kindly swith to other network or use your data pack to continue using this app.", Toast.LENGTH_LONG).show();


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
                params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void setUpTabbedView() {

        MainActivityFragmentPagerAdapter mainActivityFragmentPagerAdapter = new MainActivityFragmentPagerAdapter(getSupportFragmentManager());
        mainActivityViewPager.setAdapter(mainActivityFragmentPagerAdapter);

        mainActivitySlider.setupWithViewPager(mainActivityViewPager);
    }

    private void setupFab() {

        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.colorAccent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });

        // Set material sheet item click listeners
        findViewById(R.id.fab_sheet_item_profile).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_favourites).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_previous_orders).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_go_to_cart).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_share).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_rate_us).setOnClickListener(this);
        findViewById(R.id.fab_sheet_item_contact_support).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setCategorySlider();

        setUpTabbedView();

        setupFab();

    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_sheet_item_go_to_cart:
                AppController.getInstance().getCartManager().showCart(MainActivity.this);
                break;
            case R.id.fab_sheet_item_favourites:
                Intent intent = new Intent(MainActivity.this, FavouriteItemActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_sheet_item_profile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;


            case R.id.fab_sheet_item_share: {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey,order food from various restaurants near your college by just a tap of your finger. Download the Saddacampus app now !\nhttps://play.google.com/store/apps/details?id=com.saddacampus.app&hl=en";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            }
            case R.id.fab_sheet_item_rate_us: {
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.saddacampus.app&hl=en")));

                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.saddacampus.app&hl=en")));
                }
                break;
            }
            case R.id.fab_sheet_item_previous_orders:
                Intent intent1 = new Intent(MainActivity.this, PreviousOrdersActivity.class);
                startActivity(intent1);
                break;
            case R.id.fab_sheet_item_contact_support:
                startActivity(new Intent(MainActivity.this, ZopimChatActivity.class));
                break;
        }
        materialSheetFab.hideSheet();
    }

    //Broadcast receivers.
    private class ItemAddedReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            final CartItem cartItem = (CartItem) arg1.getSerializableExtra("cart_item");
            String cartItemName = cartItem.getRestaurantItem().getName();
            if (cartItemName.length() > 20) {
                cartItemName = cartItemName.substring(0, 20);
                cartItemName = cartItemName.concat("...");
            }
            String cartItemQuantity = "(".concat(String.valueOf(cartItem.getItemQuantity()).concat(")"));
            String snackBarText = "Added : ".concat(cartItemName).concat(cartItemQuantity);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, snackBarText, Snackbar.LENGTH_LONG)
                    .setAction("Remove", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppController.getInstance().getCartManager().deleteItemFromCart(cartItem);
                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Item removed from Cart.", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();
            Log.d(TAG, "Cart Count Updated broadcast received");

        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Item Deleted From Cart.", Snackbar.LENGTH_LONG);

            snackbar.show();
            Log.d(TAG, "Cart Count Updated broadcast received");
        }
    }

    private class CartClearedReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Log.d(TAG, "Cart cleared");

        }
    }

    private class RatingSubmittedReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Rating Submitted Successfully", Snackbar.LENGTH_LONG);

            snackbar.show();
            Log.d(TAG, "Rating submitted successfully broadcast received");
        }
    }

    //Following methods are Called By RestaurantsFragment for implementing filter.
    public int myMethod() {
        return filterKey;
    }

    public boolean getIsOffer() {
        return isoffer;
    }

    public boolean getFreeDelivery() {
        return freeDelivery;
    }

    public boolean getIsNoMinDelivery() {
        return isNoMinDelivery;
    }
}

