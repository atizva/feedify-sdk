package com.feed.sdk.push;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.feed.sdk.push.common.Logs;
import com.feed.sdk.push.common.Pref;
import com.feed.sdk.push.model.ModelDeviceApp;

import java.util.HashMap;
import java.util.Map;

public class FeedRegisterManager {

    private Context context;

    public FeedRegisterManager(Context context) {
        this.context = context;
    }

    public static void invoke(Context context){
        String token = Pref.get(context).getString(FeedMessagingService.FCM_TOKEN, null);
        if(token!=null) {
            FeedRegisterManager fm = new FeedRegisterManager(context);
            fm.register(context,VolleyManager.getInstance(),ModelDeviceApp.getInstance(context), token);
        }
    }

    public void register(final Context context, VolleyManager volleyManager, @NonNull final ModelDeviceApp modelDeviceApp, @NonNull final String token) {
       //put register logic here


        final String feedify_user = context.getString(R.string.feedify_user);
        final String feedify_dkey = context.getString(R.string.feedify_dkey);
        final String feedify_domain = context.getString(R.string.feedify_domain);

        if(!feedify_dkey.trim().isEmpty()&&!feedify_dkey.trim().isEmpty()&&!feedify_domain.isEmpty()) {
            Logs.i("Registering token...",true);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.PUSH_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Logs.i("Token sent!!!",token);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logs.e("That didn't work!");
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("agent", "Feedify Android Application 1.0");
                    MyData.put("endpoint", "https://fcm.googleapis.com/fcm/send/" + token);
                    MyData.put("registration_id", token);
                    MyData.put("user_id", feedify_user);
                    MyData.put("domkey", feedify_dkey);
                    MyData.put("referrer", feedify_domain);
                    MyData.put("browser", "Android Application");
                    MyData.put("uuid", modelDeviceApp.device_uuid);
                    return MyData;
                }
            };

            volleyManager.addRequest(stringRequest, "token_register");

        }
        else {
            Logs.e("Feedify","Missing (feedify_user, feedify_dkey, feedify_domain) strings please make sure to update your feedify credentials in strings.xml.");
        }
    }
}
