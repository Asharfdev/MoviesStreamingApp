package com.luminatv.tvshowmovies1.models;

public class WatchProgressItem {
    private String backdropPath;
    private int episodeNumber;
    private long lastWatchedTimestamp;
    private String posterPath;
    private int progressPercentage;
    private int seasonNumber;
    private String title;
    private int tmdbId;
    private String type;
    private String videoUrl;

    public WatchProgressItem() {
    }

    public WatchProgressItem(int tmdbId, String title, String posterPath, String backdropPath, String type, String videoUrl, int seasonNumber, int episodeNumber, int progressPercentage) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.type = type;
        this.videoUrl = videoUrl;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        setProgressPercentage(progressPercentage);
        this.lastWatchedTimestamp = System.currentTimeMillis();
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public long getLastWatchedTimestamp() {
        return lastWatchedTimestamp;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public String getTitle() {
        return title;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getType() {
        return type;
    }

    public String getVideoUrl() {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            return videoUrl;
        }
        if ("tv".equals(type) && seasonNumber > 0 && episodeNumber > 0) {
            return "https://vsembed.ru/embed/tv?tmdb=" + tmdbId + "&season=" + seasonNumber + "&episode=" + episodeNumber;
        }
        if ("tv".equals(type)) {
            return "https://vsembed.ru/embed/tv?tmdb=" + tmdbId;
        }
        return "https://vsembed.ru/embed/movie?tmdb=" + tmdbId;
    }

    public String getDisplayTitle() {
        String safeTitle = title != null ? title : "Untitled";
        if ("tv".equals(type) && seasonNumber > 0 && episodeNumber > 0) {
            String marker = "S" + seasonNumber;
            if (safeTitle.contains(marker)) {
                return safeTitle;
            }
            return safeTitle + "  S" + seasonNumber + " E" + episodeNumber;
        }
        return safeTitle;
    }

    public boolean matches(int tmdbId, String type, int seasonNumber, int episodeNumber) {
        return this.tmdbId == tmdbId
                && this.type != null
                && this.type.equals(type)
                && this.seasonNumber == seasonNumber
                && this.episodeNumber == episodeNumber;
    }

    public void setProgressPercentage(int progressPercentage) {
        if (progressPercentage < 0) {
            progressPercentage = 0;
        }
        if (progressPercentage > 100) {
            progressPercentage = 100;
        }
        this.progressPercentage = progressPercentage;
    }

    public void touch() {
        this.lastWatchedTimestamp = System.currentTimeMillis();
    }
}
