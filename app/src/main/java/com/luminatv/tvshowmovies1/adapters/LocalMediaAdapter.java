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
import com.luminatv.tvshowmovies1.models.LocalMediaItem;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import java.util.ArrayList;
import java.util.List;

public class LocalMediaAdapter extends RecyclerView.Adapter<LocalMediaAdapter.LocalMediaViewHolder> {
    private final Context context;
    private List<LocalMediaItem> items = new ArrayList<>();

    public LocalMediaAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<LocalMediaItem> items) {
        this.items = items == null ? new ArrayList<LocalMediaItem>() : items;
        notifyDataSetChanged();
    }

    @Override
    public LocalMediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_media, parent, false);
        return new LocalMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocalMediaViewHolder holder, int position) {
        final LocalMediaItem item = items.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtSubtitle.setText(item.getSubtitle());
        holder.txtRating.setText(String.format("%.1f", item.getRating()));

        Glide.with(context)
                .load(item.getPosterUrl())
                .transform(new RoundedCorners(28))
                .placeholder(R.drawable.rounded_card_dark)
                .error(R.drawable.rounded_card_dark)
                .into(holder.imgPoster);

        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(18f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(Math.min(position, 6) * 35L)
                .setDuration(260L)
                .start();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CopyrightContentFilter.isTitleBlocked(context, item.getTitle())) {
                    CopyrightContentFilter.showBlockedNotice(context);
                    return;
                }
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", item.getId());
                intent.putExtra("type", item.getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LocalMediaViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgPoster;
        final TextView txtRating;
        final TextView txtSubtitle;
        final TextView txtTitle;

        LocalMediaViewHolder(View view) {
            super(view);
            imgPoster = (ImageView) view.findViewById(R.id.imgLocalPoster);
            txtTitle = (TextView) view.findViewById(R.id.txtLocalTitle);
            txtSubtitle = (TextView) view.findViewById(R.id.txtLocalSubtitle);
            txtRating = (TextView) view.findViewById(R.id.txtLocalRating);
        }
    }
}
