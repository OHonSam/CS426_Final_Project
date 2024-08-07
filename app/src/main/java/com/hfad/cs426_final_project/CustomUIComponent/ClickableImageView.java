package com.hfad.cs426_final_project.CustomUIComponent;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ClickableImageView extends AppCompatImageView {

    public ClickableImageView(Context context) {
        super(context);
    }

    public ClickableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
            getDrawable().setColorFilter(0x44000000, PorterDuff.Mode.SRC_ATOP);
            invalidate();
        } else {
            getDrawable().clearColorFilter();
            invalidate();
        }
    }
}
