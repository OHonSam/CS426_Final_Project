package com.hfad.cs426_final_project.MainScreen.Music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.util.List;

public class MusicAdapter extends  RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{
    private List<MusicItem> musicList;
    private Context context;
    private OnMusicItemClickListener listener;

    public interface OnMusicItemClickListener {
        void onMusicItemClick(MusicItem musicItem);
    }

    public MusicAdapter(Context context, List<MusicItem> musicList, OnMusicItemClickListener listener) {
        this.context = context;
        this.musicList = musicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item_layout, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        MusicItem musicItem = musicList.get(position);
        holder.bind(musicItem, listener);
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;

        // get UI components references
        public MusicViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.music_item_title);
        }

        // assign values to each UI components
        @SuppressLint("ClickableViewAccessibility")
        public void bind(final MusicItem musicItem, final OnMusicItemClickListener listener) {
            titleView.setText(musicItem.getTitle());

            // Set a listener to handle press state
            itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change background or apply a color filter when pressed
                        itemView.setBackgroundColor(Color.LTGRAY); // Example: change background color
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Revert to the original state when released
                        itemView.setBackgroundColor(Color.TRANSPARENT); // Reset background color
                        break;
                }
                return false; // Return false to allow click event to be handled
            });

            itemView.setOnClickListener(v -> listener.onMusicItemClick(musicItem));
        }
    }
}
