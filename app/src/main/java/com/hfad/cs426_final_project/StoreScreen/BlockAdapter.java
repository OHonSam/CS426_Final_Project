package com.hfad.cs426_final_project.StoreScreen;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private StoreScreenActivity storeScreenActivity;
    private Context context;
    private List<Block> blockList;
    private User currentUser;

    public BlockAdapter(StoreScreenActivity storeScreenActivity, List<Block> blockList, User currentUser) {
        this.storeScreenActivity = storeScreenActivity;
        this.context = storeScreenActivity;
        this.blockList = blockList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_item_block, parent, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        Block block = blockList.get(position);

        Glide.with(context)
                .load(block.getImgUri())
                .into(holder.blockImage);

        if (currentUser.hasBlock(block)) {
            holder.blockCost.setText("Purchased!");
            holder.blockImage.setOnClickListener(null); // Disable click for owned blocks
            holder.blockContainer.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.primary_20, null));
            holder.sunDisplay.setVisibility(View.GONE);
        } else {
            holder.blockCost.setText(String.valueOf(block.getCost()));
            holder.blockImage.setOnClickListener(v -> showPurchaseDialog(block));
            holder.blockContainer.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
            holder.sunDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void showPurchaseDialog(Block block) {
        new AlertDialog.Builder(context)
                .setTitle("Purchase Block")
                .setMessage("Do you want to purchase this block for " + block.getCost() + " sun?")
                .setPositiveButton("Buy", (dialog, which) -> {
                    // Check if the user has enough money
                    if (currentUser.getSun() >= block.getCost()) {
                        // Proceed with purchase
                        purchaseBlock(block);
                    } else {
                        // Notify user of insufficient funds
                        showInsufficientFundsDialog();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showInsufficientFundsDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Insufficient Funds")
                .setMessage("You do not have enough coins to purchase this block.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void purchaseBlock(Block block) {
        // Deduct the cost from the user's balance
//        currentUser.setSun(currentUser.getSun() - block.getCost());
        currentUser.updateSun(-block.getCost());
        // Add the block to the user's owned blocks
        currentUser.getOwnBlock().add(new BlockData(block, 0));

        // Update the RecyclerView to reflect the purchase
        storeScreenActivity.updateSunDisplay();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public void updateBlockList(List<Block> newBlockList) {
        blockList = newBlockList;
        notifyDataSetChanged();
    }

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        ImageView blockImage;
        TextView blockCost;
        CardView blockContainer;
        ImageView sunDisplay;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            blockImage = itemView.findViewById(R.id.blockImage);
            blockCost = itemView.findViewById(R.id.blockCost);
            blockContainer = itemView.findViewById(R.id.block_container);
            sunDisplay = itemView.findViewById(R.id.sunDisplay_itemBlock);
        }
    }
}
