package com.luminatv.tvshowmovies1.models;

public class LocalMediaItem {
    private String backdropUrl;
    private int id;
    private long lastUpdated;
    private String posterUrl;
    private double rating;
    private String subtitle;
    private String title;
    private String type;

    public LocalMediaItem() {
    }

    public LocalMediaItem(int id, String type, String title, String subtitle, String posterUrl, String backdropUrl, double rating) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.rating = rating;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public int getId() {
        return id;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getRating() {
        return rating;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean isSameMedia(LocalMediaItem other) {
        return other != null && id == other.id && type != null && type.equals(other.type);
    }

    public void touch() {
        lastUpdated = System.currentTimeMillis();
    }
}
