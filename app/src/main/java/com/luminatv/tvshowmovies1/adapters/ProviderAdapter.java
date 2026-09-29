package com.luminatv.tvshowmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.models.WatchProvider;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    private final Context context;
    private List<WatchProvider> providers;

    public ProviderAdapter(Context context, List<WatchProvider> list) {
        this.context = context;
        this.providers = list == null ? new ArrayList<>() : list;
    }

    public void setProviders(List<WatchProvider> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.providers = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ProviderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProviderViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_provider, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ProviderViewHolder providerViewHolder, int i) {
        WatchProvider watchProvider = this.providers.get(i);
        providerViewHolder.txtProviderName.setText(watchProvider.getProviderName());
        Glide.with(this.context).load(watchProvider.getFullLogoPath()).transform(new RoundedCorners(24)).placeholder(R.drawable.rounded_card_dark).into(providerViewHolder.imgProviderLogo);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.providers.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProviderLogo;
        TextView txtProviderName;

        ProviderViewHolder(View view) {
            super(view);
            this.imgProviderLogo = (ImageView) view.findViewById(R.id.imgProviderLogo);
            this.txtProviderName = (TextView) view.findViewById(R.id.txtProviderName);
        }
    }
}
