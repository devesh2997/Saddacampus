package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.RestaurantMenuFragmentPagerAdapter;
import com.saddacampus.app.app.Adapter.RestaurantMenuRecommendedItemsAdapter;
import com.saddacampus.app.app.Adapter.RestaurantReviewsAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.app.GrayscaleTransformation;
import com.saddacampus.app.helper.CartManager;
import com.saddacampus.app.helper.DBManager;
import com.saddacampus.app.helper.RecommendationManager;
import com.saddacampus.app.helper.SearchManager;
import com.saddacampus.app.helper.SessionManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Shubham Vishwakarma on 05-06-2017.
 */

public class RestaurantMenu extends AppCompatActivity implements View.OnClickListener {

    private String TAG = RestaurantMenu.class.getSimpleName();


    //Declaring views.

    private CoordinatorLayout coordinatorLayout;
    private TabLayout mainActivitySlider;
    private ViewPager RestaurantMenuViewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;

    //private MaterialSheetFab materialSheetFab;
    private FloatingActionButton fabCart;
    private Button goToCart;
    private int statusBarColor;

    //restaurant info views
    ImageView restaurantImageView;
    SimpleRatingBar restaurantRatingView;
    TextView restaurantStatusView;
    TextView restaurantMinOrderAmountView;
    TextView restaurantDeliveryChargeView;
    LinearLayout restaurantFreeDeliveryView;
    LinearLayout restaurantOnlinePaymentView;
    TextView restaurantSpecialityView;
    LinearLayout restaurantOfferContainer;
    TextView restaurantOfferView;
    RelativeLayout restaurantMessageContainer;
    TextView restaurantMessageView;

    //Restaurant review views
    RecyclerView restaurantReviewsRecycler;
    EditText userReviewInput;
    ImageButton submitUserReviewButton;
    SlidingUpPanelLayout reviewsSlider;

    //Restaurant recommendations views
    RecyclerView restaurantTopRatedItemsRecycler;
    RecyclerView restaurantTopOrderedItemsRecycler;
    CardView restaurantMenuRecommendationsContainer;
    RecyclerView restaurantMenuRecommendationsRecycler;

    SessionManager sessionManager;
    DBManager dbManager;
    Restaurant restaurant;
    ArrayList<String> restaurantCategoriesArrayList;
    ArrayList<RestaurantItem> restaurantMenuRecommendations;
    JSONArray restaurantCategoriesJSONArray;
    JSONArray restaurantMenu;
    ArrayList<RestaurantItem> restaurantItems = new ArrayList<RestaurantItem>();

    private TextView txtViewCount;

    ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;
    RatingSubmittedReceiver ratingSubmittedReceiver;

    CartManager cartManager;

    ProgressDialog progressDialog;


    String itemNames[];//array containing search suggestions

    boolean acceptsOnlinePayment;




    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(RestaurantMenu.this);
        getMenuInflater().inflate(R.menu.menu_only_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        /*final View cart = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) cart.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(RestaurantMenu.this);
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    public void updateGoToCartText(){
        String cartButtonText = "";

        if(AppController.getInstance().getCartManager().getCart().isCartEmpty()){
            goToCart.setVisibility(View.GONE);
        }else{
            goToCart.setVisibility(View.VISIBLE);
            cartButtonText = "  Cart  |  ";
            if(AppController.getInstance().getCartManager().getCart().getCartItems().size()==1){
                cartButtonText = cartButtonText.concat("1 Item  |  ");
                cartButtonText = cartButtonText.concat("\u20B9");
                cartButtonText = cartButtonText.concat(" ").concat(String.valueOf(AppController.getInstance().getCartManager().getCart().getTotalBeforeDiscount()));

            }else{
                cartButtonText = cartButtonText.concat(String.valueOf(AppController.getInstance().getCartManager().getCart().getCartItems().size()));
                cartButtonText = cartButtonText.concat(" Items  |  ");
                cartButtonText = cartButtonText.concat("\u20B9");
                cartButtonText = cartButtonText.concat(" ").concat(String.valueOf(AppController.getInstance().getCartManager().getCart().getTotalBeforeDiscount()));
            }
        }

        goToCart.setText(cartButtonText);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    String restaurantNameInLowerCaseWithoutSpaces;
    String restaurantMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu_v4);
        sessionManager = new SessionManager(RestaurantMenu.this);
        dbManager = new DBManager(RestaurantMenu.this);
        String key=sessionManager.getfirebaseKey();
        //Log.e("firebase",key);
        while (key.equals("0")){

        }
        //Initialising Views.
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.restaurant_menu_coordinator_layout);
        mainActivitySlider = (TabLayout) findViewById(R.id.restaurant_menu_activity_slider);
        RestaurantMenuViewPager = (ViewPager) findViewById(R.id.restaurant_menu_activity_view_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //fabCart = (FloatingActionButton)findViewById(R.id.fab_cart);
        //fabCart.setVisibility(View.GONE);

        goToCart = (Button)findViewById(R.id.go_to_cart);
        updateGoToCartText();

        //restaurant info views
        restaurantImageView = (ImageView) findViewById(R.id.restaurant_image_view);
        restaurantRatingView = (SimpleRatingBar) findViewById(R.id.restaurant_rating_view);
        restaurantStatusView = (TextView) findViewById(R.id.restaurant_status_view);
        restaurantMinOrderAmountView = (TextView) findViewById(R.id.restaurant_minOrderAmount_view);
        restaurantDeliveryChargeView = (TextView) findViewById(R.id.restaurant_deliveryCharge_view);
        restaurantFreeDeliveryView = (LinearLayout)findViewById(R.id.free_delivery_icon) ;
        restaurantOnlinePaymentView = (LinearLayout)findViewById(R.id.online_payment_icon);
        restaurantSpecialityView = (TextView) findViewById(R.id.restaurant_speciality_view);
        restaurantOfferContainer = (LinearLayout) findViewById(R.id.restaurant_offer_container);
        restaurantOfferView = (TextView) findViewById(R.id.restaurant_offer_view);
        restaurantMessageContainer = (RelativeLayout)findViewById(R.id.restaurant_message_container);
        restaurantMessageView = (TextView)findViewById(R.id.restaurant_message);

        //Initialising restaurant review views.
        restaurantReviewsRecycler = (RecyclerView)findViewById(R.id.restaurant_reviews_recycler);
        userReviewInput = (EditText)findViewById(R.id.user_review_input);
        submitUserReviewButton = (ImageButton) findViewById(R.id.submit_review_button);
        reviewsSlider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //initialising restaurant recommendations views
        /*restaurantTopRatedItemsRecycler = (RecyclerView)findViewById(R.id.restaurant_menu_top_rated_items_recycler);
        restaurantTopOrderedItemsRecycler = (RecyclerView)findViewById(R.id.restaurant_menu_most_ordered_items_recycler);*/
        restaurantMenuRecommendationsContainer = (CardView)findViewById(R.id.restaurant_menu_recommended_items_container);
        restaurantMenuRecommendationsRecycler = (RecyclerView)findViewById(R.id.restaurant_menu_recommended_items_recycler);

        userReviewInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitUserReview();
                    handled = true;
                }
                return handled;
            }
        });

        submitUserReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userReviewInput.getText().toString().equals("")){
                    submitUserReview();
                }else{
                    Toast.makeText(RestaurantMenu.this,"Enter your review first !",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //setting up toolbars.
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        acceptsOnlinePayment = i.getBooleanExtra("online",true);
        restaurant = (Restaurant) i.getSerializableExtra("restaurant");

        updateRestaurantInfoViews();

        /*getSupportActionBar().setTitle(restaurant.getName());
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        toolbar.post(new Runnable()
        {
            @Override
            public void run()
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(restaurant.getName());
                getSupportActionBar().setSubtitle(restaurant.getAddress());
            }
        });

        //setting up receiver for updating cart count badge.
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);



        //setting up receiver for rating submitted succesfully.
        ratingSubmittedReceiver = new RatingSubmittedReceiver();
        IntentFilter ratingSubmittedFilter = new IntentFilter("Rating Submitted Succesfully");
        this.registerReceiver(ratingSubmittedReceiver, ratingSubmittedFilter);

        setSearch();

        restaurantNameInLowerCaseWithoutSpaces = restaurant.getName().toLowerCase().replaceAll("\\s+", "");
        setUpTabbedView(restaurantNameInLowerCaseWithoutSpaces, restaurant);

        getRestaurantReviews();

        /*RecommendationManager recommendationManager = new RecommendationManager(RestaurantMenu.this,restaurantTopRatedItemsRecycler,restaurantTopOrderedItemsRecycler,restaurant.getCode(),null,Config.KEY_GET_RECOMMENDATION_FOR_RESTAURANT);
        recommendationManager.getRecommendations();*/


        //setupFab();



    }

    public void updateRestaurantInfoViews(){
        List<Transformation> transformations = new ArrayList<>();
        Context context = RestaurantMenu.this;

        transformations.add(new GrayscaleTransformation(Picasso.with(context)));
        //transformations.add(new BlurTransformation(context));

        String url = Config.URL_GET_IMAGE_ASSETS + "Restaurants/".concat(restaurant.getImageResourceId());

        if(acceptsOnlinePayment){
            Log.d(TAG,"TOING");
            restaurantOnlinePaymentView.setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG,"Boing");
            restaurantOnlinePaymentView.setVisibility(View.GONE);
        }

        if (restaurant.getHasNewOffer()==0) {
            if(restaurant.getOffer()==0){
                restaurantOfferContainer.setVisibility(View.INVISIBLE);
                restaurantOfferView.setVisibility(View.INVISIBLE);
            }else{
                restaurantOfferContainer.setVisibility(View.VISIBLE);
                restaurantOfferView.setVisibility(View.VISIBLE);
                restaurantOfferView.setText(String.valueOf(restaurant.getOffer()).concat("%") );
            }

        } else {
            restaurantOfferView.setText(String.valueOf(restaurant.getNewOffer()).concat("%") );
            restaurantOfferContainer.setVisibility(View.VISIBLE);
            restaurantOfferView.setVisibility(View.VISIBLE);
        }

        restaurantRatingView.setIndicator(true);

        restaurantRatingView.setRating(restaurant.getRating());
        if (restaurant.getStatus().equals("open")) {
            restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorGreen));
            restaurantStatusView.setText("OPEN");

            restaurantSpecialityView.setTextColor(ContextCompat.getColor(context, R.color.colorPurple));
            restaurantRatingView.setFillColor(context.getResources().getColor(R.color.golden_stars));
            restaurantOfferContainer.setBackgroundResource(R.drawable.triangular_background);

            Picasso.with(context).load(url).placeholder(R.drawable.place_holder_color).fit().into(restaurantImageView);
        } else {
            restaurantStatusView.setTextColor(context.getResources().getColor(R.color.colorPinkDark));
            restaurantStatusView.setText("CLOSED");

            restaurantOfferContainer.setBackgroundResource(R.drawable.triangular_background_off);
            restaurantRatingView.setFillColor(context.getResources().getColor(R.color.colorGray));
            restaurantSpecialityView.setTextColor(ContextCompat.getColor(context, R.color.colorGray));

            Picasso.with(context).load(url).transform(transformations).placeholder(R.drawable.place_holder_black_).fit().into(restaurantImageView);
        }

        restaurantSpecialityView.setText("Speciality : " + restaurant.getSpeciality());

        restaurantMinOrderAmountView.setText("Minimum Order Amount : " + "\u20B9" + restaurant.getMinOrderAmount());
        if (restaurant.getDeliveryCharges()==0 ) {
            //restaurantDeliveryChargeView.setCompoundDrawables(null,null,context.getDrawable(R.drawable.ic_free_delivery),null);
            restaurantDeliveryChargeView.setVisibility(View.GONE);
            restaurantFreeDeliveryView.setVisibility(View.VISIBLE);

        } else {
            restaurantDeliveryChargeView.setVisibility(View.VISIBLE);
            restaurantFreeDeliveryView.setVisibility(View.GONE);
            String deliveryChargeString = "Delivery Charges : " + String.valueOf(restaurant.getDeliveryCharges());
            deliveryChargeString = deliveryChargeString.concat(" (Below orders of Rs. "+String.valueOf(restaurant.getDeliveryChargeSlab())+")");
            restaurantDeliveryChargeView.setText(deliveryChargeString);
            restaurantDeliveryChargeView.setCompoundDrawables(null, null, null, null);
        }

        if(restaurant.getMessageStatus()!=0){
            restaurantMessageContainer.setVisibility(View.VISIBLE);
            showMessage();
        }else{
            restaurantMessageContainer.setVisibility(View.GONE);
        }




    }

    public void showMessage(){
        restaurantMessage = restaurant.getMessage();
        if(!restaurantMessage.equals("")){
            /*Snackbar snackbar =  Snackbar.make(coordinatorLayout,restaurantMessage,Snackbar.LENGTH_INDEFINITE);
            View snackbarLayout = snackbar.getView();
            TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
            Drawable warningDrwable = getResources().getDrawable(R.drawable.warning,null);
            warningDrwable.setBounds(5,5,15,15);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.warning_drawable,0,0,0);
            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.sheet_item_spacing));
            snackbar.show();*/
            restaurantMessageView.setText(restaurantMessage);
        }
    }

    public void setUpTabbedView(final String restaurantNameInLowerCaseWithoutSpaces, final Restaurant restaurant) {


        progressDialog = new ProgressDialog(RestaurantMenu.this);
        progressDialog.setMessage("Downloading Menu ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RESTAURANT_MENU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                try {
                    JSONObject responseObj = new JSONObject(response);

                    JSONObject resultObj = responseObj.getJSONObject("result");
                    restaurantCategoriesJSONArray = resultObj.getJSONArray("categories");
                    restaurantMenu = resultObj.getJSONArray("menu");

                    restaurantCategoriesArrayList = new ArrayList<>();
                    restaurantMenuRecommendations = new ArrayList<>();


                    //converting jsonArray to ArrayList
                    if (restaurantCategoriesJSONArray != null) {
                        for (int i = 0; i < restaurantCategoriesJSONArray.length(); i++) {
                            JSONObject categoryObject = restaurantCategoriesJSONArray.getJSONObject(i);
                            String categoryName = categoryObject.getString("category_name");
                            restaurantCategoriesArrayList.add(categoryName);

                            //Code for downloading each item of Restaurant.{{{{{{{{{{{{{
                            JSONObject restaurantMenuCategory = restaurantMenu.getJSONObject(i);
                            JSONArray MenuItemArray = restaurantMenuCategory.getJSONArray("items");
                            for (int k = 0; k < MenuItemArray.length(); k++) {

                                JSONObject itemObject;

                                try {
                                    itemObject = MenuItemArray.getJSONObject(k);
                                    String itemName = itemObject.getString("item_name");
                                    double itemPrice = Double.parseDouble(itemObject.getString("item_price")) ;
                                    String itemCode = itemObject.getString("item_code");
                                    Double itemOffer;
                                    if(itemObject.getString("offer_price")!=null){
                                        itemOffer = Double.parseDouble(itemObject.getString("offer_price"));
                                    }else{
                                        itemOffer = 0.0;
                                    }
                                    String itemRestaurantCategory =  itemObject.getString("item_restaurant_category");
                                    float itemRating = Float.parseFloat( itemObject.getString("item_rating"));
                                    int itemRatingCount = Integer.parseInt( itemObject.getString("item_rating_count"));
                                    String itemCurrentRatingByUser = itemObject.getString("item_current_rating_by_user");
                                    int itemOrderCount = Integer.parseInt( itemObject.getString("item_order_count"));
                                    Boolean itemIsFavourite = itemObject.getBoolean("item_is_favourite");
                                    boolean itemIsRecommended = itemObject.getBoolean("item_is_recommended");
                                    String itemImageResource = itemObject.getString("item_image_resource");
                                    Boolean itemIsVeg = itemObject.getBoolean("item_is_veg");
                                    Boolean hasOffer=false;
                                    if(itemOffer>0){

                                        hasOffer=true;
                                        double temp=itemOffer;
                                        itemOffer=itemPrice;
                                        itemPrice=temp;
                                    }
                                    RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);
                                    restaurantItems.add(restaurantItem);
                                    if(restaurantItem.isRecommended()){
                                        restaurantMenuRecommendations.add(restaurantItem);
                                    }
                                } catch (JSONException e) {
                                   // Log.d(TAG, e.getMessage());
                                }
                            }
                        }
                    }
                    //Code for Suggesting Search{{ just below 2 lines.
                    String[] arr = restaurantCategoriesArrayList.toArray(new String[restaurantCategoriesArrayList.size()]);
                    SetupForSearch(restaurantItems, arr, restaurantCategoriesArrayList.size());

                    //Setting up restaurant menu recommendation views.
                    if(restaurantMenuRecommendations.size()==0){
                        restaurantMenuRecommendationsContainer.setVisibility(View.GONE);
                    }else{
                        RestaurantMenuRecommendedItemsAdapter restaurantMenuRecommendedItemsAdapter = new RestaurantMenuRecommendedItemsAdapter(restaurantMenuRecommendations,RestaurantMenu.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestaurantMenu.this,LinearLayoutManager.HORIZONTAL,false);
                        restaurantMenuRecommendationsRecycler.setAdapter(restaurantMenuRecommendedItemsAdapter);
                        restaurantMenuRecommendationsRecycler.setLayoutManager(linearLayoutManager);
                    }


                    RestaurantMenuFragmentPagerAdapter restaurantMenuFragmentPagerAdapter = new RestaurantMenuFragmentPagerAdapter(getSupportFragmentManager(), restaurantCategoriesArrayList.size(), restaurantCategoriesArrayList, restaurantMenu, restaurant);
                    RestaurantMenuViewPager.setAdapter(restaurantMenuFragmentPagerAdapter);

                    mainActivitySlider.setupWithViewPager(RestaurantMenuViewPager);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage() );
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 //Log.e(TAG,error.getMessage());
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Intent intent = new Intent(RestaurantMenu.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
               // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", sessionManager.getSelectedCity());
                params.put("restaurant_name", restaurantNameInLowerCaseWithoutSpaces);
                params.put("uid",AppController.getInstance().getDbManager().getUserUid());

                Log.d(TAG,params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);


    }
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
         else {
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
        /*switch (v.getId()) {
            case R.id.fab_sheet_item_go_to_cart:
                AppController.getInstance().getCartManager().showCart(RestaurantMenu.this);
                break;
            case R.id.fab_sheet_item_favourites:
                Intent intent = new Intent(RestaurantMenu.this, FavouriteItemActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_sheet_item_profile:
                Intent profileIntent = new Intent(RestaurantMenu.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.fab_sheet_item_share: {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here we can write a description about our app and also provide playstore link to download app.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            }
            case R.id.fab_sheet_item_previous_orders:
                Intent intent1 = new Intent(RestaurantMenu.this, PreviousOrdersActivity.class);
                startActivity(intent1);
                break;
        }*/
        //materialSheetFab.hideSheet();
    }


    public void setSearch() {


        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchManager searchManager = new SearchManager(RestaurantMenu.this,query,AppController.getInstance().getDbManager().getUserUid(),AppController.getInstance().getSessionManager().getSelectedCity(),true,restaurant.getCode(),restaurant.getName());
                searchManager.getSearchResults();
                return true;


            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });



        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                searchView.setVisibility(View.VISIBLE);


                searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String query = parent.getAdapter().getItem(position).toString();

                        if(query!=null){
                            SearchManager searchManager = new SearchManager(RestaurantMenu.this,query,AppController.getInstance().getDbManager().getUserUid(),AppController.getInstance().getSessionManager().getSelectedCity(),true,restaurant.getCode(),restaurant.getName());
                            searchManager.getSearchResults();
                        }

                        //Toast.makeText(RestaurantMenu.this,"yipee",Toast.LENGTH_SHORT).show();

                        Log.d(TAG,query);
                    }
                });

            }            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    public void SetupForSearch(ArrayList<RestaurantItem> restaurantItems, String[] categories, int noOfCategories) {
        int itemsSize = restaurantItems.size();
        int size = restaurantItems.size() + noOfCategories;
        itemNames = new String[size];

        for (int i = 0; i < restaurantItems.size(); i++) {
            RestaurantItem r = restaurantItems.get(i);
            itemNames[i] = r.getName();
        }
        for (int i = 0; i < noOfCategories; i++) {
            itemNames[i + itemsSize] = categories[i];
        }


        searchView.setSuggestions(itemNames);

    }

    public int isItem(ArrayList<RestaurantItem> list, String query) {
        int size = list.size();
        int notFound = -1;
        for (int i = 0; i < size; i++) {
            if (query.equals(list.get(i).getName().toLowerCase()))
                return (i);
        }
        return notFound;

    }

    public int isCategory(ArrayList<String> list, String query) {
        int size = list.size();
        int notFound = -1;
        for (int i = 0; i < size; i++) {
            if (query.equals(list.get(i).toLowerCase()))
                return (i);
        }
        return notFound;
    }

    public void goToCart(View fab){
        Intent intent = new Intent(RestaurantMenu.this,CartActivity.class);
        startActivity(intent);
    }


    private class ItemAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            updateGoToCartText();
            /*txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Item Added to Cart!", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppController.getInstance().getCartManager().deleteLastAddedItem();
                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Item removed from Cart.", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();*/
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            updateGoToCartText();
            /*txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Item Deleted From Cart.", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class RatingSubmittedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGoToCartText();
            /*Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Rating Submitted Successfully", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Log.d(TAG,"Rating submitted successfully broadcast received");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateGoToCartText();
        setUpTabbedView(restaurantNameInLowerCaseWithoutSpaces,restaurant);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getRestaurantReviews(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RESTAURANT_REVIEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                try {
                    JSONObject responseObj = new JSONObject(response);

                    if(!responseObj.getBoolean("error")){
                        JSONArray reviewsObj = responseObj.getJSONArray("reviews");

                        RestaurantReviewsAdapter restaurantReviewsAdapter = new RestaurantReviewsAdapter(reviewsObj);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestaurantMenu.this,LinearLayoutManager.VERTICAL,false);
                        restaurantReviewsRecycler.setLayoutManager(linearLayoutManager);
                        restaurantReviewsRecycler.setAdapter(restaurantReviewsAdapter);

                    }else{
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage() );
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG,error.getMessage());
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Intent intent = new Intent(RestaurantMenu.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", sessionManager.getSelectedCity());
                params.put("restaurant_code", restaurant.getCode());

                Log.d(TAG,params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void submitUserReview(){
        progressDialog = new ProgressDialog(RestaurantMenu.this);
        progressDialog.setMessage("Submitting Review...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_SUBMIT_USER_REVIEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                userReviewInput.setText("");
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                try {
                    JSONObject responseObj = new JSONObject(response);

                    if(!responseObj.getBoolean("error")){
                        getRestaurantReviews();
                    }else{
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage() );
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG,error.getMessage());
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Intent intent = new Intent(RestaurantMenu.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", sessionManager.getSelectedCity());
                params.put("restaurant_code", restaurant.getCode());
                params.put("review", userReviewInput.getText().toString());
                params.put("UID", AppController.getInstance().getDbManager().getUserUid());

                Log.d(TAG,params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void collapseReviews(View v){
        reviewsSlider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }
}
