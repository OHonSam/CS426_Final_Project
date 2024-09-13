package com.hfad.cs426_final_project.ToDoScreen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class ToDoScreenActivity extends BaseScreenActivity {
    private int UPDATE_TASK_CODE = 10;
    private MyButton btnAddNewTask;
    private RecyclerView tasksDisplayRCV;
    private AppContext appContext;
    private TaskAdapter taskAdapter;
    private ClickableImageView btnFilter;
    private List<UserTask> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToggleColor(getResources().getColor(R.color.white));
        appContext = AppContext.getInstance();
        initWidgets();
        setupAddNewTaskButton();
        setupTasksDisplayRCV();
        setupFilterButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter.updateUserTasksList();
    }
    private void setupTasksDisplayRCV() {
        tasks = appContext.getCurrentUser().getOwnUserTasksList();
        // TODO: Filter tasks list based on filter options
        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.IClickEditListener() {
            @Override
            public void onClickEdit(UserTask userTask) {
                Intent intent = new Intent(ToDoScreenActivity.this, EditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_task", userTask);
                intent.putExtras(bundle);
                startActivityForResult(intent, UPDATE_TASK_CODE);
            }
        });
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
        btnFilter = findViewById(R.id.btnFilter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_screen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_TASK_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve the updated task from the Intent
            UserTask updatedTask = (UserTask) data.getSerializableExtra("updated_task");

            if (updatedTask != null) {
                updateTaskInList(updatedTask);
            }
        }
    }

    private void updateTaskInList(UserTask updatedTask) {
        // Find the task in the list by its ID and replace it
        for (int i = 0; i < tasks.size(); i++) {
            UserTask currentTask = tasks.get(i);
            if (currentTask.getId() == updatedTask.getId()) {
                tasks.set(i, updatedTask);
                taskAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToFilterTaskActivity();
            }
        });
    }

    private void redirectToFilterTaskActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Under development");
        builder.setMessage("Filter Task function is coming soon! Wait for the next version!");
        builder.show();
    }
}