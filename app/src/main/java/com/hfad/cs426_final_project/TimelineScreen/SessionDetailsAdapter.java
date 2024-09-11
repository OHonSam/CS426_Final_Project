package com.hfad.cs426_final_project.TimelineScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.R;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SessionDetailsAdapter extends RecyclerView.Adapter<SessionDetailsAdapter.SessionDetailsHolder> {
    private List<Session> sessions;

    public SessionDetailsAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_session, parent, false);
        return new SessionDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionDetailsHolder holder, int position) {
        Session session = sessions.get(position);
        if(session == null) {
            return;
        }
        if (position == 0) {
            holder.icHead.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.ic_leaf, null));
        } else {
            holder.icHead.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.ic_leaf, null));
        }
        // Format and set the session start time
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = session.getDateTimeFromTimestamp().format(timeFormatter);
        holder.tvTimeStart.setText(startTime);

        // Set the message based on session status
        if (session.isStatus()) {
//            holder.tvMessage.setText("Success! You grew a " + session.getTree().getName() + ".");
            holder.tvMessage.setText("Success! You grew a tree.");
        } else {
            holder.tvMessage.setText("You gave up and failed to grow a tree.");
        }

        // Format and set the session duration
        long durationInSeconds = session.getDuration();
        String formattedDuration = String.format(Locale.getDefault(), "%02d:%02d", durationInSeconds / 3600, (durationInSeconds % 3600) / 60);
        holder.tvDuration.setText(startTime + " - " + session.getDateTimeFromTimestamp().plusSeconds(durationInSeconds).format(timeFormatter));

        // Set the tree image
        Glide.with(holder.itemView.getContext())
                .load(session.getTree().getImgUri())
                .into(holder.ivTreeDisplay);

        // Set the tag
        holder.icTag.setColorFilter(session.getTag().getColor());
        holder.tvTag.setText(session.getTag().getName());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionDetailsHolder extends RecyclerView.ViewHolder {
        ImageView icHead, ivTreeDisplay, icTag;
        TextView tvTimeStart, tvMessage, tvDuration, tvTag;

        public SessionDetailsHolder(@NonNull View itemView) {
            super(itemView);
            icHead = itemView.findViewById(R.id.ic_head);
            ivTreeDisplay = itemView.findViewById(R.id.ivTreeDisplay);
            tvTimeStart = itemView.findViewById(R.id.tvTimeStart);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            icTag = itemView.findViewById(R.id.ic_tag);
            tvTag = itemView.findViewById(R.id.tvTag);
        }
    }
}
