package com.example.projectjava.data;

import java.util.Map;

public abstract class PremadeExercise {
    private String exercise_name;
    private String id;

    public PremadeExercise(String exercise_name){
        this.id = null;
        this.exercise_name = exercise_name;
    }

    public String getExerciseName(){
        return this.exercise_name;
    }

    public void setId(String id){
        this.id = id;
    }

    public abstract Map<String, Object> toMap();
    public abstract Map<String, Float> getMetrics();
}
