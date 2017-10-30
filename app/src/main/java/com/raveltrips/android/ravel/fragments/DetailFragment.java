package com.raveltrips.android.ravel.fragments;


import android.content.Intent;
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
import android.widget.VideoView;

import com.google.gson.Gson;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.YoutubePlayerActivity;
import com.raveltrips.android.ravel.adapters.DetailReviewAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPutTask;
import com.raveltrips.android.ravel.models.Cart;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.Review;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.raveltrips.android.ravel.utils.AppContext.BASE_SERVER_URL;
import static com.raveltrips.android.ravel.utils.AppContext.profile;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements AsyncComplete {

    private static final String ARG_PARAM1 = "param1";
    public static String ADD_TO_CART_URL = BASE_SERVER_URL+"/api/v1/open/mobile/profile";
    private static String videoURL = "";
    private Trip tripData = new Trip();
    private List<Review> tripReviews = new ArrayList<>();
    RecyclerView detail_recycler_view;
    LinearLayoutManager layoutManager;
    DetailReviewAdapter detailReviewAdapter;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Trip trips) {
        Log.d("DetailFragment","Inside new instance");
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, trips);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Trip trip =  (Trip) getArguments().getSerializable(ARG_PARAM1);
            if(trip!=null) tripData = trip;
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
            i.putExtra(Intent.EXTRA_TEXT, tripData.getVideoUrls().get(0));
            shareActionProvider.setShareIntent(i);
            super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        detail_recycler_view = (RecyclerView)rootView.findViewById(R.id.detail_recycler_view);
        detail_recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        detail_recycler_view.setLayoutManager(layoutManager);
        if(tripData.getReviews()!=null && tripData.getReviews().size()>0){
            tripReviews = tripData.getReviews();
        }
        detailReviewAdapter = new DetailReviewAdapter(getActivity(),tripReviews);
        detail_recycler_view.setAdapter(detailReviewAdapter);
        //detail_recycler_view.setNestedScrollingEnabled(false);

        final ImageView play_button = (ImageView)rootView.findViewById(R.id.play_button);
        final ImageView thumbnail = (ImageView)rootView.findViewById(R.id.video_thumb_nail);

        try{
            String thumbnailID = tripData.getVideoUrls().get(0);
            if(thumbnailID.contains("=")){
                String[] thumb = thumbnailID.split("=");
                Picasso.with(getContext()).load("http://img.youtube.com/vi/"+thumb[1]+"/mqdefault.jpg").into(thumbnail);
            } else {
                ToastMessage("Youtube url incorrect format!!");
                thumbnail.setImageResource(R.drawable.youtube_error);
            }

        }catch(Exception ex){
            Log.d("DetailFragment","Exception setting youtube url:"+ex);
            ex.printStackTrace();
        }

        final TextView cost = (TextView) rootView.findViewById(R.id.detail_cost);
        final TextView name = (TextView) rootView.findViewById(R.id.detail_name);
        final TextView location = (TextView) rootView.findViewById(R.id.detail_location);
        final TextView description = (TextView) rootView.findViewById(R.id.textView28);
        final RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.detail_ratingBar);
        final ImageButton AddtoCart = (ImageButton) rootView.findViewById(R.id.Add_to_cart);
        final ImageButton AddtoWishlist = (ImageButton) rootView.findViewById(R.id.Add_to_wishlist);

        //handle if trip data fields are null..if in case, will cause null pointer
        try{
            cost.setText(String.valueOf(tripData.getPrice()));
            name.setText(tripData.getName());
            location.setText(tripData.getLocation());
            if(tripData.getDescription()!=null)
                description.setText(tripData.getDescription());
            else
                description.setVisibility(View.GONE);
            if(tripData.getId()!=null)
                thumbnail.setTransitionName(tripData.getId());
            ratingBar.setRating(tripData.getRating().floatValue());
        } catch(Exception ex)
        { ex.printStackTrace();}


        AddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Profile temp = AppContext.profile;
                Cart cart = temp.getCart();
                if (cart == null) {
                    cart = new Cart();
                }
                if (cart.getTripIds() == null) {
                    cart.setTripIds(new ArrayList<String>(Arrays.asList(tripData.getId())));
                } else {
                    cart.getTripIds().add(tripData.getId());
                }
                temp.setCart(cart);
                AppContext.profile.setCart(cart);

                DownloadJsonAsyncPutTask task = new DownloadJsonAsyncPutTask();
                Gson gson = new Gson();
                task.setBodyContent(gson.toJson(temp).toString());
                Log.d("DetailFragment", "profile json" + gson.toJson(temp).toString());
                final Cart finalCart = cart;
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
                                    Log.d("DetailFragment", "updated profile:" + gson.toJson(sProfile).toString());
                                    Toast.makeText(getActivity(), "Trip Added Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    finalCart.getTripIds().remove(finalCart.getTripIds().size() - 1);
                                    Toast.makeText(getActivity(), "Unable to add to cart", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            Log.d("DetailFragment", "Exception converting search trips from json:" + ex);
                        }
                    }
                });
                task.execute(ADD_TO_CART_URL);
            }
        });

        AddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Profile temp = AppContext.profile;
                    if (temp.getTripsWishlistIds() == null) {
                        temp.setTripsWishlistIds(new ArrayList<String>(Arrays.asList(tripData.getId())));
                    }
                else {
                    temp.getTripsWishlistIds().add(tripData.getId());
                }
                //set to app context, profile is updated
                AppContext.profile.setTripsWishlistIds(temp.getTripsWishlistIds());

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
                                    AppContext.profile.getTripsWishlistIds().remove(tripData.getId());
                                    Toast.makeText(getActivity(),"Unable to add to Wishlist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception ex) {
                            Log.d("DetailFragment", "Exception converting search trips from json:" + ex);
                        }
                    }
                });
                task.execute(ADD_TO_CART_URL);
            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DetailFragment","Inside VideoClick");
                if(tripData.getVideoUrls()!=null && tripData.getVideoUrls().size()>0 &&
                        tripData.getVideoUrls().get(0).contains("=") ){
                        videoURL =  tripData.getVideoUrls().get(0);
                        Log.d("DetailFragment",videoURL);
                        Intent intent = new Intent(getContext(), YoutubePlayerActivity.class);
                        intent.putExtra("url_key",videoURL);
                        startActivity(intent);
                } else {
                    ToastMessage("Cannot play youtube video!! Format incorrect!");
                }

            }
        });

       // setRetainInstance(true);
        return rootView;
    }


    public void ToastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnJsonAsyncCompleted(List<String> json) {

    }

}
