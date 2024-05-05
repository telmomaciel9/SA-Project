package com.example.projectjava.UI.bench;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projectjava.R;
import com.example.projectjava.UI.BottomNavigationView;
import com.example.projectjava.UI.Timer;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeWorkout;

public class BenchActivity extends Activity implements SensorEventListener {
    // Timer
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
        setContentView(R.layout.activity_bench);

        timer = new Timer();
        timerTextView = findViewById(R.id.textViewTimer);

        Button btnStart = findViewById(R.id.btnStartBench);
        Button btnStop = findViewById(R.id.btnStopBench);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        bottomNavigationView = new BottomNavigationView(this, true);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxAcceleration = 0;  // Reset the max acceleration
                meanAcceleration = 0; // Reset mean acceleration
                nrAccelerations = 0;
                sensorManager.registerListener(BenchActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                timer.startTimer(timerTextView);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meanAcceleration = meanAcceleration/nrAccelerations;
                timer.stopTimer();
                sensorManager.unregisterListener(BenchActivity.this);
                Intent intent = new Intent(BenchActivity.this, BenchResultsActivity.class);
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
