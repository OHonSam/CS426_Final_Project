package com.hfad.cs426_final_project.MainScreen.Clock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;

import java.util.Locale;

public class Clock {
    //Number of seconds displayed on the stopwatch.
    private int seconds;
    //Is the stopwatch running?
    private boolean running;
    private final Handler handler;

    private static final String CHANNEL_ID = "clock_channel_id";
    private static final int NOTIFICATION_ID = 1;

    public static enum ClockMode {
        STOPWATCH,
        TIMER;
    }

    private TextView timeView;
    private int timeLimit;
    private MyButton startButton;
    private Context context;
    private final int TIME_LIMIT_OUTSIDE = 10;

    private int secondsOutside;
    private boolean runningOutside = false;
    private Handler deepModeHandler;
    private Runnable runnable;
    // We can't use AppContext.getClockSetting().getType() as there is switch mode in the app
    // mClockMode is to display and it may differ from the Firebase-fetched clock setting type until onDismiss of DialogFragment

    private ClockSetting clockSetting;
    public Clock(Context context, TextView timeView, MyButton startButton, ClockSetting clockSetting, int initialTime, int timeLimit) {
        this.context = context;
        this.timeView = timeView;
        this.startButton = startButton;
        this.clockSetting = clockSetting;
        this.seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? initialTime : timeLimit;
        this.timeLimit = timeLimit;
        this.handler = new Handler();
        this.deepModeHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // print notification
                if (runningOutside && running) {
                    Log.d("ClockOutside", "Seconds:" + secondsOutside);
                    secondsOutside--;
                    if (secondsOutside < 0) {
                        Intent intent = new Intent(context, CongratulationScreenActivity.class);
                        context.startActivity(intent);

                        runningOutside = false;
                        stop();
                        reset();
                    }
                }
                deepModeHandler.postDelayed(this, 1000);
            }
        };
    }

    public ClockSetting getClockSetting() {
        return clockSetting;
    }

    public void setClockSetting(ClockSetting clockSetting) {
        this.clockSetting = clockSetting;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
        startButton.setText("Pause");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.secondary_50));

        Intent serviceIntent = new Intent(context, ClockService.class);
        serviceIntent.putExtra("isTimer", clockSetting.getType() == ClockMode.TIMER);
        serviceIntent.putExtra("timeLimit", timeLimit);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public void stop() {
        running = false;
        startButton.setText("Plant");
        startButton.setBackgroundTintList(ContextCompat.getColorStateList (startButton.getContext(), R.color.primary_20));

        Intent serviceIntent = new Intent(context, ClockService.class);
        context.stopService(serviceIntent);
    }

    public void reset() {
        seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? 0 : timeLimit;
    }

    public void setClockMode(ClockMode mClockMode) {
        this.clockSetting.setType(mClockMode);
        reset(); // Reset the clock when the mode changes
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
                    if (clockSetting.getType() == ClockMode.STOPWATCH) {
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

    public void enableDeepModeCount() {
        runningOutside = true;
        secondsOutside = TIME_LIMIT_OUTSIDE;
        if (clockSetting.getIsDeepModeTimer() || clockSetting.getIsDeepModeStopwatch()) {
            deepModeHandler.post(runnable);
        }
    }

    public void disableDeepModeCount() {
        runningOutside = false;
        deepModeHandler.removeCallbacks(runnable);
    }

    private void notifyOrVibrate(Context context) {
        String channelName = "Clock Notifications";

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        // Check if the phone is in silent mode
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isSilentMode = audioManager != null && audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Focus Session Finished")
                .setContentText("The focus session has reached its end.")
                .setAutoCancel(true);

        // Add lights only if not in silent mode
        if (isSilentMode) {
            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        } else {
            // Vibrate the phone for 1 second if not in silent mode
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)); // Vibrate for 1 second
            }
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
