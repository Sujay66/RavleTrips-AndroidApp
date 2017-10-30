package com.raveltrips.android.ravel.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.raveltrips.android.ravel.fragments.DetailPinDropFragment;
import com.raveltrips.android.ravel.utils.AppContext;

import java.lang.ref.WeakReference;

/**
 * Created by Akash Anjanappa on 24-03-2017.
 */

public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

    private final WeakReference<ImageView> imageViewWeakReference;

    public DownloadImageAsyncTask(ImageView imageView){
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        for (String url : urls){
            try {
                //Log.d("DownloadImageAsyncTask", "Downloading image from server:" + url);
                bitmap = MyUtility.downloadImageusingHTTPGetRequest(url);
                if(bitmap!=null){
                    AppContext.addBitmapToMemoryCache(url,bitmap);
                }
            }catch(Exception ex){
                Log.d("DownloadImageAsyncTask","Exception downloading image:"+ex);
            }
           }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        //Log.d("DownloadImageAsyncTask","Recieved images from server ");
        if(imageViewWeakReference!= null && bitmap!=null){
            final ImageView imageView = imageViewWeakReference.get();
            if(imageView!=null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
