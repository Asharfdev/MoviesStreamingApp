package com.luminatv.tvshowmovies1.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.models.WatchProgressItem;
import com.luminatv.tvshowmovies1.utils.ContinueWatchingStore;
import com.luminatv.tvshowmovies1.utils.CopyrightContentFilter;
import com.luminatv.tvshowmovies1.utils.LifecycleUtils;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class PlayerActivity extends AppCompatActivity {
    private static final String ALLOWED_BASE_URL = "https://vsembed.ru/";
    private static final long MOVIE_ESTIMATED_DURATION_MS = 120L * 60L * 1000L;
    private static final long TV_ESTIMATED_DURATION_MS = 45L * 60L * 1000L;

    private String backdropPath;
    private int currentProgressPercentage;
    private int episodeNumber;
    private String episodeTitle = "";
    private final Handler hideControlsHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideControlsRunnable = new Runnable() { // from class: com.freewatching.magistv4.activities.PlayerActivity$$ExternalSyntheticLambda4
        @Override // java.lang.Runnable
        public final void run() {
            PlayerActivity.this.hideControls();
        }
    };
    private int itemId;
    private String itemTitle = "";
    private String itemType = "movie";
    private String posterPath;
    private ProgressBar progressBar;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateWatchProgress();
            progressHandler.postDelayed(this, 10000L);
        }
    };
    private int seasonNumber;
    private long sessionStartTime;
    private Toolbar toolbar;
    private String videoUrl;
    private WebView webView;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);
        getIntentData();
        if (!validatePlaybackRequest()) {
            return;
        }
        setupFullscreen();
        setupWebView();
        setupClickListeners();
        setupBackPressHandler();
        loadStream();
        startProgressTracking();
    }

    private void getIntentData() {
        String stringExtra;
        this.itemId = getIntent().getIntExtra("item_id", 0);
        this.itemType = getIntent().getStringExtra("item_type");
        if (this.itemType == null || this.itemType.isEmpty()) {
            this.itemType = "movie";
        }
        this.itemTitle = getIntent().getStringExtra("item_title");
        if (this.itemTitle == null) {
            this.itemTitle = "";
        }
        this.posterPath = getIntent().getStringExtra("poster_path");
        this.backdropPath = getIntent().getStringExtra("backdrop_path");
        this.seasonNumber = getIntent().getIntExtra("season", 0);
        this.episodeNumber = getIntent().getIntExtra("episode", 0);
        this.currentProgressPercentage = getIntent().getIntExtra("progress_percentage", 0);
        this.videoUrl = getIntent().getStringExtra("video_url");
        String stringExtra2 = getIntent().getStringExtra("episode_title");
        this.episodeTitle = stringExtra2;
        if (stringExtra2 == null) {
            this.episodeTitle = "";
        }
        String str = this.videoUrl;
        if (str == null || str.isEmpty()) {
            if ("tv".equals(this.itemType)) {
                if (this.seasonNumber > 0 && this.episodeNumber > 0) {
                    this.videoUrl = "https://vsembed.ru/embed/tv?tmdb=" + this.itemId + "&season=" + this.seasonNumber + "&episode=" + this.episodeNumber;
                } else {
                    this.videoUrl = "https://vsembed.ru/embed/tv?tmdb=" + this.itemId;
                }
            } else {
                this.videoUrl = "https://vsembed.ru/embed/movie?tmdb=" + this.itemId;
            }
            if (this.episodeTitle.isEmpty() && (stringExtra = getIntent().getStringExtra("item_title")) != null) {
                this.episodeTitle = stringExtra;
            }
        }
        if (this.itemTitle.isEmpty()) {
            this.itemTitle = this.episodeTitle;
        }
        if (this.currentProgressPercentage <= 0 && this.itemId > 0) {
            this.currentProgressPercentage = ContinueWatchingStore.getProgress(this, this.itemId, this.itemType, this.seasonNumber, this.episodeNumber);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPlayer);
        this.toolbar = toolbar;
        toolbar.setTitle(this.episodeTitle);
    }

    private boolean validatePlaybackRequest() {
        if (CopyrightContentFilter.isTitleBlocked(this, this.itemTitle)
                || CopyrightContentFilter.isTitleBlocked(this, this.episodeTitle)) {
            CopyrightContentFilter.showBlockedNotice(this);
            finish();
            return false;
        }
        boolean hasVideoUrl = this.videoUrl != null && !this.videoUrl.isEmpty();
        if (hasVideoUrl || this.itemId > 0) {
            return true;
        }
        showToast("No video to play.");
        finish();
        return false;
    }

    private void setupFullscreen() {
        setRequestedOrientation(6);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.systemBars());
                insetsController.setSystemBarsBehavior(2);
                return;
            }
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(4102);
    }

    private void setupWebView() {
        this.webView = (WebView) findViewById(R.id.webViewPlayer);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBarPlayer);
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setMixedContentMode(2);
        this.webView.setWebViewClient(new WebViewClient() { // from class: com.freewatching.magistv4.activities.PlayerActivity.1
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                if (webResourceRequest == null || webResourceRequest.getUrl() == null) {
                    return false;
                }
                return !webResourceRequest.getUrl().toString().toLowerCase().contains(PlayerActivity.ALLOWED_BASE_URL.toLowerCase());
            }

            @Override // android.webkit.WebViewClient
            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                if (!LifecycleUtils.isActivityAlive(PlayerActivity.this) || PlayerActivity.this.progressBar == null) {
                    return;
                }
                PlayerActivity.this.progressBar.setVisibility(0);
                super.onPageStarted(webView, str, bitmap);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView, String str) {
                if (!LifecycleUtils.isActivityAlive(PlayerActivity.this)) {
                    return;
                }
                PlayerActivity.this.progressBar.setVisibility(8);
                PlayerActivity.this.skipAds(webView);
                PlayerActivity.this.restorePlaybackPosition(webView);
                super.onPageFinished(webView, str);
            }

            @Override // android.webkit.WebViewClient
            public void onReceivedError(WebView webView, int i, String str, String str2) {
                if (!LifecycleUtils.isActivityAlive(PlayerActivity.this) || PlayerActivity.this.progressBar == null) {
                    return;
                }
                PlayerActivity.this.progressBar.setVisibility(8);
                PlayerActivity.this.showToast("Error loading stream. Check your connection.");
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() { // from class: com.freewatching.magistv4.activities.PlayerActivity.2
            @Override // android.webkit.WebChromeClient
            public void onProgressChanged(WebView webView, int i) {
                if (i == 100 && LifecycleUtils.isActivityAlive(PlayerActivity.this) && PlayerActivity.this.progressBar != null) {
                    PlayerActivity.this.progressBar.setVisibility(8);
                }
            }
        });
        this.webView.setOnTouchListener(new View.OnTouchListener() { // from class: com.freewatching.magistv4.activities.PlayerActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return PlayerActivity.this.m300x9c8a6f3b(view, motionEvent);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupWebView$0$com-freewatching-magistv4-activities-PlayerActivity, reason: not valid java name */
    /* synthetic */ boolean m300x9c8a6f3b(View view, MotionEvent motionEvent) {
        showControls();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void skipAds(WebView webView) {
        webView.evaluateJavascript("setTimeout(function() {    var skipButtons = document.querySelectorAll('button, a, div, span');    for(var i = 0; i < skipButtons.length; i++) {        var text = skipButtons[i].textContent.toLowerCase();        if(text.includes('skip') || text.includes('continue') || text.includes('close')) {            skipButtons[i].click();        }    }}, 3000);setTimeout(function() {    var skipButtons = document.querySelectorAll('button, a, div, span');    for(var i = 0; i < skipButtons.length; i++) {        var text = skipButtons[i].textContent.toLowerCase();        if(text.includes('skip') || text.includes('continue') || text.includes('close')) {            skipButtons[i].click();        }    }}, 5000);", null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showToast(final String str) {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.freewatching.magistv4.activities.PlayerActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PlayerActivity.this.m301xbd5ac9b0(str);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$showToast$1$com-freewatching-magistv4-activities-PlayerActivity, reason: not valid java name */
    /* synthetic */ void m301xbd5ac9b0(String str) {
        if (!LifecycleUtils.isActivityAlive(this)) {
            return;
        }
        Toast.makeText(this, str, 0).show();
    }

    private void setupClickListeners() {
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.PlayerActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PlayerActivity.this.m298x8e86fc3b(view);
            }
        });
        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() { // from class: com.freewatching.magistv4.activities.PlayerActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PlayerActivity.this.m299x1bc1adbc(view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupClickListeners$2$com-freewatching-magistv4-activities-PlayerActivity, reason: not valid java name */
    /* synthetic */ void m298x8e86fc3b(View view) {
        finish();
    }

    /* JADX INFO: renamed from: lambda$setupClickListeners$3$com-freewatching-magistv4-activities-PlayerActivity, reason: not valid java name */
    /* synthetic */ void m299x1bc1adbc(View view) {
        if (this.toolbar.getVisibility() == 0) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) { // from class: com.freewatching.magistv4.activities.PlayerActivity.3
            @Override // androidx.activity.OnBackPressedCallback
            public void handleOnBackPressed() {
                PlayerActivity.this.finish();
            }
        });
    }

    private void loadStream() {
        this.progressBar.setVisibility(0);
        String str = this.videoUrl;
        if (str != null && !str.isEmpty()) {
            try {
                this.webView.loadUrl(this.videoUrl, Collections.singletonMap("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"));
                return;
            } catch (Exception e) {
                this.progressBar.setVisibility(8);
                showToast("Failed to load stream: " + e.getMessage());
                return;
            }
        }
        this.progressBar.setVisibility(8);
        showToast("No video URL provided.");
    }

    private void startProgressTracking() {
        this.sessionStartTime = System.currentTimeMillis();
        this.progressHandler.removeCallbacks(this.progressRunnable);
        this.progressHandler.postDelayed(this.progressRunnable, 10000L);
    }

    private void updateWatchProgress() {
        if (this.webView == null || this.itemId <= 0) {
            return;
        }
        this.webView.evaluateJavascript("(function(){var v=document.querySelector('video');if(v&&isFinite(v.duration)&&v.duration>0){return Math.max(1,Math.min(99,Math.round((v.currentTime/v.duration)*100)));}return -1;})()", value -> {
            if (!LifecycleUtils.isActivityAlive(PlayerActivity.this)) {
                return;
            }
            int webProgress = parseProgressValue(value);
            int progress = webProgress > 0 ? webProgress : getFallbackProgress();
            saveWatchProgress(progress);
        });
    }

    private int parseProgressValue(String value) {
        if (value == null) {
            return -1;
        }
        try {
            return Integer.parseInt(value.replace("\"", "").trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private int getFallbackProgress() {
        long elapsed = Math.max(0L, System.currentTimeMillis() - this.sessionStartTime);
        long estimatedDuration = "tv".equals(this.itemType) ? TV_ESTIMATED_DURATION_MS : MOVIE_ESTIMATED_DURATION_MS;
        int sessionProgress = (int) ((elapsed * 100L) / estimatedDuration);
        return Math.max(1, Math.min(94, this.currentProgressPercentage + sessionProgress));
    }

    private void saveWatchProgress(int progressPercentage) {
        if (this.itemId <= 0 || this.itemTitle == null || this.itemTitle.isEmpty()) {
            return;
        }
        if (progressPercentage <= this.currentProgressPercentage && this.currentProgressPercentage > 0) {
            progressPercentage = this.currentProgressPercentage;
        }
        this.currentProgressPercentage = Math.max(1, Math.min(99, progressPercentage));
        ContinueWatchingStore.saveProgress(this, new WatchProgressItem(
                this.itemId,
                this.itemTitle,
                this.posterPath,
                this.backdropPath,
                this.itemType,
                this.videoUrl,
                this.seasonNumber,
                this.episodeNumber,
                this.currentProgressPercentage));
    }

    private void restorePlaybackPosition(WebView webView) {
        if (this.currentProgressPercentage <= 0) {
            return;
        }
        String script = "(function(){var target=" + (this.currentProgressPercentage / 100f) + ";"
                + "var seek=function(){var v=document.querySelector('video');"
                + "if(v&&isFinite(v.duration)&&v.duration>0){v.currentTime=Math.max(0,v.duration*target);return true;}return false;};"
                + "if(!seek()){var tries=0;var timer=setInterval(function(){tries++;if(seek()||tries>20){clearInterval(timer);}},1000);}})();";
        webView.evaluateJavascript(script, null);
    }

    private void showControls() {
        this.toolbar.setVisibility(0);
        this.hideControlsHandler.removeCallbacks(this.hideControlsRunnable);
        this.hideControlsHandler.postDelayed(this.hideControlsRunnable, 4000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideControls() {
        this.toolbar.setVisibility(8);
        this.hideControlsHandler.removeCallbacks(this.hideControlsRunnable);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        saveWatchProgress(getFallbackProgress());
        this.hideControlsHandler.removeCallbacks(this.hideControlsRunnable);
        this.progressHandler.removeCallbacks(this.progressRunnable);
        WebView webView = this.webView;
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.removeAllViews();
            webView.destroy();
            this.webView = null;
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        saveWatchProgress(getFallbackProgress());
        WebView webView = this.webView;
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        WebView webView = this.webView;
        if (webView != null) {
            webView.onResume();
        }
    }
}
