package com.hfad.cs426_final_project.SmartEditText;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hfad.cs426_final_project.R;

public class PasswordEditText extends LinearLayout {

    private EditText passwordEditText;
    private ImageView passwordToggle;
    private TextView passwordErrorTextView;
    private boolean isPasswordVisible = false;

    public PasswordEditText(Context context) {
        super(context);
        init(context);
    }

    public PasswordEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PasswordEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.edit_text_password, this, true);

        passwordEditText = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        passwordErrorTextView = findViewById(R.id.passwordErrorTextView);

        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordToggle.setImageResource(R.drawable.opened_eyes);

        passwordToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.opened_eyes);
        } else {
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.closed_eyes);
        }
        // Move cursor to the end
        passwordEditText.setSelection(passwordEditText.length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void validatePassword(String password) {
        if (password.length() < 6) {
            passwordErrorTextView.setVisibility(View.VISIBLE);
        } else {
            passwordErrorTextView.setVisibility(View.GONE);
        }
    }

    public EditText getEditText() {
        return passwordEditText;
    }
}
