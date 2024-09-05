package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Favourite;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetFavouriteFragment extends BottomSheetDialogFragment {
    private View mView;
    private MainScreenActivity mainScreenActivity;
    private AppContext appContext;

    private RecyclerView rcvFavouriteList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bottom_sheet_favourite, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();

        appContext = AppContext.getInstance();

        initUI();
        setupRCVFavourite();
        return mView;
    }

    private void initUI() {
        rcvFavouriteList = mView.findViewById(R.id.rcvFavouriteList);
    }

    private void setupRCVFavourite() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainScreenActivity, LinearLayoutManager.VERTICAL, false);
        rcvFavouriteList.setLayoutManager(layoutManager);

        FavouriteAdapter favouriteAdapter = new FavouriteAdapter(mainScreenActivity, appContext.getCurrentUser().getFavouriteList(), new FavouriteAdapter.IClickFavouriteListener() {
            @Override
            public void onClickFavourite(Favourite favourite) {
                appContext.getCurrentUser().setFocusTag(favourite.getTag());
                appContext.getCurrentUser().setFocusTime(favourite.getFocusTime());
                appContext.getCurrentUser().getUserSetting().setSelectedTree(favourite.getTree());

                mainScreenActivity.updateBottomSheetSelection();
            }
        });
        rcvFavouriteList.setAdapter(favouriteAdapter);
        // Attach swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteFavouriteCallBack(favouriteAdapter));
        itemTouchHelper.attachToRecyclerView(rcvFavouriteList);
    }

}