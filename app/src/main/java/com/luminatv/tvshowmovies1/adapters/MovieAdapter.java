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
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private List<Movie> movies;
    private final boolean countHomeSelections;

    public MovieAdapter(Context context, List<Movie> list) {
        this(context, list, false);
    }

    public MovieAdapter(Context context, List<Movie> list, boolean countHomeSelections) {
        this.context = context;
        this.movies = list == null ? new ArrayList<>() : list;
        this.countHomeSelections = countHomeSelections;
    }

    public void setMovies(List<Movie> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.movies = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MovieViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_movie, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int i) {
        String strSubstring;
        final Movie movie = this.movies.get(i);
        movieViewHolder.txtTitle.setText(movie.getTitle() != null ? movie.getTitle() : "");
        movieViewHolder.txtRating.setText(String.format("%.1f", Double.valueOf(movie.getVoteAverage())));
        if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4) {
            strSubstring = movie.getReleaseDate().substring(0, 4);
        } else {
            strSubstring = "";
        }
        movieViewHolder.txtYear.setText(strSubstring);
        Glide.with(this.context).load(movie.getFullPosterPath()).transform(new RoundedCorners(24)).placeholder(R.drawable.rounded_card_dark).into(movieViewHolder.imgPoster);
        movieViewHolder.itemView.setAlpha(0f);
        movieViewHolder.itemView.setTranslationY(16f);
        movieViewHolder.itemView.animate().alpha(1f).translationY(0f).setStartDelay(Math.min(i, 6) * 28L).setDuration(240L).start();
        movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.MovieAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MovieAdapter.this.m305xca372ce4(movie, view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-MovieAdapter, reason: not valid java name */
    /* synthetic */ void m305xca372ce4(Movie movie, View view) {
        if (countHomeSelections) {
            AppLAds.INSTANCE.onHomeMovieSelected();
        }
        Intent intent = new Intent(this.context, (Class<?>) DetailActivity.class);
        intent.putExtra("id", movie.getId());
        intent.putExtra("type", "movie");
        this.context.startActivity(intent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtRating;
        TextView txtTitle;
        TextView txtYear;

        MovieViewHolder(View view) {
            super(view);
            this.imgPoster = (ImageView) view.findViewById(R.id.imgPoster);
            this.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            this.txtRating = (TextView) view.findViewById(R.id.txtRating);
            this.txtYear = (TextView) view.findViewById(R.id.txtYear);
        }
    }
}
