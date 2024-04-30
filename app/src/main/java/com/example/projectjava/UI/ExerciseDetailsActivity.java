package com.example.projectjava.UI;

import static com.google.android.material.internal.ContextUtils.getActivity;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.projectjava.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {
    private GraphView graphView;
    private DatabaseHelper db;
    private TextView exerciseTitle;
    private TextView exerciseStats;
    private Spinner xSpinner;
    private String selectedX;
    private Spinner ySpinner;
    private String selectedY;
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        graphView = findViewById(R.id.idGraphView);
        exerciseTitle = findViewById(R.id.textViewExerciseTitle);
        exerciseStats = findViewById(R.id.textViewExerciseStats);
        xSpinner = findViewById(R.id.xSpinner);
        ySpinner = findViewById(R.id.ySpinner);
        selectedX = null;
        selectedY = null;
        series = new LineGraphSeries<>();

        db = DatabaseHelper.getInstance();
        String exerciseId = getIntent().getStringExtra("exerciseId");
        if(exerciseId != null){
            db.getExercise(exerciseId)
                    .addOnSuccessListener(exercise -> {
                        setUpSpinners(exercise);
                        String text = exercise.getExerciseName() + " progress";
                        exerciseTitle.setText(text);
                        text = exercise.toString();
                        exerciseStats.setText(text);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ExerciseDetails", "Error fetching exercise", e);
                    });
        }else{
            Log.e("ExerciseDetails", "Coudn't get exercise id!");
        }
    }

    public void setUpSpinners(ExerciseData exercise){
        List<String> items = exercise.getExerciseProgressMetrics();
        // Create an ArrayAdapter to populate the spinner with the items
        ArrayAdapter<String> adapterx = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        ArrayAdapter<String> adaptery = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapterx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adaptery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        xSpinner.setAdapter(adapterx);
        xSpinner.setSelection(0);
        ySpinner.setAdapter(adaptery);
        ySpinner.setSelection(1);

        xSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedX = (String) adapterView.getItemAtPosition(i);
                Log.e("Details", "selectedy: " + selectedY);
                if(selectedY != null && !selectedX.equals(selectedY)){
                    Log.e("Details", "testeX");
                    loadGraph(exercise);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing!
            }
        });

        ySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedY = (String) adapterView.getItemAtPosition(i);
                if(selectedX != null && !selectedX.equals(selectedY)){
                    Log.e("Details", "testeY");
                    loadGraph(exercise);
                }
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

                for(ExerciseData ex: exercises){
                    dataPoints.add(ex.getExerciseMetrics(selectedX, selectedY));
                }

                Collections.sort(dataPoints, new Comparator<DataPoint>() {
                    @Override
                    public int compare(DataPoint dp1, DataPoint dp2) {
                        return Float.compare((float) dp1.getX(), (float) dp2.getX());
                    }
                });

                for(DataPoint dp: dataPoints){
                    series.appendData(dp, true, 5);
                }

                graphView.addSeries(series);
                graphView.setTitle(selectedX + " vs. " + selectedY);
                //graphView.getViewport().setYAxisBoundsManual(true);
                //graphView.getViewport().setMinY(0);
                //graphView.getViewport().setMaxY(7);
            } else {
                Log.e("ExerciseDetails", "Problema ao exercícios por nome: " + exercise.getExerciseName());
            }
        }).addOnFailureListener(e -> {
            Log.e("ExerciseDetails", "Failed getting exercises by name");
            Toast.makeText(ExerciseDetailsActivity.this, "Can't load exercises",  Toast.LENGTH_SHORT).show();
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
