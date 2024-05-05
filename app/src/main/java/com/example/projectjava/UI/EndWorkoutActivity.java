package com.example.projectjava.UI;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;

public class EndWorkoutActivity extends AppCompatActivity {
    private TextView textViewWorkoutType;
    private EditText editTextWorkoutName;
    private EditText editTextNotes;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_workout);
        textViewWorkoutType = findViewById(R.id.textViewWorkoutType);
        editTextWorkoutName = findViewById(R.id.editTextWorkoutName);
        editTextNotes = findViewById(R.id.editTextNotes);
        bottomNavigationView = new BottomNavigationView(this, false);

        DatabaseHelper db = DatabaseHelper.getInstance();

        boolean premade_workout = getIntent().getBooleanExtra("premade_workout", false);
        if(premade_workout){
            textViewWorkoutType.setText("Premade");
            editTextWorkoutName.setText(db.getActivePremadeWorkout().getWorkout_name());
            editTextWorkoutName.setEnabled(false);
            db.endPremadeWorkout();
        }else{
            textViewWorkoutType.setText("Manual");
        }

        findViewById(R.id.btnSaveWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);
                String notes = editTextNotes.getText().toString();
                String type = textViewWorkoutType.getText().toString();
                String workout_name = editTextWorkoutName.getText().toString();

                db.finishWorkout(type, notes, workout_name);

                startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
            }
        });

        findViewById(R.id.btnCancelWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setIsWorkoutActive(false);
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Confirm cancellation.")
                        .setMessage("Are you sure you want to cancel the workout?")
                        .setPositiveButton("Yes", (dialog, id) -> cancelWorkout())
                        .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                        .show();
            }
        });
    }

    // Cancel workout by not saving it
    public void cancelWorkout(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);

        startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
    }
}
