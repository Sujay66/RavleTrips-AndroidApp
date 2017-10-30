package com.raveltrips.android.ravel.async;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Akash Anjanappa on 13-04-2017.
 */

public interface AsyncComplete {

    //recive a list of downloaded json for the given url list
    public void OnJsonAsyncCompleted(List<String> json);

}
