package com.example.projectjava.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectjava.R;
import com.example.projectjava.data.DatabaseHelper;
import com.example.projectjava.data.PremadeExercise;
import com.example.projectjava.data.premadeExercises.PremadeBenchExercise;
import com.example.projectjava.data.premadeExercises.PremadeOverheadPressExercise;
import com.example.projectjava.data.premadeExercises.PremadeRunningExercise;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddPremadeExerciseFragment extends Fragment {
    private DatabaseHelper db;
    private LinearLayout linearContainer;
    private Spinner spinner;
    private EditText editTextAttribute1;
    private EditText editTextAttribute2;
    private Button btnAddPremadeExercise;
    private String exerciseType;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_premade_exercise, container, false);

        db = DatabaseHelper.getInstance();

        linearContainer = view.findViewById(R.id.linearContainer);
        spinner = view.findViewById(R.id.spinnerExerciseType);

        editTextAttribute1 = view.findViewById(R.id.editTextAttribute1);
        editTextAttribute2 = view.findViewById(R.id.editTextAttribute2);

        btnAddPremadeExercise = view.findViewById(R.id.btnAddPremadeExercise);

        setUpSpinner();
        setUpButton();
        return view;
    }

    public void setUpSpinner(){
        List<String> items = Stream.of("Bench Press", "Overhead Press", "Running").collect(Collectors.toList());
        // Create an ArrayAdapter to populate the spinner with the items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                exerciseType = (String) adapterView.getItemAtPosition(i);
                switch (exerciseType){
                    case "Bench Press":
                    case "Overhead Press":
                        editTextAttribute1.setHint("Repetitions");
                        editTextAttribute2.setHint("Sets");
                        break;
                    case "Running":
                        editTextAttribute1.setHint("Distance");
                        editTextAttribute2.setHint("Average Velocity");
                        break;
                    default:
                        editTextAttribute1.setHint("Invalid ex type.");
                        editTextAttribute2.setHint("Invalid ex type.");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing!
            }
        });
    }

    public void setUpButton(){
        btnAddPremadeExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_attr1 = editTextAttribute1.getText().toString();
                String s_attr2 = editTextAttribute2.getText().toString();
                float attr1; float attr2;

                try {
                    attr1 = Float.parseFloat(s_attr1);
                    attr2 = Float.parseFloat(s_attr2);

                    PremadeExercise pe = null;
                    switch (exerciseType){
                        case "Bench Press":
                            pe = new PremadeBenchExercise((int) attr1,(int) attr2);
                            break;
                        case "Overhead Press":
                            pe = new PremadeOverheadPressExercise((int) attr1,(int) attr2);
                            break;
                        case "Running":
                            pe = new PremadeRunningExercise(attr1, attr2);
                            break;
                        default:
                            Toast.makeText(getContext(), "Invalid exercise type!",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                    db.addPremadeExercise(pe);
                    Toast.makeText(getContext(), "Exercise added successfully!",
                            Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid inputs!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
