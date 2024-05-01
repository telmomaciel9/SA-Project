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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);

        findViewById(R.id.begin_workout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dh.beginWorkout();
                // makeNotification();
                // startActivity(new Intent(BeginningActivity.this, MainActivity.class));
                startActivity(new Intent(BeginningActivity.this, WorkoutTypeActivity.class));
            }
        });

        findViewById(R.id.workout_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeginningActivity.this, WorkoutHistoryActivity.class));
            }
        });
    }
}