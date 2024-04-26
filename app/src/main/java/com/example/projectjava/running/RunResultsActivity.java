package com.example.projectjava.running;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.database.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

public class RunResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView textViewRunStats;
    private ArrayList<LatLng> pathPoints;
    private float maxVelocity, averageVelocity, distanceRan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_results);

        textViewRunStats = findViewById(R.id.textViewRunStats);

        // Retrieve run data from Intent
        pathPoints = getIntent().getParcelableArrayListExtra("pathPoints");
        maxVelocity = getIntent().getFloatExtra("maxVelocity", 0);
        averageVelocity = getIntent().getFloatExtra("averageVelocity", 0);
        distanceRan = getIntent().getFloatExtra("distanceRan", 0);

        // the exercise has finished, so we add it to the workout session
        RunningExerciseData ed = new RunningExerciseData(distanceRan, averageVelocity, maxVelocity);
        DatabaseHelper dh = DatabaseHelper.getInstance(this);
        RunningExerciseData.createTable(dh);
        dh.addExerciseData(ed);

        // Set the stats text
        String stats = String.format(Locale.getDefault(), "Distance Ran: %.2f m\nAverage Velocity: %.2f m/s\nMax Velocity: %.2f m/s",
                distanceRan, averageVelocity, maxVelocity);
        textViewRunStats.setText(stats);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRunResults);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set the button
        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RunResultsActivity.this, RunActivity.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Draw the path of the run
        if (pathPoints != null && pathPoints.size() > 0) {
            PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            options.addAll(pathPoints);
            mMap.addPolyline(options);

            // Zoom the map to fit the entire path
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : pathPoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }
}
