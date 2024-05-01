package com.example.projectjava.data.defaultExercises;

import com.example.projectjava.data.ExerciseData;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverheadPressExerciseData extends ExerciseData {
    public static final String table_name = "ohp";
    private float weight;
    private int repetitions;
    private float max_acceleration;
    private float mean_acceleration;

    public OverheadPressExerciseData(){
        super("Overhead Press", -1);
    }

    public OverheadPressExerciseData(float w, float ma, int r, float meana, long time_stamp){
        super("Overhead Press", time_stamp);
        this.weight = w;
        this.max_acceleration = ma;
        this.repetitions = r;
        this.mean_acceleration = meana;
    }

    public String getTableName() {
        return table_name;
    }

    public String toString(){
        return "Weight: " + this.weight + "kg; Repetitions: " + this.repetitions + ";\n" +
                "Mean Acceleration: " + this.mean_acceleration + "m/s²;\n" +
                "Max Acceleration: " + this.max_acceleration + "m/s²";
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
        System.out.println("OHP:" + weight + "; reps: " + repetitions + "; mean_acc: " + mean_acceleration + "; max_acc" + max_acceleration + "time_stamp: " + time_stamp);
        return new OverheadPressExerciseData(weight, max_acceleration, repetitions, mean_acceleration, time_stamp);
    }

    @Override
    public float getExerciseMetrics(String yMetric) {
        float y = 0;

        switch (yMetric){
            case ("Weight"):
                y = this.weight;
                break;
            case ("Repetitions"):
                y = this.repetitions;
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

    public float getmax_acceleration() {
        return max_acceleration;
    }

    public void setmax_acceleration(float max_acceleration) {
        this.max_acceleration = max_acceleration;
    }

    public float getmean_acceleration() {
        return mean_acceleration;
    }

    public void setmean_acceleration(float mean_acceleration) {
        this.mean_acceleration = mean_acceleration;
    }
}
