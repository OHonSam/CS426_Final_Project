package com.hfad.cs426_final_project.MainScreen.Clock;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;

public class StopwatchOptionFragment extends Fragment {
    private SwitchCompat deepFocusSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch_option, container, false);

        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            boolean isCurDeepMode = args.getBoolean("isCurDeepModeStopwatch", false);

            // Initialize switch
            deepFocusSwitch = view.findViewById(R.id.deepFocusSwitchButton_stopwatch);
            deepFocusSwitch.setChecked(isCurDeepMode);

            // Check if clock is disabled
            boolean isModePickerDialogEnabled = AppContext.getInstance().getCurrentClock().getClockSetting().isModePickerDialogEnabled();
            deepFocusSwitch.setEnabled(isModePickerDialogEnabled);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean isDeepFocusEnabled() {
        return deepFocusSwitch != null && deepFocusSwitch.isChecked();
    }
}