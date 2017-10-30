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
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.models.CartRecyclerModel;
import com.raveltrips.android.ravel.models.Trip;

import java.util.List;

/**
 * Created by Akash Anjanappa on 17-04-2017.
 */

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {

    private List<CartRecyclerModel> items;
    private Context mContext;
    private CartRecyclerAdapter.CardClickListener clickListener;


    public CartRecyclerAdapter(Context context, List<CartRecyclerModel> cartItems ){
        mContext = context;
        this.items = cartItems;
    }

    @Override
    public void onBindViewHolder(CartRecyclerAdapter.ViewHolder holder, int position) {
        CartRecyclerModel item = items.get(position);
        if(item!=null)
            holder.setCartData(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public CartRecyclerAdapter.CardClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(CartRecyclerAdapter.CardClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface CardClickListener{
        void onRemoveFromCart(View v, int position);
        void onViewItem(View v, int position);
    }

    @Override
    public CartRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card_layout,parent,false);
        CartRecyclerAdapter.ViewHolder vh = new CartRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,type,price;
        RatingBar rating;
        ImageView trash,visible;

        void setCartData(CartRecyclerModel item){
            name.setText(item.getName());
            type.setText(item.getType());
            price.setText(Double.toString(item.getPrice()));
            if(item.getRating()!=null)
                rating.setRating(item.getRating().floatValue());
        }

        public ViewHolder(View v){
            super(v);
            name = (TextView)v.findViewById(R.id.name);
            type = (TextView)v.findViewById(R.id.type);
            price = (TextView)v.findViewById(R.id.price);
            rating = (RatingBar) v.findViewById(R.id.rating);
            trash = (ImageView)v.findViewById(R.id.trash);
            visible = (ImageView)v.findViewById(R.id.visible);

            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onRemoveFromCart(v,getAdapterPosition());
                }
            });

            visible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onViewItem(v,getAdapterPosition());
                }
            });
        }
    }
}
