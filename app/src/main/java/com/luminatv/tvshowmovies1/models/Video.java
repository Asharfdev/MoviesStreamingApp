package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class Video {

    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("official")
    private boolean official;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getSite() {
        return this.site;
    }

    public String getType() {
        return this.type;
    }

    public boolean isOfficial() {
        return this.official;
    }

    public boolean isYouTubeTrailer() {
        return "YouTube".equalsIgnoreCase(this.site) && "Trailer".equalsIgnoreCase(this.type);
    }

    public boolean isYouTube() {
        return "YouTube".equalsIgnoreCase(this.site);
    }

    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + this.key + "/hqdefault.jpg";
    }
}
