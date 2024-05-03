package com.example.projectjava.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;

import java.util.ArrayList;
import java.util.List;

public class PremadeExerciseListFragment extends Fragment{
    private DatabaseHelper db;
    private RecyclerView rvPremadeExercises;
    private PremadeExercisesAdapter adapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premade_workout_exercises, container, false);

        db = DatabaseHelper.getInstance();
        rvPremadeExercises = view.findViewById(R.id.rvPremadeExercises);
        rvPremadeExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        List<PremadeExercise> premade_exercises = db.getPremadeExerciseList();
        if(premade_exercises == null){
            premade_exercises = new ArrayList<>();
        }
        adapter = new PremadeExercisesAdapter(premade_exercises);
        rvPremadeExercises.setAdapter(adapter);
        return view;
    }

    public void resetList(){
        this.adapter.updateData(new ArrayList<>());
    }
    public int nrPremadeExercises(){return this.adapter.getItemCount();}
}
