package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.ExerciseData;

public class RunningExerciseData extends ExerciseData {
    public static final String table_name = "running";
    private final float distanceRan;
    private final float averageVelocity;
    private final float maxVelocity;
    public RunningExerciseData(float distanceRan, float averageVelocity, float maxVelocity) {
        super("Running");
        this.distanceRan = distanceRan;
        this.averageVelocity = averageVelocity;
        this.maxVelocity = maxVelocity;
    }
    public RunningExerciseData(long id, float distanceRan, float averageVelocity, float maxVelocity) {
        super(id, "Running");
        this.distanceRan = distanceRan;
        this.averageVelocity = averageVelocity;
        this.maxVelocity = maxVelocity;
    }

    public ContentValues getContentValues(long workoutId) {
        ContentValues values = new ContentValues();
        values.put("distance", this.distanceRan);
        values.put("avg_velocity", this.averageVelocity);
        values.put("max_velocity", this.maxVelocity);
        values.put("workout_id", workoutId);
        return values;
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
        return "Distance ran: " + this.distanceRan + "m;\n    " +
                "Average Velocity: " + this.averageVelocity + "m/s;\n    " +
                "Maximum Velocity: " + this.maxVelocity + "m/s";
    }
}
