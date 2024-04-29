package com.example.projectjava.UI;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.github.mikephil.charting.charts.LineChart;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailsActivity extends AppCompatActivity {
    private LineChart lineChart;
    private ExerciseData exercise;
    private DatabaseHelper db;
    private TextView exerciseTitle;
    private TextView exerciseStats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        lineChart = findViewById(R.id.lineChart);
        exerciseTitle = findViewById(R.id.textViewExerciseTitle);
        exerciseStats = findViewById(R.id.textViewExerciseStats);

        db = DatabaseHelper.getInstance(this);
        long exerciseId = getIntent().getLongExtra("exerciseId", -1);
        if(exerciseId != -1){
            this.exercise = db.getExercise(exerciseId);
            String text = this.exercise.getExerciseName() + " progress";
            exerciseTitle.setText(text);
            text = this.exercise.toString();
            exerciseStats.setText(text);
            generateLineChart(); // Apenas para testar agora (mudar isto depois)
        }else{
            Log.e("Exercise details", "Coudn't get exercise id!");
        }
    }

    public void generateLineChart(){
        // Customize chart appearance
        Description description = new Description();
        description.setText(this.exercise.getExerciseName() + " progress.");
        lineChart.setDescription(description);

        // Add data to the chart
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 6));
        entries.add(new Entry(3, 12));

        LineDataSet dataSet = new LineDataSet(entries, "My Data");
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Refresh chart
        lineChart.invalidate();
    }
}
