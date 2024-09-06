package com.hfad.cs426_final_project.DataStorage;

import static java.lang.Math.random;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Random;

public class Session {
    private int id;
    private boolean status;
    private long duration;
    private long timestamp;  // Store timestamp as milliseconds since epoch
    private Tree tree;
    private Tag tag;

    // No-argument constructor (required by Firebase)
    public Session() {
    }

    // Constructor with id
    public Session(int id) {
        this.id = id;
        this.status = random() > 0.5;
        this.duration = (long) (random() * 3600);
        this.tree = new Tree();
        this.tag = new Tag((int) (random() * 5) + 1);

        Random random = new Random();

        LocalDateTime startOfYear = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(2024, 12, 31, 23, 59, 59);

        long startEpochMilli = startOfYear.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endEpochMilli = endOfYear.toInstant(ZoneOffset.UTC).toEpochMilli();

        this.timestamp = startEpochMilli + (long) (random.nextDouble() * (endEpochMilli - startEpochMilli));
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    private LocalDateTime getDateTimeFromTimestamp() {
        return Instant.ofEpochMilli(this.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public int getYear() {
        return Instant.ofEpochSecond(this.timestamp)
                .atZone(ZoneId.systemDefault())
                .getYear();
    }
}