package com.hfad.cs426_final_project.StoreScreen;

import android.content.Context;
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
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;

import java.util.List;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.TreeViewHolder> {
    private StoreScreenActivity storeScreenActivity;
    private Context context;
    private List<Tree> treeList;
    private User currentUser;

    public TreeAdapter(StoreScreenActivity storeScreenActivity, List<Tree> treeList, User currentUser) {
        this.storeScreenActivity = storeScreenActivity;
        this.context = storeScreenActivity;
        this.treeList = treeList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_item_tree, parent, false);
        return new TreeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        Tree tree = treeList.get(position);

        Glide.with(context)
                .load(tree.getImgUri())
                .into(holder.treeImage);

        if (currentUser.hasTree(tree)) {
            holder.treeCost.setText("Purchased!");
            holder.treeImage.setOnClickListener(null); // Disable click for owned trees
            holder.treeContainer.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.primary_20, null));
            holder.sunDisplay.setVisibility(View.GONE);
        } else {
            holder.treeCost.setText(String.valueOf(tree.getCost()));
            holder.treeImage.setOnClickListener(v -> showPurchaseDialog(tree));
            holder.treeContainer.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
            holder.sunDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void showPurchaseDialog(Tree tree) {
        new AlertDialog.Builder(context)
                .setTitle("Purchase Tree")
                .setMessage("Do you want to purchase this tree for " + tree.getCost() + " coins?")
                .setPositiveButton("Buy", (dialog, which) -> {
                    // Check if the user has enough money
                    if (currentUser.getSun() >= tree.getCost()) {
                        // Proceed with purchase
                        purchaseTree(tree);
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
                .setMessage("You do not have enough coins to purchase this tree.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void purchaseTree(Tree tree) {
        // Deduct the cost from the user's balance
//        currentUser.setSun(currentUser.getSun() - tree.getCost());
        currentUser.updateSun(- tree.getCost());
        // Add the tree to the user's owned trees
        currentUser.getOwnTrees().add(tree);

        // Update the RecyclerView to reflect the purchase
        storeScreenActivity.updateSunDisplay();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }

    public void updateTreeList(List<Tree> newTreeList) {
        treeList = newTreeList;
        notifyDataSetChanged();
    }

    public static class TreeViewHolder extends RecyclerView.ViewHolder {
        ImageView treeImage;
        TextView treeCost;
        CardView treeContainer;
        ImageView sunDisplay;

        public TreeViewHolder(@NonNull View itemView) {
            super(itemView);
            treeImage = itemView.findViewById(R.id.treeImage);
            treeCost = itemView.findViewById(R.id.treeCost);
            treeContainer = itemView.findViewById(R.id.tree_container);
            sunDisplay = itemView.findViewById(R.id.sunDisplay_itemTree);
        }
    }
}
