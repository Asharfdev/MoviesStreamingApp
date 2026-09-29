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
import com.luminatv.tvshowmovies1.models.Movie;
import com.luminatv.tvshowmovies1.models.TvShow;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final Context context;
    private List<Object> items = new ArrayList();

    public SearchAdapter(Context context) {
        this.context = context;
    }

    public void setMovies(List<Movie> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.items = new ArrayList();
        for (Movie movie : list) {
            if (movie != null && !CopyrightContentFilter.isTitleBlocked(context, movie.getTitle())) {
                this.items.add(movie);
            }
        }
        notifyDataSetChanged();
    }

    public void setTvShows(List<TvShow> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.items = new ArrayList();
        for (TvShow tvShow : list) {
            if (tvShow != null && !CopyrightContentFilter.isTitleBlocked(context, tvShow.getName())) {
                this.items.add(tvShow);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SearchViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_search_result, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(SearchViewHolder searchViewHolder, int i) {
        Object obj = this.items.get(i);
        String strSubstring = "";
        if (obj instanceof Movie) {
            final Movie movie = (Movie) obj;
            searchViewHolder.txtTitle.setText(movie.getTitle() != null ? movie.getTitle() : "");
            searchViewHolder.txtRating.setText(String.format("%.1f", Double.valueOf(movie.getVoteAverage())));
            searchViewHolder.txtOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");
            searchViewHolder.txtType.setText("MOVIE");
            if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4) {
                strSubstring = movie.getReleaseDate().substring(0, 4);
            }
            searchViewHolder.txtYear.setText(strSubstring);
            Glide.with(this.context).load(movie.getFullPosterPath()).transform(new RoundedCorners(16)).placeholder(R.drawable.rounded_card_dark).into(searchViewHolder.imgPoster);
            searchViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.SearchAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SearchAdapter.this.m306x9a36de42(movie, view);
                }
            });
            return;
        }
        if (obj instanceof TvShow) {
            final TvShow tvShow = (TvShow) obj;
            searchViewHolder.txtTitle.setText(tvShow.getName() != null ? tvShow.getName() : "");
            searchViewHolder.txtRating.setText(String.format("%.1f", Double.valueOf(tvShow.getVoteAverage())));
            searchViewHolder.txtOverview.setText(tvShow.getOverview() != null ? tvShow.getOverview() : "");
            searchViewHolder.txtType.setText("TV SHOW");
            if (tvShow.getFirstAirDate() != null && tvShow.getFirstAirDate().length() >= 4) {
                strSubstring = tvShow.getFirstAirDate().substring(0, 4);
            }
            searchViewHolder.txtYear.setText(strSubstring);
            Glide.with(this.context).load(tvShow.getFullPosterPath()).transform(new RoundedCorners(16)).placeholder(R.drawable.rounded_card_dark).into(searchViewHolder.imgPoster);
            searchViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.SearchAdapter$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SearchAdapter.this.m307x8be08461(tvShow, view);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-SearchAdapter, reason: not valid java name */
    /* synthetic */ void m306x9a36de42(Movie movie, View view) {
        if (CopyrightContentFilter.isTitleBlocked(this.context, movie.getTitle())) {
            CopyrightContentFilter.showBlockedNotice(this.context);
            return;
        }
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", movie.getId());
        intent.putExtra("type", "movie");
        this.context.startActivity(intent);
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$1$com-freewatching-magistv4-adapters-SearchAdapter, reason: not valid java name */
    /* synthetic */ void m307x8be08461(TvShow tvShow, View view) {
        if (CopyrightContentFilter.isTitleBlocked(this.context, tvShow.getName())) {
            CopyrightContentFilter.showBlockedNotice(this.context);
            return;
        }
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", tvShow.getId());
        intent.putExtra("type", "tv");
        this.context.startActivity(intent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.items.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtOverview;
        TextView txtRating;
        TextView txtTitle;
        TextView txtType;
        TextView txtYear;

        SearchViewHolder(View view) {
            super(view);
            this.imgPoster = (ImageView) view.findViewById(R.id.imgPoster);
            this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            this.txtYear = (TextView) view.findViewById(R.id.txtYear);
            this.txtRating = (TextView) view.findViewById(R.id.txtRating);
            this.txtOverview = (TextView) view.findViewById(R.id.txtOverview);
            this.txtType = (TextView) view.findViewById(R.id.txtType);
        }
    }
}
