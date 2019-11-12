package com.feed.sdk.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.feed.sdk.push.common.Cache;
import com.feed.sdk.push.model.ModelNotification;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

public class NotificationProvider {

    private ModelNotification model;

    NotificationProvider(ModelNotification model){
        this.model = model;
    }

    private void showNotification(final VolleyManager volleyManager,final Context context){
        String feed_channel="feed_channel";
        createNotificationChannel(context,feed_channel,"Feed", "Feed notification.", NotificationManager.IMPORTANCE_MAX);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( context, feed_channel)
                .setSmallIcon(FeedSDK.notificationIcon)
                .setContentTitle(model.title)
                .setContentText(model.body)
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(model.body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, model.id, getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        if(model.button!=null)
            builder.addAction(R.drawable.ic_action_open,model.button,pendingIntent);

        if(model.icon!=null&&model.image==null){
            Bitmap bitmap = Cache.getBitmap(context,model.icon);
            if(bitmap==null) {
                ImageRequest request = new ImageRequest(model.icon, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Cache.save_bitmap(context,model.icon,response);
                        builder.setLargeIcon(response);
                        NotificationManagerCompat.from(context).notify(model.id, builder.build());
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                volleyManager.addRequest(request, "remote_icon");
            }
            else {
                builder.setLargeIcon(bitmap);
                NotificationManagerCompat.from(context).notify(model.id, builder.build());
            }


        }
        else if(model.icon==null&&model.image!=null){
            Bitmap bitmap = Cache.getBitmap(context,model.image);
            if(bitmap==null) {
                ImageRequest requestImage = new ImageRequest(model.image, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Cache.save_bitmap(context,model.image,response);
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                        NotificationManagerCompat.from(context).notify(model.id, builder.build());
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                volleyManager.addRequest(requestImage, "remote_image");
            }
            else {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
                NotificationManagerCompat.from(context).notify(model.id, builder.build());
            }
        }

        else if(model.icon!=null&&model.image!=null) {
            final Bitmap bitmap = Cache.getBitmap(context, model.icon);
            if (bitmap == null) {
                ImageRequest request = new ImageRequest(model.icon, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Cache.save_bitmap(context,model.icon,response);
                        builder.setLargeIcon(response);
                        Bitmap bitmapLarge = Cache.getBitmap(context, model.image);
                        if(bitmapLarge==null) {
                            ImageRequest requestImage = new ImageRequest(model.image, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    Cache.save_bitmap(context,model.image,response);
                                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                                    NotificationManagerCompat.from(context).notify(model.id, builder.build());
                                }
                            }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                            volleyManager.addRequest(requestImage, "remote_image");
                        }
                        else {
                            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmapLarge));
                            NotificationManagerCompat.from(context).notify(model.id, builder.build());
                        }

                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                volleyManager.addRequest(request, "remote_icon");
            }
            else {
                builder.setLargeIcon(bitmap);
                Bitmap bitmapLarge = Cache.getBitmap(context, model.image);
                if(bitmapLarge==null) {
                    ImageRequest requestImage = new ImageRequest(model.image, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            Cache.save_bitmap(context,model.image,response);
                            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                            NotificationManagerCompat.from(context).notify(model.id, builder.build());
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    volleyManager.addRequest(requestImage, "remote_image");
                }
                else {
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmapLarge));
                    NotificationManagerCompat.from(context).notify(model.id, builder.build());
                }
            }
        }
        else {
            NotificationManagerCompat.from(context).notify(model.id, builder.build());
        }


    }

    private Intent getIntent(Context context){
        if(FeedSDK.activityClass!=null){
            Intent intent = new Intent(context, FeedSDK.activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return intent;
        }
        else if(model.url!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(model.url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return intent;
        }
        else {
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return intent;
        }
    }

    public static void onMessageReceived(Context context, String jsonString){
        try {
            ModelNotification model = ModelNotification.getInstance(jsonString);
            NotificationProvider np = new NotificationProvider(model);
            np.showNotification(VolleyManager.getInstance(),context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void onMessageReceived(Context context, RemoteMessage remoteMessage){
        try {
            ModelNotification model = ModelNotification.getInstance(remoteMessage);
            NotificationProvider np = new NotificationProvider(model);
            np.showNotification(VolleyManager.getInstance(),context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void createNotificationChannel(Context context,String channel_id, String channel_name, String channel_description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, importance);
            channel.setDescription(channel_description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
