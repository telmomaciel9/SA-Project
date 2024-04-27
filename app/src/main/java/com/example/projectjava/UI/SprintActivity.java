package com.example.projectjava.UI;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projectjava.R;

public class SprintActivity extends Activity {
    private LocationManager locationManager;
    private TextView tvMaxVelocity;
    private float maxVelocity = 0;

    // Define a listener that responds to location updates
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                float speed = location.getSpeed(); // speed in meters/second
                tvMaxVelocity.setText("Max Velocity: " + speed + " m/s");
                if (speed > maxVelocity) {
                    maxVelocity = speed;
                    tvMaxVelocity.setText("Max Velocity: " + maxVelocity + " m/s");
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        tvMaxVelocity = findViewById(R.id.tvMaxVelocity);
        Button btnStart = findViewById(R.id.btnStartSprint);
        Button btnStop = findViewById(R.id.btnStopSprint);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for GPS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxVelocity = 0;
                tvMaxVelocity.setText("Max Velocity: 0 m/s");
                startLocationUpdates();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
            }
        });
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, // minimum time interval between updates in milliseconds
                    0, // minimum distance between updates in meters
                    locationListener, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
        tvMaxVelocity.setText("Max Velocity: " + maxVelocity + " m/s");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates(); // Stop location updates when Activity is no longer active
    }
}
