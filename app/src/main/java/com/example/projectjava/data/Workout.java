package com.example.projectjava.data;

import java.util.List;

public class Workout {
    private String id;
    private String type;
    private String notes;
    private List<ExerciseData> exercises;

    // No-argument constructor needed for Firebase deserialization
    public Workout() {}

    public Workout(String id, String type, String notes) {
        this.id = id;
        this.type = type;
        this.notes = notes;
    }

    public Workout(String id, String type, String notes, List<ExerciseData> exercises) {
        this.id = id;
        this.type = type;
        this.notes = notes;
        this.exercises = exercises;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(String id) {
        this.id = id;
    }
    public List<ExerciseData> getExercises(){return this.exercises;}

    public void addExercise(ExerciseData ed) {
        this.exercises.add(ed);
    }
}
