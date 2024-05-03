package com.example.projectjava.data.premadeExercises;

import com.example.projectjava.data.PremadeExercise;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class PremadeRunningExercise extends PremadeExercise {
    public static final String table_name = "premade_running";
    private float distance;
    private float avg_velocity;

    public PremadeRunningExercise(float distance, float avg_velocity){
        super("Running");
        this.distance = distance;
        this.avg_velocity = avg_velocity;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("exercise_name", table_name);
        map.put("distance", this.distance);
        map.put("avg_velocity", this.avg_velocity);
        return map;
    }

    public static PremadeExercise deserealize(DocumentSnapshot document){
        return new PremadeRunningExercise(document.getDouble("distance").floatValue(), document.getDouble("avg_velocity").floatValue());
    }

    public Map<String, Float> getMetrics(){
        Map<String, Float> metrics = new HashMap<>();
        metrics.put("Distance", distance);
        metrics.put("Average Velocity", avg_velocity);
        return metrics;
    }
}
