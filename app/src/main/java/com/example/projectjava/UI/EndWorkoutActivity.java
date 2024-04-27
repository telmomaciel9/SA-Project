package com.example.projectjava.UI;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;

public class EndWorkoutActivity extends AppCompatActivity {
    private EditText editTextNotes;
    private EditText editTextType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_workout);
        editTextNotes = findViewById(R.id.editTextNotes);
        editTextType = findViewById(R.id.editTextType);
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        findViewById(R.id.btnSaveWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(0);
                String notes = editTextNotes.getText().toString();
                String type = editTextType.getText().toString();

                db.finishWorkout(type, notes);

                startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
            }
        });

        // Cancel workout by not saving it
        findViewById(R.id.btnCancelWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(0);

                startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
            }
        });
    }
}
