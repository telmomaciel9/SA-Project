package com.example.projectjava.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.example.projectjava.data.Workout;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailsActivity extends AppCompatActivity implements ExercisesAdapter.OnItemClickListener{
    private DatabaseHelper db;
    private TextView textViewWorkoutId;
    private TextView textViewWorkoutType;
    private TextView textViewWorkoutNotes;
    private RecyclerView exercisesRecyclerView;
    private ExercisesAdapter adapter;
    private String workoutId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);
        this.db = DatabaseHelper.getInstance();
        textViewWorkoutId = findViewById(R.id.textViewWorkoutId);
        textViewWorkoutType = findViewById(R.id.textViewWorkoutType);
        textViewWorkoutNotes = findViewById(R.id.textViewWorkoutNotes);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);

        String workoutId = getIntent().getStringExtra("id");
        assert workoutId != null;
        db.getWorkout(workoutId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Workout w = documentSnapshot.toObject(Workout.class);
                        if (w != null) {
                            w.setId(workoutId);
                            Log.i("WorkoutDetails", "Setting up UI");
                            setupUI(w);
                        } else {
                            Log.e("WorkoutDetails", "Couldn't convert workout document to object");
                        }
                    } else {
                        Log.e("WorkoutDetails", "ERRO AO IR BUSCAR WORKOUT COM ID " + workoutId);
                    }
                });
    }

    private void setupUI(Workout workout) {
        this.workoutId = workout.getId();
        String id = "Id: " + this.workoutId;
        String type = "Type: " + workout.getType();
        String notes = "Notes: " + workout.getNotes();
        textViewWorkoutId.setText(id);
        textViewWorkoutType.setText(type);
        textViewWorkoutNotes.setText(notes);

        db.getWorkoutExercises(this.workoutId).addOnSuccessListener(exercises -> {
            System.out.println("exercises: " + exercises);
            if(exercises != null){
                exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ExercisesAdapter(exercises, this);
                exercisesRecyclerView.setAdapter(adapter);
            } else {
                Log.e("WorkoutDetails", "Problema ao retirar exercícios do workout " + this.workoutId);
            }
        })
                .addOnFailureListener(e -> {
                    Log.e("WorkoutDetails", "Failed getting workout exercises");

                });
    }

    @Override
    public void onItemClick(ExerciseData exercise) {
        Intent intent = new Intent(WorkoutDetailsActivity.this, ExerciseDetailsActivity.class);
        intent.putExtra("exerciseId", exercise.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh if workoutId is set
        if (this.workoutId != null) {
            refreshWorkoutList();
        }
    }

    private void refreshWorkoutList() {
        if (this.workoutId == null) {
            Log.e("WorkoutDetails", "Workout ID is not set yet.");
            return;
        }
        db.getWorkoutExercises(this.workoutId).addOnSuccessListener(exercises -> {
            if (exercises != null) {
                adapter.updateData(exercises);  // Update the adapter's data
                //adapter.notifyDataSetChanged();
            } else {
                Log.e("WorkoutDetails", "No exercises found for workout ID " + this.workoutId);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

