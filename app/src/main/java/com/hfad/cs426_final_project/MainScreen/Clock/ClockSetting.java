package com.hfad.cs426_final_project.MainScreen.Clock;

public class ClockSetting {
    private Clock.ClockMode clockMode;

    private boolean isDeepMode;

    private boolean isCountExceedTime;
    public ClockSetting(){
        this.clockMode = Clock.ClockMode.TIMER;
        this.isDeepMode = false;
        this.isCountExceedTime = false;
    }

    public ClockSetting(Clock.ClockMode clockMode, boolean isDeepMode, boolean isCountExceedTime) {
        this.clockMode = clockMode;
        this.isDeepMode = isDeepMode;
        this.isCountExceedTime = isCountExceedTime;
    }

    public Clock.ClockMode getType() {
        return clockMode;
    }
    public void setType(Clock.ClockMode type) {
        this.clockMode = type;
    }

    public boolean getIsDeepMode() {
        return isDeepMode;
    }
    public void setIsDeepMode(boolean deepMode) {
        isDeepMode = deepMode;
    }
    
    public boolean getIsCountExceedTime() {return isCountExceedTime;}
    public void setIsCountExceedTime(boolean countExceedTime) {
        isCountExceedTime = countExceedTime;
    }
}
