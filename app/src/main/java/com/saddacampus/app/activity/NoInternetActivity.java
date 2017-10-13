package com.saddacampus.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.saddacampus.app.R;

/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class NoInternetActivity extends AppCompatActivity {

    private String TAG = NoInternetActivity.class.getSimpleName();
    int activityKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);

    }


    public void refresh(View view) {


        finish();
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}

