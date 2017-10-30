package com.raveltrips.android.ravel.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.adapters.DetailReviewAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPutTask;
import com.raveltrips.android.ravel.models.Cart;
import com.raveltrips.android.ravel.models.Pindrop;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.Review;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.raveltrips.android.ravel.utils.AppContext.BASE_SERVER_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailPinDropFragment extends Fragment implements AsyncComplete {

    private static final String ARG_PARAM1 = "param1";
    public static String ADD_TO_CART_URL = BASE_SERVER_URL+"/api/v1/open/mobile/profile";
    private Pindrop tripData = new Pindrop();
    private List<Review> tripReviews = new ArrayList<>();
    RecyclerView detail_recycler_view;
    LinearLayoutManager layoutManager;
    DetailReviewAdapter detailReviewAdapter;

    public DetailPinDropFragment() {
        // Required empty public constructor
    }

    public static DetailPinDropFragment newInstance(Pindrop trips) {
        Log.d("DetailPinDropFragment","Inside new instance:");
        DetailPinDropFragment fragment = new DetailPinDropFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, trips);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Pindrop pindrop = (Pindrop) getArguments().getSerializable(ARG_PARAM1);
            if(pindrop!=null) tripData = pindrop;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu.findItem(R.id.menu_item_share) == null)
            inflater.inflate(R.menu.menu_detail, menu);

        ShareActionProvider shareActionProvider;
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, tripData.getDescription());
        shareActionProvider.setShareIntent(i);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_pin_drop, container, false);
        setHasOptionsMenu(true);
        detail_recycler_view = (RecyclerView)rootView.findViewById(R.id.pin_drop_detail_recycler_view);
        detail_recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        detail_recycler_view.setLayoutManager(layoutManager);
        if(tripData.getReviews()!=null && tripData.getReviews().size()>0){
            tripReviews = tripData.getReviews();
        }
        detailReviewAdapter = new DetailReviewAdapter(getActivity(),tripReviews);
        detail_recycler_view.setAdapter(detailReviewAdapter);
        detail_recycler_view.setNestedScrollingEnabled(false);
        final ImageView thumbnail = (ImageView)rootView.findViewById(R.id.pin_drop_video_thumb_nail);
       List<String> imageUrls = tripData.getImageUrls();
        if(imageUrls!=null && imageUrls.size()>0){
            try {
                if (AppContext.USE_PICASSO)
                    Picasso.with(getContext()).load(imageUrls.get(0)).into(thumbnail);
                else {
                    Bitmap bitmap = AppContext.getBitmapFromMemCache(imageUrls.get(0));
                    if (bitmap != null) {
                        thumbnail.setImageBitmap(bitmap);
                    } else {
                        DownloadImageAsyncTask task = new DownloadImageAsyncTask(thumbnail);
                        task.execute(imageUrls.get(0));
                    }
                }
            }catch (Exception ex){ex.printStackTrace();}
        }

        final TextView cost = (TextView) rootView.findViewById(R.id.pin_drop_detail_cost);
        final TextView name = (TextView) rootView.findViewById(R.id.pindrop_name);
        final TextView location = (TextView) rootView.findViewById(R.id.pin_drop_detail_location);
        final TextView description = (TextView) rootView.findViewById(R.id.textView28);
        final RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.pin_drop_detail_ratingBar);
        final ImageButton AddtoCart = (ImageButton) rootView.findViewById(R.id.pin_drop_Add_to_cart);
   //     final ImageButton AddtoWishlist = (ImageButton) rootView.findViewById(R.id.pin_drop_Add_to_wishlist);

        try{
            cost.setText(String.valueOf(tripData.getPrice()));
            name.setText(tripData.getName());
            location.setText(tripData.getLocation());
            description.setText(tripData.getDescription());
            thumbnail.setTransitionName(tripData.getId());
            ratingBar.setRating(tripData.getRating().floatValue());
        }catch(Exception ex){ex.printStackTrace();}


        AddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile temp = AppContext.profile;
                Cart cart = temp.getCart();
                if (cart == null) {
                    cart = new Cart();
                }
                if (cart.getPindropIds() == null) {
                    cart.setPindropIds(new ArrayList<String>(Arrays.asList(tripData.getId())));
                } else {
                    cart.getPindropIds().add(tripData.getId());
                }
                temp.setCart(cart);
                AppContext.profile.setCart(cart);

                DownloadJsonAsyncPutTask task = new DownloadJsonAsyncPutTask();
                Gson gson = new Gson();
                task.setBodyContent(gson.toJson(temp).toString());
                Log.d("DetailPinDropFragment", "profile json"+gson.toJson(temp).toString());
                task.setAsyncComplete(new AsyncComplete() {
                    @Override
                    public void OnJsonAsyncCompleted(List<String> json) {
                        try {
                            Gson gson = new Gson();
                            if (json != null && json.size() > 0) {
                                JSONObject responseModel = new JSONObject(json.get(0));
                                JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                                String status = responseModel.getString("status");
                                if (status != null && status.equalsIgnoreCase("200")) {
                                    JSONObject profObj = jsonArray.getJSONObject(0);
                                    Profile sProfile = gson.fromJson(profObj.toString(), Profile.class);
                                    Log.d("DetailPinDropFragment", "updated profile:"+gson.toJson(sProfile).toString());
                                    ToastMessage("Added to cart!!");
                                }else{
                                   AppContext.profile.getCart().getPindropIds().remove(tripData.getId());
                                    ToastMessage("Unable to add to cart!!");
                                }
                            }
                        }
                        catch (Exception ex) {
                            Log.d("DetailPinDropFragment", "Exception converting search trips from json:" + ex);
                        }
                    }
                });
                task.execute(ADD_TO_CART_URL);
            }
        });

        //no wishlist tracker for pindrops for now..no use...
        /*AddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Profile temp = AppContext.profile;
                if (temp.getTripsWishlistIds() == null) {
                    temp.setTripsWishlistIds(Arrays.asList(tripData.getId()));
                }
                else {
                    temp.getTripsWishlistIds().add(tripData.getId());
                }

                DownloadJsonAsyncPutTask task = new DownloadJsonAsyncPutTask();
                Gson gson = new Gson();
                task.setBodyContent(gson.toJson(temp).toString());
                task.setAsyncComplete(new AsyncComplete() {
                    @Override
                    public void OnJsonAsyncCompleted(List<String> json) {
                        try {
                            Gson gson = new Gson();
                            if (json != null && json.size() > 0) {
                                JSONObject responseModel = new JSONObject(json.get(0));
                                JSONArray jsonArray1 = responseModel.getJSONArray("payLoad");
                                String status = responseModel.getString("status");
                                if (status != null && status.equalsIgnoreCase("200")) {
                                    Toast.makeText(getActivity(),"Trip Added to Wishlist", Toast.LENGTH_SHORT).show();
                                }else{
                                    temp.getTripsWishlistIds().remove(temp.getTripsWishlistIds().size()-1);
                                    Toast.makeText(getActivity(),"Unable to add to Wishlist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception ex) {
                            Log.d("DetailPinDropFragment", "Exception converting search trips from json:" + ex);
                        }
                    }
                });
                task.execute(ADD_TO_CART_URL);
            }
        });*/
       // setRetainInstance(true);
        return rootView;
    }


    public void ToastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void OnJsonAsyncCompleted(List<String> json) {

        Log.d("DetailPinDropFragment", "AfterAsync" + json);
    }

}
