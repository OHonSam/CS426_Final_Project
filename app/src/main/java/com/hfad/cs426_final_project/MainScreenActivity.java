package com.hfad.cs426_final_project;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

import java.util.Locale;

public class MainScreenActivity extends AppCompatActivity {
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running;

    TextView timeView;
    MyButton startButton;
    MyButton clockMode;
    View clockModePopupView;
    ClockModePopupWindow clockModePopupWindow;
    ImageView clockModeContainer;
    ModePickerDialog modePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        getUIReferences();

        setupClockMode();

        runTimer();

    }

    private void setupClockMode() {
        clockMode = findViewById(R.id.clockMode);
        clockMode.setOnClickListener(v -> {
            //showClockModePopupWindow();
            showModePickerDialog();
        });
    }

    private void showModePickerDialog() {
        modePickerDialog.show(getSupportFragmentManager(),ModePickerDialog.TAG);
    }

    private void showClockModePopupWindow() {
        View parent = findViewById(R.id.main);
        clockModePopupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
//        int[] absolute_coordinate = new int[2];
//        clockMode.getLocationOnScreen(absolute_coordinate);
//        int x_offset = absolute_coordinate[0];
//        int y_offset = absolute_coordinate[1];
//        Log.d("MyButton ClockMode",  "x: " + x_offset + " y: " + y_offset);
        clockModePopupWindow.setOnDismissListener(() -> {
            clockModePopupWindow.dismiss();
        });
    }

    private void getUIReferences() {
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);
        startButton.setOnClickListener(v -> {
        });

        //clockModePopupView = View.inflate(this, R.layout.popup_clock_mode, null);
        //clockModePopupWindow = new ClockModePopupWindow(clockModePopupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true,this);
        modePickerDialog = new ModePickerDialog();
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

//    public void replaceFragmentInPopup(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.menuOption, fragment);
//        transaction.commit();
//    }

}