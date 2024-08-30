package com.hfad.cs426_final_project.CustomUIComponent;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;



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
   private void init() {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int color;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
