package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class WatchProvider implements Serializable {

    @SerializedName("display_priority")
    private int displayPriority;

    @SerializedName("logo_path")
    private String logoPath;

    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("provider_name")
    private String providerName;

    public WatchProvider() {
    }

    public WatchProvider(int providerId, String providerName, String logoPath) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.logoPath = logoPath;
    }

    public static WatchProvider streamingServer() {
        return new WatchProvider(-1, "Streaming Server", null);
    }

    public int getProviderId() {
        return this.providerId;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public int getDisplayPriority() {
        return this.displayPriority;
    }

    public String getFullLogoPath() {
        if (this.logoPath != null) {
            return "https://image.tmdb.org/t/p/w92" + this.logoPath;
        }
        return null;
    }
}
