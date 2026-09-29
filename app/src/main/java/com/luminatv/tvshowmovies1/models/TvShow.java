package com.luminatv.tvshowmovies1.models;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class TvShow {

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("genre_ids")
    private int[] genreIds;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getBackdropPath() {
        return this.backdropPath;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }

    public String getFirstAirDate() {
        return this.firstAirDate;
    }

    public int[] getGenreIds() {
        return this.genreIds;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public int getVoteCount() {
        return this.voteCount;
    }

    public String getOriginalLanguage() {
        return this.originalLanguage;
    }

    public String getFullPosterPath() {
        if (this.posterPath != null) {
            return "https://image.tmdb.org/t/p/w500" + this.posterPath;
        }
        return null;
    }

    public String getFullBackdropPath() {
        if (this.backdropPath != null) {
            return "https://image.tmdb.org/t/p/w780" + this.backdropPath;
        }
        return null;
    }
}
