package com.raveltrips.android.ravel.async;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash Anjanappa on 16-04-2017.
 */

public class DownloadJsonAsyncPutTask extends AsyncTask<String, Void, List<String>> {

    private AsyncComplete asyncComplete;
    private String bodyContent;

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public DownloadJsonAsyncPutTask(AsyncComplete async){
        this.asyncComplete = async;
    }

    public DownloadJsonAsyncPutTask(){}

    public AsyncComplete getAsyncComplete() {
        return asyncComplete;
    }

    public void setAsyncComplete(AsyncComplete asyncComplete) {
        this.asyncComplete = asyncComplete;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> jsons = new ArrayList<>();
        for (String url : strings){
            try {
                // Log.d("DJsonAsyncPutTask","Calling put with body:"+bodyContent);
               String resp = MyUtility.sendHttPutRequest(url,bodyContent);
                if(resp!=null)jsons.add(resp);
            } catch (Exception e) {
                Log.d("DJsonAsyncPutTask","Exception downloading json:"+e);
            }
        }
        return jsons;
    }

    @Override
    protected void onPostExecute(List<String> jsons){
        // Log.d("DownloadJsonAsyncTask","Recieved jsons from server of size:"+jsons.size());
        asyncComplete.OnJsonAsyncCompleted(jsons);
    }
}
