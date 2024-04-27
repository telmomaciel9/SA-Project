package com.example.projectjava.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.Workout;

import java.util.ArrayList;

public class WorkoutHistoryActivity extends AppCompatActivity implements WorkoutsAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WorkoutsAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = DatabaseHelper.getInstance(this);
        ArrayList<Workout> workouts = databaseHelper.getAllWorkouts();

        adapter = new WorkoutsAdapter(workouts, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWorkoutList();
    }

    private void refreshWorkoutList() {
        ArrayList<Workout> workouts = databaseHelper.getAllWorkouts();  // Fetch all workouts from the database
        adapter.updateData(workouts);  // Update the adapter's data
    }

    // Um workout é clickado
    @Override
    public void onItemClick(Workout workout) {
        Intent intent = new Intent(WorkoutHistoryActivity.this, WorkoutDetailsActivity.class);
        intent.putExtra("id", workout.getId());
        startActivity(intent);
    }
}
