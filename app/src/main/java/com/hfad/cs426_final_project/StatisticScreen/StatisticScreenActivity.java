package com.hfad.cs426_final_project.StatisticScreen;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

public class StatisticScreenActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroup;
    private IsometricLandView landView;
    private ClickableImageView escapeBtn, shareBtn, backBtn, forwardBtn;
    private MyButton overviewBtn;
    private TextView timeSelectionText;
    private TimeManager timeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_screen);
        initializeComponents();
    }

    private void initializeComponents() {
        escapeBtn = findViewById(R.id.escape_btn);
        escapeBtn.setOnClickListener(v -> {
        });

        overviewBtn = findViewById(R.id.overview_btn);
        overviewBtn.setOnClickListener(v -> {
        });

        shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(v -> {
        });

        timeSelectionText = findViewById(R.id.time_selection_text);
        backBtn = findViewById(R.id.back_btn);
        forwardBtn = findViewById(R.id.forward_btn);

        timeManager = new TimeManager();
        initializeToggleGroup();
        initializeTimeNavigation();

        landView = findViewById(R.id.isometricLandView);
        initializeIsometricLandView();

        updateTimeSelection();
    }

    private void initializeToggleGroup() {
        toggleGroup = findViewById(R.id.time_picker);

        toggleGroup.check(R.id.day_btn);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.day_btn) {
                    handleSelection("Day");
                } else if (checkedId == R.id.week_btn) {
                    handleSelection("Week");
                } else if (checkedId == R.id.month_btn) {
                    handleSelection("Month");
                } else if (checkedId == R.id.year_btn) {
                    handleSelection("Year");
                }
            }
        });
    }

    private void initializeTimeNavigation() {
        backBtn.setOnClickListener(v -> navigateTime(-1));
        forwardBtn.setOnClickListener(v -> navigateTime(1));
    }

    private void handleSelection(String period) {
        timeManager.handleSelection(period);
        updateTimeSelection();
    }

    private void navigateTime(int direction) {
        timeManager.navigateTime(direction);
        updateTimeSelection();
    }

    private void updateTimeSelection() {
        timeSelectionText.setText(timeManager.getFormattedTimeSelection());
        forwardBtn.setVisibility(timeManager.isLatestTime() ? View.INVISIBLE : View.VISIBLE);
    }

    private void initializeIsometricLandView() {
        landView.post(() -> {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if ((i + j) % 2 == 0) {
                        landView.addTree(i, j);
                    }
                }
            }
        });
    }
}