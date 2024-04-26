package com.example.projectjava.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static String workout_table_name = "workouts";
    private ArrayList<ExerciseData> exerciseDataList;  // Holds exercise data temporarily

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 2;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.exerciseDataList = null;

        // create workout table
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS workouts (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        // "duration REAL," +
                        "type TEXT," +
                        "notes TEXT" +
                        ");"
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade database if needed
    }

    public void beginWorkout() {
        this.exerciseDataList = new ArrayList<>();
    }

    public void finishWorkout(String type, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv_workout = new ContentValues();
        // cv_workout.put("duration", duration);
        cv_workout.put("type", type);
        cv_workout.put("notes", notes);
        long workoutId = db.insert(workout_table_name, null, cv_workout);

        // Save all exercises to the database
        for (ExerciseData exerciseData : exerciseDataList) {
            // Assuming exerciseData has all required fields to be saved
            this.addExercise(exerciseData, workoutId);
        }
        this.exerciseDataList = null;
    }

    private void addExercise(ExerciseData ed, long workoutId) {
        // add exercise to exercise database
        SQLiteDatabase db = this.getWritableDatabase();
        // get the values for each column of the exercise table
        ContentValues cv_exercise = ed.getContentValues(workoutId);
        // get the name of the table of the exercise
        String table_name = ed.getTableName();
        db.insert(table_name, null, cv_exercise);
    }

    public void addExerciseData(ExerciseData exerciseData) {
        this.exerciseDataList.add(exerciseData);
    }
}
