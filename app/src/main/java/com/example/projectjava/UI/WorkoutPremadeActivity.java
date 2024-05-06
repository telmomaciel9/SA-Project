package com.example.projectjava.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.UI.auth.LoginActivity;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPremadeActivity extends AppCompatActivity implements PremadeWorkoutsAdapter.OnItemClickListener{
    private RecyclerView recyclerViewPremadeWorkouts;
    private PremadeWorkoutsAdapter adapter;
    private Button btnSwitchFragment;
    private Button btnAddPremadeWorkout;
    private EditText editTextWorkoutName;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premade_workout);

        recyclerViewPremadeWorkouts = findViewById(R.id.recyclerViewPremadeWorkouts);
        btnSwitchFragment = findViewById(R.id.btnSwitchFragment);
        btnAddPremadeWorkout = findViewById(R.id.btnAddPremadeWorkout);
        btnAddPremadeWorkout.setVisibility(View.INVISIBLE);
        editTextWorkoutName = findViewById(R.id.editTextWorkoutName);

        bottomNavigationView = new BottomNavigationView(this);

        db = DatabaseHelper.getInstance();

        setUpRecyclerView();
        setUpFragments();
        setUpButtons();
    }

    public void setUpRecyclerView(){
        db.getAllPremadeWorkouts().addOnSuccessListener(premade_workouts -> {
            if(premade_workouts != null){
                recyclerViewPremadeWorkouts = findViewById(R.id.recyclerViewPremadeWorkouts);
                recyclerViewPremadeWorkouts.setLayoutManager(new LinearLayoutManager(this));

                adapter = new PremadeWorkoutsAdapter(premade_workouts, this);
                recyclerViewPremadeWorkouts.setAdapter(adapter);
            } else {
                Log.e("WorkoutPremadeActivity", "Problema ao recolher premade workouts ");
            }
        }).addOnFailureListener(e -> {
            Log.e("WorkoutPremadeActivity", "Failed getting premade workouts");
        });
    }

    // Set the default fragment to be the add premade exercise
    public void setUpFragments(){
        //Bundle bundle = new Bundle();
        //bundle.putString("exerciseId", exId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AddPremadeExerciseFragment apef = new AddPremadeExerciseFragment();
        // gf.setArguments(bundle);
        ft.replace(R.id.container, apef);
        ft.commit();
    }

    public void setUpButtons(){
        btnSwitchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnSwitchFragment.getText().toString().equals("List")){
                    Log.w("WorkoutPremadeActivity", "Switching to list fragment!");
                    PremadeExerciseListFragment pelf = new PremadeExerciseListFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, pelf);
                    ft.commit();

                    btnAddPremadeWorkout.setVisibility(View.VISIBLE);
                    btnSwitchFragment.setText("Exercise");
                }else if(btnSwitchFragment.getText().toString().equals("Exercise")){
                    Log.w("WorkoutPremadeActivity", "Switching to exercise fragment!");
                    AddPremadeExerciseFragment apef = new AddPremadeExerciseFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, apef);
                    ft.commit();

                    btnAddPremadeWorkout.setVisibility(View.INVISIBLE);
                    btnSwitchFragment.setText("List");
                }
            }
        });

        // Quando este botão é clickado sei que estamos no list fragment
        btnAddPremadeWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PremadeExerciseListFragment pelf = (PremadeExerciseListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                if(pelf != null && pelf.nrPremadeExercises() > 0){
                    String workoutName = editTextWorkoutName.getText().toString();
                    PremadeWorkout pw = new PremadeWorkout(workoutName);

                    List<PremadeExercise> pes = db.getPremadeExerciseList();
                    int count = 1;
                    for(PremadeExercise pe: pes){
                        pe.setOrder(count);
                        count++;
                    }
                    db.addPremadeWorkout(pw);

                    db.getAllPremadeWorkouts().addOnSuccessListener(premade_workouts -> {
                        if(premade_workouts != null){
                            adapter.updateData(premade_workouts);
                        } else {
                            Log.e("WorkoutPremadeActivity", "Problema ao recolher premade workouts ");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("WorkoutPremadeActivity", "Failed getting premade workouts");
                    });

                    editTextWorkoutName.setHint("Workout name.");
                    pelf.resetList();
                }else if(pelf != null && pelf.nrPremadeExercises() <= 0){
                    Toast.makeText(WorkoutPremadeActivity.this, "You need exercises to add a Workout.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Um premade workout é clickado
    @Override
    public void onItemClick(PremadeWorkout premadeWorkout) {
        Intent intent = new Intent(WorkoutPremadeActivity.this, WorkoutPremadeDetailsActivity.class);
        intent.putExtra("id", premadeWorkout.getId());
        startActivity(intent);
    }
}
