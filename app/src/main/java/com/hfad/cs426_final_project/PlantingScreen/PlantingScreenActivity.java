package com.hfad.cs426_final_project.PlantingScreen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private PlantingBlockAdapter plantingBlockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockDataList = AppContext.getInstance().getCurrentUser().getOwnBlock();
        // curBlockData.getBlock().getImgUri() URI to bitmap ?
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

        hexagonalLandView.setOnBlockUsedListener(new HexagonalLandView.OnBlockUsedListener() {
            @Override
            public void onBlockUsed(BlockData blockData) {
                updateBlockQuantity(blockData);
            }
        });
    }

    private void initRCVLandSelection() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvLandSelection.setLayoutManager(layoutManager);

        plantingBlockAdapter = new PlantingBlockAdapter(blockDataList, new PlantingBlockAdapter.IClickPlantingBlockListener() {
            @Override
            public void onClickPlantingBlock(BlockData blockData) {
                curBlockData = blockData;
                hexagonalLandView.setSelectedBlockData(blockData);
            }
        });
        rcvLandSelection.setAdapter(plantingBlockAdapter);

        if (!blockDataList.isEmpty()) {
            curBlockData = blockDataList.get(0);
            hexagonalLandView.setSelectedBlockData(curBlockData);
        }
    }

    private void updateBlockQuantity(BlockData blockData) {
        int position = blockDataList.indexOf(blockData);
        if (position != -1) {
            plantingBlockAdapter.notifyItemChanged(position);
        }

        // If the current block is depleted, select the next available block
        if (blockData.getQuantity() == 0) {
           selectNextAvailableBlock();
        }
    }

    private void selectNextAvailableBlock() {
        for (BlockData blockData : blockDataList) {
            if (blockData.getQuantity() > 0) {
                curBlockData = blockData;
                hexagonalLandView.setSelectedBlockData(blockData);
                int position = blockDataList.indexOf(blockData);
                plantingBlockAdapter.selectPosition(position);
                break;
            }
        }
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
        if (hexagonalLandView != null) {
            hexagonalLandView.saveAllTiles();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (hexagonalLandView != null) {
            hexagonalLandView.saveAllTiles();
        }
        super.onDestroy();
    }
}