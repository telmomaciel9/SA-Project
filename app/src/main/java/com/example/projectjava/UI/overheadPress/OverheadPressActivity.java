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

import com.example.projectjava.UI.BottomNavigationView;
import com.example.projectjava.UI.Timer;

public class OverheadPressActivity extends AppCompatActivity implements SensorEventListener {
    private TextView timerTextView;
    private Timer timer;
    // Sensor de aceleração
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float maxAcceleration;
    private float meanAcceleration;
    private int nrAccelerations;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overhead_press);

        timer = new Timer();
        timerTextView = findViewById(R.id.textViewTimer);

        Button btnStart = findViewById(R.id.btnStartOHP);
        Button btnStop = findViewById(R.id.btnStopOHP);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        bottomNavigationView = new BottomNavigationView(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxAcceleration = 0; // Reset the max acceleration
                meanAcceleration = 0;
                nrAccelerations = 0;
                sensorManager.registerListener(OverheadPressActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                timer.startTimer(timerTextView);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meanAcceleration = meanAcceleration / nrAccelerations;
                timer.stopTimer();
                sensorManager.unregisterListener(OverheadPressActivity.this);
                Intent intent = new Intent(OverheadPressActivity.this, OverheadPressResultsActivity.class);
                intent.putExtra("maxAcceleration", maxAcceleration);
                intent.putExtra("meanAcceleration", meanAcceleration);
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
            float currentAcceleration = Math.abs((float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH);

            if (currentAcceleration > maxAcceleration) {
                maxAcceleration = currentAcceleration;
            }
            meanAcceleration += currentAcceleration;
            nrAccelerations++;
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
