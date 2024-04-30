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

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder>{
    private List<ExerciseData> exercises;
    private OnItemClickListener listener;

    public ExercisesAdapter(List<ExerciseData> exercises, OnItemClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(exercises.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateData(List exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(ExerciseData workout);
    }

    static class ExercisesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewExerciseName;
        private TextView textViewEXerciseStats;
        private CardView cardView;
        ExercisesViewHolder(View itemView) {
            super(itemView);
            this.textViewExerciseName = itemView.findViewById(R.id.exerciseName);
            this.textViewEXerciseStats = itemView.findViewById(R.id.exerciseStats);
            this.cardView = itemView.findViewById(R.id.main_container);
        }

        void bind(ExerciseData exercise, int position) {
            String text = (position+1) + ". " + exercise.getExerciseName();
            this.textViewExerciseName.setText(text);
            text = exercise.toString();
            this.textViewEXerciseStats.setText(text);
        }
    }
}
