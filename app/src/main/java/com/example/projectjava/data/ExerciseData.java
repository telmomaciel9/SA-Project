package com.example.projectjava.data;

import android.content.ContentValues;

public abstract class ExerciseData{
    private String exerciseName;
    private long id;
    public ExerciseData(String exerciseName) {
        this.id = -1;
        this.exerciseName = exerciseName;
    }
    public ExerciseData(long id, String exerciseName) {
        this.id = id;
        this.exerciseName = exerciseName;
    }
    public abstract ContentValues getContentValues(long workoutId);
    public abstract String getTableName();
    public String getExerciseName(){
        return this.exerciseName;
    }
    public long getId(){
        return this.id;
    }
    public void setId(){
        this.id = id;
    }
}
