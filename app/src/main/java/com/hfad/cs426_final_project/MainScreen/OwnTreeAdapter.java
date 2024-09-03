package com.hfad.cs426_final_project.MainScreen;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.R;

import java.util.List;

public class OwnTreeAdapter extends RecyclerView.Adapter<OwnTreeAdapter.TreeViewHolder> {
    private final List<Tree> treeList;
    private final IClickTreeListener clickListener;
    private int selectedPosition = 0; // Initialize as 0 to indicate no selection by default

    public OwnTreeAdapter(List<Tree> treeList, IClickTreeListener listener) {
        this.treeList = treeList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_tree, parent, false);
        return new TreeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        Tree tree = treeList.get(position);
        if (tree == null)
            return;

        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(tree.getImgUri()))
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(holder.treeImageView);
        if (selectedPosition == position) {
            holder.cardViewTree.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.primary_20, null));
        } else {
            holder.cardViewTree.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.white, null));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition(holder.getAdapterPosition());
                clickListener.onClickTree(tree);
            }
        });
    }

    @Override
    public int getItemCount() {
        return treeList != null ? treeList.size() : 0;
    }

    private void selectPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    public interface IClickTreeListener {
        void onClickTree(Tree tree);
    }

    static class TreeViewHolder extends RecyclerView.ViewHolder {
        final ImageView treeImageView;
        final CardView cardViewTree;

        public TreeViewHolder(@NonNull View itemView) {
            super(itemView);
            treeImageView = itemView.findViewById(R.id.img_tree);
            cardViewTree = itemView.findViewById(R.id.card_view_tree);
        }
    }
}
