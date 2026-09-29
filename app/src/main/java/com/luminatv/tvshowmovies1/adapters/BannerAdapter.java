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
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.DetailActivity;
import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.models.Movie;
import com.luminatv.tvshowmovies1.models.TvShow;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final Context context;
    private List<Movie> movies = new ArrayList();
    private List<TvShow> tvShows = new ArrayList();
    private final String type;

    public BannerAdapter(Context context, String str) {
        this.context = context;
        this.type = str;
    }

    public void setMovies(List<Movie> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.movies = list;
        notifyDataSetChanged();
    }

    public void setTvShows(List<TvShow> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.tvShows = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public BannerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new BannerViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_movie_banner, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(BannerViewHolder bannerViewHolder, int i) {
        if ("movie".equals(this.type) && i < this.movies.size()) {
            final Movie movie = this.movies.get(i);
            bannerViewHolder.txtBannerTitle.setText(movie.getTitle());
            bannerViewHolder.txtBannerRating.setText(String.format("%.1f", Double.valueOf(movie.getVoteAverage())));
            if (bannerViewHolder.txtBannerOverview != null && movie.getOverview() != null) {
                bannerViewHolder.txtBannerOverview.setText(movie.getOverview());
            }
            if (bannerViewHolder.txtBannerYear != null && movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4) {
                bannerViewHolder.txtBannerYear.setText(movie.getReleaseDate().substring(0, 4));
            }
            Glide.with(this.context).load(movie.getFullBackdropPath() != null ? movie.getFullBackdropPath() : movie.getFullPosterPath()).into(bannerViewHolder.imgBanner);
            bannerViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.BannerAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BannerAdapter.this.m302x9a88c83e(movie, view);
                }
            });
            return;
        }
        if (!"tv".equals(this.type) || i >= this.tvShows.size()) {
            return;
        }
        final TvShow tvShow = this.tvShows.get(i);
        bannerViewHolder.txtBannerTitle.setText(tvShow.getName());
        bannerViewHolder.txtBannerRating.setText(String.format("%.1f", Double.valueOf(tvShow.getVoteAverage())));
        if (bannerViewHolder.txtBannerOverview != null && tvShow.getOverview() != null) {
            bannerViewHolder.txtBannerOverview.setText(tvShow.getOverview());
        }
        if (bannerViewHolder.txtBannerYear != null && tvShow.getFirstAirDate() != null && tvShow.getFirstAirDate().length() >= 4) {
            bannerViewHolder.txtBannerYear.setText(tvShow.getFirstAirDate().substring(0, 4));
        }
        Glide.with(this.context).load(tvShow.getFullBackdropPath() != null ? tvShow.getFullBackdropPath() : tvShow.getFullPosterPath()).into(bannerViewHolder.imgBanner);
        bannerViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.BannerAdapter$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BannerAdapter.this.m303x8c326e5d(tvShow, view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-BannerAdapter, reason: not valid java name */
    /* synthetic */ void m302x9a88c83e(Movie movie, View view) {
        AppLAds.INSTANCE.onHomeMovieSelected();
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", movie.getId());
        intent.putExtra("type", "movie");
        this.context.startActivity(intent);
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$1$com-freewatching-magistv4-adapters-BannerAdapter, reason: not valid java name */
    /* synthetic */ void m303x8c326e5d(TvShow tvShow, View view) {
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", tvShow.getId());
        intent.putExtra("type", "tv");
        this.context.startActivity(intent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return "movie".equals(this.type) ? Math.min(this.movies.size(), 10) : Math.min(this.tvShows.size(), 10);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtBannerOverview;
        TextView txtBannerRating;
        TextView txtBannerTitle;
        TextView txtBannerYear;

        BannerViewHolder(View view) {
            super(view);
            this.imgBanner = (ImageView) view.findViewById(R.id.imgBanner);
            this.txtBannerTitle = (TextView) view.findViewById(R.id.txtBannerTitle);
            this.txtBannerRating = (TextView) view.findViewById(R.id.txtBannerRating);
            this.txtBannerOverview = (TextView) view.findViewById(R.id.txtBannerOverview);
            this.txtBannerYear = (TextView) view.findViewById(R.id.txtBannerYear);
        }
    }
}
