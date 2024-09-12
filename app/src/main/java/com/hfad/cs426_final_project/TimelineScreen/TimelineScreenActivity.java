package com.hfad.cs426_final_project.TimelineScreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.StatisticScreen.StatisticScreenActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineScreenActivity extends BaseScreenActivity {
    private AppContext appContext;
    private RecyclerView rcvTimeline;
    private ClickableImageView ivStatistic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timeline_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = AppContext.getInstance();
        setToggleColor(Color.WHITE);

        initUI();
        initListener();
        initRCVTimeline();
    }

    private void initUI() {
        rcvTimeline = findViewById(R.id.rcvTimeline);
        ivStatistic = findViewById(R.id.ivStatistic);
    }

    private void initListener() {
        ivStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineScreenActivity.this, StatisticScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initRCVTimeline() {
        // Get the list of sessions from the current user
        List<Session> sessionList = appContext.getCurrentUser().getSessions();

        // Group sessions by date (ignore time)
        Map<LocalDateTime, List<Session>> sessionsByDate = new HashMap<>();

        for (Session session : sessionList) {
            // Extract the date part (year, month, day) from the session's date
            LocalDateTime sessionDate = session.getDateTimeFromTimestamp().toLocalDate().atStartOfDay();

            // If this date is not already in the map, add it
            if (!sessionsByDate.containsKey(sessionDate)) {
                sessionsByDate.put(sessionDate, new ArrayList<>());
            }

            // Add the session to the list for that date
            sessionsByDate.get(sessionDate).add(session);
        }

        // Convert the map to lists of dates and corresponding session lists
        List<LocalDateTime> dates = new ArrayList<>(sessionsByDate.keySet());
        List<List<Session>> sessionLists = new ArrayList<>();

        for (LocalDateTime date : dates) {
            sessionLists.add(sessionsByDate.get(date));
        }

        // Set up the RecyclerView with DateTimelineAdapter
        DateTimelineAdapter adapter = new DateTimelineAdapter(dates, sessionLists);
        rcvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rcvTimeline.setAdapter(adapter);
    }
}