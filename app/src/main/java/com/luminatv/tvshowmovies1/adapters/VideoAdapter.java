package com.luminatv.tvshowmovies1.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.models.Video;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final Context context;
    private OnVideoClickListener listener;
    private List<Video> videos;

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoAdapter(Context context, List<Video> list, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videos = list == null ? new ArrayList<>() : list;
        this.listener = onVideoClickListener;
    }

    public void setVideos(List<Video> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.videos = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new VideoViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_video, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(VideoViewHolder videoViewHolder, int i) {
        final Video video = this.videos.get(i);
        videoViewHolder.txtVideoName.setText(video.getName());
        if (video.isYouTube()) {
            Glide.with(this.context).load(video.getThumbnailUrl()).placeholder(R.drawable.rounded_card_dark).into(videoViewHolder.imgThumbnail);
        }
        videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.adapters.VideoAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VideoAdapter.this.m309x22a9dd39(video, view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-freewatching-magistv4-adapters-VideoAdapter, reason: not valid java name */
    /* synthetic */ void m309x22a9dd39(Video video, View view) {
        OnVideoClickListener onVideoClickListener = this.listener;
        if (onVideoClickListener != null) {
            onVideoClickListener.onVideoClick(video);
        } else if (video.isYouTube()) {
            this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/watch?v=" + video.getKey())));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView txtVideoName;

        VideoViewHolder(View view) {
            super(view);
            this.imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
            this.txtVideoName = (TextView) view.findViewById(R.id.txtVideoName);
        }
    }
}
