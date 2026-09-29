package com.luminatv.tvshowmovies1.models;

import androidx.core.app.NotificationCompat;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MovieDetail {

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("budget")
    private long budget;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("id")
    private int id;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("revenue")
    private long revenue;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName(NotificationCompat.CATEGORY_STATUS)
    private String status;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("title")
    private String title;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
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

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public List<Genre> getGenres() {
        return this.genres;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTagline() {
        return this.tagline;
    }

    public long getBudget() {
        return this.budget;
    }

    public long getRevenue() {
        return this.revenue;
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

    public String getFormattedRuntime() {
        int i = this.runtime;
        if (i <= 0) {
            return "";
        }
        int i2 = i / 60;
        int i3 = i % 60;
        return i2 > 0 ? i2 + "h " + i3 + "m" : i3 + "m";
    }

    public String getGenreString() {
        List<Genre> list = this.genres;
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.genres.size(); i++) {
            sb.append(this.genres.get(i).getName());
            if (i < this.genres.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public String getYear() {
        String str = this.releaseDate;
        if (str != null && str.length() >= 4) {
            return this.releaseDate.substring(0, 4);
        }
        return "";
    }
}
