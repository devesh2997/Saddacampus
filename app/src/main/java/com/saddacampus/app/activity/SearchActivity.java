package com.saddacampus.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.saddacampus.app.R;
import com.saddacampus.app.app.Adapter.SearchActivityFragmentPagerAdapter;
import com.saddacampus.app.app.DataObjects.SearchResults;

/**
 * Created by Devesh Anand on 29-06-2017.
 */

public class SearchActivity extends AppCompatActivity {

    private static String TAG = SearchActivity.class.getSimpleName();

    Toolbar searchActivityToolbar;
    TabLayout searchActivityTabLayout;
    ViewPager searchActivityViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);


        searchActivityToolbar = (Toolbar)findViewById(R.id.search_activity_toolbar);
        searchActivityTabLayout = (TabLayout)findViewById(R.id.search_activity_slider);
        searchActivityViewPager = (ViewPager)findViewById(R.id.search_activity_view_pager);
        setSupportActionBar(searchActivityToolbar);

        getSupportActionBar().setSubtitle("Search");

        Intent intent = getIntent();
        String searchResultsString = intent.getStringExtra("search_results");
        Gson gson = new Gson();
        SearchResults searchResults = gson.fromJson(searchResultsString, SearchResults.class);

        getSupportActionBar().setTitle(intent.getStringExtra("query").toUpperCase());

        Boolean isRestaurantActivity = intent.getBooleanExtra("is_restaurant_activity",false);
        String currentRestaurantCode = intent.getStringExtra("current_restaurant_code");
        String currentRestaurantName = intent.getStringExtra("current_restaurant_name");

        SearchActivityFragmentPagerAdapter searchActivityFragmentPagerAdapter= new SearchActivityFragmentPagerAdapter(getSupportFragmentManager(),2,searchResults,isRestaurantActivity,currentRestaurantCode,currentRestaurantName);
        searchActivityViewPager.setAdapter(searchActivityFragmentPagerAdapter);
        searchActivityTabLayout.setupWithViewPager(searchActivityViewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);

    }
}
