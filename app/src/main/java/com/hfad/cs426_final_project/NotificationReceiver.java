package com.hfad.cs426_final_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationType = intent.getStringExtra("notificationType");
        StreakManager streakManager = StreakManager.getInstance(context);

        if ("NonFocusNotification".equals(notificationType)) {
            // Check if the session was completed today from SharedPreferences (fallback to local)
            if (!streakManager.isHasCompletedASessionToday()) {
                // Send notification if session was not completed today
                NotificationHelper notificationHelper = new NotificationHelper(context);
                notificationHelper.createNotification("Non-focus today", "Don't let your focus streak over.");
            }
        } else if ("MidnightReset".equals(notificationType)) {
            // Reset hasCompletedASessionToday for the new day
            streakManager.resetCompletionStatusForNextDay();

            // Check again if the session was completed after resetting
            if (!streakManager.isHasCompletedASessionToday()) {
                // Reset streak and notify the user
                streakManager.setStreakDays(0);
                NotificationHelper notificationHelper = new NotificationHelper(context);
                notificationHelper.createNotification("Maybe next time", "Your streak is over.");
            }
        }
    }
}

