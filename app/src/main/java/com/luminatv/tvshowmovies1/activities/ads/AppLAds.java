package com.luminatv.tvshowmovies1.activities.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.YandexAds;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;
import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;

import com.luminatv.tvshowmovies1.utils.LifecycleUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public final class AppLAds {
    public static final AppLAds INSTANCE = new AppLAds();

    private static final String TAG = "AppLAds";
    private static final int MAX_INTERSTITIAL_LOAD_RETRY_ATTEMPTS = 3;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Runnable> pendingYandexInitCallbacks = new ArrayList<>();
    private final List<Runnable> pendingInitCallbacks = new ArrayList<>();
    private WeakReference<Activity> currentActivityReference;
    private InterstitialAd interstitialAd;
    private InterstitialAdLoader interstitialAdLoader;
    private AppOpenAd appOpenAd;
    private AppOpenAdLoader appOpenAdLoader;
    private com.applovin.mediation.ads.MaxInterstitialAd appLovinInterstitialAd;
    private boolean yandexInitializing;
    private boolean yandexInitialized;
    private boolean interstitialLoading;
    private boolean isConfigLoaded;
    private boolean isConfigLoading;
    private int interstitialRetryAttempt;
    private int appLovinInterstitialRetryAttempt;
    private int homeMovieSelectionCount;
    private boolean appOpenAdLoading;
    private boolean appOpenAdShownThisSession;
    private boolean fullScreenAdShowing;
    private boolean showAppLovinLaunchAdWhenLoaded;
    private boolean appLovinLaunchAdShownThisSession;

    public void refreshAdConfig(Context context, Runnable callback) {
        synchronized (pendingInitCallbacks) {
            isConfigLoaded = false;
        }
        loadAdConfig(context, callback);
    }

    private AppLAds() {
    }

    public void loadAdConfig(Context context, Runnable callback) {
        if (isConfigLoaded) {
            if (callback != null) {
                callback.run();
            }
            return;
        }

        synchronized (pendingInitCallbacks) {
            if (callback != null) {
                pendingInitCallbacks.add(callback);
            }
            if (isConfigLoading) {
                return;
            }
            isConfigLoading = true;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = com.luminatv.tvshowmovies1.utils.UpdateChecker.UPDATE_URL;
                    if (urlString.contains("?")) {
                        urlString += "&t=" + System.currentTimeMillis();
                    } else {
                        urlString += "?t=" + System.currentTimeMillis();
                    }
                    HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    if (connection.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        reader.close();
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        
                        String appLovinApiKey = jsonObject.optString("applovin_api_key", null);
                        if (appLovinApiKey != null && !appLovinApiKey.isEmpty()) {
                            AppLAdUnit.API_KEY = appLovinApiKey;
                        }
                        String interstitialId = jsonObject.optString("applovin_interstitial_id", null);
                        if (interstitialId != null && !interstitialId.isEmpty()) {
                            AppLAdUnit.INSTANCE.setInterstitialAdUnitId(interstitialId);
                        }
                        String yandexInterstitialId = jsonObject.optString("yandex_interstitial_id", null);
                        if (yandexInterstitialId != null && !yandexInterstitialId.isEmpty()) {
                            AppLAdUnit.INSTANCE.setYandexInterstitialAdUnitId(yandexInterstitialId);
                        }
                        String yandexAppOpenId = jsonObject.optString("yandex_app_open_id", null);
                        if (yandexAppOpenId != null && !yandexAppOpenId.isEmpty()) {
                            AppLAdUnit.INSTANCE.setYandexAppOpenAdUnitId(yandexAppOpenId);
                        }
                        String activeNetwork = normalizeNetwork(jsonObject.optString("active_ad_network", "both"));
                        String previousNetwork = AppLAdUnit.activeAdNetwork;
                        AppLAdUnit.activeAdNetwork = activeNetwork;
                        if (!activeNetwork.equalsIgnoreCase(previousNetwork)) {
                            mainHandler.post(() -> releaseInactiveNetworkAds(activeNetwork));
                        }
                        Log.d(TAG, "Ad configuration loaded successfully from JSON.");
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load ad configuration from JSON: " + e.getMessage());
                } finally {
                    executorService.shutdown();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            isConfigLoaded = true;
                            isConfigLoading = false;
                            List<Runnable> callbacks;
                            synchronized (pendingInitCallbacks) {
                                callbacks = new ArrayList<>(pendingInitCallbacks);
                                pendingInitCallbacks.clear();
                            }
                            for (Runnable r : callbacks) {
                                r.run();
                            }
                        }
                    });
                }
            }
        });
    }

    public void initializeYandexSDK(Context context, Runnable callback) {
        loadAdConfig(context, new Runnable() {
            @Override
            public void run() {
                if ("applovin".equalsIgnoreCase(AppLAdUnit.activeAdNetwork) || "none".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)) {
                    Log.d(TAG, "Yandex SDK initialization skipped (active_ad_network is " + AppLAdUnit.activeAdNetwork + ")");
                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (yandexInitialized) {
                            if (callback != null) {
                                callback.run();
                            }
                            return;
                        }

                        if (callback != null) {
                            pendingYandexInitCallbacks.add(callback);
                        }

                        if (yandexInitializing) {
                            return;
                        }

                        yandexInitializing = true;
                        Log.d(TAG, "Initializing Yandex Mobile Ads SDK...");
                        YandexAds.initialize(context.getApplicationContext(), new InitializationListener() {
                            @Override
                            public void onInitializationCompleted() {
                                yandexInitialized = true;
                                yandexInitializing = false;
                                Log.d(TAG, "Yandex Mobile Ads SDK initialized successfully");
                                runPendingYandexInitCallbacks();
                            }
                        });
                    }
                });
            }
        });
    }

    public void initializeSDK(Context context, Runnable callback) {
        loadAdConfig(context, new Runnable() {
            @Override
            public void run() {
                if ("yandex".equalsIgnoreCase(AppLAdUnit.activeAdNetwork) || "none".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)) {
                    Log.d(TAG, "AppLovin SDK initialization skipped (active_ad_network is " + AppLAdUnit.activeAdNetwork + ")");
                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }
                Log.d(TAG, "Initializing AppLovin SDK...");
                Context appContext = context.getApplicationContext();
                AppLovinSdk sdk = AppLovinSdk.getInstance(appContext);
                if (sdk.isInitialized()) {
                    Log.d(TAG, "AppLovin SDK already initialized");
                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }

                AppLovinSdkInitializationConfiguration configuration =
                        AppLovinSdkInitializationConfiguration.builder(AppLAdUnit.API_KEY, appContext)
                                .setMediationProvider(AppLovinMediationProvider.MAX)
                                .build();

                sdk.initialize(configuration, new AppLovinSdk.SdkInitializationListener() {
                    @Override
                    public void onSdkInitialized(AppLovinSdkConfiguration configuration) {
                        Log.d(TAG, "AppLovin SDK initialized successfully: " + configuration);
                        if (callback != null) {
                            callback.run();
                        }
                    }
                });
            }
        });
    }

    public void initializeAds(Activity activity) {
        Log.d(TAG, "Initializing ads...");
        setCurrentActivity(activity);
        if ("applovin".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)
                && !appLovinLaunchAdShownThisSession) {
            showAppLovinLaunchAdWhenLoaded = true;
        }
        initializeYandexSDK(activity, new Runnable() {
            @Override
            public void run() {
                initializeInterstitialAds(activity);
                initializeAppOpenAd(activity);
                maybeShowAppLovinLaunchAd();
            }
        });
    }

    public void setCurrentActivity(Activity activity) {
        currentActivityReference = new WeakReference<>(activity);
    }

    public void clearCurrentActivity(Activity activity) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == activity && currentActivityReference != null) {
            currentActivityReference.clear();
            currentActivityReference = null;
        }
    }

    public boolean showInterstitial() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    showInterstitial();
                }
            });
            return false;
        }

        Activity activity = getCurrentActivity();
        if (!LifecycleUtils.isActivityAlive(activity)) {
            return false;
        }

        String network = AppLAdUnit.activeAdNetwork;
        if ("none".equalsIgnoreCase(network)) {
            return false;
        }

        if ("yandex".equalsIgnoreCase(network)) {
            if (interstitialAd != null && !fullScreenAdShowing) {
                Log.d(TAG, "Showing Yandex interstitial ad");
                showYandexInterstitial(activity);
                return true;
            }
            preloadInterstitial();
            return false;
        }

        if ("applovin".equalsIgnoreCase(network)) {
            if (appLovinInterstitialAd != null && appLovinInterstitialAd.isReady()) {
                Log.d(TAG, "Showing AppLovin interstitial ad");
                showAppLovinInterstitial(activity, false);
                return true;
            }
            preloadInterstitial();
            return false;
        }

        // Default: both (try Yandex first, fallback to AppLovin)
        if (interstitialAd != null) {
            Log.d(TAG, "Showing Yandex interstitial ad");
            showYandexInterstitial(activity);
            return true;
        } else if (appLovinInterstitialAd != null && appLovinInterstitialAd.isReady()) {
            Log.d(TAG, "Showing AppLovin interstitial ad");
            showAppLovinInterstitial(activity, false);
            return true;
        }

        preloadInterstitial();
        return false;
    }

    public void onHomeMovieSelected() {
        homeMovieSelectionCount++;
        if (homeMovieSelectionCount >= 3) {
            homeMovieSelectionCount = 0;
            showInterstitial();
        } else {
            preloadInterstitial();
        }
    }

    public boolean isInterstitialReady() {
        String network = AppLAdUnit.activeAdNetwork;
        if ("yandex".equalsIgnoreCase(network)) {
            return interstitialAd != null;
        }
        if ("applovin".equalsIgnoreCase(network)) {
            return appLovinInterstitialAd != null && appLovinInterstitialAd.isReady();
        }
        return interstitialAd != null || (appLovinInterstitialAd != null && appLovinInterstitialAd.isReady());
    }

    public void preloadInterstitial() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    preloadInterstitial();
                }
            });
            return;
        }

        String network = AppLAdUnit.activeAdNetwork;
        if ("none".equalsIgnoreCase(network)) {
            return;
        }

        if (!"applovin".equalsIgnoreCase(network)) {
            if (interstitialAdLoader == null) {
                Activity activity = getCurrentActivity();
                if (activity != null) {
                    initializeYandexSDK(activity, new Runnable() {
                        @Override
                        public void run() {
                            initializeInterstitialAds(activity);
                        }
                    });
                }
            } else {
                loadInterstitialAd();
            }
        }

        if (!"yandex".equalsIgnoreCase(network)) {
            if (appLovinInterstitialAd == null) {
                Activity activity = getCurrentActivity();
                if (activity != null) {
                    initializeSDK(activity, new Runnable() {
                        @Override
                        public void run() {
                            initializeAppLovinInterstitial(activity);
                        }
                    });
                }
            } else {
                appLovinInterstitialAd.loadAd();
            }
        }
    }

    private void initializeAppLovinInterstitial(Activity activity) {
        if (appLovinInterstitialAd == null) {
            appLovinInterstitialAd = new com.applovin.mediation.ads.MaxInterstitialAd(AppLAdUnit.INSTANCE.getInterstitialAdUnitId(), activity);
            appLovinInterstitialAd.setListener(new com.applovin.mediation.MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    appLovinInterstitialRetryAttempt = 0;
                    Log.d(TAG, "AppLovin interstitial ad loaded");
                    maybeShowAppLovinLaunchAd();
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {
                    fullScreenAdShowing = true;
                    Log.d(TAG, "AppLovin interstitial ad displayed");
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    fullScreenAdShowing = false;
                    Log.d(TAG, "AppLovin interstitial ad hidden");
                    appLovinInterstitialAd.loadAd();
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    Log.d(TAG, "AppLovin interstitial ad clicked");
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    appLovinInterstitialRetryAttempt++;
                    int delay = nextRetryDelay(appLovinInterstitialRetryAttempt);
                    Log.e(TAG, "AppLovin interstitial ad failed to load: " + error.getMessage() + ". Retrying in " + delay + "s");
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (appLovinInterstitialAd != null) {
                                appLovinInterstitialAd.loadAd();
                            }
                        }
                    }, delay * 1000L);
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    fullScreenAdShowing = false;
                    showAppLovinLaunchAdWhenLoaded = false;
                    Log.e(TAG, "AppLovin interstitial ad failed to display: " + error.getMessage());
                    appLovinInterstitialAd.loadAd();
                }
            });
            appLovinInterstitialAd.loadAd();
        }
    }

    private void showAppLovinInterstitial(Activity activity, boolean launchAd) {
        if (!LifecycleUtils.isActivityAlive(activity) || fullScreenAdShowing
                || appLovinInterstitialAd == null || !appLovinInterstitialAd.isReady()) {
            return;
        }
        if (launchAd) {
            showAppLovinLaunchAdWhenLoaded = false;
            appLovinLaunchAdShownThisSession = true;
        }
        fullScreenAdShowing = true;
        appLovinInterstitialAd.showAd();
    }

    private void maybeShowAppLovinLaunchAd() {
        if (!showAppLovinLaunchAdWhenLoaded || appLovinLaunchAdShownThisSession) return;
        Activity activity = getCurrentActivity();
        if (LifecycleUtils.isActivityAlive(activity) && !fullScreenAdShowing
                && appLovinInterstitialAd != null && appLovinInterstitialAd.isReady()) {
            showAppLovinInterstitial(activity, true);
        }
    }

    public String getInterstitialLoadState() {
        return "isReady: " + isInterstitialReady() + ", isLoading: " + interstitialLoading
                + ", retryAttempt: " + interstitialRetryAttempt;
    }

    private void initializeInterstitialAds(Activity activity) {
        String network = AppLAdUnit.activeAdNetwork;
        if (!"applovin".equalsIgnoreCase(network) && !"none".equalsIgnoreCase(network)) {
            if (interstitialAdLoader == null) {
                interstitialAdLoader = new InterstitialAdLoader(activity.getApplicationContext());
            }
            loadInterstitialAd();
        }
        if (!"yandex".equalsIgnoreCase(network) && !"none".equalsIgnoreCase(network)) {
            initializeAppLovinInterstitial(activity);
        }
    }

    private void loadInterstitialAd() {
        if ("applovin".equalsIgnoreCase(AppLAdUnit.activeAdNetwork) || "none".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)) {
            return;
        }
        if (interstitialAdLoader == null || interstitialAd != null || interstitialLoading) {
            return;
        }

        interstitialLoading = true;
        AdRequest adRequest = new AdRequest.Builder(AppLAdUnit.INSTANCE.getYandexInterstitialAdUnitId()).build();
        interstitialAdLoader.loadAd(adRequest, new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd loadedAd) {
                interstitialRetryAttempt = 0;
                interstitialLoading = false;
                interstitialAd = loadedAd;
                Log.d(TAG, "Yandex interstitial ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                interstitialLoading = false;
                interstitialRetryAttempt++;
                Log.e(TAG, "Yandex interstitial ad failed to load with code " + adRequestError.getCode()
                        + ": " + adRequestError.getDescription());
                if (interstitialRetryAttempt <= MAX_INTERSTITIAL_LOAD_RETRY_ATTEMPTS) {
                    int retryDelay = nextRetryDelay(interstitialRetryAttempt);
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadInterstitialAd();
                        }
                    }, retryDelay * 1000L);
                }
            }
        });
    }

    private void showYandexInterstitial(Activity activity) {
        if ("applovin".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)
                || "none".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)) {
            destroyYandexInterstitialAd();
            return;
        }
        if (!LifecycleUtils.isActivityAlive(activity) || fullScreenAdShowing) {
            return;
        }
        if (interstitialAd == null) {
            loadInterstitialAd();
            return;
        }

        interstitialAd.setAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdShown() {
                fullScreenAdShowing = true;
                Log.d(TAG, "Yandex interstitial ad shown");
            }

            @Override
            public void onAdFailedToShow(@NonNull AdError adError) {
                fullScreenAdShowing = false;
                Log.e(TAG, "Yandex interstitial ad failed to show: " + adError);
                destroyYandexInterstitialAd();
                loadInterstitialAd();
            }

            @Override
            public void onAdDismissed() {
                fullScreenAdShowing = false;
                Log.d(TAG, "Yandex interstitial ad dismissed");
                destroyYandexInterstitialAd();
                loadInterstitialAd();
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "Yandex interstitial ad clicked");
            }

            @Override
            public void onAdImpression(@Nullable ImpressionData impressionData) {
                Log.d(TAG, "Yandex interstitial ad impression");
            }
        });
        fullScreenAdShowing = true;
        interstitialAd.show(activity);
    }

    private void initializeAppOpenAd(Context context) {
        if ("applovin".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)
                || "none".equalsIgnoreCase(AppLAdUnit.activeAdNetwork)
                || appOpenAdShownThisSession) {
            return;
        }
        if (appOpenAdLoader == null) {
            appOpenAdLoader = new AppOpenAdLoader(context.getApplicationContext());
        }
        loadAppOpenAd();
    }

    private void loadAppOpenAd() {
        if (appOpenAdLoader == null || appOpenAdLoading || appOpenAd != null
                || appOpenAdShownThisSession) {
            return;
        }
        appOpenAdLoading = true;
        AdRequest request = new AdRequest.Builder(AppLAdUnit.INSTANCE.getYandexAppOpenAdUnitId()).build();
        appOpenAdLoader.loadAd(request, new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd loadedAd) {
                appOpenAdLoading = false;
                appOpenAd = loadedAd;
                Log.d(TAG, "Yandex app-open ad loaded");
                showAppOpenAdIfReady();
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError error) {
                appOpenAdLoading = false;
                Log.e(TAG, "Yandex app-open ad failed to load with code " + error.getCode()
                        + ": " + error.getDescription());
            }
        });
    }

    private void showAppOpenAdIfReady() {
        Activity activity = getCurrentActivity();
        if (appOpenAd == null || appOpenAdShownThisSession || fullScreenAdShowing
                || !LifecycleUtils.isActivityAlive(activity)) {
            return;
        }
        appOpenAd.setAdEventListener(new AppOpenAdEventListener() {
            @Override
            public void onAdShown() {
                fullScreenAdShowing = true;
                appOpenAdShownThisSession = true;
                Log.d(TAG, "Yandex app-open ad shown");
            }

            @Override
            public void onAdFailedToShow(@NonNull AdError error) {
                fullScreenAdShowing = false;
                appOpenAdShownThisSession = true;
                Log.e(TAG, "Yandex app-open ad failed to show: " + error);
                destroyAppOpenAd();
            }

            @Override
            public void onAdDismissed() {
                fullScreenAdShowing = false;
                Log.d(TAG, "Yandex app-open ad dismissed");
                destroyAppOpenAd();
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "Yandex app-open ad clicked");
            }

            @Override
            public void onAdImpression(@Nullable ImpressionData impressionData) {
                Log.d(TAG, "Yandex app-open ad impression");
            }
        });
        fullScreenAdShowing = true;
        appOpenAd.show(activity);
    }

    private void destroyAppOpenAd() {
        if (appOpenAd != null) {
            appOpenAd.setAdEventListener(null);
            appOpenAd = null;
        }
    }

    private void destroyYandexInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.setAdEventListener(null);
            interstitialAd = null;
        }
    }

    public Activity getCurrentActivity() {
        return currentActivityReference == null ? null : currentActivityReference.get();
    }

    private void runPendingYandexInitCallbacks() {
        List<Runnable> callbacks = new ArrayList<>(pendingYandexInitCallbacks);
        pendingYandexInitCallbacks.clear();
        for (Runnable callback : callbacks) {
            callback.run();
        }
    }

    private int nextRetryDelay(int attempt) {
        return Math.min(64, (int) Math.pow(2.0d, attempt));
    }

    private String normalizeNetwork(String network) {
        if (network == null) return "both";
        String normalized = network.trim().toLowerCase();
        if ("yandex".equals(normalized) || "applovin".equals(normalized)
                || "none".equals(normalized) || "both".equals(normalized)) {
            return normalized;
        }
        Log.w(TAG, "Unknown active_ad_network '" + network + "'; using both");
        return "both";
    }

    private void releaseInactiveNetworkAds(String activeNetwork) {
        if ("applovin".equals(activeNetwork) || "none".equals(activeNetwork)) {
            destroyYandexInterstitialAd();
            destroyAppOpenAd();
            if (interstitialAdLoader != null) interstitialAdLoader.cancelLoading();
            if (appOpenAdLoader != null) appOpenAdLoader.cancelLoading();
            interstitialAdLoader = null;
            appOpenAdLoader = null;
            interstitialLoading = false;
            appOpenAdLoading = false;
        }
        if ("yandex".equals(activeNetwork) || "none".equals(activeNetwork)) {
            showAppLovinLaunchAdWhenLoaded = false;
            if (appLovinInterstitialAd != null) {
                appLovinInterstitialAd.setListener(null);
                appLovinInterstitialAd.destroy();
                appLovinInterstitialAd = null;
            }
        }
        fullScreenAdShowing = false;
        Log.d(TAG, "Released inactive ad network resources; active network is " + activeNetwork);
    }

}
