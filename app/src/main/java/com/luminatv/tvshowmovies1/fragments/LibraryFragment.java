package com.luminatv.tvshowmovies1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.adapters.ContinueWatchingAdapter;
import com.luminatv.tvshowmovies1.adapters.LocalMediaAdapter;
import com.luminatv.tvshowmovies1.models.LocalMediaItem;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.luminatv.tvshowmovies1.utils.ContinueWatchingStore;
import com.luminatv.tvshowmovies1.utils.LocalLibrary;
import java.util.List;

public class LibraryFragment extends Fragment {
    private ContinueWatchingAdapter continueAdapter;
    private LinearLayout continueSection;
    private View emptyState;
    private LocalMediaAdapter favoritesAdapter;
    private LinearLayout favoritesSection;
    private LocalMediaAdapter recentlyViewedAdapter;
    private LinearLayout recentlyViewedSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        favoritesSection = (LinearLayout) view.findViewById(R.id.sectionFavorites);
        continueSection = (LinearLayout) view.findViewById(R.id.sectionContinueWatching);
        recentlyViewedSection = (LinearLayout) view.findViewById(R.id.sectionRecentlyViewed);
        emptyState = view.findViewById(R.id.libraryEmptyState);

        RecyclerView rvFavorites = (RecyclerView) view.findViewById(R.id.rvFavorites);
        RecyclerView rvContinueWatching = (RecyclerView) view.findViewById(R.id.rvContinueWatching);
        RecyclerView rvRecentlyViewed = (RecyclerView) view.findViewById(R.id.rvRecentlyViewed);

        favoritesAdapter = new LocalMediaAdapter(requireContext());
        continueAdapter = new ContinueWatchingAdapter(requireContext());
        recentlyViewedAdapter = new LocalMediaAdapter(requireContext());

        setupRow(rvFavorites, favoritesAdapter);
        setupContinueWatchingRow(rvContinueWatching);
        setupRow(rvRecentlyViewed, recentlyViewedAdapter);
        refreshLibrary();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLibrary();
    }

    private void setupRow(RecyclerView recyclerView, LocalMediaAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setClipToPadding(false);
    }

    private void setupContinueWatchingRow(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(continueAdapter);
        recyclerView.setClipToPadding(false);
    }

    private void refreshLibrary() {
        if (getContext() == null || favoritesAdapter == null) {
            return;
        }

        List<LocalMediaItem> favorites = LocalLibrary.getFavorites(requireContext());
        List<WatchProgressItem> continueWatching = ContinueWatchingStore.getContinueWatching(requireContext());
        List<LocalMediaItem> recentlyViewed = LocalLibrary.getRecentlyViewed(requireContext());

        bindSection(favoritesSection, favoritesAdapter, favorites);
        bindContinueWatchingSection(continueWatching);
        bindSection(recentlyViewedSection, recentlyViewedAdapter, recentlyViewed);

        boolean isEmpty = favorites.isEmpty() && continueWatching.isEmpty() && recentlyViewed.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void bindSection(LinearLayout section, LocalMediaAdapter adapter, List<LocalMediaItem> items) {
        section.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.setItems(items);
        if (!items.isEmpty()) {
            section.setAlpha(0f);
            section.setTranslationY(20f);
            section.animate().alpha(1f).translationY(0f).setDuration(260L).start();
        }
    }

    private void bindContinueWatchingSection(List<WatchProgressItem> items) {
        continueSection.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        continueAdapter.setItems(items);
        if (!items.isEmpty()) {
            continueSection.setAlpha(0f);
            continueSection.setTranslationY(20f);
            continueSection.animate().alpha(1f).translationY(0f).setDuration(260L).start();
        }
    }
}
