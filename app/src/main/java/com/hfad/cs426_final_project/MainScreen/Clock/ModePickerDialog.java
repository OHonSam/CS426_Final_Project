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

public class ModePickerDialog extends DialogFragment {
    public static String TAG = "ModePickerDialog";
    private TimerOptionFragment timerOptionFragment;
    private StopwatchOptionFragment stopwatchOptionFragment;
    private SwitchModeAnimation switchModeAnimation;
    private boolean isCurDeepModeTimer;
    private boolean isCurDeepModeStopwatch;
    private boolean isCurCountExceed;
    private Clock.ClockMode curMode;
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

        appContext = AppContext.getInstance();

        this.isCurCountExceed = appContext.getCurrentUser().getClockSetting().getIsCountExceedTime();
        this.isCurDeepModeTimer = appContext.getCurrentUser().getClockSetting().getIsDeepModeTimer();
        this.isCurDeepModeStopwatch = appContext.getCurrentUser().getClockSetting().getIsDeepModeStopwatch();
        this.curMode = appContext.getCurrentUser().getClockSetting().getType();

        timerOptionFragment = new TimerOptionFragment();
        Bundle argsTimer = new Bundle();
        argsTimer.putBoolean("isCurCountExceed", isCurCountExceed);
        argsTimer.putBoolean("isCurDeepModeTimer", isCurDeepModeTimer);
        timerOptionFragment.setArguments(argsTimer);

        stopwatchOptionFragment = new StopwatchOptionFragment();
        Bundle argsStopwatch = new Bundle();
        argsStopwatch.putBoolean("isCurDeepModeStopwatch", isCurDeepModeStopwatch);
        stopwatchOptionFragment.setArguments(argsStopwatch);

        switchModeAnimation = new SwitchModeAnimation(timer, stopwatch, thumb, track, curMode);
        // Check the clock state
        boolean isModePickerDialogEnabled = AppContext.getInstance().getCurrentClock().getClockSetting().isModePickerDialogEnabled();

        if (!isModePickerDialogEnabled) {
            // If the clock is disabled, prevent interaction
            timer.setEnabled(false);
            stopwatch.setEnabled(false);
            // Add any other components you want to disable
        }

        if (curMode == Clock.ClockMode.TIMER)
            replaceFragment(timerOptionFragment);
        else
            replaceFragment(stopwatchOptionFragment);

        // Delay the movement of the thumb until the layout is complete
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (curMode == Clock.ClockMode.STOPWATCH) {
                switchModeAnimation.startTranslateAnimation(Clock.ClockMode.TIMER, stopwatch);
            }
        });

        switchModeAnimation.addSwitchListener(new OnModeClickListener() {
            @Override
            public void onModeClick(Clock.ClockMode selectedMode) {
                if (selectedMode == Clock.ClockMode.TIMER) {
                    curMode = Clock.ClockMode.TIMER;
                    replaceFragment(timerOptionFragment);
                    appContext.getCurrentClock().setClockMode(Clock.ClockMode.TIMER);
                } else {
                    curMode = Clock.ClockMode.STOPWATCH;
                    replaceFragment(stopwatchOptionFragment);
                    appContext.getCurrentClock().setClockMode(Clock.ClockMode.STOPWATCH);
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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        isCurDeepModeTimer = timerOptionFragment.isDeepFocusEnabled();
        isCurCountExceed = timerOptionFragment.isCountExceedEnabled();
        isCurDeepModeStopwatch = stopwatchOptionFragment.isDeepFocusEnabled();

        appContext.getCurrentUser().getClockSetting().setIsDeepModeTimer(isCurDeepModeTimer);
        appContext.getCurrentUser().getClockSetting().setIsDeepModeStopwatch(isCurDeepModeStopwatch);
        appContext.getCurrentUser().getClockSetting().setIsCountExceedTime(isCurCountExceed);
        appContext.getCurrentUser().getClockSetting().setType(curMode);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }
}
