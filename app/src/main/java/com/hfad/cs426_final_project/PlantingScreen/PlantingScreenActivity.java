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
        hexagonalLandView = findViewById(R.id.hexagonalLandView);

        gardenModeBtn.setOnClickListener(v -> toggleGardenMode());
    }

    private void toggleGardenMode() {
        isPlantingMode = !isPlantingMode;
        if (isPlantingMode) {
            gardenModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.plant_mode, null));
            hexagonalLandView.setPlantingMode(true);
        } else {
            gardenModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.watch_garden_mode, null));
            hexagonalLandView.setPlantingMode(false);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_planting_screen;
    }
}