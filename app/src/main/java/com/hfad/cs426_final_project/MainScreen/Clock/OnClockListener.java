package com.hfad.cs426_final_project.MainScreen.Clock;

public interface OnClockListener {
    void redirectToFailScreenActivity(String message, int rewards);
    void redirectToCongratulationScreenActivity(int rewards);
}
