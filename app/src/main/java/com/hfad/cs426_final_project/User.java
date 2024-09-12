package com.hfad.cs426_final_project;

import android.util.Pair;

import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.DataStorage.Favourite;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.DataStorage.UserSetting;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.MainScreen.Clock.ClockSetting;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private long lastAccessDate; // Store date as a timestamp (milliseconds)

    private List<Session> sessions = new ArrayList<>();
    private Tag focusTag;
    private Block selectedBlock;
    private List<Favourite> favouriteList = new ArrayList<>();
    private int streak;
    private int sun; // money
    private UserSetting userSetting;
    private List<Tree> ownTrees = new ArrayList<>();
    private List<BlockData> ownBlock = new ArrayList<>();
    private List<Tag> ownTags = new ArrayList<>();
    private List<UserTask> ownUserTasks = new ArrayList<>();
    private ClockSetting clockSetting;

    public User() {}

    // Use for adding a new user (when signing up)
    public User(long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;

        // default
        this.sessions = new ArrayList<>();
        this.lastAccessDate = System.currentTimeMillis();
        this.focusTag = new Tag();
        this.selectedBlock = new Block();
        this.favouriteList = new ArrayList<>();
        this.streak = 1;
        this.sun = 0;
        this.userSetting = new UserSetting(); // get default settings

        Tree tree = new Tree(); // default tree
        ownTrees.add(tree);

        Block block = new Block(); // default block
        ownBlock.add(new BlockData(block, 1));

        this.clockSetting = new ClockSetting();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getLastAccessDate() {
        return lastAccessDate;
    }
    public void setLastAccessDate(long lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public int retrieveFocusTimeMinutes() {
        Clock clock = AppContext.getInstance().getCurrentClock();
        if (clock != null)
            return clock.getTargetTime() / 60;
        else
            return AppContext.getInstance().getCurrentUser().getClockSetting().getTargetTime() / 60;
    }

    public void updateFocusTimeMinutes(int focusTimeMinutes) {
        Clock clock = AppContext.getInstance().getCurrentClock();
        if (clock != null)
            clock.setTargetTime(focusTimeMinutes * 60);
        else
            AppContext.getInstance().getCurrentUser().getClockSetting().setTargetTime(focusTimeMinutes * 60);
    }

    public void setSessions(List<Session> sessionsList) {
        this.sessions = sessionsList;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public Tag getFocusTag() {
        return focusTag;
    }

    public void setFocusTag(Tag focusTag) {
        this.focusTag = focusTag;
    }

    public Block getSelectedBlock() {
        return selectedBlock;
    }

    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public List<Favourite> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(List<Favourite> favouriteList) {
        this.favouriteList = favouriteList;
    }

    public int getStreak() {
        return streak;
    }
    public void setStreak(int streak) {
        this.streak = streak;
    }

    public ClockSetting getClockSetting() {
        return clockSetting;
    }
    public void setClockSetting(ClockSetting clockSetting) {
        this.clockSetting = clockSetting;
    }

    public int getSun() {
        return sun;
    }
    public void setSun(int sun) {
        this.sun = sun;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }
    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    public List<Tree> getOwnTrees() {
        return ownTrees;
    }
    public void setOwnTrees(List<Tree> ownTrees) {
        this.ownTrees = ownTrees;
    }

    public List<BlockData> getOwnBlock() {
        return ownBlock;
    }

    public void setOwnBlock(List<BlockData> ownBlock) {
        this.ownBlock = ownBlock;
    }

    public List<Tag> getOwnTags() {
        return ownTags;
    }
    public void setOwnTags(List<Tag> ownTags) {
        this.ownTags = ownTags;
    }

    public List<UserTask> getOwnUserTasks() {
        return ownUserTasks;
    }

    public void setOwnUserTasks(List<UserTask> ownUserTasks) {
        this.ownUserTasks = ownUserTasks;
    }

    public boolean hasTree(Tree tree) {
        if(ownTrees == null)
            return false;
        List<Integer> listOwnTreeID = new ArrayList<>();
        for(Tree t : ownTrees) {
            listOwnTreeID.add(t.getId());
        }
        return listOwnTreeID.contains(tree.getId());
    }

    public boolean hasBlock(Block block) {
        if (ownBlock == null)
            return false;
        List<Integer> listOwnBlockID = new ArrayList<>();
        for (BlockData b : ownBlock) {
            listOwnBlockID.add(b.getBlock().getId());
        }
        return listOwnBlockID.contains(block.getId());
    }

    public boolean hasTag(String tagName) {
        if(ownTags == null)
            return false;
        List<String> listTag = new ArrayList<>();
        for(Tag t : ownTags) {
            listTag.add(t.getName());
        }
        return listTag.contains(tagName);
    }

    public void addFavourite(Tree tree, Tag tag, int focusTime) {
        Favourite favourite = new Favourite(tree, tag, focusTime);
        favouriteList.add(favourite);
    }

    public void removeFavourite(Tree tree, Tag tag, int focusTime) {
        for (int i = 0; i < favouriteList.size(); i++) {
            if(favouriteList.get(i).sameFavourite(tree, tag, focusTime)) {
                favouriteList.remove(i);
                return;
            }
        }
    }

    public boolean isFavourite(Tree tree, Tag tag, int focusTime) {
        for (int i = 0; i < favouriteList.size(); i++) {
            if(favouriteList.get(i).sameFavourite(tree, tag, focusTime)) {
                return true;
            }
        }
        return false;
    }
}
