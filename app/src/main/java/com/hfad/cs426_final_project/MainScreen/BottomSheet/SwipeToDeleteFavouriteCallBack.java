package com.hfad.cs426_final_project.MainScreen.BottomSheet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteFavouriteCallBack extends ItemTouchHelper.SimpleCallback {

    private final FavouriteAdapter favouriteAdapter;

    public SwipeToDeleteFavouriteCallBack(FavouriteAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        this.favouriteAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        favouriteAdapter.deleteItem(position);
    }
}