package com.example.projectjava.data;

import android.text.format.Time;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectjava.UI.ExercisesAdapter;
import com.example.projectjava.data.defaultExercises.BenchExerciseData;
import com.example.projectjava.data.defaultExercises.OverheadPressExerciseData;
import com.example.projectjava.data.defaultExercises.RunningExerciseData;
import com.example.projectjava.data.premadeExercises.PremadeBenchExercise;
import com.example.projectjava.data.premadeExercises.PremadeOverheadPressExercise;
import com.example.projectjava.data.premadeExercises.PremadeRunningExercise;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseHelper{
    private static DatabaseHelper instance;
    private static String users_table_name = "users";
    private static String workout_table_name = "workouts";
    private static String premade_workout_table_name = "premade_workouts";
    private static String exercises_table_name = "exercises";
    private static String premade_exercises_table_name = "premade_exercises";
    private ArrayList<ExerciseData> exerciseDataList;       // Holds exercise data temporarily
    private List<PremadeExercise> premadeExerciseList;      // Holds premade exercises temporarily
    private PremadeWorkout activePremadeWorkout;            // Active premade workout (a valid PremadeWorkout or null)
    private int premadeExerciseNr;                          // Tracks the current and next PremadeExercises in a PremadeWorkout
    private LocalDateTime activeWorkoutBeginDate;
    private boolean isWorkoutActive;
    private FirebaseFirestore dbFirebase;

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        this.isWorkoutActive = false;
        this.exerciseDataList = null;
        this.activeWorkoutBeginDate = null;
        this.premadeExerciseList = null;
        this.activePremadeWorkout = null;
        this.premadeExerciseNr = 0;
        this.dbFirebase = FirebaseFirestore.getInstance();
    }

    public void setIsWorkoutActive(boolean value){
        this.isWorkoutActive = value;
    }

    public boolean getIsWorkoutActive(){
        return this.isWorkoutActive;
    }

    public void setActivePremadeWorkout(PremadeWorkout pw){
        this.isWorkoutActive = true;
        this.activeWorkoutBeginDate = LocalDateTime.now();
        this.activePremadeWorkout = pw;
        this.exerciseDataList = new ArrayList<>();
    }

    public PremadeWorkout getActivePremadeWorkout() {
        return this.activePremadeWorkout;
    }

    public PremadeExercise getNextPremadeExercise(List<PremadeExercise> exercises, boolean skipExercise){
        PremadeExercise pe = null;
        if(skipExercise){
            this.premadeExerciseNr++;
        }

        int count = 0;
        for(PremadeExercise pes: exercises){
            count += pes.getSets();
            if(count > this.premadeExerciseNr){
                return pes;
            }
        }
        return pe;
    }

    // Chamar quando um premade workout acabar
    public void endPremadeWorkout(){
        this.activePremadeWorkout = null;
        this.premadeExerciseNr = 0;
    }

    public void addPremadeExercise(PremadeExercise pe){
        if(this.premadeExerciseList == null){
            this.premadeExerciseList = new ArrayList<>();
        }
        this.premadeExerciseList.add(pe);
    }

    public List<PremadeExercise> getPremadeExerciseList(){
        return this.premadeExerciseList;
    }

    public void addPremadeWorkout(PremadeWorkout premadeWorkout){
        Map<String, Object> premade_workout_values = new HashMap<>();
        premade_workout_values.put("workout_name", premadeWorkout.getWorkout_name());
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        premade_workout_values.put("userId", userId); // User is logged in at this point

        dbFirebase.collection(premade_workout_table_name)
                .add(premade_workout_values)
                .addOnSuccessListener(documentReference -> {
                    String premade_workout_id = documentReference.getId();
                    savePremadeExercises(premade_workout_id);
                })
                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Error adding document", e));
    }

    public void savePremadeExercises(String preWorkoutId){
        if (premadeExerciseList == null || premadeExerciseList.isEmpty()) {
            Log.e("DatabaseHelper", "No premade exercises to save");
            return;
        }

        for (PremadeExercise ex: premadeExerciseList) {
            Map<String, Object> exercise = ex.toMap(); // Assuming ExerciseData has a method to convert to Map
            exercise.put("workoutId", preWorkoutId); // Add workout reference

            dbFirebase.collection(premade_exercises_table_name)
                    .add(exercise)
                    .addOnSuccessListener(documentReference -> Log.d("DatabaseHelper", "Exercise saved with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("DatabaseHelper", "Error adding exercise document", e));
        }
        this.premadeExerciseList = null;
    }

    public Task<List<PremadeWorkout>> getAllPremadeWorkouts(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception("No user logged in"));
        }

        // Reference to the 'exercises' collection filtered by workoutId
        Query query = dbFirebase.collection(premade_workout_table_name).whereEqualTo("userId", currentUser.getUid());

        Task<List<PremadeWorkout>> premadeWorkoutsTask = query.get().continueWith(task -> {
            List<PremadeWorkout> premadeWorkouts = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        System.out.println("Antes");
                        PremadeWorkout pw = PremadeWorkout.deserealize(document);
                        System.out.println("Depois");
                        if (pw != null) {
                            pw.setId(document.getId());
                            premadeWorkouts.add(pw);
                        }else{
                            System.out.println("Premade workout is null!");
                        }
                    }
                } else {
                    Log.d("DatabaseHelper", "No premade workouts found!");
                }
            } else {
                Log.e("DatabaseHelper", "Error retrieving premade workouts: ", task.getException());
                throw task.getException();
            }
            return premadeWorkouts;
        });
        return premadeWorkoutsTask;
    }

    public Task<DocumentSnapshot> getPremadeWorkout(String workoutId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Assuming each user has their own workouts under their user ID
            return dbFirebase.collection(premade_workout_table_name).document(workoutId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getString("userId").equals(userId)) {
                                Log.d("DatabaseHelper", "Premade workout fetched successfully: " + document.getData());
                            } else if (!document.exists()) {
                                Log.e("DatabaseHelper", "No premade workout found with ID: " + workoutId);
                            } else {
                                Log.e("DatabaseHelper", "Premade workout does not belong to the user: " + userId);
                            }
                        } else {
                            Log.e("DatabaseHelper", "Failed to fetch premade workout: " + workoutId, task.getException());
                        }
                    });
        } else {
            Log.e("DatabaseHelper", "no current user on getPremadeWorkout");
            return Tasks.forException(new Exception("No user logged in"));
        }
    }

    public Task<List<PremadeExercise>> getPremadeWorkoutExercises(String workoutId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception("No user logged in"));
        }

        if (workoutId == null || workoutId.isEmpty()) {
            return Tasks.forException(new NullPointerException("Workout ID must not be null or empty"));
        }

        // need to check if current user id is the same as the user id in the given workout

        // Reference to the 'exercises' collection filtered by workoutId
        Query query = dbFirebase.collection("premade_exercises").whereEqualTo("workoutId", workoutId);

        Task<List<PremadeExercise>> exercisesTask = query.get().continueWith(task -> {
            List<PremadeExercise> exercises = new ArrayList<>();
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        PremadeExercise premade_exercise = null;
                        switch (Objects.requireNonNull(document.getString("exercise_name"))){
                            case(PremadeBenchExercise.table_name):
                                premade_exercise = PremadeBenchExercise.deserealize(document);
                                break;
                            case(PremadeOverheadPressExercise.table_name):
                                premade_exercise = PremadeOverheadPressExercise.deserealize(document);
                                break;
                            case(PremadeRunningExercise.table_name):
                                premade_exercise = PremadeRunningExercise.deserealize(document);
                                break;
                            default:
                                System.out.println("Invalid exercise_name!");
                                break;
                        }
                        if (premade_exercise != null) {
                            premade_exercise.setId(document.getId());
                            exercises.add(premade_exercise);
                        }else{
                            System.out.println("Premade exercise is null!");
                        }
                    }
                } else {
                    Log.d("DatabaseHelper", "No exercises found for workout ID: " + workoutId);
                }
            } else {
                Log.e("DatabaseHelper", "Error retrieving exercises: ", task.getException());
                throw task.getException();
            }
            for(PremadeExercise pe: exercises){
                System.out.println(pe);
            }
            Collections.sort(exercises, new Comparator<PremadeExercise>() {
                @Override
                public int compare(PremadeExercise ex1, PremadeExercise ex2) {
                    return Integer.compare(ex1.getOrder(), ex2.getOrder());
                }
            });
            for(PremadeExercise pe: exercises){
                System.out.println(pe);
            }

            return exercises;
        });
        return exercisesTask;
    }

    public void beginWorkout() {
        this.isWorkoutActive = true;
        this.activeWorkoutBeginDate = LocalDateTime.now();
        this.exerciseDataList = new ArrayList<>();
    }

    // add workout and its exercises to firebase database
    public void finishWorkout(String type, String notes, String workout_name) {
        Map<String, Object> workout = new HashMap<>();
        workout.put("workout_name", workout_name);
        workout.put("type", type);
        workout.put("notes", notes);

        long duration = Duration.between(this.activeWorkoutBeginDate, LocalDateTime.now()).getSeconds();
        workout.put("begin_date", this.activeWorkoutBeginDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        workout.put("duration", duration);
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        workout.put("userId", userId); // User is logged in at this point

        dbFirebase.collection(workout_table_name)
                .add(workout)
                .addOnSuccessListener(documentReference -> {
                    String workoutId = documentReference.getId();
                    Log.w("DatabaseHelper", "Sucessfully added workout");
                    saveExercises(workoutId);
                })
                .addOnFailureListener(e -> Log.e("DatabaseHelper", "Error adding document"));
        this.activeWorkoutBeginDate = null;
        this.isWorkoutActive = false;
    }

    private void saveExercises(String workoutId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (exerciseDataList == null || exerciseDataList.isEmpty()) {
            Log.w("DatabaseHelper", "No exercises to save");
            return;
        }

        if(user != null){
            for (ExerciseData ed: exerciseDataList) {
                Map<String, Object> exercise = ed.toMap(); // Assuming ExerciseData has a method to convert to Map
                exercise.put("workoutId", workoutId); // Add workout reference
                exercise.put("userId", user.getUid());

                dbFirebase.collection(exercises_table_name)
                        .add(exercise)
                        .addOnSuccessListener(documentReference -> Log.d("DatabaseHelper", "Exercise saved with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w("DatabaseHelper", "Error adding exercise document", e));
            }
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
                                String workout_name = document.getString("workout_name");
                                String type = document.getString("type");
                                String notes = document.getString("notes");
                                String begin_date = document.getString("begin_date");
                                Long duration = document.getLong("duration");
                                workouts.add(new Workout(id, type, notes, workout_name, begin_date, duration));
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
                            exercise.setId(document.getId());
                            exercises.add(exercise);
                        }else{
                            Log.e("Database Helper", "exercise is null!");
                        }
                    }
                } else {
                    Log.d("DatabaseHelper", "No exercises found for workout ID: " + workoutId);
                }
            } else {
                Log.e("DatabaseHelper", "Error retrieving exercises: ", task.getException());
                throw task.getException();
            }

            // Return exercises ordered chronologically
            Collections.sort(exercises, new Comparator<ExerciseData>() {
                @Override
                public int compare(ExerciseData ex1, ExerciseData ex2) {
                    return Long.compare((long) ex1.getTimeStamp(), (long) ex2.getTimeStamp());
                }
            });

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
        Query query = dbFirebase.collection(exercises_table_name).whereEqualTo("userId", currentUser.getUid());

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

            // Return exercises ordered chronologically
            Collections.sort(exercises, new Comparator<ExerciseData>() {
                @Override
                public int compare(ExerciseData ex1, ExerciseData ex2) {
                    return Long.compare((long) ex1.getTimeStamp(), (long) ex2.getTimeStamp());
                }
            });

            return exercises;
        });
        return exercisesTask;
    }
}
