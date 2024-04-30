package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;
import com.github.mikephil.charting.data.Entry;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunningExerciseData extends ExerciseData {
    public static final String table_name = "running";
    private float distance;
    private float avg_velocity;
    private float max_velocity;
    public RunningExerciseData() {
        super("Running");
    }
    public RunningExerciseData(float distance, float avg_velocity, float max_velocity) {
        super("Running");
        this.distance = distance;
        this.avg_velocity = avg_velocity;
        this.max_velocity = max_velocity;
    }

    public RunningExerciseData(String id, float distance, float avg_velocity, float max_velocity) {
        super(id, "Running");
        this.distance = distance;
        this.avg_velocity = avg_velocity;
        this.max_velocity = max_velocity;
    }

    public String getTableName() {
        return table_name;
    }

    public String toString(){
        return "Distance ran: " + this.distance + "m;\n" +
                "Average Velocity: " + this.avg_velocity + "m/s;\n" +
                "Maximum Velocity: " + this.max_velocity + "m/s";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("exercise_name", table_name);
        values.put("distance", this.distance);
        values.put("avg_velocity", this.avg_velocity);
        values.put("max_velocity", this.max_velocity);
        return values;
    }

    @Override
    public DataPoint getExerciseMetrics(String xMetric, String yMetric) {
        float x = 0;
        float y = 0;

        switch (xMetric){
            case ("Distance"):
                x = this.distance;
                break;
            case ("Average Velocity"):
                x = this.avg_velocity;
                break;
            case ("Maximum Velocitiy"):
                x = this.max_velocity;
                break;
        }

        switch (yMetric){
            case ("Distance"):
                y = this.distance;
                break;
            case ("Average Velocity"):
                y = this.avg_velocity;
                break;
            case ("Maximum Velocitiy"):
                y = this.max_velocity;
                break;
        }

        return new DataPoint(x,y);
    }

    public List<String> getExerciseProgressMetrics(){
        return Arrays.asList("Distance", "Average Velocity", "Maximum Velocitiy");
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAvg_velocity() {
        return avg_velocity;
    }

    public void setAvg_velocity(float avg_velocity) {
        this.avg_velocity = avg_velocity;
    }

    public float getMax_velocity() {
        return max_velocity;
    }

    public void setMax_velocity(float max_velocity) {
        this.max_velocity = max_velocity;
    }
}
