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

public class JumpActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private TextView tvMaxAltitude;
    private boolean isRecording = false;
    private float initialPressure = 0;
    private float maxAltitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);

        tvMaxAltitude = findViewById(R.id.tvMaxAltitude);
        Button btnStart = findViewById(R.id.btnStartJump);
        Button btnStop = findViewById(R.id.btnStopJump);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = true;
                maxAltitude = 0; // Reset the max altitude
                initialPressure = 0; // Reset initial pressure
                tvMaxAltitude.setText("Max Altitude: 0 m");
                sensorManager.registerListener(JumpActivity.this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                sensorManager.unregisterListener(JumpActivity.this);
                tvMaxAltitude.setText("Max Altitude: " + maxAltitude + " m");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRecording) {
            if (initialPressure == 0) {
                initialPressure = event.values[0];
            }
            float currentPressure = event.values[0];
            float currentAltitude = SensorManager.getAltitude(initialPressure, currentPressure);

            if (currentAltitude > maxAltitude) {
                maxAltitude = currentAltitude;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused to conserve battery.
        sensorManager.unregisterListener(this);
    }
}
