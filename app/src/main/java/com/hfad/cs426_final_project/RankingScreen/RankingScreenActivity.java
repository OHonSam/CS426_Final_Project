package com.hfad.cs426_final_project.RankingScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

public class RankingScreenActivity extends BaseScreenActivity {
    private ListView listView;
    private MaterialButtonToggleGroup periodPicker;
    private MyButton rankingModeBtn;
    private TextView rankingModeText;
    private User currentUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ranking_screen;
    }

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
        RankingHelper adapter = new RankingHelper(this, AppContext.getInstance().getCurrentUser(), true);
        listView.setAdapter(adapter);

        periodPicker = findViewById(R.id.period_picker);
        periodPicker.check(R.id.today_btn);

        periodPicker.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                boolean isToday = checkedId == R.id.today_btn;
                RankingHelper adapter2 = new RankingHelper(this, currentUser, isToday);
                listView.setAdapter(adapter2);
            }
        });
    }

    private void setupRankingModeButton() {
        rankingModeBtn.setOnClickListener(v -> showPopupMenu(v));
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.popup_ranking_mode, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_streak) {
                rankingModeBtn.setText("Streak");
                rankingModeText.setText("Streak");
                return true;
            } else if (item.getItemId() == R.id.menu_item_time_focused) {
                rankingModeBtn.setText("Focus");
                rankingModeText.setText("Focus Time");
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }
}
