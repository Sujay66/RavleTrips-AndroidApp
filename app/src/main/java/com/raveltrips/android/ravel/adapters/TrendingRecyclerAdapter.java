package com.raveltrips.android.ravel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash Anjanappa on 13-04-2017.
 */

public class TrendingRecyclerAdapter extends RecyclerView.Adapter<TrendingRecyclerAdapter.ViewHolder>{

    private List<Trip> trips;
    private Context mContext;
    private CardClickListener clickListener;


    public TrendingRecyclerAdapter(Context context, List<Trip> tripsData ){
        mContext = context;
        this.trips = tripsData;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        if(trip!=null)
        holder.setMovieData(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public CardClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(CardClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface CardClickListener{
        void onCardClick(View v, int position);
        void onLongCardClick(View v, int position);
        void onOverflowClick(View v, int position);
        void onSharedImageClick(View v,int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trending_card_layout1,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView tripImage;
        TextView tripName,tripLocation,reviewCount,tripPrice;
        RatingBar rating;

        void setMovieData(Trip trip){
            if (trip.getImageUrls() != null && trip.getImageUrls().size() > 0) {
                try {
                    String url = trip.getImageUrls().get(0);
                    if (AppContext.USE_PICASSO)
                        Picasso.with(mContext).load(url).into(tripImage);
                    else {
                        Bitmap bitmap = AppContext.getBitmapFromMemCache(url);
                        if (bitmap != null) {
                            tripImage.setImageBitmap(bitmap);
                        } else {
                            DownloadImageAsyncTask task = new DownloadImageAsyncTask(tripImage);
                            task.execute(url);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                tripName.setText(trip.getName());
                tripPrice.setText(String.valueOf(trip.getPrice()));
                tripLocation.setText(trip.getLocation());
                reviewCount.setText(String.valueOf(trip.getReviewCount()));
                if (trip.getRating() != null)
                    rating.setRating(trip.getRating().floatValue());

                tripImage.setTransitionName(trip.getId());
            }catch(Exception ex){ ex.printStackTrace();}
        }

        public ViewHolder(View v){
            super(v);
            tripImage = (ImageView)v.findViewById(R.id.tripImage);
            tripName = (TextView)v.findViewById(R.id.tripName);
            tripLocation = (TextView)v.findViewById(R.id.tripAddress);
            tripPrice = (TextView)v.findViewById(R.id.tripAmount);
            rating = (RatingBar) v.findViewById(R.id.tripRatingBar);
            reviewCount = (TextView)v.findViewById(R.id.tripReviewCount);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener!=null)
                        clickListener.onCardClick(v,getAdapterPosition());
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(clickListener!=null)
                        clickListener.onLongCardClick(v,getAdapterPosition());
                    return true;
                }
            });

            tripImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener!=null)
                        clickListener.onSharedImageClick(v,getAdapterPosition());
                }
            });
        }
    }
}
