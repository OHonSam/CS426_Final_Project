package com.hfad.cs426_final_project.StatisticScreen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.R;

public class StatisticScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_screen);
        final IsometricLandView landView = findViewById(R.id.isometricLandView);
        landView.post(new Runnable() {
            @Override
            public void run() {
                landView.addTree(0, 0);
                landView.addTree(0, 1);
                landView.addTree(1, 0);

            }
        });
    }
}