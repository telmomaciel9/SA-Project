package com.example.projectjava.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.Workout;

import java.util.ArrayList;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder> {
    private ArrayList<Workout> workouts;
    private OnItemClickListener listener;

    public WorkoutsAdapter(ArrayList<Workout> workouts, OnItemClickListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_history_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = (Workout) workouts.get(position);
        holder.bind(workout);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(workouts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void updateData(ArrayList workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(Workout workout);
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWorkoutId;
        TextView textViewWorkoutType;
        TextView textViewWorkoutNotes;
        CardView cardView;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            textViewWorkoutId = itemView.findViewById(R.id.textViewWorkoutId);
            textViewWorkoutType = itemView.findViewById(R.id.textViewWorkoutType);
            textViewWorkoutNotes = itemView.findViewById(R.id.textViewWorkoutNotes);
            cardView = itemView.findViewById(R.id.main_container);
        }

        void bind(Workout workout) {
            String id = "Id: " + workout.getId();
            textViewWorkoutId.setText(id);
            String type = "Type: " + workout.getType();
            textViewWorkoutType.setText(type);
            String notes = "Notes: " + workout.getNotes();
            textViewWorkoutNotes.setText(notes);
        }
    }
}
