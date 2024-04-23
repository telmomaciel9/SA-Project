package com.example.projectjava.bench;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projectjava.R;
import com.example.projectjava.running.RunActivity;
import com.example.projectjava.running.RunResultsActivity;

import java.util.Locale;

public class BenchActivity extends Activity implements SensorEventListener {
    // Timer
    private TextView timerTextView;
    private Handler handler;
    private Runnable runnable;
    private long startTime;

    // Sensor de aceleração
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvMaxAcceleration;
    private boolean isRecording = false;
    private float maxAcceleration = 0;
    // Peso
    private EditText editTextWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench);

        initializeTimer();

        editTextWeight = findViewById(R.id.editTextWeight);

        tvMaxAcceleration = findViewById(R.id.tvMaxAcceleration);
        Button btnStart = findViewById(R.id.btnStartBench);
        Button btnStop = findViewById(R.id.btnStopBench);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;
                maxAcceleration = 0; // Reset the max acceleration
                tvMaxAcceleration.setText("Max Acceleration: 0");
                sensorManager.registerListener(BenchActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                startTimer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                sensorManager.unregisterListener(BenchActivity.this);
                tvMaxAcceleration.setText("Max Acceleration: " + maxAcceleration + " m/s²");
                Intent intent = new Intent(BenchActivity.this, BenchResultsActivity.class);
                intent.putExtra("maxAcceleration", maxAcceleration);
                intent.putExtra("weight", Float.parseFloat(editTextWeight.getText().toString()));
                startActivity(intent);
            }
        });
    }

    public void initializeTimer(){
        timerTextView = findViewById(R.id.textViewTimer);

        startTime = System.currentTimeMillis();
        handler = new Handler(Looper.getMainLooper());
    }

    public void startTimer(){
        runnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;

                String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);

                timerTextView.setText(timeFormatted);

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRecording) {
            // Calculate the acceleration magnitude
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (currentAcceleration > maxAcceleration) {
                maxAcceleration = currentAcceleration;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
