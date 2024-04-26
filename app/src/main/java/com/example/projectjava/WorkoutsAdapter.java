package com.example.projectjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder> {
    private ArrayList<Workout> workouts;

    public WorkoutsAdapter(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = (Workout) workouts.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void updateData(ArrayList workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDetails;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
        }

        void bind(Workout workout) {
            String details = "ID: " + workout.getId() +
                    ", Type: " + workout.getType() +
                    ", Notes: " + workout.getNotes();
            textViewDetails.setText(details);
        }
    }
}
