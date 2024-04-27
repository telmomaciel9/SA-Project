package com.example.projectjava.data;

import android.content.ContentValues;

public abstract class ExerciseData{
    private String exerciseName;
    public ExerciseData(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    public abstract ContentValues getContentValues(long workoutId);
    public abstract String getTableName();
    public String getExerciseName(){
        return this.exerciseName;
    }
}
