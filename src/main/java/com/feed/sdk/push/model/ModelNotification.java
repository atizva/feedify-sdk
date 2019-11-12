package com.feed.sdk.push.model;



import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelNotification {

    public int id;
    public String title;
    public String body;
    public String icon;
    public String image;
    public String url;
    public String type;
    public String button;

    public static ModelNotification getInstance(String jsonString) throws JSONException {
        JSONObject root = new JSONObject(jsonString);
        ModelNotification notification = new ModelNotification();
        notification.type=root.getString("type");
        notification.id=Integer.parseInt(root.getString("id"));
        notification.title=root.getString("title");
        notification.body=root.getString("body");

        notification.url=root.getString("url");
        if(notification.url!=null&&notification.url.trim().isEmpty())
            notification.url=null;
       /* notification.button=root.getString("button");
        if(notification.button!=null&&notification.button.trim().isEmpty())
            notification.button=null;*/
        notification.icon=root.getString("icon");
        if(notification.icon!=null&&notification.icon.trim().isEmpty())
            notification.icon=null;
        notification.image=root.getString("image");
        if(notification.image!=null&&notification.image.trim().isEmpty())
            notification.image=null;

        return notification;
    }


    public static ModelNotification getInstance(RemoteMessage remoteMessage) throws JSONException {
        JSONObject root = new JSONObject(remoteMessage.getData().get("promotional"));
        ModelNotification notification = new ModelNotification();
        notification.type=root.getString("type");
        notification.id=root.getInt("id");
        notification.title=root.getString("title");
        notification.body=root.getString("body");

        notification.url=root.getString("url");
        if(notification.url!=null&&notification.url.trim().isEmpty())
            notification.url=null;
        notification.button=root.getString("button");
        if(notification.button!=null&&notification.button.trim().isEmpty())
            notification.button=null;
        notification.icon=root.getString("icon");
        if(notification.icon!=null&&notification.icon.trim().isEmpty())
            notification.icon=null;
        notification.image=root.getString("image");
        if(notification.image!=null&&notification.image.trim().isEmpty())
            notification.image=null;

        return notification;
    }


}
