package com.luminatv.tvshowmovies1.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.adapters.SearchAdapter;
import com.luminatv.tvshowmovies1.api.RetrofitClient;
import com.luminatv.tvshowmovies1.models.MovieResponse;
import com.luminatv.tvshowmovies1.models.TvShowResponse;
import com.luminatv.tvshowmovies1.utils.LifecycleUtils;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import com.luminatv.tvshowmovies1.utils.NetworkUtils;
import com.luminatv.tvshowmovies1.utils.OfflineStateController;
import com.google.android.material.tabs.TabLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class SearchFragment extends Fragment {
    private CopyrightContentFilter copyrightContentFilter;
    private String currentTab = "movie";
    private int searchRequestId = 0;
    private EditText etSearch;
    private String lastSearchQuery = "";
    private OfflineStateController offlineStateController;
    private ProgressBar progressBar;
    private RecyclerView rvSearchResults;
    private SearchAdapter searchAdapter;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private TabLayout tabLayout;
    private TextView txtEmpty;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_search, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.etSearch = (EditText) view.findViewById(R.id.etSearch);
        this.tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        this.rvSearchResults = (RecyclerView) view.findViewById(R.id.rvSearchResults);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.txtEmpty = (TextView) view.findViewById(R.id.txtEmpty);
        this.copyrightContentFilter = new CopyrightContentFilter(requireContext());
        setupOfflineState(view);
        this.searchAdapter = new SearchAdapter(requireContext());
        this.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.rvSearchResults.setAdapter(this.searchAdapter);
        this.copyrightContentFilter.refreshFromRemote(new CopyrightContentFilter.LoadCallback() {
            @Override
            public void onLoaded() {
                if (LifecycleUtils.isFragmentViewAlive(SearchFragment.this)) {
                    String query = etSearch.getText().toString().trim();
                    if (query.length() >= 2 && copyrightContentFilter.isBlocked(query)) {
                        showCopyrightNotice();
                    }
                }
            }
        });
        TabLayout tabLayout = this.tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText("Movies"));
        TabLayout tabLayout2 = this.tabLayout;
        tabLayout2.addTab(tabLayout2.newTab().setText("TV Shows"));
        this.searchHandler = new Handler(Looper.getMainLooper());
        this.etSearch.addTextChangedListener(new AnonymousClass1());
        this.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.freewatching.magistv4.fragments.SearchFragment$$ExternalSyntheticLambda0
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return SearchFragment.this.m330x2faf0be9(textView, i, keyEvent);
            }
        });
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // from class: com.freewatching.magistv4.fragments.SearchFragment.2
            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabSelected(TabLayout.Tab tab) {
                SearchFragment.this.currentTab = tab.getPosition() == 0 ? "movie" : "tv";
                String strTrim = SearchFragment.this.etSearch.getText().toString().trim();
                if (strTrim.length() >= 2) {
                    SearchFragment.this.performSearch(strTrim);
                }
            }
        });
    }

    private void setupOfflineState(View view) {
        offlineStateController = OfflineStateController.bind(requireContext(), view.findViewById(R.id.offlineStateView));
        offlineStateController.setRetryCallback(new OfflineStateController.RetryCallback() {
            @Override
            public void onRetry() {
                String query = lastSearchQuery;
                if (query == null || query.length() < 2) {
                    query = etSearch.getText().toString().trim();
                }
                if (query.length() >= 2) {
                    performSearch(query);
                }
            }
        });
        offlineStateController.bindLifecycle(getViewLifecycleOwner());
    }

    /* JADX INFO: renamed from: com.freewatching.magistv4.fragments.SearchFragment$1, reason: invalid class name */
    class AnonymousClass1 implements TextWatcher {
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        AnonymousClass1() {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(final CharSequence charSequence, int i, int i2, int i3) {
            if (SearchFragment.this.searchRunnable != null) {
                SearchFragment.this.searchHandler.removeCallbacks(SearchFragment.this.searchRunnable);
            }
            SearchFragment.this.searchRunnable = new Runnable() { // from class: com.freewatching.magistv4.fragments.SearchFragment$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass1.this.m331xcbcd4d72(charSequence);
                }
            };
            SearchFragment.this.searchHandler.postDelayed(SearchFragment.this.searchRunnable, 500L);
        }

        /* JADX INFO: renamed from: lambda$onTextChanged$0$com-freewatching-magistv4-fragments-SearchFragment$1, reason: not valid java name */
        /* synthetic */ void m331xcbcd4d72(CharSequence charSequence) {
            String strTrim = charSequence.toString().trim();
            int length = strTrim.length();
            SearchFragment searchFragment = SearchFragment.this;
            if (length >= 2) {
                searchFragment.performSearch(strTrim);
                return;
            }
            searchFragment.searchAdapter.clear();
            SearchFragment.this.txtEmpty.setVisibility(0);
            SearchFragment.this.txtEmpty.setText("Search for your favorite\nmovies and TV shows");
        }
    }

    /* JADX INFO: renamed from: lambda$onViewCreated$0$com-freewatching-magistv4-fragments-SearchFragment, reason: not valid java name */
    /* synthetic */ boolean m330x2faf0be9(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 3) {
            return false;
        }
        String strTrim = this.etSearch.getText().toString().trim();
        if (strTrim.length() < 2) {
            return true;
        }
        performSearch(strTrim);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performSearch(final String str) {
        if (!LifecycleUtils.isFragmentViewAlive(this)) {
            return;
        }
        if (copyrightContentFilter != null && copyrightContentFilter.isBlocked(str)) {
            showCopyrightNotice();
            return;
        }
        lastSearchQuery = str;
        if (!offlineStateController.ensureOnlineOrShow()) {
            this.progressBar.setVisibility(8);
            this.txtEmpty.setVisibility(8);
            this.rvSearchResults.setVisibility(8);
            return;
        }
        offlineStateController.hide(true);
        final int requestId = ++this.searchRequestId;
        this.progressBar.setVisibility(0);
        this.txtEmpty.setVisibility(8);
        this.rvSearchResults.setVisibility(8);
        if ("movie".equals(this.currentTab)) {
            RetrofitClient.getApi().searchMovies(RetrofitClient.API_KEY, str, 1).enqueue(new Callback<MovieResponse>() { // from class: com.freewatching.magistv4.fragments.SearchFragment.3
                @Override // retrofit2.Callback
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (!LifecycleUtils.isFragmentViewAlive(SearchFragment.this) || requestId != SearchFragment.this.searchRequestId) {
                        return;
                    }
                    SearchFragment.this.progressBar.setVisibility(8);
                    if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                        SearchFragment.this.searchAdapter.setMovies(response.body().getResults());
                        SearchFragment.this.rvSearchResults.setVisibility(0);
                        SearchFragment.this.txtEmpty.setVisibility(8);
                    } else {
                        SearchFragment.this.txtEmpty.setVisibility(0);
                        SearchFragment.this.txtEmpty.setText("No movies found for \"" + str + "\"");
                    }
                }

                @Override // retrofit2.Callback
                public void onFailure(Call<MovieResponse> call, Throwable th) {
                    if (!LifecycleUtils.isFragmentViewAlive(SearchFragment.this) || requestId != SearchFragment.this.searchRequestId) {
                        return;
                    }
                    SearchFragment.this.progressBar.setVisibility(8);
                    if (!NetworkUtils.isNetworkAvailable(SearchFragment.this.requireContext())) {
                        SearchFragment.this.txtEmpty.setVisibility(8);
                        SearchFragment.this.rvSearchResults.setVisibility(8);
                        SearchFragment.this.offlineStateController.show(true);
                        return;
                    }
                    SearchFragment.this.txtEmpty.setVisibility(0);
                    SearchFragment.this.txtEmpty.setText("Error searching. Check your connection.");
                }
            });
        } else {
            RetrofitClient.getApi().searchTvShows(RetrofitClient.API_KEY, str, 1).enqueue(new Callback<TvShowResponse>() { // from class: com.freewatching.magistv4.fragments.SearchFragment.4
                @Override // retrofit2.Callback
                public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                    if (!LifecycleUtils.isFragmentViewAlive(SearchFragment.this) || requestId != SearchFragment.this.searchRequestId) {
                        return;
                    }
                    SearchFragment.this.progressBar.setVisibility(8);
                    if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                        SearchFragment.this.searchAdapter.setTvShows(response.body().getResults());
                        SearchFragment.this.rvSearchResults.setVisibility(0);
                        SearchFragment.this.txtEmpty.setVisibility(8);
                    } else {
                        SearchFragment.this.txtEmpty.setVisibility(0);
                        SearchFragment.this.txtEmpty.setText("No TV shows found for \"" + str + "\"");
                    }
                }

                @Override // retrofit2.Callback
                public void onFailure(Call<TvShowResponse> call, Throwable th) {
                    if (!LifecycleUtils.isFragmentViewAlive(SearchFragment.this) || requestId != SearchFragment.this.searchRequestId) {
                        return;
                    }
                    SearchFragment.this.progressBar.setVisibility(8);
                    if (!NetworkUtils.isNetworkAvailable(SearchFragment.this.requireContext())) {
                        SearchFragment.this.txtEmpty.setVisibility(8);
                        SearchFragment.this.rvSearchResults.setVisibility(8);
                        SearchFragment.this.offlineStateController.show(true);
                        return;
                    }
                    SearchFragment.this.txtEmpty.setVisibility(0);
                    SearchFragment.this.txtEmpty.setText("Error searching. Check your connection.");
                }
            });
        }
    }

    private void showCopyrightNotice() {
        searchRequestId++;
        lastSearchQuery = "";
        progressBar.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        searchAdapter.clear();
        txtEmpty.setVisibility(View.VISIBLE);
        txtEmpty.setText(CopyrightContentFilter.COPYRIGHT_NOTICE);
        CopyrightContentFilter.showBlockedNotice(requireContext());
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        if (this.searchHandler != null && this.searchRunnable != null) {
            this.searchHandler.removeCallbacks(this.searchRunnable);
        }
        this.searchRequestId++;
        super.onDestroyView();
    }
}
