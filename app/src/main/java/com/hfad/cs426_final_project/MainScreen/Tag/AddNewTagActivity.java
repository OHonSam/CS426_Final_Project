package com.hfad.cs426_final_project.MainScreen.Tag;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class AddNewTagActivity extends AppCompatActivity {
    AppContext appContext;
    MyButton btnCancel, btnAdd;
    ImageView ivCancel;
    EditText edtTagName;
    ColorPickerView colorPickerView;
    View chosenColorView;
    int chosenColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tag);
        appContext = AppContext.getInstance();

        initUI();
        setupColorPicker();
        initListener();
    }

    // Lose focus for EditText when touching outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideSoftKeyboard(v); // Hide keyboard if EditText loses focus
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Helper method to hide the keyboard
    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initUI() {
        btnCancel = findViewById(R.id.btnCancel);
        btnAdd = findViewById(R.id.btnAdd);
        ivCancel = findViewById(R.id.ivCancel);
        edtTagName = findViewById(R.id.edtTagName);
        colorPickerView = findViewById(R.id.colorPickerView);
        chosenColorView = findViewById(R.id.chosenColorView);
    }

    private void setupColorPicker() {
        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                chosenColorView.setBackgroundColor(envelope.getColor());
                chosenColor = envelope.getColor();
            }
        });
    }

    private void initListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        btnCancel.setOnClickListener(listener);
        ivCancel.setOnClickListener(listener);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = edtTagName.getText().toString().trim();
                if (!tagName.isEmpty()) {
                    if (!appContext.getCurrentUser().hasTag(tagName)) {
                        Toast.makeText(v.getContext(), "Tag added: " + tagName, Toast.LENGTH_SHORT).show();
                        // Add the new tag logic
                        Tag newTag = new Tag(appContext.getCurrentUser().getOwnTags().size(), tagName, chosenColor);
                        appContext.getCurrentUser().getOwnTags().add(newTag);
                        appContext.getCurrentUser().setFocusTag(newTag);
                        finish();
                    } else {
                        Toast.makeText(v.getContext(), "This tag already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Please enter a tag name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
