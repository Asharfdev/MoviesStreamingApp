package com.luminatv.tvshowmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.models.Cast;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private List<Cast> castList;
    private final Context context;

    public CastAdapter(Context context, List<Cast> list) {
        this.context = context;
        this.castList = list == null ? new ArrayList<>() : list;
    }

    public void setCastList(List<Cast> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.castList = list;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public CastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CastViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_cast, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(CastViewHolder castViewHolder, int i) {
        Cast cast = this.castList.get(i);
        castViewHolder.txtCastName.setText(cast.getName());
        castViewHolder.txtCharacter.setText(cast.getCharacter());
        Glide.with(this.context).load(cast.getFullProfilePath()).placeholder(R.drawable.ic_person_placeholder).circleCrop().into(castViewHolder.imgCast);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return Math.min(this.castList.size(), 20);
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCast;
        TextView txtCastName;
        TextView txtCharacter;

        CastViewHolder(View view) {
            super(view);
            this.imgCast = (ImageView) view.findViewById(R.id.imgCast);
            this.txtCastName = (TextView) view.findViewById(R.id.txtCastName);
            this.txtCharacter = (TextView) view.findViewById(R.id.txtCharacter);
        }
    }
}
