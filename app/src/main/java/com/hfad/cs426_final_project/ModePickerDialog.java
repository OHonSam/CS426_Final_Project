package com.hfad.cs426_final_project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ModePickerDialog extends DialogFragment {
    public static String TAG = "ModePickerDialog";
    private TimerOptionFragment timerOptionFragment;
    private StopwatchOptionFragment stopwatchOptionFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_clock_mode,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //int width = getResources().getDisplayMetrics().widthPixels;
        //int height = getResources().getDisplayMetrics().heightPixels;
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set width to 90% of the screen width and height to WRAP_CONTENT
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                params.gravity = Gravity.CENTER;
                window.setAttributes(params);
            }
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView timer = view.findViewById(R.id.hourglassImageView);
        ImageView stopwatch = view.findViewById(R.id.stopwatchImageView);
        ImageView thumb = view.findViewById(R.id.thumbTrackImageView);
        ImageView track = view.findViewById(R.id.rectangle);;
        SwitchMode switchMode = new SwitchMode(timer,stopwatch,thumb,track);
        timerOptionFragment = new TimerOptionFragment();
        stopwatchOptionFragment = new StopwatchOptionFragment();

        replaceFragment(timerOptionFragment);

        switchMode.addSwitchListener(new onModeClickListener() {
            @Override
            public void onModeClick(int id) {
                if(id == 0){
                    replaceFragment(timerOptionFragment);
                }
                else{
                    replaceFragment(stopwatchOptionFragment);
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menuOption,fragment);
        fragmentTransaction.commit();
    }
}
