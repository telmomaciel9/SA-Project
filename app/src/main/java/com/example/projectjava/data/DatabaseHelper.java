package com.example.projectjava.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private static String workout_table_name = "workouts";
    private static List<String> exercises_tables = new ArrayList<>(Arrays.asList("bench", "running", "ohp"));
    private ArrayList<ExerciseData> exerciseDataList;  // Holds exercise data temporarily
    private FirebaseFirestore dbFirebase;

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
        this.dbFirebase = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create workout table
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
        System.out.println(workoutId);
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

    public ArrayList<Workout> getAllWorkouts() {
        ArrayList<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + workout_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getInt(cursor.getColumnIndex("id"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String notes = cursor.getString(cursor.getColumnIndex("notes"));
                workouts.add(new Workout(id, type, notes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workouts;
    }

    public Workout getWorkout(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + workout_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                long wid = cursor.getInt(cursor.getColumnIndex("id"));
                if(id == wid){
                    return new Workout(wid, cursor.getString(cursor.getColumnIndex("type")), cursor.getString(cursor.getColumnIndex("notes")));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return null;
    }

    public List<ExerciseData> getWorkoutExercises(long workoutId){
        SQLiteDatabase db = this.getReadableDatabase();
        List<ExerciseData> exercises = new ArrayList<>();
        for(String table: exercises_tables){
            Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
            if (cursor.moveToFirst()) {
                do {
                    long wid = cursor.getInt(cursor.getColumnIndex("workout_id"));
                    if(workoutId == wid){
                        int id = cursor.getInt(cursor.getColumnIndex("id")); // Get the exercise id
                        if(table.equals("bench")){
                            exercises.add(new BenchExerciseData(id,
                                                                cursor.getFloat(cursor.getColumnIndex("weight")),
                                                                cursor.getFloat(cursor.getColumnIndex("max_acceleration")),
                                                                cursor.getInt(cursor.getColumnIndex("reps")),
                                                                cursor.getFloat(cursor.getColumnIndex("mean_acceleration"))));
                        }else if(table.equals("ohp")){
                            exercises.add(new OverheadPressExerciseData(id,
                                    cursor.getFloat(cursor.getColumnIndex("weight")),
                                    cursor.getFloat(cursor.getColumnIndex("max_acceleration")),
                                    cursor.getInt(cursor.getColumnIndex("reps")),
                                    cursor.getFloat(cursor.getColumnIndex("mean_acceleration"))));
                        } else if(table.equals("running")){
                            exercises.add(new RunningExerciseData(id,
                                    cursor.getFloat(cursor.getColumnIndex("distance")),
                                    cursor.getFloat(cursor.getColumnIndex("avg_velocity")),
                                    cursor.getFloat(cursor.getColumnIndex("max_velocity"))));
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    public ExerciseData getExercise(long exerciseId){
        SQLiteDatabase db = this.getReadableDatabase();

        for(String table: exercises_tables){
            Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getInt(cursor.getColumnIndex("id"));
                    if(id == exerciseId){
                        if(table.equals("bench")){
                            return new BenchExerciseData(id,
                                    cursor.getFloat(cursor.getColumnIndex("weight")),
                                    cursor.getFloat(cursor.getColumnIndex("max_acceleration")),
                                    cursor.getInt(cursor.getColumnIndex("reps")),
                                    cursor.getFloat(cursor.getColumnIndex("mean_acceleration")));
                        }else if(table.equals("ohp")){
                            return new OverheadPressExerciseData(id,
                                    cursor.getFloat(cursor.getColumnIndex("weight")),
                                    cursor.getFloat(cursor.getColumnIndex("max_acceleration")),
                                    cursor.getInt(cursor.getColumnIndex("reps")),
                                    cursor.getFloat(cursor.getColumnIndex("mean_acceleration")));
                        } else if(table.equals("running")){
                            return new RunningExerciseData(id,
                                    cursor.getFloat(cursor.getColumnIndex("distance")),
                                    cursor.getFloat(cursor.getColumnIndex("avg_velocity")),
                                    cursor.getFloat(cursor.getColumnIndex("max_velocity")));
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return null;
    }
    /*
    public void fetchAndStoreUserData(String userId) {
        dbFirebase.collection("Workouts")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Workout workout = new Workout(
                                    Long.parseLong(document.getId()),
                                    document.getString("type"),
                                    document.getString("notes")
                            );

                            // Fetch exercises for this workout
                            fetchExercisesForWorkout(workout);
                        }
                    } else {
                        Log.e("DatabaseHelper", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchExercisesForWorkout(Workout workout) {
        String[] subCollections = {"Running", "BenchPress"};

        for (String collection : subCollections) {
            dbFirebase.collection("Workouts").document(String.valueOf(workout.getId())).collection(collection)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ExerciseData exerciseData = convertDocumentToExerciseData(document, collection);
                                workout.addExercise(exerciseData);
                            }
                            // After all exercises are fetched, store the workout in the local database
                            this.storeWorkout(workout);
                        } else {
                            Log.e("DatabaseHelper", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private ExerciseData convertDocumentToExerciseData(QueryDocumentSnapshot document, String collection) {
        if (collection.equals("Running")) {
            return new RunningExerciseData(
                    document.getDouble("distance").floatValue(),
                    document.getLong("time"),
                    document.getDouble("average_speed").floatValue()
            );
        } else if (collection.equals("BenchPress")) {
            return new BenchExerciseData(
                    document.getLong("weight"),
                    document.getLong("reps"),
                    document.getLong("sets").intValue()
            );
        }
        return null;
    }

    public void storeWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", workout.getId());
        values.put("type", workout.getType());
        values.put("notes", workout.getNotes());

        // Insert the workout
        db.insert("Workouts", null, values);

        // Now insert the exercises
        for (ExerciseData exercise : workout.getExercises()) {
            ContentValues exerciseValues = exercise.getContentValues(workout.getId());
            db.insert(exercise.getTableName(), null, exerciseValues);
        }
    }

     */
}
