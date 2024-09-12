package com.hfad.cs426_final_project.ToDoScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

public class ToDoScreenActivity extends BaseScreenActivity {
    private MyButton btnAddNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToggleColor(getResources().getColor(R.color.white));
        initWidgets();
        setupAddNewTaskButton();
    }

    private void setupAddNewTaskButton() {
        btnAddNewTask.setOnClickListener(v -> {
            redirectToAddTaskActivity();
        });
    }

    private void redirectToAddTaskActivity() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    private void initWidgets() {
        btnAddNewTask = findViewById(R.id.btnAddNewTask);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_screen;
    }
}