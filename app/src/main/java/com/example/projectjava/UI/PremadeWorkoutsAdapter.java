package com.example.projectjava.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.PremadeWorkout;
import com.example.projectjava.data.Workout;

import java.util.ArrayList;
import java.util.List;

public class PremadeWorkoutsAdapter extends RecyclerView.Adapter<PremadeWorkoutsAdapter.PremadeWorkoutsViewHolder>{
    private List<PremadeWorkout> workouts;
    private OnItemClickListener listener;

    public PremadeWorkoutsAdapter(List<PremadeWorkout> workouts, OnItemClickListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @Override
    public PremadeWorkoutsAdapter.PremadeWorkoutsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premade_workout_item, parent, false);
        return new PremadeWorkoutsAdapter.PremadeWorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PremadeWorkoutsAdapter.PremadeWorkoutsViewHolder holder, int position) {
        PremadeWorkout pw = (PremadeWorkout) workouts.get(position);
        holder.bind(pw);

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

    public void updateData(List<PremadeWorkout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(PremadeWorkout premadeWorkout);
    }

    static class PremadeWorkoutsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvPremadeWorkoutName;
        PremadeWorkoutsViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.main_container);
            tvPremadeWorkoutName = itemView.findViewById(R.id.tvPremadeWorkoutName);
        }

        void bind(PremadeWorkout pW) {
            tvPremadeWorkoutName.setText(pW.getWorkout_name());
        }
    }
}
