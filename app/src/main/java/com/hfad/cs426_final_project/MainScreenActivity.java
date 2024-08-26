package com.hfad.cs426_final_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
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

    TextView timeView;
    MyButton startButton, todoButton, musicButton;
    ClickableImageView dropdownMenu, timer, stopwatch, todoImage, musicImage;
    LinearLayout todoContainer, musicContainer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getUIReferences();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dropdownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        stopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        View.OnTouchListener musicTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isPressed = (event.getAction() == MotionEvent.ACTION_DOWN);

                // Handle pressed state for musicImage if it's the musicButton or musicImage
                musicImage.setPressed(isPressed);
                musicButton.onTouchEvent(event); // Pass the event to musicButton

                return true; // Indicate the touch was handled
            }
        };

        // Set the listener to the views
        musicContainer.setOnTouchListener(musicTouchListener);
        musicButton.setOnTouchListener(musicTouchListener);
        musicImage.setOnTouchListener(musicTouchListener);

        View.OnTouchListener todoTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isPressed = (event.getAction() == MotionEvent.ACTION_DOWN);

                // Handle pressed state for musicImage if it's the musicButton or musicImage
                todoImage.setPressed(isPressed);
                todoButton.onTouchEvent(event); // Pass the event to musicButton

                return true; // Indicate the touch was handled
            }
        };

        // Set the listener to the views
        todoContainer.setOnTouchListener(todoTouchListener);
        todoButton.setOnTouchListener(todoTouchListener);
        todoImage.setOnTouchListener(todoTouchListener);

        runTimer();
    }

    private void getUIReferences() {
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);
        dropdownMenu = findViewById(R.id.dropdown_menu);
        todoImage = findViewById(R.id.todo_image);
        todoButton = findViewById(R.id.todo_button);
        stopwatch = findViewById(R.id.stopwatch);
        timer = findViewById(R.id.timer);
        musicContainer = findViewById(R.id.music_container);
        todoContainer = findViewById(R.id.to_do_container);
        musicImage =findViewById(R.id.music_image);
        musicButton=findViewById(R.id.music_button);
    }

    private void runTimer() {
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
                // 1 second = 1000 milliseconds
                handler.postDelayed(this, 1000);
            }
        });
    }


}