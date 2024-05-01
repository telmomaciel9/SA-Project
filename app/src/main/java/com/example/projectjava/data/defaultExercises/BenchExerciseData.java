package com.example.projectjava.data.defaultExercises;

import androidx.annotation.NonNull;

import com.example.projectjava.data.ExerciseData;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchExerciseData extends ExerciseData {
    public static final String table_name = "bench";
    private float weight;
    private int repetitions;
    private float mean_acceleration;
    private float max_acceleration;

    public BenchExerciseData(){
        super("Bench Press", -1);
    }

    public BenchExerciseData(float weight, float max_acceleration, int repetitions, float mean_acceleration, long time_stamp){
        super("Bench Press", time_stamp);
        this.weight = weight;
        this.max_acceleration = max_acceleration;
        this.mean_acceleration = mean_acceleration;
        this.repetitions = repetitions;
    }

    public String getTableName() {
        return table_name;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("exercise_name", table_name);
        values.put("weight", this.weight);
        values.put("repetitions", this.repetitions);
        values.put("mean_acceleration", this.mean_acceleration);
        values.put("max_acceleration", this.max_acceleration);
        values.put("time_stamp", super.getTimeStamp());
        return values;
    }

    public static ExerciseData deserealize(DocumentSnapshot document){
        float weight = document.getDouble("weight").floatValue();
        int repetitions = Math.toIntExact(document.getLong("repetitions"));
        float mean_acceleration = document.getDouble("mean_acceleration").floatValue();
        float max_acceleration = document.getDouble("max_acceleration").floatValue();
        long time_stamp = document.getLong("time_stamp");
        System.out.println("Timestamp: " + time_stamp);
        return new BenchExerciseData(weight, max_acceleration, repetitions, mean_acceleration, time_stamp);
    }

    @Override
    public float getExerciseMetrics(String yMetric) {
        float y = 0;

        switch (yMetric){
            case ("Weight"):
                y = this.weight;
                break;
            case ("Repetitions"):
                y = (float) this.repetitions;
                break;
            case ("Mean Acceleration"):
                y = this.mean_acceleration;
                break;
            case ("Maximum Acceleration"):
                y = this.max_acceleration;
        }

        return y;
    }

    public List<String> getExerciseProgressMetrics(){
        return Arrays.asList("Weight", "Repetitions", "Mean Acceleration", "Maximum Acceleration");
    }

    public List<String> getExerciseProgressMetricsAbrv(){
        return Arrays.asList("Weight", "Reps", "Mean Acc", "Max Acc");
    }

    @NonNull
    public String toString(){
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n" +
                "Mean Acceleration: " + this.mean_acceleration + "m/s²;\n" +
                "Max Acceleration: " + this.max_acceleration + "m/s²";
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public float getmean_acceleration() {
        return mean_acceleration;
    }

    public void setmean_acceleration(float mean_acceleration) {
        this.mean_acceleration = mean_acceleration;
    }

    public float getmax_acceleration() {
        return max_acceleration;
    }

    public void setmax_acceleration(float max_acceleration) {
        this.max_acceleration = max_acceleration;
    }

}
