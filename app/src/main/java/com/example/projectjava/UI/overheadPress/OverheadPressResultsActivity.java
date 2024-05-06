package com.example.projectjava.UI.overheadPress;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.BottomNavigationView;
import com.example.projectjava.UI.EndWorkoutActivity;
import com.example.projectjava.UI.MainActivity;
import com.example.projectjava.UI.bench.BenchActivity;
import com.example.projectjava.UI.bench.BenchResultsActivity;
import com.example.projectjava.UI.running.RunActivity;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.defaultExercises.OverheadPressExerciseData;

import java.time.Instant;

public class OverheadPressResultsActivity extends AppCompatActivity {
    private TextView textViewOHPStats;
    private EditText editTextReps;
    private EditText editTextWeight;
    private DatabaseHelper dh;
    private BottomNavigationView bottomNavigationView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overhead_press_results);

        dh = DatabaseHelper.getInstance();
        PremadeWorkout pw = dh.getActivePremadeWorkout();

        editTextReps = findViewById(R.id.editTextReps);
        editTextWeight = findViewById(R.id.editTextWeight);
        textViewOHPStats = findViewById(R.id.textViewOHPStats);
        float maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        float meanAcceleration = getIntent().getFloatExtra("meanAcceleration", 0);

        String results = "Overhead Press Stat Results:\n" +
                "Maximum acceleration: " + maxAcceleration + "\n" +
                "Mean acceleration: " + meanAcceleration + "\n";

        textViewOHPStats.setText(results);

        bottomNavigationView = new BottomNavigationView(this);

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(maxAcceleration, meanAcceleration, pw);
            }
        });

        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(pw);
            }
        });
    }

    private void saveData(float maxAcceleration, float meanAcceleration, PremadeWorkout pw) {
        try {
            int reps = Integer.parseInt(editTextReps.getText().toString().trim());
            float weight = Float.parseFloat(editTextWeight.getText().toString().trim());

            Instant instant = null;
            long timeStamp = -1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant = Instant.now();
                timeStamp = instant.getEpochSecond();
            }

            OverheadPressExerciseData exercise = new OverheadPressExerciseData(weight, maxAcceleration, reps, meanAcceleration, timeStamp);
            dh.addExerciseData(exercise);

            closeActivity(pw);
        } catch (NumberFormatException e) {
            Toast.makeText(OverheadPressResultsActivity.this, "Please enter valid numbers for reps and weight.", Toast.LENGTH_LONG).show();
        }
    }

    private void showConfirmationDialog(PremadeWorkout pw) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to leave without saving?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                closeActivity(pw);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void closeActivity(PremadeWorkout pw) {
        if(pw == null){ // Manual Workout
            Intent intent = new Intent(OverheadPressResultsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            dh.getPremadeWorkoutExercises(pw.getId()).addOnSuccessListener(premade_exercises -> {
                if(premade_exercises != null){
                    PremadeExercise pe = dh.getNextPremadeExercise(premade_exercises, true);
                    if(pe != null){
                        switch (pe.getExerciseName()){
                            case "Bench Press":
                                startActivity(new Intent(OverheadPressResultsActivity.this, BenchActivity.class));
                                break;
                            case "Overhead Press":
                                startActivity(new Intent(OverheadPressResultsActivity.this, OverheadPressActivity.class));
                                break;
                            case "Running":
                                startActivity(new Intent(OverheadPressResultsActivity.this, RunActivity.class));
                                break;
                            default:
                                System.out.println("Invalid premade exercise_name!");
                                break;
                        }
                    }else{ // Premade workout has ended
                        // dh.endPremadeWorkout();
                        Intent intent = new Intent(OverheadPressResultsActivity.this, EndWorkoutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("premade_workout_ended", true);
                        startActivity(intent);
                    }
                } else {
                    Log.e("OverheadPressResultsActivity", "Problema ao retirar exercícios do workout " + pw.getId());
                }
            }).addOnFailureListener(e -> {
                Log.e("DatabaseHelper", "Failed getting premade workout exercises");
            });
        }
    }
}
