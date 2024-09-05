package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hfad.cs426_final_project.DataStorage.Favourite;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    private final List<Favourite> favouritesList;
    private final IClickFavouriteListener clickListener;
    private int selectedPosition = -1;

    public FavouriteAdapter(List<Favourite> favouritesList, IClickFavouriteListener listener) {
        this.favouritesList = favouritesList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Favourite favourite = favouritesList.get(position);

        // Set tree image
        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(favourite.getTree().getImgUri()))
                .override(Target.SIZE_ORIGINAL)
                .fitCenter()
                .into(holder.ivTreeDisplay);

        // Set focus time
        holder.tvFocusTimeDisplay.setText(String.valueOf(favourite.getFocusTime()));

        // Set tag name and color
        holder.tvTagNameDisplay.setText(favourite.getTag().getName());
        holder.tagColorView.setColorFilter(Color.parseColor(favourite.getTag().getColorHex()));

        if (selectedPosition == position) {
            holder.favouriteBackgroundView.setBackground(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.background_favourite_green, null));
        } else {
            holder.favouriteBackgroundView.setBackground(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.background_favourite_white, null));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition(holder.getAdapterPosition());
                clickListener.onClickFavourite(favourite);
            }
        });
    }

    private void selectPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        if(selectedPosition == position)
            selectedPosition = -1;
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public interface IClickFavouriteListener {
        void onClickFavourite(Favourite favourite);
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        View favouriteBackgroundView;
        ImageView ivTreeDisplay;
        TextView tvFocusTimeDisplay, tvTagNameDisplay;
        ImageView tagColorView;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            favouriteBackgroundView = itemView.findViewById(R.id.favouriteBackgroundView);
            ivTreeDisplay = itemView.findViewById(R.id.ivTreeDisplay);
            tvFocusTimeDisplay = itemView.findViewById(R.id.tvFocusTimeDisplay);
            tvTagNameDisplay = itemView.findViewById(R.id.tvTagNameDisplay);
            tagColorView = itemView.findViewById(R.id.tagColorView);
        }
    }
}
