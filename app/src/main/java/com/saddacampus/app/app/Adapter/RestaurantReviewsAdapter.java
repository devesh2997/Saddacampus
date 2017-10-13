package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import com.saddacampus.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by anand on 02-10-2017.
 */

public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.RestaurantReviewViewHolder>{
    private static final String TAG = RestaurantReviewsAdapter.class.getSimpleName();

    private JSONArray reviewsJsonArray;

    private Context context;

    public RestaurantReviewsAdapter(JSONArray reviewsJsonArray) {
        this.reviewsJsonArray = reviewsJsonArray;
    }

    @Override
    public RestaurantReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        int layoutForCartItem = R.layout.restaurant_review_container;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForCartItem, parent, shouldAttachToParentImmediately);
        RestaurantReviewViewHolder restaurantReviewViewHolder = new RestaurantReviewViewHolder(view);

        return  restaurantReviewViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviewsJsonArray.length();
    }

    public class RestaurantReviewViewHolder extends RecyclerView.ViewHolder{

        CircleImageView reviewerDp;
        TextView reviewerName;
        TextView reviewTime;
        TextView reviewText;

        public RestaurantReviewViewHolder (View restaurantReviewView){
            super(restaurantReviewView);

            reviewerDp = (CircleImageView)restaurantReviewView.findViewById(R.id.reviewer_dp);
            reviewerName = (TextView)restaurantReviewView.findViewById(R.id.reviewer_name);
            reviewTime = (TextView)restaurantReviewView.findViewById(R.id.review_time);
            reviewText = (TextView)restaurantReviewView.findViewById(R.id.review_text);
        }

        void bind (final int listIndex){
            try{
                JSONObject reviewObject = reviewsJsonArray.getJSONObject(listIndex);
                JSONObject userObject = reviewObject.getJSONObject("user");
                reviewerName.setText(userObject.getString("name"));
                reviewTime.setText(reviewObject.getString("review_timestamp"));
                reviewText.setText(reviewObject.getString("review"));
                if(reviewObject.getString("user_auth").equals("facebook")){
                    Picasso.with(context)
                            .load("https://graph.facebook.com/" + userObject.getString("UID")+ "/picture?type=small")
                            .into(reviewerDp);
                }

            }catch (JSONException e){
                Log.d(TAG,e.getMessage());
            }

        }
    }
}
