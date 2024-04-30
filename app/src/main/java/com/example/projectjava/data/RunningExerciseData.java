package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;

import java.util.HashMap;
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

    public static void createTable(DatabaseHelper dh) {
        SQLiteDatabase db = dh.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "workout_id INTEGER, "
                + "distance REAL, "
                + "avg_velocity REAL, "
                + "max_velocity REAL,"
                + "FOREIGN KEY(workout_id) REFERENCES workouts(id)"
                + ");");
    }

    public String toString(){
        return "Distance ran: " + this.distance + "m;\n    " +
                "Average Velocity: " + this.avg_velocity + "m/s;\n    " +
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
