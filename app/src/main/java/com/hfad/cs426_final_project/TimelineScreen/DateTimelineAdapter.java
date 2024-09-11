package com.hfad.cs426_final_project.TimelineScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DateTimelineAdapter extends RecyclerView.Adapter<DateTimelineAdapter.DateTimelineHolder> {
    private List<LocalDateTime> dates;
    private List<List<Session>> sessions;

    public DateTimelineAdapter(List<LocalDateTime> dates, List<List<Session>> sessions) {
        this.dates = dates;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public DateTimelineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_date, parent, false);
        return new DateTimelineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTimelineHolder holder, int position) {
        LocalDateTime date = dates.get(position);

        // Use DateTimeFormatter to format LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
        holder.tvDate.setText(date.format(formatter));

        // Get the sessions for the current date
        List<Session> sessionList = sessions.get(position);

        // Set up the RecyclerView for session details
        SessionDetailsAdapter sessionDetailsAdapter = new SessionDetailsAdapter(sessionList);
        holder.rcvSessionDetails.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rcvSessionDetails.setAdapter(sessionDetailsAdapter);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateTimelineHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView rcvSessionDetails;

        public DateTimelineHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            rcvSessionDetails = itemView.findViewById(R.id.rcvSessionDetails);
        }
    }
}
