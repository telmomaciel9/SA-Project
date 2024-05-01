package com.example.projectjava.UI;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.projectjava.R;

public class Notification {
    private Context context;
    private Activity activity;
    public Notification(Context context, Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        this.activity = activity;
        this.context = context;
    }

    public void makeNotification(String title, String text, Class endClass, int notificationId, NotificationManager nm){
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);

        builder.setSmallIcon(R.drawable.ic_notificaitons)
                .setUsesChronometer(true)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);  // Set as ongoing notification

        Intent intent = new Intent(context, endClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Updated PendingIntent creation with FLAG_IMMUTABLE
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;  // Use FLAG_MUTABLE if necessary
        }
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, flags);
        builder.setContentIntent(pi);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel nc = nm.getNotificationChannel(channelID);
            if(nc == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                nc = new NotificationChannel(channelID, "Time: 00:00:00", importance);
                nc.setLightColor(Color.GREEN);
                nc.enableVibration(true);
                nm.createNotificationChannel(nc);
            }
        }
        nm.notify(notificationId, builder.build());
    }
}
