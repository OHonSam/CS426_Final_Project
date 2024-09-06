package com.hfad.cs426_final_project.MainScreen;

import android.os.Bundle;
import android.view.View;
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

    String chosenColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tag);
        appContext = AppContext.getInstance();

        initUI();
        setupColorPicker();
        initListener();
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
                chosenColor = "#" + envelope.getHexCode();
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
                    if(!appContext.getCurrentUser().hasTag(tagName)) {
                        Toast.makeText(v.getContext(), "Tag added: " + tagName, Toast.LENGTH_SHORT).show();
                        // tag logic
                        Tag newTag = new Tag(appContext.getCurrentUser().getOwnTags().size(), tagName, chosenColor);
                        appContext.getCurrentUser().getOwnTags().add(newTag);
                        appContext.getCurrentUser().setFocusTag(newTag);
                        // back to mainscreen activity
                        finish();
                    }
                    else {
                        Toast.makeText(v.getContext(), "This tag has existed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Please enter a tag name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}