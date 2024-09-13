package com.hfad.cs426_final_project.RankingScreen;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RankingHelper extends BaseAdapter {
    private Context context;
    private List<User> users;
    private User currentUser;
    private boolean isToday;
    private boolean isStreak;
    private Map<Long, Long> focusTimeMap;

    public RankingHelper(Context context, User currentUser, boolean isToday, boolean isStreak) {
        this.context = context;
        this.users = new ArrayList<>();
        this.currentUser = currentUser;
        this.isToday = isToday;
        this.isStreak = isStreak;
        this.focusTimeMap = new HashMap<>();
        fetchUsers();
    }

    private void fetchUsers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                focusTimeMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                        calculateFocusTime(user.getId(), snapshot);
                    }
                }
                sortUsers();
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error fetching users: " + databaseError.getMessage());
            }
        });
    }

    private void calculateFocusTime(long userId, DataSnapshot userSnapshot) {
        long totalFocusTime = 0;
        DataSnapshot sessionsSnapshot = userSnapshot.child("sessions");
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        for (DataSnapshot sessionSnapshot : sessionsSnapshot.getChildren()) {
            Boolean status = sessionSnapshot.child("status").getValue(Boolean.class);
            if (status != null && status) {
                Long duration = sessionSnapshot.child("duration").getValue(Long.class);
                Long timestamp = sessionSnapshot.child("timestamp").getValue(Long.class);

                if (duration != null && timestamp != null) {
                    String sessionDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(timestamp));
                    if (!isToday || sessionDate.equals(today)) {
                        totalFocusTime += duration;
                    }
                }
            }
        }
        focusTimeMap.put(userId, totalFocusTime);
    }

    private void sortUsers() {
        if (isStreak) {
            users.sort((user1, user2) -> user2.getStreak() - user1.getStreak());
        } else {
            users.sort((user1, user2) -> Long.compare(
                    focusTimeMap.getOrDefault(user2.getId(), 0L),
                    focusTimeMap.getOrDefault(user1.getId(), 0L)
            ));
        }
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ranking_card, null);
        }

        TextView rankingOutTextView = convertView.findViewById(R.id.rankingOutTop);
        TextView rankingTextView = convertView.findViewById(R.id.rankingTop);
        ImageView medalImageView = convertView.findViewById(R.id.medal);
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView streakTextView = convertView.findViewById(R.id.streak);

        User user = users.get(position);
        rankingTextView.setText(String.valueOf(position + 1));
        rankingOutTextView.setText(String.valueOf(position + 1));
        medalImageView.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
        rankingTextView.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
        rankingOutTextView.setVisibility(position >= 3 ? View.VISIBLE : View.GONE);
        usernameTextView.setText(user.getName());

        if (isStreak) {
            streakTextView.setText(String.valueOf(user.getStreak()));
        } else {
            long focusTime = focusTimeMap.getOrDefault(user.getId(), 0L);
            long hours = focusTime / 3600;
            long minutes = (focusTime % 3600) / 60;
            streakTextView.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));
        }

        CardView cardView = convertView.findViewById(R.id.ranking_card);

        switch (position) {
            case 0:
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Gold));
                break;
            case 1:
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Silver));
                break;
            case 2:
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Bronze));
                break;
            default:
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Accent2));
                break;
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_user_info, null);

                // Initialize the dialog views
                ImageView userImageView = dialogView.findViewById(R.id.user_image);
                TextView userNameTextView = dialogView.findViewById(R.id.user_name);
                TextView userEmailTextView = dialogView.findViewById(R.id.user_email);
                TextView userFocusTimeTextView = dialogView.findViewById(R.id.user_focus_time);

                // Set user data
                userNameTextView.setText(user.getName());
                userEmailTextView.setText(user.getEmail());
                Glide.with(userImageView)
                        .load(user.getImgUri())
                        .error(R.drawable.default_avatar)
                        .into(userImageView);

                // If focus time or streak is required, format it appropriately
                if (isStreak) {
                    userFocusTimeTextView.setText("Streak: " + user.getStreak() + " days");
                } else {
                    long focusTime = focusTimeMap.getOrDefault(user.getId(), 0L);
                    long hours = focusTime / 3600;
                    long minutes = (focusTime % 3600) / 60;
                    userFocusTimeTextView.setText(String.format(Locale.getDefault(), "%dh %dm focus", hours, minutes));
                }

                // Create and show the dialog
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setView(dialogView)
                        .setPositiveButton("OK", null)  // Close the dialog with OK button                       .create()
                        .show();
            }
        });


        if (user.getId() == currentUser.getId()) {
            usernameTextView.setText(user.getName() + " (you)");
            usernameTextView.setTypeface(usernameTextView.getTypeface(), Typeface.BOLD_ITALIC);
        } else {
            usernameTextView.setText(user.getName());
        }

        return convertView;
    }
}