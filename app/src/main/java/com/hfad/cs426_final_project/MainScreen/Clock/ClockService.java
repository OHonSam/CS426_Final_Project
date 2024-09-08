package com.hfad.cs426_final_project.MainScreen.Clock;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.hfad.cs426_final_project.R;
import java.util.Locale;

public class ClockService extends Service {
    public static final String CHANNEL_ID = "ClockServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private Runnable runnable;
    private int seconds = 0;
    private boolean isTimer; // true for timer, false for stopwatch
    private int targetTime; // Used when isTimer is true

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isTimer = intent.getBooleanExtra("isTimer", false);
        targetTime = intent.getIntExtra("timeLimit", 0);
        seconds = isTimer ? targetTime : 0;

        startForeground(NOTIFICATION_ID, buildNotification(getFormattedTimeString()));
        startClock();

        return START_STICKY;
    }

    private void startClock() {
        runnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();

                if (isTimer) {
                    if (seconds <= 0) {
                        stopSelf();
                    } else {
                        seconds--;
                        handler.postDelayed(this, 1000);
                    }
                } else {
                    if (seconds >= targetTime) {
                        stopSelf();
                    } else {
                        seconds++;
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };
        handler.post(runnable);
    }

    private void updateNotification() {
        Notification notification = buildNotification(getFormattedTimeString());
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    private String getFormattedTimeString() {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }

    private Notification buildNotification(String time) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(isTimer ? "Timer Running" : "Stopwatch Running")
                .setContentText("Elapsed Time: " + time)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Clock Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
