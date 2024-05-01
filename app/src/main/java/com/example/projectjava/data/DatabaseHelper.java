package com.example.projectjava.data;

import android.util.Log;

import com.example.projectjava.data.defaultExercises.BenchExerciseData;
import com.example.projectjava.data.defaultExercises.OverheadPressExerciseData;
import com.example.projectjava.data.defaultExercises.RunningExerciseData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseHelper{
    private static DatabaseHelper instance;
    private static String users_table_name = "users";
    private static String workout_table_name = "workouts";
    private static String premade_workout_table_name = "premade_workouts";
    private static String exercises_table_name = "exercises";
    private static String premade_exercises_table_name = "premade_exercises";
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

    public void addPremadeWorkout(String workout_name, List<PremadeExercise> exercises){
        Map<String, Object> premade_workout = new HashMap<>();
        premade_workout.put("workout_name", workout_name);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        premade_workout.put("userId", userId); // User is logged in at this point

        dbFirebase.collection(premade_workout_table_name)
                .add(premade_workout)
                .addOnSuccessListener(documentReference -> {
                    String premade_workout_id = documentReference.getId();
                    savePremadeExercises(premade_workout_id, exercises);
                })
                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Error adding document", e));
    }

    public void savePremadeExercises(String preWorkoutId, List<PremadeExercise> exercises){
        if (exercises == null || exercises.isEmpty()) {
            Log.e("DatabaseHelper", "No exercises to save");
            return;
        }

        for (PremadeExercise ex: exercises) {
            Map<String, Object> exercise = ex.toMap(); // Assuming ExerciseData has a method to convert to Map
            exercise.put("workoutId", preWorkoutId); // Add workout reference

            dbFirebase.collection(premade_exercises_table_name)
                    .add(exercise)
                    .addOnSuccessListener(documentReference -> Log.d("DatabaseHelper", "Exercise saved with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("DatabaseHelper", "Error adding exercise document", e));
        }
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
                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Error adding document", e));
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
                                // exercise = document.toObject(BenchExerciseData.class);
                                exercise = BenchExerciseData.deserealize(document);
                                break;
                            case(OverheadPressExerciseData.table_name):
                                // exercise = document.toObject(OverheadPressExerciseData.class);
                                exercise = OverheadPressExerciseData.deserealize(document);
                                break;
                            case(RunningExerciseData.table_name):
                                // exercise = document.toObject(RunningExerciseData.class);
                                exercise = RunningExerciseData.deserealize(document);
                                break;
                            default:
                                System.out.println("Invalid exercise_name!");
                                break;
                        }
                        if (exercise != null) {
                            System.out.println("Adding the exercise!");
                            System.out.println("TimeStamp: " + exercise.getTimeStamp());
                            exercise.setId(document.getId());
                            exercises.add(exercise);
                        }else{
                            System.out.println("Exercise is null!");
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

    public Task<ExerciseData> getExercise(String exerciseId) {
        if (exerciseId == null || exerciseId.isEmpty()) {
            return Tasks.forException(new NullPointerException("Exercise ID must not be null or empty"));
        }

        // Directly accessing the 'exercises' collection to retrieve a specific exercise by its ID.
        return dbFirebase.collection("exercises")
                .document(exerciseId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String type = document.getString("exercise_name");
                            if (type == null) {
                                throw new IllegalArgumentException("Exercise type is missing in the document");
                            }
                            switch (type) {
                                case BenchExerciseData.table_name:
                                    return document.toObject(BenchExerciseData.class);
                                case OverheadPressExerciseData.table_name:
                                    return document.toObject(OverheadPressExerciseData.class);
                                case RunningExerciseData.table_name:
                                    return document.toObject(RunningExerciseData.class);
                                default:
                                    throw new IllegalArgumentException("Unknown exercise type: " + type);
                            }
                        } else {
                            throw new IllegalStateException("Exercise not found with ID: " + exerciseId);
                        }
                    } else {
                        throw Objects.requireNonNull(task.getException(), "Error fetching exercise");
                    }
                });
    }


    public Task<List<ExerciseData>> getExercisesByType(ExerciseData exercise) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception("No user logged in"));
        }

        if (exercise == null) {
            return Tasks.forException(new NullPointerException("Exercise must not be null"));
        }

        // need to check if current user id is the same as the user id in the given workout

        // Reference to the 'exercises' collection filtered by workoutId
        Query query = dbFirebase.collection("exercises");

        Task<List<ExerciseData>> exercisesTask = query.get().continueWith(task -> {
            List<ExerciseData> exercises = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        ExerciseData e = null;  // Ensure ExerciseData class is correctly mapped
                        switch (Objects.requireNonNull(document.getString("exercise_name"))){
                            case(BenchExerciseData.table_name):
                                // e = document.toObject(BenchExerciseData.class);
                                e = BenchExerciseData.deserealize(document);
                                break;
                            case(OverheadPressExerciseData.table_name):
                                // e = document.toObject(OverheadPressExerciseData.class);
                                e = OverheadPressExerciseData.deserealize(document);
                                break;
                            case(RunningExerciseData.table_name):
                                // e = document.toObject(RunningExerciseData.class);
                                e = RunningExerciseData.deserealize(document);
                                break;
                            default:
                                Log.e("Database", "Exercise type not valid");
                                break;
                        }
                        if (e != null && e.getClass() == exercise.getClass()) {
                            System.out.println("Reps: " + e.getExerciseMetrics("Repetitions"));
                            e.setId(document.getId());
                            exercises.add(e);
                        }
                    }
                } else {
                    Log.d("DatabaseHelper", "No exercises found for the type: " + exercise.getClass());
                }
            } else {
                Log.e("DatabaseHelper", "Error retrieving exercises: ", task.getException());
                throw task.getException();
            }
            return exercises;
        });
        return exercisesTask;
    }

}
