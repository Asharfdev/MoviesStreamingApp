package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class Episode {

    @SerializedName("air_date")
    private String airDate;

    @SerializedName("episode_number")
    private int episodeNumber;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("season_number")
    private int seasonNumber;

    @SerializedName("still_path")
    private String stillPath;

    @SerializedName("vote_average")
    private double voteAverage;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() {
        return this.overview;
    }

    public int getEpisodeNumber() {
        return this.episodeNumber;
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public String getStillPath() {
        return this.stillPath;
    }

    public String getAirDate() {
        return this.airDate;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public String getFullStillPath() {
        if (this.stillPath != null) {
            return "https://image.tmdb.org/t/p/w300" + this.stillPath;
        }
        return null;
    }
}
