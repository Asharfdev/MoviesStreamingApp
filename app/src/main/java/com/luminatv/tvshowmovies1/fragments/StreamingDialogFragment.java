package com.luminatv.tvshowmovies1.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.os.BundleCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.PlayerActivity;
import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.luminatv.tvshowmovies1.models.WatchProvider;
import com.luminatv.tvshowmovies1.utils.ContinueWatchingStore;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class StreamingDialogFragment extends BottomSheetDialogFragment {
    private static final String ARG_EPISODE = "episode";
    private static final String ARG_BACKDROP_PATH = "backdrop_path";
    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ITEM_TITLE = "item_title";
    private static final String ARG_ITEM_TYPE = "item_type";
    private static final String ARG_POSTER_PATH = "poster_path";
    private static final String ARG_PROVIDERS = "providers";
    private static final String ARG_SEASON = "season";

    public static StreamingDialogFragment newInstance(int i, String str, String str2, ArrayList<WatchProvider> arrayList) {
        return newInstance(i, str, str2, arrayList, 0, 0, null, null);
    }

    public static StreamingDialogFragment newInstance(int i, String str, String str2, ArrayList<WatchProvider> arrayList, int i2, int i3) {
        return newInstance(i, str, str2, arrayList, i2, i3, null, null);
    }

    public static StreamingDialogFragment newInstance(int i, String str, String str2, ArrayList<WatchProvider> arrayList, int i2, int i3, String posterPath, String backdropPath) {
        StreamingDialogFragment streamingDialogFragment = new StreamingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ITEM_ID, i);
        bundle.putString(ARG_ITEM_TYPE, str);
        bundle.putString(ARG_ITEM_TITLE, str2);
        bundle.putSerializable(ARG_PROVIDERS, arrayList);
        bundle.putInt(ARG_SEASON, i2);
        bundle.putInt(ARG_EPISODE, i3);
        bundle.putString(ARG_POSTER_PATH, posterPath);
        bundle.putString(ARG_BACKDROP_PATH, backdropPath);
        streamingDialogFragment.setArguments(bundle);
        return streamingDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public int getTheme() {
        return R.style.Theme_HDHub4U_BottomSheet;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.dialog_streaming, viewGroup, false);
        final int i = getArguments() != null ? getArguments().getInt(ARG_ITEM_ID) : 0;
        final String string = getArguments() != null ? getArguments().getString(ARG_ITEM_TYPE, "movie") : "movie";
        final String string2 = getArguments() != null ? getArguments().getString(ARG_ITEM_TITLE, "") : "";
        final int i2 = getArguments() != null ? getArguments().getInt(ARG_SEASON, 0) : 0;
        final int i3 = getArguments() != null ? getArguments().getInt(ARG_EPISODE, 0) : 0;
        final String posterPath = getArguments() != null ? getArguments().getString(ARG_POSTER_PATH) : null;
        final String backdropPath = getArguments() != null ? getArguments().getString(ARG_BACKDROP_PATH) : null;
        ArrayList<WatchProvider> arrayList = null;
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayList = BundleCompat.getSerializable(getArguments(), ARG_PROVIDERS, ArrayList.class);
            } else {
                arrayList = (ArrayList<WatchProvider>) getArguments().getSerializable(ARG_PROVIDERS);
            }
        }
        ImageView imageView = (ImageView) viewInflate.findViewById(R.id.btnCloseStreaming);
        TextView textView = (TextView) viewInflate.findViewById(R.id.txtProvidersLabel);
        RecyclerView recyclerView = (RecyclerView) viewInflate.findViewById(R.id.rvDialogProviders);
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.fragments.StreamingDialogFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StreamingDialogFragment.this.m332xd843713f(view);
            }
        });
        // TMDB providers describe commercial availability and may be empty even
        // though our configured streaming server can resolve the TMDB id.
        // Always expose the source that this dialog actually opens.
        ArrayList<WatchProvider> streamingSources = new ArrayList<>();
        streamingSources.add(WatchProvider.streamingServer());
        textView.setVisibility(0);
        recyclerView.setVisibility(0);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(new DialogProviderAdapter(streamingSources, new DialogProviderAdapter.OnProviderClickListener() { // from class: com.freewatching.magistv4.fragments.StreamingDialogFragment$$ExternalSyntheticLambda1
                @Override // com.freewatching.magistv4.fragments.StreamingDialogFragment.DialogProviderAdapter.OnProviderClickListener
                public final void onProviderClick(WatchProvider watchProvider) {
                    StreamingDialogFragment.this.m333x92b911c0(i, string, string2, i2, i3, posterPath, backdropPath, watchProvider);
                }
            }));
        return viewInflate;
    }

    /* JADX INFO: renamed from: lambda$onCreateView$0$com-freewatching-magistv4-fragments-StreamingDialogFragment, reason: not valid java name */
    /* synthetic */ void m332xd843713f(View view) {
        dismiss();
    }

    /* JADX INFO: renamed from: lambda$onCreateView$1$com-freewatching-magistv4-fragments-StreamingDialogFragment, reason: not valid java name */
    /* synthetic */ void m333x92b911c0(int i, String str, String str2, int i2, int i3, String posterPath, String backdropPath, WatchProvider watchProvider) {
        openPlayer(i, str, str2, i2, i3, posterPath, backdropPath);
        dismiss();
    }

    private void openPlayer(int i, String str, String str2, int i2, int i3, String posterPath, String backdropPath) {
        String str3;
        if ("tv".equals(str) && i2 > 0 && i3 > 0) {
            str3 = "https://vsembed.ru/embed/tv?tmdb=" + i + "&season=" + i2 + "&episode=" + i3;
        } else if ("tv".equals(str)) {
            str3 = "https://vsembed.ru/embed/tv?tmdb=" + i;
        } else {
            str3 = "https://vsembed.ru/embed/movie?tmdb=" + i;
        }
        WatchProgressItem savedItem = ContinueWatchingStore.getItem(requireContext(), i, str, i2, i3);
        Intent intent = new Intent(requireContext(), (Class<?>) PlayerActivity.class);
        intent.putExtra("video_url", str3);
        intent.putExtra("item_id", i);
        intent.putExtra("item_type", str);
        intent.putExtra("item_title", str2);
        intent.putExtra("episode_title", str2);
        intent.putExtra("poster_path", posterPath);
        intent.putExtra("backdrop_path", backdropPath);
        intent.putExtra("season", i2);
        intent.putExtra("episode", i3);
        intent.putExtra("progress_percentage", savedItem == null ? 0 : savedItem.getProgressPercentage());
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class DialogProviderAdapter extends RecyclerView.Adapter<DialogProviderAdapter.VH> {
        private final OnProviderClickListener listener;
        private final List<WatchProvider> providers;

        interface OnProviderClickListener {
            void onProviderClick(WatchProvider watchProvider);
        }

        DialogProviderAdapter(List<WatchProvider> list, OnProviderClickListener onProviderClickListener) {
            this.providers = list;
            this.listener = onProviderClickListener;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new VH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_provider, viewGroup, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(VH vh, int i) {
            final WatchProvider watchProvider = this.providers.get(i);
            vh.txtName.setText(watchProvider.getProviderName());
            if (watchProvider.getFullLogoPath() == null) {
                Glide.with(vh.itemView.getContext()).clear(vh.imgLogo);
                vh.imgLogo.setBackgroundResource(R.drawable.rounded_card_dark);
                vh.imgLogo.setImageResource(R.drawable.ic_play);
                int padding = Math.round(16 * vh.itemView.getResources().getDisplayMetrics().density);
                vh.imgLogo.setPadding(padding, padding, padding, padding);
            } else {
                vh.imgLogo.setPadding(0, 0, 0, 0);
                Glide.with(vh.itemView.getContext()).load(watchProvider.getFullLogoPath()).transform(new RoundedCorners(24)).placeholder(R.drawable.rounded_card_dark).into(vh.imgLogo);
            }
            vh.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.fragments.StreamingDialogFragment$DialogProviderAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DialogProviderAdapter.this.m334xc4ff487e(watchProvider, view);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-fragments-StreamingDialogFragment$DialogProviderAdapter, reason: not valid java name */
        /* synthetic */ void m334xc4ff487e(WatchProvider watchProvider, View view) {
            this.listener.onProviderClick(watchProvider);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.providers.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView imgLogo;
            TextView txtName;

            VH(View view) {
                super(view);
                this.imgLogo = (ImageView) view.findViewById(R.id.imgProviderLogo);
                this.txtName = (TextView) view.findViewById(R.id.txtProviderName);
            }
        }
    }
}
