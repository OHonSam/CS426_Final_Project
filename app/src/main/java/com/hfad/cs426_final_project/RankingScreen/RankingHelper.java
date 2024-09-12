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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingHelper extends BaseAdapter {
    private Context context;
    private List<User> users;
    private User currentUser;
    private boolean isToday;

    public RankingHelper(Context context, User currentUser, boolean isToday) {
        this.context = context;
        users = new ArrayList<>();
        this.currentUser = currentUser;
        this.isToday = isToday;
        fetchUsers();
    }

    private void fetchUsers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
                if (isToday) {
                    users.sort((user1, user2) -> user2.getStreak() - user1.getStreak());
                } else {
                    // need to change to getHighestStreak when developed
                    // users.sort((user1, user2) -> user2.getHighestStreak() - user1.getHighestStreak());
                }

                notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error fetching users: " + databaseError.getMessage());
            }
        });
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ranking_card,
            null);
        }

        TextView rankingTextView = convertView.findViewById(R.id.ranking);
        ImageView medalImageView = convertView.findViewById(R.id.medal);
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView streakTextView = convertView.findViewById(R.id.streak);

        User user = users.get(position);
        rankingTextView.setText(String.valueOf(position + 1));
        medalImageView.setVisibility(position < 3 ? View.VISIBLE : View.GONE);
        usernameTextView.setText(user.getName());
        streakTextView.setText(String.valueOf(user.getStreak()));
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

        // need to change into CurrentUser.getId() when finish merging
        if (user.getId() == AppContext.getInstance().getCurrentUser().getId()) {
            usernameTextView.setText(user.getName() + " (you)");
            usernameTextView.setTypeface(usernameTextView.getTypeface(), Typeface.BOLD_ITALIC);
        } else {
            usernameTextView.setText(user.getName());
        }

        return convertView;
    }
}
