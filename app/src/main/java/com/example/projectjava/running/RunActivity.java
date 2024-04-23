package com.example.projectjava.running;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler; // a
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// google maps
import com.example.projectjava.CardioActivity;
import com.example.projectjava.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Locale;

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Spinner spinnerRunDistance;
    private TextView textViewTimer, textViewDistance;
    private Button buttonStartRun, buttonFinishRun;
    private SupportMapFragment mapFragment;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private LocationManager locationManager;
    private LocationListener locationListener;
    private long startTime;
    private boolean running;
    private float totalDistance;
    private float maxVelocity;
    private GoogleMap map;
    private ArrayList<LatLng> points; // List of points on the map

    // Runnable to update the timer TextView
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        // initialize UI components
        spinnerRunDistance = findViewById(R.id.spinnerRunDistance);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewDistance = findViewById(R.id.textViewDistance);
        buttonStartRun = findViewById(R.id.buttonStartRun);
        buttonFinishRun = findViewById(R.id.buttonFinishRun);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initializeActivity();

        // Set the button
        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RunActivity.this, CardioActivity.class));
            }
        });
    }

    private void initializeActivity() {
        startTime = 0;
        running = false;
        totalDistance = 0;
        maxVelocity = 0;
        points = new ArrayList<>();

        // Reset UI components
        textViewTimer.setText("00:00:00");
        textViewDistance.setText("Distance: 0 m");

        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (running) {
                    long millis = System.currentTimeMillis() - startTime;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    int hours = minutes / 60;
                    minutes = minutes % 60;

                    textViewTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                    timerHandler.postDelayed(this, 500);
                }
            }
        };

        // Reset map if already available
        if (map != null) {
            map.clear();
            points.clear(); // Clear the list that accumulates LatLng points for the map
        } else {
            // Initialize map if null
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        buttonStartRun.setEnabled(true);
        buttonFinishRun.setEnabled(false);
        spinnerRunDistance.setEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.run_distances, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRunDistance.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            Location lastLocation = null;

            @Override
            public void onLocationChanged(Location location) {
                //super.onLocationChanged(location);
                updateMap(location); // Update the map with the new location
                if (lastLocation != null && location != null) {
                    float distance = location.distanceTo(lastLocation);
                    totalDistance += distance;
                    float speed = location.getSpeed();
                    if (speed > maxVelocity) {
                        maxVelocity = speed;
                    }
                    textViewDistance.setText(String.format("Distance: %.2f m", totalDistance));
                }
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Set onClickListeners if they need to be reset or initialized
        buttonStartRun.setOnClickListener(v -> startRun());
        buttonFinishRun.setOnClickListener(v -> finishRun());

        checkLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Disable user interaction like dragging
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
    }

    private void updateMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        points.add(latLng);

        if (map != null) {
            map.clear(); // Clear old markers or paths
            PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
            options.addAll(points);
            map.addPolyline(options);  // Add polyline to the map

            // Adjust this value to control the zoom level
            float zoomLevel = 18.0f; // A value from 2.0 (most zoomed out) to 21.0 (most zoomed in)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }

    private void startRun() {
        buttonStartRun.setEnabled(false);
        buttonFinishRun.setEnabled(true);
        spinnerRunDistance.setEnabled(false);
        running = true;
        startTime = System.currentTimeMillis();
        totalDistance = 0;
        maxVelocity = 0;
        textViewDistance.setText("Distance: 0 m");
        timerHandler.postDelayed(timerRunnable, 0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener, Looper.getMainLooper());
    }

    private void finishRun() {
        buttonStartRun.setEnabled(true);
        buttonFinishRun.setEnabled(false);
        spinnerRunDistance.setEnabled(true);
        running = false;
        timerHandler.removeCallbacks(timerRunnable);
        locationManager.removeUpdates(locationListener);

        // Calculate the stats
        long totalTime = System.currentTimeMillis() - startTime;
        float averageVelocity = totalDistance / (totalTime / 1000f); // totalTime is in milliseconds

        // Create an Intent to start RunResultsActivity
        Intent intent = new Intent(RunActivity.this, RunResultsActivity.class);
        intent.putParcelableArrayListExtra("pathPoints", points);
        intent.putExtra("maxVelocity", maxVelocity);
        intent.putExtra("averageVelocity", averageVelocity);
        intent.putExtra("distanceRan", String.format(Locale.getDefault(), "%.2f m", totalDistance));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!running) {
            initializeActivity();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    // Make sure to unregister location updates when the Activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        //if (running) {
        //    finishRun();
        //}
    }
}
