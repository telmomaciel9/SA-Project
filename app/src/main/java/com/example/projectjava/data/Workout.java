package com.example.projectjava.data;

import java.util.List;

public class Workout {
    private long id;
    private String type;
    private String notes;
    private List<ExerciseData> exercises;

    public Workout(long id, String type, String notes) {
        this.id = id;
        this.type = type;
        this.notes = notes;
    }

    public Workout(long id, String type, String notes, List<ExerciseData> exercises) {
        this.id = id;
        this.type = type;
        this.notes = notes;
        this.exercises = exercises;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }
    public List<ExerciseData> getExercises(){return this.exercises;}

    public void addExercise(ExerciseData ed) {
        this.exercises.add(ed);
    }
}
