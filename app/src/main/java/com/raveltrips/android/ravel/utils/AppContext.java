package com.raveltrips.android.ravel.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.raveltrips.android.ravel.models.Pindrop;
import com.raveltrips.android.ravel.models.Profile;
import com.raveltrips.android.ravel.models.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash Anjanappa on 13-04-2017.
 */

public class AppContext {

    public static String BASE_SERVER_URL = "http://216.172.176.65";

    public static String TRENDING_TRIPS_URL = BASE_SERVER_URL+"/api/v1/open/mobile/trendingtrips/rating";
   // sample:  /api/v1/open/mobile/profile/fetch?name=Akash Anjanappa&email=akash.dedly@gmail.com
    public static String FETCH_PROFILE_URL = BASE_SERVER_URL+"/api/v1/open/mobile/profile/fetch";

    public static String UPDATE_PROFILE_URL = BASE_SERVER_URL+"/api/v1/open/mobile/profile";

    //example:http://localhost:8080/api/v1/open/mobile/checkout?email=akash.a2351@gmail.com
    public static String CHECKOUT_URL = BASE_SERVER_URL+"/api/v1/open/mobile/checkout";

    public static String SEARCH_URL = BASE_SERVER_URL+"/api/v1/open/mobile/trip/search";

    public static String IMAGE_UPLOAD_URL = BASE_SERVER_URL+"/api/v1/open/mobile/image";

    public static String CREATE_TRIP_URL = BASE_SERVER_URL+"/api/v1/open/mobile/trip";

    //example:http://localhost:8080/api/v1/open/mobile/cart?email=akash.a2351@gmail.com
    public static String FETCH_CART_URL = BASE_SERVER_URL+"/api/v1/open/mobile/cart";

    public static String UPDATE_FCM_URL = BASE_SERVER_URL+"/api/v1/open/mobile/notify";

    //example: http://216.172.176.65/images/ravel-images/1492405942285.jpg
    public static String BASE_IMAGE_URL = BASE_SERVER_URL+"/images/ravel-images/";

    public static String GET_TRIP_URL = BASE_SERVER_URL+"/api/v1/open/mobile/trip";

    public static String TRENDING_PINDROPS_URL = BASE_SERVER_URL+"/api/v1/open/mobile/trendingpindrops/rating";

    // http://localhost:8080/api/v1/open/mobile/image
    public static LruCache<String, Bitmap> mImgCache;

    static{
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        Log.d("AppContext","Setting img cache to :"+cacheSize*2);
        mImgCache = new LruCache<String, Bitmap>(cacheSize*2) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static String buildHttpParams(Map<String,String> map){
        Uri.Builder builder = new Uri.Builder();
        for(Map.Entry<String,String> entry:map.entrySet()){
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
       return builder.build().getEncodedQuery();
    }

    public static void initializeImgCache(){
        try{
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 2/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            Log.d("AppContext","Setting img cache to :"+cacheSize *2+" kbs");

            mImgCache = new LruCache<String, Bitmap>(cacheSize*2) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }catch(Exception ex){   ex.printStackTrace();
        }
    }


    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Log.d("AppContext","Adding image to cache:"+key);
            AppContext.mImgCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        if(AppContext.mImgCache != null)
            return AppContext.mImgCache.get(key);
        else {
            AppContext.initializeImgCache();
            return null;
        }
    }

    public static void clearUserCache(){
        completedTrips.clear();
        wishlistTrips.clear();
        profile = null;
    }

    public static FirebaseUser fireBaseUser;

    //cahcing the users, trips and pindrops, avoiding unneccessary server calls
    public static Profile profile;

    public static String FIREBASE_TOPIC_NAME = "ravel-all";

    public static String FCM_TOKEN;

    public static List<Trip> trips = new ArrayList<>();

    public static List<Pindrop> pindrops = new ArrayList<>();

    public static List<Trip> completedTrips = new ArrayList<>();

    public static List<Trip> wishlistTrips = new ArrayList<>();

    public static Boolean USE_PICASSO = false;

    public static void setProfileData(Profile updatedProfile){
        profile = new Profile();
        if(updatedProfile.getCart()!=null) profile.setCart(updatedProfile.getCart());
        if(!IsEmpty(updatedProfile.getCreatedDate()))profile.setCreatedDate(updatedProfile.getCreatedDate());
        if(!IsEmpty(updatedProfile.getEmail()))profile.setEmail(updatedProfile.getEmail());
        if(!IsEmpty(updatedProfile.getName()))profile.setName(updatedProfile.getName());
        if(updatedProfile.getOrderIds()!=null)profile.setOrderIds(updatedProfile.getOrderIds());
        if(!IsEmpty(updatedProfile.getRole()))profile.setRole(updatedProfile.getRole());
        if(updatedProfile.getTripsCompletedIds()!=null)profile.setTripsCompletedIds(updatedProfile.getTripsCompletedIds());
        if(updatedProfile.getTripsWishlistIds()!=null)profile.setTripsWishlistIds(updatedProfile.getTripsWishlistIds());
        if(!IsEmpty(updatedProfile.getMobNo()))profile.setMobNo(updatedProfile.getMobNo());
        if(!IsEmpty(updatedProfile.getAddress()))profile.setAddress(updatedProfile.getAddress());
        if(!IsEmpty(updatedProfile.getImageUrl()))profile.setImageUrl(updatedProfile.getImageUrl());
    }

     public static boolean IsEmpty(String str){
        return (str == null || str.isEmpty());
    }

}
