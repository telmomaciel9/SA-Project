package com.example.projectjava.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.PremadeExercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PremadeExercisesAdapter extends RecyclerView.Adapter<PremadeExercisesAdapter.PremadeExercisesViewHolder>{
    private List<PremadeExercise> exercises;

    public PremadeExercisesAdapter(List<PremadeExercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public PremadeExercisesAdapter.PremadeExercisesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premade_exercise_item, parent, false);
        return new PremadeExercisesAdapter.PremadeExercisesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PremadeExercisesAdapter.PremadeExercisesViewHolder holder, int position) {
        PremadeExercise pe = (PremadeExercise) exercises.get(position);
        holder.bind(pe);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateData(ArrayList exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    static class PremadeExercisesViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;
        TextView tvPremadeMetrics;
        CardView cardView;

        PremadeExercisesViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvPremadeMetrics = itemView.findViewById(R.id.tvPremadeMetrics);
            cardView = itemView.findViewById(R.id.main_container);
        }

        void bind(PremadeExercise pe) {
            tvExerciseName.setText(pe.getExerciseName());
            Map<String, Float> values = pe.getMetrics();
            String metrics = "";
            for (Map.Entry<String, Float> entry: values.entrySet()){
                metrics += entry.getKey() + ": " + entry.getValue() + "\n";
            }
            tvPremadeMetrics.setText(metrics);
        }
    }
}
