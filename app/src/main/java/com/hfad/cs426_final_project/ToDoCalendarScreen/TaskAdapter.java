package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hfad.cs426_final_project.R;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(@NonNull Context context, List<Task> tasksList) {
        super(context, 0, tasksList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_cell, parent, false);
        }
        TextView tvTaskCell = convertView.findViewById(R.id.tvTaskCell);
        assert task != null;
        tvTaskCell.setText(task.getTitle() + " " + CalendarUtils.getFormattedTime(task.getTime()));
        return convertView;
    }
}
