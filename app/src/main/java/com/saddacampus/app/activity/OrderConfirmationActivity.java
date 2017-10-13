package com.saddacampus.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.saddacampus.app.R;

/**
 * Created by Devesh Anand on 25-07-2017.
 */

public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = OrderConfirmationActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
    }
}
