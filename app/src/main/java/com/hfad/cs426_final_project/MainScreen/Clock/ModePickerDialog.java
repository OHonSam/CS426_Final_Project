package com.hfad.cs426_final_project.MainScreen.Clock;

import android.app.Dialog;
import android.content.DialogInterface;
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

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

public class ModePickerDialog extends DialogFragment {
    public static String TAG = "ModePickerDialog";
    private TimerOptionFragment timerOptionFragment;
    private StopwatchOptionFragment stopwatchOptionFragment;
    private SwitchMode switchMode;
    private boolean isCurDeepMode;
    private boolean isCurTimer;
    private boolean isCurCountExceed;
    private AppContext appContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_clock_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView timer = view.findViewById(R.id.hourglassImageView);
        ImageView stopwatch = view.findViewById(R.id.stopwatchImageView);
        ImageView thumb = view.findViewById(R.id.thumbTrackImageView);
        ImageView track = view.findViewById(R.id.rectangle);

        switchMode = new SwitchMode(timer, stopwatch, thumb, track);
        timerOptionFragment = new TimerOptionFragment();
        stopwatchOptionFragment = new StopwatchOptionFragment();

        appContext = AppContext.getInstance();

        this.isCurCountExceed = appContext.getCurrentUser().getIsCountExceed();
        this.isCurDeepMode = appContext.getCurrentUser().getIsDeepMode();
        this.isCurTimer = appContext.getCurrentUser().getIsTimer();

        if (isCurTimer)
            replaceFragment(timerOptionFragment);
        else
            replaceFragment(stopwatchOptionFragment);

        switchMode.addSwitchListener(new onModeClickListener() {
            @Override
            public void onModeClick(int id) {
                if (id == 0) {
                    isCurTimer = true;
                    replaceFragment(timerOptionFragment);
                    AppContext.getInstance().getCurrentClock().setClockMode(Clock.ClockMode.TIMER);
                } else {
                    isCurTimer = false;
                    replaceFragment(stopwatchOptionFragment);
                    AppContext.getInstance().getCurrentClock().setClockMode(Clock.ClockMode.STOPWATCH);
                }
            }
        });
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menuOption, fragment);
        fragmentTransaction.commit();
    }

    //Create interface in your DialogFragment (or a new file)
    public interface onDismissListener {
        void onDismiss(ModePickerDialog modePickerDialog);
    }

    //create Pointer and setter to it
    private onDismissListener onDismissListener;

    public void setOnDismissListener(onDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    //Call it on the dialogFragment onDismiss
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        appContext.getCurrentUser().setIsDeepMode(isCurDeepMode);
        appContext.getCurrentUser().setIsCountExceed(isCurCountExceed);
        appContext.getCurrentUser().setIsTimer(isCurTimer);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }
}
