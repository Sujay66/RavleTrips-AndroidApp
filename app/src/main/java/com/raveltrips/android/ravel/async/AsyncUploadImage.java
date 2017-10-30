package com.raveltrips.android.ravel.async;

import android.os.AsyncTask;
import android.util.Log;

import com.raveltrips.android.ravel.utils.AppContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Akash Anjanappa on 16-04-2017.
 */

public class AsyncUploadImage extends AsyncTask<String, Integer, String> {

    private AsyncComplete asyncComplete;
    private String filePath = null;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public AsyncUploadImage(AsyncComplete async){
        this.asyncComplete = async;
    }

    public AsyncUploadImage(){}

    public AsyncComplete getAsyncComplete() {
        return asyncComplete;
    }

    public void setAsyncComplete(AsyncComplete asyncComplete) {
        this.asyncComplete = asyncComplete;
    }

    @Override
    protected String doInBackground(String... params) {
        return uploadFile(params[0]);
    }

    @SuppressWarnings("deprecation")
    private String uploadFile(String serverUrl) {

        if(filePath == null){
            Log.d("AsyncUploadImage", "filepath is null..please set filepath first!!");
            return null;
        }
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(serverUrl);

        try {
            AndroidMultipartEntity entity = new AndroidMultipartEntity(
                    new AndroidMultipartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            //publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(filePath);

            // Adding file data to http body
            entity.addPart("file", new FileBody(sourceFile));
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                Log.d("AsyncUploadImage", "error uploading image: code:"+statusCode);
            }

        } catch (ClientProtocolException e) {
            Log.d("AsyncUploadImage", "error uploading image: ClientProtocolException:"+e);
        } catch (Exception e) {
            Log.d("AsyncUploadImage", "error uploading image: IOException:"+e);
        }

        //on failure, response string will be null
        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("AsyncUploadImage", "Response from server: " + result);
        asyncComplete.OnJsonAsyncCompleted(Arrays.asList(result));
    }
}
