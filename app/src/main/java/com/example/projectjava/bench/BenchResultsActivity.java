package com.example.projectjava.bench;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;

public class BenchResultsActivity extends AppCompatActivity {
    private TextView textViewBenchStats;
    private EditText editTextReps;
    private float maxAcceleration;
    private float weight;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench_results);

        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BenchResultsActivity.this, BenchActivity.class));
            }
        });

        textViewBenchStats = findViewById(R.id.textViewBenchStats);
        maxAcceleration = getIntent().getFloatExtra("maxAcceleration", 0);
        weight = getIntent().getFloatExtra("weight", 0);

        textViewBenchStats.setText("Bench Press Stat Results:\n" +
                "1. Weight: " + String.valueOf(weight) + "\n" +
                "2. Maximum acceleration: " + String.valueOf(maxAcceleration) + "\n");
    }
}
