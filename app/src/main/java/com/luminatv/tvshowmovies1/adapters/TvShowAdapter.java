package com.luminatv.tvshowmovies1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.DetailActivity;
import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.models.TvShow;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {
    private final Context context;
    private List<TvShow> tvShows;

    public TvShowAdapter(Context context, List<TvShow> list) {
        this.context = context;
        this.tvShows = list == null ? new ArrayList<>() : list;
    }

    public void setTvShows(List<TvShow> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.tvShows = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public TvShowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TvShowViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_movie, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(TvShowViewHolder tvShowViewHolder, int i) {
        String strSubstring;
        final TvShow tvShow = this.tvShows.get(i);
        tvShowViewHolder.txtTitle.setText(tvShow.getName());
        tvShowViewHolder.txtRating.setText(String.format("%.1f", Double.valueOf(tvShow.getVoteAverage())));
        if (tvShow.getFirstAirDate() != null && tvShow.getFirstAirDate().length() >= 4) {
            strSubstring = tvShow.getFirstAirDate().substring(0, 4);
        } else {
            strSubstring = "";
        }
        tvShowViewHolder.txtYear.setText(strSubstring);
        Glide.with(this.context).load(tvShow.getFullPosterPath()).transform(new RoundedCorners(24)).placeholder(R.drawable.rounded_card_dark).into(tvShowViewHolder.imgPoster);
        tvShowViewHolder.itemView.setAlpha(0f);
        tvShowViewHolder.itemView.setTranslationY(16f);
        tvShowViewHolder.itemView.animate().alpha(1f).translationY(0f).setStartDelay(Math.min(i, 6) * 28L).setDuration(240L).start();
        tvShowViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.TvShowAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TvShowAdapter.this.m308x658c836b(tvShow, view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-TvShowAdapter, reason: not valid java name */
    /* synthetic */ void m308x658c836b(TvShow tvShow, View view) {
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", tvShow.getId());
        intent.putExtra("type", "tv");
        this.context.startActivity(intent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.tvShows.size();
    }

    static class TvShowViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtRating;
        TextView txtTitle;
        TextView txtYear;

        TvShowViewHolder(View view) {
            super(view);
            this.imgPoster = (ImageView) view.findViewById(R.id.imgPoster);
            this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            this.txtRating = (TextView) view.findViewById(R.id.txtRating);
            this.txtYear = (TextView) view.findViewById(R.id.txtYear);
        }
    }
}
