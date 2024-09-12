package com.hfad.cs426_final_project.PlantingScreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.res.ResourcesCompat;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.R;

public class PlantingScreenActivity extends BaseScreenActivity {
    private HexagonalLandView hexagonalLandView;
    private ImageView gardenModeBtn;
    private boolean isPlantingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
    }

    private void initializeComponents() {
        gardenModeBtn = findViewById(R.id.garden_mode);

        gardenModeBtn.setOnClickListener(v -> {
            isPlantingMode = !isPlantingMode;
            if (isPlantingMode) {
                gardenModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.watch_garden_mode, null));
            } else {
                gardenModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.plant_mode, null));
            }
        });

        hexagonalLandView = findViewById(R.id.hexagonalLandView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_planting_screen;
    }

    protected void onPause() {
        Log.d("PlantingScreenActivity", "onPause: Saving tiles");
        super.onPause();
        if (hexagonalLandView != null) {

            hexagonalLandView.saveAllTiles();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("PlantingScreenActivity", "onDestroy: Saving tiles");
        super.onDestroy();
        if (hexagonalLandView != null) {

            hexagonalLandView.saveAllTiles();
        }
    }
}