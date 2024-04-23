package com.example.projectjava;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "athleteTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_EXERCISES = "exercises";

    // Workout Table Columns
    private static final String KEY_WORKOUT_ID = "id";
    private static final String KEY_WORKOUT_DATE = "date";
    private static final String KEY_WORKOUT_TYPE = "type";
    private static final String KEY_WORKOUT_NOTES = "notes";

    // Exercise Table Columns
    private static final String KEY_EXERCISE_ID = "id";
    private static final String KEY_EXERCISE_WORKOUT_ID = "workoutId";
    private static final String KEY_EXERCISE_NAME = "name";
    private static final String KEY_EXERCISE_SETS = "sets";
    private static final String KEY_EXERCISE_REPS = "reps";
    private static final String KEY_EXERCISE_WEIGHT = "weight";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORKOUTS_TABLE = "CREATE TABLE " + TABLE_WORKOUTS +
                "(" +
                KEY_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_WORKOUT_DATE + " TEXT," +
                KEY_WORKOUT_TYPE + " TEXT," +
                KEY_WORKOUT_NOTES + " TEXT" +
                ")";

        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES +
                "(" +
                KEY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_EXERCISE_WORKOUT_ID + " INTEGER REFERENCES " + TABLE_WORKOUTS + "," + // Foreign Key
                KEY_EXERCISE_NAME + " TEXT," +
                KEY_EXERCISE_SETS + " INTEGER," +
                KEY_EXERCISE_REPS + " INTEGER," +
                KEY_EXERCISE_WEIGHT + " REAL" + // Use REAL for floating point values
                ")";

        db.execSQL(CREATE_WORKOUTS_TABLE);
        db.execSQL(CREATE_EXERCISES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for workout data, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        onCreate(db);
    }

    public long addWorkout(String date, String type, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_DATE, date);
        values.put(KEY_WORKOUT_TYPE, type);
        values.put(KEY_WORKOUT_NOTES, notes);

        // Inserting Row
        long workoutId = db.insert(TABLE_WORKOUTS, null, values);
        db.close(); // Closing database connection
        return workoutId;
    }

    // Method to add exercise linked to a workout
    public long addExercise(long workoutId, String name, int sets, int reps, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_WORKOUT_ID, workoutId);
        values.put(KEY_EXERCISE_NAME, name);
        values.put(KEY_EXERCISE_SETS, sets);
        values.put(KEY_EXERCISE_REPS, reps);
        values.put(KEY_EXERCISE_WEIGHT, weight);

        // Inserting Row
        long exerciseId = db.insert(TABLE_EXERCISES, null, values);
        db.close(); // Closing database connection
        return exerciseId;
    }

    // Additional CRUD operations go here (querying workouts, updating, and deleting)
}
