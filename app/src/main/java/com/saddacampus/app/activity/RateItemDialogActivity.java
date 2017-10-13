package com.saddacampus.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.saddacampus.app.R;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devesh Anand on 27-06-2017.
 */

public class RateItemDialogActivity extends AppCompatActivity {

    TextView ratingSuggestionView;
    SimpleRatingBar ratingBarDialogView;
    Button cancelDialogButton;
    Button submitRatingButton;
    ViewSwitcher dialogViewSwitcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_item_dialog);


        Intent intent = getIntent();
        final String itemCode = intent.getStringExtra("item_code");
        final String itemName = intent.getStringExtra("item_name");

        setTitle(itemName);

        ratingSuggestionView = (TextView)findViewById(R.id.dialog_rating_suggestion);
        ratingSuggestionView.setText("");
        ratingBarDialogView = (SimpleRatingBar) findViewById(R.id.dialog_rating_bar);
        submitRatingButton = (Button)findViewById(R.id.submit_rating);
        cancelDialogButton = (Button)findViewById(R.id.cancel_dialog);
        dialogViewSwitcher = (ViewSwitcher)findViewById(R.id.dialog_view_switcher);


        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating(itemCode,(int)ratingBarDialogView.getRating(), AppController.getInstance().getDbManager().getUserUid(),AppController.getInstance().getSessionManager().getSelectedCity());

            }
        });

        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ratingBarDialogView.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                int ratingInt = (int)rating;
                switch (ratingInt){
                    case 1:
                        ratingSuggestionView.setText("Very Bad");
                        break;
                    case 2:
                        ratingSuggestionView.setText("Bad");
                        break;
                    case 3:
                        ratingSuggestionView.setText("Okay");
                        break;
                    case 4:
                        ratingSuggestionView.setText("Good");
                        break;
                    case 5:
                        ratingSuggestionView.setText("Excellent");
                        break;
                }
            }
        });

    }

    public void submitRating(final String itemCode, final int ratingSubmitted, final String UUID, final String selectedCity){
        dialogViewSwitcher.showNext();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_SET_ITEM_RATING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("error")){
                        //Toast.makeText(RateItemDialogActivity.this,"Unknown Error in submitting Rating.",Toast.LENGTH_SHORT).show();
                        Intent goToPopupIntent = new Intent();
                        setResult(0,goToPopupIntent);
                        finish();
                    }else{

                        Intent broadcastIntent=new Intent();
                        broadcastIntent.setAction("Rating Submitted Succesfully");
                        RateItemDialogActivity.this.sendBroadcast(broadcastIntent);

                        Intent goToPopupIntent = new Intent();
                        goToPopupIntent.putExtra("submitted_rating",String.valueOf(ratingSubmitted));
                        setResult(1,goToPopupIntent);
                        finish();
                    }
                }catch (JSONException e){
                    //Log.d("RateItemDialogFragment",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(RateItemDialogActivity.this,NoInternetActivity.class);
                startActivityForResult(intent,0);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("item_code", itemCode);
                params.put("item_submitted_rating", String.valueOf(ratingSubmitted));
                params.put("UID", UUID);
                params.put("city",selectedCity);

                Log.d("ItemPopupWindow",params.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
