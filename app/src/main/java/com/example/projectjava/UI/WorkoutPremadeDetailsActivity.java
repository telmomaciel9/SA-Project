package com.example.projectjava.UI;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.UI.bench.BenchActivity;
import com.example.projectjava.UI.overheadPress.OverheadPressActivity;
import com.example.projectjava.UI.running.RunActivity;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.premadeExercises.PremadeBenchExercise;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPremadeDetailsActivity extends AppCompatActivity {
    private List<PremadeExercise> premade_exercises;
    private TextView tvPremadeWorkoutName;
    private RecyclerView rvPremadeExercises;
    private PremadeExercisesAdapter adapter;
    private Button btnBeginPremadeWorkout;
    private Button btnSharePremadeWorkout;
    private DatabaseHelper db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premade_workout_details);

        tvPremadeWorkoutName = findViewById(R.id.tvPremadeWorkoutName);
        rvPremadeExercises = findViewById(R.id.rvPremadeExercises);
        btnBeginPremadeWorkout = findViewById(R.id.btnBeginPremadeWorkout);
        btnSharePremadeWorkout = findViewById(R.id.btnSharePremadeWorkout);

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
                premade_exercises = exercises;
                adapter = new PremadeExercisesAdapter(exercises);
                rvPremadeExercises.setAdapter(adapter);
            } else {
                Log.e("WorkoutDetails", "Problema ao retirar exercícios do workout " + pw.getId());
            }
        }).addOnFailureListener(e -> {
            Log.e("WorkoutDetails", "Failed getting workout exercises");
        });

        btnBeginPremadeWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setActivePremadeWorkout(pw);
                PremadeExercise fpe = adapter.getPremadeExercises().get(0);
                System.out.println("Primeiro exercício: " + fpe.getExerciseName());

                Notification n = new Notification(getApplicationContext(), WorkoutPremadeDetailsActivity.this);
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                n.makeNotification("Your premade workout has started.", "Give it your best!", EndWorkoutActivity.class,1, nm);

                switch (fpe.getExerciseName()){
                    case("Bench Press"):
                        startActivity(new Intent(WorkoutPremadeDetailsActivity.this, BenchActivity.class));
                        break;
                    case("Overhead Press"):
                        startActivity(new Intent(WorkoutPremadeDetailsActivity.this, OverheadPressActivity.class));
                        break;
                    case("Running"):
                        startActivity(new Intent(WorkoutPremadeDetailsActivity.this, RunActivity.class));
                        break;
                    default:
                        System.out.println("Invalid premade exercise name!");
                        break;
                }
            }
        });

        btnSharePremadeWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                // change the type of data you need to share,
                // for image use "image/*"
                intent.setType("text/plain");

                int count = 1;
                String text_to_share = pw.getWorkout_name() + "\n";
                for(PremadeExercise pe: premade_exercises){
                    text_to_share += "\t" + count + ". " + pe.share() + "\n";
                    count++;
                }
                text_to_share += "\n\nBuilt with the FitTracker app!";


                intent.putExtra(Intent.EXTRA_TEXT, text_to_share);
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }
}
