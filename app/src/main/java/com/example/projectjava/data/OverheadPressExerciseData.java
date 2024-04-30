package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.github.mikephil.charting.data.Entry;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverheadPressExerciseData extends ExerciseData{
    public static final String table_name = "ohp";
    private float weight;
    private int repetitions;
    private float max_acceleration;
    private float mean_acceleration;
    public OverheadPressExerciseData() {
        super("Overhead Press");
    }

    public OverheadPressExerciseData(float w, float ma, int r, float meana){
        super("Overhead Press");
        this.weight = w;
        this.max_acceleration = ma;
        this.repetitions = r;
        this.mean_acceleration = meana;
    }

    public OverheadPressExerciseData(String id, float w, float ma, int r, float meana){
        super(id, "Overhead Press");
        this.weight = w;
        this.max_acceleration = ma;
        this.repetitions = r;
        this.mean_acceleration = meana;
    }

    public String getTableName() {
        return table_name;
    }

    public String toString(){
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n" +
                "Mean Acceleration: " + this.mean_acceleration + "m/s²;\n" +
                "Max Acceleration: " + this.max_acceleration + "m/s²";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("exercise_name", table_name);
        values.put("weight", this.weight);
        values.put("repetitions", this.repetitions);
        values.put("mean_acceleration", this.mean_acceleration);
        values.put("max_acceleration", this.max_acceleration);
        return values;
    }

    @Override
    public DataPoint getExerciseMetrics(String xMetric, String yMetric) {
        float x = 0;
        float y = 0;

        switch (xMetric){
            case ("Weight"):
                x = this.weight;
                break;
            case ("Repetitions"):
                x = this.repetitions;
                break;
            case ("Mean Acceleration"):
                x = this.mean_acceleration;
                break;
            case ("Maximum Acceleration"):
                x = this.max_acceleration;
        }

        switch (yMetric){
            case ("Weight"):
                y = this.weight;
                break;
            case ("Repetitions"):
                y = this.repetitions;
                break;
            case ("Mean Acceleration"):
                y = this.mean_acceleration;
                break;
            case ("Maximum Acceleration"):
                y = this.max_acceleration;
        }

        return new DataPoint(x,y);
    }

    public List<String> getExerciseProgressMetrics(){
        return Arrays.asList("Weight", "Repetitions", "Mean Acceleration", "Maximum Acceleration");
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public float getmax_acceleration() {
        return max_acceleration;
    }

    public void setmax_acceleration(float max_acceleration) {
        this.max_acceleration = max_acceleration;
    }

    public float getmean_acceleration() {
        return mean_acceleration;
    }

    public void setmean_acceleration(float mean_acceleration) {
        this.mean_acceleration = mean_acceleration;
    }
}
