package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteItemListAdapter extends BaseAdapter {
    private Context context;
    private List<ItemStatistic> items;
    private boolean isTreeMode;

    public FavoriteItemListAdapter(Context context, List<Session> filteredSessions, boolean isTreeMode) {
        this.context = context;
        this.isTreeMode = isTreeMode;
        this.items = calculateItemStatistics(filteredSessions);
    }

    private List<ItemStatistic> calculateItemStatistics(List<Session> sessions) {
        Map<String, ItemStatistic> itemMap = new HashMap<>();

        for (Session session : sessions) {
            String imgUri;
            int id;
            if (isTreeMode) {
                imgUri = session.getTree().getImgUri();
                id = session.getTree().getId();
            } else {
                imgUri = session.getBlock().getImgUri();
                id = session.getBlock().getId();
            }

            ItemStatistic item = itemMap.get(imgUri);
            if (item == null) {
                item = new ItemStatistic(id, imgUri);
                itemMap.put(imgUri, item);
            }
            item.incrementOccurrence();
        }

        List<ItemStatistic> sortedItems = new ArrayList<>(itemMap.values());
        sortedItems.sort((a, b) -> b.getOccurrence() - a.getOccurrence());
        Log.d("FavoriteItemListAdapter", "calculateItemStatistics: " + sortedItems.size());
        return sortedItems;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.statistic_card, null);
        }

        ItemStatistic item = items.get(position);

        TextView favoriteRankingOutTop = convertView.findViewById(R.id.favoriteRankingOutTop);
        TextView favoriteRankingTop = convertView.findViewById(R.id.favoriteRankingTop);
        ImageView favoriteMedal = convertView.findViewById(R.id.favoriteMedal);
        ImageView itemImage = convertView.findViewById(R.id.item_image);
        TextView occurrence = convertView.findViewById(R.id.occurrence);

        switch (position) {
            case 0:
                convertView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Gold));
                break;
            case 1:
                convertView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Silver));
                break;
            case 2:
                convertView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Bronze));
                break;
            default:
                convertView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.Accent2));
                break;
        }

        // Set ranking
        int ranking = position + 1;
        if (ranking <= 3) {
            favoriteRankingTop.setText(String.valueOf(ranking));
            favoriteRankingTop.setVisibility(View.VISIBLE);
            favoriteMedal.setVisibility(View.VISIBLE);
            favoriteRankingOutTop.setVisibility(View.GONE);
        } else {
            favoriteRankingOutTop.setText(String.valueOf(ranking));
            favoriteRankingOutTop.setVisibility(View.VISIBLE);
            favoriteRankingTop.setVisibility(View.GONE);
            favoriteMedal.setVisibility(View.GONE);
        }



        // Load item image
        Glide.with(context)
                .load(item.getImgUri())
                .into(itemImage);

        // Set occurrence
        occurrence.setText(item.getOccurrence() + " times");

        return convertView;
    }

    public void updateData(List<Session> filteredSessions, boolean isTreeMode) {
        this.isTreeMode = isTreeMode;
        this.items = calculateItemStatistics(filteredSessions);
        notifyDataSetChanged();
    }

    private static class ItemStatistic {
        private int id;
        private String imgUri;
        private int occurrence;

        public ItemStatistic(int id, String imgUri) {
            this.id = id;
            this.imgUri = imgUri;
            this.occurrence = 0;
        }

        public void incrementOccurrence() {
            occurrence++;
        }

        public int getId() {
            return id;
        }

        public String getImgUri() {
            return imgUri;
        }

        public int getOccurrence() {
            return occurrence;
        }
    }
}