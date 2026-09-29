package com.luminatv.tvshowmovies1.onboarding;

public class OnboardingPage {
    private final String backgroundImageUrl;
    private final String subtitle;
    private final String title;

    public OnboardingPage(String title, String subtitle, String backgroundImageUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }
}
