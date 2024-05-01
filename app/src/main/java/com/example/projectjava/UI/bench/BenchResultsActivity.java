package com.example.projectjava.UI.bench;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.MainActivity;
import com.example.projectjava.data.defaultExercises.BenchExerciseData;
import com.example.projectjava.data.DatabaseHelper;

import java.time.Instant;

public class BenchResultsActivity extends AppCompatActivity {
    private TextView textViewBenchStats;
    private EditText editTextReps;
    private EditText editTextWeight;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench_results);

        DatabaseHelper dh = DatabaseHelper.getInstance();

        editTextReps = findViewById(R.id.editTextReps);
        editTextWeight = findViewById(R.id.editTextWeight);
        textViewBenchStats = findViewById(R.id.textViewBenchStats);
        float maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        float meanAcceleration = getIntent().getFloatExtra("meanAcceleration", 0);

        String results = "Bench Press Stat Results:\n" +
                "Maximum acceleration: " + maxAcceleration + "\n" +
                "Mean acceleration: " + meanAcceleration + "\n";

        textViewBenchStats.setText(results);

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(maxAcceleration, meanAcceleration);
            }
        });
        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void saveData(float maxAcceleration, float meanAcceleration) {
        try {
            int reps = Integer.parseInt(editTextReps.getText().toString().trim());
            float weight = Float.parseFloat(editTextWeight.getText().toString().trim());

            Instant instant = null;
            long timeStamp = -1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant = Instant.now();
                timeStamp = instant.getEpochSecond();
            }
            BenchExerciseData exercise = new BenchExerciseData(weight, maxAcceleration, reps, maxAcceleration, timeStamp);
            DatabaseHelper dh = DatabaseHelper.getInstance();
            dh.addExerciseData(exercise);

            closeActivity();
        } catch (NumberFormatException e) {
            Toast.makeText(BenchResultsActivity.this, "Please enter valid numbers for reps and weight.", Toast.LENGTH_LONG).show();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to leave without saving?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                closeActivity();
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

    private void closeActivity() {
        Intent intent = new Intent(BenchResultsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
