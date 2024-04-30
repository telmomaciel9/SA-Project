package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class BenchExerciseData extends ExerciseData{
    public static final String table_name = "bench";
    private float weight;
    private int repetitions;
    private float mean_acceleration;
    private float max_acceleration;
    public BenchExerciseData() {
        super("Bench Press");
    }

    public BenchExerciseData(float w, float maxa, int r, float meana){
        super("Bench Press");
        this.weight = w;
        this.max_acceleration = maxa;
        this.mean_acceleration = meana;
        this.repetitions = r;
    }

    public BenchExerciseData(String id, float w, float maxa, int r, float meana){
        super(id, "Bench Press");
        this.weight = w;
        this.max_acceleration = maxa;
        this.mean_acceleration = meana;
        this.repetitions = r;
    }

    public String getTableName() {
        return table_name;
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

    public static void createTable(DatabaseHelper dh) {
        SQLiteDatabase db = dh.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "workout_id INTEGER, "
                + "weight REAL, "
                + "reps INTEGER, "
                + "mean_acceleration REAL, "
                + "max_acceleration REAL, "
                + "FOREIGN KEY(workout_id) REFERENCES workouts(id)"
                + ");");
    }

    @NonNull
    public String toString(){
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n    " +
                "Mean Acceleration: " + this.mean_acceleration + "m/s²;\n    " +
                "Max Acceleration: " + this.max_acceleration + "m/s²";
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

    public float getmean_acceleration() {
        return mean_acceleration;
    }

    public void setmean_acceleration(float mean_acceleration) {
        this.mean_acceleration = mean_acceleration;
    }

    public float getmax_acceleration() {
        return max_acceleration;
    }

    public void setmax_acceleration(float max_acceleration) {
        this.max_acceleration = max_acceleration;
    }
}
