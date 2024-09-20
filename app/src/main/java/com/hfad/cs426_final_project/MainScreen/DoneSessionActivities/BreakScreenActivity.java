package com.hfad.cs426_final_project.MainScreen.DoneSessionActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.MainScreen.Tag.TagAdapterSpinner;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class BreakScreenActivity extends AppCompatActivity {
    public static final String TIME_BREAK = "TIME_BREAK";
    public static final String AUTO_START = "AUTO_START";
    private Clock clock;
    private AppContext appContext;
    private Bundle extras;

    private Spinner searchTagSpinner;
    private ClickableImageView backButton;
    private Button cancelButton, focusButton;
    private TextView timeView, tvSunDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_screen);
        appContext = AppContext.getInstance();
        clock = appContext.getCurrentClock();
        clock.disableDeepModeCount();
        getUIReferences();
        setupTextDisplay();
        setupOnClickListener();
        setupSearchTag();
        startBreak();
        stopBreak();
    }

    private void setupTextDisplay() {
        tvSunDisplay.setText(String.valueOf(AppContext.getInstance().getCurrentUser().getSun()));
    }

    private void setupOnClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBreakSession();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBreakSession();
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
                clock.disableBreakSessionCount();
                if (isAutoStartEnable){
                    focusAgain();
                }
                else {
                    setResult(RESULT_CANCELED);
                    finishBreakSession();
                }
            }
        });
    }

    private void startBreak() {
        int breakTime = extras.getInt(TIME_BREAK);
        clock.setSeconds(breakTime);
        clock.enableBreakSessionCount();
    }

    private void getUIReferences(){
        extras = getIntent().getExtras();
        timeView = findViewById(R.id.break_time_view);
        backButton = findViewById(R.id.back_button);
        clock.setTimeView(timeView);
        cancelButton = findViewById(R.id.buttonCancel);
        focusButton = findViewById(R.id.buttonFocus);
        searchTagSpinner = findViewById(R.id.search_tag_spinner);
        tvSunDisplay = findViewById(R.id.tvSunDisplay);
    }

    private void focusAgain(){
        setResult(RESULT_OK);
        finishBreakSession();
    }

    private void finishBreakSession() {
        clock.disableBreakSessionCount();
        clock.reset();
        finish();
    }

    private void setupSearchTag() {
        List<Tag> tagList = appContext.getCurrentUser().getOwnTags();
        if (tagList.isEmpty()) {
            tagList.add(new Tag());
        }
        TagAdapterSpinner tagAdapterSpinner = new TagAdapterSpinner(this, R.layout.item_tag_selected, tagList);
        searchTagSpinner.setEnabled(false);
        searchTagSpinner.setClickable(false);
        searchTagSpinner.setAdapter(tagAdapterSpinner);
        updateTagDisplay();
    }

    private void updateTagDisplay() {
        TagAdapterSpinner tagAdapterSpinner = (TagAdapterSpinner) searchTagSpinner.getAdapter();
        int position = tagAdapterSpinner.getPosition(appContext.getCurrentUser().getFocusTag());
        searchTagSpinner.setSelection(position);
    }
}