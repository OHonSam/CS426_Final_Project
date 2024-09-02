package com.hfad.cs426_final_project.StoreScreen;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

import java.util.ArrayList;
import java.util.List;

public class StoreScreenActivity extends AppCompatActivity {
    private AppContext appContext;
    private User currentUser;
    private List<Tree> treeList;

    private RecyclerView rcvTreeList;
    private TreeAdapter treeAdapter;
    private CheckBox ownTreeCheckBox;
    private TextView tvSunDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_screen);

        appContext = AppContext.getInstance();
        currentUser = appContext.getCurrentUser();
        treeList = appContext.getTreeList();

        initUI();
        updateSunDisplay();

        initTreeRecylerView();
        initOwnCheckBox();
    }

    private void initUI() {
        rcvTreeList = findViewById(R.id.rcvTreeList);
        ownTreeCheckBox = findViewById(R.id.ownTreeCheckBox);
        tvSunDisplay = findViewById(R.id.sunDisplay_storeScreen);
    }

    public void updateSunDisplay() {
        tvSunDisplay.setText(String.valueOf(currentUser.getSun()));
    }

    private void initTreeRecylerView() {
        rcvTreeList.setLayoutManager(new GridLayoutManager(this, 3));
        treeAdapter = new TreeAdapter(this, filterTrees(treeList, ownTreeCheckBox.isChecked()), currentUser);
        rcvTreeList.setAdapter(treeAdapter);
    }

    private void initOwnCheckBox() {
        ownTreeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            treeAdapter.updateTreeList(filterTrees(treeList, isChecked));
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


}
