package com.example.projectjava.UI.bench;

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
import com.example.projectjava.UI.PremadeExercisesAdapter;
import com.example.projectjava.UI.overheadPress.OverheadPressActivity;
import com.example.projectjava.UI.running.RunActivity;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.defaultExercises.BenchExerciseData;
import com.example.projectjava.data.DatabaseHelper;

import java.time.Instant;

public class BenchResultsActivity extends AppCompatActivity {
    private TextView textViewBenchStats;
    private EditText editTextReps;
    private EditText editTextWeight;
    private DatabaseHelper dh;
    private BottomNavigationView bottomNavigationView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench_results);

        dh = DatabaseHelper.getInstance();
        PremadeWorkout pw = dh.getActivePremadeWorkout();

        editTextReps = findViewById(R.id.editTextReps);
        editTextWeight = findViewById(R.id.editTextWeight);
        textViewBenchStats = findViewById(R.id.textViewBenchStats);
        float maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        float meanAcceleration = getIntent().getFloatExtra("meanAcceleration", 0);

        String results = "Bench Press Stat Results:\n" +
                "Maximum acceleration: " + maxAcceleration + "\n" +
                "Mean acceleration: " + meanAcceleration + "\n";

        textViewBenchStats.setText(results);

        bottomNavigationView = new BottomNavigationView(this, true);

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
            instant = Instant.now();
            timeStamp = instant.getEpochSecond();
            BenchExerciseData exercise = new BenchExerciseData(weight, maxAcceleration, reps, meanAcceleration, timeStamp);
            dh.addExerciseData(exercise);

            closeActivity(pw);
        } catch (NumberFormatException e) {
            Toast.makeText(BenchResultsActivity.this, "Please enter valid numbers for reps and weight.", Toast.LENGTH_LONG).show();
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
        if(pw == null){ // Manual workout
            Intent intent = new Intent(BenchResultsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{ // Premade Workout
            dh.getPremadeWorkoutExercises(pw.getId()).addOnSuccessListener(premade_exercises -> {
                if(premade_exercises != null){
                    PremadeExercise pe = dh.getNextPremadeExercise(premade_exercises);
                    if(pe != null){
                        switch (pe.getExerciseName()){
                            case "Bench Press":
                                startActivity(new Intent(BenchResultsActivity.this, BenchActivity.class));
                                break;
                            case "Overhead Press":
                                startActivity(new Intent(BenchResultsActivity.this, OverheadPressActivity.class));
                                break;
                            case "Running":
                                startActivity(new Intent(BenchResultsActivity.this, RunActivity.class));
                                break;
                            default:
                                System.out.println("Invalid premade exercise_name!");
                                break;
                        }
                    }else{ // Premade workout has ended
                        // dh.endPremadeWorkout();
                        Intent intent = new Intent(BenchResultsActivity.this, EndWorkoutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("premade_workout", true);
                        startActivity(intent);
                    }
                } else {
                    Log.e("BenchResultsActivity", "Problema ao retirar exercícios do workout " + pw.getId());
                }
            }).addOnFailureListener(e -> {
                Log.e("DatabaseHelper", "Failed getting premade workout exercises");
            });
        }
    }
}
