package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TvShowResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<TvShow> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public int getPage() {
        return this.page;
    }

    public List<TvShow> getResults() {
        return this.results != null ? this.results : Collections.emptyList();
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public int getTotalResults() {
        return this.totalResults;
    }
}
