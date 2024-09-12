package com.hfad.cs426_final_project.PlantingScreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.MainScreen.BottomSheet.OwnBlockAdapter;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class PlantingScreenActivity extends BaseScreenActivity {
    private HexagonalLandView hexagonalLandView;
    private ClickableImageView zoomInButton, zoomOutButton, zoomResetButton;
    private ImageView gardenModeBtn;
    private boolean isPlantingMode = false;

    private RecyclerView rcvLandSelection;
    private List<BlockData> blockDataList;
    private BlockData curBlockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockDataList = AppContext.getInstance().getCurrentUser().getOwnBlock();
        initializeComponents();
        initRCVLandSelection();
    }

    private void initializeComponents() {
        gardenModeBtn = findViewById(R.id.garden_mode);
        hexagonalLandView = findViewById(R.id.hexagonalLandView);
        rcvLandSelection = findViewById(R.id.land_selection);

        zoomInButton = findViewById(R.id.zoom_in_btn);
        zoomOutButton = findViewById(R.id.zoom_out_btn);
        zoomResetButton = findViewById(R.id.zoom_reset_btn);

        gardenModeBtn.setOnClickListener(v -> toggleGardenMode());

        zoomInButton.setOnClickListener(v -> hexagonalLandView.zoomIn());
        zoomOutButton.setOnClickListener(v -> hexagonalLandView.zoomOut());
        zoomResetButton.setOnClickListener(v -> hexagonalLandView.resetZoom());
    }

    private void initRCVLandSelection() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvLandSelection.setLayoutManager(layoutManager);

        PlantingBlockAdapter plantingBlockAdapter = new PlantingBlockAdapter(blockDataList, new PlantingBlockAdapter.IClickPlantingBlockListener() {
            @Override
            public void onClickPlantingBlock(BlockData blockData) {
                curBlockData = blockData;
            }
        });
        rcvLandSelection.setAdapter(plantingBlockAdapter);
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