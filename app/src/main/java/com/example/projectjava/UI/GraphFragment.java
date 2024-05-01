package com.example.projectjava.UI;

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
    private Button btnTableGraph;
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

    public void loadGraph(ExerciseData exercise){
        List<DataPoint> dataPoints = new ArrayList<>();
        series.resetData(new DataPoint[]{});

        db.getExercisesByType(exercise).addOnSuccessListener(exercises -> {
            if(exercises != null){
                // Fazer alguma cena com os exercícios aqui!
                Collections.sort(exercises, new Comparator<ExerciseData>() {
                    @Override
                    public int compare(ExerciseData ex1, ExerciseData ex2) {
                        return Long.compare((long) ex1.getTimeStamp(), (long) ex2.getTimeStamp());
                    }
                });

                float count = 1;
                for(ExerciseData ex: exercises){
                    System.out.println("Exercise details: " + ex.getTimeStamp());
                    series.appendData(new DataPoint(count, ex.getExerciseMetrics(selectedY)), false, 5);
                    count++;
                }

                graphView.addSeries(series);
                graphView.setTitle(selectedY);
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinY(0);
                graphView.getViewport().setMaxY(exercises.get(exercises.size()-1).getExerciseMetrics(selectedY)+5);
            } else {
                Log.e("ExerciseDetails", "Problema ao exercícios por nome: " + exercise.getExerciseName());
            }
        }).addOnFailureListener(e -> {
            Log.e("ExerciseDetails", "Failed getting exercises by name");
            Toast.makeText(getContext(), "Can't load exercises",  Toast.LENGTH_SHORT).show();
        });
    }

    /*
    public void prepareData(List<Entry> entries, List<ExerciseData> exercises){
        // Variáveis para fazer o scaling
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;

        exercises = exercises.subList(0,3);

        for(ExerciseData ex : exercises){
            Entry e = ex.getExerciseProgressMetrics();
            entries.add(e);
            float x = e.getX();
            float y = e.getY();
            if(x > maxX){
                maxX = x;
            }
            if(x < minX){
                minX = x;
            }

            if(y > maxY){
                maxY = y;
            }
            if(y < minY){
                minY = y;
            }
        }

        // scale data
        for(Entry e : entries){
            float x = e.getX();
            float y = e.getY();
            e.setX(((x-minX)/(maxX-minX))*10);
            e.setY(((y-minY)/(maxY-minY))*10);
            Log.e("Exercise details entries", e.toString());
        }
    }
    */
}