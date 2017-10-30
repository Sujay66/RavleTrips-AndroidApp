package com.raveltrips.android.ravel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.models.Review;

import java.util.List;

/**
 * Created by LENOVO on 16-04-2017.
 */

public class DetailReviewAdapter extends RecyclerView.Adapter<DetailReviewAdapter.ViewHolder> {

    private List<Review> TripReviews;
    public DetailReviewAdapter(){}
    public DetailReviewAdapter(Context context, List<Review> reviews){
        TripReviews = reviews;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reviews_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
           Review review = TripReviews.get(position);
            if(review!=null)
            holder.setMovieData(review);
    }

    @Override
    public int getItemCount() {
        return TripReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar rating;
        TextView tripReview,tripDate,name;
        public ViewHolder(View itemView) {
            super(itemView);
            tripReview = (TextView)itemView.findViewById(R.id.user_review);
            tripDate = (TextView)itemView.findViewById(R.id.user_review_date);
            name = (TextView)itemView.findViewById(R.id.textView29);
            rating = (RatingBar)itemView.findViewById(R.id.ratingBar2);
        }

        public void setMovieData(Review review) {
            try{
                tripReview.setText(review.getText());
                tripDate.setText(review.getCreatedDate());
                name.setText(review.getUsername());
                if(review.getRating()!=null)
                rating.setRating(review.getRating().floatValue());
            } catch(Exception ex)
            { ex.printStackTrace();}
        }

    }
}
