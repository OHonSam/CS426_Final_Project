package com.hfad.cs426_final_project.ToDoCalendarScreen;

import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity {
    private ImageButton arrowBackwards, arrowForwards;
    private TextView monthDayText, dayOfWeekText;
    private MyButton btnAddNewEvents;
    private ListView listViewHours;
    private ClickableImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        initWidgets();
        setupBackButton();
        setupCalendarArrowListener();
        setupAddNewEventsButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDayView();
    }

    private void updateDayView() {
        monthDayText.setText(CalendarUtils.getMonthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        dayOfWeekText.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(this, getHourTasksList());
        listViewHours.setAdapter(hourAdapter);
    }

    private ArrayList<HourTask> getHourTasksList() {
        ArrayList<HourTask> hourTasksList = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Task> tasksList = Task.getTasksForDateAndTime(selectedDate, time);
            HourTask hourTask = new HourTask(time, tasksList);
            hourTasksList.add(hourTask);
        }

        return hourTasksList;
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupAddNewEventsButton() {
        btnAddNewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToEventEditActivity();
            }
        });
    }

    private void redirectToEventEditActivity() {
        Intent intent = new Intent(this, TaskEditActivity.class);
        startActivity(intent);
    }

    private void setupCalendarArrowListener() {
        arrowBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousDayAction(v);
            }
        });
        arrowForwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDayAction(v);
            }
        });
    }

    private void nextDayAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        updateDayView();
    }

    private void previousDayAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        updateDayView();
    }

    private void initWidgets() {
        arrowBackwards = findViewById(R.id.arrowBackwards_DailyCalendarView);
        arrowForwards = findViewById(R.id.arrowForwards_DailyCalendarView);
        btnAddNewEvents = findViewById(R.id.btnAddNewEvent);
        listViewHours = findViewById(R.id.listViewHours);
        btnBack = findViewById(R.id.btnBack);
        monthDayText = findViewById(R.id.tvMonthDay_DailyCalendarView);
        dayOfWeekText = findViewById(R.id.tvDayOfWeek_DailyCalendarView);
    }
}