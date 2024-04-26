package com.example.projectjava;

public class Workout {
    private long id;
    private String type;
    private String notes;

    public Workout(long id, String type, String notes) {
        this.id = id;
        this.type = type;
        this.notes = notes;
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
}
