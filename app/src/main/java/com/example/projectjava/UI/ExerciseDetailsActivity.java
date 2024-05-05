package com.example.projectjava.UI;

import static com.google.android.material.internal.ContextUtils.getActivity;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectjava.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView exerciseTitle;
    private TextView exerciseStats;
    private Button btnTableGraph;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        exerciseTitle = findViewById(R.id.textViewExerciseTitle);
        exerciseStats = findViewById(R.id.textViewExerciseStats);
        btnTableGraph = findViewById(R.id.btnTableGraph);
        bottomNavigationView = new BottomNavigationView(this, false);

        db = DatabaseHelper.getInstance();
        String exerciseId = getIntent().getStringExtra("exerciseId");
        if(exerciseId != null){
            db.getExercise(exerciseId)
                    .addOnSuccessListener(exercise -> {
                        String text = exercise.getExerciseName() + " progress";
                        exerciseTitle.setText(text);
                        text = exercise.toString();
                        exerciseStats.setText(text);
                        setUpbutton(exerciseId);
                        setUpFragments(exerciseId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ExerciseDetails", "Error fetching exercise", e);
                    });
        }else{
            Log.e("ExerciseDetails", "Coudn't get exercise id!");
        }
    }

    public void setUpbutton(String exId){
        btnTableGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("exerciseId", exId);

                if(btnTableGraph.getText().equals("Table")){
                    TableFragment tf = new TableFragment();
                    tf.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, tf);
                    ft.commit();

                    btnTableGraph.setText("Graph");
                }else if(btnTableGraph.getText().equals("Graph")){
                    GraphFragment gf = new GraphFragment();
                    gf.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, gf);
                    ft.commit();

                    btnTableGraph.setText("Table");
                }
            }
        });
    }

    // Set the default fragment to be the graph
    public void setUpFragments(String exId){
        Bundle bundle = new Bundle();
        bundle.putString("exerciseId", exId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GraphFragment gf = new GraphFragment();
        gf.setArguments(bundle);
        ft.replace(R.id.container, gf);
        ft.commit();
    }
}
