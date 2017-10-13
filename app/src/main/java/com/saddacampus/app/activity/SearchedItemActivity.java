package com.saddacampus.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.DataObjects.CartItem;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.app.DataObjects.RestaurantItem;
import com.saddacampus.app.helper.CartManager;

/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class SearchedItemActivity extends AppCompatActivity {


    CartManager cartManager;
    Restaurant restaurant;
    private Toolbar toolbar;
    TextView txtViewCount;
    TextView price;
    TextView total;
    TextView NoOfItems;
    TextView itemName;
    TextView restaurantName;
    Button plus;
    Button minus;
    Button add;
    ImageView image;
    TextView isVegText;
    TextView isOtherRestext;
    Button moveToOtherRes;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        cartManager = new CartManager(SearchedItemActivity.this);
        getMenuInflater().inflate(R.menu.menu_just_cart, menu);
        final View notifications = menu.findItem(R.id.cart_icon_badge_action).getActionView();

        txtViewCount = (TextView) notifications.findViewById(R.id.cart_count_badge);
        txtViewCount.setText(String.valueOf(AppController.getInstance().getCartManager().getCartCount()));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    TODO
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
        setContentView(R.layout.activity_item_searched);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        // restaurant = (Restaurant) bundle.getSerializable("restaurant");
        final RestaurantItem item = (RestaurantItem) bundle.getSerializable("value");
        int  isOtherRes = bundle.getInt("isOtherRes");
        Log.d("SearchedITEM"," " + isOtherRes);
        getSupportActionBar().setTitle(item.getCategory());

        final Restaurant restaurant = item.getItemRestaurant();


        isVegText = (TextView) findViewById(R.id.isVegText);
        restaurantName = (TextView) findViewById(R.id.restaurant_name);
        itemName = (TextView) findViewById(R.id.item_name);
        image = (ImageView) findViewById(R.id.isvegimage);
        price = (TextView) findViewById(R.id.price);
        total = (TextView) findViewById(R.id.totalPrice);
        NoOfItems = (TextView) findViewById(R.id.no_of_items);
        minus = (Button) findViewById(R.id.minus);
        plus = (Button) findViewById(R.id.plus);
        add = (Button) findViewById(R.id.add);
        isOtherRestext = (TextView)findViewById(R.id.isOtherRes);
        moveToOtherRes = (Button)findViewById(R.id.moveButton);
        moveToOtherRes.setText("Go to " + restaurant.getName());

        if(isOtherRes==0) {
            isOtherRestext.setVisibility(View.GONE);
            moveToOtherRes.setVisibility(View.GONE);
        }



        if (item.isVeg()) {
            image.setImageResource(R.drawable.veg_icon);
            isVegText.setText("Vegetarian");
        } else {
            image.setImageResource(R.drawable.nonvegicon);
            isVegText.setText("Non Vegetarian");
        }
        price.setText("Rs. " + item.getPrice());
        restaurantName.setText(item.getItemRestaurant().getName());
        itemName.setText(item.getName());


        updateMinusButtonView();

        moveToOtherRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchedItemActivity.this,RestaurantMenu.class);
                intent.putExtra("restaurant",restaurant);
                SearchedItemActivity.this.startActivity(intent);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NoOfItems.getText().equals("0")) {
                    NoOfItems.setText(String.valueOf(Integer.parseInt(NoOfItems.getText().toString()) - 1));
                    updateMinusButtonView();
                    updateTotal();
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoOfItems.setText(String.valueOf(Integer.parseInt(NoOfItems.getText().toString()) + 1));
                updateMinusButtonView();
                updateTotal();

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(item, Integer.parseInt(NoOfItems.getText().toString()));
                AppController.getInstance().getCartManager().addItemToCart(cartItem, SearchedItemActivity.this);


//                    RestaurantMenu restaurantMenu = new RestaurantMenu();
//                    restaurantMenu.updateCartCountBadge();
            }
        });
    }

    void updateMinusButtonView() {

        if (NoOfItems.getText().equals("0")) {
            minus.setVisibility(View.VISIBLE);
        } else {
            minus.setVisibility(View.VISIBLE);
        }
    }

    void updateTotal() {
        String totalString = "Rs. " + String.valueOf(Double.parseDouble(NoOfItems.getText().toString()) * Double.parseDouble(price.getText().toString().substring(4)));
        total.setText(totalString);
    }
}




