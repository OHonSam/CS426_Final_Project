package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.time.LocalTime;

public class TaskEditActivity extends AppCompatActivity {
    private MyButton btnSaveTask;
    private EditText edtTaskTitle, edtTaskDate, edtTaskTime;
    private LocalTime time;
    private ClickableImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        initWidgets();
        setupBackButton();
        setupSaveButton();
        time = LocalTime.now();
        edtTaskDate.setText("Date: " + CalendarUtils.getFormattedDate(CalendarUtils.selectedDate));
        edtTaskTime.setText("Time: " + CalendarUtils.getFormattedTime(time));
    }

    private void setupSaveButton() {
        btnSaveTask.setOnClickListener(v -> {
            if (!edtTaskTitle.getText().toString().isEmpty()) {
                Task event = new Task(CalendarUtils.selectedDate, time, edtTaskTitle.getText().toString());
                Task.tasksList.add(event);
            }
            finish();
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task newTask = new Task(CalendarUtils.selectedDate, time, edtTaskTitle.getText().toString());
                Task.tasksList.add(newTask);
                finish();
            }
        });
    }

    private void initWidgets() {
        btnBack = findViewById(R.id.btnBack);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        edtTaskDate = findViewById(R.id.edtTaskDate);
        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtTaskTime = findViewById(R.id.edtTaskTime);
    }
}