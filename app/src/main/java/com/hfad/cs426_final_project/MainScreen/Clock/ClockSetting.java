package com.hfad.cs426_final_project.MainScreen.Clock;

public class ClockSetting {
    private Clock.ClockMode clockMode;
    private boolean isDeepFocus;
    private boolean isCountExceedTime;

    public ClockSetting(Clock.ClockMode clockMode, boolean isDeepFocus, boolean isCountExceedTime) {
        this.clockMode = clockMode;
        this.isDeepFocus = isDeepFocus;
        this.isCountExceedTime = isCountExceedTime;
    }

    public ClockSetting(){
        this.clockMode = Clock.ClockMode.TIMER;
        this.isDeepFocus = false;
        this.isCountExceedTime = false;
    }

    public Clock.ClockMode getType() {
        return clockMode;
    }

    public boolean isDeepFocus() {
        return isDeepFocus;
    }

    public boolean isCountExceedTime() {return isCountExceedTime;}

    public ClockSetting getSetting(){
        return new ClockSetting(this.clockMode,this.isDeepFocus,this.isCountExceedTime);
    }

    public void setType(Clock.ClockMode clockMode){
        this.clockMode = clockMode;
    }
}
