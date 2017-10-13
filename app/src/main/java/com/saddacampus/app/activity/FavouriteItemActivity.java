package com.saddacampus.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.FavouriteItemListAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.helper.CartManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class FavouriteItemActivity extends AppCompatActivity {

    private String TAG = FavouriteItemActivity.class.getSimpleName();

    Toolbar toolbar;
    CartManager cartManager;
    TextView txtViewCount;
    RecyclerView favouriteItemsRecycler;

    ItemAddedReceiver itemAddedReceiver;
    ItemDeletedReceiver itemDeletedReceiver;
    LinearLayout noFavourites;

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    private Paint p = new Paint();



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(FavouriteItemActivity.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View cart = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) cart.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getCartManager().showCart(FavouriteItemActivity.this);
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
        setContentView(R.layout.activity_item_favourite);

        favouriteItemsRecycler = (RecyclerView)findViewById(R.id.favourite_recyclerView);

        //progressBar = (ProgressBar)findViewById(R.id.favourite_items_progress_bar);

        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

        //setting up receiver for updating cart count badge.
        itemAddedReceiver = new ItemAddedReceiver();
        itemDeletedReceiver = new ItemDeletedReceiver();
        IntentFilter itemAddedFilter = new IntentFilter("Item Added To Cart");
        this.registerReceiver(itemAddedReceiver, itemAddedFilter);
        IntentFilter itemDeletedFilter = new IntentFilter("Item Deleted From Cart");
        this.registerReceiver(itemDeletedReceiver, itemDeletedFilter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favourite Items");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        noFavourites = (LinearLayout)findViewById(R.id.no_favourite);
        noFavourites.setVisibility(View.GONE);

        /*ChasingDots chasingDots = new ChasingDots();
        chasingDots.setColor(getResources().getColor(R.color.colorAccent,null));

        progressBar.setIndeterminateDrawable(chasingDots);
        progressBar.setVisibility(View.GONE);*/



        getUserFavourites();



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavouriteItemActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getUserFavourites();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserFavourites(){




        progressDialog = new ProgressDialog(FavouriteItemActivity.this);
        progressDialog.setMessage("Getting your favourite dishes...");
        progressDialog.setCancelable(false);
        //progressDialog.setIndeterminateDrawable(chasingDots);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_USER_FAVOURITES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                final ArrayList<RestaurantItem> favouriteItems = new ArrayList<>();
                Log.d(TAG,response);
                try{
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray resultArray = responseObject.getJSONArray("result");
                    for(int i = 0 ; i<resultArray.length(); i++){
                        JSONObject itemObject = resultArray.getJSONObject(i);
                        JSONObject restaurantObject = itemObject.getJSONObject("restaurant");

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
                        Restaurant restaurant = new Restaurant(name,contact,acceptsOnlinePayment, address, code, imageResourceId, minOrderAmount, timing, deliveryCharges,deliveryChargeSlab, newOffer,hasNewOffer, offer, status, speciality, rating,messageStatus,message);

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
                        Boolean itemIsVeg = itemObject.getBoolean("item_is_veg");
                        boolean itemIsRecommended = itemObject.getBoolean("item_is_recommended");
                        String itemImageResource = itemObject.getString("item_image_resource");
                        Boolean hasOffer=false;
                        if(itemOffer>0){

                            hasOffer=true;
                            double temp=itemOffer;
                            itemOffer=itemPrice;
                            itemPrice=temp;
                        }
                        RestaurantItem restaurantItem = new RestaurantItem(itemName, itemCode, itemRestaurantCategory,itemIsRecommended,itemImageResource, restaurant, itemPrice, itemIsVeg,itemIsFavourite,itemCurrentRatingByUser,itemRating,itemRatingCount,itemOrderCount,itemOffer,hasOffer);

                        favouriteItems.add(restaurantItem);
                        Log.d(TAG,"item Added");
                    }


                    //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
                    LinearLayoutManager la = new LinearLayoutManager(FavouriteItemActivity.this, LinearLayoutManager.VERTICAL, false);
                    favouriteItemsRecycler.setLayoutManager(la);
                    favouriteItemsRecycler.setHasFixedSize(true);

                    if(favouriteItems.size()==0){
                        noFavourites.setVisibility(View.VISIBLE);
                    }

                    final FavouriteItemListAdapter adapter = new FavouriteItemListAdapter(favouriteItems);

                    favouriteItemsRecycler.setAdapter(adapter);

                    //progressBar.setVisibility(View.GONE);

                    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            final int position = viewHolder.getAdapterPosition();
                            adapter.removeItem(position);
                        }

                        @Override
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                            Bitmap icon;
                            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                                View itemView = viewHolder.itemView;
                                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                                float width = height / 3;

                                if(dX > 0){
                                    p.setColor(Color.parseColor("#D32F2F"));
                                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                                    c.drawRect(background,p);
                                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_red_square);
                                    RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                                    c.drawBitmap(icon,null,icon_dest,p);
                                } else {
                                    p.setColor(Color.parseColor("#D32F2F"));
                                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                                    c.drawRect(background,p);
                                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_red_square);
                                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                                    c.drawBitmap(icon,null,icon_dest,p);
                                }
                            }
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    };

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

                    itemTouchHelper.attachToRecyclerView(favouriteItemsRecycler);

                }catch (JSONException e){
                    Log.d(TAG, e.getMessage());
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
                Intent intent = new Intent(FavouriteItemActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);
               /* Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", AppController.getInstance().getSessionManager().getSelectedCity());
                params.put("UID", AppController.getInstance().getDbManager().getUserUid());

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class ItemAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }

    private class ItemDeletedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));

            Log.d(TAG,"Cart Count Updated broadcast received");
        }
    }



}

