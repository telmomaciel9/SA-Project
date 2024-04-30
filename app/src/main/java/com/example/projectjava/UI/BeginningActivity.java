package com.example.projectjava.UI;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;

public class BeginningActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);
        DatabaseHelper dh = DatabaseHelper.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(BeginningActivity.this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(BeginningActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        findViewById(R.id.begin_workout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.beginWorkout();
                //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                //}
                makeNotification();
                startActivity(new Intent(BeginningActivity.this, MainActivity.class));
            }
        });

        findViewById(R.id.workout_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeginningActivity.this, WorkoutHistoryActivity.class));
            }
        });
    }

    public void makeNotification(){
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);

        builder.setSmallIcon(R.drawable.ic_notificaitons)
                .setUsesChronometer(true)
                .setContentTitle("Workout is active.")
                .setContentText("Give it your best!")
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);  // Set as ongoing notification

        Intent intent = new Intent(getApplicationContext(), EndWorkoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Updated PendingIntent creation with FLAG_IMMUTABLE
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;  // Use FLAG_MUTABLE if necessary
        }
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, flags);
        builder.setContentIntent(pi);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
        nm.notify(0, builder.build());
    }
}