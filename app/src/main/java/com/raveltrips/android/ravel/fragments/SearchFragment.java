package com.raveltrips.android.ravel.fragments;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.raveltrips.android.ravel.AddTripActivity;
import com.raveltrips.android.ravel.NavDrawerActivity;
import com.raveltrips.android.ravel.R;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPostTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncTask;
import com.raveltrips.android.ravel.models.GPSCoOrdinate;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.SearchOptions;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.raveltrips.android.ravel.utils.PermUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.apptik.widget.MultiSlider;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements LocationListener {

    private RadioGroup radioTypeGroup;
    private RadioButton radioTypeButton;
    private EditText searchLocation;
    private TextView minPrice;
    private TextView maxPrice;
    private  MultiSlider priceSlider;
    private RatingBar ratingBar;
    private EditText currentLocation;
    private MultiSlider distanceSlider;
    private TextView minDistance;
    private TextView maxDistance;
    private Button resetBtn;
    private Button searchBtn;
    private Button gpsBtn;
    private ImageView voiceApiBtn;
    private ProgressBar loadingBar;

    private List<Trip> searchResult = new ArrayList<>();
    private SearchOptions searchOptions = new SearchOptions();

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m
    public static final int REQ_CODE_SPEECH_INPUT = 100;

    /* GPS */
    private String mProviderName;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location currentGpsLocation;
    private String currentAddress;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_search, container, false);

        radioTypeGroup = (RadioGroup)view.findViewById(R.id.radioType);
        searchLocation = (EditText)view.findViewById(R.id.editText);
        loadingBar = (ProgressBar)view.findViewById(R.id.progressBar4);
        gpsBtn = (Button)view.findViewById(R.id.button5);
        voiceApiBtn = (ImageView) view.findViewById(R.id.voice_api_btn);

        ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    ratingBar.setRating(starsf);
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }
                return true;
            }});


        currentLocation = (EditText)view.findViewById(R.id.textView10) ;
        minPrice = (TextView) view.findViewById(R.id.minValue1);
        maxPrice = (TextView) view.findViewById(R.id.maxValue1);
        priceSlider = (MultiSlider) view.findViewById(R.id.range_slider1);
        priceSlider.setMax(1000);
        minPrice.setText(String.valueOf(priceSlider.getThumb(0).getValue()));
        maxPrice.setText(String.valueOf(priceSlider.getThumb(1).getValue()));

        priceSlider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minPrice.setText(String.valueOf(value));
                } else {
                    maxPrice.setText(String.valueOf(value));
                }
            }
        });


        minDistance = (TextView) view.findViewById(R.id.distminValue1);
        maxDistance = (TextView) view.findViewById(R.id.distmaxValue1);
        distanceSlider = (MultiSlider) view.findViewById(R.id.range_slider2);
        distanceSlider.setMax(10000);
        minDistance.setText(String.valueOf(distanceSlider.getThumb(0).getValue()));
        maxDistance.setText(String.valueOf(distanceSlider.getThumb(1).getValue()));


        distanceSlider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minDistance.setText(String.valueOf(value));
                } else {
                    maxDistance.setText(String.valueOf(value));
                }
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadingBar!=null)
                    loadingBar.setVisibility(View.VISIBLE);
                fetchGpsLocation();
            }
        });

        voiceApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        searchBtn = (Button)view.findViewById(R.id.button3);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    // get selected radio button from radioGroup
                    int selectedId = radioTypeGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioTypeButton = (RadioButton) view.findViewById(selectedId);
                    // ToastMessage(radioTypeButton.getText().toString());
                    if ("General Search".equalsIgnoreCase(radioTypeButton.getText().toString())) {
                        //normal search
                        searchOptions.setIsCurrentAddressSearch(false);
                        searchOptions.setSearchLocation(searchLocation.getText().toString());
                        searchOptions.setPriceStart(new Double(priceSlider.getThumb(0).getValue()));
                        searchOptions.setPriceEnd(new Double(priceSlider.getThumb(1).getValue()));
                        searchOptions.setRatingGreaterThan(new Double(ratingBar.getRating()));

                        //make server call
                        if (loadingBar != null)
                            loadingBar.setVisibility(View.VISIBLE);
                        makeServerCall();
                    } else {
                        //current location search!
                        if (currentGpsLocation == null) {
                            ToastMessage("Press the current location button!! / Wait for location!");
                        } else {
                            searchOptions.setIsCurrentAddressSearch(true);
                            searchOptions.setCurrentAddress(currentAddress);
                            GPSCoOrdinate gps = new GPSCoOrdinate();
                            gps.setLattitude(Double.toString(currentGpsLocation.getLatitude()));
                            gps.setLongitude(Double.toString(currentGpsLocation.getLongitude()));
                            searchOptions.setCurrentCoOrds(gps);
                            searchOptions.setRangeStart(new Double(distanceSlider.getThumb(0).getValue()));
                            searchOptions.setRangeEnd(new Double(distanceSlider.getThumb(1).getValue()));
                            //make server call
                            if (loadingBar != null)
                                loadingBar.setVisibility(View.VISIBLE);
                            makeServerCall();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        resetBtn = (Button)view.findViewById(R.id.button2);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceSlider.repositionThumbs();
                distanceSlider.repositionThumbs();
                ratingBar.setRating(new Float(2.5));
                searchLocation.setText("");
                currentLocation.setText("");
                try{
                    if(mLocationManager!=null){
                        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            mLocationManager.removeUpdates(SearchFragment.this);
                            if(loadingBar!=null)
                                loadingBar.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception ex){ex.printStackTrace();}
            }
        });

        ratingBar.setRating(new Float(2.5));
        priceSlider.repositionThumbs();
        distanceSlider.repositionThumbs();

        /*final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null && act.findViewById(R.id.toolbar) != null) {
            ((TextView)act.findViewById(R.id.toolbar_title)).setText("Search Trips");
        }*/
       // setRetainInstance(true);
        return view;
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak now!");
        //set voice listener to activity.
        try{
            final NavDrawerActivity act = (NavDrawerActivity) getActivity();
            if(act!=null){
                Log.i("SearchFragment","voice listener set to activity");
                act.setVoiceApiListener(new NavDrawerActivity.onVoiceApiReturn() {
                    @Override
                    public void handleVoiceResult(String result) {
                        searchLocation.setText(result);
                    }
                });
            }
        }catch (RuntimeException ex){ex.printStackTrace();}

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            ToastMessage("Speech Api not supported!!");
        }
    }



    public void jumpToSearchResult(ArrayList<Trip> trips){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,SearchResultFragment.newInstance(trips),"MapFragment")
                .addToBackStack(null).commit();

    }

    public void makeServerCall(){
        DownloadJsonAsyncPostTask searchTask = new DownloadJsonAsyncPostTask();
        Gson gson = new Gson();
        searchTask.setBodyContent(gson.toJson(searchOptions).toString());
        searchTask.setAsyncComplete(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> jsons) {
                try {
                    Gson gson = new Gson();
                    if (jsons != null && jsons.size() > 0) {
                        JSONObject responseModel = new JSONObject(jsons.get(0));
                        JSONArray jsonArray1 = responseModel.getJSONArray("payLoad");
                        //fix for double list from server
                        JSONArray jsonArray = jsonArray1.getJSONArray(0);
                        String status = responseModel.getString("status");
                        if (status != null && status.equalsIgnoreCase("200")) {
                            searchResult.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tripObj = jsonArray.getJSONObject(i);
                                Trip trip = gson.fromJson(tripObj.toString(),Trip.class);
                                if(trip!=null && !searchResult.contains(trip)){
                                    searchResult.add(trip);
                                }
                            }
                        }
                        if(searchResult.size()==0){
                            ToastMessage("No results found for search!!");
                        } else {
                            ToastMessage(searchResult.size()+" Trips found!");
                            jumpToSearchResult((ArrayList)searchResult);
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Please try again later!!",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.d("SearchFragment", "Exception converting search trips from json:" + ex);
                }
                if(loadingBar!=null)
                    loadingBar.setVisibility(View.GONE);
            }
        });
        searchTask.execute(AppContext.SEARCH_URL);
    }

    public void ToastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    public void  fetchGpsLocation(){
        try{
            if (PermUtility.checkFineLocationPermission(getContext())) {
                mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                // Get the best provider between gps, network and passive
                Criteria criteria = new Criteria();
                mProviderName = mLocationManager.getBestProvider(criteria, true);
                // No one provider activated: prompt GPS
                if (mProviderName == null || mProviderName.equals("")) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                // At least one provider activated. Get the coordinates
                switch (mProviderName) {
                    case "passive":
                        mLocationManager.requestLocationUpdates(mProviderName, MINIMUM_TIME, MINIMUM_DISTANCE, this);
                        // Location location = mLocationManager.getLastKnownLocation(mProviderName);
                        break;

                    case "network":
                        mLocationManager.requestLocationUpdates(mProviderName, MINIMUM_TIME, MINIMUM_DISTANCE, this);
                        Log.d("SearchFragment", "using netwkk:");
                        break;

                    case "gps":
                        mLocationManager.requestLocationUpdates(mProviderName, MINIMUM_TIME, MINIMUM_DISTANCE, this);
                        Log.d("SearchFragment", "location using gps");
                        break;
                }
            }
        }catch(Exception ex){
            if(loadingBar!=null)
            loadingBar.setVisibility(View.GONE);
            Log.d("SearchFragment", "Exception getting gps!!"+ex);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(loadingBar!=null)
            loadingBar.setVisibility(View.GONE);
        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_LOCATION_FINE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "GPS permission granted", Toast.LENGTH_LONG).show();
                PermUtility.checkCoarseLocationPermission(getContext());
                fetchGpsLocation();
            } else {
                Toast.makeText(getActivity(), "GPS denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_LOCATION_COARSE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), "Network denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(loadingBar!=null)
            loadingBar.setVisibility(View.GONE);
        if (location != null) {
            Log.d("SearchFragment", "Received location!! lat:" + location.getLatitude());
            currentGpsLocation = new Location(location);
            String address =  getLocationAddress(location);
            currentAddress = address;
            currentLocation.setText(address);
         }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getLocationAddress(Location location) {
        try{
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                return ("No address found by service");
            } catch (Exception e2) {
                e2.printStackTrace();
                return ("No address found by service");
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return addressText;
            } else {
                return "No address found by the service!!";
            }
        }catch(Exception ex){ex.printStackTrace(); return " ";}

    }
}
