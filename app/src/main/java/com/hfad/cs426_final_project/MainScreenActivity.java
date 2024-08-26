package com.hfad.cs426_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

import java.util.Locale;

public class MainScreenActivity extends AppCompatActivity {
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running;
    private int clockState;

    TextView timeView;
    MyButton startButton;
    ClickableImageView dropdownMenu, timer, stopwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        getUIReferences();

        dropdownMenu = findViewById(R.id.dropdown_menu);
        dropdownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupClockMode();
        setupStartButton();

        runClock();
    }

    private void getUIReferences() {
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);

        timer = findViewById(R.id.timer);
        stopwatch = findViewById(R.id.stopwatch);
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);

                // Decrement the seconds only if running and seconds are greater than 0
                if (running && seconds > 0) {
                    seconds--;
                }

                // If the timer reaches 0, move to CongratulationScreen Activity
                if (seconds == 0 && running) {
                    // Stop the timer
                    running = false;

                    // Navigate to CongratulationScreen Activity
                    // Intent intent = new Intent(this, CongratulationScreen.class);
                    // startActivity(intent);
                    // finish();
                }

                // Schedule the next update in 1 second
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void runStopwatch() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if (running) {
                    seconds++;
                }

                // If the stopwatch time reached time limit, move to CongratulationScreen Activity
//                if (seconds >= timeLimit && running) {
//                    running = false; // Stop the stopwatch
//
//                    // Navigate to CongratulationScreen Activity
//                    Intent intent = new Intent(MainScreenActivity.this, CongratulationScreen.class);
//                    startActivity(intent);
//                    finish(); // Finish current activity
//                }
                // 1 second = 1000 milliseconds
                handler.postDelayed(this, 1000);
            }


        });
    }

    private void setupClockMode() {
        clockState = 0;
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the stopwatch is not running then start it
                if (!running) {
                    running = true;
                }
                else {
                    running = false;
                }
            }
        });

    }

    private void runClock() {
        if (clockState == 1) {
            runTimer();
        }
        else {
            runStopwatch();
        }
    }

}