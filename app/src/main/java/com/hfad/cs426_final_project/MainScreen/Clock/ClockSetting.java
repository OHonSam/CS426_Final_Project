package com.hfad.cs426_final_project.MainScreen.Clock;

public class ClockSetting {

    private boolean isModePickerDialogEnabled = true; // Initial state is ENABLE
    private Clock.ClockMode clockMode;
    private boolean isDeepModeTimer;
    private boolean isDeepModeStopwatch;
    private boolean isCountExceedTime;
    private int targetTime;

    public ClockSetting(){
        this.clockMode = Clock.ClockMode.TIMER;
        this.isDeepModeTimer = false;
        this.isDeepModeStopwatch = false;
        this.isCountExceedTime = false;
        this.targetTime = 600; // by default 10 minutes
    }

    public ClockSetting(Clock.ClockMode clockMode, boolean isDeepModeTimer, boolean isDeepModeStopwatch, boolean isCountExceedTime, int targetTime) {
        this.clockMode = clockMode;
        this.isDeepModeTimer = isDeepModeTimer;
        this.isDeepModeStopwatch = isDeepModeStopwatch;
        this.isCountExceedTime = isCountExceedTime;
        this.targetTime = targetTime;
    }

    public Clock.ClockMode getType() {
        return clockMode;
    }

    public void setType(Clock.ClockMode type) {
        this.clockMode = type;
    }

    public boolean getIsDeepModeTimer() {
        return isDeepModeTimer;
    }
    public void setIsDeepModeTimer(boolean deepMode) {
        isDeepModeTimer = deepMode;
    }

    public boolean getIsCountExceedTime() {
        return isCountExceedTime;
    }
    public void setIsCountExceedTime(boolean countExceedTime) {
        isCountExceedTime = countExceedTime;
    }

    public boolean getIsDeepModeStopwatch() {
        return isDeepModeStopwatch;
    }
    public void setIsDeepModeStopwatch(boolean deepModeStopwatch) {
        isDeepModeStopwatch = deepModeStopwatch;
    }

    public int getTargetTime() {
        return targetTime;
    }
    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isModePickerDialogEnabled() {
        return isModePickerDialogEnabled;
    }

    public void setModePickerDialogEnabled(boolean modePickerDialogEnabled) {
        this.isModePickerDialogEnabled = modePickerDialogEnabled;
    }
}
