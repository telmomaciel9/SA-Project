package com.example.projectjava.UI;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPremadeDetailsActivity extends AppCompatActivity {
    private TextView tvPremadeWorkoutName;
    private RecyclerView rvPremadeExercises;
    private PremadeExercisesAdapter adapter;
    private DatabaseHelper db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premade_workout_details);

        tvPremadeWorkoutName = findViewById(R.id.tvPremadeWorkoutName);
        rvPremadeExercises = findViewById(R.id.rvPremadeExercises);

        db = DatabaseHelper.getInstance();

        String workoutId = getIntent().getStringExtra("id");
        assert workoutId != null;
        db.getPremadeWorkout(workoutId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PremadeWorkout pw = PremadeWorkout.deserealize(documentSnapshot);
                        if (pw != null) {
                            pw.setId(workoutId);
                            Log.i("WorkoutPremadeDetails", "Setting up UI");
                            setUpUI(pw);
                        } else {
                            Log.e("WorkoutPremadeDetails", "Couldn't convert premade workout document to object");
                        }
                    } else {
                        Log.e("WorkoutPremadeDetails", "ERRO AO IR BUSCAR PREMADE WORKOUT COM ID " + workoutId);
                    }
                });
    }

    public void setUpUI(PremadeWorkout pw){
        tvPremadeWorkoutName.setText(pw.getWorkout_name());
        rvPremadeExercises.setLayoutManager(new LinearLayoutManager(this));

        db.getPremadeWorkoutExercises(pw.getId()).addOnSuccessListener(exercises -> {
            if(exercises != null){
                adapter = new PremadeExercisesAdapter(exercises);
                rvPremadeExercises.setAdapter(adapter);
            } else {
                Log.e("WorkoutDetails", "Problema ao retirar exercícios do workout " + pw.getId());
            }
        }).addOnFailureListener(e -> {
            Log.e("WorkoutDetails", "Failed getting workout exercises");
        });
    }
}
