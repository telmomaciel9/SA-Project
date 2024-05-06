package com.example.projectjava.UI.running;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.BottomNavigationView;
import com.example.projectjava.UI.EndWorkoutActivity;
import com.example.projectjava.UI.MainActivity;
import com.example.projectjava.UI.bench.BenchActivity;
import com.example.projectjava.UI.bench.BenchResultsActivity;
import com.example.projectjava.UI.overheadPress.OverheadPressActivity;
import com.example.projectjava.UI.overheadPress.OverheadPressResultsActivity;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.defaultExercises.RunningExerciseData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;

public class RunResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView textViewRunStats;
    private ArrayList<LatLng> pathPoints;
    private float maxVelocity, averageVelocity, distanceRan;
    private DatabaseHelper dh;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_results);

        dh = DatabaseHelper.getInstance();
        PremadeWorkout pw = dh.getActivePremadeWorkout();

        bottomNavigationView = new BottomNavigationView(this);

        initViews(pw);
    }

    private void initViews(PremadeWorkout pw) {
        textViewRunStats = findViewById(R.id.textViewRunStats);
        setupRun();
        findViewById(R.id.btnSave).setOnClickListener(v -> saveData(pw));
        findViewById(R.id.btnGoBack).setOnClickListener(v -> confirmExit(pw));
    }

    private void setupRun() {
        pathPoints = getIntent().getParcelableArrayListExtra("pathPoints");
        maxVelocity = getIntent().getFloatExtra("maxVelocity", 0);
        averageVelocity = getIntent().getFloatExtra("averageVelocity", 0);
        distanceRan = getIntent().getFloatExtra("distanceRan", 0);
        String stats = String.format(Locale.getDefault(), "Distance Ran: %.2f m\nAverage Velocity: %.2f m/s\nMax Velocity: %.2f m/s", distanceRan, averageVelocity, maxVelocity);
        textViewRunStats.setText(stats);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRunResults);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void saveData(PremadeWorkout pw) {
        Instant instant = null;
        long timeStamp = -1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.now();
            timeStamp = instant.getEpochSecond();
        }

        RunningExerciseData exercise = new RunningExerciseData(distanceRan, averageVelocity, maxVelocity, timeStamp);
        DatabaseHelper dh = DatabaseHelper.getInstance();
        dh.addExerciseData(exercise);
        closeActivity(pw);
    }

    private void confirmExit(PremadeWorkout pw) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to leave without saving?")
                .setPositiveButton("Yes", (dialog, id) -> closeActivity(pw))
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void closeActivity(PremadeWorkout pw) {
        if(pw == null){
            Intent intent = new Intent(RunResultsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Clears all activities on top of MainActivity
            startActivity(intent);
        }else{
            dh.getPremadeWorkoutExercises(pw.getId()).addOnSuccessListener(premade_exercises -> {
                if(premade_exercises != null){
                    PremadeExercise pe = dh.getNextPremadeExercise(premade_exercises, true);
                    if(pe != null){
                        switch (pe.getExerciseName()){
                            case "Bench Press":
                                startActivity(new Intent(RunResultsActivity.this, BenchActivity.class));
                                break;
                            case "Overhead Press":
                                startActivity(new Intent(RunResultsActivity.this, OverheadPressActivity.class));
                                break;
                            case "Running":
                                startActivity(new Intent(RunResultsActivity.this, RunActivity.class));
                                break;
                            default:
                                System.out.println("Invalid premade exercise_name!");
                                break;
                        }
                    }else{
                        // dh.endPremadeWorkout();
                        Intent intent = new Intent(RunResultsActivity.this, EndWorkoutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("premade_workout_ended", true);
                        startActivity(intent);
                    }
                } else {
                    Log.e("RunResultsActivity", "Problema ao retirar exercícios do workout " + pw.getId());
                }
            }).addOnFailureListener(e -> {
                Log.e("DatabaseHelper", "Failed getting premade workout exercises");
            });
        }
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
