package com.hfad.cs426_final_project.DataStorage;

public class BlockData {
    private Block block;
    private int quantity;

    public BlockData() {
    }

    public BlockData(Block block, int quantity) {
        this.block = block;
        this.quantity = quantity;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}