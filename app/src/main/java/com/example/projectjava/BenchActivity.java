package com.example.projectjava;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BenchActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvMaxAcceleration;
    private boolean isRecording = false;
    private float maxAcceleration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bench);

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
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                sensorManager.unregisterListener(BenchActivity.this);
                tvMaxAcceleration.setText("Max Acceleration: " + maxAcceleration + " m/s²");
            }
        });
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
