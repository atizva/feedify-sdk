package com.feed.sdk.push;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.feed.sdk.push.common.Logs;
import com.feed.sdk.push.common.Pref;

public class DataProvider {

    public static void loadData(final Context context){
        String token =  Pref.get(context).getString(FeedMessagingService.FCM_TOKEN,null);
        if(token!=null) {
            String dataUrl = String.format("%s=%s", Const.DELIVER_ENDPOINT, token);
            VolleyManager vm = VolleyManager.getInstance();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, dataUrl,
                    new Response.Listener<String>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onResponse(String response) {
                            Logs.d("DataProvider", "Response is: " + response);
                            NotificationProvider.onMessageReceived(context, response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Logs.d("DataProvider", "That didn't work!");
                }
            });
            vm.addRequest(stringRequest, "get_data");
        }
    }


}
