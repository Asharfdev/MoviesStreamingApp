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
import com.luminatv.tvshowmovies1.activities.PlayerActivity;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.List;

public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.ContinueWatchingViewHolder> {
    private final Context context;
    private List<WatchProgressItem> items = new ArrayList<>();

    public ContinueWatchingAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<WatchProgressItem> items) {
        this.items = items == null ? new ArrayList<WatchProgressItem>() : items;
        notifyDataSetChanged();
    }

    @Override
    public ContinueWatchingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_continue_watching, parent, false);
        return new ContinueWatchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContinueWatchingViewHolder holder, int position) {
        final WatchProgressItem item = items.get(position);
        int progress = item.getProgressPercentage();

        holder.txtTitle.setText(item.getDisplayTitle());
        holder.txtProgress.setText(progress + "% watched");
        holder.progressIndicator.setProgressCompat(progress, false);

        Glide.with(context)
                .load(item.getPosterPath())
                .transform(new RoundedCorners(24))
                .placeholder(R.drawable.rounded_card_dark)
                .error(R.drawable.rounded_card_dark)
                .into(holder.imgPoster);

        View.OnClickListener resumeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume(item);
            }
        };
        holder.itemView.setOnClickListener(resumeListener);
        holder.btnResume.setOnClickListener(resumeListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void resume(WatchProgressItem item) {
        if (CopyrightContentFilter.isTitleBlocked(context, item.getTitle())) {
            CopyrightContentFilter.showBlockedNotice(context);
            return;
        }
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("item_id", item.getTmdbId());
        intent.putExtra("item_type", item.getType());
        intent.putExtra("item_title", item.getTitle());
        intent.putExtra("episode_title", item.getDisplayTitle());
        intent.putExtra("poster_path", item.getPosterPath());
        intent.putExtra("backdrop_path", item.getBackdropPath());
        intent.putExtra("video_url", item.getVideoUrl());
        intent.putExtra("season", item.getSeasonNumber());
        intent.putExtra("episode", item.getEpisodeNumber());
        intent.putExtra("progress_percentage", item.getProgressPercentage());
        context.startActivity(intent);
    }

    static class ContinueWatchingViewHolder extends RecyclerView.ViewHolder {
        final MaterialButton btnResume;
        final ImageView imgPoster;
        final LinearProgressIndicator progressIndicator;
        final TextView txtProgress;
        final TextView txtTitle;

        ContinueWatchingViewHolder(View view) {
            super(view);
            imgPoster = (ImageView) view.findViewById(R.id.imgContinuePoster);
            txtTitle = (TextView) view.findViewById(R.id.txtContinueTitle);
            txtProgress = (TextView) view.findViewById(R.id.txtContinueProgress);
            progressIndicator = (LinearProgressIndicator) view.findViewById(R.id.progressContinue);
            btnResume = (MaterialButton) view.findViewById(R.id.btnResumeContinue);
        }
    }
}
