package com.example.projectjava.data;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class PremadeWorkout {
    private String id;
    private String workout_name;
    private List<PremadeExercise> exercises;

    public PremadeWorkout(String workout_name){
        this.workout_name = workout_name;
    }

    public static PremadeWorkout deserealize(DocumentSnapshot document){
        return new PremadeWorkout(document.getString("workout_name"));
    }

    public String getWorkout_name(){
        return this.workout_name;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setWorkout_name(String workout_name){
        this.workout_name = workout_name;
    }

    public void setExercises(List<PremadeExercise> exercises){
        this.exercises = exercises;
    }

    public List<PremadeExercise> getExercises(){
        return this.exercises;
    }
}
