package com.example.projectjava.UI.overheadPress;

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
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.OverheadPressExerciseData;

public class OverheadPressResultsActivity extends AppCompatActivity {
    private TextView textViewOHPStats;
    private EditText editTextReps;
    private EditText editTextWeight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overhead_press_results);

        DatabaseHelper dh = DatabaseHelper.getInstance(this);

        editTextReps = findViewById(R.id.editTextReps);
        editTextWeight = findViewById(R.id.editTextWeight);
        textViewOHPStats = findViewById(R.id.textViewOHPStats);
        float maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        float meanAcceleration = getIntent().getFloatExtra("meanAcceleration", 0);

        String results = "Overhead Press Stat Results:\n" +
                "Maximum acceleration: " + maxAcceleration + "\n" +
                "Mean acceleration: " + meanAcceleration + "\n";

        textViewOHPStats.setText(results);

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

            OverheadPressExerciseData exercise = new OverheadPressExerciseData(weight, maxAcceleration, reps, meanAcceleration);
            DatabaseHelper dh = DatabaseHelper.getInstance(this);
            OverheadPressExerciseData.createTable(dh);
            dh.addExerciseData(exercise);

            closeActivity();
        } catch (NumberFormatException e) {
            Toast.makeText(OverheadPressResultsActivity.this, "Please enter valid numbers for reps and weight.", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(OverheadPressResultsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
