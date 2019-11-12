package com.feed.sdk.push;

import android.app.Application;
import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.feed.sdk.push.common.Logs;
import com.feed.sdk.push.common.Pref;
import com.feed.sdk.push.exception.GoogleServiceJsonException;
import com.feed.sdk.push.model.ModelDeviceApp;
import com.feed.sdk.push.model.ModelFirebaseApp;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FeedSDK extends Application {

    private static final String TAG = "FeedSDK";

    protected static Class activityClass;
    protected static @DrawableRes int notificationIcon=R.drawable.ic_notification;

    public void setStartActivity(Class activityClass){
           FeedSDK.activityClass = activityClass;
    }

    public static void setNotificationIcon(@DrawableRes int icon){
        notificationIcon=icon;
    }

    public static void setEnabled(boolean enable){
        Pref.get(mContext).put(Const.PREF_ENABLE_KEY,enable);
    }

    public static boolean isEnabled(){
       return Pref.get(mContext).getBoolean(Const.PREF_ENABLE_KEY,true);
    }


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Logs.setEnabled(BuildConfig.DEBUG);
        //init network manager
        VolleyManager.init(this);
        //init firebase
        initializeApp(this);
        //debug logs
        Logs.i("ModelDeviceApp info...",true);
        ModelDeviceApp modelDeviceApp = ModelDeviceApp.getInstance(this);
        Logs.i("device_name",modelDeviceApp.device_name);
        Logs.i("device_uuid",modelDeviceApp.device_uuid);
        Logs.i("package_name",modelDeviceApp.package_name);
        Logs.i("app_name",modelDeviceApp.app_name);
        Logs.i("platform",modelDeviceApp.platform);

        FeedRegisterManager.invoke(this);

    }


    private void initializeApp(@NonNull Context context){
        try {
            ModelFirebaseApp modelFirebaseApp = ModelFirebaseApp.getInstance(this);

            Logs.i("ModelFirebaseApp model initiated successfully...",true);
            Logs.i("api_key",modelFirebaseApp.api_key);
            Logs.i("firebase_url",modelFirebaseApp.firebase_url);
            Logs.i("mobilesdk_app_id",modelFirebaseApp.mobilesdk_app_id);
            Logs.i("project_number",modelFirebaseApp.project_number);
            Logs.i("storage_bucket",modelFirebaseApp.storage_bucket);


            FirebaseApp.initializeApp(context, new FirebaseOptions.Builder().
                    setApiKey(modelFirebaseApp.api_key).
                    setApplicationId(modelFirebaseApp.mobilesdk_app_id).
                    setDatabaseUrl(modelFirebaseApp.firebase_url).
                    setGcmSenderId(modelFirebaseApp.project_number).
                    setStorageBucket(modelFirebaseApp.storage_bucket).build());

        } catch (GoogleServiceJsonException e) {
            e.printStackTrace();
        }
    }

    public static String getToken(Context context){
        return Pref.get(context).getString(FeedMessagingService.FCM_TOKEN,null);
    }



}
