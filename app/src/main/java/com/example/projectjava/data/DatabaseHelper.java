package com.example.projectjava.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

public class DatabaseHelper{

    private static DatabaseHelper instance;
    private static String users_table_name = "users";
    private static String workout_table_name = "workouts";
    private static String exercises_table_name = "exercises";
    private static List<String> exercises_tables = new ArrayList<>(Arrays.asList("bench", "running", "ohp"));
    private ArrayList<ExerciseData> exerciseDataList;  // Holds exercise data temporarily
    private FirebaseFirestore dbFirebase;

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        this.exerciseDataList = null;
        this.dbFirebase = FirebaseFirestore.getInstance();
    }

    public void beginWorkout() {
        this.exerciseDataList = new ArrayList<>();
    }

    // add workout and its exercises to firebase database
    public void finishWorkout(String type, String notes) {
        Map<String, Object> workout = new HashMap<>();
        workout.put("type", type);
        workout.put("notes", notes);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        workout.put("userId", userId); // User is logged in at this point

        dbFirebase.collection(workout_table_name)
                .add(workout)
                .addOnSuccessListener(documentReference -> {
                    String workoutId = documentReference.getId();
                    Log.w("DatabaseHelper", "Sucessfully added workout");
                    saveExercises(workoutId);
                })
                .addOnFailureListener(e -> Log.w("DatabaseHelper", "Error adding document", e));

    }

    private void saveExercises(String workoutId) {
        if (exerciseDataList == null || exerciseDataList.isEmpty()) {
            Log.w("DatabaseHelper", "No exercises to save");
            return;
        }
        for (ExerciseData ed: exerciseDataList) {
            Map<String, Object> exercise = ed.toMap(); // Assuming ExerciseData has a method to convert to Map
            exercise.put("workoutId", workoutId); // Add workout reference

            dbFirebase.collection(exercises_table_name)
                    .add(exercise)
                    .addOnSuccessListener(documentReference -> Log.d("DatabaseHelper", "Exercise saved with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("DatabaseHelper", "Error adding exercise document", e));
        }
    }

    public void addExerciseData(ExerciseData exerciseData) {
        this.exerciseDataList.add(exerciseData);
    }

    // Asynchronous method to retrieve all workouts of a given user
    public void getAllWorkouts(FirebaseFirestoreCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<Workout> workouts = new ArrayList<>();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            dbFirebase.collection(workout_table_name)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String type = document.getString("type");
                                String notes = document.getString("notes");
                                workouts.add(new Workout(id, type, notes));
                            }
                            Log.w("DatabaseHelper", "Calling workouts callback ", task.getException());
                            callback.onCallback(workouts);
                        } else {
                            Log.w("DatabaseHelper", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            Log.w("DatabaseHelper", "No user logged in");
            callback.onCallback(workouts); // callback with empty list if not logged in or error
        }
    }

    // Interface for handling Firestore callbacks
    public interface FirebaseFirestoreCallback {
        void onCallback(ArrayList<Workout> workouts);
    }

    // Asynchronously retrieves a workout by ID from Firestore
    public Task<DocumentSnapshot> getWorkout(String workoutId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Assuming each user has their own workouts under their user ID
            return dbFirebase.collection("workouts").document(workoutId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getString("userId").equals(userId)) {
                                Log.d("DatabaseHelper", "Workout fetched successfully: " + document.getData());
                            } else if (!document.exists()) {
                                Log.e("DatabaseHelper", "No workout found with ID: " + workoutId);
                            } else {
                                Log.e("DatabaseHelper", "Workout does not belong to the user: " + userId);
                            }
                        } else {
                            Log.e("DatabaseHelper", "Failed to fetch workout: " + workoutId, task.getException());
                        }
                    });
        } else {
            Log.e("DatabaseHelper", "no current user on getWorkout");
            return Tasks.forException(new Exception("No user logged in"));
        }
    }

    // Asynchronously retrieves all exercises for a given workout from Firestore
    public Task<List<ExerciseData>> getWorkoutExercises(String workoutId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception("No user logged in"));
        }

        if (workoutId == null || workoutId.isEmpty()) {
            return Tasks.forException(new NullPointerException("Workout ID must not be null or empty"));
        }

        // need to check if current user id is the same as the user id in the given workout

        // Reference to the 'exercises' collection filtered by workoutId
        Query query = dbFirebase.collection("exercises").whereEqualTo("workoutId", workoutId);

        Task<List<ExerciseData>> exercisesTask = query.get().continueWith(task -> {
            List<ExerciseData> exercises = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        System.out.println("here");
                        System.out.println(document.getString("exercise_name"));
                        ExerciseData exercise = null;  // Ensure ExerciseData class is correctly mapped
                        switch (Objects.requireNonNull(document.getString("exercise_name"))){
                            case(BenchExerciseData.table_name):
                                exercise = document.toObject(BenchExerciseData.class);
                                break;
                            case(OverheadPressExerciseData.table_name):
                                exercise = document.toObject(OverheadPressExerciseData.class);
                                break;
                            case(RunningExerciseData.table_name):
                                exercise = document.toObject(RunningExerciseData.class);
                                break;
                        }
                        if (exercise != null) {
                            exercises.add(exercise);
                        }
                    }
                } else {
                    Log.d("DatabaseHelper", "No exercises found for workout ID: " + workoutId);
                }
            } else {
                Log.e("DatabaseHelper", "Error retrieving exercises: ", task.getException());
                throw task.getException();
            }
            return exercises;
        });
        return exercisesTask;
    }

    public Task<ExerciseData> getExercise(String exerciseId){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Assuming user-based data segregation
            return dbFirebase.collection(users_table_name)
                    .document(userId)
                    .collection(exercises_table_name)
                    .document(exerciseId)
                    .get()
                    .continueWith(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String table = document.getString("type");
                                switch (Objects.requireNonNull(table)) {
                                    case BenchExerciseData.table_name:
                                        return document.toObject(BenchExerciseData.class);
                                    case OverheadPressExerciseData.table_name:
                                        return document.toObject(OverheadPressExerciseData.class);
                                    case RunningExerciseData.table_name:
                                        return document.toObject(RunningExerciseData.class);
                                    default:
                                        throw new IllegalArgumentException("Unknown exercise type");
                                }
                            }
                            throw new IllegalStateException("Exercise not found");
                        } else {
                            throw Objects.requireNonNull(task.getException());
                        }
                    });
        } else {
            return Tasks.forException(new Exception("No user logged in"));
        }
    }

}
