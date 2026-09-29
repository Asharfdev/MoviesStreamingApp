package com.luminatv.tvshowmovies1.activities.ads;

public final class AppLAdUnit {
    public static final AppLAdUnit INSTANCE = new AppLAdUnit();

    public static String API_KEY = "drgVzz14PWsRLfTzJFE_cp0vsxxdDbNCJPzpl4LCdvTseNx7oV5vnI3AsAiRStodGdVzP3Lxgo7zusf0nZ1fuh";
    public static String activeAdNetwork = "both";
    private String interstitialAdUnitId = "0000000";
    private String yandexInterstitialAdUnitId = "R-M-19499585-1";
    private String yandexAppOpenAdUnitId = "demo-appopenad-yandex";

    private AppLAdUnit() {
    }

    public String getActiveAdNetwork() {
        return activeAdNetwork;
    }

    public void setActiveAdNetwork(String activeAdNetwork) {
        AppLAdUnit.activeAdNetwork = activeAdNetwork;
    }

    public String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

    public void setInterstitialAdUnitId(String interstitialAdUnitId) {
        this.interstitialAdUnitId = interstitialAdUnitId;
    }

    public String getYandexInterstitialAdUnitId() {
        return yandexInterstitialAdUnitId;
    }

    public void setYandexInterstitialAdUnitId(String yandexInterstitialAdUnitId) {
        this.yandexInterstitialAdUnitId = yandexInterstitialAdUnitId;
    }

    public String getYandexAppOpenAdUnitId() {
        return yandexAppOpenAdUnitId;
    }

    public void setYandexAppOpenAdUnitId(String yandexAppOpenAdUnitId) {
        this.yandexAppOpenAdUnitId = yandexAppOpenAdUnitId;
    }

}
