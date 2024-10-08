package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetSelectionFragment extends BottomSheetDialogFragment {
    private View mView;
    private MainScreenActivity mainScreenActivity;
    private AppContext appContext;

    private RecyclerView rcvTree, rcvFocusTime, rcvTag, rcvBlock;

    public BottomSheetSelectionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bottom_sheet_selection, container, false); // Use the correct layout resource ID
        mainScreenActivity = (MainScreenActivity) getActivity();

        appContext = AppContext.getInstance();

        initUI();
        initRCVTreeSelection();
        initRCVFocusTime();
        initRCVTag();
        initRCVBlock();
        return mView;
    }

    private void initUI() {
        rcvTree = mView.findViewById(R.id.rcv_treeSelection);
        rcvFocusTime = mView.findViewById(R.id.rcvFocusedTime);
        rcvTag = mView.findViewById(R.id.rcvTag);
        rcvBlock = mView.findViewById(R.id.rcvBlock);
    }

    private void initRCVTreeSelection() {
        rcvTree.setLayoutManager(new GridLayoutManager(getContext(), 4));

        OwnTreeAdapter treeAdapter = new OwnTreeAdapter(appContext.getCurrentUser().getOwnTrees(), new OwnTreeAdapter.IClickTreeListener() {
            @Override
            public void onClickTree(Tree tree) {
                appContext.getCurrentUser().getUserSetting().setSelectedTree(tree);
                mainScreenActivity.setupTree();

                // change in selection area
                mainScreenActivity.updateBottomSheetSelection();
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
                appContext.getCurrentUser().updateFocusTimeMinutes(focusTime);

                // change in selection area
                mainScreenActivity.updateBottomSheetSelection();
            }
        });
        rcvFocusTime.setAdapter(adapter);

        rcvFocusTime.scrollToPosition(adapter.getSelectedPosition() - 2);
    }

    private void initRCVTag() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainScreenActivity, LinearLayoutManager.HORIZONTAL, false);
        rcvTag.setLayoutManager(layoutManager);

        TagAdapter tagAdapter = new TagAdapter(appContext.getCurrentUser().getOwnTags(), new TagAdapter.IClickTagListener() {
            @Override
            public void onClickTag(Tag tag) {
                appContext.getCurrentUser().setFocusTag(tag);
                mainScreenActivity.updateTagDisplay();

                // change in selection area
                mainScreenActivity.updateBottomSheetSelection();
            }
        });
        rcvTag.setAdapter(tagAdapter);
    }

    private void initRCVBlock() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainScreenActivity, LinearLayoutManager.HORIZONTAL, false);
        rcvBlock.setLayoutManager(layoutManager);

        OwnBlockAdapter blockAdapter = new OwnBlockAdapter(appContext.getCurrentUser().getOwnBlock(), new OwnBlockAdapter.IClickOwnBlockListener() {
            @Override
            public void onClickOwnBlock(BlockData blockData) {
                appContext.getCurrentUser().setSelectedBlock(blockData.getBlock());
            }
        });
        rcvBlock.setAdapter(blockAdapter);
    }
}
