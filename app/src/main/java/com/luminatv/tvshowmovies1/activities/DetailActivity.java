package com.luminatv.tvshowmovies1.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.adapters.CastAdapter;
import com.luminatv.tvshowmovies1.adapters.EpisodeAdapter;
import com.luminatv.tvshowmovies1.adapters.MovieAdapter;
import com.luminatv.tvshowmovies1.adapters.TvShowAdapter;
import com.luminatv.tvshowmovies1.adapters.VideoAdapter;
import com.luminatv.tvshowmovies1.api.RetrofitClient;
import com.luminatv.tvshowmovies1.fragments.StreamingDialogFragment;
import com.luminatv.tvshowmovies1.fragments.TrailerDialogFragment;
import com.luminatv.tvshowmovies1.models.CreditsResponse;
import com.luminatv.tvshowmovies1.models.Episode;
import com.luminatv.tvshowmovies1.models.LocalMediaItem;
import com.luminatv.tvshowmovies1.models.MovieDetail;
import com.luminatv.tvshowmovies1.models.MovieResponse;
import com.luminatv.tvshowmovies1.models.SeasonResponse;
import com.luminatv.tvshowmovies1.models.TvShowDetail;
import com.luminatv.tvshowmovies1.models.TvShowResponse;
import com.luminatv.tvshowmovies1.models.Video;
import com.luminatv.tvshowmovies1.models.VideoResponse;
import com.luminatv.tvshowmovies1.models.WatchProvider;
import com.luminatv.tvshowmovies1.models.WatchProviderResponse;
import com.luminatv.tvshowmovies1.utils.LocalLibrary;
import com.luminatv.tvshowmovies1.utils.LifecycleUtils;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import com.luminatv.tvshowmovies1.utils.NetworkUtils;
import com.luminatv.tvshowmovies1.utils.OfflineStateController;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class DetailActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {
    private ImageView btnBack;
    private ImageView btnFavorite;
    private Button btnPlayNow;
    private LinearLayout btnPlayTrailerOverlay;
    private Button btnTrailer;
    private CastAdapter castAdapter;
    private ChipGroup chipGroupSeasons;
    private EpisodeAdapter episodeAdapter;
    private ImageView imgBackdrop;
    private ImageView imgPoster;
    private int itemId;
    private String itemType;
    private boolean hasLoadedContent;
    private OfflineStateController offlineStateController;
    private ProgressBar progressBar;
    private RecyclerView rvCast;
    private RecyclerView rvEpisodes;
    private RecyclerView rvSimilar;
    private RecyclerView rvVideos;
    private LinearLayout seasonsContainer;
    private MovieAdapter similarMovieAdapter;
    private TvShowAdapter similarTvShowAdapter;
    private TextView txtCastLabel;
    private TextView txtGenres;
    private TextView txtMeta;
    private TextView txtOverview;
    private TextView txtRating;
    private TextView txtSimilarLabel;
    private TextView txtStatus;
    private TextView txtTagline;
    private TextView txtTitle;
    private TextView txtVideosLabel;
    private TextView txtVoteCount;
    private VideoAdapter videoAdapter;
    private String trailerKey = null;
    private LocalMediaItem currentMediaItem = null;
    private String currentTitle = "";
    private ArrayList<WatchProvider> allProviders = new ArrayList<>();
    private int totalSeasons = 0;
    private int selectedSeason = 1;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark, getTheme()));
        this.itemId = getIntent().getIntExtra("id", -1);
        String stringExtra = getIntent().getStringExtra("type");
        this.itemType = stringExtra;
        if (this.itemId == -1 || stringExtra == null) {
            finish();
            return;
        }
        initViews();
        setupOfflineState();
        setupAdapters();
        loadData();
    }

    private void setupOfflineState() {
        offlineStateController = OfflineStateController.bind(this, findViewById(R.id.offlineStateView));
        offlineStateController.setRetryCallback(new OfflineStateController.RetryCallback() {
            @Override
            public void onRetry() {
                loadData();
            }
        });
        offlineStateController.bindLifecycle(this);
    }

    private void initViews() {
        this.imgBackdrop = (ImageView) findViewById(R.id.imgBackdrop);
        this.imgPoster = (ImageView) findViewById(R.id.imgPoster);
        this.btnBack = (ImageView) findViewById(R.id.btnBack);
        this.btnFavorite = (ImageView) findViewById(R.id.btnFavorite);
        this.txtTitle = (TextView) findViewById(R.id.txtTitle);
        this.txtTagline = (TextView) findViewById(R.id.txtTagline);
        this.txtRating = (TextView) findViewById(R.id.txtRating);
        this.txtVoteCount = (TextView) findViewById(R.id.txtVoteCount);
        this.txtMeta = (TextView) findViewById(R.id.txtMeta);
        this.txtGenres = (TextView) findViewById(R.id.txtGenres);
        this.txtStatus = (TextView) findViewById(R.id.txtStatus);
        this.txtOverview = (TextView) findViewById(R.id.txtOverview);
        this.btnPlayNow = (Button) findViewById(R.id.btnPlayNow);
        this.btnTrailer = (Button) findViewById(R.id.btnTrailer);
        this.btnPlayTrailerOverlay = (LinearLayout) findViewById(R.id.btnPlayTrailerOverlay);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.txtVideosLabel = (TextView) findViewById(R.id.txtVideosLabel);
        this.txtCastLabel = (TextView) findViewById(R.id.txtCastLabel);
        this.txtSimilarLabel = (TextView) findViewById(R.id.txtSimilarLabel);
        this.rvVideos = (RecyclerView) findViewById(R.id.rvVideos);
        this.rvCast = (RecyclerView) findViewById(R.id.rvCast);
        this.rvSimilar = (RecyclerView) findViewById(R.id.rvSimilar);
        this.seasonsContainer = (LinearLayout) findViewById(R.id.seasonsContainer);
        this.chipGroupSeasons = (ChipGroup) findViewById(R.id.chipGroupSeasons);
        this.rvEpisodes = (RecyclerView) findViewById(R.id.rvEpisodes);
        this.btnBack.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DetailActivity.this.m287xd32f9cb3(view);
            }
        });
        this.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.this.toggleFavorite();
            }
        });
        this.btnPlayNow.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DetailActivity.this.m288x606a4e34(view);
            }
        });
        this.btnTrailer.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DetailActivity.this.m289xeda4ffb5(view);
            }
        });
        this.btnPlayTrailerOverlay.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DetailActivity.this.m290x7adfb136(view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initViews$0$com-freewatching-magistv4-activities-DetailActivity, reason: not valid java name */
    /* synthetic */ void m287xd32f9cb3(View view) {
        finish();
    }

    /* JADX INFO: renamed from: lambda$initViews$1$com-freewatching-magistv4-activities-DetailActivity, reason: not valid java name */
    /* synthetic */ void m288x606a4e34(View view) {
        AppLAds.INSTANCE.showInterstitial();
        showStreamingDialog();
    }

    /* JADX INFO: renamed from: lambda$initViews$2$com-freewatching-magistv4-activities-DetailActivity, reason: not valid java name */
    /* synthetic */ void m289xeda4ffb5(View view) {
        String str = this.trailerKey;
        if (str != null) {
            showTrailerDialog(str, "Trailer");
        } else {
            Toast.makeText(this, "No trailer available", 0).show();
        }
    }

    /* JADX INFO: renamed from: lambda$initViews$3$com-freewatching-magistv4-activities-DetailActivity, reason: not valid java name */
    /* synthetic */ void m290x7adfb136(View view) {
        String str = this.trailerKey;
        if (str != null) {
            showTrailerDialog(str, "Trailer");
        } else {
            Toast.makeText(this, "No trailer available yet", 0).show();
        }
    }

    private void setupAdapters() {
        this.videoAdapter = new VideoAdapter(this, null, this);
        this.rvVideos.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.rvVideos.setAdapter(this.videoAdapter);
        this.castAdapter = new CastAdapter(this, null);
        this.rvCast.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.rvCast.setAdapter(this.castAdapter);
        if ("movie".equals(this.itemType)) {
            this.similarMovieAdapter = new MovieAdapter(this, null);
            this.rvSimilar.setLayoutManager(new LinearLayoutManager(this, 0, false));
            this.rvSimilar.setAdapter(this.similarMovieAdapter);
        } else {
            this.similarTvShowAdapter = new TvShowAdapter(this, null);
            this.rvSimilar.setLayoutManager(new LinearLayoutManager(this, 0, false));
            this.rvSimilar.setAdapter(this.similarTvShowAdapter);
            this.episodeAdapter = new EpisodeAdapter(this, null, new EpisodeAdapter.OnEpisodeClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda4
                @Override // com.freewatching.magistv4.adapters.EpisodeAdapter.OnEpisodeClickListener
                public final void onEpisodeClick(Episode episode) {
                    DetailActivity.this.m291xab567a5a(episode);
                }
            });
            this.rvEpisodes.setLayoutManager(new LinearLayoutManager(this));
            this.rvEpisodes.setAdapter(this.episodeAdapter);
        }
    }

    private void loadData() {
        this.progressBar.setVisibility(0);
        if (!offlineStateController.ensureOnlineOrShow()) {
            this.progressBar.setVisibility(8);
            return;
        }
        if ("movie".equals(this.itemType)) {
            loadMovieDetail(RetrofitClient.API_KEY);
            loadMovieVideos(RetrofitClient.API_KEY);
            loadMovieCredits(RetrofitClient.API_KEY);
            loadMovieWatchProviders(RetrofitClient.API_KEY);
            loadSimilarMovies(RetrofitClient.API_KEY);
            return;
        }
        loadTvShowDetail(RetrofitClient.API_KEY);
        loadTvShowVideos(RetrofitClient.API_KEY);
        loadTvShowCredits(RetrofitClient.API_KEY);
        loadTvShowWatchProviders(RetrofitClient.API_KEY);
        loadSimilarTvShows(RetrofitClient.API_KEY);
    }

    private void loadMovieDetail(String str) {
        RetrofitClient.getApi().getMovieDetail(this.itemId, str).enqueue(new Callback<MovieDetail>() { // from class: com.freewatching.magistv4.activities.DetailActivity.1
            @Override // retrofit2.Callback
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this)) {
                    return;
                }
                DetailActivity.this.progressBar.setVisibility(8);
                if (!response.isSuccessful() || response.body() == null) {
                    DetailActivity.this.showDetailLoadError();
                    return;
                }
                DetailActivity.this.displayMovieDetail(response.body());
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<MovieDetail> call, Throwable th) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this)) {
                    return;
                }
                DetailActivity.this.showDetailLoadError();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayMovieDetail(MovieDetail movieDetail) {
        String title = movieDetail.getTitle() != null ? movieDetail.getTitle() : "Untitled";
        if (CopyrightContentFilter.isTitleBlocked(this, title)) {
            CopyrightContentFilter.showBlockedNotice(this);
            finish();
            return;
        }
        this.currentTitle = title;
        this.currentMediaItem = new LocalMediaItem(
                movieDetail.getId(),
                "movie",
                title,
                buildSubtitle(movieDetail.getYear(), movieDetail.getGenreString()),
                movieDetail.getFullPosterPath(),
                movieDetail.getFullBackdropPath(),
                movieDetail.getVoteAverage());
        this.txtTitle.setText(title);
        this.txtOverview.setText(movieDetail.getOverview() != null ? movieDetail.getOverview() : "");
        this.txtRating.setText(String.format("%.1f", Double.valueOf(movieDetail.getVoteAverage())));
        this.txtVoteCount.setText("(" + movieDetail.getVoteCount() + " votes)");
        this.txtGenres.setText(movieDetail.getGenreString() != null ? movieDetail.getGenreString() : "");
        this.txtStatus.setText(movieDetail.getStatus() != null ? movieDetail.getStatus() : "");
        String year = movieDetail.getYear();
        if (movieDetail.getRuntime() > 0) {
            year = year + " · " + movieDetail.getFormattedRuntime();
        }
        this.txtMeta.setText(year);
        if (movieDetail.getTagline() != null && !movieDetail.getTagline().isEmpty()) {
            this.txtTagline.setText(movieDetail.getTagline());
            this.txtTagline.setVisibility(0);
        }
        Glide.with((FragmentActivity) this).load(movieDetail.getFullBackdropPath()).into(this.imgBackdrop);
        Glide.with((FragmentActivity) this).load(movieDetail.getFullPosterPath()).into(this.imgPoster);
        LocalLibrary.addRecentlyViewed(this, this.currentMediaItem);
        updateFavoriteState();
        animateDetailContent();
        this.hasLoadedContent = true;
        this.offlineStateController.markContentLoaded();
    }

    private void loadMovieVideos(String str) {
        RetrofitClient.getApi().getMovieVideos(this.itemId, str).enqueue(new Callback<VideoResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.2
            @Override // retrofit2.Callback
            public void onFailure(Call<VideoResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                DetailActivity.this.processVideos(response.body().getResults());
            }
        });
    }

    private void loadMovieCredits(String str) {
        RetrofitClient.getApi().getMovieCredits(this.itemId, str).enqueue(new Callback<CreditsResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.3
            @Override // retrofit2.Callback
            public void onFailure(Call<CreditsResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (response.body().getCast().isEmpty()) {
                    return;
                }
                DetailActivity.this.castAdapter.setCastList(response.body().getCast());
                DetailActivity.this.txtCastLabel.setVisibility(0);
                DetailActivity.this.rvCast.setVisibility(0);
            }
        });
    }

    private void loadMovieWatchProviders(String str) {
        RetrofitClient.getApi().getMovieWatchProviders(this.itemId, str).enqueue(new Callback<WatchProviderResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.4
            @Override // retrofit2.Callback
            public void onFailure(Call<WatchProviderResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<WatchProviderResponse> call, Response<WatchProviderResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                DetailActivity.this.processWatchProviders(response.body());
            }
        });
    }

    private void loadSimilarMovies(String str) {
        RetrofitClient.getApi().getSimilarMovies(this.itemId, str, 1).enqueue(new Callback<MovieResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.5
            @Override // retrofit2.Callback
            public void onFailure(Call<MovieResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (response.body().getResults().isEmpty()) {
                    return;
                }
                DetailActivity.this.similarMovieAdapter.setMovies(response.body().getResults());
                DetailActivity.this.txtSimilarLabel.setVisibility(0);
                DetailActivity.this.rvSimilar.setVisibility(0);
            }
        });
    }

    private void loadTvShowDetail(String str) {
        RetrofitClient.getApi().getTvShowDetail(this.itemId, str).enqueue(new Callback<TvShowDetail>() { // from class: com.freewatching.magistv4.activities.DetailActivity.6
            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowDetail> call, Response<TvShowDetail> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this)) {
                    return;
                }
                DetailActivity.this.progressBar.setVisibility(8);
                if (!response.isSuccessful() || response.body() == null) {
                    DetailActivity.this.showDetailLoadError();
                    return;
                }
                DetailActivity.this.displayTvShowDetail(response.body());
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowDetail> call, Throwable th) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this)) {
                    return;
                }
                DetailActivity.this.showDetailLoadError();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayTvShowDetail(TvShowDetail tvShowDetail) {
        String title = tvShowDetail.getName() != null ? tvShowDetail.getName() : "Untitled";
        if (CopyrightContentFilter.isTitleBlocked(this, title)) {
            CopyrightContentFilter.showBlockedNotice(this);
            finish();
            return;
        }
        this.currentTitle = title;
        this.currentMediaItem = new LocalMediaItem(
                tvShowDetail.getId(),
                "tv",
                title,
                buildSubtitle(tvShowDetail.getYear(), tvShowDetail.getGenreString()),
                tvShowDetail.getFullPosterPath(),
                tvShowDetail.getFullBackdropPath(),
                tvShowDetail.getVoteAverage());
        this.txtTitle.setText(title);
        this.txtOverview.setText(tvShowDetail.getOverview() != null ? tvShowDetail.getOverview() : "");
        this.txtRating.setText(String.format("%.1f", Double.valueOf(tvShowDetail.getVoteAverage())));
        this.txtVoteCount.setText("(" + tvShowDetail.getVoteCount() + " votes)");
        this.txtGenres.setText(tvShowDetail.getGenreString() != null ? tvShowDetail.getGenreString() : "");
        this.txtStatus.setText(tvShowDetail.getStatus() != null ? tvShowDetail.getStatus() : "");
        this.txtMeta.setText(tvShowDetail.getYear() + " · " + tvShowDetail.getSeasonsInfo());
        if (tvShowDetail.getTagline() != null && !tvShowDetail.getTagline().isEmpty()) {
            this.txtTagline.setText(tvShowDetail.getTagline());
            this.txtTagline.setVisibility(0);
        }
        Glide.with((FragmentActivity) this).load(tvShowDetail.getFullBackdropPath()).into(this.imgBackdrop);
        Glide.with((FragmentActivity) this).load(tvShowDetail.getFullPosterPath()).into(this.imgPoster);
        LocalLibrary.addRecentlyViewed(this, this.currentMediaItem);
        updateFavoriteState();
        animateDetailContent();
        int numberOfSeasons = tvShowDetail.getNumberOfSeasons();
        this.totalSeasons = numberOfSeasons;
        if (numberOfSeasons > 0) {
            setupSeasonChips(numberOfSeasons);
            loadSeasonEpisodes(1);
        }
        this.hasLoadedContent = true;
        this.offlineStateController.markContentLoaded();
    }

    private void loadTvShowVideos(String str) {
        RetrofitClient.getApi().getTvShowVideos(this.itemId, str).enqueue(new Callback<VideoResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.7
            @Override // retrofit2.Callback
            public void onFailure(Call<VideoResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                DetailActivity.this.processVideos(response.body().getResults());
            }
        });
    }

    private void loadTvShowCredits(String str) {
        RetrofitClient.getApi().getTvShowCredits(this.itemId, str).enqueue(new Callback<CreditsResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.8
            @Override // retrofit2.Callback
            public void onFailure(Call<CreditsResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (response.body().getCast().isEmpty()) {
                    return;
                }
                DetailActivity.this.castAdapter.setCastList(response.body().getCast());
                DetailActivity.this.txtCastLabel.setVisibility(0);
                DetailActivity.this.rvCast.setVisibility(0);
            }
        });
    }

    private void loadTvShowWatchProviders(String str) {
        RetrofitClient.getApi().getTvShowWatchProviders(this.itemId, str).enqueue(new Callback<WatchProviderResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.9
            @Override // retrofit2.Callback
            public void onFailure(Call<WatchProviderResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<WatchProviderResponse> call, Response<WatchProviderResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                DetailActivity.this.processWatchProviders(response.body());
            }
        });
    }

    private void loadSimilarTvShows(String str) {
        RetrofitClient.getApi().getSimilarTvShows(this.itemId, str, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.10
            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (response.body().getResults().isEmpty()) {
                    return;
                }
                DetailActivity.this.similarTvShowAdapter.setTvShows(response.body().getResults());
                DetailActivity.this.txtSimilarLabel.setVisibility(0);
                DetailActivity.this.rvSimilar.setVisibility(0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processVideos(List<Video> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (Video video : list) {
            if (video.isYouTube()) {
                arrayList.add(video);
                if (this.trailerKey == null && video.isYouTubeTrailer()) {
                    this.trailerKey = video.getKey();
                }
            }
        }
        if (this.trailerKey == null && !arrayList.isEmpty()) {
            this.trailerKey = ((Video) arrayList.get(0)).getKey();
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.videoAdapter.setVideos(arrayList);
        this.txtVideosLabel.setVisibility(0);
        this.rvVideos.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processWatchProviders(WatchProviderResponse watchProviderResponse) {
        WatchProviderResponse.CountryProviders next;
        if (watchProviderResponse.getResults() == null) {
            return;
        }
        if (watchProviderResponse.getResults().containsKey("US")) {
            next = watchProviderResponse.getResults().get("US");
        } else if (watchProviderResponse.getResults().containsKey("IN")) {
            next = watchProviderResponse.getResults().get("IN");
        } else if (watchProviderResponse.getResults().containsKey("GB")) {
            next = watchProviderResponse.getResults().get("GB");
        } else {
            next = !watchProviderResponse.getResults().isEmpty() ? watchProviderResponse.getResults().values().iterator().next() : null;
        }
        if (next == null) {
            return;
        }
        this.allProviders.clear();
        if (next.getFlatrate() != null) {
            this.allProviders.addAll(next.getFlatrate());
        }
        if (next.getRent() != null) {
            this.allProviders.addAll(next.getRent());
        }
        if (next.getBuy() != null) {
            this.allProviders.addAll(next.getBuy());
        }
    }

    private void setupSeasonChips(int i) {
        this.seasonsContainer.setVisibility(0);
        this.chipGroupSeasons.removeAllViews();
        this.chipGroupSeasons.setSelectionRequired(true);
        this.chipGroupSeasons.setSingleSelection(true);
        int id = -1;
        for (int i2 = 1; i2 <= i; i2++) {
            final int seasonNumber = i2;
            Chip chip = new Chip(this);
            chip.setId(View.generateViewId());
            chip.setText("Season " + seasonNumber);
            chip.setCheckable(true);
            chip.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));
            chip.setChipBackgroundColorResource(R.color.surface_dark);
            chip.setChipStrokeColorResource(R.color.divider_dark);
            chip.setChipStrokeWidth(2.0f);
            if (seasonNumber == 1) {
                id = chip.getId();
            }
            chip.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.DetailActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DetailActivity.this.m292x29b6965b(seasonNumber, view);
                }
            });
            this.chipGroupSeasons.addView(chip);
        }
        if (id != -1) {
            this.chipGroupSeasons.check(id);
        }
    }

    /* JADX INFO: renamed from: lambda$setupSeasonChips$5$com-freewatching-magistv4-activities-DetailActivity, reason: not valid java name */
    /* synthetic */ void m292x29b6965b(int i, View view) {
        this.selectedSeason = i;
        loadSeasonEpisodes(i);
    }

    private void loadSeasonEpisodes(int i) {
        RetrofitClient.getApi().getTvSeasonDetail(this.itemId, i, RetrofitClient.API_KEY).enqueue(new Callback<SeasonResponse>() { // from class: com.freewatching.magistv4.activities.DetailActivity.11
            @Override // retrofit2.Callback
            public void onResponse(Call<SeasonResponse> call, Response<SeasonResponse> response) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (response.body().getEpisodes().isEmpty()) {
                    return;
                }
                DetailActivity.this.episodeAdapter.setEpisodes(response.body().getEpisodes());
                DetailActivity.this.rvEpisodes.setVisibility(0);
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<SeasonResponse> call, Throwable th) {
                if (!LifecycleUtils.isActivityAlive(DetailActivity.this)) {
                    return;
                }
                Toast.makeText(DetailActivity.this, "Failed to load episodes", 0).show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: showEpisodeStreamingDialog, reason: merged with bridge method [inline-methods] */
    public void m291xab567a5a(Episode episode) {
        StreamingDialogFragment.newInstance(this.itemId, this.itemType, this.currentTitle + " - S" + episode.getSeasonNumber() + ExifInterface.LONGITUDE_EAST + episode.getEpisodeNumber() + " " + episode.getName(), this.allProviders, episode.getSeasonNumber(), episode.getEpisodeNumber(), getCurrentPosterPath(), getCurrentBackdropPath()).show(getSupportFragmentManager(), "streaming_dialog");
    }

    private String buildSubtitle(String year, String genres) {
        boolean hasYear = year != null && !year.isEmpty();
        boolean hasGenres = genres != null && !genres.isEmpty();
        if (hasYear && hasGenres) {
            return year + " · " + genres;
        }
        if (hasYear) {
            return year;
        }
        return hasGenres ? genres : "Streaming title";
    }

    private void toggleFavorite() {
        if (this.currentMediaItem == null) {
            Toast.makeText(this, "Title is still loading", 0).show();
            return;
        }
        boolean isFavorite = LocalLibrary.toggleFavorite(this, this.currentMediaItem);
        updateFavoriteState();
        Toast.makeText(this, isFavorite ? "Added to Favorites" : "Removed from Favorites", 0).show();
    }

    private void updateFavoriteState() {
        if (this.btnFavorite == null) {
            return;
        }
        boolean isFavorite = this.currentMediaItem != null && LocalLibrary.isFavorite(this, this.currentMediaItem.getId(), this.currentMediaItem.getType());
        this.btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite);
        this.btnFavorite.setScaleX(0.88f);
        this.btnFavorite.setScaleY(0.88f);
        this.btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(180L).start();
    }

    private void animateDetailContent() {
        this.imgPoster.setAlpha(0f);
        this.imgPoster.setTranslationY(22f);
        this.imgPoster.animate().alpha(1f).translationY(0f).setDuration(320L).start();

        this.txtTitle.setAlpha(0f);
        this.txtTitle.setTranslationY(20f);
        this.txtTitle.animate().alpha(1f).translationY(0f).setStartDelay(90L).setDuration(320L).start();
    }

    private void showTrailerDialog(String str, String str2) {
        TrailerDialogFragment.newInstance(str, str2).show(getSupportFragmentManager(), "trailer_dialog");
    }

    private void showStreamingDialog() {
        StreamingDialogFragment.newInstance(this.itemId, this.itemType, this.currentTitle, this.allProviders, 0, 0, getCurrentPosterPath(), getCurrentBackdropPath()).show(getSupportFragmentManager(), "streaming_dialog");
    }

    private String getCurrentPosterPath() {
        return this.currentMediaItem == null ? null : this.currentMediaItem.getPosterUrl();
    }

    private String getCurrentBackdropPath() {
        return this.currentMediaItem == null ? null : this.currentMediaItem.getBackdropUrl();
    }

    private void showDetailLoadError() {
        this.progressBar.setVisibility(8);
        if (!this.hasLoadedContent && !NetworkUtils.isNetworkAvailable(this)) {
            this.offlineStateController.show(true);
            return;
        }
        Toast.makeText(this, "Failed to load details", 0).show();
        finish();
    }

    @Override // com.freewatching.magistv4.adapters.VideoAdapter.OnVideoClickListener
    public void onVideoClick(Video video) {
        if (video != null && video.isYouTube() && video.getKey() != null && !video.getKey().isEmpty()) {
            showTrailerDialog(video.getKey(), video.getName());
        }
    }

}
