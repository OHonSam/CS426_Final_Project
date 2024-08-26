package com.hfad.cs426_final_project.MainScreen;

import android.os.Handler;
import android.widget.TextView;

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

    public Clock(TextView timeView, boolean clockState, int initialTime, int timeLimit) {
        this.timeView = timeView;
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
    }

    public void stop() {
        running = false;
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
                        if (timeLimit > 0 && seconds >= timeLimit) {
                            stop();
                            // handle limit reached (e.g., navigate to CongratulationScreen)
                        }
                    } else {
                        if (seconds > 0) {
                            seconds--;
                        }
                        if (seconds <= 0) {
                            stop();
                            // handle timer end (e.g., navigate to CongratulationScreen)
                        }
                    }
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

}
