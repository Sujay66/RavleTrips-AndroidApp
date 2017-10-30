package com.raveltrips.android.ravel.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import retrofit2.http.POST;

/**
 * Revised by kevin on 3/9/2016.
 */
public class MyUtility {

    //timeout set to 15 secs
    private static final int HTTP_REQUEST_TIMEOUT = 15000;

    // Download an image using HTTP Get Request
    public static Bitmap downloadImageusingHTTPGetRequest(String urlString) {
        Bitmap image=null, line;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT);
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                image = getImagefromStream(stream);
            }
            httpConnection.disconnect();
        }  catch (UnknownHostException e1) {
            Log.d("MyDebugMsg", "UnknownHostexception in sendHttpGetRequest");
            e1.printStackTrace();
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in sendHttpGetRequest");
            ex.printStackTrace();
        }
        return image;
    }

    // Get an image from the input stream
    private static Bitmap getImagefromStream(InputStream stream) {
        Bitmap bitmap = null;
        if(stream!=null) {
            bitmap = BitmapFactory.decodeStream(stream);
            try {
                stream.close();
            }catch (IOException e1) {
                Log.d("MyDebugMsg", "IOException in getImagefromStream()");
                e1.printStackTrace();
            }
        }
        return bitmap;
    }


    // Download JSON data (string) using HTTP Get Request
    public static String downloadJSONusingHTTPGetRequest(String urlString) {
        String jsonString=null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; PPC; en-US; rv:1.3.1)");
            httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT);
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                jsonString = getStringfromStream(stream);
            } else {
                Log.d("MyDebugMsg", "received response code:"+httpConnection.getResponseCode());
            }
            httpConnection.disconnect();
        }  catch (UnknownHostException e1) {
            Log.d("MyDebugMsg", "UnknownHostexception in downloadJSONusingHTTPGetRequest");
            e1.printStackTrace();
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in downloadJSONusingHTTPGetRequest:"+ex.getMessage());
        }
        return jsonString;
    }

    // Get a string from an input stream
    private static String getStringfromStream(InputStream stream){
        String line, jsonString=null;
        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                jsonString = out.toString();
            } catch (IOException ex) {
                Log.d("MyDebugMsg", "IOException in downloadJSON()");
                ex.printStackTrace();
            }
        }
        return jsonString;
    }

    // Load JSON string from a local file (in the asset folder)
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null, line;
        try {
            InputStream stream = context.getAssets().open(fileName);
            if (stream != null) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder out = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                json = out.toString();
            }
        } catch (IOException ex) {
            Log.d("MyDebugMsg", "IOException in loadJSONFromAsset()");
            ex.printStackTrace();
        }
        return json;
    }


    // Send json data via HTTP POST Request
    public static void sendHttPostRequest(String urlString, JSONObject json){
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(urlString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setDoOutput(true);
            httpConnection.setChunkedStreamingMode(0);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(json.toString());
            out.close();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                /*while ((line = reader.readLine()) != null) {
                    //Log.d("MyDebugMsg:PostRequest", line);  // for debugging purpose
                }*/
                reader.close();
                Log.d("MyDebugMsg:PostRequest", "POST request returns ok");
            } else Log.d("MyDebugMsg:PostRequest", "POST request returns error");
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in sendHttpPostRequest");
            ex.printStackTrace();
        }

        if (httpConnection != null) httpConnection.disconnect();
    }


    // Send json data via HTTP POST Request
    public static String sendHttPostRequest(String urlString, String jsonData){
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(urlString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            httpConnection.setChunkedStreamingMode(0);

            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(jsonData);
            out.close();

           // Log.d("MyDebugMsg:PostRequest", "post url:"+urlString);

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                String jsonString = getStringfromStream(stream);
                Log.d("MyDebugMsg:PostRequest", "POST request returns ok");
                if (httpConnection != null) httpConnection.disconnect();
                return jsonString;
            } else {
                Log.d("MyDebugMsg:PostRequest", "POST response code:"+httpConnection.getResponseCode());
               // Log.d("MyDebugMsg:PostRequest", "POST request returns error");
                if (httpConnection != null) httpConnection.disconnect();
                return null;
            }
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in sendHttpPostRequest:"+ex.getMessage());
            if (httpConnection != null) httpConnection.disconnect();
            return null;
        }
    }

    // Send json data via HTTP POST Request
    public static String sendHttPutRequest(String urlString, String jsonData){
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(urlString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setReadTimeout(HTTP_REQUEST_TIMEOUT);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            httpConnection.setChunkedStreamingMode(0);

            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(jsonData);
            out.close();

            // Log.d("MyDebugMsg:PostRequest", "post url:"+urlString);

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                String jsonString = getStringfromStream(stream);
                Log.d("MyDebugMsg:PutRequest", "PUT request returns ok");
                if (httpConnection != null) httpConnection.disconnect();
                return jsonString;
            } else {
                Log.d("MyDebugMsg:PutRequest", "PUT response code:"+httpConnection.getResponseCode());
                // Log.d("MyDebugMsg:PostRequest", "POST request returns error");
                if (httpConnection != null) httpConnection.disconnect();
                return null;
            }
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in sendHttpPutRequest:"+ex.getMessage());
            if (httpConnection != null) httpConnection.disconnect();
            return null;
        }
    }
}
