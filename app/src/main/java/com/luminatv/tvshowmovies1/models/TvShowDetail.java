package com.luminatv.tvshowmovies1.models;

import androidx.core.app.NotificationCompat;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TvShowDetail {

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("episode_run_time")
    private List<Integer> episodeRunTime;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("id")
    private int id;

    @SerializedName("last_air_date")
    private String lastAirDate;

    @SerializedName("name")
    private String name;

    @SerializedName("number_of_episodes")
    private int numberOfEpisodes;

    @SerializedName("number_of_seasons")
    private int numberOfSeasons;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName(NotificationCompat.CATEGORY_STATUS)
    private String status;

    @SerializedName("tagline")
    private String tagline;

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

    public String getLastAirDate() {
        return this.lastAirDate;
    }

    public int getNumberOfSeasons() {
        return this.numberOfSeasons;
    }

    public int getNumberOfEpisodes() {
        return this.numberOfEpisodes;
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

    public List<Integer> getEpisodeRunTime() {
        return this.episodeRunTime;
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
        String str = this.firstAirDate;
        if (str != null && str.length() >= 4) {
            return this.firstAirDate.substring(0, 4);
        }
        return "";
    }

    public String getSeasonsInfo() {
        return this.numberOfSeasons + " Season" + (this.numberOfSeasons != 1 ? "s" : "") + " · " + this.numberOfEpisodes + " Episode" + (this.numberOfEpisodes == 1 ? "" : "s");
    }
}
