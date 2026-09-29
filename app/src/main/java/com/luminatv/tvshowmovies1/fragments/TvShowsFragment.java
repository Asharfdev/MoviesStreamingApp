package com.luminatv.tvshowmovies1.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.adapters.BannerAdapter;
import com.luminatv.tvshowmovies1.adapters.ContinueWatchingAdapter;
import com.luminatv.tvshowmovies1.adapters.TvShowAdapter;
import com.luminatv.tvshowmovies1.api.RetrofitClient;
import com.luminatv.tvshowmovies1.models.TvShowResponse;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.luminatv.tvshowmovies1.utils.ContinueWatchingStore;
import com.luminatv.tvshowmovies1.utils.LifecycleUtils;
import com.luminatv.tvshowmovies1.utils.OfflineStateController;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class TvShowsFragment extends Fragment {
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private BannerAdapter bannerAdapter;
    private LinearLayout bannerIndicator;
    private ViewPager2 bannerViewPager;
    private ContinueWatchingAdapter continueWatchingAdapter;
    private boolean hasLoadedContent;
    private OfflineStateController offlineStateController;
    private TvShowAdapter onTheAirAdapter;
    private TvShowAdapter popularAdapter;
    private RecyclerView rvOnTheAir;
    private RecyclerView rvPopular;
    private RecyclerView rvContinueWatching;
    private RecyclerView rvTopRated;
    private RecyclerView rvTrending;
    private LinearLayout sectionContinueWatching;
    private SwipeRefreshLayout swipeRefresh;
    private TvShowAdapter topRatedAdapter;
    private TvShowAdapter trendingAdapter;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_tvshows, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.bannerViewPager = (ViewPager2) view.findViewById(R.id.bannerViewPager);
        this.bannerIndicator = (LinearLayout) view.findViewById(R.id.bannerIndicator);
        this.sectionContinueWatching = (LinearLayout) view.findViewById(R.id.sectionContinueWatching);
        this.rvContinueWatching = (RecyclerView) view.findViewById(R.id.rvContinueWatching);
        this.rvTrending = (RecyclerView) view.findViewById(R.id.rvTrending);
        this.rvPopular = (RecyclerView) view.findViewById(R.id.rvPopular);
        this.rvTopRated = (RecyclerView) view.findViewById(R.id.rvTopRated);
        this.rvOnTheAir = (RecyclerView) view.findViewById(R.id.rvOnTheAir);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        this.swipeRefresh = swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent_red, null));
        this.swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.surface_dark, null));
        setupOfflineState(view);
        setupAdapters();
        refreshContinueWatching();
        loadData();
        this.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment$$ExternalSyntheticLambda0
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public final void onRefresh() {
                TvShowsFragment.this.loadData();
            }
        });
        setupAutoScroll();
    }

    private void setupOfflineState(View view) {
        offlineStateController = OfflineStateController.bind(requireContext(), view.findViewById(R.id.offlineStateView));
        offlineStateController.setRetryCallback(new OfflineStateController.RetryCallback() {
            @Override
            public void onRetry() {
                loadData();
            }
        });
        offlineStateController.bindLifecycle(getViewLifecycleOwner());
    }

    private void setupAdapters() {
        BannerAdapter bannerAdapter = new BannerAdapter(requireContext(), "tv");
        this.bannerAdapter = bannerAdapter;
        this.bannerViewPager.setAdapter(bannerAdapter);
        this.bannerViewPager.setOffscreenPageLimit(3);
        this.bannerViewPager.setClipToPadding(false);
        this.bannerViewPager.setClipChildren(false);
        this.bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                TvShowsFragment.this.updateIndicator(i);
            }
        });
        this.continueWatchingAdapter = new ContinueWatchingAdapter(requireContext());
        this.rvContinueWatching.setLayoutManager(new LinearLayoutManager(requireContext(), 0, false));
        this.rvContinueWatching.setAdapter(this.continueWatchingAdapter);
        this.trendingAdapter = new TvShowAdapter(requireContext(), null);
        this.rvTrending.setLayoutManager(new LinearLayoutManager(requireContext(), 0, false));
        this.rvTrending.setAdapter(this.trendingAdapter);
        this.popularAdapter = new TvShowAdapter(requireContext(), null);
        this.rvPopular.setLayoutManager(new LinearLayoutManager(requireContext(), 0, false));
        this.rvPopular.setAdapter(this.popularAdapter);
        this.topRatedAdapter = new TvShowAdapter(requireContext(), null);
        this.rvTopRated.setLayoutManager(new LinearLayoutManager(requireContext(), 0, false));
        this.rvTopRated.setAdapter(this.topRatedAdapter);
        this.onTheAirAdapter = new TvShowAdapter(requireContext(), null);
        this.rvOnTheAir.setLayoutManager(new LinearLayoutManager(requireContext(), 0, false));
        this.rvOnTheAir.setAdapter(this.onTheAirAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContinueWatching();
    }

    private void refreshContinueWatching() {
        if (this.continueWatchingAdapter == null || getContext() == null) {
            return;
        }
        List<WatchProgressItem> items = ContinueWatchingStore.getContinueWatching(requireContext());
        this.continueWatchingAdapter.setItems(items);
        this.sectionContinueWatching.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadData() {
        if (!LifecycleUtils.isFragmentViewAlive(this)) {
            return;
        }
        if (!offlineStateController.ensureOnlineOrShow()) {
            this.swipeRefresh.setRefreshing(false);
            return;
        }
        RetrofitClient.getApi().getAiringTodayTvShows(RetrofitClient.API_KEY, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.2
            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (!LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) || !response.isSuccessful() || response.body() == null) {
                    if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                        TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                    }
                    return;
                }
                if (!response.body().getResults().isEmpty()) {
                    TvShowsFragment.this.hasLoadedContent = true;
                    TvShowsFragment.this.offlineStateController.markContentLoaded();
                }
                TvShowsFragment.this.bannerAdapter.setTvShows(response.body().getResults());
                TvShowsFragment tvShowsFragment = TvShowsFragment.this;
                tvShowsFragment.setupIndicator(tvShowsFragment.bannerAdapter.getItemCount());
            }
        });
        RetrofitClient.getApi().getTrendingTvDay(RetrofitClient.API_KEY, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.3
            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (!LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (!response.body().getResults().isEmpty()) {
                    TvShowsFragment.this.hasLoadedContent = true;
                    TvShowsFragment.this.offlineStateController.markContentLoaded();
                }
                TvShowsFragment.this.trendingAdapter.setTvShows(response.body().getResults());
            }
        });
        RetrofitClient.getApi().getPopularTvShows(RetrofitClient.API_KEY, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.4
            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) && response.isSuccessful() && response.body() != null) {
                    if (!response.body().getResults().isEmpty()) {
                        TvShowsFragment.this.hasLoadedContent = true;
                        TvShowsFragment.this.offlineStateController.markContentLoaded();
                    }
                    TvShowsFragment.this.popularAdapter.setTvShows(response.body().getResults());
                }
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.swipeRefresh.setRefreshing(false);
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.swipeRefresh.setRefreshing(false);
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }
        });
        RetrofitClient.getApi().getTopRatedTvShows(RetrofitClient.API_KEY, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.5
            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (!LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (!response.body().getResults().isEmpty()) {
                    TvShowsFragment.this.hasLoadedContent = true;
                    TvShowsFragment.this.offlineStateController.markContentLoaded();
                }
                TvShowsFragment.this.topRatedAdapter.setTvShows(response.body().getResults());
            }
        });
        RetrofitClient.getApi().getOnTheAirTvShows(RetrofitClient.API_KEY, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.6
            @Override // retrofit2.Callback
            public void onFailure(Call<TvShowResponse> call, Throwable th) {
                if (LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this)) {
                    TvShowsFragment.this.offlineStateController.handleLoadFailure(TvShowsFragment.this.hasLoadedContent);
                }
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (!LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) || !response.isSuccessful() || response.body() == null) {
                    return;
                }
                if (!response.body().getResults().isEmpty()) {
                    TvShowsFragment.this.hasLoadedContent = true;
                    TvShowsFragment.this.offlineStateController.markContentLoaded();
                }
                TvShowsFragment.this.onTheAirAdapter.setTvShows(response.body().getResults());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupIndicator(int i) {
        this.bannerIndicator.removeAllViews();
        int i2 = 0;
        while (i2 < i) {
            View view = new View(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i2 == 0 ? dpToPx(10) : dpToPx(8), i2 == 0 ? dpToPx(10) : dpToPx(8));
            layoutParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(i2 == 0 ? R.drawable.indicator_dot_active : R.drawable.indicator_dot_inactive);
            this.bannerIndicator.addView(view);
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIndicator(int i) {
        for (int i2 = 0; i2 < this.bannerIndicator.getChildCount(); i2++) {
            View childAt = this.bannerIndicator.getChildAt(i2);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            if (i2 == i) {
                layoutParams.width = dpToPx(10);
                layoutParams.height = dpToPx(10);
                childAt.setBackgroundResource(R.drawable.indicator_dot_active);
            } else {
                layoutParams.width = dpToPx(8);
                layoutParams.height = dpToPx(8);
                childAt.setBackgroundResource(R.drawable.indicator_dot_inactive);
            }
            childAt.setLayoutParams(layoutParams);
        }
    }

    private int dpToPx(int i) {
        return (int) (i * getResources().getDisplayMetrics().density);
    }

    private void setupAutoScroll() {
        this.autoScrollHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() { // from class: com.freewatching.magistv4.fragments.TvShowsFragment.7
            @Override // java.lang.Runnable
            public void run() {
                if (!LifecycleUtils.isFragmentViewAlive(TvShowsFragment.this) || TvShowsFragment.this.bannerViewPager == null) {
                    return;
                }
                if (TvShowsFragment.this.bannerAdapter.getItemCount() > 0) {
                    TvShowsFragment.this.bannerViewPager.setCurrentItem((TvShowsFragment.this.bannerViewPager.getCurrentItem() + 1) % TvShowsFragment.this.bannerAdapter.getItemCount(), true);
                }
                TvShowsFragment.this.autoScrollHandler.postDelayed(this, 4000L);
            }
        };
        this.autoScrollRunnable = runnable;
        this.autoScrollHandler.postDelayed(runnable, 4000L);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        Runnable runnable;
        super.onDestroyView();
        Handler handler = this.autoScrollHandler;
        if (handler != null && (runnable = this.autoScrollRunnable) != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
