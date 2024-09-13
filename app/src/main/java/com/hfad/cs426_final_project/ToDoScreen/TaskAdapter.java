package com.hfad.cs426_final_project.ToDoScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card,parent, false);
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

    public void updateUserTasksList() {
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskStartDate, tvTaskEndDate, tvTaskStartTime, tvTaskEndTime, tvTaskLocation, tvTaskTag;
        ClickableImageView btnDeleteTask;
        CheckBox btnCheckComplete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskStartDate = itemView.findViewById(R.id.tvTaskStartDate);
            tvTaskEndDate = itemView.findViewById(R.id.tvTaskEndDate);
            tvTaskStartTime = itemView.findViewById(R.id.tvTaskStartTime);
            tvTaskEndTime = itemView.findViewById(R.id.tvTaskEndTime);
            tvTaskLocation = itemView.findViewById(R.id.tvTaskLocation);
            tvTaskTag = itemView.findViewById(R.id.tvTaskTag);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
            btnCheckComplete = itemView.findViewById(R.id.btnCheckComplete);
        }

        public void bind(UserTask userTask, int position) {
            tvTaskTitle.setText(userTask.getTitle());
            tvTaskStartDate.setText(CalendarUtils.convertMillisToDate(userTask.getStartDate()));
            tvTaskEndDate.setText(CalendarUtils.convertMillisToDate(userTask.getEndDate()));
            tvTaskStartTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getStartTimeInMinutes()));
            tvTaskEndTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getEndTimeInMinutes()));
            tvTaskLocation.setText(userTask.getLocation() != null ? userTask.getLocation() : "None");
            tvTaskTag.setText(userTask.getTag().getName()); // Placeholder for Tag if needed
            btnCheckComplete.setChecked(userTask.getIsComplete());

            btnDeleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a confirmation dialog to delete the task
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Task")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                // Remove the task from the list and notify the adapter
                                userTaskList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, userTaskList.size());

                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });

            btnCheckComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle the isComplete status of the task
                    userTask.setIsComplete(btnCheckComplete.isChecked());
                    notifyItemChanged(position);
                }
            });
        }
    }


}
