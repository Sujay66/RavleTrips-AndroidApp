package com.raveltrips.android.ravel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.raveltrips.android.ravel.fragments.MyTripFragment;
import com.raveltrips.android.ravel.fragments.PindropsFragment;
import com.raveltrips.android.ravel.fragments.SearchFragment;
import com.raveltrips.android.ravel.fragments.TrendingFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    int count;

    public ViewPagerAdapter(FragmentManager fm, int size){
        super(fm);
        count = size;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new TrendingFragment();
            case 1: return new PindropsFragment();
            case 2: return new SearchFragment();
            case 3: return new MyTripFragment();
            default:
                    return new TrendingFragment();
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch(position){
            case 0: return "Trending";
            case 1: return "Pindrops";
            case 2: return "Search";
            case 3: return "My Trips";
            default:
                return "Trending";
        }
    }
}
