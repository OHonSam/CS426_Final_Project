package com.hfad.cs426_final_project;

import android.content.Context;
import android.content.SharedPreferences;

public class StreakManager {

    private SharedPreferences sharedPreferences;
    // Singleton instance
    private static StreakManager instance;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    // Attributes
    private int streakDays;

    public static void setInstance(StreakManager instance) {
        StreakManager.instance = instance;
    }

    public boolean isHasCompletedASessionToday() {
        return hasCompletedASessionToday;
    }

    public void setHasCompletedASessionToday(boolean hasCompletedASessionToday) {
        this.hasCompletedASessionToday = hasCompletedASessionToday;
    }

    private boolean hasCompletedASessionToday;

    private StreakManager(Context context) {
        sharedPreferences = context.getSharedPreferences("StreakManagerPrefs", Context.MODE_PRIVATE);
        this.hasCompletedASessionToday = sharedPreferences.getBoolean("hasCompletedASessionToday", false);
        this.streakDays = 0;
    }

    // Private constructor to prevent instantiation
    private StreakManager() {
        // Initialize the attributes
        this.streakDays = 0; // default
        this.hasCompletedASessionToday = false; // default
    }

    // Method to get the singleton instance
    public static synchronized StreakManager getInstance() {
        if (instance == null) {
            instance = new StreakManager();
        }
        return instance;
    }

    public static synchronized StreakManager getInstance(Context context) {
        if (instance == null) {
            instance = new StreakManager(context);
        }
        return instance;
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
    }

    // Method to reset the completion status for the next day
    public void resetCompletionStatusForNextDay() {
        hasCompletedASessionToday = false;
    }

    // Getters and setters for the attributes
    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    // Save the boolean hasCompletedASessionToday to SharedPreferences
    private void saveSessionCompletionToLocal() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasCompletedASessionToday", hasCompletedASessionToday);
        editor.apply(); // Use apply for asynchronous saving
    }
}

