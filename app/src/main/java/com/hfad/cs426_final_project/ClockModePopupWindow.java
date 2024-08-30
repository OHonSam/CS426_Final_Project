package com.hfad.cs426_final_project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ClockModePopupWindow extends PopupWindow{
    private SwitchMode switchMode;
    private MainScreenActivity mainScreenActivity;
    private StopwatchOptionFragment stopwatchOptionFragment;
    private TimerOptionFragment timerOption;
    private FrameLayout menuOption;
    public ClockModePopupWindow(Context context) {
        super(context);
    }

    public ClockModePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockModePopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClockModePopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ClockModePopupWindow() {
    }

    public ClockModePopupWindow(View contentView) {
        super(contentView);
    }

    public ClockModePopupWindow(int width, int height) {
        super(width, height);
    }

    public ClockModePopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public ClockModePopupWindow(View contentView, int width, int height, boolean focusable, MainScreenActivity mainScreenActivity) {
        super(contentView, width, height, focusable);
        this.mainScreenActivity = mainScreenActivity;

        ImageView timer = contentView.findViewById(R.id.hourglassImageView);
        ImageView stopwatch = contentView.findViewById(R.id.stopwatchImageView);
        ImageView thumb = contentView.findViewById(R.id.thumbTrackImageView);
        ImageView track = contentView.findViewById(R.id.rectangle);;
        menuOption = contentView.findViewById(R.id.menuOption);

        switchMode = new SwitchMode(timer,stopwatch,thumb,track);
        stopwatchOptionFragment = new StopwatchOptionFragment();
        timerOption = new TimerOptionFragment();
        //mainScreenActivity.replaceFragmentInPopup(stopwatchOption);

//        switchMode.addSwitchListener(new onModeClickListener() {
//            @Override
//            public void onModeClick(int id) {
//                if(id == 0){
//                    replaceFragment(timerOption);
//                }
//                else{
//                    replaceFragment(stopwatchOption);
//                }
//            }
//        });
    }

    private void replaceFragment(Fragment fragment, int containerViewId){
        FragmentManager fragmentManager = mainScreenActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(menuOption.getId(),fragment);
        fragmentTransaction.commit();
    }
}
