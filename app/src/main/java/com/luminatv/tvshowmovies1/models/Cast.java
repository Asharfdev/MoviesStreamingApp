package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class Cast {

    @SerializedName("character")
    private String character;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("order")
    private int order;

    @SerializedName("profile_path")
    private String profilePath;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCharacter() {
        return this.character;
    }

    public String getProfilePath() {
        return this.profilePath;
    }

    public int getOrder() {
        return this.order;
    }

    public String getFullProfilePath() {
        if (this.profilePath != null) {
            return "https://image.tmdb.org/t/p/w185" + this.profilePath;
        }
        return null;
    }
}
