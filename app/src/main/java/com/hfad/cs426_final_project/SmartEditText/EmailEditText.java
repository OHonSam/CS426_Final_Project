package com.hfad.cs426_final_project.SmartEditText;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hfad.cs426_final_project.R;

public class EmailEditText extends LinearLayout {

    private EditText emailEditText;
    private TextView emailErrorTextView;

    public EmailEditText(Context context) {
        super(context);
        init(context);
    }

    public EmailEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmailEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.edit_text_email, this, true);

        emailEditText = findViewById(R.id.emailEditText);
        emailErrorTextView = findViewById(R.id.emailErrorTextView);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validateEmail(String email) {
        if (email.contains("@") && email.contains(".")) {
            emailErrorTextView.setVisibility(View.GONE);
        } else {
            emailErrorTextView.setVisibility(View.VISIBLE);
        }
    }
    public EditText getEditText() {
        return emailEditText;
    }
}
