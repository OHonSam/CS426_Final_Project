package com.hfad.cs426_final_project.MainScreen.Clock;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SwitchMode{
    onModeClickListener onModeClickListener;
    ImageView timer, stopwatch;
    public ImageView thumb;
    ImageView track;
    int selectedMode; // 0: timer ; 1: stopwatch

    public SwitchMode(ImageView timer, ImageView stopwatch, ImageView thumb, ImageView track) {
        this.timer = timer;
        this.stopwatch = stopwatch;
        this.thumb = thumb;
        this.track = track;
        this.selectedMode = 0;
        this.onModeClickListener = null;
        setupOnClickListener();
    }

    private void setupOnClickListener() {
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTranslateAnimation(selectedMode,timer);
                selectedMode = 0;
                onModeClickListener.onModeClick(selectedMode);
            }
        });

        stopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTranslateAnimation(selectedMode,stopwatch);
                selectedMode = 1;
                onModeClickListener.onModeClick(selectedMode);
            }
        });
    }

    private void startTranslateAnimation(int selectedMode, View destination){

        float thumb_x = thumb.getX();
        float thumb_y = thumb.getY();

        Log.d("Thumb", "thumb_x: " + thumb_x + " thumb_y: " + thumb_y);

        int[] absolute_coordinate_source = new int[2];
        View source;
        if(selectedMode == 0){
            source = timer;
        }
        else
            source = stopwatch;

        source.getLocationOnScreen(absolute_coordinate_source);
        int x_source = absolute_coordinate_source[0];
        int y_source = absolute_coordinate_source[1];

        int[] absolute_coordinate_destination = new int[2];
        destination.getLocationOnScreen(absolute_coordinate_destination);
        int x_des = absolute_coordinate_destination[0];
        int y_des = absolute_coordinate_destination[1];

        float delta = x_des-x_source;
        Log.d("Translate Animation", "x_des-x_source: " + delta);

        TranslateAnimation animation = new TranslateAnimation(0,delta,0,0);
        animation.setDuration(300);
        animation.setFillAfter(true);

        // Set the animation listener to update the X position after the animation ends
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation start callback
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation end callback
                // Update the X coordinate of the thumb view
                thumb.setX(thumb.getX() + delta);
                Log.d("Thumb", "thumb_x_after: " + thumb.getX() + " thumb_y_after: " + thumb_y);
                // Optionally, clear the animation to reset any visual artifacts
                thumb.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat callback
            }
        });
        thumb.startAnimation(animation);

    }

    public void addSwitchListener(onModeClickListener onModeClickListener){
        this.onModeClickListener = onModeClickListener;
    }
}
