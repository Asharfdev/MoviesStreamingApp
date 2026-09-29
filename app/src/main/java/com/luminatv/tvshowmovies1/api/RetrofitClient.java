package com.luminatv.tvshowmovies1.api;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    public static final String API_KEY = "97b1c29b907f2ae2f996b39d23c68dcc";
    public static final String BACKDROP_SIZE = "w780";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String LOGO_SIZE = "w92";
    public static final String POSTER_SIZE = "w500";
    public static final String PROFILE_SIZE = "w185";
    private static final int CONNECT_TIMEOUT_SECONDS = 15;
    private static final int READ_TIMEOUT_SECONDS = 20;
    private static final int WRITE_TIMEOUT_SECONDS = 20;

    private static Retrofit retrofit;
    private static TmdbApi tmdbApi;

    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized TmdbApi getApi() {
        if (tmdbApi == null) {
            tmdbApi = getRetrofit().create(TmdbApi.class);
        }
        return tmdbApi;
    }
}
