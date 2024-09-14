package com.hfad.cs426_final_project;

public class StreakManager {
    private int streakAllTime;
    private int streakDays;
    private boolean hasCompletedASessionToday;

    public StreakManager() {
        // Initialize the attributes
        this.streakAllTime = 0; // default
        this.streakDays = 0; // default
        this.hasCompletedASessionToday = false; // default
    }

    // Getters and setters for the attributes
    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public boolean isHasCompletedASessionToday() {
        return hasCompletedASessionToday;
    }

    public void setHasCompletedASessionToday(boolean hasCompletedASessionToday) {
        this.hasCompletedASessionToday = hasCompletedASessionToday;
    }

    public int getStreakAllTime() {
        return streakAllTime;
    }

    public void setStreakAllTime(int streakAllTime) {
        this.streakAllTime = streakAllTime;
    }

    // Method to mark session completion and update streak
    public void markSessionComplete() {
        if (hasCompletedASessionToday) {
            // If session was already completed today, do nothing
            return;
        }

        // Update the completion status
        hasCompletedASessionToday = true;

        // Increase streakDays if session was completed
        streakDays++;
        streakAllTime = Math.max(streakDays, streakAllTime);
    }

    // Method to reset the completion status for the next day
    public void resetCompletionStatusForNextDay() {
        hasCompletedASessionToday = false;
    }
}

