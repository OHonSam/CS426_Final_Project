package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetFavourite extends BottomSheetDialogFragment {
    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bottom_sheet_favourite, container, false);

        return mView;
    }
}