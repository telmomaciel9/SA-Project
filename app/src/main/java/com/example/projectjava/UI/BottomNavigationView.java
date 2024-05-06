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
    public BottomNavigationView(Context context) {
        super(context);
        this.context = context;
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
                context.startActivity(new Intent(context, BeginningActivity.class));
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
