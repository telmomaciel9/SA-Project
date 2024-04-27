package com.example.projectjava.UI.bench;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.MainActivity;
import com.example.projectjava.data.BenchExerciseData;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.RunningExerciseData;

public class BenchResultsActivity extends AppCompatActivity {
    private TextView textViewBenchStats;
    private EditText editTextReps;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench_results);

        DatabaseHelper dh = DatabaseHelper.getInstance(this);

        editTextReps = findViewById(R.id.editTextReps);
        textViewBenchStats = findViewById(R.id.textViewBenchStats);
        float maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        float weight = getIntent().getFloatExtra("weight", 0);

        String results = "Bench Press Stat Results:\n" +
                "1. Weight: " + String.valueOf(weight) + "\n" +
                "2. Maximum acceleration: " + String.valueOf(maxAcceleration) + "\n";

        textViewBenchStats.setText(results);

        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reps = Integer.parseInt(editTextReps.getText().toString());
                BenchExerciseData exercise = new BenchExerciseData(weight, maxAcceleration, reps);
                BenchExerciseData.createTable(dh);
                dh.addExerciseData(exercise);
                startActivity(new Intent(BenchResultsActivity.this, MainActivity.class));
            }
        });
    }
}
