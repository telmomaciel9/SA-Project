package com.example.projectjava.database;

import android.content.ContentValues;

public abstract class ExerciseData{
    public ExerciseData() {

    }
    public abstract ContentValues getContentValues(long workoutId);
    public abstract String getTableName();

}
