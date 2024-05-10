package com.example.projectjava.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GraphFragment extends Fragment {
    private GraphView graphView;
    private Spinner ySpinner;
    private String selectedY;
    private LineGraphSeries<DataPoint> series;
    private DatabaseHelper db;
    private ExerciseData exercise;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graphView = view.findViewById(R.id.idGraphView);
        ySpinner = view.findViewById(R.id.ySpinner);
        selectedY = null;
        series = new LineGraphSeries<>();
        series.setColor(Color.parseColor("#BC50B2"));

        Bundle args = getArguments();
        String exerciseId = "";
        if(args != null){
            exerciseId = args.getString("exerciseId");
        }

        db = DatabaseHelper.getInstance();
        db.getExercise(exerciseId)
                .addOnSuccessListener(ex -> {
                    this.exercise = ex;
                    setUpSpinner(exercise);
                })
                .addOnFailureListener(e -> {
                    Log.e("ExerciseDetails", "Error fetching exercise", e);
                });
        return view;
    }

    public void setUpSpinner(ExerciseData exercise){
        List<String> items = exercise.getExerciseProgressMetrics();
        // Create an ArrayAdapter to populate the spinner with the items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ySpinner.setAdapter(adapter);

        ySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedY = (String) adapterView.getItemAtPosition(i);
                Log.e("Details", "testeY");
                loadGraph(exercise);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing!
            }
        });
    }

    public void loadGraph(ExerciseData exercise) {
        series.resetData(new DataPoint[]{});

        db.getExercisesByType(exercise).addOnSuccessListener(exercises -> {
            if (exercises != null) {
                int count = 1;
                int nrDataPoints = exercises.size();

                for (ExerciseData ex : exercises) {
                    System.out.println("Graph fragment: " + ex.getExerciseMetrics("Weight"));
                    series.appendData(new DataPoint(count, ex.getExerciseMetrics(selectedY)), false, nrDataPoints);
                    count++;
                }

                graphView.addSeries(series);
                graphView.setTitle(selectedY + " progress along time.");
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(1);
                graphView.getViewport().setMaxX(nrDataPoints);
            } else {
                Log.e("ExerciseDetails", "Problema ao exercícios por nome: " + exercise.getExerciseName());
            }
        }).addOnFailureListener(e -> {
            Log.e("ExerciseDetails", "Failed getting exercises by name");
            Toast.makeText(getContext(), "Can't load exercises", Toast.LENGTH_SHORT).show();
        });
    }
}