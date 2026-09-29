package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SeasonResponse {

    @SerializedName("episodes")
    private List<Episode> episodes;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("season_number")
    private int seasonNumber;

    public int getId() {
        return this.id;
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() {
        return this.overview;
    }

    public List<Episode> getEpisodes() {
        return this.episodes != null ? this.episodes : Collections.emptyList();
    }
}
