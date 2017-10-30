package com.raveltrips.android.ravel.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.SettingsAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    RecyclerView SettingsRecyclerView;
    LinearLayoutManager layout;
    SettingsAdapter settingsAdapter;
    ArrayList<String> options = new ArrayList<String>();


    public SettingsFragment() {
        options.add(0, "Notification Settings");
        options.add(1, "Notification Settings");
        options.add(2, "Privacy Settings");
        options.add(3, "Change what others see");
        options.add(4, "Location Settings");
        options.add(5, "Change Location preferences");
        options.add(6, "Connect & Share");
        options.add(7, "Manage Connected Accounts");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().setTitle("Settings");
        SettingsRecyclerView = (RecyclerView) rootView.findViewById(R.id.settings_recycler_view);
        SettingsRecyclerView.setHasFixedSize(true);
        layout = new LinearLayoutManager(getActivity());
        SettingsRecyclerView.setLayoutManager(layout);
        settingsAdapter = new SettingsAdapter(getActivity(),options);
        SettingsRecyclerView.setAdapter(settingsAdapter);
        return rootView;
    }

}
