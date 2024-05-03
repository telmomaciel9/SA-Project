package com.example.projectjava.data.premadeExercises;

import com.example.projectjava.data.PremadeExercise;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class PremadeBenchExercise extends PremadeExercise {
    public static final String table_name = "premade_bench";
    private int reps;
    private int sets;

    public PremadeBenchExercise(int reps, int sets){
        super("Bench press");
        this.reps = reps;
        this.sets = sets;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("exercise_name", table_name);
        map.put("repetitions", this.reps);
        map.put("sets", this.sets);
        return map;
    }

    public static PremadeExercise deserealize(DocumentSnapshot document){
        return new PremadeBenchExercise(Math.toIntExact(document.getLong("repetitions")), Math.toIntExact(document.getLong("sets")));
    }

    public Map<String, Float> getMetrics(){
        Map<String, Float> metrics = new HashMap<>();
        metrics.put("Repetitions", (float) reps);
        metrics.put("Sets", (float) sets);
        return metrics;
    }
}