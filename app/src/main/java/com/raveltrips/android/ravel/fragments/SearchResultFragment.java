package com.raveltrips.android.ravel.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.TrendingRecyclerAdapter;
import com.raveltrips.android.ravel.models.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultFragment extends SupportMapFragment implements OnMapReadyCallback{

    private List<MarkerOptions> markers;
    private ArrayList<Trip> tripData = new ArrayList<>();
    private TrendingRecyclerAdapter tripAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    //shared image transition handler
    private TrendingFragment.TripImageSharedTransition sharedTransitionHandler;


    public SearchResultFragment() {
        // Required empty public constructor
    }


    public static SearchResultFragment newInstance(ArrayList<Trip> trips){
        SearchResultFragment mf = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putSerializable("trips",trips);
        mf.setArguments(args);
        return mf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_search_result, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);



        try {
            ArrayList<Trip> trips = (ArrayList<Trip>)getArguments().getSerializable("trips");
            if(trips!=null){
                tripData.addAll(trips);
                markers = populateMarkers(trips);
            }

            initializeMap();
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            Log.e("SrhResFragment", "Exception initializing maps:" + e);
        }
        tripAdapter = new TrendingRecyclerAdapter(getActivity(),tripData);

        recyclerView.setAdapter(tripAdapter);
        try{
            sharedTransitionHandler = (TrendingFragment.TripImageSharedTransition) getContext();
        }catch (ClassCastException e){
            Log.d("TrendingFragment","NavDrawer does not implement TripImageSharedTransition!");
        }

        tripAdapter.setClickListener(new TrendingRecyclerAdapter.CardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                try{
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    int count = fm.getBackStackEntryCount();
                    for(int i = 0; i < count; ++i) {
                        fm.popBackStack();
                    }
                    Log.d("SearchMapFragment","popped "+count+" fragments");

                    fm.beginTransaction()
                            .replace(R.id.main_container,DetailFragment.newInstance(tripData.get(position)))
                            .commit();
                    Log.d("SearchMapFragment","card clicked, position:"+position);
                }catch (Exception ex){ex.printStackTrace();}

            }
            @Override
            public void onSharedImageClick(View v,int position){

            }

            @Override
            public void onLongCardClick(View v, int position) {
                Log.d("SrhResFragment","card long clicked, position:"+position);
            }

            @Override
            public void onOverflowClick(View v, int position) {
                Log.d("SrhResFragment","overflow clicked, position:"+position);
            }
        });

    return view;
    }

    List<MarkerOptions> populateMarkers(ArrayList<Trip> trips){
        List<MarkerOptions> markers = new ArrayList<>();
        try {
            for (Trip trip : trips) {
                if (trip.getGps() != null) {
                    if(!trip.getGps().getLattitude().equalsIgnoreCase("null")){
                        LatLng latLng = new LatLng(Double.valueOf(trip.getGps().getLattitude()), Double.valueOf(trip.getGps().getLongitude()));
                        MarkerOptions mark = new MarkerOptions().position(latLng).title(trip.getName());
                        markers.add(mark);
                    }
                }
            }
        }catch(Exception ex){
            Log.d("SrhResFragment", "building marker exception:"+ex);
        }
        return markers;
    }

    private void initializeMap()
    {
           FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SupportMapFragment mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.gmap, mSupportMapFragment).commit();
           mSupportMapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ToastMessage("No Permissions enabled for location!!");
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (markers != null && markers.size() > 0) {
                for (MarkerOptions marker : markers) {
                    googleMap.addMarker(marker);
                    builder.include(marker.getPosition());
                    Log.d("SrhResFragment", "added marker:"+marker.getTitle());
                }
            }

            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }catch(Exception ex){
            Log.d("SrhResFragment", "Exception in map"+ex);
        }
    }


    public void ToastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

}
