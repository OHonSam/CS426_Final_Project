package com.hfad.cs426_final_project.CustomUIComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ClickableImageView extends AppCompatImageView {

    public ClickableImageView(Context context) {
        super(context);
    }

    public ClickableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d("debug","Enter constructor");

    }

    public ClickableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
        public void setPressed(boolean pressed) {

        super.setPressed(pressed);

        if (getDrawable() == null)
            return;

        if (pressed) {
            getDrawable().setTint(0x44000000);
            invalidate();
        } else {
            getDrawable().setTintList(null);
            invalidate();
        }
    }
}
