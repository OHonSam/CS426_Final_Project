package com.hfad.cs426_final_project.MainScreen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;

public class TimePickerDialog extends DialogFragment {
    public static final String TAG = "TimePickerDialog";

    public interface onDismissListener {
        void onDismiss(TimePickerDialog timePickerDialog, int breakTime, boolean isBreak, boolean autoStartSession);
    }

    private NumberPicker numberPicker;
    private Button btnCancel, btnBreak;
    private TimePickerDialog.onDismissListener onDismissListener;
    private SwitchCompat autoStartSessionSwitch;

    private int breakTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_time_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUIReferences(view);
        setupOnClickListener();
    }

    private void setupOnClickListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(TimePickerDialog.this, breakTime,false,isAutoStartEnabled() );
                }
                dismiss();  // Dismiss the dialog
            }
        });

        btnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(TimePickerDialog.this, breakTime,true,isAutoStartEnabled());
                }
                dismiss();  // Dismiss the dialog
            }
        });
    }

    private void setupUIReferences(View view) {
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);

        numberPicker.setMaxValue(20);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                breakTime = newVal*60;
            }
        });

        btnCancel = view.findViewById(R.id.buttonCancel);
        btnBreak = view.findViewById(R.id.buttonBreak);
        autoStartSessionSwitch = view.findViewById(R.id.autoStartSessionSwitch);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void setOnDismissListener(TimePickerDialog.onDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    private boolean isAutoStartEnabled() {
        return autoStartSessionSwitch != null && autoStartSessionSwitch.isChecked();
    }
}

