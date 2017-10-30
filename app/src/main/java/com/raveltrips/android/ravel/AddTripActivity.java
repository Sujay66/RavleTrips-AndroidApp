package com.raveltrips.android.ravel;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.AsyncUploadImage;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPostTask;
import com.raveltrips.android.ravel.fragments.SearchFragment;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.raveltrips.android.ravel.utils.PermUtility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class AddTripActivity extends AppCompatActivity implements LocationListener {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect; //button4
    private ImageView ivImage; //imageView2
    private EditText titleText; //editText2
    private EditText videoLink; //editText3
    private EditText locationText; //editText4
    private Button gpsBtn; //button6
    private EditText descriptionText; //editText6
    private EditText price; //editText7
    private EditText keyword1; //editText10
    private EditText keyword2; //editText9
    private EditText keyword3; //editText12
    private EditText keyword4; //editText11
    private LinearLayout loadingLayout; //loading
    private Button cancelBtn; //button8
    private Button saveBtn; //button7

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    private Uri fileUri = null; //not used for now
    private String filePath = null;
    private String fileName = null;
    int imagePickerType = 1;
    private Trip newTrip;
    private TextView profileName;
    private TextView profileEmail;
    /* GPS */
    private String mProviderName;
    private LocationManager mLocationManager;
    private Location currentGpsLocation;
    private String currentAddress;
    private Button voiceApiBtn;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m
    public static final int REQ_CODE_SPEECH_INPUT = 100;

    // LogCat tag
    private static final String TAG = AddTripActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        btnSelect = (Button)findViewById(R.id.button4);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView)findViewById(R.id.imageView2);
        titleText = (EditText)findViewById(R.id.editText2);
        videoLink = (EditText)findViewById(R.id.editText3);
        locationText = (EditText)findViewById(R.id.editText4);
        gpsBtn = (Button)findViewById(R.id.button6);
        descriptionText = (EditText)findViewById(R.id.editText6);
        price = (EditText)findViewById(R.id.editText7);
        keyword1 = (EditText)findViewById(R.id.editText10);
        keyword2 = (EditText)findViewById(R.id.editText9);
        keyword3 = (EditText)findViewById(R.id.editText12);
        keyword4 = (EditText)findViewById(R.id.editText11);
        loadingLayout = (LinearLayout)findViewById(R.id.loading);
        cancelBtn = (Button)findViewById(R.id.button8);
        saveBtn = (Button)findViewById(R.id.button7);
        voiceApiBtn = (Button)findViewById(R.id.button26);

        loadingLayout.setVisibility(View.GONE);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.white_pindrop);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_add_trip);
         navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
             if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.home:
                        switchActivity("home");
                        return true;
                    case R.id.my_profile:
                        switchActivity("profile");
                        return true;
                    case R.id.add_trip:
                        //switchActivity("addTrip");  already in add trip
                        return true;
                    case R.id.settings:
                        switchActivity("settings");
                        return true;
                    case R.id.about:
                        switchActivity("about");
                        return true;
                    case R.id.logout:
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        AppContext.clearUserCache();
                        Intent intent = new Intent(AddTripActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.exit:
                    {
                        try{
                            finish();
                            moveTaskToBack(true);
                        }catch (RuntimeException ex){ex.printStackTrace();}
                        return true;
                    }

                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchGpsLocation();
            }
        });

        voiceApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    if(filePath==null){
                        ToastMessage("Please pick an Image first");
                        return;
                    }
                    loadingLayout.setVisibility(View.VISIBLE);
                    uploadImageToServer();
                }
            }
        });

        if (getSupportActionBar() != null && findViewById(R.id.toolbar) != null) {
            ((TextView)findViewById(R.id.toolbar_title)).setText("Add a Trip");
        }
        setNavDrawerProfile();

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak now!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            ToastMessage("Speech Api not supported!!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                switchActivity("cart");
                return true;

            default:
                Toast.makeText(this, "Invalid Selection",Toast.LENGTH_SHORT).show();
                return super.onContextItemSelected(item);
        }
    }

    public void switchActivity(String viewName) {
        Intent intent = null;
        switch (viewName){
            case "home":
                intent = new Intent(this,NavDrawerActivity.class);
                break;
            case "profile":
                intent = new Intent(this,ProfileActivity.class);
                break;
            case "addTrip":
                intent = new Intent(this,AddTripActivity.class);
                break;
            case "settings":
                intent = new Intent(this,SettingsActivity.class);
                break;
            case "about":
                intent = new Intent(this,AboutActivity.class);
                break;
            case "cart":
                intent = new Intent(this,CartActivity.class);
                break;
        }
        if(intent!=null){
            intent.putExtra("Key", "value");  //if needed in future
            startActivity(intent);
        }
    }

    public void setNavDrawerProfile(){
        //download profile image and set to nav view image
        if(AppContext.profile!=null){
            try{
                navigationView = (NavigationView)findViewById(R.id.navigation_view);
                View headerView =  navigationView.getHeaderView(0);
                profileName = (TextView)headerView.findViewById(R.id.username);
                profileEmail = (TextView) headerView.findViewById(R.id.email);
                CircleImageView imageView = (CircleImageView) headerView.findViewById(R.id.review_image);
                String imageUrl =  AppContext.profile.getImageUrl();
                if(imageUrl!=null && !imageUrl.isEmpty()){
                    try {
                        if (AppContext.USE_PICASSO)
                            Picasso.with(this).load(imageUrl).into(imageView);
                        else {
                            Bitmap bitmap = AppContext.getBitmapFromMemCache(imageUrl);
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                DownloadImageAsyncTask task = new DownloadImageAsyncTask(imageView);
                                task.execute(imageUrl);
                            }
                        }
                    }catch (Exception ex){ex.printStackTrace();}
                }
                profileName.setText(AppContext.profile.getName());
                profileEmail.setText(AppContext.profile.getEmail());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    boolean validateFields(){
        if(titleText.getText().toString().isEmpty() || videoLink.getText().toString().isEmpty()
            ||locationText.getText().toString().isEmpty()||descriptionText.getText().toString().isEmpty()
                ||price.getText().toString().isEmpty()){
            ToastMessage("Please fill all the fields!!");
            return false;
        }
        return true;
    }

    void resetFields(){
        titleText.setText("");
        videoLink.setText("");
        locationText.setText("");
        descriptionText.setText("");
        price.setText("");
        keyword1.setText("");
        keyword2.setText("");
        keyword3.setText("");
        keyword4.setText("");
        loadingLayout.setVisibility(View.GONE);
    }

    boolean populateTripObject(){
        try{
            newTrip = new Trip();
            //access profile and set it as creator it

            newTrip.setCreatorId(AppContext.profile.getId());
            newTrip.setDescription(descriptionText.getText().toString());
            newTrip.setImageUrls(Arrays.asList(AppContext.BASE_IMAGE_URL+fileName));
            newTrip.setLocation(locationText.getText().toString());
            newTrip.setName(titleText.getText().toString());
            newTrip.setRating(2.5);
            newTrip.setVideoUrls(Arrays.asList(videoLink.getText().toString()));
            ArrayList<String> keywords = new ArrayList<>();
            if(!keyword1.getText().toString().isEmpty())
                keywords.add(keyword1.getText().toString());
            if(!keyword2.getText().toString().isEmpty())
                keywords.add(keyword2.getText().toString());
            if(!keyword3.getText().toString().isEmpty())
                keywords.add(keyword3.getText().toString());
            if(!keyword4.getText().toString().isEmpty())
                keywords.add(keyword4.getText().toString());
            newTrip.setKeywords(keywords);
            try {
                newTrip.setPrice(Double.valueOf(price.getText().toString()));
            }catch(NumberFormatException ex){
                newTrip.setPrice(0.00);
            }
        }catch(Exception ex){
            Log.d(TAG,"Exception building trip obj:"+ex);
            return false;
        }
        return true;
    }

    public void  fetchGpsLocation(){
        try{
            // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
            if (PermUtility.checkFineLocationPermission(this)) {

                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            Log.d("SearchFragment", "Exception getting gps!!"+ex);
        }
    }


    void  uploadImageToServer(){
        AsyncUploadImage imageUpload = new AsyncUploadImage(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> json) {
                try {
                    if (json != null && json.size() > 0) {
                        JSONObject responseModel = new JSONObject(json.get(0));
                        String message = responseModel.getString("message");
                        String status = responseModel.getString("status");
                        if (status != null && status.equalsIgnoreCase("200")) {
                           //proceed to next stage...
                            //upload trip
                            if(populateTripObject()){
                                uploadTripToServer();
                            }  else {
                                loadingLayout.setVisibility(View.GONE);
                                ToastMessage("Failed to upload trip to server!! Try again later!");
                            }
                        }
                        else {
                            loadingLayout.setVisibility(View.GONE);
                            ToastMessage("Failed to upload image to server!! Try again later!");
                            Log.d("SearchFragment", "Message from server"+message);
                        }

                        Log.d("SearchFragment", "image uploaded..filename"+fileName);
                    }
                } catch (Exception ex) {
                    loadingLayout.setVisibility(View.GONE);
                    ToastMessage("Failed to upload image to server!! Try again later!");
                    Log.d("SearchFragment", "Exception converting search trips from json:" + ex);
                }
            }
        });
        imageUpload.setFilePath(filePath);
        imageUpload.execute(AppContext.IMAGE_UPLOAD_URL);

    }

    void uploadTripToServer(){
        DownloadJsonAsyncPostTask createTripTask = new DownloadJsonAsyncPostTask();
        Gson gson = new Gson();
        createTripTask.setBodyContent(gson.toJson(newTrip).toString());
        createTripTask.setAsyncComplete(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> jsons) {
                try {
                    if (jsons != null && jsons.size() > 0) {
                        JSONObject responseModel = new JSONObject(jsons.get(0));
                        JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                        String status = responseModel.getString("status");
                        if (status != null && status.equalsIgnoreCase("200")) {
                            loadingLayout.setVisibility(View.GONE);
                           ToastMessage("Trip uploaded successfully!!");
                            resetFields();
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                            ToastMessage("Failed to upload trip to server!! Try again later!");
                        }
                    }
                } catch (Exception ex) {
                    loadingLayout.setVisibility(View.GONE);
                    ToastMessage("Failed to upload trip to server!! Try again later!");
                    Log.d(TAG, "Exception converting create trips from json:" + ex);
                }
            }
        });
        createTripTask.execute(AppContext.CREATE_TRIP_URL);
    }

    public void ToastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddTripActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //get type, for first time, asks for permission
                if (items[item].equals("Take Photo")) {
                    imagePickerType = 0;
                } else if (items[item].equals("Choose from Gallery")) {
                    imagePickerType = 1;
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                    return;
                }
                if(PermUtility.checkReadPermission(AddTripActivity.this)){
                    if (items[item].equals("Take Photo")) {
                        imagePickerType = 0;
                        handleImagepick(0);
                    } else if (items[item].equals("Choose from Gallery")) {
                        imagePickerType = 1;
                        handleImagepick(1);
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    void handleImagepick(int selection){
        switch(selection){
            case 0:
                  cameraIntent();
                break;
            case 1:
                galleryIntent();
                break;
        }
    }

    private void galleryIntent()
    {
        try{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

        }catch(Exception ex){
            Log.d("AddTrip","Exception starting gallery activity:"+ex);
            ToastMessage(" Failed to Open Gallery");
        }
    }

    private void cameraIntent()
    {
        try{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }catch(Exception ex){
            Log.d("AddTrip","Exception starting camera activity:"+ex);
            ToastMessage(" Failed to start Camera");
        }

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        try{
            outState.putSerializable("trip_obj", (Serializable)newTrip);
            outState.putSerializable("filePath", filePath);
            outState.putSerializable("fileName", fileName);
        }catch (Exception ex) {}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            newTrip =(Trip) savedInstanceState.getSerializable("trip_obj");
            filePath = (String) savedInstanceState.getSerializable("filePath");
            fileName = (String) savedInstanceState.getSerializable("fileName");
        }catch (Exception ex){}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // Toast.makeText(this, "Read storage permission granted", Toast.LENGTH_LONG).show();
                PermUtility.checkCameraPermission(AddTripActivity.this);
            } else {
                Toast.makeText(this, "Read storage denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermUtility.checkWritePermission(AddTripActivity.this);
                //Toast.makeText(this, "Permissions granted!! Try again now.", Toast.LENGTH_LONG).show();
                //handle image picker.
                handleImagepick(imagePickerType);
            } else {
                Toast.makeText(this, "Camera denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Write storage permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Write storage denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_LOCATION_FINE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "GPS permission granted", Toast.LENGTH_LONG).show();
                PermUtility.checkCoarseLocationPermission(AddTripActivity.this);
                fetchGpsLocation();
            } else {
                Toast.makeText(this, "GPS denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_LOCATION_COARSE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Network permission granted", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Network denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if(requestCode == AddTripActivity.REQ_CODE_SPEECH_INPUT){
                    Log.d("AddTrip","voice result inside");
                    if (null != data) {
                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        Log.d("AddTrip","voice result received:"+result);
                        locationText.setText(result.get(0));
                    }
            }
        } else {
            Log.i("AddTrip","Error: onActivityResult, ReqCode:"+requestCode+" resCode:"+requestCode);
        }
    }

    private void onCaptureImageResult(Intent data) {
        try{

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            createFileFromBitmap(thumbnail);
            ivImage.setImageBitmap(thumbnail);
        }catch(Exception ex){
            Log.d("AddTrip", "error capturing from camera"+ex);
        }
    }

    public void createFileFromBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            Log.d("Utility", "onCaptureImageResult, writing image to disk..");

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            filePath = destination.getPath();
            fileName = destination.getName();
            Log.d("AddTrip", "Image file created at" + filePath);

        } catch (Exception ex) {
            Log.d("AddTrip", "runtime error caught:" + ex);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        try {
            Bitmap bm = null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    createFileFromBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ivImage.setImageBitmap(bm);

        } catch (Exception ex) {
            Log.d("AddTrip", "error capturing from gallery" + ex);
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d(TAG, "Received location!! lat:" + location.getLatitude());
            currentGpsLocation = new Location(location);
            String address =  getLocationAddress(location);
            currentAddress = address;
            locationText.setText(address);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {    }

    public String getLocationAddress(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        String className = intent.getComponent().getClassName();
        super.startActivity(intent);
        overridePendingTransitionEnter(className);
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter(String toActivity) {
        if (toActivity != null && toActivity.contains("CartActivity")) {
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        } else
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }
}
