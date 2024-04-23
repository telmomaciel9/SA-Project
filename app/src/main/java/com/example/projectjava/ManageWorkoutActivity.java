package com.example.projectjava;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.running.RunActivity;
import com.example.projectjava.running.RunResultsActivity;

import java.util.Locale;

public class ManageWorkoutActivity extends AppCompatActivity {
    private TextView textViewTimer;
    private EditText editTextNotes;
    private EditText editTextType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_workout);

        findViewById(R.id.btnSaveWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(0);
                String notes = editTextNotes.getText().toString();
                String type = editTextType.getText().toString();

                startActivity(new Intent(ManageWorkoutActivity.this, BeginningActivity.class));
            }
        });
    }
}
