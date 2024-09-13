package com.hfad.cs426_final_project.ToDoScreen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.MainScreen.Tag.TagAdapterSpinner;
import com.hfad.cs426_final_project.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private ClickableImageView btnBack;
    private MyButton btnSaveTask;
    private EditText edtTaskTitle, edtTaskStartDate, edtTaskEndDate, edtTaskStartTime, edtTaskEndTime;
    private EditText edtTaskLocation, edtTaskDescription;
    private Spinner searchTagSpinner;
    private TagAdapterSpinner tagAdapterSpinner;
    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initWidgets();
        appContext = AppContext.getInstance();
        setupSearchTag();
        setupBackButton();
        setupSaveTaskButton();
        setupDatePickerDialog();
        setupTimePickerDialog();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        appContext.saveUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appContext.saveUserInfo();
    }

    private void setupSearchTag() {
        List<Tag> tagList = appContext.getCurrentUser().getOwnTags();
        tagAdapterSpinner = new TagAdapterSpinner(this, R.layout.item_tag_selected, tagList);
        searchTagSpinner.setAdapter(tagAdapterSpinner);
        searchTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appContext.getCurrentUser().setFocusTag(tagList.get(position));
                updateTagDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateTagDisplay() {
        TagAdapterSpinner tagAdapterSpinner = (TagAdapterSpinner) searchTagSpinner.getAdapter();
        int position = tagAdapterSpinner.getPosition(appContext.getCurrentUser().getFocusTag());
        searchTagSpinner.setSelection(position);
    }


    private void setupTimePickerDialog() {
        // When the user clicks on the Start Time EditText
        edtTaskStartTime.setOnClickListener(v -> showTimePickerDialog(edtTaskStartTime));

        // When the user clicks on the End Time EditText
        edtTaskEndTime.setOnClickListener(v -> showTimePickerDialog(edtTaskEndTime));
    }

    private void showTimePickerDialog(EditText editText) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddTaskActivity.this,
                (view, selectedHour, selectedMinute) -> {
                    // Format time as HH:mm and set it to the EditText
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    editText.setText(selectedTime);
                },
                hour, minute, true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void setupDatePickerDialog() {
        // When the user clicks on the Start Date EditText
        edtTaskStartDate.setOnClickListener(v -> showDatePickerDialog(edtTaskStartDate));

        // When the user clicks on the End Date EditText
        edtTaskEndDate.setOnClickListener(v -> showDatePickerDialog(edtTaskEndDate));
    }

    private void showDatePickerDialog(EditText editText) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format date as dd/MM/yyyy and set it to the EditText
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void setupSaveTaskButton() {
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskAndFinish();
            }
        });
    }

    private void saveTaskAndFinish() {
        if (!validateInputFields()){
            return;
        }

        String title = edtTaskTitle.getText().toString();
        String startDate = edtTaskStartDate.getText().toString();
        String endDate = edtTaskEndDate.getText().toString();
        String startTime = edtTaskStartTime.getText().toString();
        String endTime = edtTaskEndTime.getText().toString();
        String location = edtTaskLocation.getText().toString();
        String description = edtTaskDescription.getText().toString();

        long startDateMillis = CalendarUtils.convertDateToMillis(startDate);
        long endDateMillis = CalendarUtils.convertDateToMillis(endDate);
        long startTimeInMinutes = CalendarUtils.convertTimeToMinutes(startTime);
        long endTimeInMinutes = CalendarUtils.convertTimeToMinutes(endTime);

        if (!isValidDateTime(startDateMillis, endDateMillis, startTimeInMinutes, endTimeInMinutes)) {
            return;
        }

        Tag selectedTag = tagAdapterSpinner.getSelectedTag();

        UserTask newUserTask = new UserTask(getNextTaskId(), title, startDateMillis, endDateMillis, startTimeInMinutes, endTimeInMinutes, selectedTag);
        appContext.getCurrentUser().getOwnUserTasksList().add(newUserTask);
        finish();
    }

    private boolean isValidDateTime(long startDateMillis, long endDateMillis, long startTimeInMinutes, long endTimeInMinutes) {
        if (startDateMillis > endDateMillis ||
                (startDateMillis == endDateMillis && startTimeInMinutes > endTimeInMinutes)) {
            edtTaskEndDate.setError("End date/time must be after start date/time");
            return false;
        }
        return true;
    }

    private int getNextTaskId() {
        List<UserTask> taskList = appContext.getCurrentUser().getOwnUserTasksList();
        int highestId = -1;
        for (UserTask task : taskList) {
            if (highestId < task.getId()){
                highestId = task.getId();
            }
        }
        return highestId + 1;
    }

    private boolean validateInputFields() {
        if (isEmpty(edtTaskTitle, "Please enter a title")) return false;
        if (isEmpty(edtTaskStartDate, "Please enter a start date")) return false;
        if (isEmpty(edtTaskEndDate, "Please enter an end date")) return false;
        if (isEmpty(edtTaskStartTime, "Please enter a start time")) return false;
        return !isEmpty(edtTaskEndTime, "Please enter an end time");
    }

    private boolean isEmpty(EditText field, String errorMsg) {
        if (field.getText().toString().isEmpty()) {
            field.setError(errorMsg);
            return true;
        }
        return false;
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void initWidgets() {
        btnBack = findViewById(R.id.btnBack);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtTaskStartDate = findViewById(R.id.edtTaskStartDate);
        edtTaskEndDate = findViewById(R.id.edtTaskEndDate);
        edtTaskStartTime = findViewById(R.id.edtTaskStartTime);
        edtTaskEndTime = findViewById(R.id.edtTaskEndTime);
        searchTagSpinner = findViewById(R.id.search_tag_spinner);
        edtTaskDescription = findViewById(R.id.edtTaskDescription);
        edtTaskLocation = findViewById(R.id.edtTaskLocation);
    }
}