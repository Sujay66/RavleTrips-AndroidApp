package com.raveltrips.android.ravel.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.ViewPagerAdapter;
import com.raveltrips.android.ravel.animation.DepthPageTransformer;
import com.raveltrips.android.ravel.animation.TransformerItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    private ViewPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab, container, false);

        //set up the view pager layout
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),4);
        viewPager = (ViewPager)view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        TabLayout tabs = (TabLayout)view.findViewById(R.id.tab_layout);
        tabs.setupWithViewPager(viewPager);

        try{
           // viewPager.setPageTransformer(false,(new TransformerItem(DepthPageTransformer.class)).getClazz().newInstance());
        }catch(Exception ex){ex.printStackTrace();}

      /*  viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                //fadein-out
                view.setTranslationX(view.getWidth() * -position);
                if(position <= -1.0F || position >= 1.0F) {
                    view.setAlpha(0.0F);
                } else if( position == 0.0F ) {
                    view.setAlpha(1.0F);
                } else {
                    // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    view.setAlpha(1.0F - Math.abs(position));
                }
                final float normalisedPosn = Math.abs(Math.abs(position)-1);
                view.setScaleX(normalisedPosn/2 + 0.5f);
                view.setScaleY(normalisedPosn/2 + 0.5f);
            }
        });*/
       // setRetainInstance(true);
        return view;
    }

    public ViewPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(ViewPagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
