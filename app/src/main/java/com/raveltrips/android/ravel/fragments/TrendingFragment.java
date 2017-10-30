package com.raveltrips.android.ravel.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.gson.Gson;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.TrendingRecyclerAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncTask;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TrendingFragment extends Fragment implements AsyncComplete {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Trip> tripData = new ArrayList<Trip>();
    private TrendingRecyclerAdapter tripAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TrendingFragment thisFragment;

    private NavigationView navigationView;
    private TextView profileName;
    private TextView profileEmail;

    //shared image transition handler
    private TripImageSharedTransition sharedTransitionHandler;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragment = this;
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_trending, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.trip_recycler_view);
        recyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar)rootView.findViewById(R.id.tripLoadingBar) ;
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.fragment_trending);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        Log.d("TrendingFragment","Before PinDrap Adapter");
        tripAdapter = new TrendingRecyclerAdapter(getActivity(),tripData);

        //download profile data
        AppContext.fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (AppContext.fireBaseUser != null && AppContext.profile == null) {
            DownloadJsonAsyncTask profileDownload = new DownloadJsonAsyncTask();
            profileDownload.setAsyncComplete(new AsyncComplete() {
                @Override
                public void OnJsonAsyncCompleted(List<String> jsons) {
                    try {
                        Gson gson = new Gson();
                        if (jsons != null && jsons.size() > 0) {
                            JSONObject responseModel = new JSONObject(jsons.get(0));
                            JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                            String status = responseModel.getString("status");
                            if (status != null && status.equalsIgnoreCase("200")) {
                                JSONObject profObj = jsonArray.getJSONObject(0);
                                Profile sProfile = gson.fromJson(profObj.toString(), Profile.class);
                                if (sProfile != null) {
                                    AppContext.setProfileData(sProfile);
                                    Log.d("TrendingFragment", "Received server profile..name:" + sProfile.getName());
                                    setNavDrawerProfile();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Log.d("TrendingFragment", "Exception converting profile from json:" + ex);
                        ex.printStackTrace();
                    }
                }
            });
            String profileUrl = null;
            HashMap<String,String> paramsMap = new HashMap<>();
            paramsMap.put("email",AppContext.fireBaseUser.getEmail());
            if(AppContext.fireBaseUser.getDisplayName() == null){
                paramsMap.put("name","Name");
               String query =  AppContext.buildHttpParams(paramsMap);
                profileUrl = AppContext.FETCH_PROFILE_URL + "?"+query;
            } else {
                paramsMap.put("name",AppContext.fireBaseUser.getDisplayName());
                String query =  AppContext.buildHttpParams(paramsMap);
                profileUrl = AppContext.FETCH_PROFILE_URL + "?"+query;
            }
            profileDownload.execute(profileUrl);
        }

        try{
            sharedTransitionHandler = (TripImageSharedTransition) getContext();
        }catch (ClassCastException e){
            Log.d("TrendingFragment","NavDrawer does not implement TripImageSharedTransition!");
        }

        recyclerView.setAdapter(tripAdapter);

        tripAdapter.setClickListener(new TrendingRecyclerAdapter.CardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                Log.d("TrendingFragment","card clicked, position:"+position);
                /*getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container,DetailFragment.newInstance(tripData.get(position)),"TripDetailFragment")
                        .addToBackStack(null)
                        .commit();*/

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_left,0,0,R.anim.slide_to_right)
                        .replace(R.id.main_container,DetailFragment.newInstance(tripData.get(position)),"TripDetailFragment")
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onSharedImageClick(View v,int position){
                if(sharedTransitionHandler!=null){
                    sharedTransitionHandler.onTripImageClick(position,tripData.get(position),v);
                }
            }

            @Override
            public void onLongCardClick(View v, int position) {
                Log.d("TrendingFragment","card long clicked, position:"+position);
            }

            @Override
            public void onOverflowClick(View v, int position) {
                Log.d("TrendingFragment","overflow clicked, position:"+position);
            }
        });

        //refresh server data when we pull down the screen
       refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               Log.d("TrendingFragment","Refreshing data from server");
               DownloadJsonAsyncTask downloadTask = new DownloadJsonAsyncTask(thisFragment);
               downloadTask.execute(AppContext.TRENDING_TRIPS_URL);
           }
       });

        //download trip data
        //download only once and cache, swipe refresh can be used to refresh server data
        if(AppContext.trips.size() == 0){
            if(progressBar!=null)
                progressBar.setVisibility(View.VISIBLE);
            DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(this);
            downloadJSONAsyncTask.execute(AppContext.TRENDING_TRIPS_URL);
        } else {
            tripData.clear();
            tripData.addAll(AppContext.trips);
            tripAdapter.notifyDataSetChanged();
        }

        setNavDrawerProfile();
       // setRetainInstance(true);
        return rootView;
    }

    public void setNavDrawerProfile(){
        //download profile image and set to nav view image
        if(AppContext.profile!=null){
            try{
                navigationView = (NavigationView) getActivity().findViewById(R.id.navigation_view);
                View headerView =  navigationView.getHeaderView(0);
                profileName = (TextView)headerView.findViewById(R.id.username);
                profileEmail = (TextView) headerView.findViewById(R.id.email);
                CircleImageView imageView = (CircleImageView) headerView.findViewById(R.id.review_image);
                String imageUrl =  AppContext.profile.getImageUrl();
                if(imageUrl!=null && !imageUrl.isEmpty()){
                    try {
                        if (AppContext.USE_PICASSO)
                            Picasso.with(getContext()).load(imageUrl).into(imageView);
                        else {
                            Bitmap bitmap = AppContext.getBitmapFromMemCache(imageUrl);
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                DownloadImageAsyncTask task = new DownloadImageAsyncTask(imageView);
                                task.execute(imageUrl);
                            }
                        }
                    }catch (Exception ex){ex.printStackTrace();}
                }
                profileName.setText(AppContext.profile.getName());
                profileEmail.setText(AppContext.profile.getEmail());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void OnJsonAsyncCompleted(List<String> jsons) {
        //parse json strings to trips, notify the recycler list
        Gson gson = new Gson();
        if(jsons!=null && jsons.size()>0){
           // Log.d("TrendingFragment","Received json from server:"+jsons.get(0));

            for(String json: jsons){
                try {
                    //first fetch the top body, and then use gson for payload
                    JSONObject responseModel = new JSONObject(json);
                    JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                    String status = responseModel.getString("status");
                    String message = responseModel.getString("message");
                    if(status !=null && status.equalsIgnoreCase("200")){
                        tripData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tripObj = jsonArray.getJSONObject(i);
                            Trip trip = gson.fromJson(tripObj.toString(),Trip.class);
                            if(trip!=null && !tripData.contains(trip)){
                                tripData.add(trip);
                            }
                        }
                        //cache
                        AppContext.trips.clear();
                        AppContext.trips.addAll(tripData);
                    }   else {
                        Log.d("TrendingFragment","Received error from server:"+message);
                    }
                }catch(Exception ex){
                    Log.d("TrendingFragment","Exception converting from json:"+ex);
                    ex.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please try again later!!",Toast.LENGTH_SHORT).show();
        }
        tripAdapter.notifyDataSetChanged();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        if(refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);

    }

    public interface TripImageSharedTransition{
        public void onTripImageClick(int position, Trip trip, View sharedView);
    }
}
