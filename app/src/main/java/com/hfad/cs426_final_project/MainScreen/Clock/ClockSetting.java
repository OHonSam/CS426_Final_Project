package com.hfad.cs426_final_project.MainScreen.Clock;

public class ClockSetting {
    private Clock.ClockMode clockMode;
    private boolean isDeepModeTimer;
    private boolean isDeepModeStopwatch;
    private boolean isCountExceedTime;

    public ClockSetting(){
        this.clockMode = Clock.ClockMode.TIMER;
        this.isDeepModeTimer = false;
        this.isDeepModeStopwatch = false;
        this.isCountExceedTime = false;
    }

    public ClockSetting(Clock.ClockMode clockMode, boolean isDeepModeTimer, boolean isDeepModeStopwatch, boolean isCountExceedTime) {
        this.clockMode = clockMode;
        this.isDeepModeTimer = isDeepModeTimer;
        this.isDeepModeStopwatch = isDeepModeStopwatch;
        this.isCountExceedTime = isCountExceedTime;
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
}
