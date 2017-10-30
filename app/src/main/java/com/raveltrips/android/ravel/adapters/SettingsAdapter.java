package com.raveltrips.android.ravel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raveltrips.android.ravel.R;

import java.util.ArrayList;

/**
 * Created by LENOVO on 16-04-2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> settings_list;
    private int count = 0;
    TextView settings_type;
    TextView settings_desc;

    public SettingsAdapter(Context context, ArrayList<String> list){
        mContext = context;
        settings_list = list;
    }

    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SettingsAdapter.ViewHolder holder, int position) {
        if(count<settings_list.size()) {
            settings_type.setText(settings_list.get(count));
            settings_desc.setText(settings_list.get(count + 1));
            count = count + 2;
        }
    }

    @Override
    public int getItemCount() {
        return settings_list.size()/2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            settings_desc = (TextView) itemView.findViewById(R.id.settings_description);
            settings_type = (TextView) itemView.findViewById(R.id.settings_type);
        }
    }
}
