package com.example.projectjava.UI;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;

public class WorkoutTypeActivity extends AppCompatActivity {
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_type);
        db = DatabaseHelper.getInstance();

        findViewById(R.id.btnPremadeWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WorkoutTypeActivity.this, WorkoutPremadeActivity.class));
            }
        });

        findViewById(R.id.btnManualWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.beginWorkout();
                Notification n = new Notification(getApplicationContext(), WorkoutTypeActivity.this);
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                n.makeNotification("Your manual workout has started.", "Give it your best!", EndWorkoutActivity.class,1, nm);
                startActivity(new Intent(WorkoutTypeActivity.this, MainActivity.class));
            }
        });
    }
}
