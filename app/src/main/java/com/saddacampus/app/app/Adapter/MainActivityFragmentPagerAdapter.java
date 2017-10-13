package com.saddacampus.app.app.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.saddacampus.app.app.Fragment.MainActivityDishesFragment;
import com.saddacampus.app.app.Fragment.RestaurantsFragment;

/**
 * Created by Devesh Anand on 09-06-2017.
 */

public class MainActivityFragmentPagerAdapter extends FragmentPagerAdapter {

    public MainActivityFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private String tabTitles[] = new String[] { "Restaurants", "Dishes" };

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new RestaurantsFragment();
        } else {
            return new MainActivityDishesFragment();
        }
    }

    @Override
    public int getCount()
    {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}