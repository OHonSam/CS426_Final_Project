package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hfad.cs426_final_project.R;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourTask> {
    public HourAdapter(@NonNull Context context, List<HourTask> hourTasksList) {
        super(context, 0, hourTasksList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourTask hourTask = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);
        }

        setHour(convertView, hourTask.getTime());
        setEvent(convertView, hourTask.getHourTasksList());
        return convertView;
    }

    private void setEvent(View convertView, ArrayList<Task> tasksList) {
        TextView tvTask1 = convertView.findViewById(R.id.task1);
        TextView tvTask2 = convertView.findViewById(R.id.task2);
        TextView tvTask3 = convertView.findViewById(R.id.task3);
        if (tasksList.isEmpty()) {
            hideTask(tvTask1);
            hideTask(tvTask2);
            hideTask(tvTask3);
        } else if (tasksList.size() == 1) {
            displayTask(tvTask1, tasksList.get(0));
            hideTask(tvTask2);
            hideTask(tvTask3);
        } else if (tasksList.size() == 2) {
            displayTask(tvTask1, tasksList.get(0));
            displayTask(tvTask2, tasksList.get(1));
            hideTask(tvTask3);
        } else if (tasksList.size() == 3) {
            displayTask(tvTask1, tasksList.get(0));
            displayTask(tvTask2, tasksList.get(1));
            displayTask(tvTask3, tasksList.get(2));
        } else {
            displayTask(tvTask1, tasksList.get(0));
            displayTask(tvTask2, tasksList.get(1));
            tvTask3.setText("+ " + (tasksList.size() - 2) + " more events");
        }

    }

    private void displayTask(TextView tvTask, Task task) {
        tvTask.setVisibility(View.VISIBLE);
        tvTask.setText(task.getTitle());
    }

    private void hideTask(TextView tvTask) {
        tvTask.setVisibility(View.GONE);
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.tvTime);
        timeTV.setText(CalendarUtils.getFormattedShortTime(time));
    }
}
