package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetMainScreen extends BottomSheetDialogFragment {
    View mView;
    MyButton btnSelection, btnFavourite;

    public BottomSheetMainScreen() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bottom_sheet_main_screen, container, false);

        initUI();
        navigateSelectionFragment();
        initListener();
        return mView;
    }

    private void initUI() {
        btnSelection = mView.findViewById(R.id.btnSelection);
        btnFavourite = mView.findViewById(R.id.btnFavourite);
    }

    private void navigateSelectionFragment() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.selectionContainer, new BottomSheetSelectionFragment());
        fragmentTransaction.commit();
    }

    private void navigateFavouriteFragment() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.selectionContainer, new BottomSheetSelectionFragment());
        fragmentTransaction.commit();
    }

    private void initListener() {
        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSelection.setActivate();
                btnFavourite.setInActivate();
                navigateSelectionFragment();
            }
        });
        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSelection.setInActivate();
                btnFavourite.setActivate();
                navigateFavouriteFragment();
            }
        });
    }


}