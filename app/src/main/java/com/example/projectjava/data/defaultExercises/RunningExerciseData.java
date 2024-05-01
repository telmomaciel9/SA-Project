package com.example.projectjava.data.defaultExercises;

import com.example.projectjava.data.ExerciseData;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunningExerciseData extends ExerciseData {
    public static final String table_name = "running";
    private float distance;
    private float avg_velocity;
    private float max_velocity;

    public RunningExerciseData(){
        super("Running", -1);
    }

    public RunningExerciseData(float distance, float avg_velocity, float max_velocity, long time_stamp) {
        super("Running", time_stamp);
        this.distance = distance;
        this.avg_velocity = avg_velocity;
        this.max_velocity = max_velocity;
    }

    public String getTableName() {
        return table_name;
    }

    public String toString(){
        return "Distance ran: " + this.distance + "m;\n" +
                "Average Velocity: " + this.avg_velocity + "m/s;\n" +
                "Maximum Velocity: " + this.max_velocity + "m/s";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("exercise_name", table_name);
        values.put("distance", this.distance);
        values.put("avg_velocity", this.avg_velocity);
        values.put("max_velocity", this.max_velocity);
        values.put("time_stamp", super.getTimeStamp());
        return values;
    }

    public static ExerciseData deserealize(DocumentSnapshot document){
        float distance = document.getDouble("distance").floatValue();
        float avg_vel = document.getDouble("avg_velocity").floatValue();
        float max_vel = document.getDouble("max_velocity").floatValue();
        long time_stamp = document.getLong("time_stamp");
        return new RunningExerciseData(distance, avg_vel, max_vel, time_stamp);
    }

    @Override
    public float getExerciseMetrics(String yMetric) {
        float y = 0;

        switch (yMetric){
            case ("Distance"):
                y = this.distance;
                break;
            case ("Average Velocity"):
                y = this.avg_velocity;
                break;
            case ("Maximum Velocitiy"):
                y = this.max_velocity;
                break;
        }

        return y;
    }

    public List<String> getExerciseProgressMetrics(){
        return Arrays.asList("Distance", "Average Velocity", "Maximum Velocitiy");
    }

    public List<String> getExerciseProgressMetricsAbrv(){
        return Arrays.asList("Distance", "Avg Vel", "Max Vel");
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAvg_velocity() {
        return avg_velocity;
    }

    public void setAvg_velocity(float avg_velocity) {
        this.avg_velocity = avg_velocity;
    }

    public float getMax_velocity() {
        return max_velocity;
    }

    public void setMax_velocity(float max_velocity) {
        this.max_velocity = max_velocity;
    }
}
