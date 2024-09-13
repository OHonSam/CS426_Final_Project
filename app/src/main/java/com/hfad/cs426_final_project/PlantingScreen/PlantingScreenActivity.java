package com.hfad.cs426_final_project.PlantingScreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class PlantingScreenActivity extends BaseScreenActivity implements HexagonalLandView.OnBlockRestoredListener {
    private HexagonalLandView hexagonalLandView;
    private ClickableImageView zoomInButton, zoomOutButton, zoomResetButton, resetGardenButton;
    private ImageView gardenModeBtn;
    private RecyclerView rcvLandSelection;
    private List<BlockData> blockDataList;
    private PlantingBlockAdapter plantingBlockAdapter;
    private boolean isPlantingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockDataList = AppContext.getInstance().getCurrentUser().getOwnBlock();
        initializeComponents();
        setupRecyclerView();
        setupListeners();
    }

    private void initializeComponents() {
        gardenModeBtn = findViewById(R.id.garden_mode);
        hexagonalLandView = findViewById(R.id.hexagonalLandView);
        rcvLandSelection = findViewById(R.id.land_selection);
        zoomInButton = findViewById(R.id.zoom_in_btn);
        zoomOutButton = findViewById(R.id.zoom_out_btn);
        zoomResetButton = findViewById(R.id.zoom_reset_btn);
        resetGardenButton = findViewById(R.id.reset_garden_btn);
        hexagonalLandView.setCurrentUser(AppContext.getInstance().getCurrentUser());
    }

    private void setupRecyclerView() {
        rcvLandSelection.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        plantingBlockAdapter = new PlantingBlockAdapter(blockDataList, this::onBlockSelected);
        rcvLandSelection.setAdapter(plantingBlockAdapter);

        if (!blockDataList.isEmpty()) {
            hexagonalLandView.setSelectedBlockData(blockDataList.get(0));
        }
    }

    private void setupListeners() {
        gardenModeBtn.setOnClickListener(v -> toggleGardenMode());
        zoomInButton.setOnClickListener(v -> hexagonalLandView.zoomIn());
        zoomOutButton.setOnClickListener(v -> hexagonalLandView.zoomOut());
        zoomResetButton.setOnClickListener(v -> hexagonalLandView.resetZoom());
        resetGardenButton.setOnClickListener(v -> showResetGardenDialog());

        hexagonalLandView.setOnBlockUsedListener(this::updateBlockQuantity);
        hexagonalLandView.setOnBlockRestoredListener(this);
    }

    private void onBlockSelected(BlockData blockData) {
        hexagonalLandView.setSelectedBlockData(blockData);
    }

    @Override
    public void onBlockRestored(BlockData restoredBlockData) {
        for (BlockData blockData : blockDataList) {
            if (blockData.getBlock().getId() == restoredBlockData.getBlock().getId()) {
                blockData.setQuantity(blockData.getQuantity() + 1);
                int position = blockDataList.indexOf(blockData);
                plantingBlockAdapter.notifyItemChanged(position);
                break;
            }
        }
    }

    private void updateBlockQuantity(BlockData blockData) {
        int position = blockDataList.indexOf(blockData);
        if (position != -1) {
            plantingBlockAdapter.notifyItemChanged(position);
        }

        if (blockData.getQuantity() == 0) {
            selectNextAvailableBlock();
        }
    }

    private void selectNextAvailableBlock() {
        for (BlockData blockData : blockDataList) {
            if (blockData.getQuantity() > 0) {
                hexagonalLandView.setSelectedBlockData(blockData);
                int position = blockDataList.indexOf(blockData);
                plantingBlockAdapter.selectPosition(position);
                break;
            }
        }
    }

    private void toggleGardenMode() {
        isPlantingMode = !isPlantingMode;
        int drawableRes = isPlantingMode ? R.drawable.plant_mode : R.drawable.watch_garden_mode;

        if (isPlantingMode) {
            resetGardenButton.setVisibility(View.VISIBLE);
        } else {
            resetGardenButton.setVisibility(View.GONE);
        }

        gardenModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableRes, null));
        hexagonalLandView.setPlantingMode(isPlantingMode);
    }

    private void showResetGardenDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Garden")
                .setMessage("Are you sure you want to reset your garden? This action cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGarden();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void resetGarden() {
        hexagonalLandView.resetGarden();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_planting_screen;
    }

    @Override
    protected void onPause() {
        super.onPause();
        hexagonalLandView.saveLandState();
    }

    @Override
    protected void onDestroy() {
        hexagonalLandView.saveLandState();
        super.onDestroy();
    }
}