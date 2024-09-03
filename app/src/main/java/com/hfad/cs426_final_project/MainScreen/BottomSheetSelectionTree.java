package com.hfad.cs426_final_project.MainScreen;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.R;

public class BottomSheetSelectionTree extends BottomSheetDialogFragment {
    View mView;
    MainScreenActivity mainScreenActivity;
    private AppContext appContext;

    private RecyclerView rcvTree, rcvFocusTime;
    private OwnTreeAdapter treeAdapter;
    private FocusTimeAdapter focusTimeAdapter;

    private ImageView ivTreeSelected;
    private TextView tvFocusTime;

    public BottomSheetSelectionTree() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_tree_selection, container, false); // Use the correct layout resource ID
        mainScreenActivity = (MainScreenActivity) getActivity();

        appContext = AppContext.getInstance();

        initUI();
        setupSelectionArea();
        initRCVTreeSelection();
        initRCVFocusTime();

        return mView;
    }

    private void initUI() {
        rcvTree = mView.findViewById(R.id.rcv_treeSelection);
        rcvFocusTime = mView.findViewById(R.id.rcvFocusedTime);
        ivTreeSelected = mView.findViewById(R.id.selectedTree);
        tvFocusTime = mView.findViewById(R.id.tvFocusedTime);
    }

    private void setupSelectionArea() {
        Glide.with(mainScreenActivity)
                .load(Uri.parse(appContext.getCurrentUser().getUserSetting().getSelectedTree().getImgUri()))
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(ivTreeSelected);
        tvFocusTime.setText(String.valueOf((appContext.getCurrentUser().getFocusTime() / 5) * 5));
    }

    private void initRCVTreeSelection() {
        rcvTree.setLayoutManager(new GridLayoutManager(getContext(), 4));

        // Initialize the adapter (assuming you pass the list of trees from the main activity)
        treeAdapter = new OwnTreeAdapter(appContext.getCurrentUser().getOwnTrees(), new OwnTreeAdapter.IClickTreeListener() {
            @Override
            public void onClickTree(Tree tree) {
                appContext.getCurrentUser().getUserSetting().setSelectedTree(tree);
                mainScreenActivity.setupTree();

                // change in selection area
                Glide.with(mainScreenActivity)
                        .load(Uri.parse(tree.getImgUri()))
                        .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                        .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                        .into(ivTreeSelected);
            }
        }); // Replace with actual data source
        rcvTree.setAdapter(treeAdapter);
    }

    private void initRCVFocusTime() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainScreenActivity, LinearLayoutManager.HORIZONTAL, false);
        rcvFocusTime.setLayoutManager(layoutManager);

        FocusTimeAdapter adapter = new FocusTimeAdapter(new FocusTimeAdapter.IClickFocusTimeListener() {
            @Override
            public void onClickFocusTime(int focusTime) {
                // setUser focus time
                appContext.getCurrentUser().setFocusTime(focusTime);

                tvFocusTime.setText(String.valueOf(focusTime));
            }
        });
        rcvFocusTime.setAdapter(adapter);

        // Optionally, start from the middle position to simulate endless scroll
        int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 12; // Assuming there are 12 items (10-120)
        rcvFocusTime.scrollToPosition(initialPosition);
    }
}
