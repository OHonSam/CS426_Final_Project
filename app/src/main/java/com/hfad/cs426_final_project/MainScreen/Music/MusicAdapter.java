package com.hfad.cs426_final_project.MainScreen.Music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.util.List;

public class MusicAdapter extends  RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{
    private final List<MusicItem> musicList;
    private final Context context;
    private final OnMusicItemClickListener listener;

    private int selectedPosition = -1;

    public interface OnMusicItemClickListener {
        void onMusicItemClick(MusicItem musicItem);
    }

    public MusicAdapter(Context context, List<MusicItem> musicList, int savedMusicResId,OnMusicItemClickListener listener) {
        this.context = context;
        this.musicList = musicList;
        this.listener = listener;
        this.selectedPosition = getPositionByMusicResId(savedMusicResId);
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
        holder.bind(musicItem, listener, position);
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public int getPositionByMusicResId(int musicResId) {
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getFileResourceId() == musicResId) {
                return i;  // Return the position if a match is found
            }
        }
        return 0;  // Return 0 (forest_rain) if no match is found
    }


    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;

        // get UI components references
        public MusicViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.music_item_title);
        }

        // assign values to each UI components
        @SuppressLint("ClickableViewAccessibility")
        private void bind(final MusicItem musicItem, final OnMusicItemClickListener listener, int adapterPosition) {
            titleView.setText(musicItem.getTitle());

            if (selectedPosition == adapterPosition) {
                itemView.setBackgroundColor(Color.LTGRAY);
                titleView.setTextAppearance(R.style.SubHead);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }

//            // Set a listener to handle press state
//            itemView.setOnTouchListener((v, event) -> {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Change background or apply a color filter when pressed
//                        itemView.setBackgroundColor(Color.LTGRAY); // Example: change background color
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        // Revert to the original state when released
//                        itemView.setBackgroundColor(Color.TRANSPARENT); // Reset background color
//                        break;
//                }
//                return false; // Return false to allow click event to be handled
//            });

            itemView.setOnClickListener(v -> {
                listener.onMusicItemClick(musicItem);
                notifyItemChanged(selectedPosition);
                selectedPosition = adapterPosition;
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
