package com.example.projectjava;

import java.time.LocalDateTime;

public class Workout {
    private String type;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private String notes;
    // Futuramente, ter a lista de exercícios deste workout
    //private List<Exercise> Exercises;

    public Workout(LocalDateTime date){
        this.start_date = date;
    }

    public Workout(LocalDateTime start_date, LocalDateTime end_date, String notes, String type){
        this.start_date = start_date;
        this.end_date = end_date;
        this.notes = notes;
        this.type = type;
    }

    public void setStart_date(LocalDateTime date){
        this.start_date = date;
    }

    public LocalDateTime getStart_date(){
        return this.start_date;
    }

    public void setEnd_date(LocalDateTime date){
        this.end_date = date;
    }

    public LocalDateTime getEnd_date(){
        return this.end_date;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public String getNotes(){
        return this.notes;
    }
}
