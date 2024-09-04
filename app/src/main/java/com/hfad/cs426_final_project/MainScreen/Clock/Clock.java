package com.hfad.cs426_final_project.MainScreen.Clock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.util.Locale;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Clock {
    public static enum ClockMode {
        STOPWATCH,
        TIMER;
    }

    private static final String CHANNEL_ID = "clock_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private final int TIME_LIMIT_OUTSIDE = 10;
    private int totalInterval = 24;
    private int interval = 5;

    //Number of seconds displayed on the stopwatch.
    private int seconds;
    //Is the stopwatch running?
    private boolean running;
    private boolean isEndSession;

    private final Handler handler;
    private Runnable runnableClock;
    private Handler deepModeHandler;
    private Runnable runnableDeepMode;

    private TextView timeView;
    private MyButton startButton;
    private Context context;
    private int secondsOutside;
    private boolean runningOutside = false;

    private ClockSetting clockSetting;

    private CircularSeekBar progressBar;

    public Clock(Context context, TextView timeView, MyButton startButton, ClockSetting clockSetting, CircularSeekBar progressBar) {
        this.context = context;
        this.timeView = timeView;
        this.startButton = startButton;
        this.clockSetting = clockSetting;
        this.seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? 0 : clockSetting.getTargetTime();
        this.isEndSession = false;
        this.handler = new Handler();
        this.deepModeHandler = new Handler();
        this.runnableClock = createClockRunnable();
        this.runnableDeepMode = createDeepModeRunnable();
        this.progressBar = progressBar;
        initProgressBar();
        setupStartButton();
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getClockSetting().getIsCountExceedTime() && getIsEndSession()) {
                    setIsEndSession(false);
                    stop();
                    reset();
                    redirectToCongratulationScreen();
                    return;
                }

                // If the clock is not running then start it
                if (!isRunning()) {
                    start();
                } else {
                    giveUp();
                }
            }
        });
    }

    private void redirectToCongratulationScreen() {
        Intent intent = new Intent(context, CongratulationScreenActivity.class);
        context.startActivity(intent);
    }

    private void redirectToFailScreenActivity() {
        // TODO: Redirect to FailScreenActivity
        Intent intent = new Intent(context, CongratulationScreenActivity.class);
        context.startActivity(intent);
    }

    private void initProgressBar() {
        progressBar.setMax(totalInterval);
        int progressIntervalIndex = (clockSetting.getTargetTime() / 60) / interval;
        progressBar.setProgress(progressIntervalIndex);
        updateTimeTextFromProgressBar(progressIntervalIndex);
        handleProgressBarChanges();
    }

    private void handleProgressBarChanges() {
        progressBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    int roundedProgress = Math.round(progress);
                    roundedProgress = Math.min(Math.max(roundedProgress, 0), totalInterval);
                    circularSeekBar.setProgress(roundedProgress);
                    setTargetTime(roundedProgress * interval * 60);
                    updateTimeTextFromProgressBar(roundedProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) { }
        });
    }

    private void updateTimeTextFromProgressBar(int progress) {
        int totalMinutes = progress * interval;
        String timeString = String.format("%02d:00", totalMinutes);
        timeView.setText(timeString);
    }

    private Runnable createClockRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                updateTimeDisplay();
                if (running) {
                    if (clockSetting.getType() == ClockMode.STOPWATCH) {
                        handleStopwatchTick();
                    } else {
                        handleTimerTick();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
    }

    private void handleTimerTick() {
        seconds -= 60;
        if (seconds <= 0) {
            if (!clockSetting.getIsCountExceedTime()) {
                stop();
                reset();
                // Notify or vibrate when the timer reaches the limit
                notifyOrVibrate(context);
                redirectToCongratulationScreen();
            }
            else {
                isEndSession = true;
                updateStartButton("End Session", R.color.secondary_50);
            }
        }
    }

    private void handleStopwatchTick() {
        seconds+=60;
        if (clockSetting.getTargetTime() > 0 && seconds > clockSetting.getTargetTime()) {
            stop();
            reset();
            // Notify or vibrate when the timer reaches the limit
            notifyOrVibrate(context);
            redirectToCongratulationScreen();
        }
    }

    private void updateTimeDisplay() {
        int minutes = Math.abs(seconds) / 60;
        int secs = Math.abs(seconds) % 60;

        // Ensure that the maximum time is 120:00 (7200 seconds)
        if (minutes > 120) {
            minutes = 120;
            secs = 0;
        }

        String time;
        if (isEndSession && clockSetting.getIsCountExceedTime()) {
            time = String.format(Locale.getDefault(),
                    "-%02d:%02d", minutes, secs);
        } else {
            time = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, secs);
        }
        timeView.setText(time);
    }

    private Runnable createDeepModeRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                // print notification
                if (runningOutside && running) {
                    Log.d("ClockOutside", "Seconds:" + secondsOutside);
                    secondsOutside--;
                    if (secondsOutside < 0) {
                        runningOutside = false;
                        stop();
                        reset();
                        redirectToFailScreenActivity();
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
        handler.removeCallbacks(runnableClock);

        running = true;
        handler.post(runnableClock);
        Log.d("ClockTest", "start handler");
        updateStartButton("Give Up", R.color.secondary_50);
        progressBar.setDisablePointer(true);
        startForegroundService();
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(runnableClock);
        Log.d("ClockTest", "stop handler");
        updateStartButton("Plant", R.color.primary_20);
        stopForegroundService();
    }

    private void startForegroundService() {
        Intent serviceIntent = new Intent(context, ClockService.class);
        serviceIntent.putExtra("isTimer", clockSetting.getType() == ClockMode.TIMER);
        serviceIntent.putExtra("timeLimit", clockSetting.getTargetTime());
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    private void stopForegroundService() {
        Intent serviceIntent = new Intent(context, ClockService.class);
        context.stopService(serviceIntent);
    }

    public void reset() {
        seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? 0 : clockSetting.getTargetTime();
        progressBar.setDisablePointer(false);
    }

    public void giveUp() {
        stop();
        reset();
        int progressIntervalIndex = (clockSetting.getTargetTime() / 60) / interval;
        progressBar.setProgress(progressIntervalIndex);
        updateTimeTextFromProgressBar(progressIntervalIndex);
        redirectToFailScreenActivity();
    }

    public void setClockMode(ClockMode mClockMode) {
        this.clockSetting.setType(mClockMode);
        reset(); // Reset the clock when the mode changes
    }

    public void enableDeepModeCount() {
        runningOutside = true;
        secondsOutside = TIME_LIMIT_OUTSIDE;
        if (clockSetting.getIsDeepModeTimer() || clockSetting.getIsDeepModeStopwatch()) {
            deepModeHandler.post(runnableDeepMode);
        }
    }

    public void disableDeepModeCount() {
        runningOutside = false;
        deepModeHandler.removeCallbacks(runnableDeepMode);
    }

    public boolean getIsEndSession() {
        return isEndSession;
    }

    public void setIsEndSession(boolean isEndSession) {
        this.isEndSession = isEndSession;
    }

    public int getTargetTime() {
        return clockSetting.getTargetTime();
    }

    public void setTargetTime(int targetTime) {
        clockSetting.setTargetTime(targetTime);
        reset();
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

    private void updateStartButton(String text, int colorResId) {
        startButton.setText(text);
        startButton.setBackgroundTintList(ContextCompat.getColorStateList(startButton.getContext(), colorResId));
    }
}
