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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.MainScreen.DoneSessionActivities.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;
import com.hfad.cs426_final_project.StreakManager;

import java.util.Locale;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Clock {
    public static enum ClockMode {
        STOPWATCH,
        TIMER
    }

    public static enum SessionStatus{
        COMPLETE,
        GIVE_UP,
        NON_FOCUS;
    }

    private static final double COEFFICIENT_SUN = 0.3;
    private OnClockListener onClockListener;

    private static final String CHANNEL_ID = "clock_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private final int TIME_LIMIT_OUTSIDE = 10;
    private final int TOTAL_PROGRESS_INTERVAL = 24;
    private final int PROGRESS_INTERVAL_LEN = 5;
    private final int PROGRESS_MINUTES_MIN = 10;
    private final int PROGRESS_MINUTES_MAX = 120;


    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    //Number of seconds displayed on the stopwatch.
    private int seconds;
    //Is the stopwatch running?
    private boolean running;
    private boolean isEndSession;
    private int secondsOutside;
    private boolean runningOutside = false;

    private final Handler countTimeHandler;
    private Runnable runnableClock;
    private final Handler deepModeHandler;
    private Runnable runnableDeepMode;
    private final Handler breakSessionHandler;
    private Runnable runnableBreakSession;

    public TextView setTimeView(TextView timeView) {
        return this.timeView = timeView;
    }

    private TextView timeView;
    private MyButton startButton;
    private Context context;
    private MainScreenActivity mainScreenActivity;

    private ClockSetting clockSetting;
    private CircularSeekBar progressBar;
    private ImageView toggleIcon;

    public Clock(MainScreenActivity mainScreenActivity, TextView timeView, MyButton startButton, ClockSetting clockSetting, CircularSeekBar progressBar, ImageView toggleIcon, OnClockListener onClockListener) {
        this.context = mainScreenActivity;
        this.mainScreenActivity = mainScreenActivity;
        this.timeView = timeView;
        this.startButton = startButton;
        this.clockSetting = clockSetting;
        this.seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? 0 : clockSetting.getTargetTime();
        this.isEndSession = false;
        this.countTimeHandler = new Handler();
        this.deepModeHandler = new Handler();
        this.runnableClock = createClockRunnable();
        this.runnableDeepMode = createDeepModeRunnable();
        this.breakSessionHandler = new Handler();
        this.runnableBreakSession = createBreakSessionRunnable();
        this.progressBar = progressBar;
        this.toggleIcon = toggleIcon;
        this.onClockListener = onClockListener;
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
                    saveSession(true);
                    completeSession(getTargetTime(),SessionStatus.COMPLETE);
                    //onClockListener.redirectToCongratulationScreenActivity();
                    return;
                }

                if (!isRunning()) {
                    // Start the clock and disable interaction
                    start();
                    clockSetting.setModePickerDialogEnabled(false); // DISABLE the clock
                    mainScreenActivity.disableWhenFocus();
                } else {
                    giveUp();
                }
            }
        });
    }

    private void completeSession(int targetTime, SessionStatus status) {
        int rewards = 0;
        if(status == SessionStatus.COMPLETE){
            rewards = (int) ((double) targetTime / 60 * COEFFICIENT_SUN);
            AppContext.getInstance().getCurrentUser().updateSun(rewards);
            AppContext.getInstance().getCurrentUser().getStreakManager().markSessionComplete();
            onClockListener.redirectToCongratulationScreenActivity(rewards);
        }
        else if (status == SessionStatus.GIVE_UP){
            // Retrieve the string from strings.xml using the context
            String message = context.getString(R.string.reason_why_tree_withered_give_up);
            onClockListener.redirectToFailScreenActivity(message,rewards);
        }
        else if(status == SessionStatus.NON_FOCUS){
            // Retrieve the string from strings.xml using the context
            String message = context.getString(R.string.reason_why_tree_withered_non_focus);
            onClockListener.redirectToFailScreenActivity(message,rewards);
        }
    }

    private void redirectToCongratulationScreen() {
        Intent intent = new Intent(context, CongratulationScreenActivity.class);
        context.startActivity(intent);
    }

//    private void redirectToFailScreenActivity() {
//        ActivityResultLauncher<Intent> failScreenLauncher
//        // TODO: Redirect to FailScreenActivity
//        Intent intent = new Intent(context, FailScreenActivity.class);
//
//    }

    private void initProgressBar() {
        progressBar.setMax(TOTAL_PROGRESS_INTERVAL);
        int initialTargetMinutes = clockSetting.getTargetTime() / 60;
        int initialProgressIntervalIndex = initialTargetMinutes / PROGRESS_INTERVAL_LEN;

        // Ensure that the initial progress is at least the minimum interval
        if (initialProgressIntervalIndex < (PROGRESS_MINUTES_MIN / PROGRESS_INTERVAL_LEN)) {
            initialProgressIntervalIndex = PROGRESS_MINUTES_MIN / PROGRESS_INTERVAL_LEN;
        }

        progressBar.setProgress(initialProgressIntervalIndex);
        updateTimeTextFromProgressBar(initialProgressIntervalIndex);
        handleProgressBarChanges();
    }


    private void handleProgressBarChanges() {
        progressBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    int roundedProgress = Math.round(progress);
                    // Ensure progress value within the allowed range (from min to max interval index).
                    roundedProgress = Math.max(Math.min(roundedProgress, (PROGRESS_MINUTES_MAX / PROGRESS_INTERVAL_LEN)), (PROGRESS_MINUTES_MIN / PROGRESS_INTERVAL_LEN));
                    circularSeekBar.setProgress(roundedProgress);
                    setTargetTime(roundedProgress * PROGRESS_INTERVAL_LEN * 60);
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
        int totalMinutes = progress * PROGRESS_INTERVAL_LEN;
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

                    // Post the next tick only if still running
                    if (running) {
                        countTimeHandler.postDelayed(this, 1000);
                    }
                }
            }
        };
    }

    private Runnable createBreakSessionRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                updateTimeDisplay();
                if (running) {
                    seconds -= 1;
                    Log.d("BreakScreenActivity",""+seconds);
                    if (seconds < 0) {
                        running = false;
                        breakSessionCompleteListener.onBreakSessionComplete();
                    }
                    // Post the next tick only if still running
                    if (running) {
                        breakSessionHandler.postDelayed(this, 1000);
                    }
                }
            }
        };
    }

    private void handleTimerTick() {
        seconds -= 1;
        if (seconds < 0) {
            if (!clockSetting.getIsCountExceedTime()) {
                stop();
                saveSession(true);
                //reset();
                // Notify or vibrate when the timer reaches the limit
                notifyOrVibrate(context);

                completeSession(getTargetTime(),SessionStatus.COMPLETE);
//                onClockListener.redirectToCongratulationScreenActivity();
            }
            else {
                isEndSession = true;
                updateStartButton("End Session", R.color.secondary_50);
            }
        }
    }

    private void handleStopwatchTick() {
        seconds += 1;
        if (clockSetting.getTargetTime() > 0 && seconds > clockSetting.getTargetTime()) {
            stop();
            saveSession(true);
            //reset();
            // Notify or vibrate when the timer reaches the limit
            notifyOrVibrate(context);
            completeSession(getTargetTime(),SessionStatus.COMPLETE);
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
                    secondsOutside--;
                    Log.d("Outside",""+secondsOutside);
                    if (secondsOutside < 0) {
                        runningOutside = false;
                        stop();
                        saveSession(false);
                        //reset();

                        completeSession(getTargetTime(),SessionStatus.NON_FOCUS);
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
        // Ensure no previous running clock
        countTimeHandler.removeCallbacks(runnableClock);
        running = true;
        countTimeHandler.post(runnableClock);

        toggleIcon.setVisibility(View.GONE);

        updateStartButton("Give Up", R.color.secondary_50);
        progressBar.setDisablePointer(true);
        //startForegroundService();
    }

    public void stop() {
        running = false;
        countTimeHandler.removeCallbacks(runnableClock);
        updateStartButton("Plant", R.color.primary_20);
        stopForegroundService();
    }

    private void startForegroundService() {
        if (clockSetting.getTargetTime() > 0) {
            Intent serviceIntent = new Intent(context, ClockService.class);
            serviceIntent.putExtra("isTimer", clockSetting.getType() == ClockMode.TIMER);
            serviceIntent.putExtra("timeLimit", clockSetting.getTargetTime());
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            Log.d("ClockService", "Target time is 0, not starting the service.");
        }
    }


    private void stopForegroundService() {
        Intent serviceIntent = new Intent(context, ClockService.class);
        context.stopService(serviceIntent);
    }

    public void reset() {
        seconds = (clockSetting.getType() == ClockMode.STOPWATCH) ? 0 : clockSetting.getTargetTime();
        progressBar.setDisablePointer(false);

        int progressIntervalIndex = (clockSetting.getTargetTime() / 60) / PROGRESS_INTERVAL_LEN;
        progressBar.setProgress(progressIntervalIndex);
        updateTimeTextFromProgressBar(progressIntervalIndex);


        toggleIcon.setVisibility(View.VISIBLE);

        // Ensure the clock is enabled after reset
        clockSetting.setModePickerDialogEnabled(true);
    }


    public void giveUp() {
        stop();
        saveSession(false);
        //reset();

        completeSession(getTargetTime(),SessionStatus.GIVE_UP);
    }

    public void setClockMode(ClockMode mClockMode) {
        this.clockSetting.setType(mClockMode);
        reset(); // Reset the clock when the mode changes
    }

    public void enableDeepModeCount() {
        Log.d("Outside","enableDeepModeCount triggered");
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

    private void saveSession(boolean isComplete) {
        User user = AppContext.getInstance().getCurrentUser();
        if(isComplete) {
            // Reward
            user.addSelectedBlock();
        }
        Session session = new Session();
        session.setId(user.getSessions().size());
        session.setStatus(isComplete);
        int duration = clockSetting.getTargetTime();

        if (!isComplete) {
            duration = seconds;
        } else if(clockSetting.getType() == ClockMode.TIMER && clockSetting.getIsCountExceedTime() && isEndSession) {
            duration = clockSetting.getTargetTime() - seconds;
        }

        session.setDuration(duration);
        session.setTimestamp(System.currentTimeMillis());
        session.setTree(user.getUserSetting().getSelectedTree());
        session.setTag(user.getFocusTag());
        session.setBlock(user.getSelectedBlock()); // ồn quá có j nói to lên nha
        user.getSessions().add(session);
    }

    public void enableBreakSessionCount(){
        Log.d("BreakScreenActivity","enableBreakSessionCount triggered");
        running = true;
        if(runnableBreakSession != null) {
            breakSessionHandler.removeCallbacks(runnableBreakSession);
        }
        breakSessionHandler.post(runnableBreakSession);
    }

    public void disableBreakSessionCount(){
        Log.d("BreakScreenActivity","enableBreakSessionCount triggered");
        if(runnableBreakSession != null) {
            breakSessionHandler.removeCallbacks(runnableBreakSession);
        }
    }

    // Define the listener interface
    public interface OnBreakSessionCompleteListener {
        void onBreakSessionComplete();
    }

    private OnBreakSessionCompleteListener breakSessionCompleteListener;

    // Provide a method to set the listener
    public void setOnBreakSessionCompleteListener(OnBreakSessionCompleteListener listener) {
        this.breakSessionCompleteListener = listener;
    }

}