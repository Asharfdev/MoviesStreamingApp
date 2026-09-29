package com.luminatv.tvshowmovies1.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luminatv.tvshowmovies1.R;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    private final List<OnboardingPage> pages;

    public OnboardingAdapter(List<OnboardingPage> pages) {
        this.pages = pages;
    }

    @Override
    public OnboardingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_page, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OnboardingViewHolder holder, int position) {
        OnboardingPage page = pages.get(position);
        Glide.with(holder.background.getContext())
                .load(page.getBackgroundImageUrl())
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.onboarding_backdrop_placeholder)
                .error(R.drawable.onboarding_backdrop_placeholder)
                .centerCrop()
                .into(holder.background);

        holder.title.setText(page.getTitle());
        holder.subtitle.setText(page.getSubtitle());
        animateText(holder);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        final ImageView background;
        final View content;
        final TextView subtitle;
        final TextView title;

        OnboardingViewHolder(View view) {
            super(view);
            background = (ImageView) view.findViewById(R.id.imgOnboardingBackground);
            content = view.findViewById(R.id.onboardingContent);
            title = (TextView) view.findViewById(R.id.txtOnboardingTitle);
            subtitle = (TextView) view.findViewById(R.id.txtOnboardingSubtitle);
        }
    }

    private void animateText(OnboardingViewHolder holder) {
        holder.title.animate().cancel();
        holder.subtitle.animate().cancel();

        holder.title.setAlpha(0f);
        holder.title.setTranslationY(36f);
        holder.subtitle.setAlpha(0f);
        holder.subtitle.setTranslationY(28f);

        holder.title.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(120L)
                .setDuration(520L)
                .start();
        holder.subtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(220L)
                .setDuration(520L)
                .start();
    }
}
