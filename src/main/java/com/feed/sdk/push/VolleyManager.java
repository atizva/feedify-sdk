package com.feed.sdk.push;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyManager {


    private RequestQueue mRequestQueue;
    private static VolleyManager mInstance;
    private Context context;


    public VolleyManager(Context context){
        this.context = context;
    }


    public static void init(Context context){
         mInstance = new VolleyManager(context);
    }


    public static synchronized VolleyManager getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);
        return mRequestQueue;
    }

    public <T> void addRequest(Request<T> req, @NonNull String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
