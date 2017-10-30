package com.raveltrips.android.ravel.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.raveltrips.android.ravel.async.AsyncComplete;
import com.raveltrips.android.ravel.async.DownloadJsonAsyncPostTask;
import com.raveltrips.android.ravel.models.FcmToken;
import com.raveltrips.android.ravel.utils.AppContext;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Akash Anjanappa on 22-04-2017.
 */

public class RavelFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "RavelFbIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        try{
            String email = null;
            if(AppContext.profile!=null && AppContext.profile.getEmail()!=null)
                email = AppContext.profile.getEmail();
            else {
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }
            updateFcmTokenToServer(email,token);
        }catch(Exception ex){ ex.printStackTrace();}
    }

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

    /*public void updateFcmTokenToServer(String email, String token) {
        Log.i(TAG, "sending fcm token to server..");
        if (token != null && email != null) {
            try {
                AsyncHttpClient client = new AsyncHttpClient();
                FcmToken fcmToken = new FcmToken(email, token);
                Gson gson = new Gson();
                String body = gson.toJson(fcmToken).toString();
                ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.setMaxRetriesAndTimeout(3, 30000);
                client.setUserAgent("android-async-http-1.4.9");
                client.post(this, AppContext.UPDATE_FCM_URL, entity, "application/json; charset=utf8", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            String resp = new String(responseBody);
                            JSONObject responseModel = new JSONObject(resp);
                            String status = responseModel.getString("status");
                            if (status != null && status.equalsIgnoreCase("200")) {
                                Log.i(TAG, "successfully updated fcm token with server");
                            } else {
                                Log.i(TAG, "failed to update fcm token: status:" + status + " error:" + responseModel.getString("message"));
                            }
                        }catch(Exception ex){ex.printStackTrace();}
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i(TAG,"Fcm token server call failed: status:"+statusCode);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/

}
