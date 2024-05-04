package com.example.projectjava.UI;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableFragment extends Fragment {
    private TableLayout tl;
    private DatabaseHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        tl = view.findViewById(R.id.table_main);
        db = DatabaseHelper.getInstance();

        Bundle args = getArguments();
        String exerciseId = "";
        if(args != null){
            exerciseId = args.getString("exerciseId");

            db.getExercise(exerciseId)
                    .addOnSuccessListener(ex -> {
                        System.out.println("Exercise time_stamp: " + ex.getTimeStamp());
                        db.getExercisesByType(ex).addOnSuccessListener(exercises -> {
                            if(exercises != null){
                                buildTable(exercises);
                            } else {
                                Log.e("ExerciseDetails", "Problema ao exercícios por nome: " + ex.getExerciseName());
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("ExerciseDetails", "Failed getting exercises by name");
                            Toast.makeText(getContext(), "Can't load exercises",  Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ExerciseDetails", "Error fetching exercise", e);
                    });
        }else{
            System.out.println("Não passou o exerciseId");
        }

        return view;
    }

    public void buildTable(List<ExerciseData> exercises){
        List<String> metrics_abrv = exercises.get(0).getExerciseProgressMetricsAbrv();
        List<String> metrics = exercises.get(0).getExerciseProgressMetrics();
        List<TextView> maxMetrics_tvs = new ArrayList<>();
        List<Float> maxMetrics = new ArrayList<>();

        TableRow tr = new TableRow(getContext());
        for(String metric: metrics_abrv){
            String m = "   " + metric + "   ";
            TextView tv = new TextView(getContext());
            tv.setText(m);
            tv.setTextColor(Color.WHITE);
            tr.addView(tv);
            maxMetrics.add(Float.MIN_VALUE);
            maxMetrics_tvs.add(new TextView(getContext()));
        }
        tl.addView(tr);

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setMinimumFractionDigits(0);

        for(ExerciseData ex: exercises){
            tr = new TableRow(getContext());

            int i = 0;
            for(String metric: metrics){
                TextView tv = new TextView(getContext());
                float value = ex.getExerciseMetrics(metric);
                String value_string = decimalFormat.format(value);
                tv.setText(value_string);
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                tr.addView(tv);

                if(value > maxMetrics.get(i)){
                    maxMetrics.set(i, value);
                    maxMetrics_tvs.set(i, tv);
                }
                i++;
            }
            tl.addView(tr);
        }
        // (PR's in app's theme color)
        for(TextView tv: maxMetrics_tvs){
            tv.setTextColor(Color.parseColor("#BC50B2"));
        }
    }
}