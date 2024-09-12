package com.hfad.cs426_final_project.PlantingScreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.res.ResourcesCompat;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.R;

public class PlantingScreenActivity extends BaseScreenActivity {
    private HexagonalLandView hexagonalLandView;
    private ClickableImageView zoomInButton, zoomOutButton, zoomResetButton;
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

        zoomInButton = findViewById(R.id.zoom_in_btn);
        zoomOutButton = findViewById(R.id.zoom_out_btn);
        zoomResetButton = findViewById(R.id.zoom_reset_btn);

        gardenModeBtn.setOnClickListener(v -> toggleGardenMode());

        zoomInButton.setOnClickListener(v -> hexagonalLandView.zoomIn());
        zoomOutButton.setOnClickListener(v -> hexagonalLandView.zoomOut());
        zoomResetButton.setOnClickListener(v -> hexagonalLandView.resetZoom());
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