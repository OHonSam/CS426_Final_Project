package com.hfad.cs426_final_project.StoreScreen;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;

import java.util.ArrayList;
import java.util.List;

public class StoreScreenActivity extends BaseScreenActivity {
    private AppContext appContext;
    private User currentUser;
    private List<Tree> treeList;
    private List<Block> blockList;

    MyButton btnTreeShop, btnBlockShop;
    private RecyclerView rcvShop;
    private TreeAdapter treeAdapter;
    private BlockAdapter blockAdapter;
    private CheckBox ownTreeCheckBox;
    private TextView tvSunDisplay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_store_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();

        appContext = AppContext.getInstance();
        currentUser = appContext.getCurrentUser();
        treeList = appContext.getTreeList();
        blockList = appContext.getGrassList();

        setupListener();
        updateSunDisplay();
        prepareRCVShop();
        setupTreeShop();
        initOwnCheckBox();
    }

    private void initUI() {
        btnTreeShop = findViewById(R.id.treeShopButton);
        btnBlockShop = findViewById(R.id.blockShopButton);
        rcvShop = findViewById(R.id.rcvTreeList);
        ownTreeCheckBox = findViewById(R.id.ownTreeCheckBox);
        tvSunDisplay = findViewById(R.id.sunDisplay_storeScreen);
        navigationView = findViewById(R.id.nav_view_screen_choices);
    }

    private void setupListener() {
        btnTreeShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTreeShop.setActivate();
                btnBlockShop.setInActivate();
                setupTreeShop();
            }
        });
        btnBlockShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlockShop.setActivate();
                btnTreeShop.setInActivate();
                setupBlockShop();
            }
        });
    }

    public void updateSunDisplay() {
        tvSunDisplay.setText(String.valueOf(currentUser.getSun()));
    }


    private void prepareRCVShop() {
        rcvShop.setLayoutManager(new GridLayoutManager(this, 3));
        treeAdapter = new TreeAdapter(this, filterTrees(treeList, ownTreeCheckBox.isChecked()), currentUser);
        blockAdapter = new BlockAdapter(this, filterBlock(blockList, ownTreeCheckBox.isChecked()), currentUser);
    }

    private void setupTreeShop() {
        rcvShop.setAdapter(treeAdapter);
    }

    private void setupBlockShop() {
        rcvShop.setAdapter(blockAdapter);
    }

    private void initOwnCheckBox() {
        ownTreeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            treeAdapter.updateTreeList(filterTrees(treeList, isChecked));
            blockAdapter.updateBlockList(filterBlock(blockList, isChecked));
        });
    }

    private List<Tree> filterTrees(List<Tree> treeList, boolean excludeOwned) {
        List<Tree> filteredList = new ArrayList<>();
        for (Tree tree : treeList) {
            if (excludeOwned && currentUser.hasTree(tree)) {
                continue;
            }
            filteredList.add(tree);
        }
        return filteredList;
    }

    private List<Block> filterBlock(List<Block> blockList, boolean excludeOwned) {
        List<Block> filteredList = new ArrayList<>();
        for (Block block : blockList) {
            if (excludeOwned && currentUser.hasBlock(block)) {
                continue;
            }
            filteredList.add(block);
        }
        return filteredList;
    }
}
