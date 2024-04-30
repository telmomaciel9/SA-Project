package com.example.projectjava.data;

import android.content.ContentValues;

import com.github.mikephil.charting.data.Entry;
import com.jjoe64.graphview.series.DataPoint;

import java.util.List;
import java.util.Map;

public abstract class ExerciseData{
    private String exerciseName;
    private String id;

    public ExerciseData() {}
    public ExerciseData(String exerciseName) {
        this.id = null;
        this.exerciseName = exerciseName;
    }
    public ExerciseData(String id, String exerciseName) {
        this.id = id;
        this.exerciseName = exerciseName;
    }
    public abstract String getTableName();
    public String getExerciseName(){
        return this.exerciseName;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public abstract Map<String, Object> toMap();
    public abstract List<String> getExerciseProgressMetrics();
    public abstract DataPoint getExerciseMetrics(String xMetric, String yMetric);
}
