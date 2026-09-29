package com.luminatv.tvshowmovies1.onboarding;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.MainActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w1280";

    private MaterialButton btnGetStarted;
    private MaterialButton btnNext;
    private MaterialButton btnSkip;
    private LinearLayout indicatorsContainer;
    private List<OnboardingPage> pages;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_onboarding);

        viewPager = (ViewPager2) findViewById(R.id.onboardingViewPager);
        indicatorsContainer = (LinearLayout) findViewById(R.id.onboardingIndicators);
        btnSkip = (MaterialButton) findViewById(R.id.btnSkipOnboarding);
        btnNext = (MaterialButton) findViewById(R.id.btnNextOnboarding);
        btnGetStarted = (MaterialButton) findViewById(R.id.btnGetStarted);

        pages = createPages();
        viewPager.setAdapter(new OnboardingAdapter(pages));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer(new CinematicPageTransformer());

        createIndicators();
        updateControls(0);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishOnboarding();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextItem = viewPager.getCurrentItem() + 1;
                if (nextItem < pages.size()) {
                    viewPager.setCurrentItem(nextItem, true);
                }
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishOnboarding();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateControls(position);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager != null && viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private List<OnboardingPage> createPages() {
        List<OnboardingPage> onboardingPages = new ArrayList<>();
        onboardingPages.add(new OnboardingPage(
                getString(R.string.onboarding_discover_title),
                getString(R.string.onboarding_discover_subtitle),
                TMDB_IMAGE_BASE_URL + "/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg"));
        onboardingPages.add(new OnboardingPage(
                getString(R.string.onboarding_search_title),
                getString(R.string.onboarding_search_subtitle),
                TMDB_IMAGE_BASE_URL + "/628Dep6AxEtDxjZoGP78TsOxYbK.jpg"));
        onboardingPages.add(new OnboardingPage(
                getString(R.string.onboarding_watch_title),
                getString(R.string.onboarding_watch_subtitle),
                TMDB_IMAGE_BASE_URL + "/zoVeIgKzGJzpdG6Gwnr7iOYfIMU.jpg"));
        return onboardingPages;
    }

    private void createIndicators() {
        indicatorsContainer.removeAllViews();
        for (int i = 0; i < pages.size(); i++) {
            View indicator = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.onboarding_indicator_inactive_width),
                    getResources().getDimensionPixelSize(R.dimen.onboarding_indicator_height));
            params.setMargins(7, 0, 7, 0);
            indicator.setLayoutParams(params);
            indicator.setAlpha(0.42f);
            indicatorsContainer.addView(indicator);
        }
    }

    private void updateControls(int position) {
        boolean isLastPage = position == pages.size() - 1;
        btnSkip.setVisibility(isLastPage ? View.INVISIBLE : View.VISIBLE);
        btnNext.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
        btnGetStarted.setVisibility(isLastPage ? View.VISIBLE : View.GONE);

        for (int i = 0; i < indicatorsContainer.getChildCount(); i++) {
            View indicator = indicatorsContainer.getChildAt(i);
            animateIndicator(indicator, i == position);
        }
    }

    private void animateIndicator(final View indicator, boolean isActive) {
        indicator.setBackgroundResource(isActive
                ? R.drawable.onboarding_indicator_active
                : R.drawable.onboarding_indicator_inactive);

        int targetWidth = getResources().getDimensionPixelSize(isActive
                ? R.dimen.onboarding_indicator_active_width
                : R.dimen.onboarding_indicator_inactive_width);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator.getLayoutParams();
        int currentWidth = params.width;

        ValueAnimator widthAnimator = ValueAnimator.ofInt(currentWidth, targetWidth);
        widthAnimator.setDuration(240L);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) indicator.getLayoutParams();
                layoutParams.width = (Integer) animator.getAnimatedValue();
                indicator.setLayoutParams(layoutParams);
            }
        });
        widthAnimator.start();

        indicator.animate()
                .alpha(isActive ? 1f : 0.42f)
                .scaleY(isActive ? 1.08f : 1f)
                .setDuration(240L)
                .start();
    }

    private void finishOnboarding() {
        OnboardingPreferences.setCompleted(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static class CinematicPageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            float absolutePosition = Math.abs(position);
            float normalized = Math.min(1f, absolutePosition);

            View background = page.findViewById(R.id.imgOnboardingBackground);
            if (background != null) {
                background.setTranslationX(-position * page.getWidth() * 0.32f);
                background.setScaleX(1.06f - (normalized * 0.03f));
                background.setScaleY(1.06f - (normalized * 0.03f));
            }

            View content = page.findViewById(R.id.onboardingContent);
            if (content != null) {
                content.setTranslationX(-position * page.getWidth() * 0.12f);
                content.setTranslationY(normalized * 30f);
                content.setAlpha(1f - (normalized * 0.72f));
            }

            page.setAlpha(0.58f + ((1f - normalized) * 0.42f));
        }
    }
}
