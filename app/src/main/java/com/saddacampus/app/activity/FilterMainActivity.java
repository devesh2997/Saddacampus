package com.saddacampus.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.saddacampus.app.R;
import com.saddacampus.app.helper.SessionManager;

/**
 * Created by Shubham Vishwakarma on 26-06-2017.
 */

public class FilterMainActivity extends AppCompatActivity {

    private String TAG = FilterMainActivity.class.getSimpleName();

    int key = 0;

    boolean isoffer;
    boolean isNoMinDelivery;
    boolean freeDelivery;

    Toolbar toolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);



        SessionManager sessionManager = new SessionManager(FilterMainActivity.this);
        String keyy=sessionManager.getfirebaseKey();
        //Log.e("firebase",key);
        while (keyy.equals("0")){

        }


        RadioGroup sortBy = (RadioGroup) findViewById(R.id.sortBy);
        sortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = group.getCheckedRadioButtonId();
                switch (radioButtonID) {
                    case R.id.deliveryPrice: {
                        key = 1;
                    }
                    break;  //optional
                    case R.id.bestOffers: {
                        key = 2;
                    }
                    break;
                    case R.id.ratings: {
                        key = 3;
                    }
                    break;
                    case R.id.minOrderAmmount: {
                        key = 4;
                    }
                    break;

                }
                Log.d(TAG, "  id = " + radioButtonID);
            }
        });


        Button button = (Button) findViewById(R.id.applySort);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent = new Intent(FilterMainActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("key", key);
                bundle.putBoolean("isOffer",isoffer);
                bundle.putBoolean("isNoMinDelivery",isNoMinDelivery);
                bundle.putBoolean("freeDelivery",freeDelivery);
                intent.putExtras(bundle);
                FilterMainActivity.this.startActivity(intent);

            }
        });


    }

    public void selectItem(View view){

        boolean checked = ((CheckBox)(view)).isChecked();
        switch (view.getId()){
            case R.id.filter_offer:
            {
                if(checked)
                    isoffer=true;
                else
                    isoffer=false;
                break;
            }
            case R.id.filter_free_delivery:
            {
                if(checked)
                    freeDelivery=true;
                else
                    freeDelivery=false;

                break;
            }
            case R.id.filter_no_min_delivery_amount:
            {
                if(checked)
                    isNoMinDelivery=true;
                else
                    isNoMinDelivery=false;
                break;
            }

        }

    }

}

