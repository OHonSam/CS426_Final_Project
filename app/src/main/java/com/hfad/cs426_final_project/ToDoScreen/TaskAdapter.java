package com.hfad.cs426_final_project.ToDoScreen;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<UserTask> userTaskList; // current user task list based on filter options
    private IClickEditListener listener;

    public TaskAdapter(List<UserTask> userTaskList, IClickEditListener listener) {
        this.userTaskList = userTaskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_card,parent, false);
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

    public interface IClickEditListener {
        void onClickEdit(UserTask userTask);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskTitle, tvTaskStartDate, tvTaskEndDate, tvTaskStartTime, tvTaskEndTime, tvTaskTag;
        ImageView ivTag;
        ClickableImageView btnDeleteTask, btnEditTask;
        CheckBox btnCheckComplete;

        TextView tvTaskLocationLabel, tvTaskDescriptionLabel;
        TextView tvTaskLocation, tvTaskDescription, tvTaskDetails;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskStartDate = itemView.findViewById(R.id.tvTaskStartDate);
            tvTaskEndDate = itemView.findViewById(R.id.tvTaskEndDate);
            tvTaskStartTime = itemView.findViewById(R.id.tvTaskStartTime);
            tvTaskEndTime = itemView.findViewById(R.id.tvTaskEndTime);
            tvTaskTag = itemView.findViewById(R.id.tvTaskTag);
            ivTag = itemView.findViewById(R.id.tagLabel);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
            btnEditTask = itemView.findViewById(R.id.btnEditTask);
            btnCheckComplete = itemView.findViewById(R.id.btnCheckComplete);

            tvTaskLocationLabel = itemView.findViewById(R.id.tvTaskLocationLabel);
            tvTaskLocation = itemView.findViewById(R.id.tvTaskLocation);
            tvTaskDescriptionLabel = itemView.findViewById(R.id.tvTaskDescriptionLabel);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskDetails = itemView.findViewById(R.id.tvShowDetails);
        }

        public void bind(UserTask userTask, int position) {
            tvTaskTitle.setText(userTask.getTitle());
            tvTaskStartDate.setText(CalendarUtils.convertMillisToDate(userTask.getStartDate()));
            tvTaskEndDate.setText(CalendarUtils.convertMillisToDate(userTask.getEndDate()));
            tvTaskStartTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getStartTimeInMinutes()));
            tvTaskEndTime.setText(CalendarUtils.convertMinutesToTimeFormat(userTask.getEndTimeInMinutes()));
            tvTaskLocation.setText(userTask.getLocation() != null ? userTask.getLocation() : "None");
            tvTaskTag.setText(userTask.getTag().getName()); // Placeholder for Tag if needed
            ivTag.setColorFilter(userTask.getTag().getColor());
            btnCheckComplete.setChecked(userTask.getIsComplete());
            tvTaskDescription.setText(userTask.getDescription() != null ? userTask.getDescription() : "None");

            btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickEdit(userTask);
                }
            });

            tvTaskLocation.setText(userTask.getLocation());
            tvTaskDescription.setText(userTask.getDescription());

            tvTaskDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = tvTaskDetails.getText().toString().trim();
                    if(message.equals("---------More Details---------")) {
                        tvTaskLocationLabel.setVisibility(View.VISIBLE);
                        tvTaskLocation.setVisibility(View.VISIBLE);
                        tvTaskDescriptionLabel.setVisibility(View.VISIBLE);
                        tvTaskDescription.setVisibility(View.VISIBLE);
                        tvTaskDetails.setText("---------Less Details---------");
                    }
                    else {
                        tvTaskLocationLabel.setVisibility(View.GONE);
                        tvTaskLocation.setVisibility(View.GONE);
                        tvTaskDescriptionLabel.setVisibility(View.GONE);
                        tvTaskDescription.setVisibility(View.GONE);
                        tvTaskDetails.setText("---------More Details---------");
                    }
                }
            });

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
