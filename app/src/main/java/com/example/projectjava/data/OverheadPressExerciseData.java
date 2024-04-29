package com.example.projectjava.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class OverheadPressExerciseData extends ExerciseData{
    private static final String table_name = "ohp";
    private float weight;
    private int repetitions;
    private float maxAcceleration;
    private float meanAcceleration;

    public OverheadPressExerciseData(float w, float ma, int r, float meana){
        super("Overhead Press");
        this.weight = w;
        this.maxAcceleration = ma;
        this.repetitions = r;
        this.meanAcceleration = meana;
    }

    public OverheadPressExerciseData(long id, float w, float ma, int r, float meana){
        super(id, "Overhead Press");
        this.weight = w;
        this.maxAcceleration = ma;
        this.repetitions = r;
        this.meanAcceleration = meana;
    }

    public ContentValues getContentValues(long workoutId) {
        ContentValues values = new ContentValues();
        values.put("weight", this.weight);
        values.put("reps", this.repetitions);
        values.put("mean_acceleration", this.meanAcceleration);
        values.put("max_acceleration", this.maxAcceleration);
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
                + "weight REAL, "
                + "reps INTEGER, "
                + "mean_acceleration REAL, "
                + "max_acceleration REAL, "
                + "FOREIGN KEY(workout_id) REFERENCES workouts(id)"
                + ");");
    }

    public String toString(){
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n    " +
                "Mean Acceleration: " + this.meanAcceleration + "m/s²;\n    " +
                "Max Acceleration: " + this.maxAcceleration + "m/s²";
    }
}
