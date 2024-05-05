package com.example.projectjava.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.projectjava.R;

public class BottomNavigationView extends LinearLayout {
    private Context context;
    private boolean home_warning;
    public BottomNavigationView(Context context, boolean home_warning) {
        super(context);
        this.context = context;
        this.home_warning = home_warning;
        init();
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bottom_navigation_view, this, true);

        view.findViewById(R.id.ibHome).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_warning){
                    new AlertDialog.Builder(context)
                            .setTitle("Confirm")
                            .setMessage("If you go to the home page you won't be able to resume the workout! Proceed?")
                            .setPositiveButton("Yes", (dialog, id) -> {context.startActivity(new Intent(context, BeginningActivity.class));})
                            .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                            .show();
                }else{
                    context.startActivity(new Intent(context, BeginningActivity.class));
                }
            }
        });

        view.findViewById(R.id.ibProfile).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProfileActivity.class));
            }
        });
    }
}
