package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class Genre {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
