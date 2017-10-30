package com.raveltrips.android.ravel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.raveltrips.android.ravel.adapters.CartRecyclerAdapter;
import com.raveltrips.android.ravel.adapters.TrendingRecyclerAdapter;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadImageAsyncTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPostTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPutTask;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncTask;
import com.raveltrips.android.ravel.models.Cart;
import com.raveltrips.android.ravel.models.CartRecyclerModel;
import com.raveltrips.android.ravel.models.CartUiModel;
import com.raveltrips.android.ravel.models.Pindrop;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.Trip;
import com.raveltrips.android.ravel.utils.AppContext;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<CartRecyclerModel> cartItems = new ArrayList<CartRecyclerModel>();
    private CartRecyclerAdapter cartAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button clearCart;
    private Button checkout;
    private TextView totalAmount;
    CartUiModel cartUiModel;
    String serverUrl ="";
    String checkoutUrl = "";
    private TextView profileName;
    private TextView profileEmail;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = (RecyclerView)findViewById(R.id.cart_recycler_view);
        recyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar)findViewById(R.id.cartLoadingBar) ;
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        clearCart = (Button)findViewById(R.id.button6);
        checkout = (Button)findViewById(R.id.button4);
        totalAmount = (TextView)findViewById(R.id.textView3) ;

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.white_pindrop);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_cart);
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
                        switchActivity("about");
                        return true;
                    case R.id.logout:
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        AppContext.clearUserCache();
                        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
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

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        cartAdapter = new CartRecyclerAdapter(this,cartItems);
        if(AppContext.profile!=null){
            serverUrl = AppContext.FETCH_CART_URL+"?email="+AppContext.profile.getEmail();
            checkoutUrl = AppContext.CHECKOUT_URL+"?email="+AppContext.profile.getEmail();

            DownloadJsonAsyncTask downloadJSONAsyncTask = new DownloadJsonAsyncTask(new AsyncComplete() {
                @Override
                public void OnJsonAsyncCompleted(List<String> jsons) {
                    processCartServerResponse(jsons);
                }
            });
            downloadJSONAsyncTask.execute(serverUrl);
            if(progressBar!=null)
                progressBar.setVisibility(View.VISIBLE);
        }

        recyclerView.setAdapter(cartAdapter);
        cartAdapter.setClickListener(new CartRecyclerAdapter.CardClickListener() {
            @Override
            public void onRemoveFromCart(View v, int position) {
                try{
                    CartRecyclerModel model = cartItems.get(position);
                    Log.d("CartActivity","Removing item from cart:name"+model.getName());
                    removeFromCart(model);
                }catch(Exception ex){ ex.printStackTrace(); }
            }

            @Override
            public void onViewItem(View v, int position) {
                try{
                CartRecyclerModel model = cartItems.get(position);
                Log.d("CartActivity","view item from cart:name"+model.getName());
                jumpToDetailView(model);
                }catch(Exception ex){ }
            }
        });

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(AppContext.profile!=null)
                        clearAllCartItems();
                    else
                        ToastMessage("Please try again later..");
                }catch(Exception ex){ex.printStackTrace();}
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartItems.size()==0){
                    ToastMessage("No items to checkout!!");
                    return;
                }
                if(progressBar!=null)
                    progressBar.setVisibility(View.VISIBLE);
                try{
                    if(AppContext.profile!=null)
                        checkoutCart();
                    else
                        ToastMessage("Please try again later..");
                }catch(Exception ex){ex.printStackTrace();}

            }
        });
        setNavDrawerProfile();
        if (getSupportActionBar() != null && findViewById(R.id.toolbar) != null) {
            ((TextView)findViewById(R.id.toolbar_title)).setText("Cart");
        }

        //refresh server data when we pull down the screen
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AppContext.profile!=null)
                    refreshCart();
                else
                    ToastMessage("Please try again later..");
            }
        });
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
                //switchActivity("cart");  already in cart
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

    public void calculateTotal(){
        if(cartItems.size()>0){
            double sum = 0.0;
           for(CartRecyclerModel model: cartItems){
               sum += model.getPrice();
           }
            totalAmount.setText(String.valueOf(sum));
        } else {
            totalAmount.setText("0.00");
        }
    }

    public void refreshCart(){
        Log.d("CartActivity","Refreshing data from server");
        DownloadJsonAsyncTask downloadTask = new DownloadJsonAsyncTask(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> jsons) {
                processCartServerResponse(jsons);
            }
        });
        downloadTask.execute(serverUrl);
    }

    public void jumpToDetailView(CartRecyclerModel model){
        if(model!=null){
            Trip viewTrip = null;
            Pindrop viewPindrop = null;
            if(model.getType().equalsIgnoreCase("Trip")){
                for(Trip trip: cartUiModel.getTrips()){
                    if(trip.getId().equalsIgnoreCase(model.getId())){
                        viewTrip = trip;
                        break;
                    }
                }
            } else {
                for(Pindrop pindrop: cartUiModel.getPindrops()){
                    if(pindrop.getId().equalsIgnoreCase(model.getId())){
                        viewPindrop = pindrop;
                        break;
                    }
                }
            }
            if(viewTrip!=null){
                //move to trip detail view...
            }
           if(viewPindrop!=null){
                //move to pindrop detail view...
            }
        }
    }

    void checkoutCart(){
        //call checkout for email.
        DownloadJsonAsyncTask checkoutTask = new DownloadJsonAsyncTask();
        Log.d("CartActivity","checking out cart");
        checkoutTask.setAsyncComplete(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> jsons) {
                try {
                    if (jsons != null && jsons.size() > 0) {
                        JSONObject responseModel = new JSONObject(jsons.get(0));
                        String status = responseModel.getString("status");
                        if (status != null && status.equalsIgnoreCase("200")) {
                            ToastMessage("Cart Checkout successful!!");
                            refreshCart();
                            updateProfile();
                        }
                        else {
                            ToastMessage("Cart checkout failed!!");
                        }
                    }
                } catch (Exception ex) {
                    Log.d("CartActivity", "Exception converting cart from json:" + ex);
                }
                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
                if(refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
            }
        });
        checkoutTask.execute(checkoutUrl);
    }

    void clearAllCartItems(){
        Log.d("CartActivity","Clearing all cart items..");
        if(AppContext.profile.getCart() == null){
            Log.d("CartActivity","Fatal...cart is null, but trying clear items!!");
            return;
        }
        Cart cart = AppContext.profile.getCart();
        Log.d("CartActivity","cart:"+cart+" trips:"+cart.getTripIds()+" pindrops:"+cart.getPindropIds());


        if(cart.getPindropIds()!=null && cart.getPindropIds().size()==0 &&
                cart.getTripIds()!=null && cart.getTripIds().size()==0){
            Log.d("CartActivity","case1");
            ToastMessage("No cart items to clear!!");
            return;
        }
        if(cart.getPindropIds()==null && cart.getTripIds()==null){
            ToastMessage("No cart items to clear!!");
            Log.d("CartActivity","case2");
            return;
        }

        if((cart.getPindropIds()==null && (cart.getTripIds()!=null && cart.getTripIds().size()==0))
                ||(cart.getTripIds()==null && (cart.getPindropIds()!=null && cart.getPindropIds().size()==0))){
            ToastMessage("No cart items to clear!!");
            Log.d("CartActivity","case3");
            return;
        }


        if(cart.getPindropIds()!=null)cart.getPindropIds().clear();
        if(cart.getTripIds()!=null)cart.getTripIds().clear();
        AppContext.profile.setCart(cart);
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
        updateCartServerCall(null);
    }

    public void removeFromCart(CartRecyclerModel model){
        //make server call and remove from cart..update profile
        if(AppContext.profile.getCart() == null){
            Log.d("CartActivity","Fatal...cart is null, but trying to remove items!!");
            return;
        }
        Cart cart = AppContext.profile.getCart();
        if(model.getType()=="Trip"){
           cart.getTripIds().remove(model.getId());
        } else {
            cart.getPindropIds().remove(model.getId());
        }
        AppContext.profile.setCart(cart);

        Log.d("CartActivity","Removing from cart:"+model.getName());
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
        updateCartServerCall(model);
    }

    public void addToCart(CartRecyclerModel model){
        if(AppContext.profile.getCart() == null){
            Log.d("CartActivity","Fatal...cart is null, but trying to add items!!");
            return;
        }
        Cart cart = AppContext.profile.getCart();
        if(model.getType()=="Trip"){
            cart.getTripIds().add(model.getId());
        } else {
            cart.getPindropIds().add(model.getId());
        }
        AppContext.profile.setCart(cart);
    }

    public void updateCartServerCall(final CartRecyclerModel model){
        DownloadJsonAsyncPutTask profileTask = new DownloadJsonAsyncPutTask();
        Gson gson = new Gson();
        String body = gson.toJson(AppContext.profile).toString();
        Log.d("CartActivity","updating cart profile object:"+body);
        profileTask.setBodyContent(body);
        profileTask.setAsyncComplete(new AsyncComplete() {
            @Override
            public void OnJsonAsyncCompleted(List<String> jsons) {
                try {
                    if (jsons != null && jsons.size() > 0) {
                        JSONObject responseModel = new JSONObject(jsons.get(0));
                        String status = responseModel.getString("status");
                        if (status != null && status.equalsIgnoreCase("200")) {
                            ToastMessage("Cart updated!!");
                            //remove item from recycler view and refresh
                            if(model!=null){
                                cartItems.remove(model);
                                cartAdapter.notifyDataSetChanged();
                                calculateTotal();
                            } else {
                                //clear cart case
                                cartItems.clear();
                                cartAdapter.notifyDataSetChanged();
                                calculateTotal();
                            }
                        }
                        else {
                            ToastMessage("Cart updation failed!!");
                            //revert back the cart item, add it back to cart
                            if(model!=null){
                                addToCart(model);
                            }
                            //handle case when all items were removed...profile is outdated!
                        }
                    }
                } catch (Exception ex) {
                    Log.d("SearchFragment", "Exception converting search trips from json:" + ex);
                }
                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
                if(refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
            }
        });
        profileTask.execute(AppContext.UPDATE_PROFILE_URL);
    }

    public void processCartServerResponse(List<String> jsons){
        try {
            Gson gson = new Gson();
            if (jsons != null && jsons.size() > 0) {
                JSONObject responseModel = new JSONObject(jsons.get(0));
                String message = responseModel.getString("message");
                String status = responseModel.getString("status");
                if (status != null && status.equalsIgnoreCase("200")) {
                    JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                    JSONObject cartObj = jsonArray.getJSONObject(0);
                    cartUiModel = gson.fromJson(cartObj.toString(), CartUiModel.class);
                    convertFromCartUiModel();
                    cartAdapter.notifyDataSetChanged();
                    calculateTotal();
                } else {
                    if(!message.contains("found to fetch cart"))
                        ToastMessage("Failed to fetch cart Items!! Try again later.") ;
                    Log.d("CartActivity", "Exception from server:code"+status+" message:"+message);
                }
            }
        } catch (Exception ex) {
            Log.d("CartActivity", "Exception converting cart from json:" + ex);
        }
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        if(refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
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
            outState.putSerializable("serverUrl", serverUrl);
            outState.putSerializable("checkoutUrl", checkoutUrl);
        }catch (Exception ex) {}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            serverUrl =(String) savedInstanceState.getSerializable("serverUrl");
            checkoutUrl =(String) savedInstanceState.getSerializable("checkoutUrl");
        }catch (Exception ex){}
    }

    public void ToastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void convertFromCartUiModel(){
        if(cartUiModel == null || (cartUiModel.getPindrops()==null && cartUiModel.getTrips()==null))
            return;
        cartItems.clear();
        if(cartUiModel.getTrips()!=null && cartUiModel.getTrips().size()>0){
            for(Trip trip:cartUiModel.getTrips() ){
                CartRecyclerModel cartItem = new CartRecyclerModel(trip);
                cartItems.add(cartItem);
            }
        }
        if(cartUiModel.getPindrops()!=null && cartUiModel.getPindrops().size()>0){
            for(Pindrop pindrop:cartUiModel.getPindrops() ){
                CartRecyclerModel cartItem = new CartRecyclerModel(pindrop);
                cartItems.add(cartItem);
            }
        }
        Log.d("CartActivity","Populated :"+cartItems.size()+" items from server");
    }

    public void updateProfile(){
        Log.d("CartActivity", "Updating profile from server");
        //download profile data
           DownloadJsonAsyncTask profileDownload = new DownloadJsonAsyncTask();
            profileDownload.setAsyncComplete(new AsyncComplete() {
                @Override
                public void OnJsonAsyncCompleted(List<String> jsons) {
                    try {
                        Gson gson = new Gson();
                        if (jsons != null && jsons.size() > 0) {
                            JSONObject responseModel = new JSONObject(jsons.get(0));
                            JSONArray jsonArray = responseModel.getJSONArray("payLoad");
                            String status = responseModel.getString("status");
                            if (status != null && status.equalsIgnoreCase("200")) {
                                JSONObject profObj = jsonArray.getJSONObject(0);
                                Profile sProfile = gson.fromJson(profObj.toString(), Profile.class);
                                if (sProfile != null) {
                                    AppContext.setProfileData(sProfile);
                                    Log.d("CartActivity", "Received server profile..name:" + sProfile.getName());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Log.d("CartActivity", "Exception converting profile from json:" + ex);
                    }
                }
            });
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("email", AppContext.profile.getEmail());
        paramsMap.put("name", AppContext.profile.getName());
        String profileUrl = AppContext.FETCH_PROFILE_URL + "?" + AppContext.buildHttpParams(paramsMap);
        profileDownload.execute(profileUrl);
        }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }
}


