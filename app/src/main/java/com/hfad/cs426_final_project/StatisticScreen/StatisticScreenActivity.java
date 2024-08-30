package com.hfad.cs426_final_project.StatisticScreen;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

public class StatisticScreenActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroup;
    private IsometricLandView landView;
    private ClickableImageView escapeBtn, shareBtn;
    private MyButton overviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_screen);
        initializeComponents();
    }

    private void initializeComponents() {
        // Initialize escape button
        escapeBtn = findViewById(R.id.escape_btn);
        escapeBtn.setOnClickListener(v -> {
        });

        overviewBtn = findViewById(R.id.overview_btn);
        overviewBtn.setOnClickListener(v -> {
        });

        shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(v -> {
        });

        initializeToggleGroup();

        landView = findViewById(R.id.isometricLandView);
        initializeIsometricLandView();
    }

    private void initializeIsometricLandView() {
        landView.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 6; j++) {
                        if ((i + j) % 2 == 0) {
                            landView.addTree(i, j);
                        }
                    }
                }
            }
        });
    }

    private void initializeToggleGroup() {
        toggleGroup = findViewById(R.id.time_picker);

        // Set initial selection
        toggleGroup.check(R.id.day_btn);

        // Handle button clicks
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
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
            }
        });
    }

    private void handleSelection(String period) {
    }
}