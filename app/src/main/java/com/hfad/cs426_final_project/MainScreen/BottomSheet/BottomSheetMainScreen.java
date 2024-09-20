package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

public class BottomSheetMainScreen extends BottomSheetDialogFragment {
    private View mView;
    private MainScreenActivity mainScreenActivity;

    private AppContext appContext;

    private MyButton btnPlant;
    private MaterialButtonToggleGroup modePicker;
    private Button btnSelection, btnFavourite;

    private ImageView ivTreeSelected, tagColorDisplay;
    private TextView tvFocusTime, tvTag;
    private ImageView ivHeart;

    public BottomSheetMainScreen() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bottom_sheet_main_screen, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();

        appContext = AppContext.getInstance();

        initUI();
        initListener();
        setupFavouriteHeart();
        updateSelectionArea();
        navigateSelectionFragment();
        return mView;
    }

    private void initUI() {
        modePicker = mView.findViewById(R.id.modePicker);
        btnSelection = mView.findViewById(R.id.btnSelection);
        btnFavourite = mView.findViewById(R.id.btnFavourite);
        btnPlant = mView.findViewById(R.id.btnPlant);

        modePicker.check(R.id.btnSelection);
        activateBtn(btnSelection);
        deactivateBtn(btnFavourite);

        ivTreeSelected = mView.findViewById(R.id.selectedTree);
        tagColorDisplay = mView.findViewById(R.id.tagColorDisplay);
        tvFocusTime = mView.findViewById(R.id.tvFocusedTime);
        tvTag = mView.findViewById(R.id.tvTag);
        ivHeart = mView.findViewById(R.id.heart);
    }

    public void updateSelectionArea() {
        Tree curTree = appContext.getCurrentUser().getUserSetting().getSelectedTree();
        Tag curTag = appContext.getCurrentUser().getFocusTag();
        int curFocusTime = appContext.getCurrentUser().retrieveFocusTimeMinutes();

        Glide.with(mainScreenActivity)
                .load(Uri.parse(curTree.getImgUri()))
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(ivTreeSelected);
        tvFocusTime.setText(String.valueOf((curFocusTime / 5) * 5));
        tvTag.setText(curTag.getName());
        tagColorDisplay.setColorFilter(curTag.getColor());

        if(appContext.getCurrentUser().isFavourite(curTree, curTag, curFocusTime)) {
            setFavouriteOn();
        }
        else {
            setFavouriteOff();
        }
    }

    public void navigateSelectionFragment() {
        modePicker.check(R.id.btnSelection);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.selectionContainer, new BottomSheetSelectionFragment());
        fragmentTransaction.commit();
    }

    public void navigateFavouriteFragment() {
        modePicker.check(R.id.btnFavourite);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.selectionContainer, new BottomSheetFavouriteFragment());
        fragmentTransaction.commit();
    }

    private void initListener() {
        modePicker.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnSelection) {
                    activateBtn(btnSelection);
                    deactivateBtn(btnFavourite);
                    navigateSelectionFragment();
                }
                else if (checkedId == R.id.btnFavourite) {
                    activateBtn(btnFavourite);
                    deactivateBtn(btnSelection);
                    navigateFavouriteFragment();
                }
            }
        });

        btnPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreenActivity.startPlanting();
            }
        });
    }

    private void activateBtn(Button button) {
        button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
    }

    private void deactivateBtn(Button button) {
        button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_70, null));
        button.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    }


    private void setupFavouriteHeart() {
        Drawable unfilledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart_unfilled, null);
        Drawable filledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart, null);

        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable currentDrawable = ivHeart.getDrawable();
                Tree curTree = appContext.getCurrentUser().getUserSetting().getSelectedTree();
                Tag curTag = appContext.getCurrentUser().getFocusTag();
                int curFocusTime = appContext.getCurrentUser().retrieveFocusTimeMinutes();
                Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.selectionContainer);

                if (currentDrawable != null && currentDrawable.getConstantState().equals(unfilledHeart.getConstantState())) {
                    ivHeart.setImageDrawable(filledHeart);
                    appContext.getCurrentUser().addFavourite(curTree, curTag, curFocusTime);

                    if (currentFragment instanceof BottomSheetFavouriteFragment) {
                        navigateFavouriteFragment();
                    }
                }
                else {
                    ivHeart.setImageDrawable(unfilledHeart);
                    appContext.getCurrentUser().removeFavourite(curTree, curTag, curFocusTime);

                    if (currentFragment instanceof BottomSheetFavouriteFragment) {
                        navigateFavouriteFragment();
                    }
                }
            }
        });
    }

    private void setFavouriteOn() {
        Drawable filledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart, null);
        ivHeart.setImageDrawable(filledHeart);
    }

    private void setFavouriteOff() {
        Drawable unfilledHeart = ResourcesCompat.getDrawable(getResources(), R.drawable.heart_unfilled, null);
        ivHeart.setImageDrawable(unfilledHeart);
    }
}