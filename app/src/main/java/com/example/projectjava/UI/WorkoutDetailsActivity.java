package com.example.projectjava.UI;

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

import java.util.List;

public class WorkoutDetailsActivity extends AppCompatActivity{
    private DatabaseHelper db;
    private TextView textViewWorkoutId;
    private TextView textViewWorkoutType;
    private TextView textViewWorkoutNotes;
    private RecyclerView exercisesRecyclerView;
    private ExercisesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);
        this.db = DatabaseHelper.getInstance(this);

        long workoutId = getIntent().getLongExtra("id", 0);
        Workout w = db.getWorkout(workoutId);
        if(w != null){
            textViewWorkoutId = findViewById(R.id.textViewWorkoutId);
            String id = "Id: " + w.getId();
            textViewWorkoutId.setText(id);
            textViewWorkoutType = findViewById(R.id.textViewWorkoutType);
            String type = "Type: " + w.getType();
            textViewWorkoutType.setText(type);
            textViewWorkoutNotes = findViewById(R.id.textViewWorkoutNotes);
            String notes = "Notes: " + w.getNotes();
            textViewWorkoutNotes.setText(notes);

            List<ExerciseData> exercises = db.getWorkoutExercises(w.getId());
            if(exercises != null){
                exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
                exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                adapter = new ExercisesAdapter(exercises);
                exercisesRecyclerView.setAdapter(adapter);
            }else{
                Log.e("WorkoutDetails", "Problema ao retirar exercísios do workout " + w.getId());
            }
        }else{
            Log.e("Base de dados", "ERRO OU IR BUSCAR WORKOUT COM ID " + workoutId);
        }
    }
}

