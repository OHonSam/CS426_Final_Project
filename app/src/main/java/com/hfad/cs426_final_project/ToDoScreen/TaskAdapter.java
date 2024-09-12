package com.hfad.cs426_final_project.ToDoScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<UserTask> userTaskList; // current user task list based on filter options

    public TaskAdapter(List<UserTask> userTaskList) {
        this.userTaskList = userTaskList;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        UserTask userTask = userTaskList.get(position);
        holder.bind(userTask, position);
    }

    @Override
    public int getItemCount() {
        return userTaskList.size();
    }

    public void updateUserTasksList(List<UserTask> updatedTaskList) {
        userTaskList = updatedTaskList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskTitle, tvTaskStartDate, tvTaskEndDate, tvTaskStartTime, tvTaskEndTime, tvTaskLocation, tvTaskTag;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskStartDate = itemView.findViewById(R.id.tvTaskStartDate);
            tvTaskEndDate = itemView.findViewById(R.id.tvTaskEndDate);
            tvTaskStartTime = itemView.findViewById(R.id.tvTaskStartTime);
            tvTaskEndTime = itemView.findViewById(R.id.tvTaskEndTime);
            tvTaskLocation = itemView.findViewById(R.id.tvTaskLocation);
            tvTaskTag = itemView.findViewById(R.id.tvTaskTag);
        }

        public void bind(UserTask userTask, int position) {
            tvTaskTitle.setText(userTask.getTitle());
            tvTaskStartDate.setText(CalendarUtils.convertMillisToDate(userTask.getStartDate()));
            tvTaskEndDate.setText(CalendarUtils.convertMillisToDate(userTask.getEndDate()));
            tvTaskStartTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getStartTimeInMinutes()));
            tvTaskEndTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getEndTimeInMinutes()));
            tvTaskLocation.setText(userTask.getLocation() != null ? userTask.getLocation() : "None");
            tvTaskTag.setText("None"); // Placeholder for Tag if needed
        }
    }


}
