package com.example.projectjava.UI.overheadPress;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;

import com.example.projectjava.UI.Timer;

public class OverheadPressActivity extends AppCompatActivity implements SensorEventListener {
    private TextView timerTextView;
    private Timer timer;
    // Sensor de aceleração
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView textViewMaxAcceleration;
    private boolean isRecording = false;
    private float maxAcceleration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overhead_press);

        timer = new Timer();
        timerTextView = findViewById(R.id.textViewTimer);

        textViewMaxAcceleration = findViewById(R.id.textViewMaxAcceleration);
        Button btnStart = findViewById(R.id.btnStartOHP);
        Button btnStop = findViewById(R.id.btnStopOHP);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxAcceleration = 0; // Reset the max acceleration
                textViewMaxAcceleration.setText("Max Acceleration: 0");
                sensorManager.registerListener(OverheadPressActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                timer.startTimer(timerTextView);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.stopTimer();
                sensorManager.unregisterListener(OverheadPressActivity.this);
                textViewMaxAcceleration.setText("Max Acceleration: " + maxAcceleration + " m/s²");
                Intent intent = new Intent(OverheadPressActivity.this, OverheadPressResultsActivity.class);
                intent.putExtra("maxAcceleration", maxAcceleration);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (timer.isRecording()) {
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
        // Sensor accuracy changes.
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
