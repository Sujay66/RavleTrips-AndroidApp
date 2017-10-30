package com.raveltrips.android.ravel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.models.Pindrop;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by LENOVO on 19-04-2017.
 */

public class PinDropAdapter  extends BaseAdapter {

    private PinDropAdapter.CardClickListener clickListener;
    private Context mContext;
    private ArrayList PinDrops;
    TextView textViewAndroid;
    ImageView imageViewAndroid;

    public PinDropAdapter(Context context, ArrayList arrayList) {
        mContext = context;
        this.PinDrops = arrayList;
    }

    @Override
    public int getCount() {
        return PinDrops.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        View gridViewAndroid =null;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try{
            Pindrop eachPin = (Pindrop) PinDrops.get(i);
            if (convertView == null) {
                gridViewAndroid = new View(mContext);
                gridViewAndroid = inflater.inflate(R.layout.layout_grid_single, null);
                textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.grid_text);
                imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.grid_image);
                RatingBar ratingBar = (RatingBar) gridViewAndroid.findViewById(R.id.rating_pindrop);
                textViewAndroid.setText(eachPin.getName());
                if (eachPin.getRating() == null)
                    ratingBar.setRating(0);
                else
                    ratingBar.setRating(eachPin.getRating().floatValue());

                if (eachPin.getImageUrls() != null && eachPin.getImageUrls().size() > 0) {
                    String url = eachPin.getImageUrls().get(0);
                    try {
                        if(AppContext.USE_PICASSO)
                            Picasso.with(mContext).load(url).into(imageViewAndroid);
                        else {
                            Bitmap bitmap = AppContext.getBitmapFromMemCache(url);
                            if(bitmap != null){
                                imageViewAndroid.setImageBitmap(bitmap);
                            }else {
                                DownloadImageAsyncTask task = new DownloadImageAsyncTask(imageViewAndroid);
                                task.execute(url);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(eachPin.getId()!=null) {
                    imageViewAndroid.setTransitionName(eachPin.getId());
                    gridViewAndroid.setTransitionName(eachPin.getId());
                }

            } else {
                gridViewAndroid = (View) convertView;
            }
        }catch(Exception ex){ex.printStackTrace();}



        return gridViewAndroid;
    }

    public void setClickListener(PinDropAdapter.CardClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public interface CardClickListener{
        void onCardClick(View v,int position);    }
}
