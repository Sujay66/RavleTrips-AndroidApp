package com.raveltrips.android.ravel.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.CompletedAdapter;
import com.raveltrips.android.ravel.adapters.TrendingRecyclerAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncTask;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyTripFragment extends Fragment implements AsyncComplete {

    public Toolbar toptoolbar;
    private ArrayList<Trip> tripData = new ArrayList<Trip>();
    SearchView search;
    RecyclerView MyTripRecyclerView;
    LinearLayoutManager linearLayoutManager;
    RadioButton completed,wishlist;
    private SwipeRefreshLayout refreshLayout;
    TrendingRecyclerAdapter myAdapter;
    String choice = "completed";

    //shared image transition handler
    private TrendingFragment.TripImageSharedTransition sharedTransitionHandler;


    public MyTripFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu.findItem(R.id.action_search) == null)
            inflater.inflate(R.menu.menu_mytrips, menu);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_trip, container, false);
        getActivity().setTitle("My Trips");
        toptoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_top);
        search = (SearchView)rootView.findViewById(R.id.search_my_trips);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.fragment_my_trip);

        completed = (RadioButton)rootView.findViewById(R.id.completed_rb);
        wishlist = (RadioButton)rootView.findViewById(R.id.wishlist_rb);
        MyTripRecyclerView = (RecyclerView) rootView.findViewById(R.id.mytrips_recyclerview);
        MyTripRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        MyTripRecyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new TrendingRecyclerAdapter(getActivity(), tripData);


        MyTripRecyclerView.setAdapter(myAdapter);

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "completed";
                SetCompleteWishlist(choice);
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "wishlist";
                SetCompleteWishlist(choice);
            }
        });

        try{
            sharedTransitionHandler = (TrendingFragment.TripImageSharedTransition) getContext();
        }catch (ClassCastException e){
            Log.d("TrendingFragment","NavDrawer does not implement TripImageSharedTransition!");
        }

        myAdapter.setClickListener(new TrendingRecyclerAdapter.CardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container,DetailFragment.newInstance(tripData.get(position)))
                        .addToBackStack(null).commit();
            }

            @Override
            public void onSharedImageClick(View v,int position){
                if(sharedTransitionHandler!=null){
                    sharedTransitionHandler.onTripImageClick(position,tripData.get(position),v);
                }
            }

            @Override
            public void onLongCardClick(View v, int position) {

            }
            @Override
            public void onOverflowClick(View v, int position) {

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int pos = findTrip(query);
                if(pos > -1){
                    try {
                        MyTripRecyclerView.scrollToPosition(pos);
                    }catch (Exception e){
                        Log.d("MyTripFragment","Inside Search Exception");
                    }
                }else{
                    Toast.makeText(getActivity(),"No Trips found", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //refresh server data when we pull down the screen
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AppContext.profile!=null){
                    Log.d("TrendingFragment", "Refreshing data from server");
                    String url = AppContext.GET_TRIP_URL + "?type=" + choice +
                            "&email=" + AppContext.profile.getEmail();
                    DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(MyTripFragment.this);
                    downloadJSONAsyncTask.execute(url);
                }
            }
        });


        //fetch completed trips.
        if(choice.equalsIgnoreCase("completed") && AppContext.completedTrips.size()==0){
            if(AppContext.profile!=null){
                String profileUrl = AppContext.GET_TRIP_URL + "?type=" + choice +
                        "&email=" +AppContext.profile.getEmail();

                DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(this);
                downloadJSONAsyncTask.execute(profileUrl);
            }
        } else {
            tripData.clear();
            tripData.addAll(AppContext.completedTrips);
            myAdapter.notifyDataSetChanged();
        }
       // setRetainInstance(true);
        return rootView;
    }

    private int findTrip(String query) {
        for(Trip trip: tripData){
            if (trip.getName().toLowerCase().contains(query.toLowerCase())){
                return tripData.indexOf(trip);
            }
        }
        return -1;
    }


    private void SetCompleteWishlist(String choice) {
        if(choice.equalsIgnoreCase("completed")){
            if(AppContext.completedTrips.size()>0){
                tripData.clear();
                tripData.addAll(AppContext.completedTrips);
                myAdapter.notifyDataSetChanged();
            } else {
                tripData.clear();
                if(AppContext.profile!=null){
                    String profileUrl = AppContext.GET_TRIP_URL + "?type=" + choice +
                            "&email=" + AppContext.profile.getEmail();
                    DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(this);
                    downloadJSONAsyncTask.execute(profileUrl);
                }
            }
        } else { //wishlist
            if(AppContext.wishlistTrips.size()>0){
                tripData.clear();
                tripData.addAll(AppContext.wishlistTrips);
                myAdapter.notifyDataSetChanged();
            } else {
                tripData.clear();
                if(AppContext.profile!=null){
                    String profileUrl = AppContext.GET_TRIP_URL + "?type=" + choice +
                            "&email=" + AppContext.profile.getEmail();
                    DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(this);
                    downloadJSONAsyncTask.execute(profileUrl);
                }
            }
        }

    }

    @Override
    public void OnJsonAsyncCompleted(List<String> jsons) {
        //parse json strings to trips, notify the recycler list
        Gson gson = new Gson();
        if(jsons!=null && jsons.size()>0){
            for(String json: jsons){
                try {
                    //first fetch the top body, and then use gson for payload
                    JSONObject responseModel = new JSONObject(json);
                    String status = responseModel.getString("status");
                    String message = responseModel.getString("message");
                    if(status !=null && status.equalsIgnoreCase("200")){
                        tripData.clear();
                        JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tripObj = jsonArray.getJSONObject(i);
                            Trip trip = gson.fromJson(tripObj.toString(),Trip.class);
                            if(trip!=null && !tripData.contains(trip)){
                                tripData.add(trip);
                            }
                        }
                        if(choice.equalsIgnoreCase("completed")){
                            AppContext.completedTrips.clear();
                            AppContext.completedTrips.addAll(tripData);
                        } else {
                            AppContext.wishlistTrips.clear();
                            AppContext.wishlistTrips.addAll(tripData);
                        }
                    }   else {
                        Log.d("MyTripFragment","Received error from server:"+message);
                    }
                }catch(Exception ex){
                    Log.d("MyTripFragment","Exception converting from json:"+ex);
                }
            }
        }else {
            Toast.makeText(getActivity(), "Please try again later!!",Toast.LENGTH_SHORT).show();
        }
        if(refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
        myAdapter.notifyDataSetChanged();
    }
    }

