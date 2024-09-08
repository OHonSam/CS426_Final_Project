package com.hfad.cs426_final_project.CustomUIComponent;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.hfad.cs426_final_project.R;

import java.util.Objects;

public class MyButton extends AppCompatButton {

    private Drawable originalDrawable;

    public MyButton(Context context) {
        super(context);
        init();
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
   private void init() {

        // Store the original drawable
        //originalDrawable = DrawableCompat.wrap(getBackground()).mutate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int color;
        switch (event.getAction()) {
            //case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_DOWN:
                // Change tint color when the button is pressed
                //DrawableCompat.setTint(originalDrawable, ContextCompat.getColor(getContext(), R.color.ripple_effect));
                this.getBackground().setAlpha(128);
                color = getCurrentTextColor();
                this.setTextColor(Color.argb(128, Color.red(color),Color.green(color),Color.blue(color)));
                break;

            //case MotionEvent.ACTION_UP:
            //case MotionEvent.ACTION_CANCEL:
            default:
                // Reset to the original tint color when the button is released
                //DrawableCompat.setTintList(originalDrawable, null); // Reset to the original tint
                this.getBackground().setAlpha(255);
                color = getCurrentTextColor();
                this.setTextColor(Color.argb(255, Color.red(color),Color.green(color),Color.blue(color)));
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setActivate() {
        this.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        this.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
    }

    public void setInActivate() {
        this.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_70, null));
        this.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    }
}
