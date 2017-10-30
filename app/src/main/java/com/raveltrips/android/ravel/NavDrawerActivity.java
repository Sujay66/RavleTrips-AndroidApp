package com.raveltrips.android.ravel;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.loopj.android.http.*;
import com.raveltrips.android.ravel.adapters.DetailsTransition;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPostTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPutTask;
import com.raveltrips.android.ravel.fragments.DetailFragment;
import com.raveltrips.android.ravel.fragments.SearchFragment;
import com.raveltrips.android.ravel.fragments.SearchResultFragment;
import com.raveltrips.android.ravel.fragments.TabFragment;
import com.raveltrips.android.ravel.fragments.TrendingFragment;
import com.raveltrips.android.ravel.models.FcmToken;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavDrawerActivity extends AppCompatActivity implements TrendingFragment.TripImageSharedTransition {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NavDrawerActivity thisActivity;
    private int exitCount=0;
    private onVoiceApiReturn voiceApiListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.white_pindrop);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_nav_drawer);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
               //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Closing drawer on item click
                drawerLayout.closeDrawers();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    case R.id.home:
                        //switchActivity("home");  already in home, do nothing
                        return true;
                    case R.id.my_profile:
                        switchActivity("profile");
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
                        Intent intent = new Intent(thisActivity, LoginActivity.class);
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
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        exitCount=0;
        if (getSupportActionBar() != null && findViewById(R.id.toolbar) != null) {
            ((TextView)findViewById(R.id.toolbar_title)).setText("Home");
        }

        //launch tab fragment.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,new TabFragment(),"Tab_Fragment")
                .commit();

        //fetchFirebaseToken();
        fetchFCMToken();

    }


    public void ToastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void fetchFirebaseToken(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.i("NavDrawerActivity","Firebase token:"+idToken);
                        } else {
                            Log.i("NavDrawerActivity","Firebase login failed:"+task.getException().getMessage());
                        }
                    }
                });

    }

    public void fetchFCMToken(){
        if(AppContext.FCM_TOKEN == null){
            try{
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                String token = FirebaseInstanceId.getInstance().getToken();
                AppContext.FCM_TOKEN = token;
                FirebaseMessaging.getInstance().subscribeToTopic(AppContext.FIREBASE_TOPIC_NAME);
                Log.i("NavDrawerActivity","subscribed to topic");
                Log.i("NavDrawerActivity","Firebase FCM token:"+token);
                updateFcmTokenToServer(mUser.getEmail(),token);
            }catch (Exception ex){ ex.printStackTrace();}
        }
    }

  /*  public void updateFcmTokenToServer(String email, String token) {
        Log.i("NavDrawerActivity", "sending fcm token to server..email:"+email+" token:"+token);
        if (token != null && email != null) {
            try {
                AsyncHttpClient client = new AsyncHttpClient();
                FcmToken fcmToken = new FcmToken(email, token);
                Gson gson = new Gson();
                String body = gson.toJson(fcmToken).toString();
                HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
               // ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes("UTF-8"));
               // entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.setMaxRetriesAndTimeout(3, 3000);
                client.setUserAgent("android-async-http-1.4.9");
                client.post(this, AppContext.UPDATE_FCM_URL, entity, "application/json; charset=utf8", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            String resp = new String(responseBody);
                            JSONObject responseModel = new JSONObject(resp);
                            String status = responseModel.getString("status");
                            if (status != null && status.equalsIgnoreCase("200")) {
                                Log.i("NavDrawerActivity", "successfully updated fcm token with server");
                            } else {
                                Log.i("NavDrawerActivity", "failed to update fcm token: status:" + status + " error:" + responseModel.getString("message"));
                            }
                        }catch(Exception ex){ex.printStackTrace();}
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i("NavDrawerActivity","Fcm token server call failed: status:"+statusCode);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/

    public void updateFcmTokenToServer(String email, String token) {
        Log.i("NavDrawerActivity", "sending fcm token to server..email:"+email+" token:"+token);
        if (token != null && email != null) {
            try{
                DownloadJsonAsyncPostTask fcmTask = new DownloadJsonAsyncPostTask();
                FcmToken fcmToken = new FcmToken(email, token);
                Gson gson = new Gson();
                String body = gson.toJson(fcmToken).toString();
                fcmTask.setBodyContent(body);
                fcmTask.setAsyncComplete(new AsyncComplete() {
                    @Override
                    public void OnJsonAsyncCompleted(List<String> jsons) {
                        try {
                            if (jsons != null && jsons.size() > 0) {
                                JSONObject responseModel = new JSONObject(jsons.get(0));
                                String status = responseModel.getString("status");
                                if (status != null && status.equalsIgnoreCase("200")) {
                                    Log.d("NavDrawerActivity", "fcm updated to server");
                                }
                                else {
                                    Log.d("NavDrawerActivity", "fcm token send failed: status:"+status+" message:"
                                            +responseModel.getString("message"));
                                }
                            }
                        } catch (Exception ex) {
                            Log.d("NavDrawerActivity", "Exception sending fcm token to server:" + ex);
                        }
                    }
                });
                fcmTask.execute(AppContext.UPDATE_FCM_URL);
            }catch (Exception ex){ ex.printStackTrace();}
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

    public interface onVoiceApiReturn {
        void handleVoiceResult(String result);
    }

    public onVoiceApiReturn getVoiceApiListener() {
        return voiceApiListener;
    }

    public void setVoiceApiListener(onVoiceApiReturn voiceApiListener) {
        this.voiceApiListener = voiceApiListener;
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SearchFragment.REQ_CODE_SPEECH_INPUT: {
                Log.i("NavDrawer","voice result inside");
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.i("NavDrawer","voice result received:"+result);
                    if(result!=null && result.size()>0 && voiceApiListener!=null){
                        voiceApiListener.handleVoiceResult(result.get(0));
                    }
                }
                break;
            }

        }
    }

    public void onTripImageClick(int position, Trip trip, View sharedView){
        Log.d("NavDrawer","transition name:"+sharedView.getTransitionName());
        DetailFragment fragment = DetailFragment.newInstance(trip);
        fragment.setSharedElementEnterTransition(new DetailsTransition());
        fragment.setEnterTransition(new Fade());
        fragment.setExitTransition(new Fade());
        fragment.setSharedElementReturnTransition(new DetailsTransition());
        getSupportFragmentManager().beginTransaction()
                .addSharedElement(sharedView,sharedView.getTransitionName())
                .replace(R.id.main_container,fragment)
                .addToBackStack(null).commit();
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
