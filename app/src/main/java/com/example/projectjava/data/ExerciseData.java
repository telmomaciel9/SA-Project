package com.example.projectjava.data;

import java.util.List;
import java.util.Map;

public abstract class ExerciseData{
    private String exerciseName;
    private String id;
    private long time_stamp;

    public ExerciseData(String exerciseName, long time_stamp) {
        this.id = null;
        this.exerciseName = exerciseName;
        this.time_stamp = time_stamp;
    }

    public abstract String getTableName();
    public String getExerciseName(){
        return this.exerciseName;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public long getTimeStamp(){
        return this.time_stamp;
    }
    public void setTimeStamp(long time_stamp){
        this.time_stamp = time_stamp;
    }
    public abstract Map<String, Object> toMap();
    public abstract List<String> getExerciseProgressMetrics();
    public abstract List<String> getExerciseProgressMetricsAbrv();
    public abstract float getExerciseMetrics(String yMetric);
}
