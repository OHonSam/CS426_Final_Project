package com.hfad.cs426_final_project.ToDoScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class ToDoScreenActivity extends BaseScreenActivity {
    private MyButton btnAddNewTask;
    private RecyclerView tasksDisplayRCV;
    private AppContext appContext;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToggleColor(getResources().getColor(R.color.white));
        appContext = AppContext.getInstance();
        initWidgets();
        setupAddNewTaskButton();
        setupTasksDisplayRCV();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the updated task list
        List<UserTask> updatedTaskList = appContext.getCurrentUser().getOwnUserTasksList();
        // Update the adapter's task list and notify that the data has changed
        taskAdapter.updateUserTasksList(updatedTaskList);
    }

    private void setupTasksDisplayRCV() {
        List<UserTask> tasks = appContext.getCurrentUser().getOwnUserTasksList();
        // TODO: Filter tasks list based on filter options
        taskAdapter = new TaskAdapter(tasks);
        tasksDisplayRCV.setLayoutManager(new LinearLayoutManager(this));
        tasksDisplayRCV.setAdapter(taskAdapter);
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
        tasksDisplayRCV = findViewById(R.id.TasksDisplayRCV);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_screen;
    }
}