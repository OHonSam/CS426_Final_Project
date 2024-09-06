package com.hfad.cs426_final_project.DataStorage;

public class Favourite {
    private Tree tree;
    private Tag tag;
    private int focusTime;

    public Favourite() {
    }

    public Favourite(Tree tree, Tag tag, int focusTime) {
        this.tree = tree;
        this.tag = tag;
        this.focusTime = focusTime;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(int focusTime) {
        this.focusTime = focusTime;
    }

    public boolean sameFavourite (Tree tree, Tag tag, int focusTime) {
        return this.tree.sameID(tree) && this.tag.sameID(tag) && this.focusTime == focusTime;
    }
}
