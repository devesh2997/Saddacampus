package com.saddacampus.app.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saddacampus.app.R;
import com.saddacampus.app.activity.MainActivity;
import com.saddacampus.app.activity.NoInternetActivity;
import com.saddacampus.app.app.Adapter.RestaurantListAdapter;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Restaurant;
import com.saddacampus.app.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Devesh Anand on 09-06-2017.
 */

public class RestaurantsFragment extends Fragment {

    private static final String TAG = RestaurantsFragment.class.getSimpleName();

    private RestaurantListAdapter restaurantListAdapter;
    private RecyclerView restaurantsListRecycler;
    private SessionManager sessionManager;
    ProgressBar progressBar;

    ArrayList<Restaurant> restaurantArrayList=new ArrayList<>();

    LinearLayoutManager restaurantListLayoutManager;


    int key=0;

    boolean isoffer=false;
    boolean isNoMinDelivery=false;
    boolean freeDelivery=false;


    public RestaurantsFragment() {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants_list_fragment,container,false);

        sessionManager = new SessionManager(getApplicationContext());
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        restaurantsListRecycler = (RecyclerView)rootView.findViewById(R.id.restaurant_list_recycler);
        restaurantListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        getKey();
        loadData();




        return rootView;
    }


    public void getKey() {
        MainActivity activity = (MainActivity) getActivity();
        key = activity.myMethod();
        isoffer=activity.getIsOffer();
        freeDelivery=activity.getFreeDelivery();
        isNoMinDelivery=activity.getIsNoMinDelivery();
    }

    public void loadData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_RESTAURANTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, response);

                progressBar.setVisibility(View.GONE);

                if(AppController.getInstance().getDbManager().storeRestaurantsData(response)){
                    restaurantArrayList= AppController.getInstance().getDbManager().getRestaurantsData();

                    if(restaurantArrayList.size()>0)
                    {
                        switch (key) {
                            case 0: {
                                sortOpenClose(restaurantArrayList);
                            }
                            break;  //optional
                            case 1: {
                                sortByDeliveryPrice(restaurantArrayList);
                            }
                            break;
                            case 2: {
                                sortByOffers(restaurantArrayList);
                            }
                            break;
                            case 3: {
                                sortByRatings(restaurantArrayList);
                            }
                            break;
                            case 4: {
                                sortByMinOrderAmount(restaurantArrayList);
                            }

                        }
                        ArrayList<Restaurant> newList=new ArrayList<Restaurant>();
                        ListIterator listIterator = restaurantArrayList.listIterator();
                        for(int i=0;i<restaurantArrayList.size();i++){

                        Restaurant restaurant = restaurantArrayList.get(i);
                            if(isoffer){

                                int n = restaurant.getOffer();
                                int m = restaurant.getNewOffer();

                                if(n>0||m>0)
                                {
                                    newList.add(restaurant);
                                    restaurantArrayList.remove(i);
                                }

                            }
                            /*if(freeDelivery){

                            }
                            if(isNoMinDelivery){

                            }*/
                        }
                        for(int i=0;i<restaurantArrayList.size();i++){

                            Restaurant restaurant = restaurantArrayList.get(i);
                            if(isNoMinDelivery){

                                int n = Integer.parseInt(restaurant.getMinOrderAmount().trim());

                                if(n==0)
                                {
                                    newList.add(restaurant);
                                    restaurantArrayList.remove(i);
                                }

                            }

                        }

                        for(int i=0;i<restaurantArrayList.size();i++){

                            Restaurant restaurant = restaurantArrayList.get(i);
                            if(freeDelivery){

                                int n = (int)(restaurant.getDeliveryCharges());

                                if(n==0)
                                {
                                    newList.add(restaurant);
                                    restaurantArrayList.remove(i);
                                }

                            }

                        }

                        if(isoffer||isNoMinDelivery||freeDelivery){
                            //Log.e(TAG,"inside filter");
                            restaurantListAdapter = new RestaurantListAdapter(newList,restaurantsListRecycler);
                        }
                        else{
                            restaurantListAdapter = new RestaurantListAdapter(restaurantArrayList,restaurantsListRecycler);
                        }

                        restaurantsListRecycler.setLayoutManager(restaurantListLayoutManager);
                        restaurantsListRecycler.setHasFixedSize(true);
                        restaurantsListRecycler.setAdapter(restaurantListAdapter);
                    }
                    else {
                       // Toast.makeText(getContext(),"fuck",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG,"error in line 79");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.d(TAG,"error");
                progressBar.setVisibility(View.GONE);
                //imageView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getContext(),NoInternetActivity.class);
                startActivityForResult(intent,0);


                // Toast.makeText(getApplicationContext(),error.getMessage()+"  fffffffff ", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", sessionManager.getSelectedCity());
                params.put("service", sessionManager.getSelectedService());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    public ArrayList<Restaurant> sortByRatings(ArrayList<Restaurant> list) {
        int n = list.size();
        Restaurant res;
        for (int i = 0; i < n; i++) {

            for (int j = 1; j < (n - i); j++) {
                Float first = (list.get(j - 1).getRating());
                Float second = (list.get(j).getRating());
                if (first < second) {
                    //swap elements
                    res = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, res);
                }

            }
        }
        return list;
    }


    public ArrayList<Restaurant> sortByDeliveryPrice(ArrayList<Restaurant> list) {
        int n = list.size();
        Restaurant res;
        for (int i = 0; i < n; i++) {

            for (int j = 1; j < (n - i); j++) {
                Integer first = (int)(list.get(j - 1).getDeliveryCharges());
                Integer second = (int)(list.get(j).getDeliveryCharges());
                if (first > second) {
                    //swap elements
                    res = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, res);
                }

            }
        }
        //Toast.makeText(getContext(), "Delivery Prices To be set on Databse First", Toast.LENGTH_SHORT).show();
        return list;
    }

    public ArrayList<Restaurant> sortByMinOrderAmount(ArrayList<Restaurant> list) {
        int n = list.size();
        Restaurant res;
        for (int i = 0; i < n; i++) {


            for (int j = 1; j < (n - i); j++) {
                Integer first = Integer.parseInt(list.get(j - 1).getMinOrderAmount().trim());
                Integer second = Integer.parseInt(list.get(j).getMinOrderAmount().trim());

                Log.d(TAG, " first, second  =" + first + "," + second + "\n");
                if (first > second) {
                    //swap elements
                    //Log.d(TAG, "swap \n");
                    res = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, res);
                }


            }
        }
        return list;
    }


    public ArrayList<Restaurant> sortByOffers(ArrayList<Restaurant> list) {
        int n = list.size();
        Restaurant res;
        for (int i = 0; i < n; i++) {


            for (int j = 1; j < (n - i); j++) {
                Integer first,second;

                if (list.get(j - 1).getOffer()>0) {
                    first = list.get(j - 1).getOffer();
                } else if (list.get(j - 1).getNewOffer()>0) {
                    first = list.get(j - 1).getOffer();
                }
                else{
                    first=0;
                }

                if (list.get(j).getOffer()>0) {
                    second = list.get(j).getOffer();
                } else if (list.get(j).getNewOffer()>0) {
                    second = list.get(j).getOffer();
                }
                else {
                    second=0;
                }


                if (first < second) {
                    //swap elements
                    //Log.d(TAG, "swap \n");
                    res = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, res);
                }


            }
        }
        return list;
    }




    public ArrayList<Restaurant> sortOpenClose(ArrayList<Restaurant> list) {
        int n = list.size();
        Restaurant res;
        for (int i = 0; i < n; i++) {


            for (int j = 1; j < (n - i); j++) {
                Integer first = 1;
                if (list.get(j - 1).getStatus().equals("open"))
                    first = 0;
                Integer second = 1;
                if (list.get(j).getStatus().equals("open"))
                    second = 0;

                Log.d(TAG, " first, second  =" + first + "," + second + "\n");
                if (first > second) {
                    //swap elements
                    Log.d(TAG, "swap \n");
                    res = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, res);
                }


            }
        }

        return list;
    }


}
