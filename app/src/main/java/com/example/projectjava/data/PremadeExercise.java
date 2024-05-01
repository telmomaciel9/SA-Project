package com.example.projectjava.data;

import java.util.Map;

public abstract class PremadeExercise {
    private String exercise_name;
    private String id;

    public PremadeExercise(String exercise_name){
        this.id = null;
        this.exercise_name = exercise_name;
    }

    public abstract Map<String, Object> toMap();
}
