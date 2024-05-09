package com.example.projectjava.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.example.projectjava.data.Workout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity implements WorkoutsAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WorkoutsAdapter adapter;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = new BottomNavigationView(this);

        databaseHelper = DatabaseHelper.getInstance();
        databaseHelper.getAllWorkouts(new DatabaseHelper.FirebaseFirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Workout> workouts) {
                orderWorkoutsChronologically(workouts);
                adapter = new WorkoutsAdapter(workouts, WorkoutHistoryActivity.this);
                System.out.println(adapter);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWorkoutList();
    }

    private void refreshWorkoutList() {
        databaseHelper.getAllWorkouts(new DatabaseHelper.FirebaseFirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Workout> workouts) {
                orderWorkoutsChronologically(workouts);
                adapter.updateData(workouts);  // Update the adapter's data
            }
        });
    }

    // Um workout é clickado
    @Override
    public void onItemClick(Workout workout) {
        Intent intent = new Intent(WorkoutHistoryActivity.this, WorkoutDetailsActivity.class);
        intent.putExtra("id", workout.getId());
        startActivity(intent);
    }

    public void orderWorkoutsChronologically(List<Workout> workouts){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Order the workouts chronologically
        Collections.sort(workouts, new Comparator<Workout>() {
            @Override
            public int compare(Workout w1, Workout w2) {
                try {
                    Date date1 = dateFormat.parse(w1.getBegin_date());
                    Date date2 = dateFormat.parse(w2.getBegin_date());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
