package com.example.projectjava.UI;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.bench.BenchActivity;
import com.example.projectjava.UI.bench.BenchResultsActivity;
import com.example.projectjava.UI.overheadPress.OverheadPressActivity;
import com.example.projectjava.UI.running.RunActivity;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;

public class EndWorkoutActivity extends AppCompatActivity {
    private TextView textViewWorkoutType;
    private EditText editTextWorkoutName;
    private EditText editTextNotes;
    private Button btnResumeWorkout;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_workout);
        textViewWorkoutType = findViewById(R.id.textViewWorkoutType);
        editTextWorkoutName = findViewById(R.id.editTextWorkoutName);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnResumeWorkout = findViewById(R.id.btnResumeWorkout);
        bottomNavigationView = new BottomNavigationView(this);

        db = DatabaseHelper.getInstance();

        PremadeWorkout pw = db.getActivePremadeWorkout();
        if(pw != null){ // It's a premade workout
            textViewWorkoutType.setText("Premade");
            editTextWorkoutName.setText(db.getActivePremadeWorkout().getWorkout_name());
            editTextWorkoutName.setEnabled(false);
        }else{
            textViewWorkoutType.setText("Manual");
        }

        boolean warning = getIntent().getBooleanExtra("warning", false);
        if(warning){ // The user tried to start a new workout without ending the current one
            Toast.makeText(getApplicationContext(), "You need to end the current workout to start another!", Toast.LENGTH_LONG).show();
        }

        boolean premade_workout_ended = getIntent().getBooleanExtra("premade_workout_ended", false);
        if(premade_workout_ended){
            Toast.makeText(getApplicationContext(), "Your premade workout as ended!", Toast.LENGTH_SHORT).show();
            btnResumeWorkout.setEnabled(false); // Premade workouts that ended can't continue
        }

        setupButtons(pw);
    }

    public void setupButtons(PremadeWorkout pw){
        findViewById(R.id.btnResumeWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pw != null){
                    db.getPremadeWorkoutExercises(pw.getId()).addOnSuccessListener(exercises -> {
                        if(exercises != null){
                            PremadeExercise pe = db.getNextPremadeExercise(exercises, false);
                            switch (pe.getExerciseName()){
                                case "Bench Press":
                                    startActivity(new Intent(EndWorkoutActivity.this, BenchActivity.class));
                                    break;
                                case "Overhead Press":
                                    startActivity(new Intent(EndWorkoutActivity.this, OverheadPressActivity.class));
                                    break;
                                case "Running":
                                    startActivity(new Intent(EndWorkoutActivity.this, RunActivity.class));
                                    break;
                                default:
                                    System.out.println("Invalid premade exercise_name!");
                                    break;
                            }
                        } else {
                            Log.e("EndWorkoutActivity", "Problema ao retirar exercícios do workout " + pw.getId());
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("EndWorkoutActivity", "Failed getting workout exercises");
                    });
                }else{
                    startActivity(new Intent(EndWorkoutActivity.this, MainActivity.class));
                }
            }
        });

        findViewById(R.id.btnSaveWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);
                String notes = editTextNotes.getText().toString();
                String type = textViewWorkoutType.getText().toString();
                String workout_name = editTextWorkoutName.getText().toString();

                db.finishWorkout(type, notes, workout_name);
                db.endPremadeWorkout();
                startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
            }
        });

        findViewById(R.id.btnCancelWorkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EndWorkoutActivity.this)
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

        db.endPremadeWorkout();
        db.setIsWorkoutActive(false);
        startActivity(new Intent(EndWorkoutActivity.this, BeginningActivity.class));
    }
}
