package com.raveltrips.android.ravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.transition.Fade;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.AsyncUploadImage;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPutTask;
import com.raveltrips.android.ravel.models.Cart;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.utils.AppContext;
import com.raveltrips.android.ravel.utils.PermUtility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView profileName;
    private TextView profileEmail;

    private ImageView profile;
    private EditText name;
    private EditText contact;
    private EditText address;
    private TextView email;
    private Toolbar toolbar2;
    private Profile modifiedProfile;
    private Button cancelBtn;
    private Button saveBtn;
    private FrameLayout scrollLayout;
    private String filePath = null;
    private String fileName = null;
    int imagePickerType = 1;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.white_pindrop);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_profile);
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
                        //switchActivity("profile"); already in profile
                        return true;
                    case R.id.add_trip:
                        switchActivity("addTrip");
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
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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


        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);

        profile = (ImageView)findViewById(R.id.profile_image);
        name = (EditText)findViewById(R.id.name1);
        email = (TextView)findViewById(R.id.tvNumber3);
        address = (EditText)findViewById(R.id.tvNumber5);
        contact = (EditText)findViewById(R.id.tvNumber1);
        cancelBtn = (Button)findViewById(R.id.button10) ;
        saveBtn = (Button)findViewById(R.id.button9);
        setProfileData();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(AppContext.profile!=null){
                        setViewData(AppContext.profile);
                    }
                }catch(Exception ex) {}
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileActivity","Saving the profile update changes!!..");
                String updatedName = name.getText().toString();
                String updatedAddress = address.getText().toString();
                String updatedContact = contact.getText().toString();
                modifiedProfile.setName(updatedName);
                modifiedProfile.setAddress(updatedAddress);modifiedProfile.setMobNo(updatedContact);
                if(filePath==null){
                    // oly datd update, no image
                    UpdateProfileServerCall();
                } else {
                    //upload image first, then profile
                    uploadImageToServer();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                Snackbar.make(view, "Please select an Image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setNavDrawerProfile();

        /*CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.cooridinate_layout);
        if(coordinatorLayout!=null){
            scrollLayout = (FrameLayout)findViewById(R.id.scroll_layout) ;
          AppBarLayout mAppBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar) ;
          mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset == 0) {
                       // Log.d("ProfileActivity","Expanded!: height:"+verticalOffset);
                        if(scrollLayout!=null){
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollLayout.getLayoutParams();
                            params.setMargins(0, 550, 0, 0);
                            scrollLayout.setLayoutParams(params);
                        }
                    } else if (verticalOffset == -425) {
                      //  Log.d("ProfileActivity","collapsed!: height:"+verticalOffset);
                        if(scrollLayout!=null){
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollLayout.getLayoutParams();
                            params.setMargins(0, 110, 0, 0);
                            scrollLayout.setLayoutParams(params);
                        }
                    }
                }
            });
        }*/

        if (getSupportActionBar() != null && findViewById(R.id.toolbar) != null) {
            ((TextView)findViewById(R.id.toolbar_title)).setText("My Profile");
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                if(PermUtility.checkReadPermission(ProfileActivity.this)){
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

    void UpdateProfileServerCall(){
        DownloadJsonAsyncPutTask task = new DownloadJsonAsyncPutTask();
        Gson gson = new Gson();
        task.setBodyContent(gson.toJson(modifiedProfile).toString());
        Log.d("ProfileActivity", "profile json"+gson.toJson(modifiedProfile).toString());
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
                            if(sProfile!=null){
                                Log.d("ProfileActivity", "updated profile:"+gson.toJson(sProfile).toString());
                                ToastMessage("Profile updated successfully!");
                                AppContext.profile.copyFromProfile(sProfile);
                                setViewData(AppContext.profile);
                                setNavDrawerProfile();
                            }
                        }else{
                            ToastMessage("Failed to update Profile! try again later..");
                        }
                    }
                }
                catch (Exception ex) {
                    Log.d("ProfileActivity", "Exception converting profile from json:" + ex);
                }
            }
        });
        task.execute(AppContext.UPDATE_PROFILE_URL);
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
                            modifiedProfile.setImageUrl(AppContext.BASE_IMAGE_URL+fileName);
                            UpdateProfileServerCall();
                        }
                        else {
                            ToastMessage("Failed to upload image to server!! Try again later!");
                            Log.d("ProfileActivity", "Message from server"+message);
                        }

                        Log.d("ProfileActivity", "image uploaded..filename"+fileName);
                    }
                } catch (Exception ex) {
                    ToastMessage("Failed to upload image to server!! Try again later!");
                    Log.d("ProfileActivity", "Exception converting search trips from json:" + ex);
                }
            }
        });
        imageUpload.setFilePath(filePath);
        imageUpload.execute(AppContext.IMAGE_UPLOAD_URL);

    }


    public void ToastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void setProfileData(){
        if(AppContext.profile!=null){
            try{
                modifiedProfile = new Profile();
                modifiedProfile.copyFromProfile(AppContext.profile);
                setViewData(AppContext.profile);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void setViewData(Profile profiledata){
        name.setText(profiledata.getName());
        email.setText(profiledata.getEmail());
        address.setText(profiledata.getAddress());
        contact.setText(profiledata.getMobNo());
        Log.d("ProfileActivity","setting name to toolbar as:"+profiledata.getName());
        toolbar2.setTitle(profiledata.getName());
       toolbar2.refreshDrawableState();

        if(profiledata.getImageUrl()!=null && !profiledata.getImageUrl().isEmpty()){
            try {
                if (AppContext.USE_PICASSO)
                    Picasso.with(this).load(profiledata.getImageUrl()).into(profile);
                else {
                    Bitmap bitmap = AppContext.getBitmapFromMemCache(profiledata.getImageUrl());
                    if (bitmap != null) {
                        profile.setImageBitmap(bitmap);
                    } else {
                        DownloadImageAsyncTask task = new DownloadImageAsyncTask(profile);
                        task.execute(profiledata.getImageUrl());
                    }
                }
            }catch (Exception ex){ex.printStackTrace();}
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(this, "Read storage permission granted", Toast.LENGTH_LONG).show();
                PermUtility.checkCameraPermission(ProfileActivity.this);
            } else {
                Toast.makeText(this, "Read storage denied", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PermUtility.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermUtility.checkWritePermission(ProfileActivity.this);
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
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        try{

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            createFileFromBitmap(thumbnail);
            profile.setImageBitmap(thumbnail);
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

            profile.setImageBitmap(bm);

        } catch (Exception ex) {
            Log.d("AddTrip", "error capturing from gallery" + ex);
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
}
