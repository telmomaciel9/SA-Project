package com.example.projectjava.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.running.RunActivity;

public class CardioActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        bottomNavigationView = new BottomNavigationView(this);

        findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CardioActivity.this, RunActivity.class));
            }
        });

        // set the behaviour for when the back button is pressed
        findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // close current activity and return to previous state
            }
        });
    }
}

