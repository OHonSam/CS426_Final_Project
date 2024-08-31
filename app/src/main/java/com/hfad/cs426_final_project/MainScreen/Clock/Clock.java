package com.hfad.cs426_final_project.MainScreen.Clock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.util.Locale;

public class Clock {
    //Number of seconds displayed on the stopwatch.
    private int seconds;
    //Is the stopwatch running?
    private boolean running;

    public static enum ClockMode {
        STOPWATCH,
        TIMER
    }

    private final Handler handler;
    private TextView timeView;
    private int timeLimit;
    private MyButton startButton;
    private Context context;
    private ClockMode clockMode; // Enum for clock mode

    public Clock(Context context, TextView timeView, MyButton startButton, ClockMode clockMode, int initialTime, int timeLimit) {
        this.context = context;
        this.timeView = timeView;
        this.startButton = startButton;
        this.clockMode = clockMode;
        this.seconds = (this.clockMode == ClockMode.STOPWATCH) ? initialTime : timeLimit ;
        this.timeLimit = timeLimit;
        this.handler = new Handler();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
        startButton.setText("Pause");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.secondary_50));
    }

    public void stop() {
        running = false;
        startButton.setText("Plant");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.primary_20));
    }

    public void reset() {
        seconds = (clockMode == ClockMode.STOPWATCH) ? 0 : timeLimit;
    }

    public void setClockMode(ClockMode clockMode) {
        this.clockMode = clockMode;
        reset(); // Reset the clock when the mode changes
        // Update the UI to reflect the new clock mode
    }

    public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;

                // Ensure that the maximum time is 120:00 (7200 seconds)
                if (minutes > 120) {
                    minutes = 120;
                    secs = 0;
                }

                String time = String.format(Locale.getDefault(),
                        "%02d:%02d", minutes, secs);
                timeView.setText(time);

                if (running) {
                    if (clockMode == ClockMode.STOPWATCH) {
                        seconds++;
                        if (timeLimit > 0 && seconds > timeLimit) {
                            stop();
                            // Notify or vibrate when the timer reaches the limit
                            notifyOrVibrate(context);

                            // handle limit reached (e.g., navigate to CongratulationScreen)
                            Intent intent = new Intent(context, CongratulationScreenActivity.class);
                            context.startActivity(intent);
                            reset();
                        }
                    } else {
                        seconds--;
                        if (seconds < 0) {
                            stop();
                            // Notify or vibrate when the timer reaches the limit
                            notifyOrVibrate(context);
                            // handle timer end (e.g., navigate to CongratulationScreen)
                            Intent intent = new Intent(context, CongratulationScreenActivity.class);
                            context.startActivity(intent);
                            reset();
                        }
                    }
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void notifyOrVibrate(Context context) {
        // Check if the phone is in silent mode
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null && audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            // Vibrate the phone for 1 second
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)); // Vibrate for 1 second
            }

            // Show a short notification


        } else {
            // Show a notification
            String channelId = "clock_channel_id";
            String channelName = "Clock Notifications";

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Focus Session Finished")
                    .setContentText("The focus session has reached its end.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(1, notification);
        }
    }

}
