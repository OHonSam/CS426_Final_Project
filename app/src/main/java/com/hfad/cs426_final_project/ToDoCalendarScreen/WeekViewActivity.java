package com.hfad.cs426_final_project.ToDoCalendarScreen;

import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.generateDaysInWeekArray;
import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.getMonthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnDayClickListener {
    private ImageButton arrowBackwards, arrowForwards;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private MyButton btnAddNewEvents;
    private ListView listViewTasks;
    private ClickableImageView btnBack;
    private MyButton btnSwitchDailyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();

        setupBackButton();
        setupCalendarArrowListener();
        setWeekView();
        setupAddNewEventsButton();
        setupSwitchDailyModeListener();
    }

    private void setupSwitchDailyModeListener() {
        btnSwitchDailyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDailyMode();
            }
        });
    }

    private void switchDailyMode() {
        Intent intent = new Intent(this, DailyCalendarActivity.class);
        startActivity(intent);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTaskAdapter();
    }

    private void setTaskAdapter() {
        ArrayList<Task> tasksForDate = Task.getTasksForDate(CalendarUtils.selectedDate);
        TaskAdapter taskAdapter = new TaskAdapter(this, tasksForDate);
        listViewTasks.setAdapter(taskAdapter);
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

    private void setWeekView() {
        monthYearText.setText(getMonthYearFromDate(CalendarUtils.selectedDate));

        ArrayList<LocalDate> daysInMonth = generateDaysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setTaskAdapter();
    }

    private void setupCalendarArrowListener() {
        arrowBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWeekAction(v);
            }
        });
        arrowForwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeekAction(v);
            }
        });
    }

    private void nextWeekAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    private void previousWeekAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void initWidgets() {
        monthYearText = findViewById(R.id.tvMonthYear_WeekView);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        arrowBackwards = findViewById(R.id.arrowBackwards_WeekView);
        arrowForwards = findViewById(R.id.arrowForwards_WeekView);
        btnAddNewEvents = findViewById(R.id.btnAddNewEvent);
        listViewTasks = findViewById(R.id.listViewTasks);
        btnBack = findViewById(R.id.btnBack);
        btnSwitchDailyMode = findViewById(R.id.btnSwitchDailyMode);
    }

    @Override
    public void onDayClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setWeekView();
        }
    }
}