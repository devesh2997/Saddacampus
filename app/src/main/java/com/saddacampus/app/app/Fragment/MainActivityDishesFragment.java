package com.saddacampus.app.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saddacampus.app.R;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.helper.RecommendationManager;

/**
 * Created by Devesh Anand on 10-07-2017.
 */

public class MainActivityDishesFragment extends Fragment {

    private static final String TAG = MainActivityDishesFragment.class.getSimpleName();

    NestedScrollView recommendedDishesScrollView;

    RecyclerView topRatedItemsRecycler;
    RecyclerView mostOrderedItemsRecycler;
    RecommendationManager recommendationManager;

    public RecommendationManager getRecommendationManager(){
        return this.recommendationManager;
    }

    public MainActivityDishesFragment() {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_activity_dishes_fragment,container,false);

        recommendedDishesScrollView = (NestedScrollView)rootView.findViewById(R.id.recommended_dishes_container);
        topRatedItemsRecycler = (RecyclerView)rootView.findViewById(R.id.top_rated_items_recycler);
        mostOrderedItemsRecycler = (RecyclerView)rootView.findViewById(R.id.most_ordered_items_recycler);


        recommendationManager = new RecommendationManager(getContext(),topRatedItemsRecycler,mostOrderedItemsRecycler,null,null, Config.KEY_GET_RECOMMENDATION_FOR_CITY);
        recommendationManager.getRecommendations();

        return rootView;

    }
}
