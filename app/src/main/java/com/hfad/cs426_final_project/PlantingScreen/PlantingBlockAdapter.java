package com.hfad.cs426_final_project.PlantingScreen;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class PlantingBlockAdapter extends RecyclerView.Adapter<PlantingBlockAdapter.PlantingBlockViewHolder> {
    private List<BlockData> blockDataList;
    private final PlantingBlockAdapter.IClickPlantingBlockListener clickListener;
    private int selectedPosition = 0;

    public PlantingBlockAdapter(List<BlockData> blockDataList, PlantingBlockAdapter.IClickPlantingBlockListener listener) {
        this.blockDataList = blockDataList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PlantingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planting_block, parent, false);
        return new PlantingBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantingBlockViewHolder holder, int position) {
        BlockData blockData = blockDataList.get(position);

        if (blockData == null)
            return;

        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(blockData.getBlock().getImgUri()))
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(holder.ivBlock);

        holder.tvNumOwnBlock.setText(String.valueOf(blockData.getQuantity()));

        if (selectedPosition == position) {
            holder.cvBlock.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.primary_20, null));
        } else {
            holder.cvBlock.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.white, null));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition(holder.getAdapterPosition());
                clickListener.onClickPlantingBlock(blockData);
            }
        });
    }

    void selectPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return blockDataList.size();
    }

    public interface IClickPlantingBlockListener {
        void onClickPlantingBlock(BlockData blockData);
    }

    static class PlantingBlockViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBlock;
        TextView tvNumOwnBlock;
        CardView cvBlock;

        public PlantingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBlock = itemView.findViewById(R.id.ivBlock);
            tvNumOwnBlock = itemView.findViewById(R.id.tvNumOwnBlock);
            cvBlock = itemView.findViewById(R.id.cvBlock);
        }
    }
}
