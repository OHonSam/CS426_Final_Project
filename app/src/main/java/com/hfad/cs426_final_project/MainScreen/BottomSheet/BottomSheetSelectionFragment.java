package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetSelectionFragment extends BottomSheetDialogFragment {
    View mView;
    MainScreenActivity mainScreenActivity;
    private AppContext appContext;

    private RecyclerView rcvTree, rcvFocusTime, rcvTag;
    private ImageView ivTreeSelected, tagColorDisplay;
    private TextView tvFocusTime, tvTag;
    private ImageView ivHeart;

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
        setupSelectionArea();
        initRCVTreeSelection();
        initRCVFocusTime();
        initRCVTag();
        setupFavouriteHeart();
        return mView;
    }

    private void initUI() {
        rcvTree = mView.findViewById(R.id.rcv_treeSelection);
        rcvFocusTime = mView.findViewById(R.id.rcvFocusedTime);
        rcvTag = mView.findViewById(R.id.rcvTag);
        ivTreeSelected = mView.findViewById(R.id.selectedTree);
        tagColorDisplay = mView.findViewById(R.id.tagColorDisplay);
        tvFocusTime = mView.findViewById(R.id.tvFocusedTime);
        tvTag = mView.findViewById(R.id.tvTag);
        ivHeart = mView.findViewById(R.id.heart);
    }

    private void setupSelectionArea() {
        Glide.with(mainScreenActivity)
                .load(Uri.parse(appContext.getCurrentUser().getUserSetting().getSelectedTree().getImgUri()))
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(ivTreeSelected);
        tvFocusTime.setText(String.valueOf((appContext.getCurrentUser().getFocusTime() / 5) * 5));
        tvTag.setText(appContext.getCurrentUser().getFocusTag().getName());
        tagColorDisplay.setColorFilter(Color.parseColor(appContext.getCurrentUser().getFocusTag().getColorHex()));
    }

    private void initRCVTreeSelection() {
        rcvTree.setLayoutManager(new GridLayoutManager(getContext(), 4));

        // Initialize the adapter (assuming you pass the list of trees from the main activity)
        // change in selection area
        // or specify your desired dimensions
        // Or use centerCrop() if you want to crop the image to fit
        OwnTreeAdapter treeAdapter = new OwnTreeAdapter(appContext.getCurrentUser().getOwnTrees(), new OwnTreeAdapter.IClickTreeListener() {
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

                tvTag.setText(tag.getName());
                tagColorDisplay.setColorFilter(Color.parseColor(tag.getColorHex()));
            }
        });
        rcvTag.setAdapter(tagAdapter);
    }

    private void setupFavouriteHeart() {
        Drawable currentDrawable = ivHeart.getDrawable();
        Drawable unfilledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart_unfilled, null);
        Drawable filledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart, null);

        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDrawable != null && currentDrawable.getConstantState().equals(unfilledHeart.getConstantState())) {
                    ivHeart.setImageDrawable(filledHeart);
                    appContext.getCurrentUser().setFavourite(appContext.getCurrentUser().getUserSetting().getSelectedTree());
                }
                else {
                    ivHeart.setImageDrawable(unfilledHeart);
                    appContext.getCurrentUser().removeFavourite(appContext.getCurrentUser().getUserSetting().getSelectedTree());
                }
            }
        });
    }

}
