package com.hfad.cs426_final_project.MainScreen.BottomSheet;

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
import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class OwnBlockAdapter extends RecyclerView.Adapter<OwnBlockAdapter.OwnBlockViewHolder> {
    private List<BlockData> blockDataList;
    private final OwnBlockAdapter.IClickOwnBlockListener clickListener;
    private int selectedPosition;

    public OwnBlockAdapter(List<BlockData> blockDataList, OwnBlockAdapter.IClickOwnBlockListener listener) {
        this.blockDataList = blockDataList;
        this.clickListener = listener;

        for(int i = 0; i < getItemCount(); i++) {
            if(AppContext.getInstance().getCurrentUser().getSelectedBlock().getId() == blockDataList.get(i).getBlock().getId()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public OwnBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_block, parent, false);
        return new OwnBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnBlockViewHolder holder, int position) {
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
                clickListener.onClickOwnBlock(blockData);
            }
        });
    }

    private void selectPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return blockDataList.size();
    }

    public interface IClickOwnBlockListener {
        void onClickOwnBlock(BlockData blockData);
    }

    static class OwnBlockViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBlock;
        TextView tvNumOwnBlock;
        CardView cvBlock;

        public OwnBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBlock = itemView.findViewById(R.id.ivBlock);
            tvNumOwnBlock = itemView.findViewById(R.id.tvNumOwnBlock);
            cvBlock = itemView.findViewById(R.id.cvBlock);
        }
    }
}
