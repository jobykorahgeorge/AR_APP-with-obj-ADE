package com.adobe.intelliscan.network;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bento on 01/04/17.
 */

public class VolleyHandler {

    private static final String TAG = VolleyHandler.class.getSimpleName();
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static VolleyHandler mInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static NetworkListener mNetworkListener;

    public static synchronized VolleyHandler getInstance(Activity activity) {
        if(mInstance == null){
            mInstance = new VolleyHandler();
        }

        if (activity instanceof NetworkListener) {
            mNetworkListener = (NetworkListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement NetworkListener");
        }

        return mInstance;
    }

    public static synchronized VolleyHandler getInstance(Fragment fragment) {
        if(mInstance == null){
            mInstance = new VolleyHandler();
        }

        if (fragment instanceof NetworkListener) {
            mNetworkListener = (NetworkListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                    + " must implement NetworkListener");
        }

        return mInstance;
    }

    public void makeRequest(Context context, String url) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String urlWithParams = url;
        Log.d(TAG, "URL_DEFAULT:" + urlWithParams);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                urlWithParams,
                null,
                response -> {
                    Log.d(TAG, response.toString());
                    if(mNetworkListener != null) {
                        mNetworkListener.onResponse(response);
                    }
                },
                error -> {
//                    Log.d(TAG, "Error: " + error.getMessage());
//                    Log.d(TAG, "Error1: " + error.networkResponse.statusCode);
                    if(mNetworkListener != null) {
                        mNetworkListener.onError(error);
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };

        // Adding request to request queue
        addToRequestQueue(context, jsonObjReq, tag_json_obj);
        if(mNetworkListener != null) {
            mNetworkListener.onRequest();
        }
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void loadImage(Context context, String imageUrl, final ImageView imageView){
        Log.e(TAG, "Image Load : " + imageUrl);
        ImageLoader imageLoader = getImageLoader(context);
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                Log.e(TAG, "Image Load Response received!");
                if (response.getBitmap() != null) {
                    // load image into imageview
                    Bitmap bitmap = getScaledBitmap(response.getBitmap(), imageView.getWidth(), imageView.getHeight());
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }


    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    private ImageLoader getImageLoader(Context context) {
        getRequestQueue(context);
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapCache());
        }
        return this.mImageLoader;
    }

    private <T> void addToRequestQueue(Context context, Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(req);
    }

    private <T> void addToRequestQueue(Context context, Request<T> req) {
        req.setTag(TAG);
        getRequestQueue(context).add(req);
    }

    private HashMap<String, String> getHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int width, int height)
    {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int nHeight = height;
        int nWidth = height * bitmapWidth / bitmapHeight;

        return Bitmap.createScaledBitmap(bitmap, nWidth, nHeight, true);
    }

    public interface NetworkListener{
        void onRequest();
        void onResponse(JSONObject joResponse);
        void onError(VolleyError error);
    }
}
