package com.hfad.cs426_final_project.MainScreen.DoneSessionActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.R;

public class BreakScreenActivity extends AppCompatActivity {

    public static final String TIME_BREAK = "TIME_BREAK";
    public static final String AUTO_START = "AUTO_START";
    private Clock clock;
    private AppContext appContext;
    private Bundle extras;

    private ClickableImageView backButton;
    private Button cancelButton, focusButton;
    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_screen);
        init();
        setupOnClickListener();
        startBreak();
        stopBreak();
    }

    private void setupOnClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        focusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.disableBreakSessionCount();
                focusAgain();
            }
        });
    }

    private void stopBreak() {
        // Set the listener to get notified when the break session completes
        clock.setOnBreakSessionCompleteListener(new Clock.OnBreakSessionCompleteListener() {
            @Override
            public void onBreakSessionComplete() {
                boolean isAutoStartEnable = extras.getBoolean(AUTO_START);
                if(isAutoStartEnable){
                    clock.disableBreakSessionCount();
                    focusAgain();
                }
                else{
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    private void startBreak() {
        int breakTime = extras.getInt(TIME_BREAK);
        Log.d("BreakScreenActivity",""+breakTime);
        clock.setSeconds(breakTime);
        clock.enableBreakSessionCount();
    }

    private void init(){
        appContext = AppContext.getInstance();
        clock = appContext.getCurrentClock();
        extras = getIntent().getExtras();
        timeView = findViewById(R.id.time_view);
        backButton = findViewById(R.id.back_button);
        clock.setTimeView(timeView);
        cancelButton = findViewById(R.id.buttonCancel);
        focusButton = findViewById(R.id.buttonFocus);
    }

    private void focusAgain(){
        setResult(RESULT_OK);
        finish();
    }
}