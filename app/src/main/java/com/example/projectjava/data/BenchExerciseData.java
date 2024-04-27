package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class BenchExerciseData extends ExerciseData{
    private static final String table_name = "bench";
    private float weight;
    private float maxAcceleration;
    private int repetitions;

    public BenchExerciseData(float w, float ma, int r){
        super("Bench Press");
        this.weight = w;
        this.maxAcceleration = ma;
        this.repetitions = r;
    }

    public ContentValues getContentValues(long workoutId) {
        ContentValues values = new ContentValues();
        values.put("weight", this.weight);
        values.put("reps", this.repetitions);
        values.put("max_acceleration", this.maxAcceleration);
        values.put("workout_id", workoutId);
        return values;
    }

    public String getTableName() {
        return this.table_name;
    }

    public static void createTable(DatabaseHelper dh) {
        SQLiteDatabase db = dh.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "workout_id INTEGER, "
                + "weight REAL, "
                + "reps INTEGER, "
                + "max_acceleration REAL,"
                + "FOREIGN KEY(workout_id) REFERENCES workouts(id)"
                + ");");
    }
}
