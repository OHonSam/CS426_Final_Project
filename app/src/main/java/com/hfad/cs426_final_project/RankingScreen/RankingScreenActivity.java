package com.hfad.cs426_final_project.RankingScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;

public class RankingScreenActivity extends BaseScreenActivity {
    private ListView listView;
    private MaterialButtonToggleGroup periodPicker;
    private MyButton rankingModeBtn;
    private TextView rankingModeText;
    private User currentUser;
    private boolean isStreak = true;
    private boolean isToday = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ranking_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
        setToggleColor(Color.WHITE);
        setupRankingModeButton();
    }

    private void initializeComponents() {
        rankingModeBtn = findViewById(R.id.ranking_mode_btn);
        rankingModeText = findViewById(R.id.ranking_mode_text);
        initializeRankingList();
    }

    private void initializeRankingList() {
        currentUser = AppContext.getInstance().getCurrentUser();

        listView = findViewById(R.id.ranking_list);
        updateAdapter();

        periodPicker = findViewById(R.id.period_picker);
        periodPicker.check(R.id.today_btn);

        periodPicker.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isToday = checkedId == R.id.today_btn;
                updateAdapter();
            }
        });
    }

    private void updateAdapter() {
        RankingHelper adapter = new RankingHelper(this, currentUser, isToday, isStreak);
        listView.setAdapter(adapter);
    }

    private void setupRankingModeButton() {
        rankingModeBtn.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.popup_ranking_mode, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_streak) {
                setRankingMode("Streak");
                isStreak = true;
                updateAdapter();
                return true;
            } else if (item.getItemId() == R.id.menu_item_time_focused) {
                setRankingMode("Focus");
                isStreak = false;
                updateAdapter();
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void setRankingMode(String mode) {
        rankingModeBtn.setText(mode);
        rankingModeText.setText(mode.equals("Focus") ? "Focus Time" : mode);
    }
}