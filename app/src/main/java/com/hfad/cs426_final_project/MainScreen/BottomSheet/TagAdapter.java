package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<Tag> tagList;
    private final TagAdapter.IClickTagListener clickListener;
    private int selectedPosition;

    public TagAdapter(List<Tag> tagList, TagAdapter.IClickTagListener listener) {
        this.tagList = tagList;
        this.clickListener = listener;

        for(int i = 0; i < getItemCount(); i++) {
            if(AppContext.getInstance().getCurrentUser().getFocusTag().getId() == tagList.get(i).getId()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.tagNameTextView.setText(tag.getName());
        if (selectedPosition == position) {
            holder.cvTag.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.primary_20, null));
        } else {
            holder.cvTag.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.white, null));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition(holder.getAdapterPosition());
                clickListener.onClickTag(tag);
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
        return tagList.size();
    }

    public interface IClickTagListener {
        void onClickTag(Tag tag);
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        final TextView tagNameTextView;
        CardView cvTag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagNameTextView = itemView.findViewById(R.id.tvTagName);
            cvTag = itemView.findViewById(R.id.cvTag);
        }
    }
}