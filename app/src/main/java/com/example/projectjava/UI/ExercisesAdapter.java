package com.example.projectjava.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.ExerciseData;
import com.example.projectjava.data.Workout;

import java.util.ArrayList;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder>{
    private List<ExerciseData> exercises;

    public ExercisesAdapter(List<ExerciseData> exercises) {
        this.exercises = exercises;
    }

    @Override
    public ExercisesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_item, parent, false);
        return new ExercisesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        ExerciseData exerciseData = exercises.get(position);
        holder.bind(exerciseData, position);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateData(ArrayList exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(Workout workout);
    }

    static class ExercisesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewExerciseName;
        ExercisesViewHolder(View itemView) {
            super(itemView);
            this.textViewExerciseName = itemView.findViewById(R.id.exerciseName);
        }

        void bind(ExerciseData exercise, int position) {
            String text = (position+1) + ". " + exercise.getExerciseName();
            this.textViewExerciseName.setText(text);
        }
    }
}
