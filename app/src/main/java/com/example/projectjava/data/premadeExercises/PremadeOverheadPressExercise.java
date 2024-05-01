package com.example.projectjava.data.premadeExercises;

import com.example.projectjava.data.PremadeExercise;

import java.util.HashMap;
import java.util.Map;

public class PremadeOverheadPressExercise extends PremadeExercise {
    public static final String table_name = "premade_ohp";
    private int reps;
    private int sets;

    public PremadeOverheadPressExercise(int reps, int sets){
        super("Overhead Press");
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
}
