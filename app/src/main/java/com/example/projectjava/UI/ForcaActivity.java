package com.example.projectjava.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectjava.R;
import com.example.projectjava.UI.bench.BenchActivity;
import com.example.projectjava.UI.overheadPress.OverheadPressActivity;

public class ForcaActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forca);

        bottomNavigationView = new BottomNavigationView(this);

        findViewById(R.id.btnBench).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForcaActivity.this, BenchActivity.class));
            }
        });

        findViewById(R.id.btnOverheadPress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForcaActivity.this, OverheadPressActivity.class));
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
