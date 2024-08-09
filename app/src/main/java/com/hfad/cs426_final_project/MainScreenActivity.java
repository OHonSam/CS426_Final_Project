package com.hfad.cs426_final_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

import java.util.Locale;
import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity {
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running;

    TextView timeView;
    MyButton startButton, todoButton, musicButton;
    ClickableImageView dropdownMenu, timer, stopwatch, todo, music ;
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

        todoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug","todoContainer");
                todo.setPressed(true);

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

        musicContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                // Pass the MotionEvent to the MyButton

                return true; // Return true to indicate the touch was handled
            }
        });

        musicButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                musicButton.onTouchEvent(event);
                boolean isPressed = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isPressed = true;
                }
                music.setPressed(isPressed);
                return true;
            }
        });

        music.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                musicButton.onTouchEvent(event);
                boolean isPressed = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isPressed = true;
                }
                music.setPressed(isPressed);
                return true;
            }
        });

        todoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                todoButton.onTouchEvent(event);
                boolean isPressed = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isPressed = true;
                }
                todo.setPressed(isPressed);
                return true;
            }
        });

        todo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                todoButton.onTouchEvent(event);
                boolean isPressed = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isPressed = true;
                }
                todo.setPressed(isPressed);
                return true;
            }
        });



        runTimer();
    }

    private void getUIReferences() {
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);
        dropdownMenu = findViewById(R.id.dropdown_menu);
        todo = findViewById(R.id.todo_image);
        todoButton = findViewById(R.id.todo_button);
        stopwatch = findViewById(R.id.stopwatch);
        timer = findViewById(R.id.timer);
        musicContainer = findViewById(R.id.music_container);
        todoContainer = findViewById(R.id.to_do_container);
        music=findViewById(R.id.music_image);
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