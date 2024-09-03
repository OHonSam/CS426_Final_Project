package com.hfad.cs426_final_project.MainScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class FocusTimeAdapter extends RecyclerView.Adapter<FocusTimeAdapter.FocusedTimeViewHolder> {
    private List<Integer> focusedTimes;
    private final FocusTimeAdapter.IClickFocusTimeListener clickListener;
    private int selectedPosition = -1; // Initialize as 0 to indicate no selection by default

    public FocusTimeAdapter(FocusTimeAdapter.IClickFocusTimeListener listener) {
        focusedTimes = new ArrayList<>();
        for (int i = 10; i <= 120; i += 5) {
            focusedTimes.add(i);
        }
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public FocusedTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_focused_time, parent, false);
        return new FocusedTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FocusedTimeViewHolder holder, int position) {
        int actualPosition = position % focusedTimes.size();
        int time = focusedTimes.get(actualPosition);

        holder.tvFocusedTime.setText(String.valueOf(time));
        if (selectedPosition == position) {
            holder.cvFocusedTime.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.primary_20, null));
        } else {
            holder.cvFocusedTime.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.white, null));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition(holder.getAdapterPosition());
                clickListener.onClickFocusTime(time);
            }
        });
    }

    private void selectPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Infinite scrolling
    }

    public interface IClickFocusTimeListener {
        void onClickFocusTime(int focusTime);
    }

    static class FocusedTimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvFocusedTime;
        CardView cvFocusedTime;

        public FocusedTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFocusedTime = itemView.findViewById(R.id.tvFocusedTime);
            cvFocusedTime = itemView.findViewById(R.id.cvFocusedTime);
        }
    }
}
