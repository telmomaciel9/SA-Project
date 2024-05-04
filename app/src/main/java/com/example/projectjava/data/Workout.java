package com.example.projectjava.data;

import java.util.Date;
import java.util.List;

public class Workout {
    private String id;
    private String workout_name;
    private String type;
    private String notes;
    private String begin_date;
    private long duration;
    private List<ExerciseData> exercises;

    // No-argument constructor needed for Firebase deserialization
    public Workout() {}

    public Workout(String id, String type, String notes, String workout_name, String begin_date, long duration) {
        this.id = id;
        this.workout_name = workout_name;
        this.type = type;
        this.notes = notes;
        this.begin_date = begin_date;
        this.duration = duration;
    }

    public String getWorkout_name(){
        return this.workout_name;
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
    public String getBegin_date(){
        return this.begin_date;
    }

    public long getDuration(){
        return this.duration;
    }

    public void setId(String id) {
        this.id = id;
    }
    public List<ExerciseData> getExercises(){return this.exercises;}

}
