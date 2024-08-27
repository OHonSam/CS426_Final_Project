package com.hfad.cs426_final_project;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
    ClickableImageView todoImage, musicImage, progressBarButton;
    ClickableImageView dropdownMenu, timer, stopwatch, todo, music ;
    LinearLayout todoContainer, musicContainer;
    ProgressBar progressBar;
    ConstraintLayout popupMusicContainer;

    private long currentTimeMillis = 0;
    private final long MAX_TIME_MILLIS = 7200000;
    private long gapTimeEachDrag = 300000;

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

        handleAdjustTime();
        setupMusicListener();
        setupTodoListener();
    }

        dropdownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    @SuppressLint("ClickableViewAccessibility")
    private void handleAdjustTime() {
        progressBar = findViewById(R.id.progress_bar);
        timeView = findViewById(R.id.time_view);
        progressBarButton = findViewById(R.id.progress_bar_button);
        updateProgressBar();
        updateTimeDisplay();

        progressBarButton.setOnTouchListener(new View.OnTouchListener() {
            private float startAngle;
            private long startTime;
            private long lastValidTime;
            private float lastAngle;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startAngle = getAngle(event.getRawX(), event.getRawY());
                        startTime = currentTimeMillis;
                        lastValidTime = startTime;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float currentAngle = getAngle(event.getRawX(), event.getRawY());
                        Log.d("debug", "Current angle: " + currentAngle + ", " + startAngle);
                        float angleDiff = getAngleDifference(currentAngle, startAngle);
                        if (abs(angleDiff) < 10) {
                            return true;
                        }
                        long timeDiff = (long) (angleDiff / 360 * MAX_TIME_MILLIS);

                        currentTimeMillis += timeDiff;
                        currentTimeMillis = Math.min(Math.max(currentTimeMillis, 0), MAX_TIME_MILLIS);

                        long roundedTime = (currentTimeMillis / gapTimeEachDrag) * gapTimeEachDrag;

                        if (roundedTime != lastValidTime) {
                            currentTimeMillis = roundedTime;
                            updateTimeDisplay();
                            updateProgressBar();
                            lastValidTime = currentTimeMillis;
                        }
                        return true;
                }
                return false;
            }

            private float getAngleDifference(float angle1, float angle2) {
                float diff = angle1 - angle2;

                if (angle1 > angle2) {
                    if (diff < 0) {
                        diff += 360;
                    }
                } else {
                    if (diff > 0) {
                        diff -= 360;
                    }
                }
                return diff;
            }
        });
    }

    private void updateButtonPosition(float angle) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) progressBarButton.getLayoutParams();
        layoutParams.circleAngle = angle;
        progressBarButton.setLayoutParams(layoutParams);
    }


    private float getAngle(float x, float y) {
        float xCoordinate = (float) (x - 336.0);
        float yCoordinate = (float) (y - 603.0);
        float angle = (float) Math.toDegrees(Math.atan2(yCoordinate, xCoordinate));
        return angle;
    }

    private void updateTimeDisplay() {
        long hours = currentTimeMillis / 3600000;
        long minutes = (currentTimeMillis % 3600000) / 60000;
        long seconds = (currentTimeMillis % 60000) / 1000;
        String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timeView.setText(timeString);
    }

    private void updateProgressBar() {
        int progress = (int) ((currentTimeMillis * 100) / MAX_TIME_MILLIS);
        progressBar.setProgress(progress);

        float angle = (float) (progress * 360.0 / progressBar.getMax());
        updateButtonPosition(angle);
    }


    private void showMusicSelectionPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_music_selection, null);

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