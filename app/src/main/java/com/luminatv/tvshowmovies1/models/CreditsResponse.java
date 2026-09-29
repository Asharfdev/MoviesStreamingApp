package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CreditsResponse {

    @SerializedName("cast")
    private List<Cast> cast;

    @SerializedName("id")
    private int id;

    public int getId() {
        return this.id;
    }

    public List<Cast> getCast() {
        return this.cast != null ? this.cast : Collections.emptyList();
    }
}
