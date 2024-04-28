package com.example.projectjava.UI;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/*
*  Esta classe implementa a funcionalidade de Timer que é usada em diferentes activities!
 */

public class Timer {
    private boolean isRecording;
    private Handler handler;
    private Runnable runnable;
    private long startTime;

    public Timer(){
        this.isRecording = false;
        handler = new Handler(Looper.getMainLooper());
    }

    public void startTimer(TextView timerTextView){
        this.isRecording = true;
        startTime = System.currentTimeMillis();
        runnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;

                String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);

                timerTextView.setText(timeFormatted);

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    public void stopTimer(){
        this.isRecording = false;
        handler.removeCallbacks(runnable);
    }

    public boolean isRecording(){
        return isRecording;
    }
}
