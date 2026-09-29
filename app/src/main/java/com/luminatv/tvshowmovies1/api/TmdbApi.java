package com.luminatv.tvshowmovies1.api;

import com.luminatv.tvshowmovies1.models.CreditsResponse;
import com.luminatv.tvshowmovies1.models.MovieDetail;
import com.luminatv.tvshowmovies1.models.MovieResponse;
import com.luminatv.tvshowmovies1.models.SeasonResponse;
import com.luminatv.tvshowmovies1.models.TvShowDetail;
import com.luminatv.tvshowmovies1.models.TvShowResponse;
import com.luminatv.tvshowmovies1.models.VideoResponse;
import com.luminatv.tvshowmovies1.models.WatchProviderResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/* JADX INFO: loaded from: classes.dex */
public interface TmdbApi {
    @GET("discover/movie")
    Call<MovieResponse> discoverMovies(@Query("api_key") String str, @Query("with_genres") String str2, @Query("page") int i);

    @GET("discover/tv")
    Call<TvShowResponse> discoverTvShows(@Query("api_key") String str, @Query("with_genres") String str2, @Query("page") int i);

    @GET("tv/airing_today")
    Call<TvShowResponse> getAiringTodayTvShows(@Query("api_key") String str, @Query("page") int i);

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(@Path("movie_id") int i, @Query("api_key") String str);

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(@Path("movie_id") int i, @Query("api_key") String str);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(@Path("movie_id") int i, @Query("api_key") String str);

    @GET("movie/{movie_id}/watch/providers")
    Call<WatchProviderResponse> getMovieWatchProviders(@Path("movie_id") int i, @Query("api_key") String str);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String str, @Query("page") int i);

    @GET("tv/on_the_air")
    Call<TvShowResponse> getOnTheAirTvShows(@Query("api_key") String str, @Query("page") int i);

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String str, @Query("page") int i);

    @GET("tv/popular")
    Call<TvShowResponse> getPopularTvShows(@Query("api_key") String str, @Query("page") int i);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getRecommendedMovies(@Path("movie_id") int i, @Query("api_key") String str, @Query("page") int i2);

    @GET("tv/{tv_id}/recommendations")
    Call<TvShowResponse> getRecommendedTvShows(@Path("tv_id") int i, @Query("api_key") String str, @Query("page") int i2);

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getSimilarMovies(@Path("movie_id") int i, @Query("api_key") String str, @Query("page") int i2);

    @GET("tv/{tv_id}/similar")
    Call<TvShowResponse> getSimilarTvShows(@Path("tv_id") int i, @Query("api_key") String str, @Query("page") int i2);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String str, @Query("page") int i);

    @GET("tv/top_rated")
    Call<TvShowResponse> getTopRatedTvShows(@Query("api_key") String str, @Query("page") int i);

    @GET("movie/trending/day")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String str, @Query("page") int i);

    @GET("trending/movie/day")
    Call<MovieResponse> getTrendingMoviesDay(@Query("api_key") String str, @Query("page") int i);

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMoviesWeek(@Query("api_key") String str, @Query("page") int i);

    @GET("trending/tv/day")
    Call<TvShowResponse> getTrendingTvDay(@Query("api_key") String str, @Query("page") int i);

    @GET("trending/tv/week")
    Call<TvShowResponse> getTrendingTvWeek(@Query("api_key") String str, @Query("page") int i);

    @GET("tv/{tv_id}/season/{season_number}")
    Call<SeasonResponse> getTvSeasonDetail(@Path("tv_id") int i, @Path("season_number") int i2, @Query("api_key") String str);

    @GET("tv/{tv_id}/credits")
    Call<CreditsResponse> getTvShowCredits(@Path("tv_id") int i, @Query("api_key") String str);

    @GET("tv/{tv_id}")
    Call<TvShowDetail> getTvShowDetail(@Path("tv_id") int i, @Query("api_key") String str);

    @GET("tv/{tv_id}/videos")
    Call<VideoResponse> getTvShowVideos(@Path("tv_id") int i, @Query("api_key") String str);

    @GET("tv/{tv_id}/watch/providers")
    Call<WatchProviderResponse> getTvShowWatchProviders(@Path("tv_id") int i, @Query("api_key") String str);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String str, @Query("page") int i);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("api_key") String str, @Query("query") String str2, @Query("page") int i);

    @GET("search/tv")
    Call<TvShowResponse> searchTvShows(@Query("api_key") String str, @Query("query") String str2, @Query("page") int i);
}
