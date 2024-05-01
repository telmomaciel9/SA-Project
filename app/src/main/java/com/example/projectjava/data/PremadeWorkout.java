package com.example.projectjava.data;

import java.util.List;

public class PremadeWorkout {
    private String id;
    private String workout_name;
    private List<PremadeExercise> exercises;

    public PremadeWorkout(String workout_name){
        this.workout_name = workout_name;
    }
}
