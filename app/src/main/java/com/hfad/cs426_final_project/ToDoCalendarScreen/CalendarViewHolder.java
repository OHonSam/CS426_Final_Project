package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnDayClickListener listener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnDayClickListener listener) {
        super(itemView);
        this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.listener = listener;
    }

    public void bind(String day) {
        dayOfMonth.setText(day);
        // Enable click listener only if the day is not empty
        itemView.setOnClickListener(v -> {
            if (!day.isEmpty()) {
                listener.onDayClick(getAdapterPosition(), day);
            }
        });

    }
}
