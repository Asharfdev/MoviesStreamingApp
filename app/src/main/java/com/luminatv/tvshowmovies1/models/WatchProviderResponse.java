package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WatchProviderResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private Map<String, CountryProviders> results;

    public int getId() {
        return this.id;
    }

    public Map<String, CountryProviders> getResults() {
        return this.results;
    }

    public static class CountryProviders {

        @SerializedName("ads")
        private List<WatchProvider> ads;

        @SerializedName("buy")
        private List<WatchProvider> buy;

        @SerializedName("flatrate")
        private List<WatchProvider> flatrate;

        @SerializedName("link")
        private String link;

        @SerializedName("rent")
        private List<WatchProvider> rent;

        public String getLink() {
            return this.link;
        }

        public List<WatchProvider> getFlatrate() {
            return this.flatrate;
        }

        public List<WatchProvider> getRent() {
            return this.rent;
        }

        public List<WatchProvider> getBuy() {
            return this.buy;
        }

        public List<WatchProvider> getAds() {
            return this.ads;
        }
    }
}
