package com.hfad.cs426_final_project.DataStorage;

public class Block {
    private int id;
    private int cost;
    private String imgUri;

    public Block() {
        this.id = 0;
        this.cost = 0;
        this.imgUri = "https://firebasestorage.googleapis.com/v0/b/focus-da00f.appspot.com/o/Blocks%2FGrass%2Fgrass0.png?alt=media&token=eba81284-b44c-400c-b211-3ee1d6479da0";
    }

    public Block(int id, String imgUri) {
        this.id = id;
        this.imgUri = imgUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
