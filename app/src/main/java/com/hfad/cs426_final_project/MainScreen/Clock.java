package com.hfad.cs426_final_project.MainScreen;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.util.Locale;

public class Clock {
    //Number of seconds displayed on the stopwatch.
    private int seconds;
    //Is the stopwatch running?
    private boolean running;
    private boolean clockState; // 0 for stopwatch, 1 for timer
    private final Handler handler;
    private TextView timeView;
    private int timeLimit;
    private MyButton startButton;
    private Context context;

    public Clock(Context context, TextView timeView, MyButton startButton, boolean clockState, int initialTime, int timeLimit) {
        this.context = context;
        this.timeView = timeView;
        this.startButton = startButton;
        this.clockState = clockState;
        this.seconds = (!this.clockState) ? initialTime : timeLimit ;
        this.timeLimit = timeLimit;
        this.handler = new Handler();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
        startButton.setText("Pause");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.secondary_50));
    }

    public void stop() {
        running = false;
        startButton.setText("Plant");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.primary_20));
    }

    public void reset() {
        seconds = (!clockState ? 0 : timeLimit);
    }

    public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;

                // Ensure that the maximum time is 120:00 (7200 seconds)
                if (minutes > 120) {
                    minutes = 120;
                    secs = 0;
                }

                String time = String.format(Locale.getDefault(),
                        "%02d:%02d", minutes, secs);
                timeView.setText(time);

                if (running) {
                    if (!clockState) {
                        seconds++;
                        if (timeLimit > 0 && seconds > timeLimit) {
                            stop();
                            // handle limit reached (e.g., navigate to CongratulationScreen)
                            Intent intent = new Intent(context, CongratulationScreenActivity.class);
                            context.startActivity(intent);
                            reset();
                        }
                    } else {
                        seconds--;
                        if (seconds < 0) {
                            stop();
                            // handle timer end (e.g., navigate to CongratulationScreen)
                            Intent intent = new Intent(context, CongratulationScreenActivity.class);
                            context.startActivity(intent);
                            reset();
                        }
                    }
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

}
