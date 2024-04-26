package com.example.projectjava;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.database.DatabaseHelper;

public class LogWorkoutActivity extends AppCompatActivity {
    private EditText editTextDate, editTextType, editTextNotes;
    private Button btnSaveWorkout;
    private DatabaseHelper databaseHelper; // need to create this class to handle database operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_workout);

        editTextDate = findViewById(R.id.editTextDate);
        editTextType = findViewById(R.id.editTextType);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);

        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });
    }

    private void saveWorkout() {
        String date = editTextDate.getText().toString();
        String type = editTextType.getText().toString();
        String notes = editTextNotes.getText().toString();

        if (date.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database
        /*
        long id = databaseHelper.addWorkout(date, type, notes);

        if (id != -1) {
            Toast.makeText(this, "Workout saved", Toast.LENGTH_SHORT).show();
            finish(); // Close activity and go back
        } else {
            Toast.makeText(this, "Failed to save workout", Toast.LENGTH_SHORT).show();
        }
        */
    }
}
