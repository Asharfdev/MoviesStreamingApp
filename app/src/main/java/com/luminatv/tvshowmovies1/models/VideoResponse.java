package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VideoResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> results;

    public int getId() {
        return this.id;
    }

    public List<Video> getResults() {
        return this.results != null ? this.results : Collections.emptyList();
    }
}
