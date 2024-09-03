package com.hfad.cs426_final_project.MainScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class TagAdapterSpinner extends ArrayAdapter<Tag> {
    public TagAdapterSpinner(@NonNull Context context, int resource, @NonNull List<Tag> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_selected, parent, false);
        TextView tvTag = convertView.findViewById(R.id.tvSelectTag);

        Tag tag = this.getItem(position);
        if(tag != null) {
            tvTag.setText(tag.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_category, parent, false);
        TextView tvTag = convertView.findViewById(R.id.tvTagMenu);

        Tag tag = this.getItem(position);
        if(tag != null) {
            tvTag.setText(tag.getName());
        }
        return convertView;
    }
}
