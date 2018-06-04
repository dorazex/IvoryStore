package com.dorazouri.IvoryStore.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Map;

import com.dorazouri.IvoryStore.R;
import com.dorazouri.IvoryStore.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG ="PushNotificationService";


    public PushNotificationService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "onMessageReceived() >>");
        String title = "title";
        String body = "body";
        int icon = R.drawable.ic_notifications_black_24dp;
        Uri soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Map<String,String> data;
        RemoteMessage.Notification notification;


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getNotification() == null) {
            Log.e(TAG, "onMessageReceived() >> Notification is empty");
        } else {
            notification = remoteMessage.getNotification();
            title = notification.getTitle();
            body = notification.getBody();
            Log.e(TAG, "onMessageReceived() >> title: " + title + " , body="+body);
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() == 0) {
            Log.e(TAG, "onMessageReceived() << No data doing nothing");
            return;
        }


        //parse the data
        data = remoteMessage.getData();
        Log.e(TAG, "Message data : " + data);

        String value = data.get("title");
        if (value != null) {
            title = value;
        }

        value = data.get("body");
        if (value != null) {
            body = value;
        }

        value = data.get("small_icon");
        if (value != null  && value.equals("alarm")) {
            icon = R.drawable.ic_alarm_black_24dp;
        }
        value = data.get("sound");
        if (value != null) {
            if (value.equals("alert")) {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            } else if (value.equals("ringtone")) {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        Intent intent = new Intent(this, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, null)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(icon)
                        .setSound(soundRri);


        value = data.get("action");
        if (value != null) {
            if (value.contains("share")) {
                PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_share_black_24dp,"Share",pendingShareIntent));
            }
            if (value.contains("go to sale")) {
                PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_shopping_cart_black_24dp,"Go to sale!",pendingShareIntent));
            }

        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());

        Log.e(TAG, "onMessageReceived() <<");

    }
}
