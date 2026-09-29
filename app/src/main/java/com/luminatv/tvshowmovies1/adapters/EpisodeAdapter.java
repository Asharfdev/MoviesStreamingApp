package com.luminatv.tvshowmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.models.Episode;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeVH> {
    private final Context context;
    private List<Episode> episodes;
    private final OnEpisodeClickListener listener;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(Context context, List<Episode> list, OnEpisodeClickListener onEpisodeClickListener) {
        this.context = context;
        this.episodes = list;
        this.listener = onEpisodeClickListener;
    }

    public void setEpisodes(List<Episode> list) {
        this.episodes = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public EpisodeVH onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new EpisodeVH(LayoutInflater.from(this.context).inflate(R.layout.item_episode, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(EpisodeVH episodeVH, int i) {
        String airDate;
        final Episode episode = this.episodes.get(i);
        episodeVH.txtEpisodeNumber.setText(ExifInterface.LONGITUDE_EAST + episode.getEpisodeNumber());
        episodeVH.txtEpisodeName.setText(episode.getName());
        if (episode.getAirDate() != null && !episode.getAirDate().isEmpty()) {
            airDate = episode.getAirDate();
        } else {
            airDate = "";
        }
        if (episode.getRuntime() > 0) {
            if (!airDate.isEmpty()) {
                airDate = airDate + " · ";
            }
            airDate = airDate + episode.getRuntime() + " min";
        }
        if (episode.getVoteAverage() > 0.0d) {
            if (!airDate.isEmpty()) {
                airDate = airDate + " · ";
            }
            airDate = airDate + String.format("★ %.1f", Double.valueOf(episode.getVoteAverage()));
        }
        episodeVH.txtEpisodeMeta.setText(airDate);
        if (episode.getOverview() != null && !episode.getOverview().isEmpty()) {
            episodeVH.txtEpisodeOverview.setText(episode.getOverview());
            episodeVH.txtEpisodeOverview.setVisibility(0);
        } else {
            episodeVH.txtEpisodeOverview.setVisibility(8);
        }
        if (episode.getFullStillPath() != null) {
            Glide.with(this.context).load(episode.getFullStillPath()).placeholder(R.drawable.rounded_card_dark).into(episodeVH.imgEpisodeStill);
        } else {
            episodeVH.imgEpisodeStill.setImageResource(R.drawable.rounded_card_dark);
        }
        episodeVH.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.EpisodeAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EpisodeAdapter.this.m304xeb705e79(episode, view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-EpisodeAdapter, reason: not valid java name */
    /* synthetic */ void m304xeb705e79(Episode episode, View view) {
        OnEpisodeClickListener onEpisodeClickListener = this.listener;
        if (onEpisodeClickListener != null) {
            onEpisodeClickListener.onEpisodeClick(episode);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<Episode> list = this.episodes;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    static class EpisodeVH extends RecyclerView.ViewHolder {
        ImageView imgEpisodeStill;
        TextView txtEpisodeMeta;
        TextView txtEpisodeName;
        TextView txtEpisodeNumber;
        TextView txtEpisodeOverview;

        EpisodeVH(View view) {
            super(view);
            this.imgEpisodeStill = (ImageView) view.findViewById(R.id.imgEpisodeStill);
            this.txtEpisodeNumber = (TextView) view.findViewById(R.id.txtEpisodeNumber);
            this.txtEpisodeName = (TextView) view.findViewById(R.id.txtEpisodeName);
            this.txtEpisodeMeta = (TextView) view.findViewById(R.id.txtEpisodeMeta);
            this.txtEpisodeOverview = (TextView) view.findViewById(R.id.txtEpisodeOverview);
        }
    }
}
