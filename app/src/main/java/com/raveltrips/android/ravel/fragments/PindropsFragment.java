package com.raveltrips.android.ravel.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.DetailsTransition;
import com.raveltrips.android.ravel.adapters.PinDropAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncTask;
import com.raveltrips.android.ravel.models.Pindrop;
import com.raveltrips.android.ravel.utils.AppContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PindropsFragment extends Fragment implements AsyncComplete {

    private ArrayList<Pindrop> tripData = new ArrayList<Pindrop>();
    GridView gridView;
    PinDropAdapter pinDropAdapter;


    public PindropsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pindrops, container, false);

        /*final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null && act.findViewById(R.id.toolbar) != null) {
            ((TextView)act.findViewById(R.id.toolbar_title)).setText("Pindrops");
        }*/
        gridView = (GridView)rootView.findViewById(R.id.pindrop_grid);
        pinDropAdapter = new PinDropAdapter(getContext(),tripData);

        gridView.setAdapter(pinDropAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View sharedView, int position, long id) {
               /* getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container,DetailPinDropFragment.newInstance(tripData.get(position)))
                        .addToBackStack(null).commit();*/
                flipCard(tripData.get(position));
            }
        });

        if(AppContext.pindrops.size() == 0){
            DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(this);
            downloadJSONAsyncTask.execute(AppContext.TRENDING_PINDROPS_URL);
        } else {
            tripData.clear();
            tripData.addAll(AppContext.pindrops);
            pinDropAdapter.notifyDataSetChanged();
        }
       // setRetainInstance(true);
        return rootView;
    }

    private void flipCard(Pindrop pindrop) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, 0, 0, R.anim.zoom_out)
                .replace(R.id.main_container, DetailPinDropFragment.newInstance(pindrop))
                .addToBackStack(null)
                .commit();
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
                            Pindrop pinDrop = gson.fromJson(tripObj.toString(),Pindrop.class);
                            if(pinDrop!=null && !tripData.contains(pinDrop)){
                                tripData.add(pinDrop);
                            }
                        }
                        //cache
                        if(!AppContext.pindrops.containsAll(tripData))
                            AppContext.pindrops.addAll(tripData);

                    }   else {
                        Log.d("PindropsFragment","Received error from server:"+message);
                    }
                }catch(Exception ex){
                    Log.d("PindropsFragment","Exception converting from json:"+ex);
                }
            }
        }else {
            Toast.makeText(getActivity(), "Please try again later!!",Toast.LENGTH_SHORT).show();
        }
        pinDropAdapter.notifyDataSetChanged();
    }

    public void itemClicked(int position) {

    }
}
