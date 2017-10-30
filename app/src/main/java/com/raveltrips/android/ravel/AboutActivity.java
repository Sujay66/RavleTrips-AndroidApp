package com.raveltrips.android.ravel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView profileName;
    private TextView profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.white_pindrop);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_about);
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
                        switchActivity("addTrip");
                        return true;
                    case R.id.settings:
                        switchActivity("settings");
                        return true;
                    case R.id.about:
                        //switchActivity("about");
                        return true;
                    case R.id.logout:
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        AppContext.clearUserCache();
                        Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
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
        if (getSupportActionBar() != null && findViewById(R.id.toolbar) != null) {
            ((TextView)findViewById(R.id.toolbar_title)).setText("About Us");
        }
        setNavDrawerProfile();
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
