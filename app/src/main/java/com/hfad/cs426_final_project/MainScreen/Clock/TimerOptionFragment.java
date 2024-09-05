package com.hfad.cs426_final_project.MainScreen.Clock;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SwitchCompat;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;

public class TimerOptionFragment extends Fragment {
    private SwitchCompat deepFocusSwitch;
    private SwitchCompat countExceedSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_option, container, false);

        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            boolean isCurCountExceed = args.getBoolean("isCurCountExceed", false);
            boolean isCurDeepMode = args.getBoolean("isCurDeepModeTimer", false);

            // Initialize switches
            deepFocusSwitch = view.findViewById(R.id.deepFocusSwitchButton_timer);
            countExceedSwitch = view.findViewById(R.id.countExceedTimeSwitchButton);

            // Set switch states
            deepFocusSwitch.setChecked(isCurDeepMode);
            countExceedSwitch.setChecked(isCurCountExceed);

            // Check if clock is disabled
            boolean isModePickerDialogEnabled = AppContext.getInstance().getCurrentClock().getClockSetting().isModePickerDialogEnabled();
            deepFocusSwitch.setEnabled(isModePickerDialogEnabled);
            countExceedSwitch.setEnabled(isModePickerDialogEnabled);
        }
        return view;
    }


    public boolean isDeepFocusEnabled() {
        return deepFocusSwitch != null && deepFocusSwitch.isChecked();
    }

    public boolean isCountExceedEnabled() {
        return countExceedSwitch != null && countExceedSwitch.isChecked();
    }
}