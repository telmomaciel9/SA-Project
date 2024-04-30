package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
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
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n    " +
                "Mean Acceleration: " + this.mean_acceleration + "m/s²;\n    " +
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
